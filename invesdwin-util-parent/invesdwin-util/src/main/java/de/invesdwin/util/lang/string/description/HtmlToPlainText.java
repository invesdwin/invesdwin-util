package de.invesdwin.util.lang.string.description;

import javax.annotation.concurrent.Immutable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

/**
 * HTML to plain-text. This example program demonstrates the use of jsoup to convert HTML input to lightly-formatted
 * plain-text. That is divergent from the general goal of jsoup's .text() methods, which is to get clean data from a
 * scrape.
 * <p>
 * Note that this is a fairly simplistic formatter -- for real world use you'll want to embrace and extend.
 * </p>
 * <p>
 * To invoke from the command line, assuming you've downloaded the jsoup jar to your current directory:
 * </p>
 * <p>
 * <code>java -cp jsoup.jar org.jsoup.examples.HtmlToPlainText url [selector]</code>
 * </p>
 * where <i>url</i> is the URL to fetch, and <i>selector</i> is an optional CSS selector.
 * 
 * @author Jonathan Hedley, jonathan@hedley.net
 */
@Immutable
public final class HtmlToPlainText {

    private HtmlToPlainText() {}

    public static String htmlToPlainText(final String html) {
        return htmlToPlainText(Jsoup.parse(html));
    }

    /**
     * Format an Element to plain-text
     * 
     * @param html
     *            the root element to format
     * @return formatted text
     */
    public static String htmlToPlainText(final Element html) {
        final FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor.traverse(formatter, html); // walk the DOM, and call .head() and .tail() for each node

        return formatter.toString();
    }

    // the formatting rules, implemented in a breadth-first DOM traverse
    private static final class FormattingVisitor implements NodeVisitor {
        private static final int MAX_WIDTH = 80;
        private int width = 0;
        private final StringBuilder accum = new StringBuilder(); // holds the accumulated text

        // hit when the node is first seen
        @Override
        public void head(final Node node, final int depth) {
            final String name = node.nodeName();
            if (node instanceof TextNode) {
                append(((TextNode) node).text()); // TextNodes carry all user-readable text in the DOM.
            } else if ("li".equals(name)) {
                append("\n * ");
            } else if ("dt".equals(name)) {
                append("  ");
            } else if (org.jsoup.internal.StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) {
                append("\n");
            }
        }

        // hit when all of the node's children (if any) have been visited
        @Override
        public void tail(final Node node, final int depth) {
            final String name = node.nodeName();
            if (org.jsoup.internal.StringUtil.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")) {
                append("\n");
            } else if ("a".equals(name)) {
                append(TextDescription.format(" <%s>", node.absUrl("href")));
            }
        }

        // appends text to the string builder with a simple word wrap method
        private void append(final String text) {
            if (text.startsWith("\n")) {
                width = 0; // reset counter if starts with a newline. only from formats above, not in natural text
            }
            if (" ".equals(text) && (accum.length() == 0
                    || org.jsoup.internal.StringUtil.in(accum.substring(accum.length() - 1), " ", "\n"))) {
                return; // don't accumulate long runs of empty spaces
            }

            if (text.length() + width > MAX_WIDTH) { // won't fit, needs to wrap
                final String[] words = text.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    final boolean last = i == words.length - 1;
                    if (!last) {
                        word = word + " ";
                    }
                    if (word.length() + width > MAX_WIDTH) { // wrap and reset counter
                        accum.append("\n").append(word);
                        width = word.length();
                    } else {
                        accum.append(word);
                        width += word.length();
                    }
                }
            } else { // fits as is, without need to wrap text
                accum.append(text);
                width += text.length();
            }
        }

        @Override
        public String toString() {
            return accum.toString();
        }
    }

}
