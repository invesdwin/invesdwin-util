package de.invesdwin.util.math.expression.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LookaheadReader extends ALookahead<Char> {

    private Reader input;
    private int line = 1;
    private int column = 0;
    private int index = 0;

    public void init(final Reader input) {
        super.init();
        this.input = new BufferedReader(input);
        line = 1;
        column = 0;
        index = 0;
    }

    public int getColumn() {
        return column;
    }

    public int getIndex() {
        return index;
    }

    @Override
    protected Char endOfInput() {
        return new Char('\0', line, column, index);
    }

    @Override
    protected Char fetch() {
        try {
            final int character = input.read();
            if (character == -1) {
                return null;
            }
            if (character == '\n') {
                line++;
                column = 0;
            }
            column++;
            index++;
            return new Char((char) character, line, column, index);
        } catch (final IOException e) {
            throw new ParseException(new Char('\0', line, column, index), e.getMessage());
        }
    }

    @Override
    public String toString() {
        if (itemBuffer.isEmpty()) {
            return line + ":" + column + ": Buffer empty";
        }
        if (itemBuffer.size() < 2) {
            return line + ":" + column + ": " + current();
        }
        return line + ":" + column + ": " + current() + ", " + next();
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
