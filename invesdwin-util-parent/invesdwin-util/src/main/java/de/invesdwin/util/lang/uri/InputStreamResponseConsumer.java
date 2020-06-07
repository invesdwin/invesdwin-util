package de.invesdwin.util.lang.uri;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.async.methods.AbstractBinResponseConsumer;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.streams.ADelegateInputStream;
import de.invesdwin.util.streams.DeletingFileInputStream;

@NotThreadSafe
public class InputStreamResponseConsumer extends AbstractBinResponseConsumer<HttpInputStream> {

    private static final int MAX_SIZE_IN_MEMORY = (int) ByteSizeScale.BYTES.convert(512, ByteSizeScale.KILOBYTES);
    private static File defaultTempDir;
    private static Path defaultTempDirPath;

    private File tempDir = defaultTempDir;
    private Path tempDirPath = defaultTempDirPath;

    private HttpResponse response;
    private ByteArrayBuilder byteArrayOut;
    private FileOutputStream fileOut;
    private File file;

    public InputStreamResponseConsumer() {
    }

    /**
     * Set a non null value here to enable a write cache when the size exceeds 512KB. This helps to reduce the load on
     * the memory.
     */
    public static void setDefaultTempDir(final File file) {
        defaultTempDir = file;
        defaultTempDirPath = defaultTempDir.toPath();
    }

    public InputStreamResponseConsumer withTempDir(final File tempDir) {
        this.tempDir = tempDir;
        this.tempDirPath = tempDir.toPath();
        return this;
    }

    @Override
    public void releaseResources() {
        byteArrayOut = null;
        if (fileOut != null) {
            try {
                fileOut.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            fileOut = null;
            Files.deleteQuietly(file);
            file = null;
        }
    }

    @Override
    protected void start(final HttpResponse response, final ContentType contentType) throws HttpException, IOException {
        this.response = response;
        this.byteArrayOut = new ByteArrayBuilder();
    }

    @Override
    protected HttpInputStream buildResult() {
        return new HttpInputStream(response, newDelegate());
    }

    private InputStream newDelegate() {
        if (byteArrayOut != null) {
            return new ByteArrayInputStream(byteArrayOut.toByteArray());
        } else {
            return new ADelegateInputStream(new TextDescription(InputStreamResponseConsumer.class.getSimpleName())) {
                @Override
                protected InputStream newDelegate() {
                    try {
                        return new DeletingFileInputStream(file);
                    } catch (final FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }

    @Override
    protected int capacityIncrement() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected void data(final ByteBuffer src, final boolean endOfStream) throws IOException {
        if (byteArrayOut != null) {
            if (defaultTempDir != null) {
                while (src.hasRemaining()) {
                    byteArrayOut.write(src.get());
                    if (byteArrayOut.size() > MAX_SIZE_IN_MEMORY) {
                        Files.forceMkdir(tempDir);
                        file = newTempFile();
                        fileOut = new FileOutputStream(file);
                        IOUtils.write(byteArrayOut.toByteArray(), fileOut);
                        byteArrayOut = null;
                        break;
                    }
                }
            } else {
                while (src.hasRemaining()) {
                    byteArrayOut.write(src.get());
                }
            }
        }
        if (fileOut != null) {
            while (src.hasRemaining()) {
                fileOut.write(src.get());
            }
            if (endOfStream) {
                fileOut.close();
                fileOut = null;
            }
        }
    }

    private File newTempFile() {
        try {
            return Files.createTempFile(tempDirPath, "download_", ".bin").toFile();
        } catch (final IOException e1) {
            if (!tempDir.exists()) {
                try {
                    Files.forceMkdir(tempDir);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    return Files.createTempFile(tempDirPath, "download_", ".bin").toFile();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(e1);
            }
        }
    }

}
