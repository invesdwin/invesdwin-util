package de.invesdwin.util.lang;

import java.io.File;
import java.nio.charset.Charset;

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
}
