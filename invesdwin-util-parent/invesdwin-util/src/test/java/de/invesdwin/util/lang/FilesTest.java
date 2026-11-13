package de.invesdwin.util.lang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
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
