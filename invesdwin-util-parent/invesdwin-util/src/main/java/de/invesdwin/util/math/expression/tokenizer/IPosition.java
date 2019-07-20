package de.invesdwin.util.math.expression.tokenizer;

public interface IPosition {

    IPosition UNKNOWN = new IPosition() {

        @Override
        public int getLineOffset() {
            return -1;
        }

        @Override
        public int getColumnOffset() {
            return -1;
        }

        @Override
        public int getIndexOffset() {
            return -1;
        }

        @Override
        public int getLength() {
            return 0;
        }
    };

    /**
     * Starts counting at 1, 0 if undefined
     */
    default int getLine() {
        return getLineOffset() + 1;
    }

    /**
     * Starts counting at 1, 0 if undefined
     */
    default int getColumn() {
        return getColumnOffset() + 1;
    }

    /**
     * Starts counting at 1, 0 if undefined
     */
    default int getIndex() {
        return getIndexOffset() + 1;
    }

    int getLength();

    /**
     * Starts counting at 0, -1 if undefined
     */
    int getLineOffset();

    /**
     * Starts counting at 0, -1 if undefined
     */
    int getColumnOffset();

    /**
     * Starts counting at 0, -1 if undefined
     */
    int getIndexOffset();

}
