package de.invesdwin.util.marshallers.jaxb;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class DatatypeConverterTest {

    @Test
    public void testDateTime() {
        final FDate date1 = new FDate();
        final String str1 = DatatypeConverter.printDateTime(date1);
        final FDate date2 = DatatypeConverter.parseDateTime(str1);
        final String str2 = DatatypeConverter.printDateTime(date2);
        Assertions.assertThat(date2).isEqualTo(date1);
        Assertions.assertThat(str2).isEqualTo(str1);
    }

}
