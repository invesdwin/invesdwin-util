package de.invesdwin.util.streams.buffer.file;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.OperatingSystem;

@NotThreadSafe
public class MemoryMappedFileSegmentSizingTest {

    /**
     * The predicate must answer the question its callers actually ask it: 'would this length not fit a single segment
     * on this platform'. The timeseriesdb writer chooser calls it with Long.MAX_VALUE as a platform probe, and
     * MAX_SEGMENT_SIZE is itself Long.MAX_VALUE off Windows, so comparing with '>=' makes that probe true on every
     * platform - which silently routes every Linux update attempt through the chunked writer and leaves the continuous
     * one unreachable (cp 3eedec94, 2026-08-31, on top of util 24be80c5, 2026-04-17). Pinned as a platform invariant
     * because the two defects are only visible together: neither file looks wrong alone.
     */
    @Test
    public void testLongMaxValueProbeIsAPlatformTestNotATautology() {
        Assertions.assertThat(IMemoryMappedFile.isSegmentSizeExceeded(Long.MAX_VALUE))
                .as("the Long.MAX_VALUE probe must be false wherever a single segment has no ceiling")
                .isEqualTo(OperatingSystem.isWindows());
    }

    @Test
    public void testSegmentationStartsOnlyAboveTheCeiling() {
        Assertions.assertThat(IMemoryMappedFile.isSegmentSizeExceeded(1)).isFalse();
        Assertions.assertThat(IMemoryMappedFile.isSegmentSizeExceeded(IMemoryMappedFile.MAX_SEGMENT_SIZE - 1))
                .isFalse();
        //exactly at the ceiling still fits one segment
        Assertions.assertThat(IMemoryMappedFile.isSegmentSizeExceeded(IMemoryMappedFile.MAX_SEGMENT_SIZE)).isFalse();
        if (OperatingSystem.isWindows()) {
            //the ceiling is finite here, so one byte above it has to segment
            Assertions.assertThat(IMemoryMappedFile.isSegmentSizeExceeded(IMemoryMappedFile.MAX_SEGMENT_SIZE + 1))
                    .isTrue();
        }
    }
}
