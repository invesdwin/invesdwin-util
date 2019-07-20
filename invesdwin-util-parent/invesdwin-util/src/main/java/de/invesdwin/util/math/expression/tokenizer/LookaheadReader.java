package de.invesdwin.util.math.expression.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LookaheadReader extends ALookahead<Char> {

    private Reader input;
    private int lineOffset = 0;
    private int columnOffset = -1;
    private int indexOffset = -1;

    public void init(final Reader input) {
        super.init();
        this.input = new BufferedReader(input);
        lineOffset = 0;
        columnOffset = -1;
        indexOffset = -1;
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
        try {
            final int character = input.read();
            if (character == -1) {
                return null;
            }
            if (character == '\n') {
                lineOffset++;
                columnOffset = -1;
            } else {
                columnOffset++;
            }
            indexOffset++;
            return new Char((char) character, lineOffset, columnOffset, indexOffset);
        } catch (final IOException e) {
            throw new ParseException(new Char('\0', lineOffset, columnOffset, indexOffset), e.getMessage());
        }
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

}
