package de.invesdwin.util.lang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class FilesTest {

    @TempDir
    private File tempDir;

    @Test
    public void testGetReferenceActualFile() {
        Assertions.assertThat(Files.getReferenceActualFile(new File("doc/test.csv"), 0).getName())
                .isEqualTo("test_actual.csv");
        Assertions.assertThat(Files.getReferenceActualFile(new File("doc/test.csv"), 3).getName())
                .isEqualTo("test_actual_3.csv");
        Assertions.assertThat(Files.getReferenceActualFile(new File("doc/test"), 0).getName())
                .isEqualTo("test_actual");
        //the recorded side must not be mistaken for an indexed reference chunk
        Assertions.assertThat(Files.getReferenceActualFile(new File("doc/test.csv"), 3).getName())
                .isNotEqualTo(Files.prefixExtension(new File("doc/test.csv"), "_3").getName());
    }

    @Test
    public void testCheckReferenceRecordsActualSideOnDifference() throws Exception {
        final File referenceFile = new File(tempDir, "test.csv");
        final File producedFile = new File(tempDir, "produced/test.csv");
        Files.writeStringToFileIfDifferent(referenceFile, "one two");
        Files.writeStringToFileIfDifferent(producedFile, "one three");
        try {
            Files.checkReference(false, referenceFile, producedFile);
            Assertions.fail("the reference difference must fail the check");
        } catch (final AssertionError e) {
            //the platform truncates the message from its end, so the recorded file has to be named at its start
            Assertions.assertThat(e.getMessage())
                    .startsWith("[" + Files.getReferenceActualFile(referenceFile, 0).getAbsolutePath() + "]");
            Assertions.assertThat(
                    Files.readFileToString(Files.getReferenceActualFile(referenceFile, 0), Charset.defaultCharset()))
                    .isEqualTo("one three");
        }
    }

    @Test
    public void testCreateReferenceDeletesRecordedActualSide() throws Exception {
        final File referenceFile = new File(tempDir, "test.csv");
        final File producedFile = new File(tempDir, "produced/test.csv");
        Files.writeStringToFileIfDifferent(referenceFile, "one two");
        Files.writeStringToFileIfDifferent(producedFile, "one three");
        try {
            Files.checkReference(false, referenceFile, producedFile);
            Assertions.fail("the reference difference must fail the check");
        } catch (final AssertionError e) {
            //expected
        }
        final File actualFile = Files.getReferenceActualFile(referenceFile, 0);
        Assertions.assertThat(actualFile).exists();
        Files.checkReference(true, referenceFile, producedFile);
        Assertions.assertThat(Files.readFileToString(referenceFile, Charset.defaultCharset())).isEqualTo("one three");
        Assertions.assertThat(actualFile).doesNotExist();
    }

    @Test
    public void testMoveAtomicKeepsTheTargetNameVisibleDuringRepublish() throws Exception {
        assertRepublishKeepsTargetReadable((staging, target) -> Files.moveAtomic(staging, target));
    }

    @Test
    public void testMoveFileKeepsTheTargetNameVisibleDuringRepublish() throws Exception {
        //the File-based entry point is the funnel the trading live-calculated sources publish interval files through
        assertRepublishKeepsTargetReadable((staging, target) -> Files.moveFile(staging.toFile(), target.toFile()));
    }

    @Test
    public void testMoveClaimRefusesAnOccupiedNameAndKeepsTheLoserRetryable() throws Exception {
        final Path target = new File(tempDir, "4_memory.index").toPath();
        final Path first = new File(tempDir, "4_memory_ownerA.tmp").toPath();
        final Path second = new File(tempDir, "4_memory_ownerB.tmp").toPath();
        Files.write(first, "FIRST".getBytes(Charset.defaultCharset()));
        Files.write(second, "SECOND".getBytes(Charset.defaultCharset()));

        Files.moveClaim(first, target);
        Assertions.assertThat(Files.readAllBytes(target)).as("the claim publishes the staged content").isEqualTo(
                "FIRST".getBytes(Charset.defaultCharset()));
        Assertions.assertThat(first).as("the winner's staging name is unlinked").doesNotExist();

        try {
            Files.moveClaim(second, target);
            Assertions.fail("a claim onto an occupied name must be refused, not published");
        } catch (final FileAlreadyExistsException e) {
            //expected
        }
        Assertions.assertThat(Files.readAllBytes(target))
                .as("a refused claim must not touch what the winner published")
                .isEqualTo("FIRST".getBytes(Charset.defaultCharset()));
        Assertions.assertThat(second)
                .as("a refused claim must leave its content staged, so the caller can pick another name and retry")
                .exists();
    }

    @Test
    public void testMoveClaimGivesExactlyOneWinnerOfOneName() throws Exception {
        final int writers = 16;
        final Path target = new File(tempDir, "5_memory.index").toPath();
        final CountDownLatch ready = new CountDownLatch(writers);
        final CountDownLatch go = new CountDownLatch(1);
        final AtomicInteger winners = new AtomicInteger();
        final AtomicInteger refused = new AtomicInteger();
        final List<Thread> threads = new ArrayList<>();
        final List<Path> staged = new ArrayList<>();
        for (int i = 0; i < writers; i++) {
            final Path source = new File(tempDir, "5_memory_w" + i + ".tmp").toPath();
            Files.write(source, ("w" + i).getBytes(Charset.defaultCharset()));
            staged.add(source);
            final Thread writer = new Thread(() -> {
                ready.countDown();
                try {
                    go.await();
                    Files.moveClaim(source, target);
                    winners.incrementAndGet();
                } catch (final FileAlreadyExistsException e) {
                    refused.incrementAndGet();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }, "files-claim-writer");
            threads.add(writer);
            writer.start();
        }
        ready.await();
        go.countDown();
        for (final Thread writer : threads) {
            writer.join(10000);
        }
        Assertions.assertThat(winners.get())
                .as("one name may be published by exactly one writer, got " + winners.get() + " winners")
                .isEqualTo(1);
        Assertions.assertThat(refused.get()).as("every other writer must be told the name is taken").isEqualTo(
                writers - 1);
        int stagedLeft = 0;
        for (final Path source : staged) {
            if (source.toFile().exists()) {
                stagedLeft++;
            }
        }
        Assertions.assertThat(stagedLeft).as("the losers keep their content staged").isEqualTo(writers - 1);
    }

    @Test
    public void testMoveAtomicCannotClaimAName() throws Exception {
        //The premise of moveClaim, pinned: a rename is never a claim on this platform. Without this, swapping
        //moveClaim for moveAtomic would look equivalent and silently restore the overwrite it exists to prevent.
        final Path target = new File(tempDir, "6_memory.index").toPath();
        final Path first = new File(tempDir, "6_memory_a.tmp").toPath();
        final Path second = new File(tempDir, "6_memory_b.tmp").toPath();
        Files.write(first, "FIRST".getBytes(Charset.defaultCharset()));
        Files.write(second, "SECOND".getBytes(Charset.defaultCharset()));
        Files.moveAtomic(first, target);
        Files.moveAtomic(second, target);
        Assertions.assertThat(Files.readAllBytes(target))
                .as("moveAtomic replaces, it does not refuse")
                .isEqualTo("SECOND".getBytes(Charset.defaultCharset()));

        //and the REPLACE_EXISTING flag buys no exclusivity either: java.nio.file.Files.move with ATOMIC_MOVE only
        //still replaced the target when measured on ext4 with JDK 17.0.20.1 (the JDK maps ATOMIC_MOVE to rename(2)).
        //java.nio.file.Files is qualified inline because this test class sits in de.invesdwin.util.lang, where the
        //simple name Files is the house facade.
        final Path third = new File(tempDir, "6_memory_c.tmp").toPath();
        Files.write(third, "THIRD".getBytes(Charset.defaultCharset()));
        java.nio.file.Files.move(third, target, StandardCopyOption.ATOMIC_MOVE);
        Assertions.assertThat(Files.readAllBytes(target))
                .as("an ATOMIC_MOVE without REPLACE_EXISTING still replaces on this platform")
                .isEqualTo("THIRD".getBytes(Charset.defaultCharset()));
    }

    private interface IPublisher {
        void publish(Path staging, Path target) throws Exception;
    }

    private void assertRepublishKeepsTargetReadable(final IPublisher publisher) throws Exception {
        final int payloadBytes = 300_000;
        final int publishes = 300;
        final int readers = 4;
        final Path target = new File(tempDir, "chain.index").toPath();
        final Path staging = new File(tempDir, "chain.index.tmp").toPath();
        final byte[] payload = new byte[payloadBytes];
        for (int i = 0; i < payloadBytes; i++) {
            payload[i] = (byte) (i * 7);
        }
        //seed it the same way, so the assertion below is about a re-publish onto an existing name
        Files.write(staging, payload);
        publisher.publish(staging, target);

        final AtomicBoolean stop = new AtomicBoolean(false);
        final AtomicInteger nameMissing = new AtomicInteger();
        final AtomicInteger halfWritten = new AtomicInteger();
        final AtomicInteger wholeReads = new AtomicInteger();
        final CountDownLatch ready = new CountDownLatch(readers);
        final List<Thread> pollingThreads = new ArrayList<>();
        for (int i = 0; i < readers; i++) {
            final Thread polling = new Thread(() -> {
                ready.countDown();
                while (!stop.get()) {
                    try {
                        if (Files.readAllBytes(target).length != payloadBytes) {
                            halfWritten.incrementAndGet();
                        } else {
                            wholeReads.incrementAndGet();
                        }
                    } catch (final NoSuchFileException e) {
                        nameMissing.incrementAndGet();
                    } catch (final IOException e) {
                        halfWritten.incrementAndGet();
                    }
                }
            }, "files-move-atomic-poller");
            pollingThreads.add(polling);
            polling.start();
        }
        ready.await();
        try {
            for (int i = 0; i < publishes; i++) {
                Files.write(staging, payload);
                publisher.publish(staging, target);
            }
        } finally {
            stop.set(true);
            for (final Thread polling : pollingThreads) {
                polling.join(10000);
            }
        }
        Assertions.assertThat(nameMissing.get())
                .as("a re-publish onto an existing name must never leave that name missing for a reader")
                .isZero();
        Assertions.assertThat(halfWritten.get())
                .as("a re-publish must never be observable half written")
                .isZero();
        //a green run that never watched a publish would prove nothing
        Assertions.assertThat(wholeReads.get()).as("the pollers must have been reading during the publishes").isGreaterThan(500);
        Assertions.assertThat(Files.readAllBytes(target)).as("the last publish must be the visible one").isEqualTo(payload);
        Assertions.assertThat(staging).doesNotExist();
    }
}
