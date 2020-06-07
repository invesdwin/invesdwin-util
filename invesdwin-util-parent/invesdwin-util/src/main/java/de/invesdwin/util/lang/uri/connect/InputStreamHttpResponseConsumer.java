package de.invesdwin.util.lang.uri.connect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.concurrent.Future;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.async.methods.AbstractBinResponseConsumer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;

import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.lang.uri.connect.apache.HttpResponseApache;
import de.invesdwin.util.lang.uri.connect.okhttp.HttpResponseOkHttp;
import de.invesdwin.util.math.decimal.scaled.ByteSize;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.streams.ADelegateInputStream;
import de.invesdwin.util.streams.DeletingFileInputStream;
import okhttp3.Call;
import okhttp3.Response;

@NotThreadSafe
public class InputStreamHttpResponseConsumer extends AbstractBinResponseConsumer<InputStreamHttpResponse> {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private static File defaultTempDir;
    private static Path defaultTempDirPath;
    private static int defaultMaxSizeInMemory = (int) ByteSizeScale.BYTES.convert(512, ByteSizeScale.KILOBYTES);

    private File tempDir = defaultTempDir;
    private Path tempDirPath = defaultTempDirPath;
    private int maxSizeInMemory = defaultMaxSizeInMemory;

    private IHttpResponse response;
    private ByteArrayBuilder byteArrayOut;
    private FileOutputStream fileOut;
    private File file;

    public InputStreamHttpResponseConsumer() {
    }

    /**
     * Set a non null value here to enable a write cache when the size exceeds 512KB. This helps to reduce the load on
     * the memory.
     */
    public static void setDefaultTempDir(final File file) {
        defaultTempDir = file;
        defaultTempDirPath = defaultTempDir.toPath();
    }

    /**
     * Use this method to change the default threshold of 512KB.
     */
    public static void setDefaultMaxSizeInMemory(final ByteSize defaultMaxSizeInMemory) {
        InputStreamHttpResponseConsumer.defaultMaxSizeInMemory = (int) defaultMaxSizeInMemory
                .getValue(ByteSizeScale.BYTES);
    }

    /**
     * Use this method to override the write cache directory. Null disables the write cache for this call.
     */
    public InputStreamHttpResponseConsumer withTempDir(final File tempDir) {
        this.tempDir = tempDir;
        if (tempDir == null) {
            this.tempDirPath = null;
        } else {
            this.tempDirPath = tempDir.toPath();
        }
        return this;
    }

    public File getTempDir() {
        return tempDir;
    }

    /**
     * Use this method to override the default size limit for using the write cache.
     */
    public InputStreamHttpResponseConsumer withMaxSizeInMemory(final ByteSize maxSizeInMemory) {
        this.maxSizeInMemory = (int) maxSizeInMemory.getValue(ByteSizeScale.BYTES);
        return this;
    }

    public int getMaxSizeInMemory() {
        return maxSizeInMemory;
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
    protected void start(final HttpResponse response, final ContentType contentType) {
        start(new HttpResponseApache(response));
    }

    public void start(final IHttpResponse response) {
        this.response = response;
        this.byteArrayOut = new ByteArrayBuilder();
    }

    @Override
    protected InputStreamHttpResponse buildResult() {
        return new InputStreamHttpResponse(response, newDelegate());
    }

    private InputStream newDelegate() {
        if (byteArrayOut != null) {
            return new ByteArrayInputStream(byteArrayOut.toByteArray());
        } else {
            return new ADelegateInputStream(
                    new TextDescription(InputStreamHttpResponseConsumer.class.getSimpleName())) {
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
                    if (byteArrayOut.size() > maxSizeInMemory) {
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

    public void consume(final InputStream src) throws FileNotFoundException, IOException {
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        if (byteArrayOut != null) {
            if (defaultTempDir != null) {
                int n;
                while (IOUtils.EOF != (n = src.read(buffer))) {
                    byteArrayOut.write(buffer, 0, n);
                    if (byteArrayOut.size() > maxSizeInMemory) {
                        Files.forceMkdir(tempDir);
                        file = newTempFile();
                        fileOut = new FileOutputStream(file);
                        IOUtils.write(byteArrayOut.toByteArray(), fileOut);
                        byteArrayOut = null;
                        break;
                    }
                }
            } else {
                while (src.available() > 0) {
                    byteArrayOut.write(src.read());
                }
            }
        }
        if (fileOut != null) {
            int n;
            while (IOUtils.EOF != (n = src.read(buffer))) {
                fileOut.write(buffer, 0, n);
            }
            fileOut.close();
            fileOut = null;
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

    public static InputStreamHttpResponse getInputStream(final Future<InputStreamHttpResponse> call, final URI uri)
            throws IOException {
        final InputStreamHttpResponse response;
        try {
            response = Futures.get(call);
        } catch (final InterruptedException e) {
            throw new IOException(e);
        }
        // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
        final int responseCode = response.getResponse().getCode();
        if (isSuccessful(responseCode)) {
            return response;
        } else {
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND || responseCode == HttpURLConnection.HTTP_GONE) {
                throw new FileNotFoundException(uri.toString());
            } else {
                if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    final String errorStr = IOUtils.toString(response, Charset.defaultCharset());
                    throw new IOException("Server returned HTTP response code [" + responseCode + "] for URL ["
                            + uri.toString() + "] with error:\n*****************************\n"
                            + Strings.putSuffix(errorStr, "\n") + "*****************************");
                } else {
                    throw new IOException("Server returned HTTP response code [" + responseCode + "] for URL ["
                            + uri.toString() + "]");
                }
            }
        }
    }

    public static InputStreamHttpResponse getInputStream(final CloseableHttpResponse response, final URI uri)
            throws IOException {
        try {
            // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
            final int responseCode = response.getCode();
            if (isSuccessful(responseCode)) {
                final InputStreamHttpResponseConsumer consumer = new InputStreamHttpResponseConsumer();
                consumer.start(response, ContentType.DEFAULT_BINARY);
                consumer.consume(response.getEntity().getContent());
                final InputStreamHttpResponse result = consumer.buildResult();
                consumer.releaseResources();
                return result;
            } else {
                if (responseCode == HttpURLConnection.HTTP_NOT_FOUND || responseCode == HttpURLConnection.HTTP_GONE) {
                    throw new FileNotFoundException(uri.toString());
                } else {
                    if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                        final String errorStr = IOUtils.toString(response.getEntity().getContent(),
                                Charset.defaultCharset());
                        throw new IOException("Server returned HTTP response code [" + responseCode + "] for URL ["
                                + uri.toString() + "] with error:\n*****************************\n"
                                + Strings.putSuffix(errorStr, "\n") + "*****************************");
                    } else {
                        throw new IOException("Server returned HTTP response code [" + responseCode + "] for URL ["
                                + uri.toString() + "]");
                    }
                }
            }
        } finally {
            response.close();
        }
    }

    public static InputStreamHttpResponse getInputStream(final Call call) throws IOException {
        final Response response = call.execute();
        // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
        if (response.isSuccessful()) {
            final InputStreamHttpResponseConsumer consumer = new InputStreamHttpResponseConsumer();
            consumer.start(new HttpResponseOkHttp(response));
            consumer.consume(response.body().byteStream());
            final InputStreamHttpResponse result = consumer.buildResult();
            consumer.releaseResources();
            return result;
        } else {
            final int respCode = response.code();
            final String urlString = call.request().url().toString();
            if (respCode == HttpURLConnection.HTTP_NOT_FOUND || respCode == HttpURLConnection.HTTP_GONE) {
                throw new FileNotFoundException(urlString);
            } else {
                if (respCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    final String errorStr = response.body().string();
                    throw new IOException("Server returned HTTP" + " response code: " + respCode + " for URL: "
                            + urlString + " error response:" + "\n*****************************" + errorStr
                            + "*****************************");
                } else {
                    throw new IOException(
                            "Server returned HTTP" + " response code: " + respCode + " for URL: " + urlString);
                }
            }
        }
    }

    public static boolean isSuccessful(final HttpResponse response) {
        return isSuccessful(response.getCode());
    }

    public static boolean isSuccessful(final int responseCode) {
        return responseCode >= 200 && responseCode <= 299;
    }

}
