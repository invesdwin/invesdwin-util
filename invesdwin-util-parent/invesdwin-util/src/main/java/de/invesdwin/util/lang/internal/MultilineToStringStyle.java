package de.invesdwin.util.lang.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.SystemUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ThreadSafe
public class MultilineToStringStyle extends DefaultToStringStyle {

    public static final MultilineToStringStyle INSTANCE = new MultilineToStringStyle();
    public static final String INDENT = " _ ";

    private static final long serialVersionUID = 1L;
    private final String initialArrayStart = getArrayStart();
    private final String initialArrayEnd = getArrayEnd();
    private final String initialContentStart = getContentStart();
    private final String initialContentEnd = getContentEnd();

    protected MultilineToStringStyle() {
        super();
        setFieldNameValueSeparator("=");
        setFieldSeparator(",");
        setArrayContentDetail(true);
        setFieldNameValueSeparator(" " + getFieldNameValueSeparator() + " ");
        setArrayStart(initialArrayStart + SystemUtils.LINE_SEPARATOR + INDENT);
        setArraySeparator(getArraySeparator() + SystemUtils.LINE_SEPARATOR + INDENT);
        setArrayEnd(SystemUtils.LINE_SEPARATOR + initialArrayEnd);
        setContentStart(initialContentStart + SystemUtils.LINE_SEPARATOR + INDENT);
        setFieldSeparator(getFieldSeparator() + SystemUtils.LINE_SEPARATOR + INDENT);
        setContentEnd(SystemUtils.LINE_SEPARATOR + initialContentEnd);
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final char[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final double[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final float[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final int[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final long[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final short[] array) {
        appendSummarySize(buffer, fieldName, array, array.length);
        final StringBuffer sb = new StringBuffer();
        if (array.length > 0) {
            super.appendDetail(sb, fieldName, array);
        } else {
            sb.append(initialArrayStart);
            sb.append(initialArrayEnd);
        }
        buffer.append(indentDetail(sb.toString()));
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Map map) {
        appendSummarySize(buffer, fieldName, map, map.size());
        final Iterator<Entry> i = map.entrySet().iterator();
        if (i.hasNext()) {
            final StringBuffer sb = new StringBuffer();
            super.appendDetail(sb, fieldName, map);
            buffer.append(indentDetail(sb.toString()));
        } else {
            buffer.append(initialArrayStart);
            buffer.append(initialArrayEnd);
        }
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Collection coll) {
        appendSummarySize(buffer, fieldName, coll, coll.size());
        if (coll.size() > 0) {
            final StringBuffer sb = new StringBuffer();
            super.appendDetail(sb, fieldName, coll);
            buffer.append(indentDetail(sb.toString()));
        } else {
            buffer.append(initialArrayStart);
            buffer.append(initialArrayEnd);
        }
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
        if (value == null || value.getClass().getName().startsWith("java.")) {
            super.appendDetail(buffer, fieldName, value);
        } else {
            super.appendDetail(buffer, fieldName,
                    indentDetail(ExtendedReflectionToStringBuilder.toString(value, this)));
        }
    }

    private void appendSummarySize(final StringBuffer buffer, final String fieldName, final Object obj,
            final int size) {
        appendClassName(buffer, obj);
        appendIdentityHashCode(buffer, obj);
        appendSummarySize(buffer, fieldName, size);
    }

    private String indentDetail(final String detail) {
        String indent = detail.replaceAll(
                "(?m)\\" + initialContentStart + "[\\s\\Q" + INDENT + "\\E]+\\" + initialContentEnd,
                initialContentStart + initialContentEnd);
        indent = indent.replace("\n", "\n" + INDENT);
        return indent;
    }
}