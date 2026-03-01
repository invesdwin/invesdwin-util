package de.invesdwin.util.lang.string.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.streams.StringInputStream;

@NotThreadSafe
public final class SplitByMaxLengthIterator implements ICloseableIterator<String> {
    private final int maxLength;
    private final int whitespaceMaxLength;
    private final int hardMaxLength;
    private final boolean once;
    private final byte[] buffer = new byte[8192]; // 8KB buffer
    private final StringBuilder sb;
    private final InputStream input;
    private int bufferPos = 0;
    private int bufferLen = 0;
    private boolean eof = false;
    private boolean nextChunkReady = false;
    private boolean firstChunkReturned = false;

    private SplitByMaxLengthIterator(final InputStream input, final int maxLength, final boolean once) {
        this.input = input;
        this.maxLength = maxLength;
        this.once = once;
        whitespaceMaxLength = (int) (maxLength * 1.1D);
        hardMaxLength = (int) (whitespaceMaxLength * 1.1D);
        sb = new StringBuilder(hardMaxLength);
    }

    @Override
    public boolean hasNext() {
        if (!nextChunkReady && !eof) {
            // Try to find the next chunk
            findNextChunk();
        }
        return nextChunkReady || sb.length() > 0;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw FastNoSuchElementException.getInstance("No more chunks available");
        }

        if (nextChunkReady) {
            final String chunk = sb.toString();
            sb.setLength(0);
            nextChunkReady = false;
            firstChunkReturned = true;
            return chunk;
        } else if (sb.length() > 0) {
            final String chunk = sb.toString();
            sb.setLength(0);
            firstChunkReturned = true;
            return chunk;
        } else {
            throw FastNoSuchElementException.getInstance("No more chunks available");
        }
    }

    private void findNextChunk() {
        while (!nextChunkReady && !eof) {
            if (bufferPos >= bufferLen) {
                // Read more data
                try {
                    bufferLen = input.read(buffer);
                    if (bufferLen == -1) {
                        eof = true;
                        break;
                    }
                    bufferPos = 0;
                } catch (final IOException e) {
                    throw FastNoSuchElementException.getInstance(e.getMessage(), e);
                }
            }

            // Process current buffer
            while (bufferPos < bufferLen && !nextChunkReady) {
                final byte b = buffer[bufferPos++];
                final char c = (char) (b & 0xFF); // Handle as unsigned byte

                sb.append(c);

                // If once is true and we already returned the first chunk, don't create more chunks
                if (once && firstChunkReturned) {
                    continue;
                }

                if (sb.length() >= hardMaxLength || (sb.length() >= whitespaceMaxLength && Character.isWhitespace(c))
                        || (sb.length() >= maxLength && c == '\n')) {
                    nextChunkReady = true;
                }
            }
        }
    }

    @Override
    public void close() {
        try {
            input.close();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        sb.setLength(0);
    }

    public static ICloseableIterator<String> splitByMaxLength(final String str, final int maxLength) {
        return splitByMaxLength(str, maxLength, false);
    }

    public static ICloseableIterator<String> splitByMaxLength(final String str, final int maxLength,
            final boolean once) {
        return new SplitByMaxLengthIterator(new StringInputStream(str), maxLength, once);
    }

    public static ICloseableIterator<String> splitByMaxLength(final InputStream str, final int maxLength) {
        return new SplitByMaxLengthIterator(str, maxLength, false);
    }

    public static ICloseableIterator<String> splitByMaxLength(final InputStream str, final int maxLength,
            final boolean once) {
        return new SplitByMaxLengthIterator(str, maxLength, once);
    }
}