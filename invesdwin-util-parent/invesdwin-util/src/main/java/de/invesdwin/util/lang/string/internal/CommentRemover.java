package de.invesdwin.util.lang.string.internal;

import java.util.Scanner;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;

/**
 * https://stackoverflow.com/a/41613171
 */
@Immutable
public final class CommentRemover {

    private CommentRemover() {
    }

    public static String removeComments(final String code) {
        State state = State.outsideComment;
        final StringBuilder result = new StringBuilder();
        final Scanner s = new Scanner(code);
        s.useDelimiter("");
        while (s.hasNext()) {
            final String c = s.next();
            switch (state) {
            case outsideComment:
                if ("/".equals(c) && s.hasNext()) {
                    final String c2 = s.next();
                    if ("/".equals(c2)) {
                        state = State.insideLineComment;
                    } else if ("*".equals(c2)) {
                        state = State.insideblockComment_noNewLineYet;
                    } else {
                        result.append(c).append(c2);
                    }
                } else {
                    result.append(c);
                    if ("\"".equals(c)) {
                        state = State.insideString;
                    }
                }
                break;
            case insideString:
                result.append(c);
                if ("\"".equals(c)) {
                    state = State.outsideComment;
                } else if ("\\".equals(c) && s.hasNext()) {
                    result.append(s.next());
                }
                break;
            case insideLineComment:
                if ("\n".equals(c)) {
                    state = State.outsideComment;
                    result.append("\n");
                }
                break;
            case insideblockComment_noNewLineYet:
                if ("\n".equals(c)) {
                    result.append("\n");
                    state = State.insideblockComment;
                }
            case insideblockComment:
                while ("*".equals(c) && s.hasNext()) {
                    final String c2 = s.next();
                    if ("/".equals(c2)) {
                        state = State.outsideComment;
                        break;
                    }
                }
            default:
                throw UnknownArgumentException.newInstance(State.class, state);
            }
        }
        s.close();
        return result.toString();
    }

    private enum State {
        outsideComment,
        insideLineComment,
        insideblockComment,
        insideblockComment_noNewLineYet,
        insideString
    };

}
