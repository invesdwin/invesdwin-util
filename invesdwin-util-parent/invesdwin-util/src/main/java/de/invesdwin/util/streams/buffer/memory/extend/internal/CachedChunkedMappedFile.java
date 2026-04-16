package de.invesdwin.util.streams.buffer.memory.extend.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.bytes.MappedBytesStore;
import net.openhft.chronicle.bytes.MappedBytesStoreFactory;
import net.openhft.chronicle.bytes.MappedFile;
import net.openhft.chronicle.bytes.PageUtil;
import net.openhft.chronicle.core.io.CleaningRandomAccessFile;
import net.openhft.chronicle.core.io.ClosedIllegalStateException;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.io.ReferenceOwner;
import net.openhft.chronicle.core.io.ThreadingIllegalStateException;

@NotThreadSafe
public class CachedChunkedMappedFile extends net.openhft.chronicle.bytes.internal.ChunkedMappedFile {

    private static final long DEFAULT_CAPACITY = 128L << 40;

    private final long chunkSize;
    private final List<MappedBytesStore> chunkToMappedBytesStore = new ArrayList<>();

    public CachedChunkedMappedFile(final File file, final RandomAccessFile raf, final long chunkSize,
            final long overlapSize, final long capacity, final boolean readOnly) throws IORuntimeException {
        super(file, raf, chunkSize, overlapSize, capacity, readOnly);
        this.chunkSize = chunkSize;
    }

    public CachedChunkedMappedFile(final File file, final RandomAccessFile raf, final long chunkSize,
            final long overlapSize, final int pageSize, final long capacity, final boolean readOnly)
            throws IORuntimeException {
        super(file, raf, chunkSize, overlapSize, pageSize, capacity, readOnly);
        this.chunkSize = chunkSize;
    }

    @Override
    public MappedBytesStore acquireByteStore(final ReferenceOwner owner, final long position,
            final BytesStore<?, ?> oldByteStore, final MappedBytesStoreFactory mappedBytesStoreFactory)
            throws IOException, IllegalArgumentException, ClosedIllegalStateException, ThreadingIllegalStateException {
        final int chunk = (int) (position / chunkSize);

        while (chunkToMappedBytesStore.size() <= chunk) {
            chunkToMappedBytesStore.add(null);
        }

        final MappedBytesStore existingStore = chunkToMappedBytesStore.get(chunk);
        if (existingStore != null && existingStore.tryReserve(owner)) {
            return existingStore;
        }

        final MappedBytesStore newStore = super.acquireByteStore(owner, position, oldByteStore,
                mappedBytesStoreFactory);
        if (!RETAIN) {
            //make sure we always retain
            newStore.reserve(this);
        }
        chunkToMappedBytesStore.set(chunk, newStore);
        return newStore;
    }

    @Override
    protected void performRelease() {
        chunkToMappedBytesStore.clear();
        super.performRelease();
    }

    @Override
    public net.openhft.chronicle.bytes.MappedBytes createBytesFor() throws ClosedIllegalStateException {
        return new CachedChunkedMappedBytes(this);
    }

    public static net.openhft.chronicle.bytes.MappedBytes mappedBytes(final File file, final long chunkSize,
            final long overlapSize, final boolean readOnly) throws FileNotFoundException, ClosedIllegalStateException {
        return mappedBytes(file, chunkSize, overlapSize, PageUtil.getPageSize(file.getAbsolutePath()), readOnly);
    }

    public static net.openhft.chronicle.bytes.MappedBytes mappedBytes(final File file, final long chunkSize,
            final long overlapSize, final int pageSize, final boolean readOnly)
            throws FileNotFoundException, ClosedIllegalStateException {
        final MappedFile rw = of(file, chunkSize, overlapSize, pageSize, readOnly);
        try {
            return net.openhft.chronicle.bytes.MappedBytes.mappedBytes(rw);
        } finally {
            rw.release(INIT);
        }
    }

    public static CachedChunkedMappedFile of(final File file, final long chunkSize, final long overlapSize,
            final boolean readOnly) throws FileNotFoundException {
        return of(file, chunkSize, overlapSize, PageUtil.getPageSize(file.getAbsolutePath()), readOnly);
    }

    public static CachedChunkedMappedFile of(final File file, final long chunkSize, final long overlapSize,
            final int pageSize, final boolean readOnly) throws FileNotFoundException {
        final RandomAccessFile raf = new CleaningRandomAccessFile(file, readOnly ? "r" : "rw");
        return new CachedChunkedMappedFile(file, raf, chunkSize, overlapSize, pageSize, DEFAULT_CAPACITY, readOnly);
    }

}
