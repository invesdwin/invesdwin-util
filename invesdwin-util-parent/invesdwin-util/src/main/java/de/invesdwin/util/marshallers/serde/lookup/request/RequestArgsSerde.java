package de.invesdwin.util.marshallers.serde.lookup.request;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class RequestArgsSerde implements ISerde<Object[]> {

    private final IRequestSerdeProvider[] providers;
    private final Integer fixedArrayLength;
    private final int maxSerdeProviderIndex;

    public RequestArgsSerde(final IRequestSerdeProvider[] providers, final boolean varArgs) {
        this.providers = providers;
        if (varArgs) {
            this.fixedArrayLength = null;
        } else {
            this.fixedArrayLength = providers.length;
        }
        this.maxSerdeProviderIndex = providers.length - 1;
    }

    @Override
    public Object[] fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final Object[] obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public Object[] fromBuffer(final IByteBuffer buffer) {
        final int arrayLength;
        int position = 0;
        if (fixedArrayLength == null) {
            arrayLength = buffer.getInt(position);
            position += Integer.BYTES;
        } else if (fixedArrayLength == 1) {
            final Object[] result = new Object[1];
            final IRequestSerdeProvider serdeProvider = providers[0];
            final ISerde<Object> serde = serdeProvider.getSerde(result, 0);
            result[0] = serde.fromBuffer(buffer);
            return result;
        } else {
            arrayLength = fixedArrayLength;
        }
        final Object[] result = new Object[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            final int curLength = buffer.getInt(position);
            position += Integer.BYTES;
            final IRequestSerdeProvider serdeProvider = providers[Integers.min(i, maxSerdeProviderIndex)];
            final ISerde<Object> serde = serdeProvider.getSerde(result, i);
            result[i] = serde.fromBuffer(buffer.slice(position, curLength));
            position += curLength;
        }
        return result;
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final Object[] obj) {
        final int arrayLength = obj.length;
        int position = 0;
        if (fixedArrayLength == null) {
            buffer.putInt(position, arrayLength);
            position += Integer.BYTES;
        } else if (fixedArrayLength == 1) {
            assertFixedArrayLength(arrayLength);
            final IRequestSerdeProvider serdeProvider = providers[0];
            final ISerde<Object> serde = serdeProvider.getSerde(obj, 0);
            return serde.toBuffer(buffer, obj[0]);
        } else {
            assertFixedArrayLength(arrayLength);
        }
        for (int i = 0; i < arrayLength; i++) {
            final IRequestSerdeProvider serdeProvider = providers[Integers.min(i, maxSerdeProviderIndex)];
            final ISerde<Object> serde = serdeProvider.getSerde(obj, i);
            final int curLength = serde.toBuffer(buffer.sliceFrom(position + Integer.BYTES), obj[i]);
            buffer.putInt(position, curLength);
            position += Integer.BYTES + curLength;
        }
        return position;
    }

    private void assertFixedArrayLength(final int arrayLength) {
        if (arrayLength != fixedArrayLength) {
            throw new IllegalArgumentException(
                    "arrayLength[" + arrayLength + "] != fixedarrayLength[" + fixedArrayLength + "]");
        }
    }

}
