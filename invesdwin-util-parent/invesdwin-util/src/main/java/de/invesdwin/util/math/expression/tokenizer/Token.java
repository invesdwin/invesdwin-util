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

    private int columnOffset;
    private int lineOffset;
    private int indexOffset;

    private Token() {}

    public static Token create(final TokenType type, final IPosition pos) {
        final Token result = new Token();
        result.type = type;
        result.lineOffset = pos.getLineOffset();
        result.columnOffset = pos.getColumnOffset();
        result.indexOffset = pos.getIndexOffset();

        return result;
    }

    public static Token create(final Token previous, final String newContent) {
        final Token result = new Token();
        result.type = previous.type;
        result.lineOffset = previous.lineOffset;
        result.columnOffset = previous.columnOffset;
        result.indexOffset = previous.indexOffset;
        result.trigger = newContent;
        result.contents = newContent;
        result.source = newContent;
        return result;
    }

    public static Token createAndFill(final TokenType type, final Char ch) {
        final Token result = new Token();
        result.type = type;
        result.lineOffset = ch.getLineOffset();
        result.columnOffset = ch.getColumnOffset();
        result.indexOffset = ch.getIndexOffset();
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
        return getSource().length();
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

    public boolean matches(final String trigger) {
        return getTrigger() == trigger.intern();
    }

    public boolean is(final TokenType type) {
        return this.type == type;
    }

    public boolean isSymbol() {
        return is(TokenType.SYMBOL);
    }

    public boolean isSymbol(final String trigger) {
        return is(TokenType.SYMBOL) && matches(trigger);
    }

    public boolean isIdentifier() {
        return is(TokenType.ID);
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
        return getType().toString() + ":" + getSource() + " (" + lineOffset + ":" + columnOffset + ")";
    }
}
