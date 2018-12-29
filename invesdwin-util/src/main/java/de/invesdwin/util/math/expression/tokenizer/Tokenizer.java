package de.invesdwin.util.math.expression.tokenizer;

import java.io.Reader;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class Tokenizer extends ALookahead<Token> {

    private static final char DECIMAL_SEPARATOR = '.';
    private static final char GROUPING_SEPARATOR = '_';
    private static final char[] BRACKETS = { '(', '[', '{', '}', ']', ')' };
    private static final boolean TREAT_SINGLE_PIPE_AS_BRACKET = true;
    private static final Map<Character, Character> STRING_DELIMITERS = new IdentityHashMap<Character, Character>() {
        {
            put('"', '\\');
            put('\'', '\0');
        }
    };

    protected final LookaheadReader input;

    public Tokenizer() {
        this.input = new LookaheadReader();
    }

    public void init(final Reader input) {
        super.init();
        this.input.init(input);
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

        // End of input reached? Pass end of input signal on...
        if (input.current().isEndOfInput()) {
            return null;
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

        throw new RuntimeException(ParseError
                .error(input.current(),
                        String.format("Invalid character in input: '%s'", input.current().getStringValue()))
                .toString());
    }

    protected boolean isAtStartOfNumber() {
        //CHECKSTYLE:OFF
        return input.current().isDigit() || input.current().is('-') && input.next().isDigit()
                || input.current().is('-') && input.next().is('.') && input.next(2).isDigit()
                || input.current().is('.') && input.next().isDigit();
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
                    throw new RuntimeException(ParseError.error(input.next(),
                            String.format("Cannot use '%s' as escaped character", input.next().getStringValue()))
                            .toString());
                }
            } else {
                result.addToContent(input.consume());
            }
        }
        if (input.current().is(separator)) {
            result.addToSource(input.consume());
        } else {
            throw new RuntimeException(
                    ParseError.error(input.current(), "Premature end of string constant").toString());
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
        return input.current().isLetter();
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
        return current.isDigit() || current.isLetter() || current.is('_', ':');
    }

    private Token fetchSymbol() {
        final Token result = Token.create(Token.TokenType.SYMBOL, input.current());
        result.addToTrigger(input.consume());
        //CHECKSTYLE:OFF
        if (result.isSymbol("*") && input.current().is('*') || result.isSymbol("&") && input.current().is('&')
                || result.isSymbol("|") && input.current().is('|') || result.isSymbol() && input.current().is('=')) {
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
                || (input.current().is(GROUPING_SEPARATOR) && input.next().isDigit())) {
            if (input.current().is(GROUPING_SEPARATOR)) {
                result.addToSource(input.consume());
            } else if (input.current().is(DECIMAL_SEPARATOR)) {
                if (result.is(Token.TokenType.DECIMAL)) {
                    throw new RuntimeException(
                            ParseError.error(input.current(), "Unexpected decimal separators").toString());
                } else {
                    final Token decimalToken = Token.create(Token.TokenType.DECIMAL, result);
                    decimalToken.setContent(result.getContents() + DECIMAL_SEPARATOR);
                    decimalToken.setSource(result.getSource());
                    result = decimalToken;
                }
                result.addToSource(input.consume());
            } else {
                result.addToContent(input.consume());
            }
        }

        return result;
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
        if (current().matches(Token.TokenType.SYMBOL, symbol)) {
            consume();
        } else {
            throw new RuntimeException(
                    ParseError
                            .error(current(),
                                    String.format("Unexpected token: '%s'. Expected: '%s'", current().getSource(),
                                            symbol))
                            .toString());
        }
    }

}
