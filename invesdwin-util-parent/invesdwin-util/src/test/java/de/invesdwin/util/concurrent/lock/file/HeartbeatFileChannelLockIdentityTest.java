package de.invesdwin.util.concurrent.lock.file;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.time.date.millis.FDateMillis;

/**
 * The registry entry of a lock file belongs to the instance that acquired it, and only that instance may take it away.
 * This is not bookkeeping tidiness: every failed tryLock() runs unlock() as its cleanup, so a loser that wants the same
 * path evicts the entry of the instance that actually holds the lock - and hasLiveLocalInstance() is the honest "we
 * already own it" test behind the claim owner-string match in FileChannelLock.tryStealOrVerifyLock(). With the entry
 * gone, the next attempt replaces the holder's own claim file (REPLACE_EXISTING), the holder's OS lock ends up on an
 * unlinked inode, and two threads of ONE JVM hold the same "exclusive" file lock. Measured 2026-11-15 in a bounded-run
 * backtest suite on a dev box, where that let two threads stage the same live-calculated segment file at once.
 */
@NotThreadSafe
public class HeartbeatFileChannelLockIdentityTest {

    @TempDir
    private File tempDir;

    /**
     * The mechanism: a failed acquirer of the same path must leave the holder's registration alone.
     */
    @Test
    public void testAFailedAcquirerDoesNotEvictTheHoldersRegistration() throws Exception {
        final File lockFile = new File(tempDir, "eviction.lock");
        final Holder holder = startHolder(lockFile);
        try {
            Assertions.assertThat(holder.locked.get()).as("the holder must have the lock").isTrue();
            Assertions.assertThat(HeartbeatFileChannelLockRegistry.hasLiveLocalInstance(lockFile))
                    .as("the holder must be registered as a live local instance")
                    .isTrue();

            Assertions.assertThat(new HeartbeatFileChannelLock(lockFile).tryLock())
                    .as("a second thread may not take a held file lock")
                    .isFalse();
            Assertions.assertThat(HeartbeatFileChannelLockRegistry.hasLiveLocalInstance(lockFile))
                    .as("the failed acquirer must not have evicted the holder's registration")
                    .isTrue();
        } finally {
            holder.release();
        }
    }

    /**
     * The consequence the eviction enables: with the entry gone, the claim file still names this JVM as owner, so the
     * next attempt reads it as a stale claim of its own JVM and replaces it - after which the holder's OS lock sits on
     * an unlinked inode and a second instance of the SAME JVM holds the same lock.
     */
    @Test
    public void testNoSecondInstanceOfTheSameJvmCanHoldOneFileLock() throws Exception {
        final File lockFile = new File(tempDir, "steal.lock");
        final Holder holder = startHolder(lockFile);
        try {
            //this failed attempt is the evictor in the pre-fix code
            Assertions.assertThat(new HeartbeatFileChannelLock(lockFile).tryLock())
                    .as("a second thread may not take a held file lock")
                    .isFalse();
            final HeartbeatFileChannelLock wouldBeThief = new HeartbeatFileChannelLock(lockFile);
            Assertions.assertThat(wouldBeThief.tryLock())
                    .as("a third attempt must not steal the claim of a live instance of the same JVM - if it does, "
                            + "two threads hold one exclusive lock and the store's writers collide")
                    .isFalse();
            Assertions.assertThat(holder.locked.get())
                    .as("and the holder is still the only owner")
                    .isTrue();
            wouldBeThief.unlock();
        } finally {
            holder.release();
        }
    }

    /**
     * The guard must not turn the lock into a permanent denial.
     */
    @Test
    public void testTheLockIsAcquirableAgainAfterTheHolderReleases() throws Exception {
        final File lockFile = new File(tempDir, "release.lock");
        final Holder holder = startHolder(lockFile);
        holder.release();
        final HeartbeatFileChannelLock next = new HeartbeatFileChannelLock(lockFile);
        Assertions.assertThat(next.tryLock()).as("after the holder released, the lock must be acquirable again")
                .isTrue();
        next.unlock();
        Assertions.assertThat(HeartbeatFileChannelLockRegistry.hasLiveLocalInstance(lockFile))
                .as("and the released instance must leave nothing registered behind")
                .isFalse();
    }

    /**
     * The other evictor: the periodic heartbeat sweep removes an entry whenever a beat fails, and
     * FileChannelLock.touchHeartbeatUnchecked() answers false for ANY IOException - including for an instance that
     * still holds the lock. Running the real sweep against a holder whose heartbeat file cannot be written must
     * leave the registration in place, because that registration is what keeps a second instance of this JVM from
     * replacing the holder's own claim.
     */
    @Test
    public void testTheHeartbeatSweepKeepsTheEntryOfALockedInstance() throws Exception {
        final File directory = new File(tempDir, "sweep");
        Files.forceMkdir(directory);
        final File lockFile = new File(directory, "swept.lock");
        final Holder holder = startHolder(lockFile);
        try {
            Assertions.assertThat(holder.locked.get()).as("the holder must have the lock").isTrue();

            //Break only the beat: replace the heartbeat file with a DIRECTORY of the same name, so writing it (or
            //reading the owner out of it) throws IOException while the claim file stays exactly as the holder left it.
            //Deleting the claim instead would be a different - and legitimate - situation: a fresh acquisition.
            final File heartbeatFile = new File(directory, "swept.heartbeat");
            Assertions.assertThat(heartbeatFile).as("the holder must have written its heartbeat").exists();
            Files.deleteNative(heartbeatFile);
            Assertions.assertThat(heartbeatFile.mkdir())
                    .as("the test must be able to plant the un-writable path")
                    .isTrue();

            HeartbeatFileChannelLockRegistry.updateHeartbeats();

            Assertions.assertThat(HeartbeatFileChannelLockRegistry.hasLiveLocalInstance(lockFile))
                    .as("a failed beat must not evict the entry of an instance that still holds the lock")
                    .isTrue();
            Assertions.assertThat(new HeartbeatFileChannelLock(lockFile).tryLock())
                    .as("and while that entry stands, no second instance of this JVM may take the lock")
                    .isFalse();
        } finally {
            holder.release();
        }
    }


    /**
     * The killed-holder state, modelled directly: a claim file that is PRESENT, freshly touched, and owned by someone
     * else - which is exactly what a `kill -9`, an OOM kill or a surefire fork timeout leaves behind, because the OS
     * reclaims the file lock when the process dies but nothing removes the claim.
     * <p>
     * The mtime rule in tryStealOrVerifyLock cannot tell that from a live writer's claim, so before this fix the key
     * stayed unclaimable for the full HEARTBEAT_TIMEOUT (two minutes) after every killed holder - measured downstream
     * as the timeseriesdb update lock refusing every later attempt of the same key while the file lock was
     * demonstrably free, which is one of the shapes behind a parallel suite parking on retry exhaustion.
     * <p>
     * The rule this pins: when the OS itself grants us the file lock, the claim's mtime is no longer evidence of
     * anything - a live owner would have blocked the grant - so the claim is taken by replace-and-verify. The safety
     * half of that is the cell above: a LIVE holder still blocks the acquirer, because the OS grant fails there.
     */
    @Test
    public void testAFreshLeftoverClaimOfADeadHolderIsStolenUnderTheOsLock() throws Exception {
        final File lockFile = new File(tempDir, "killed-holder.lock");
        //the state a dead process leaves: claim present, mtime NOW, owner some other JVM
        java.nio.file.Files.writeString(lockFile.toPath(), "99999@some-other-jvm");
        Assertions.assertThat(lockFile.lastModified())
                .as("the leftover claim must look fresh, that is the whole point")
                .isGreaterThan(FDateMillis.nowMillis() - 60000L);
        Assertions.assertThat(HeartbeatFileChannelLockRegistry.hasLiveLocalInstance(lockFile))
                .as("and this JVM must have no live instance owning it")
                .isFalse();

        final HeartbeatFileChannelLock acquirer = new HeartbeatFileChannelLock(lockFile);
        try {
            Assertions.assertThat(acquirer.tryLock())
                    .as("with the OS lock free, a fresh leftover claim must not wedge the key: "
                            + acquirer.getLastTryLockFailureReason())
                    .isTrue();
            Assertions.assertThat(Files.readFileToString(lockFile).trim())
                    .as("the stolen claim must now name this JVM")
                    .isEqualTo(HeartbeatFileChannelLockRegistry.HEARTBEAT_OWNER);
        } finally {
            acquirer.close();
        }

        Assertions.assertThat(new HeartbeatFileChannelLock(lockFile).tryLock())
                .as("and after the holder released, the next acquirer takes it as a matter of course")
                .isTrue();
    }

    @NotThreadSafe
    private static final class Holder {

        private final AtomicBoolean locked = new AtomicBoolean();
        private final CountDownLatch ready = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);
        private final Thread thread;

        private Holder(final File lockFile) {
            this.thread = new Thread(() -> {
                final HeartbeatFileChannelLock lock = new HeartbeatFileChannelLock(lockFile);
                locked.set(lock.tryLock());
                ready.countDown();
                try {
                    release.await();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                lock.unlock();
            }, "heartbeat-lock-holder");
        }

        private void start() {
            thread.start();
            try {
                ready.await();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void release() throws InterruptedException {
            release.countDown();
            thread.join(10000);
        }
    }

    private Holder startHolder(final File lockFile) {
        final Holder holder = new Holder(lockFile);
        holder.start();
        return holder;
    }
}
