package de.invesdwin.util.lang.string.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.builder.ToStringStyle;

import de.invesdwin.util.lang.Objects;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ThreadSafe
public class DefaultToStringStyle extends ToStringStyle {

    public static final DefaultToStringStyle INSTANCE = new DefaultToStringStyle();

    private static final long serialVersionUID = 1L;

    protected DefaultToStringStyle() {
        setFieldNameValueSeparator(":");
        setFieldSeparator("|");
        setUseShortClassName(true);
        setUseIdentityHashCode(false);
    }

    @Override
    public void appendStart(final StringBuffer buffer, final Object object) {
        if (object != null) {
            boolean printContent = true;
            if (object instanceof Object[]) {
                final Object[] content = (Object[]) object;
                appendDetail(buffer, "", content);
            } else if (object instanceof Map) {
                final Map content = (Map) object;
                appendDetail(buffer, "", content);
            } else if (object instanceof Collection) {
                final Collection content = (Collection) object;
                appendDetail(buffer, "", content);
            } else {
                printContent = false;
                super.appendStart(buffer, object);
            }
            if (printContent) {
                appendContentStart(buffer);
                if (isFieldSeparatorAtStart()) {
                    appendFieldSeparator(buffer);
                }
            }
        }
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Map map) {
        final Iterator<Entry> i = map.entrySet().iterator();
        buffer.append(getArrayStart());
        if (!i.hasNext()) {
            buffer.append(getArrayEnd());
        } else {
            while (i.hasNext()) {
                final Entry e = i.next();
                final Object key = e.getKey();
                final Object value = e.getValue();
                if (key == map) {
                    buffer.append("(this Map)");
                } else {
                    appendDetail(buffer, fieldName, key);
                }
                buffer.append('=');
                if (value == map) {
                    buffer.append("(this Map)");
                } else {
                    appendDetail(buffer, fieldName, value);
                }
                if (!i.hasNext()) {
                    buffer.append(getArrayEnd());
                    break;
                }
                buffer.append(getArraySeparator());
            }
        }
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Collection coll) {
        buffer.append(getArrayStart());
        int i = 0;
        for (final Object item : coll) {
            if (i > 0) {
                buffer.append(getArraySeparator());
            }
            if (item == null) {
                appendNullText(buffer, fieldName);
            } else {
                appendInternal(buffer, fieldName, item, isArrayContentDetail());
            }
            i++;
        }
        buffer.append(getArrayEnd());
    }

    @Override
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
        if (value instanceof BigInteger || value instanceof BigDecimal) {
            super.appendDetail(buffer, fieldName, value.toString());
        } else if (value instanceof Date || value instanceof Calendar) {
            super.appendDetail(buffer, fieldName,
                    org.apache.commons.lang3.time.FastDateFormat
                            .getDateTimeInstance(org.apache.commons.lang3.time.FastDateFormat.FULL,
                                    org.apache.commons.lang3.time.FastDateFormat.LONG)
                            .format(value));
        } else {
            super.appendDetail(buffer, fieldName, value);
        }
    }

    @Override
    protected void appendFieldStart(final StringBuffer buffer, final String fieldName) {
        if (Objects.REFLECTION_EXCLUDED_FIELDS.contains(fieldName)) {
            throw new IllegalArgumentException("Printing of this fieldName should have been prevented: " + fieldName);
        }
        super.appendFieldStart(buffer, fieldName);
    }

    @Override
    public void append(final StringBuffer buffer, final String fieldName, final Object value,
            final Boolean fullDetail) {
        if (Objects.REFLECTION_EXCLUDED_FIELDS.contains(fieldName)) {
            return;
        }
        super.append(buffer, fieldName, value, fullDetail);
    }

    @Override
    protected void appendInternal(final StringBuffer buffer, final String fieldName, final Object value,
            final boolean detail) {
        try {
            super.appendInternal(buffer, fieldName, value, detail);
        } catch (final Throwable t) {
            super.appendInternal(buffer, fieldName, "<!" + t.toString() + "!>", detail);
        }
    }

    /**
     * Overriden for making it public
     */
    @Override
    public String getNullText() {
        return super.getNullText();
    }

}
