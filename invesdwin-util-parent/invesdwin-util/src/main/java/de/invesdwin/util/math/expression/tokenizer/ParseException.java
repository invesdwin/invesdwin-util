package de.invesdwin.util.math.expression.tokenizer;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ParseException extends RuntimeException {

    private final IPosition pos;
    private final String originalMessage;

    public ParseException(final IPosition pos, final String message) {
        super(newMessage(pos, message));
        this.pos = pos;
        this.originalMessage = message;
    }

    private static String newMessage(final IPosition pos, final String message) {
        if (pos.getLine() > 0) {
            return "Line=" + pos.getLine() + ": Column=" + pos.getPos() + ": " + message;
        } else {
            return message;
        }
    }

    public IPosition getPosition() {
        return pos;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

}
