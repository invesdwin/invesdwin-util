package de.invesdwin.util.math.expression.tokenizer;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ParseError {

    private final IPosition pos;
    private final String message;

    protected ParseError(final IPosition pos, final String message) {
        this.pos = pos;
        this.message = message;
    }

    public static ParseError error(final IPosition pos, final String msg) {
        String message = msg;
        if (pos.getLine() > 0) {
            message = String.format("%3d:%2d: %s", pos.getLine(), pos.getPos(), msg);
        }
        return new ParseError(pos, message);
    }

    public IPosition getPosition() {
        return pos;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("ERROR: %s", message);
    }
}
