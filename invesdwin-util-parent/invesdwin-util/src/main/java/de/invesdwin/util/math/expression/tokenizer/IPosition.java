package de.invesdwin.util.math.expression.tokenizer;

public interface IPosition {

    IPosition UNKNOWN = new IPosition() {

        @Override
        public int getLine() {
            return 0;
        }

        @Override
        public int getColumn() {
            return 0;
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public int getLength() {
            return 0;
        }
    };

    int getLine();

    int getColumn();

    int getIndex();

    int getLength();

}
