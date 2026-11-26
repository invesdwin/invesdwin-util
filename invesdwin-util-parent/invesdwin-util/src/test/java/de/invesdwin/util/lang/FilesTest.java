package de.invesdwin.util.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.opentest4j.AssertionFailedError;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.streams.StringInputStream;

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
            //the comparison editor of the test runner only opens for a failure that carries both values, so the
            //type has to reach the caller instead of being wrapped in a plain AssertionError
            Assertions.assertThat(e).isInstanceOf(AssertionFailedError.class);
            final AssertionFailedError comparisonFailure = (AssertionFailedError) e;
            Assertions.assertThat(comparisonFailure.getExpected().getValue()).isEqualTo("one two");
            Assertions.assertThat(comparisonFailure.getActual().getValue()).isEqualTo("one three");
            //the platform truncates the message from its end, so the recorded file has to be named at its start
            Assertions.assertThat(e.getMessage())
                    .startsWith("[" + Files.getReferenceActualFile(referenceFile, 0).getAbsolutePath() + "]");
            Assertions.assertThat(
                    Files.readFileToString(Files.getReferenceActualFile(referenceFile, 0), Charset.defaultCharset()))
                    .isEqualTo("one three");
        }
    }

    @Test
    public void testCheckReferenceClearsStaleActualsOfChunksThatMatchBeforeOneFails() throws Exception {
        //The case a sweep after the loop cannot reach: chunk 0 matches, chunk 1 differs and throws. If cleanup only
        //happens at the end, chunk 0's recorded side from an earlier failing run survives the passing run.
        final File referenceFile = new File(tempDir, "chunked.csv");
        //the reference is written in create mode so the chunk files exist as chunks (a single write would put all of
        //it in chunk 0, and the second chunk would then fail as a missing file rather than as a difference)
        Files.checkReference(true, referenceFile, new StringInputStream("01234567"), 4);
        try {
            //first run: chunk 0 differs - its actual is recorded
            Files.checkReference(false, referenceFile, new StringInputStream("XXXXYYYY"), 4);
            Assertions.fail("the differing first chunk must fail the check");
        } catch (final AssertionError e) {
            Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 0)).exists();
        }
        try {
            //second run: chunk 0 matches, chunk 1 differs
            Files.checkReference(false, referenceFile, new StringInputStream("0123YYYY"), 4);
            Assertions.fail("the differing second chunk must fail the check");
        } catch (final AssertionError e) {
            Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 1)).exists();
            //the chunk that matched in THIS run must not keep the previous run's recorded side
            Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 0)).doesNotExist();
        }
    }

    @Test
    public void testCheckReferenceClearsAStaleActualWhenTheReferenceLaterMatches() throws Exception {
        //The case the fresh-directory test cannot reach: a previous run failed and left its recorded side behind, and
        //the next run matches. Stale evidence in a reference tree is worse than none - it gets quoted as if it came
        //from the run being read.
        final File referenceFile = new File(tempDir, "retry.csv");
        final File producedFile = new File(tempDir, "produced/retry.csv");
        Files.writeStringToFileIfDifferent(referenceFile, "one two");
        Files.writeStringToFileIfDifferent(producedFile, "one three");
        try {
            Files.checkReference(false, referenceFile, producedFile);
            Assertions.fail("the differing side must fail the check first");
        } catch (final AssertionError e) {
            Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 0)).exists();
        }
        Files.writeStringToFileIfDifferent(producedFile, "one two");
        Files.checkReference(false, referenceFile, producedFile);
        Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 0)).doesNotExist();
    }

    @Test
    public void testCheckReferenceRecordsNothingWhenTheReferenceMatches() throws Exception {
        final File referenceFile = new File(tempDir, "same.csv");
        final File producedFile = new File(tempDir, "produced/same.csv");
        Files.writeStringToFileIfDifferent(referenceFile, "one two");
        Files.writeStringToFileIfDifferent(producedFile, "one two");
        Files.checkReference(false, referenceFile, producedFile);
        //the recorded sibling is a difference report: a matching run must not leave one behind
        Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 0)).doesNotExist();
    }

    @Test
    public void testCheckReferenceRecordsTheDifferingChunkOfAReference() throws Exception {
        final File referenceFile = new File(tempDir, "chunked.csv");
        Files.writeStringToFileIfDifferent(referenceFile, "0123");
        Files.writeStringToFileIfDifferent(Files.prefixExtension(referenceFile, "_1"), "4567");
        Files.writeStringToFileIfDifferent(Files.prefixExtension(referenceFile, "_2"), "89");
        try {
            //four characters per chunk: only the last of the three chunks differs
            Files.checkReference(false, referenceFile, new StringInputStream("01234567XY"), 4);
            Assertions.fail("a differing chunk must fail the check");
        } catch (final AssertionError e) {
            final File differingReferenceFile = Files.prefixExtension(referenceFile, "_2");
            final File actualReferenceFile = Files.getReferenceActualFile(referenceFile, 2);
            Assertions.assertThat(e.getMessage())
                    .startsWith("[" + actualReferenceFile.getAbsolutePath() + "]");
            Assertions.assertThat(e.getMessage()).contains("[" + differingReferenceFile.getAbsolutePath() + "]");
            Assertions.assertThat(Files.readFileToString(actualReferenceFile, Charset.defaultCharset()))
                    .as("the recorded side belongs to the chunk index that differed")
                    .isEqualTo("XY");
            Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 0)).doesNotExist();
            Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 1)).doesNotExist();
        }
    }

    @Test
    public void testCheckReferenceKeepsThePathWhenTheMessageIsTruncated() throws Exception {
        final File referenceFile = new File(tempDir, "long.csv");
        final File producedFile = new File(tempDir, "produced/long.csv");
        //far past the comparison-failure message limit: the two values cannot survive the cut, the path must
        final String commonHead = "A".repeat(3000);
        Files.writeStringToFileIfDifferent(referenceFile, commonHead + "REF_TAIL");
        Files.writeStringToFileIfDifferent(producedFile, commonHead + "PROD_TAIL");
        try {
            Files.checkReference(false, referenceFile, producedFile);
            Assertions.fail("the reference difference must fail the check");
        } catch (final AssertionError e) {
            Assertions.assertThat(e.getMessage())
                    .startsWith("[" + Files.getReferenceActualFile(referenceFile, 0).getAbsolutePath() + "]");
            Assertions.assertThat(e.getMessage().length())
                    .as("the values are cut, so the message cannot be read past the limit")
                    .isLessThanOrEqualTo(Assertions.COMPARISON_FAILURE_MESSAGE_LIMIT);
            Assertions.assertThat(
                    Files.readFileToString(Files.getReferenceActualFile(referenceFile, 0), Charset.defaultCharset()))
                    .isEqualTo(commonHead + "PROD_TAIL");
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
    public void testWriteReferenceRemovesTheChunkNoLongerWritten() throws Exception {
        final File referenceFile = new File(tempDir, "shrink.csv");
        //three chunks become two: readReference concatenates every chunk it finds, so a leftover third chunk
        //appends the tail of the previous reference to the new one
        Files.writeStringToFileIfDifferent(referenceFile, "0123");
        Files.writeStringToFileIfDifferent(Files.prefixExtension(referenceFile, "_1"), "4567");
        Files.writeStringToFileIfDifferent(Files.prefixExtension(referenceFile, "_2"), "89");
        Files.writeReference(referenceFile, new StringInputStream("01234567"), 4);
        Assertions.assertThat(Files.prefixExtension(referenceFile, "_2"))
                .as("the chunk the rewritten reference no longer has")
                .doesNotExist();
        Assertions.assertThat(Files.prefixExtension(referenceFile, "_1")).exists();
        try (InputStream reference = Files.readReference(referenceFile)) {
            Assertions.assertThat(IOUtils.toString(reference, Charset.defaultCharset())).isEqualTo("0123\n4567");
        }
    }

    @Test
    public void testCreateReferenceClearsTheRecordOfALaterChunk() throws Exception {
        final File referenceFile = new File(tempDir, "gap.csv");
        Files.writeStringToFileIfDifferent(referenceFile, "0123");
        Files.writeStringToFileIfDifferent(Files.prefixExtension(referenceFile, "_1"), "4567");
        Files.writeStringToFileIfDifferent(Files.prefixExtension(referenceFile, "_2"), "89");
        try {
            Files.checkReference(false, referenceFile, new StringInputStream("01234567XY"), 4);
            Assertions.fail("the differing last chunk must fail the check");
        } catch (final AssertionError e) {
            //expected
        }
        //only chunk 2 differed, so its record is sparse: chunk indices 0 and 1 have none
        Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 2)).exists();
        Files.checkReference(true, referenceFile, new StringInputStream("01234567"), 4);
        Assertions.assertThat(Files.getReferenceActualFile(referenceFile, 2))
                .as("the create run rewrote the reference, so its record is stale")
                .doesNotExist();
        Assertions.assertThat(Files.prefixExtension(referenceFile, "_2")).doesNotExist();
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
