package de.invesdwin.util.math.expression.tokenizer;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class Token implements IPosition {

    public enum TokenType {
        /**
         * Represents an name or reference like a function name or variable
         */
        ID,
        /**
         * Represents a string constant
         */
        STRING,
        /**
         * Represents a decimal constant
         */
        DECIMAL,
        /**
         * Represents an integer constant
         */
        INTEGER,
        /**
         * Represents any combination of "special" chars like + - ** etc. This will also be all bracket
         */
        SYMBOL,
        /**
         * Signals the end of input
         */
        EOI
    }

    private TokenType type;
    private String trigger = "";
    private String internTrigger = null;
    private String contents = "";
    private String source = "";

    private int pos;
    private int line;

    private Token() {}

    public static Token create(final TokenType type, final IPosition pos) {
        final Token result = new Token();
        result.type = type;
        result.line = pos.getLine();
        result.pos = pos.getPos();

        return result;
    }

    public static Token createAndFill(final TokenType type, final Char ch) {
        final Token result = new Token();
        result.type = type;
        result.line = ch.getLine();
        result.pos = ch.getPos();
        result.contents = ch.getStringValue();
        result.trigger = ch.getStringValue();
        result.source = ch.toString();
        return result;
    }

    public Token addToTrigger(final Char ch) {
        trigger += ch.getValue();
        internTrigger = null;
        source += ch.getValue();
        return this;
    }

    public Token addToSource(final Char ch) {
        source += ch.getValue();
        return this;
    }

    public Token addToContent(final Char ch) {
        return addToContent(ch.getValue());
    }

    public Token addToContent(final char ch) {
        contents += ch;
        source += ch;
        return this;
    }

    public Token silentAddToContent(final char ch) {
        contents += ch;
        return this;
    }

    public String getTrigger() {
        if (internTrigger == null) {
            internTrigger = trigger.intern();
        }
        return internTrigger;
    }

    public TokenType getType() {
        return type;
    }

    public String getContents() {
        return contents;
    }

    public String getSource() {
        return source;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getPos() {
        return pos;
    }

    public void setTrigger(final String trigger) {
        this.trigger = trigger;
        this.internTrigger = null;
    }

    public void setContent(final String content) {
        this.contents = content;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public boolean isEnd() {
        return type == TokenType.EOI;
    }

    public boolean isNotEnd() {
        return type != TokenType.EOI;
    }

    public boolean matches(final TokenType type, final String trigger) {
        if (!is(type)) {
            return false;
        }
        if (trigger == null) {
            throw new IllegalArgumentException("trigger must not be null");
        }

        return getTrigger() == trigger.intern();
    }

    public boolean wasTriggeredBy(final String... triggers) {
        if (triggers.length == 0) {
            return false;
        }
        for (int i = 0; i < triggers.length; i++) {
            final String aTrigger = triggers[i];
            if (aTrigger != null && aTrigger.intern() == getTrigger()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasContent(final String content) {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        return content.equalsIgnoreCase(getContents());
    }

    public boolean is(final TokenType type) {
        return this.type == type;
    }

    public boolean isSymbol(final String... symbols) {
        if (symbols.length == 0) {
            return is(TokenType.SYMBOL);
        }
        for (int i = 0; i < symbols.length; i++) {
            final String symbol = symbols[i];
            if (matches(TokenType.SYMBOL, symbol)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIdentifier(final String... values) {
        if (values.length == 0) {
            return is(TokenType.ID);
        }
        for (int i = 0; i < values.length; i++) {
            final String value = values[i];
            if (matches(TokenType.ID, value)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInteger() {
        return is(TokenType.INTEGER);
    }

    public boolean isDecimal() {
        return is(TokenType.DECIMAL);
    }

    public boolean isNumber() {
        return isInteger() || isDecimal();
    }

    public boolean isString() {
        return is(TokenType.STRING);
    }

    @Override
    public String toString() {
        return getType().toString() + ":" + getSource() + " (" + line + ":" + pos + ")";
    }
}
