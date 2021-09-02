package de.invesdwin.util.math.expression.tokenizer;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.math.expression.tokenizer.Token.TokenType;

@NotThreadSafe
public class Tokenizer extends ALookahead<Token> {

    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final String LINE_COMMENT = "//";
    private static final String BLOCK_COMMENT_START = "/*";
    private static final String BLOCK_COMMENT_END = "*/";
    private static final char DECIMAL_SEPARATOR = '.';
    private static final char GROUPING_SEPARATOR = '_';
    private static final char[] BRACKETS = { '(', '[', '{', '}', ']', ')' };
    private static final boolean TREAT_SINGLE_PIPE_AS_BRACKET = true;

    /*
     * Scientific notation separator (e.g. 3e2 = 3*10**2 = 300)
     */
    private static final char SCIENTIFIC_NOTATION_SEPARATOR = 'e';
    private static final char UPPER_SCIENTIFIC_NOTATION_SEPARATOR = 'E';

    private static final Map<Character, Character> STRING_DELIMITERS = new IdentityHashMap<Character, Character>() {
        {
            put('"', '\\');
            put('\'', '\0');
        }
    };

    protected final LookaheadReader input;
    private boolean semicolonAllowed;

    public Tokenizer() {
        this.input = new LookaheadReader();
    }

    public void init(final String input, final boolean semicolonAllowed) {
        super.init();
        this.input.init(input);
        this.semicolonAllowed = semicolonAllowed;
    }

    @Override
    protected Token endOfInput() {
        return Token.createAndFill(Token.TokenType.EOI, input.current());
    }

    //CHECKSTYLE:OFF
    @Override
    protected Token fetch() {
        //CHECKSTYLE:ON
        // Fetch and ignore any whitespace
        while (input.current().isWhitepace()) {
            input.consume();
        }

        if (input.current.isSemicolon() && !semicolonAllowed) {
            //semicolon is an unsupported character, it is reserved for csv files containing expressions
            throw new ParseException(input.current(),
                    TextDescription.format(
                            "Semicolon is reserved for splitting multiple expressions in CSV files: '%s'",
                            input.current().getStringValue()));
        }

        // End of input reached? Pass end of input signal on...
        if (input.current().isEndOfInput()) {
            return null;
        }

        // Handle (and ignore) line comments
        if (isAtStartOfLineComment(true)) {
            skipToEndOfLine();
            return fetch();
        }

        // Handle (and ignore) block comments
        if (isAtStartOfBlockComment(true)) {
            skipBlockComment();
            return fetch();
        }

        // A digit signals the start of a number
        if (isAtStartOfNumber()) {
            return fetchNumber();
        }

        // A letter signals the start of an id
        if (isAtStartOfIdentifier()) {
            return fetchId();
        }

        // A " or ' (or whatever string delimiters are used...) start a string constant
        if (STRING_DELIMITERS.containsKey(input.current().getValue())) {
            return fetchString();
        }

        // Treat brackets as special symbols: (( will create two consecutive symbols but ** will create a single
        // symbol "**".
        if (isAtBracket(false)) {
            return Token.createAndFill(Token.TokenType.SYMBOL, input.consume());
        }

        // Read all symbol characters and form a SYMBOL of it
        if (isSymbolCharacter(input.current())) {
            return fetchSymbol();
        }

        throw new ParseException(input.current(),
                TextDescription.format("Invalid character in input: '%s'", input.current().getStringValue()));
    }

    protected boolean isAtStartOfNumber() {
        //CHECKSTYLE:OFF
        return input.current().isDigit() || input.current().is(MINUS) && input.next().isDigit()
                || input.current().is(MINUS) && input.next().is(DECIMAL_SEPARATOR) && input.next(2).isDigit()
                || input.current().is(DECIMAL_SEPARATOR) && input.next().isDigit();
        //CHECKSTYLE:ON
    }

    private boolean isAtBracket(final boolean inSymbol) {
        return input.current().is(BRACKETS)
                || !inSymbol && TREAT_SINGLE_PIPE_AS_BRACKET && input.current().is('|') && !input.next().is('|');
    }

    private Token fetchString() {
        final char separator = input.current().getValue();
        final char escapeChar = STRING_DELIMITERS.get(input.current().getValue());
        final Token result = Token.create(Token.TokenType.STRING, input.current());
        result.addToTrigger(input.consume());
        while (!input.current().isNewLine() && !input.current().is(separator) && !input.current().isEndOfInput()) {
            if (escapeChar != '\0' && input.current().is(escapeChar)) {
                result.addToSource(input.consume());
                if (!handleStringEscape(separator, escapeChar, result)) {
                    throw new ParseException(input.next(), TextDescription
                            .format("Cannot use '%s' as escaped character", input.next().getStringValue()));
                }
            } else {
                result.addToContent(input.consume());
            }
        }
        if (input.current().is(separator)) {
            result.addToSource(input.consume());
        } else {
            throw new ParseException(input.current(), "Premature end of string constant");
        }
        return result;
    }

    private boolean handleStringEscape(final char separator, final char escapeChar, final Token stringToken) {
        if (input.current().is(separator)) {
            stringToken.addToContent(separator);
            stringToken.addToSource(input.consume());
            return true;
        } else if (input.current().is(escapeChar)) {
            stringToken.silentAddToContent(escapeChar);
            stringToken.addToSource(input.consume());
            return true;
        } else if (input.current().is('n')) {
            stringToken.silentAddToContent('\n');
            stringToken.addToSource(input.consume());
            return true;
        } else if (input.current().is('r')) {
            stringToken.silentAddToContent('\r');
            stringToken.addToSource(input.consume());
            return true;
        } else {
            return false;
        }
    }

    private boolean isAtStartOfIdentifier() {
        return input.current().isLetter() || input.current().is('@');
    }

    private Token fetchId() {
        final Token result = Token.create(Token.TokenType.ID, input.current());
        result.addToContent(input.consume());
        while (isIdentifierChar(input.current())) {
            result.addToContent(input.consume());
        }
        return result;
    }

    private boolean isIdentifierChar(final Char current) {
        return current.isDigit() || current.isLetter() || current.is('_', ':', '@');
    }

    private Token fetchSymbol() {
        final Token result = Token.create(Token.TokenType.SYMBOL, input.current());
        result.addToTrigger(input.consume());
        //CHECKSTYLE:OFF
        if (result.isSymbol("*") && input.current().is('*') || result.isSymbol("&") && input.current().is('&')
                || result.isSymbol("|") && input.current().is('|') || result.isSymbol()
                        && (input.current().is('=') || input.current().is('>') || input.current().is('<'))) {
            //CHECKSTYLE:ON
            result.addToTrigger(input.consume());
        }
        return result;
    }

    private boolean isSymbolCharacter(final Char ch) {
        if (ch.isEndOfInput() || ch.isDigit() || ch.isLetter() || ch.isWhitepace()) {
            return false;
        }

        final char c = ch.getValue();
        if (Character.isISOControl(c)) {
            return false;
        }

        return !(isAtBracket(true) || isAtStartOfNumber() || isAtStartOfIdentifier()
                || STRING_DELIMITERS.containsKey(ch.getValue()));
    }

    private Token fetchNumber() {
        Token result = Token.create(Token.TokenType.INTEGER, input.current());
        result.addToContent(input.consume());
        while (input.current().isDigit() || input.current().is(DECIMAL_SEPARATOR)
                || (input.current().is(GROUPING_SEPARATOR) && input.next().isDigit()) || isScientificNumber()) {
            if (input.current().is(GROUPING_SEPARATOR)) {
                result.addToSource(input.consume());
            } else if (input.current().is(DECIMAL_SEPARATOR)) {
                if (result.isDecimal() || result.isScientificDecimal()) {
                    throw new ParseException(input.current(), "Unexpected decimal separators");
                } else {
                    final Token decimalToken = Token.create(Token.TokenType.DECIMAL, result);
                    decimalToken.setContent(result.getContents() + DECIMAL_SEPARATOR);
                    decimalToken.setSource(result.getSource());
                    result = decimalToken;
                }
                result.addToSource(input.consume());
            } else if (isScientificSeparator()) {
                if (result.isScientificDecimal()) {
                    throw new ParseException(input.current(), "Unexpected scientific notation separators");
                } else {
                    final Token scientificDecimalToken = Token.create(TokenType.SCIENTIFIC_DECIMAL, result);
                    scientificDecimalToken.setContent(result.getContents() + SCIENTIFIC_NOTATION_SEPARATOR);
                    scientificDecimalToken.setSource(result.getSource() + SCIENTIFIC_NOTATION_SEPARATOR);
                    result = scientificDecimalToken;
                    input.consume();
                    if (input.current().is(PLUS) || input.current().is(MINUS)) {
                        result.addToContent(input.consume());
                    }
                }
            } else {
                result.addToContent(input.consume());
            }
        }

        return result;
    }

    private boolean isScientificNumber() {
        return isScientificSeparator() && (input.next().isDigit() || input.next().is(PLUS) || input.next().is(MINUS));
    }

    private boolean isScientificSeparator() {
        return input.current().is(SCIENTIFIC_NOTATION_SEPARATOR)
                || input.current().is(UPPER_SCIENTIFIC_NOTATION_SEPARATOR);
    }

    @Override
    public String toString() {
        // We check the internal buffer first to that no further parsing is triggered by calling toString()
        // as it is frequently invoked by the debugger which causes nasty side-effects otherwise
        if (itemBuffer.isEmpty()) {
            return "No Token fetched...";
        }
        if (itemBuffer.size() < 2) {
            return "Current: " + current();
        }
        return "Current: " + current().toString() + ", Next: " + next().toString();
    }

    public void consumeExpectedSymbol(final String symbol) {
        final Token current = current();
        if (current.isSymbol() && current.matches(symbol)) {
            consume();
        } else {
            throw new ParseException(current,
                    TextDescription.format("Unexpected token: '%s'. Expected: '%s'", current.getSource(), symbol));
        }
    }

    private boolean isAtStartOfLineComment(final boolean consume) {
        return canConsumeThisString(LINE_COMMENT, consume);
    }

    private void skipToEndOfLine() {
        while (!input.current().isEndOfInput() && !input.current().isNewLine()) {
            input.consume();
        }
    }

    private boolean isAtStartOfBlockComment(final boolean consume) {
        return canConsumeThisString(BLOCK_COMMENT_START, consume);
    }

    private boolean isAtEndOfBlockComment() {
        return canConsumeThisString(BLOCK_COMMENT_END, true);
    }

    private void skipBlockComment() {
        while (!input.current().isEndOfInput()) {
            if (isAtEndOfBlockComment()) {
                return;
            }
            input.consume();
        }
        throw new ParseException(input.current(), "Premature end of block comment");
    }

    private boolean canConsumeThisString(final String string, final boolean consume) {
        if (string == null) {
            return false;
        }
        for (int i = 0; i < string.length(); i++) {
            if (!input.next(i).is(string.charAt(i))) {
                return false;
            }
        }
        if (consume) {
            input.consume(string.length());
        }
        return true;
    }

    public void skipCharacters(final int amount) {
        input.skipCharacters(amount);
        itemBuffer.clear();
    }

    public void setPostition(final IPosition position) {
        input.setPosition(position);
        clear();
    }

    @Override
    public void clear() {
        super.clear();
        input.clear();
    }

    public IPosition getPosition() {
        return input.getPosition();
    }

}
