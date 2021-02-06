package de.invesdwin.util.math.expression.tokenizer;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LookaheadReader extends ALookahead<Char> {

    private String input;
    private int lineOffset = 0;
    private int columnOffset = -1;
    private int indexOffset = -1;
    private int lastIndexOffset = -1;

    public void init(final String input) {
        super.init();
        this.input = input;
        lineOffset = 0;
        columnOffset = -1;
        indexOffset = -1;
        lastIndexOffset = input.length() - 1;
    }

    public int getColumnOffset() {
        return columnOffset;
    }

    public int getIndexOffset() {
        return indexOffset;
    }

    @Override
    protected Char endOfInput() {
        return new Char('\0', lineOffset, columnOffset, indexOffset);
    }

    @Override
    protected Char fetch() {
        if (indexOffset >= lastIndexOffset) {
            return null;
        }
        indexOffset++;
        final char character = input.charAt(indexOffset);
        if (character == '\n') {
            lineOffset++;
            columnOffset = -1;
        } else {
            columnOffset++;
        }
        return new Char(character, lineOffset, columnOffset, indexOffset);
    }

    @Override
    public String toString() {
        if (itemBuffer.isEmpty()) {
            return lineOffset + ":" + columnOffset + ": Buffer empty";
        }
        if (itemBuffer.size() < 2) {
            return lineOffset + ":" + columnOffset + ": " + current();
        }
        return lineOffset + ":" + columnOffset + ": " + current() + ", " + next();
    }

    public void skipCharacters(final int amount) {
        for (int i = 0; i < amount; i++) {
            fetch();
        }
        if (current != null) {
            consume();
        }
    }

    public void setPosition(final IPosition position) {
        lineOffset = position.getLineOffset();
        columnOffset = position.getColumnOffset();
        indexOffset = position.getIndexOffset();
    }

    public IPosition getPosition() {
        return new Char(input.charAt(indexOffset), lineOffset, columnOffset, indexOffset);
    }

}
