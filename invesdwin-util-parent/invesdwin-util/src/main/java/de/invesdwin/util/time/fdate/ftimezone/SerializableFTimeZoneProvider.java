package de.invesdwin.util.time.fdate.ftimezone;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;

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
        final byte[] array = timeZone.getId().getBytes();
        stream.writeInt(array.length);
        stream.write(array);
    }

    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        final int size = stream.readInt();
        final byte[] array = new byte[size];
        stream.read(array);
        final String id = new String(array);
        timeZone = TimeZones.getFTimeZone(id);
    }

}
