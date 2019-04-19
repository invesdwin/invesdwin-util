package de.invesdwin.util.math.expression.tokenizer;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Char implements IPosition {
    private final char value;
    private final int line;
    private final int pos;

    Char(final char value, final int line, final int pos) {
        this.value = value;
        this.line = line;
        this.pos = pos;
    }

    public char getValue() {
        return value;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public int getLength() {
        return 1;
    }

    public boolean isDigit() {
        return Character.isDigit(value);
    }

    public boolean isLetter() {
        return Character.isLetter(value) || value == '#';
    }

    public boolean isWhitepace() {
        return Character.isWhitespace(value) && !isEndOfInput();
    }

    public boolean isNewLine() {
        return value == '\n';
    }

    public boolean isEndOfInput() {
        return value == '\0';
    }

    @Override
    public String toString() {
        if (isEndOfInput()) {
            return "<End Of Input>";
        } else {
            return String.valueOf(value);
        }
    }

    public boolean is(final char... tests) {
        for (int i = 0; i < tests.length; i++) {
            final char test = tests[i];
            if (test == value && test != '\0') {
                return true;
            }
        }
        return false;
    }

    public String getStringValue() {
        if (isEndOfInput()) {
            return "";
        }
        return String.valueOf(value);
    }
}
