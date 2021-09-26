package de.invesdwin.util.time.date.timezone;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@Immutable
public class SerializableFTimeZoneProvider implements ISerializableValueObject, IFTimeZoneProvider {

    private FTimeZone timeZone;

    public SerializableFTimeZoneProvider(final FTimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public FTimeZone asFTimeZone() {
        return timeZone;
    }

    private void writeObject(final java.io.ObjectOutputStream stream) throws IOException {
        final String id = timeZone.getId();
        stream.writeInt(ByteBuffers.newStringAsciiLength(id));
        stream.write(ByteBuffers.newStringAsciiBytes(id));
    }

    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        final int size = stream.readInt();
        final byte[] array = ByteBuffers.allocateByteArray(size);
        stream.read(array);
        final String id = ByteBuffers.newStringAscii(array);
        timeZone = TimeZones.getFTimeZone(id);
    }

}
