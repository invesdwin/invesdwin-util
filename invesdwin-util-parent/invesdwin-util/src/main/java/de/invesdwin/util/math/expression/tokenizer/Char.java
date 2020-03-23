package de.invesdwin.util.math.expression.tokenizer;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Char implements IPosition {
    private final char value;
    private final int lineOffset;
    private final int columnOffset;
    private final int indexOffset;

    Char(final char value, final int lineOffset, final int columnOffset, final int indexOffset) {
        this.value = value;
        this.lineOffset = lineOffset;
        this.columnOffset = columnOffset;
        this.indexOffset = indexOffset;
    }

    public char getValue() {
        return value;
    }

    @Override
    public int getLineOffset() {
        return lineOffset;
    }

    @Override
    public int getColumnOffset() {
        return columnOffset;
    }

    @Override
    public int getIndexOffset() {
        return indexOffset;
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

    public boolean isSemicolon() {
        return value == ';';
    }
}
