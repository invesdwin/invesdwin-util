package de.invesdwin.util.time.date.format;

import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.invesdwin.norva.beanpath.annotation.Hidden;
import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import jakarta.persistence.Transient;

@Immutable
public class SerializableFDateTimeFormatterProvider implements ISerializableValueObject, IFDateTimeFormatterProvider {

    private final String pattern;
    private final String timeZoneId;
    private final Locale locale;
    @Hidden(skip = true)
    @Transient
    @JsonIgnore
    private transient FDateTimeFormatter formatter;

    public SerializableFDateTimeFormatterProvider(final FDateTimeFormatter formatter) {
        this.formatter = formatter;
        this.pattern = formatter.getPattern();
        final FTimeZone timeZone = formatter.getTimeZone();
        if (timeZone != null) {
            this.timeZoneId = timeZone.getId();
        } else {
            this.timeZoneId = null;
        }
        this.locale = formatter.getLocale();
    }

    @Override
    public FDateTimeFormatter asFDateTimeFormatter() {
        if (formatter == null) {
            formatter = FDateTimeFormatter.forPattern(pattern).withTimeZoneId(timeZoneId).withLocale(locale);
        }
        return formatter;
    }

}
