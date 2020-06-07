package de.invesdwin.util.lang.uri.connect.apache;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.hc.client5.http.async.methods.AbstractBinResponseConsumer;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;

import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponse;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponseConsumer;

@NotThreadSafe
public class InputStreamHttpResponseConsumerApache extends AbstractBinResponseConsumer<InputStreamHttpResponse> {

    private final InputStreamHttpResponseConsumer delegate = new InputStreamHttpResponseConsumer();

    @Override
    public void releaseResources() {
        delegate.releaseResources();
    }

    @Override
    protected void start(final HttpResponse response, final ContentType contentType) throws HttpException, IOException {
        delegate.start(new HttpResponseApache(response));
    }

    @Override
    protected InputStreamHttpResponse buildResult() {
        return delegate.buildResult();
    }

    @Override
    protected int capacityIncrement() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected void data(final ByteBuffer src, final boolean endOfStream) throws IOException {
        delegate.data(src, endOfStream);
    }

}
