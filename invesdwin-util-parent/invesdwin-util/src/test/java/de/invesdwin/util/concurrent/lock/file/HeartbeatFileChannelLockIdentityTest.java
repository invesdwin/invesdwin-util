package de.invesdwin.util.concurrent.lock.file;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.invesdwin.util.assertions.Assertions;

/**
 * The registry entry of a lock file belongs to the instance that acquired it, and only that instance may take it away.
 * This is not bookkeeping tidiness: every failed tryLock() runs unlock() as its cleanup, so a loser that wants the same
 * path evicts the entry of the instance that actually holds the lock - and hasLiveLocalInstance() is the honest "we
 * already own it" test behind the claim owner-string match in FileChannelLock.tryStealOrVerifyLock(). With the entry
 * gone, the next attempt replaces the holder's own claim file (REPLACE_EXISTING), the holder's OS lock ends up on an
 * unlinked inode, and two threads of ONE JVM hold the same "exclusive" file lock. Measured 2026-11-15 in the umbrella
 * PerformanceTraderTestSuite run on subes-lap-mid, where that let two threads stage the same live-calculated segment
 * file at once.
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
