package de.invesdwin.util.math.expression.tokenizer;

public interface IPosition {

    IPosition UNKNOWN = new IPosition() {

        @Override
        public int getLine() {
            return 0;
        }

        @Override
        public int getPos() {
            return 0;
        }
    };

    int getLine();

    int getPos();

}
