package de.invesdwin.util.lang;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.output.WriterOutputStream;

import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.streams.EmptyInputStream;
import de.invesdwin.util.streams.delegate.ADelegateInputStream;
import it.unimi.dsi.fastutil.io.FastBufferedInputStream;

@Immutable
public class FileStringBuilder implements Appendable, Closeable {

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator() {
        @Override
        protected long getInitialValue() {
            return 1;
        }
    };
    private final FileStringBuilderFinalizer finalizer;

    public FileStringBuilder() {
        this(FileStringBuilder.class.getSimpleName());
    }

    @Deprecated
    public FileStringBuilder(final String name) {
        this(new File(new File(Files.getTempDirectory(), FileStringBuilder.class.getSimpleName()),
                Files.normalizeFilename(UNIQUE_NAME_GENERATOR.get(Strings.putSuffix(name, ".txt")))));
    }

    public FileStringBuilder(final File file) {
        this(file, true);
    }

    public FileStringBuilder(final File file, final boolean deleteOnClose) {
        this.finalizer = new FileStringBuilderFinalizer(file, deleteOnClose);
        finalizer.register(this);
    }

    public File getFile() {
        return finalizer.file;
    }

    private BufferedWriter getWriter() {
        if (finalizer.writer == null) {
            try {
                Files.forceMkdirParent(finalizer.file);
                finalizer.writer = new BufferedWriter(new FileWriter(finalizer.file));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        return finalizer.writer;
    }

    public FileStringBuilder append(final Object str) {
        return append(String.valueOf(str));
    }

    @Override
    public FileStringBuilder append(final CharSequence csq) {
        try {
            getWriter().append(csq);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileStringBuilder append(final CharSequence csq, final int start, final int end) {
        try {
            getWriter().append(csq, start, end);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileStringBuilder append(final char c) {
        try {
            getWriter().append(c);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileStringBuilder append(final String str) {
        try {
            getWriter().append(str);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileStringBuilder append(final char[] str) {
        for (int i = 0; i < str.length; i++) {
            append(str[i]);
        }
        return this;
    }

    public FileStringBuilder append(final char[] str, final int offset, final int len) {
        for (int i = offset; i < offset + len; i++) {
            append(str[i]);
        }
        return this;
    }

    public FileStringBuilder append(final boolean b) {
        return append(String.valueOf(b));
    }

    public FileStringBuilder append(final int i) {
        return append(String.valueOf(i));
    }

    public FileStringBuilder append(final long lng) {
        return append(String.valueOf(lng));
    }

    public FileStringBuilder append(final float f) {
        return append(String.valueOf(f));
    }

    public FileStringBuilder append(final double d) {
        return append(String.valueOf(d));
    }

    public void consume(final FileStringBuilder file) {
        try {
            file.closeWriter();
            appendFile(file.getFile());
        } finally {
            file.close();
        }
    }

    public FileStringBuilder consume(final File file) {
        try {
            return appendFile(file);
        } finally {
            Files.deleteQuietly(file);
        }
    }

    public FileStringBuilder appendFile(final File file) {
        try (WriterOutputStream outputStream = WriterOutputStream.builder().setWriter(getWriter()).get()) {
            Files.copyFile(file, outputStream);
            return this;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        if (finalizer.writer != null) {
            try {
                finalizer.writer.flush();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void closeWriter() {
        finalizer.closeWriter();
    }

    @Override
    public void close() {
        finalizer.close();
    }

    @Deprecated
    @Override
    public String toString() {
        closeWriter();
        return Files.readFileToStringNoThrow(getFile(), Charset.defaultCharset());
    }

    public InputStream toInputStream() {
        closeWriter();
        if (!finalizer.file.exists()) {
            return EmptyInputStream.INSTANCE;
        }
        return new ADelegateInputStream(
                new TextDescription("%s: %s", FileStringBuilder.class.getSimpleName(), finalizer.file)) {
            @Override
            protected InputStream newDelegate() {
                registerCloseable(() -> Files.deleteQuietly(finalizer.file));
                try {
                    return new FastBufferedInputStream(new FileInputStream(finalizer.file));
                } catch (final FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private static final class FileStringBuilderFinalizer extends AFinalizer {
        private final File file;
        private BufferedWriter writer;
        private final boolean deleteOnClose;

        private FileStringBuilderFinalizer(final File file, final boolean deleteOnClose) {
            this.file = file;
            this.deleteOnClose = deleteOnClose;
        }

        @Override
        protected void clean() {
            closeWriter();
            if (deleteOnClose) {
                Files.deleteQuietly(file);
            }
        }

        private void closeWriter() {
            final BufferedWriter writerCopy = writer;
            if (writerCopy != null) {
                try {
                    writerCopy.close();
                    writer = null;
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        protected boolean isCleaned() {
            return writer == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }

    }

    public boolean isEmpty() {
        return finalizer.writer == null && finalizer.file.length() == 0;
    }

}
