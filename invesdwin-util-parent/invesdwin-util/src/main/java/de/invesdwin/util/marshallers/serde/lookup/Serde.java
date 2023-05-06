package de.invesdwin.util.marshallers.serde.lookup;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.lookup.response.IResponseSerdeProvider;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Serde {

    /**
     * Should handle Object[] type which contains all arguments to the method.
     */
    Class<? extends ISerde<?>> request() default DEFAULT_REQUEST.class;

    /**
     * Can be used to dynamically resolve the Serde depending on the method call arguments.
     * 
     * responseProvider wins when also response if configured simultaneously. Thus responseProvider has a higher
     * priority.
     */
    Class<? extends IResponseSerdeProvider> responseProvider() default DEFAULT_RESPONSE_PROVIDER.class;

    /**
     * Should handle the return type of the method or general Object.
     */
    Class<? extends ISerde<?>> response() default DEFAULT_RESPONSE.class;

    //CHECKSTYLE:OFF
    class DEFAULT_REQUEST implements ISerde<Object[]> {
        //CHECKSTYLE:ON
        @Override
        public Object[] fromBytes(final byte[] bytes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] toBytes(final Object[] obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] fromBuffer(final IByteBuffer buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int toBuffer(final IByteBuffer buffer, final Object[] obj) {
            throw new UnsupportedOperationException();
        }
    }

    //CHECKSTYLE:OFF
    class DEFAULT_RESPONSE implements ISerde<Object> {
        //CHECKSTYLE:ON
        @Override
        public Object fromBytes(final byte[] bytes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] toBytes(final Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object fromBuffer(final IByteBuffer buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int toBuffer(final IByteBuffer buffer, final Object obj) {
            throw new UnsupportedOperationException();
        }
    }

    //CHECKSTYLE:OFF
    class DEFAULT_RESPONSE_PROVIDER implements IResponseSerdeProvider {
        //CHECKSTYLE:ON
        @Override
        public ISerde<Object> getSerde(final Object[] requestArgs) {
            throw new UnsupportedOperationException();
        }

    }

}
