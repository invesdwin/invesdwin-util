package de.invesdwin.util.lang;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.internal.MultilineToStringStyle;

@NotThreadSafe
public final class ToStringHelper {
    private static final String INDENT = MultilineToStringStyle.INDENT;
    private final StringBuilder builder;
    private boolean needsSeparator = false;
    private final List<String> withObjStrs;
    private boolean bracketAdded;
    private final boolean multiline;

    ToStringHelper(final Object self, final boolean multiline) {
        this(self.getClass(), multiline);
    }

    ToStringHelper(final Class<?> clazz, final boolean multiline) {
        this(clazz.getSimpleName(), multiline);
    }

    ToStringHelper(final String className, final boolean multiline) {
        org.assertj.core.api.Assertions.assertThat(className).isNotNull();
        this.builder = new StringBuilder(32).append(className);
        this.withObjStrs = new ArrayList<String>();
        this.multiline = multiline;
    }

    public ToStringHelper add(final String name, @Nullable final Object value) {
        checkNameAndAppend(name).append(toString(value));
        return this;
    }

    public ToStringHelper add(final String name, final boolean value) {
        checkNameAndAppend(name).append(value);
        return this;
    }

    public ToStringHelper add(final String name, final char value) {
        checkNameAndAppend(name).append(value);
        return this;
    }

    public ToStringHelper add(final String name, final double value) {
        checkNameAndAppend(name).append(value);
        return this;
    }

    public ToStringHelper add(final String name, final float value) {
        checkNameAndAppend(name).append(value);
        return this;
    }

    public ToStringHelper add(final String name, final int value) {
        checkNameAndAppend(name).append(value);
        return this;
    }

    public ToStringHelper add(final String name, final long value) {
        checkNameAndAppend(name).append(value);
        return this;
    }

    public ToStringHelper addValue(@Nullable final Object value) {
        maybeAppendSeparator().append(toString(value));
        return this;
    }

    public ToStringHelper addValue(final boolean value) {
        maybeAppendSeparator().append(value);
        return this;
    }

    public ToStringHelper addValue(final char value) {
        maybeAppendSeparator().append(value);
        return this;
    }

    public ToStringHelper addValue(final double value) {
        maybeAppendSeparator().append(value);
        return this;
    }

    public ToStringHelper addValue(final float value) {
        maybeAppendSeparator().append(value);
        return this;
    }

    public ToStringHelper addValue(final int value) {
        maybeAppendSeparator().append(value);
        return this;
    }

    public ToStringHelper addValue(final long value) {
        maybeAppendSeparator().append(value);
        return this;
    }

    //CHECKSTYLE:OFF
    public ToStringHelper with(final Object... objs) {
        //CHECKSTYLE:ON
        for (final Object obj : objs) {
            if (obj != null) {
                final String string = obj.toString();
                this.withObjStrs.add(string);
            }
        }
        return this;
    }

    private StringBuilder checkNameAndAppend(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        final StringBuilder append = maybeAppendSeparator().append(name);
        final String colon;
        if (multiline) {
            colon = " = ";
        } else {
            colon = ":";
        }
        return append.append(colon);
    }

    private String toString(final Object value) {
        final String str = Strings.asStringNullText(value);
        if (multiline) {
            return Strings.replace(str, "\n", "\n" + INDENT);
        } else {
            return str;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(builder);
        if (bracketAdded) {
            if (multiline) {
                sb.append("\n");
            }
            sb.append(']');
        }
        if (!withObjStrs.isEmpty()) {
            boolean firstWith = true;
            for (final String withObjStr : withObjStrs) {
                if (firstWith) {
                    sb.append(" with {");
                    firstWith = false;
                } else {
                    sb.append(" and ");
                }
                if (multiline) {
                    sb.append("\n").append(INDENT);
                }
                if (multiline) {
                    sb.append(withObjStr.replace("\n", "\n" + INDENT));
                } else {
                    sb.append(withObjStr);
                }
            }
            if (multiline) {
                sb.append("\n");
            }
            sb.append("}");
        }
        return sb.toString();
    }

    public boolean isMultiline() {
        return multiline;
    }

    private StringBuilder maybeAppendSeparator() {
        if (!bracketAdded) {
            bracketAdded = true;
            builder.append('[');
            if (multiline) {
                builder.append("\n").append(INDENT);
            }
        }
        if (needsSeparator) {
            if (multiline) {
                return builder.append(",").append("\n").append(INDENT);
            } else {
                return builder.append("|");
            }
        } else {
            needsSeparator = true;
            return builder;
        }
    }
}