package de.invesdwin.util.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
public class StringsTest {

    @Test
    public void testRemoveEnd() {
        Assertions.assertThat(Strings.removeEnd(new StringBuilder("asdasd"), "asd").toString()).isEqualTo("asd");
        Assertions.assertThat(Strings.removeEnd(new StringBuilder("asdasd"), "d").toString()).isEqualTo("asdas");
        Assertions.assertThat(Strings.removeEnd(new StringBuilder("asdasd"), "").toString()).isEqualTo("asdasd");
        Assertions.assertThat(Strings.removeEnd(new StringBuilder("asdasd"), "f").toString()).isEqualTo("asdasd");
        Assertions.assertThat(Strings.removeEnd(new StringBuilder("asdasd"), "asdasda").toString()).isEqualTo("asdasd");
        Assertions.assertThat(Strings.removeEnd(new StringBuilder("asdasd"), "asd".length()).toString())
                .isEqualTo("asd");
        Assertions.assertThat(Strings.removeEnd("asdasd", "asd").toString()).isEqualTo("asd");
        Assertions.assertThat(Strings.removeEnd("asdasd", "asd".length()).toString()).isEqualTo("asd");
        Assertions.assertThat(Strings.removeEnd("asdasd", 1).toString()).isEqualTo("asdas");
        Assertions.assertThat(Strings.removeEnd("", 1).toString()).isEqualTo("");
        Assertions.assertThat(Strings.removeEnd(new StringBuilder(""), 1).toString()).isEqualTo("");
        Assertions.assertThat(Strings.removeEnd(new StringBuilder("asd"), 5).toString()).isEqualTo("asd");
    }

    @Test
    public void testStripBlankLines() {
        final String sWithEmptyLines = "\nasd\n\n\n  \n\t\nasd\n";
        final String sWithoutEmptyLines = Strings.stripBlankLines(sWithEmptyLines);
        Assertions.assertThat("asd\nasd").isEqualTo(sWithoutEmptyLines);
    }

    @Test
    public void testCountLines() {
        String sMultipleLines = "asd\nasd" + "\nsadasd" + "\nasd";
        Assertions.assertThat(Strings.countLines(sMultipleLines)).isEqualTo(4);
        sMultipleLines = "asd\nasd" + "\nsadasd" + "\n";
        Assertions.assertThat(Strings.countLines(sMultipleLines)).isEqualTo(4);
    }

    @Test
    public void testAsStringReflectiveMultiline() {
        System.out.println(String.format(Objects.toStringMultiline(new PrettyToStringVO()))); //SUPPRESS CHECKSTYLE single line
        System.out.println(String.format(Strings.asStringReflectiveMultiline(new ArrayList<Integer>(Arrays.asList(1, 2, //SUPPRESS CHECKSTYLE single line
                3, 4, 5)))));
    }

    @Test
    public void testAsStringReflective() {
        //CHECKSTYLE:OFF
        System.out.println(String.format(new PrettyToStringVO().toString()));
        System.out.println(
                String.format(Strings.asStringReflective(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)))));
        //CHECKSTYLE:ON
    }

    @Test
    public void testToStringHelperMultiline() {
        System.out.println(String.format(new PrettyToStringVO().toStringHelperMultiline())); //SUPPRESS CHECKSTYLE single line
    }

    @Test
    public void testToStringHelper() {
        System.out.println(String.format(new PrettyToStringVO().toStringHelper())); //SUPPRESS CHECKSTYLE single line
    }

    @Test
    public void testAsStringIdentity() {
        //CHECKSTYLE:OFF
        System.out.println(String.format(new Object().toString()));
        System.out.println(String.format(Strings.asStringIdentity(new PrettyToStringVO())));
        System.out
                .println(String.format(Strings.asStringIdentity(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)))));
        //CHECKSTYLE:ON
    }

    @Test
    public void testReplaceRange() {
        Assertions.checkEquals("aadf", Strings.replaceRange("asdf", 1, 2, "a"));
    }

    @SuppressWarnings("unused")
    public static class PrettyToStringVO {
        private static final long serialVersionUID = 1L;

        private final int number = 1;
        private final String string = "bla";
        private final String[] array = { "1", "2", "3" };
        private PrettyToStringVO object;
        private List<PrettyToStringVO> collection;
        private Map<Object, PrettyToStringVO> map;
        private Object nulll;
        private final FDate date = new FDate();
        private final transient boolean someInvisibleState = true;

        public PrettyToStringVO() {
            this(1);
        }

        public PrettyToStringVO(final int level) {
            final int maxEbene = 3;
            if (level < maxEbene) {
                object = new PrettyToStringVO(maxEbene);
                collection = Arrays.asList(new PrettyToStringVO(level + 2), new PrettyToStringVO(level + 1),
                        new PrettyToStringVO(level + 1));
                map = new HashMap<Object, PrettyToStringVO>();
                map.put("1", new PrettyToStringVO(level + 2));
                map.put("2", new PrettyToStringVO(level + 2));
                map.put(new PrettyToStringVO(level + 2), new PrettyToStringVO(level + 2));
            } else {
                object = this;
                collection = new ArrayList<PrettyToStringVO>();
                map = new HashMap<Object, PrettyToStringVO>();
            }
        }

        public String toStringHelperMultiline() {
            return internalToStringHelper(Objects.toStringHelperMultiline(this))
                    .with(internalToStringHelper(Objects.toStringHelperMultiline(object)))
                    .with(internalToStringHelper(Objects.toStringHelperMultiline(object)))
                    .toString();
        }

        public String toStringHelper() {
            return internalToStringHelper(Objects.toStringHelper(this))
                    .with(internalToStringHelper(Objects.toStringHelper(object)))
                    .with(internalToStringHelper(Objects.toStringHelper(object)))
                    .toString();
        }

        private ToStringHelper internalToStringHelper(final ToStringHelper helper) {
            //            private final int number = 1;
            return helper.add("number", number)
                    //            private final String string = "bla";
                    .add("string", string)
                    //            private final String[] array = { "1", "2", "3" };
                    .add("array", array)
                    //            private PrettyToStringVO object;
                    .add("object", object)
                    //            private List<PrettyToStringVO> collection;
                    .add("collection", collection)
                    //            private Map<Object, PrettyToStringVO> map;
                    .add("map", map)
                    //            private Object nulll;
                    .add("nulll", nulll)
                    //            private final FDate date = new FDate();
                    .add("date", date)
                    //            private final transient boolean someInvisibleState = true;
                    .add("someInvisibleState", someInvisibleState);
        }
    }

}
