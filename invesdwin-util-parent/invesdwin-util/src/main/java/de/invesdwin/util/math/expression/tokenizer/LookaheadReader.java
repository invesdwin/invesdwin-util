package de.invesdwin.util.math.expression.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class LookaheadReader extends ALookahead<Char> {

    private Reader input;
    private int line = 1;
    private int pos = 0;

    public void init(final Reader input) {
        super.init();
        this.input = new BufferedReader(input);
        line = 1;
        pos = 0;
    }

    public int getPos() {
        return pos;
    }

    @Override
    protected Char endOfInput() {
        return new Char('\0', line, pos);
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
                pos = 0;
            }
            pos++;
            return new Char((char) character, line, pos);
        } catch (final IOException e) {
            throw new ParseException(new Char('\0', line, pos), e.getMessage());
        }
    }

    @Override
    public String toString() {
        if (itemBuffer.isEmpty()) {
            return line + ":" + pos + ": Buffer empty";
        }
        if (itemBuffer.size() < 2) {
            return line + ":" + pos + ": " + current();
        }
        return line + ":" + pos + ": " + current() + ", " + next();
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
