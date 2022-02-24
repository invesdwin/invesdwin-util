package de.invesdwin.util.streams.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.Resource;

import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.FlatteningResourceInputStream;

@NotThreadSafe
public class FlatteningResource implements Resource {

    private final Resource[] resources;

    public FlatteningResource(final Collection<? extends Resource> resources) {
        this(resources.toArray(Resources.EMPTY_ARRAY));
    }

    public FlatteningResource(final Resource... resources) {
        this.resources = resources;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FlatteningResourceInputStream(resources);
    }

    @Override
    public boolean exists() {
        if (resources.length == 0) {
            return false;
        }
        return resources[0].exists();
    }

    @Override
    public URL getURL() throws IOException {
        if (resources.length == 0) {
            return null;
        }
        return resources[0].getURL();
    }

    @Override
    public URI getURI() throws IOException {
        if (resources.length == 0) {
            return null;
        }
        return resources[0].getURI();
    }

    @Override
    public File getFile() throws IOException {
        if (resources.length == 0) {
            return null;
        }
        return resources[0].getFile();
    }

    @Override
    public long contentLength() throws IOException {
        if (resources.length == 0) {
            throw new IOException("Resources are empty");
        }
        long length = 0;
        for (int i = 0; i < resources.length; i++) {
            length += resources[i].contentLength();
        }
        return length;
    }

    @Override
    public long lastModified() throws IOException {
        if (resources.length == 0) {
            throw new IOException("Resources are empty");
        }
        long lastModified = -1;
        for (int i = 0; i < resources.length; i++) {
            lastModified = Longs.max(lastModified, resources[i].lastModified());
        }
        return lastModified;
    }

    @Override
    public Resource createRelative(final String relativePath) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFilename() {
        if (resources.length == 0) {
            return null;
        }
        return resources[0].getFilename();
    }

    @Override
    public String getDescription() {
        if (resources.length == 0) {
            return null;
        }
        return resources[0].getDescription();
    }

}
