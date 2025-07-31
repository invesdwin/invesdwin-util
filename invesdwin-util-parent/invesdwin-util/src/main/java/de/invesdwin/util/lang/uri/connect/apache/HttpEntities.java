package de.invesdwin.util.lang.uri.connect.apache;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import org.apache.hc.core5.http.HttpEntity;

import de.invesdwin.util.streams.pool.PooledFastByteArrayOutputStream;

@Immutable
public final class HttpEntities {

    private HttpEntities() {}

    /**
     * entity.getContent() has a size limit. By using this method we can avoid going into that validation and thus get
     * the content of any size.
     */
    public static byte[] getContent(final HttpEntity entity) {
        try (PooledFastByteArrayOutputStream out = PooledFastByteArrayOutputStream.newInstance()) {
            entity.writeTo(out);
            return out.toByteArray();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
