package de.invesdwin.util.math.expression.tokenizer;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;

@Immutable
public final class ExpressionContextUtil {

    private ExpressionContextUtil() {}

    //CHECKSTYLE:OFF
    public static String collectContext(final Tokenizer tokenizer, final String originalExpression,
            final boolean ofContext) {
        //CHECKSTYLE:ON

        final IPosition startPosition = tokenizer.current();

        final StringBuilder context = new StringBuilder();
        boolean consumeMore = true;
        OUTER: while (consumeMore) {
            consumeMore = false;
            final Token contextToken = tokenizer.current();

            if (contextToken.isEnd()) {
                break;
            }

            final String source = contextToken.getSource();

            if (source.length() == 1) {
                final char contextCharacter = source.charAt(0);
                if (contextCharacter == ')') {
                    //stop looking, we are inside parameters that now get closed
                    break OUTER;
                } else if (contextCharacter == ':' || contextCharacter == '@') {
                    consumeMore = true;
                }
            }

            if (!consumeMore) {
                int voteConsumeMoreCharacters = 0;
                for (int i = 0; i < source.length(); i++) {
                    final char contextCharacter = source.charAt(i);
                    if (isContextEscapedCharacter(contextCharacter)) {
                        voteConsumeMoreCharacters++;
                    } else {
                        break;
                    }
                }
                if (voteConsumeMoreCharacters == source.length()) {
                    consumeMore = true;
                }
            }
            tokenizer.consume();

            final int start = contextToken.getIndexOffset();
            int end = start + contextToken.getLength();
            int skipCharacters = -1;
            int skipBracketClose = -1;
            while (true) {
                if (originalExpression.length() <= end) {
                    break;
                }
                final char endCharacter = originalExpression.charAt(end);
                if (endCharacter == '[') {
                    skipBracketClose++;
                } else if (endCharacter == ']' && skipBracketClose > -1) {
                    skipBracketClose--;
                    skipCharacters++;
                    end++;
                }
                if (skipBracketClose >= 0) {
                    skipCharacters++;
                    end++;
                } else {
                    break;
                }
            }

            if (!consumeMore) {
                final int next = end;
                if (originalExpression.length() > next) {
                    final char nextCharacter = originalExpression.charAt(next);
                    if (nextCharacter == '@' || nextCharacter == ':') {
                        consumeMore = true;
                        skipCharacters++;
                        end++;
                    }
                }
            }

            if (!consumeMore) {
                boolean checkMoreEscapeCharacters = true;
                while (checkMoreEscapeCharacters) {
                    final int next = end;
                    if (originalExpression.length() > next) {
                        final char nextCharacter = originalExpression.charAt(next);
                        if (isContextEscapedCharacter(nextCharacter)) {
                            consumeMore = true;
                            end++;
                            skipCharacters++;
                        } else {
                            checkMoreEscapeCharacters = false;
                        }
                    } else {
                        checkMoreEscapeCharacters = false;
                    }
                }
            }
            final boolean endsWithComment = maybeSkipContextCharacters(tokenizer, originalExpression, ofContext,
                    context, consumeMore, start, end, skipCharacters);
            if (endsWithComment) {
                consumeMore = true;
            }
        }

        maybeUnskipContextCharacters(tokenizer, originalExpression, ofContext, context, startPosition);

        return context.toString();
    }

    /**
     * Fixes parameters for functions that get mixed with timeseries contexts that contain special characters
     */
    private static void maybeUnskipContextCharacters(final Tokenizer tokenizer, final String originalExpression,
            final boolean ofContext, final StringBuilder context, final IPosition startPosition) {
        if (ofContext) {
            //no need for unwind here
            return;
        }
        //-1 becomes 0
        final int firstIndexAfterContext = context.lastIndexOf(":") + 1;
        final int firstIndexOfContextEscapedCharacter = getFirstIndexOfContextEscapedCharacter(context,
                firstIndexAfterContext);
        if (firstIndexOfContextEscapedCharacter > 0) {
            tokenizer.setPostition(startPosition);
            tokenizer.skipCharacters(firstIndexOfContextEscapedCharacter - 1);
            context.setLength(firstIndexOfContextEscapedCharacter);
        }
    }

    private static int getFirstIndexOfContextEscapedCharacter(final StringBuilder context, final int startIndex) {
        for (int i = startIndex; i < context.length(); i++) {
            final char character = context.charAt(i);
            if (isContextEscapedCharacter(character)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean maybeSkipContextCharacters(final Tokenizer tokenizer, final String originalExpression,
            final boolean ofContext, final StringBuilder context, final boolean consumeMore, final int start,
            final int end, final int skipCharacters) {
        int usedSkipCharacters = skipCharacters;

        boolean endsWithComment = false;
        final String substring = originalExpression.substring(start, end);
        if (substring.endsWith("//")) {
            endsWithComment = true;
            final String substringWithoutComment = Strings.removeEnd(substring, 2);
            context.append(substringWithoutComment);
            final int commentEnd = originalExpression.indexOf('\n', end + 1);
            if (commentEnd >= 0) {
                usedSkipCharacters += commentEnd - end + 1;
            } else {
                usedSkipCharacters += originalExpression.length() - end + 1;
            }
        } else if (substring.endsWith("/*")) {
            endsWithComment = true;
            final String substringWithoutComment = Strings.removeEnd(substring, 2);
            context.append(substringWithoutComment);
            final int commentEnd = originalExpression.indexOf("*/", end + 1);
            if (commentEnd >= 0) {
                usedSkipCharacters += commentEnd - end + 2;
            }
        } else {
            context.append(substring);
        }

        if (!ofContext && !consumeMore && Strings.endsWith(context, "]")) {
            final int lastIndexOf = context.lastIndexOf("[");
            if (lastIndexOf >= 0) {
                usedSkipCharacters -= context.length() - lastIndexOf + 1;
                context.setLength(lastIndexOf);
            }
        }
        if (usedSkipCharacters >= 0) {
            tokenizer.skipCharacters(usedSkipCharacters);
        }
        return endsWithComment;
    }

    private static boolean isContextEscapedCharacter(final char character) {
        switch (character) {
        case '.':
        case ',':
        case '+':
        case '-':
        case '^':
        case '*':
        case '/':
        case '\\':
        case ';':
        case '!':
        case '§':
        case '$':
        case '%':
        case '&':
        case '{':
        case '}':
        case '?':
        case '#':
        case '~':
        case '¸':
        case '´':
        case '|':
        case '<':
        case '>':
        case '=':
        case '€':
        case 'ß':
            return true;
        default:
            return false;
        }
    }

    public static void putContext(final String context, final StringBuilder sb) {
        if (context != null) {
            sb.append(context);
            sb.append(":");
        }
    }

}
