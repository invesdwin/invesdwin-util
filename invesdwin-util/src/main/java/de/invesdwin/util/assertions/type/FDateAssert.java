// CHECKSTYLE:OFF
package de.invesdwin.util.assertions.type;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.util.Lists.newArrayList;
import static org.assertj.core.util.Preconditions.checkNotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.internal.ComparatorBasedComparisonStrategy;
import org.assertj.core.util.DateUtil;
import org.assertj.core.util.VisibleForTesting;

import de.invesdwin.util.assertions.type.internal.FDatesAssertions;
import de.invesdwin.util.time.fdate.FDate;

/**
 * Base class for all implementations of assertions for {@link FDate}s.
 * <p/>
 * Note that assertions with date parameter comes with two flavor, one is obviously a {@link FDate} and the other is a
 * {@link String} representing a FDate.<br>
 * For the latter, the default format follows ISO 8901 : "yyyy-MM-dd", user can override it with a custom format by
 * calling {@link #withDateFormat(DateFormat)}.<br>
 * The user custom format will then be used for all next FDate assertions (i.e not limited to the current assertion) in
 * the test suite.<br>
 * To turn back to default format, simply call {@link #withDefaultDateFormatsOnly()}.
 *
 * @param <S>
 *            the "self" type of this assertion class. Please read "<a href="http://bit.ly/anMa4g"
 *            target="_blank">Emulating 'self types' using Java Generics to simplify fluent API implementation</a>" for
 *            more details.
 * @author Tomasz Nurkiewicz (thanks for giving assertions idea)
 * @author Joel Costigliola
 * @author Mikhail Mazursky
 * @author William Delanoue
 */
@Immutable
public class FDateAssert extends AbstractAssert<FDateAssert, FDate> {

    /**
     * the default DateFormat used to parse any String date representation.
     */
    private static final List<DateFormat> DEFAULT_DATE_FORMATS = newArrayList(DateUtil.newIsoDateTimeWithMsFormat(),
            DateUtil.newIsoDateTimeFormat(), DateUtil.newIsoDateFormat());

    private static final String DATE_FORMAT_PATTERN_SHOULD_NOT_BE_NULL = "Given date format pattern should not be null";
    private static final String DATE_FORMAT_SHOULD_NOT_BE_NULL = "Given date format should not be null";

    /**
     * Used in String based FDate assertions - like {@link #isAfter(String)} - to convert input date represented as
     * string to FDate.<br>
     * It keeps the instertion order so first format added will be first format used.
     */
    @VisibleForTesting
    static ThreadLocal<LinkedHashSet<DateFormat>> userDateFormats = new ThreadLocal<LinkedHashSet<DateFormat>>() {
        @Override
        protected LinkedHashSet<DateFormat> initialValue() {
            return new LinkedHashSet<DateFormat>();
        }
    };
    @VisibleForTesting
    FDatesAssertions dates = FDatesAssertions.instance();

    public FDateAssert(final FDate actual) {
        this(actual, FDateAssert.class);
    }

    protected FDateAssert(final FDate actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    /**
     * Same assertion as {@link AbstractAssert#isEqualTo(Object) isEqualTo(FDate date)} but given date is represented as
     * String either with one of the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isEqualTo(&quot;2002-12-18&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isEqualTo(&quot;2002-12-19&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualTo(final String dateAsString) {
        return isEqualTo(parse(dateAsString));
    }

    /**
     * Same assertion as {@link FDateAssert#isEqualToIgnoringHours(FDate)} but given FDate is represented as String
     * either with one of the default supported date format or user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // OK : all dates fields are the same up to minutes excluded
     * assertThat(&quot;2003-04-26T13:01:35&quot;).isEqualToIgnoringHours(&quot;2003-04-26T14:02:35&quot;);
     * 
     * // KO : fail as day fields differ
     * assertThat(&quot;2003-04-26T14:01:35&quot;).isEqualToIgnoringHours(&quot;2003-04-27T13:02:35&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal ignoring hours, minutes, seconds and
     *             milliseconds.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualToIgnoringHours(final String dateAsString) {
        return isEqualToIgnoringHours(parse(dateAsString));
    }

    /**
     * Same assertion as {@link AbstractAssert#isEqualTo(Object)} but given FDate is represented as String either with
     * one of the default supported date format or user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-04-26T13:01:35&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-04-26T14:01:00&quot;);
     * FDate date3 = parseFDatetime(&quot;2003-04-27T13:01:35&quot;);
     * 
     * // OK : all dates fields are the same up to hours excluded
     * assertThat(date1).isEqualToIgnoringHours(date2);
     * 
     * // KO : fail as day fields differ
     * assertThat(date1).isEqualToIgnoringHours(date3);
     * </pre>
     *
     * @param date
     *            the given FDate.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal ignoring hours, minutes, seconds and
     *             milliseconds.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualToIgnoringHours(final FDate date) {
        dates.assertIsEqualWithPrecision(info, actual, date, HOURS);
        return myself;
    }

    /**
     * Same assertion as {@link FDateAssert#isEqualToIgnoringMinutes(FDate)} but given FDate is represented as String
     * either with one of the default supported date format or user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * withDateFormat(&quot;yyyy-MM-dd'T'HH:mm:ss&quot;);
     * // OK : all dates fields are the same up to minutes excluded
     * assertThat(&quot;2003-04-26T13:01:35&quot;).isEqualToIgnoringMinutes(&quot;2003-04-26T13:02:35&quot;);
     * 
     * // KO : fail as hour fields differ
     * assertThat(&quot;2003-04-26T14:01:35&quot;).isEqualToIgnoringMinutes(&quot;2003-04-26T13:02:35&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal ignoring minutes, seconds and
     *             milliseconds.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualToIgnoringMinutes(final String dateAsString) {
        return isEqualToIgnoringMinutes(parse(dateAsString));
    }

    /**
     * Same assertion as {@link AbstractAssert#isEqualTo(Object)} but given FDate should not take care of minutes,
     * seconds and milliseconds precision.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-04-26T13:01:35&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-04-26T13:02:00&quot;);
     * FDate date3 = parseFDatetime(&quot;2003-04-26T14:02:00&quot;);
     * 
     * // OK : all dates fields are the same up to minutes excluded
     * assertThat(date1).isEqualToIgnoringMinutes(date2);
     * 
     * // KO : fail as hour fields differ
     * assertThat(date1).isEqualToIgnoringMinutes(date3);
     * </pre>
     *
     * @param date
     *            the given FDate.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal ignoring minutes, seconds and
     *             milliseconds.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualToIgnoringMinutes(final FDate date) {
        dates.assertIsEqualWithPrecision(info, actual, date, MINUTES);
        return myself;
    }

    /**
     * Same assertion as {@link FDateAssert#isEqualToIgnoringSeconds(FDate)} but given FDate is represented as String
     * either with one of the default supported date format or user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-04-26T13:01:35&quot;);
     * 
     * // OK : all dates fields are the same up to seconds excluded
     * assertThat(date1).isEqualToIgnoringSeconds(&quot;2003-04-26T13:01:57&quot;);
     * 
     * // KO : fail as minute fields differ
     * assertThat(date1).isEqualToIgnoringMinutes(&quot;2003-04-26T13:02:00&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal ignoring seconds and milliseconds.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualToIgnoringSeconds(final String dateAsString) {
        return isEqualToIgnoringSeconds(parse(dateAsString));
    }

    /**
     * Same assertion as {@link AbstractAssert#isEqualTo(Object)} but given FDate should not take care of seconds and
     * milliseconds precision.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-04-26T13:01:35&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-04-26T13:01:36&quot;);
     * FDate date3 = parseFDatetime(&quot;2003-04-26T14:02:00&quot;);
     * 
     * // OK : all dates fields are the same up to seconds excluded
     * assertThat(date1).isEqualToIgnoringSeconds(date2);
     * 
     * // KO : fail as minute fields differ
     * assertThat(date1).isEqualToIgnoringSeconds(date3);
     * </pre>
     *
     * @param date
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal ignoring seconds and milliseconds.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualToIgnoringSeconds(final FDate date) {
        dates.assertIsEqualWithPrecision(info, actual, date, SECONDS);
        return myself;
    }

    /**
     * Same assertion as {@link FDateAssert#isEqualToIgnoringMillis(FDate)} but given FDate is represented as String
     * either with one of the default supported date format or user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetimeWithMs(&quot;2003-04-26T13:01:35.998&quot;);
     * 
     * // OK : all dates fields are the same up to milliseconds excluded
     * assertThat().isEqualToIgnoringMillis(&quot;2003-04-26T13:01:35.997&quot;);
     * 
     * // KO : fail as seconds fields differ
     * assertThat(&quot;2003-04-26T13:01:35.998&quot;).isEqualToIgnoringMinutes(&quot;2003-04-26T13:01:36.998&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal ignoring milliseconds.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualToIgnoringMillis(final String dateAsString) {
        return isEqualToIgnoringMillis(parse(dateAsString));
    }

    /**
     * Same assertion as {@link AbstractAssert#isEqualTo(Object)} but given FDate should not take care of milliseconds
     * precision.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetimeAndMs(&quot;2003-04-26T13:01:35.001&quot;);
     * FDate date2 = parseFDatetimeAndMs(&quot;2003-04-26T13:01:35.002&quot;);
     * FDate date3 = parseFDatetimeAndMs(&quot;2003-04-26T14:01:36.001&quot;);
     * 
     * // OK : all dates fields are the same up to milliseconds excluded
     * assertThat(date1).isEqualToIgnoringMillis(date2);
     * 
     * // KO : fail as second fields differ
     * assertThat(date1).isEqualToIgnoringMillis(date3);
     * </pre>
     *
     * @param date
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not equal ignoring milliseconds.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isEqualToIgnoringMillis(final FDate date) {
        dates.assertIsEqualWithPrecision(info, actual, date, MILLISECONDS);
        return myself;
    }

    /**
     * Same assertion as {@link AbstractAssert#isNotEqualTo(Object) isNotEqualTo(FDate date)} but given date is
     * represented as String either with one of the supported defaults date format or a user custom date format (set
     * with method {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isNotEqualTo(&quot;2002-12-19&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isNotEqualTo(&quot;2002-12-18&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual and given FDate represented as String are equal.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isNotEqualTo(final String dateAsString) {
        return isNotEqualTo(parse(dateAsString));
    }

    /**
     * Same assertion as {@link Assert#isIn(Object...)}but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isIn(&quot;2002-12-17&quot;, &quot;2002-12-18&quot;, &quot;2002-12-19&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isIn(&quot;2002-12-17&quot;, &quot;2002-12-19&quot;, &quot;2002-12-20&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param datesAsString
     *            the given FDatesAssertions represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual is not in given FDatesAssertions represented as String.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isIn(final String... datesAsString) {
        final FDate[] dates = new FDate[datesAsString.length];
        for (int i = 0; i < datesAsString.length; i++) {
            dates[i] = parse(datesAsString[i]);
        }
        return isIn((Object[]) dates);
    }

    /**
     * Same assertion as {@link Assert#isIn(Iterable)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isInWithStringFDateCollection(
     *         Arrays.asList(&quot;2002-12-17&quot;, &quot;2002-12-18&quot;, &quot;2002-12-19&quot;));
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isInWithStringFDateCollection(
     *         Arrays.asList(&quot;2002-12-17&quot;, &quot;2002-12-19&quot;, &quot;2002-12-20&quot;));
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     * <p/>
     * Method signature could not be <code>isIn(Collection&lt;String&gt;)</code> because it would be same signature as
     * <code>isIn(Collection&lt;FDate&gt;)</code> since java collection type are erased at runtime.
     *
     * @param datesAsString
     *            the given FDatesAssertions represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual is not in given FDatesAssertions represented as String.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isInWithStringFDateCollection(final Collection<String> datesAsString) {
        final Collection<FDate> dates = new ArrayList<FDate>(datesAsString.size());
        for (final String dateAsString : datesAsString) {
            dates.add(parse(dateAsString));
        }
        return isIn(dates);
    }

    /**
     * Same assertion as {@link Assert#isNotIn(Object...)} but given date is represented as String either with one of
     * the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isNotIn(&quot;2002-12-17&quot;, &quot;2002-12-19&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isNotIn(&quot;2002-12-17&quot;, &quot;2002-12-18&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param datesAsString
     *            the given FDatesAssertions represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual is in given FDatesAssertions represented as String.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isNotIn(final String... datesAsString) {
        final FDate[] dates = new FDate[datesAsString.length];
        for (int i = 0; i < datesAsString.length; i++) {
            dates[i] = parse(datesAsString[i]);
        }
        return isNotIn((Object[]) dates);
    }

    /**
     * Same assertion as {@link Assert#isNotIn(Iterable)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isNotInWithStringFDateCollection(Arrays.asList(&quot;2002-12-17&quot;, &quot;2002-12-19&quot;));
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isNotInWithStringFDateCollection(Arrays.asList(&quot;2002-12-17&quot;, &quot;2002-12-18&quot;));
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     * Method signature could not be <code>isNotIn(Collection&lt;String&gt;)</code> because it would be same signature
     * as <code>isNotIn(Collection&lt;FDate&gt;)</code> since java collection type are erased at runtime.
     *
     * @param datesAsString
     *            the given FDatesAssertions represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if actual is in given FDatesAssertions represented as String.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isNotInWithStringFDateCollection(final Collection<String> datesAsString) {
        final Collection<FDate> dates = new ArrayList<FDate>(datesAsString.size());
        for (final String dateAsString : datesAsString) {
            dates.add(parse(dateAsString));
        }
        return isNotIn(dates);
    }

    /**
     * Verifies that the actual {@code FDate} is <b>strictly</b> before the given one.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isBefore(theReturnOfTheKing.getReleaseFDate());
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isBefore(theFellowshipOfTheRing.getReleaseFDate());
     * </pre>
     *
     * @param other
     *            the given FDate.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not strictly before the given one.
     */
    public FDateAssert isBefore(final FDate other) {
        dates.assertIsBefore(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isBefore(FDate)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isBefore(&quot;2002-12-19&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isBefore(&quot;2002-12-17&quot;);
     * assertThat(theTwoTowers.getReleaseFDate()).isBefore(&quot;2002-12-18&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if given date as String is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not strictly before the given FDate represented as String.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isBefore(final String dateAsString) {
        return isBefore(parse(dateAsString));
    }

    /**
     * Verifies that the actual {@code FDate} is before or equals to the given one.
     *
     * @param other
     *            the given FDate.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not before or equals to the given one.
     */
    public FDateAssert isBeforeOrEqualsTo(final FDate other) {
        dates.assertIsBeforeOrEqualsTo(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isBeforeOrEqualsTo(FDate)} but given date is represented as String either with one of
     * the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isBeforeOrEqualsTo(&quot;2002-12-19&quot;);
     * assertThat(theTwoTowers.getReleaseFDate()).isBeforeOrEqualsTo(&quot;2002-12-18&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isBeforeOrEqualsTo(&quot;2002-12-17&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if given date as String is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not before or equals to the given FDate represented as String.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isBeforeOrEqualsTo(final String dateAsString) {
        return isBeforeOrEqualsTo(parse(dateAsString));
    }

    /**
     * Verifies that the actual {@code FDate} is <b>strictly</b> after the given one.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isAfter(theFellowshipOfTheRing.getReleaseFDate());
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isAfter(theReturnOfTheKing.getReleaseFDate());
     * </pre>
     *
     * @param other
     *            the given FDate.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not strictly after the given one.
     */
    public FDateAssert isAfter(final FDate other) {
        dates.assertIsAfter(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isAfter(FDate)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isAfter(&quot;2002-12-17&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isAfter(&quot;2002-12-18&quot;);
     * assertThat(theTwoTowers.getReleaseFDate()).isAfter(&quot;2002-12-19&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if given date as String is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not strictly after the given FDate represented as String.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isAfter(final String dateAsString) {
        return isAfter(parse(dateAsString));
    }

    /**
     * Verifies that the actual {@code FDate} is after or equals to the given one.
     *
     * @param other
     *            the given FDate.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not after or equals to the given one.
     */
    public FDateAssert isAfterOrEqualsTo(final FDate other) {
        dates.assertIsAfterOrEqualsTo(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isAfterOrEqualsTo(FDate)} but given date is represented as String either with one of
     * the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)} ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isAfterOrEqualsTo(&quot;2002-12-17&quot;);
     * assertThat(theTwoTowers.getReleaseFDate()).isAfterOrEqualsTo(&quot;2002-12-18&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isAfterOrEqualsTo(&quot;2002-12-19&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if given date as String is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not after or equals to the given FDate represented as String.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isAfterOrEqualsTo(final String dateAsString) {
        return isAfterOrEqualsTo(parse(dateAsString));
    }

    /**
     * Verifies that the actual {@code FDate} is in [start, end[ period (start included, end excluded).
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isBetween(theFellowshipOfTheRing.getReleaseFDate(),
     *         theReturnOfTheKing.getReleaseFDate());
     * 
     * // assertion will fail
     * assertThat(theFellowshipOfTheRing.getReleaseFDate()).isBetween(theTwoTowers.getReleaseFDate(),
     *         theReturnOfTheKing.getReleaseFDate());
     * </pre>
     *
     * @param start
     *            the period start (inclusive), expected not to be null.
     * @param end
     *            the period end (exclusive), expected not to be null.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if start {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if end {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in [start, end[ period.
     */
    public FDateAssert isBetween(final FDate start, final FDate end) {
        return isBetween(start, end, true, false);
    }

    /**
     * Same assertion as {@link #isBetween(FDate, FDate)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isBetween(&quot;2002-12-17&quot;, &quot;2002-12-19&quot;);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isBetween(&quot;2002-12-15&quot;, &quot;2002-12-17&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param start
     *            the period start (inclusive), expected not to be null.
     * @param end
     *            the period end (exclusive), expected not to be null.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if start FDate as String is {@code null}.
     * @throws NullPointerException
     *             if end FDate as String is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in [start, end[ period.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isBetween(final String start, final String end) {
        return isBetween(parse(start), parse(end));
    }

    /**
     * Verifies that the actual {@code FDate} is in the given period defined by start and end dates.<br>
     * To include start in the period set inclusiveStart parameter to <code>true</code>.<br>
     * To include end in the period set inclusiveEnd parameter to <code>true</code>.<br>
     *
     * @param start
     *            the period start, expected not to be null.
     * @param end
     *            the period end, expected not to be null.
     * @param inclusiveStart
     *            whether to include start date in period.
     * @param inclusiveEnd
     *            whether to include end date in period.
     * @return this assertion object.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if start {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if end {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in (start, end) period.
     */
    public FDateAssert isBetween(final FDate start, final FDate end, final boolean inclusiveStart,
            final boolean inclusiveEnd) {
        dates.assertIsBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        return myself;
    }

    /**
     * Same assertion as {@link #isBetween(FDate, FDate, boolean, boolean)}but given date is represented as String
     * either with one of the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isBetween(&quot;2002-12-17&quot;, &quot;2002-12-18&quot;, false, true);
     * assertThat(theTwoTowers.getReleaseFDate()).isBetween(&quot;2002-12-18&quot;, &quot;2002-12-19&quot;, true, false);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isBetween(&quot;2002-12-17&quot;, &quot;2002-12-18&quot;, false, false);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param start
     *            the period start, expected not to be null.
     * @param end
     *            the period end, expected not to be null.
     * @param inclusiveStart
     *            whether to include start date in period.
     * @param inclusiveEnd
     *            whether to include end date in period.
     * @return this assertion object.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if start FDate as String is {@code null}.
     * @throws NullPointerException
     *             if end FDate as String is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in (start, end) period.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isBetween(final String start, final String end, final boolean inclusiveStart,
            final boolean inclusiveEnd) {
        dates.assertIsBetween(info, actual, parse(start), parse(end), inclusiveStart, inclusiveEnd);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} is not in the given period defined by start and end dates.<br>
     * To include start in the period set inclusiveStart parameter to <code>true</code>.<br>
     * To include end in the period set inclusiveEnd parameter to <code>true</code>.<br>
     *
     * @param start
     *            the period start (inclusive), expected not to be null.
     * @param end
     *            the period end (exclusive), expected not to be null.
     * @param inclusiveStart
     *            whether to include start date in period.
     * @param inclusiveEnd
     *            whether to include end date in period.
     * @return this assertion object.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if start {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if end {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in (start, end) period.
     */
    public FDateAssert isNotBetween(final FDate start, final FDate end, final boolean inclusiveStart,
            final boolean inclusiveEnd) {
        dates.assertIsNotBetween(info, actual, start, end, inclusiveStart, inclusiveEnd);
        return myself;
    }

    /**
     * Same assertion as {@link #isNotBetween(FDate, FDate, boolean, boolean)} but given date is represented as String
     * either with one of the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isNotBetween(&quot;2002-12-17&quot;, &quot;2002-12-18&quot;, false, false);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isNotBetween(&quot;2002-12-17&quot;, &quot;2002-12-18&quot;, false, true);
     * assertThat(theTwoTowers.getReleaseFDate()).isNotBetween(&quot;2002-12-18&quot;, &quot;2002-12-19&quot;, true, false);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param start
     *            the period start (inclusive), expected not to be null.
     * @param end
     *            the period end (exclusive), expected not to be null.
     * @param inclusiveStart
     *            whether to include start date in period.
     * @param inclusiveEnd
     *            whether to include end date in period.
     * @return this assertion object.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if start FDate as String is {@code null}.
     * @throws NullPointerException
     *             if end FDate as String is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in (start, end) period.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isNotBetween(final String start, final String end, final boolean inclusiveStart,
            final boolean inclusiveEnd) {
        return isNotBetween(parse(start), parse(end), inclusiveStart, inclusiveEnd);
    }

    /**
     * Verifies that the actual {@code FDate} is not in [start, end[ period
     *
     * @param start
     *            the period start (inclusive), expected not to be null.
     * @param end
     *            the period end (exclusive), expected not to be null.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if start {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if end {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is in [start, end[ period.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isNotBetween(final FDate start, final FDate end) {
        return isNotBetween(start, end, true, false);
    }

    /**
     * Same assertion as {@link #isNotBetween(FDate, FDate)} but given date is represented as String either with one of
     * the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theFellowshipOfTheRing.getReleaseFDate()).isNotBetween(&quot;2002-12-01&quot;, &quot;2002-12-10&quot;);
     * 
     * // assertion will fail
     * assertThat(theFellowshipOfTheRing.getReleaseFDate()).isNotBetween(&quot;2002-12-01&quot;, &quot;2002-12-19&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param start
     *            the period start (inclusive), expected not to be null.
     * @param end
     *            the period end (exclusive), expected not to be null.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if start FDate as String is {@code null}.
     * @throws NullPointerException
     *             if end FDate as String is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is in [start, end[ period.
     * @throws AssertionError
     *             if one of the given date as String could not be converted to a FDate.
     */
    public FDateAssert isNotBetween(final String start, final String end) {
        return isNotBetween(parse(start), parse(end), true, false);
    }

    /**
     * Verifies that the actual {@code FDate} is strictly in the past.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isInThePast();
     * </pre>
     *
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in the past.
     */
    public FDateAssert isInThePast() {
        dates.assertIsInThePast(info, actual);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} is today, that is matching current year, month and day (no check on hour,
     * minute, second, milliseconds).
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(new FDate()).isToday();
     * 
     * // assertion will fail
     * assertThat(theFellowshipOfTheRing.getReleaseFDate()).isToday();
     * </pre>
     *
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not today.
     */
    public FDateAssert isToday() {
        dates.assertIsToday(info, actual);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} is strictly in the future.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isInTheFuture();
     * </pre>
     *
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in the future.
     */
    public FDateAssert isInTheFuture() {
        dates.assertIsInTheFuture(info, actual);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} is <b>strictly</b> before the given year.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isBeforeYear(2004);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isBeforeYear(2002);
     * assertThat(theTwoTowers.getReleaseFDate()).isBeforeYear(2000);
     * </pre>
     *
     * @param year
     *            the year to compare actual year to
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} year is after or equals to the given year.
     */
    public FDateAssert isBeforeYear(final int year) {
        dates.assertIsBeforeYear(info, actual, year);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} is <b>strictly</b> after the given year.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isAfterYear(2001);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isAfterYear(2002);
     * assertThat(theTwoTowers.getReleaseFDate()).isAfterYear(2004);
     * </pre>
     *
     * @param year
     *            the year to compare actual year to
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} year is before or equals to the given year.
     */
    public FDateAssert isAfterYear(final int year) {
        dates.assertIsAfterYear(info, actual, year);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} year is equal to the given year.
     * <p>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isWithinYear(2002);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isWithinYear(2004);
     * </pre>
     *
     * @param year
     *            the year to compare actual year to
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} year is not equal to the given year.
     */
    public FDateAssert isWithinYear(final int year) {
        dates.assertIsWithinYear(info, actual, year);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} month is equal to the given month, <b>month value starting at 1</b>
     * (January=1, February=2, ...).
     * <p>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isWithinMonth(12);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isWithinMonth(10);
     * </pre>
     *
     * @param month
     *            the month to compare actual month to, <b>month value starting at 1</b> (January=1, February=2, ...).
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} month is not equal to the given month.
     */
    public FDateAssert isWithinMonth(final int month) {
        dates.assertIsWithinMonth(info, actual, month);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} day of month is equal to the given day of month.
     * <p>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(theTwoTowers.getReleaseFDate()).isWithinDayOfMonth(18);
     * 
     * // assertion will fail
     * assertThat(theTwoTowers.getReleaseFDate()).isWithinDayOfMonth(20);
     * </pre>
     *
     * @param dayOfMonth
     *            the day of month to compare actual day of month to
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} month is not equal to the given day of month.
     */
    public FDateAssert isWithinDayOfMonth(final int dayOfMonth) {
        dates.assertIsWithinDayOfMonth(info, actual, dayOfMonth);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} day of week is equal to the given day of week (see
     * {@link Calendar#DAY_OF_WEEK} for valid values).
     * <p>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(new FDate(parseFDatetime("2003-04-26T13:20:35").getTime()).isWithinDayOfWeek(Calendar.SATURDAY);
     * 
     * // assertion will fail
     * assertThat(new FDate(parseFDatetime("2003-04-26T13:20:35").getTime()).isWithinDayOfWeek(Calendar.MONDAY);
     * </pre>
     *
     * @param dayOfWeek
     *            the day of week to compare actual day of week to, see {@link Calendar#DAY_OF_WEEK} for valid values
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} week is not equal to the given day of week.
     */
    public FDateAssert isWithinDayOfWeek(final int dayOfWeek) {
        dates.assertIsWithinDayOfWeek(info, actual, dayOfWeek);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} hour of day is equal to the given hour of day (24-hour clock).
     * <p>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(new FDate(parseFDatetime("2003-04-26T13:20:35").getTime()).isWithinHourOfDay(13);
     * 
     * // assertion will fail
     * assertThat(new FDate(parseFDatetime("2003-04-26T13:20:35").getTime()).isWithinHourOfDay(22);
     * </pre>
     *
     * @param hourOfDay
     *            the hour of day to compare actual hour of day to (24-hour clock)
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} hour is not equal to the given hour.
     */
    public FDateAssert isWithinHourOfDay(final int hourOfDay) {
        dates.assertIsWithinHourOfDay(info, actual, hourOfDay);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} minute is equal to the given minute.
     * <p>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(new FDate(parseFDatetime("2003-04-26T13:20:35").getTime()).isWithinMinute(20);
     * 
     * // assertion will fail
     * assertThat(new FDate(parseFDatetime("2003-04-26T13:20:35").getTime()).isWithinMinute(17);
     * </pre>
     *
     * @param minute
     *            the minute to compare actual minute to
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} minute is not equal to the given minute.
     */
    public FDateAssert isWithinMinute(final int minute) {
        dates.assertIsWithinMinute(info, actual, minute);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} second is equal to the given second.
     * <p>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     * <p/>
     * Example:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(new FDate(parseFDatetime("2003-04-26T13:20:35").getTime()).isWithinSecond(35);
     * 
     * // assertion will fail
     * assertThat(new FDate(parseFDatetime("2003-04-26T13:20:35").getTime()).isWithinSecond(11);
     * </pre>
     *
     * @param second
     *            the second to compare actual second to
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} second is not equal to the given second.
     */
    public FDateAssert isWithinSecond(final int second) {
        dates.assertIsWithinSecond(info, actual, second);
        return myself;
    }

    /**
     * Verifies that the actual {@code FDate} millisecond is equal to the given millisecond.
     * <p/>
     * Examples:
     * 
     * <pre>
     * // assertion will pass
     * assertThat(parseFDatetimeWithMs(&quot;2003-04-26T13:20:35.017&quot;)).isWithinMillisecond(17);
     * 
     * // assertion will fail
     * assertThat(parseFDatetimeWithMs(&quot;2003-04-26T13:20:35.017&quot;)).isWithinMillisecond(25);
     * </pre>
     * 
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     *
     * @param millisecond
     *            the millisecond to compare actual millisecond to
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} millisecond is not equal to the given millisecond.
     */
    public FDateAssert isWithinMillisecond(final int millisecond) {
        dates.assertIsWithinMillisecond(info, actual, millisecond);
        return myself;
    }

    /**
     * Verifies that actual and given {@code FDate} are in the same year.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parse(&quot;2003-04-26&quot;);
     * FDate date2 = parse(&quot;2003-05-27&quot;);
     * 
     * assertThat(date1).isInSameYearAs(date2);
     * </pre>
     * 
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same year.
     */
    public FDateAssert isInSameYearAs(final FDate other) {
        dates.assertIsInSameYearAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameYearAs(FDate)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parse(&quot;2003-04-26&quot;);
     * assertThat(date1).isInSameYearAs(&quot;2003-05-27&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given FDate represented as String are not in the same year.
     * @throws AssertionError
     *             if the given date as String could not be converted to a FDate.
     */
    public FDateAssert isInSameYearAs(final String dateAsString) {
        return isInSameYearAs(parse(dateAsString));
    }

    /**
     * Verifies that actual and given {@code FDate} have same month and year fields.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parse(&quot;2003-04-26&quot;);
     * FDate date2 = parse(&quot;2003-04-27&quot;);
     * 
     * assertThat(date1).isInSameMonthAs(date2);
     * </pre>
     * 
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same month.
     */
    public FDateAssert isInSameMonthAs(final FDate other) {
        dates.assertIsInSameMonthAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameMonthAs(FDate)}but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parse(&quot;2003-04-26&quot;);
     * assertThat(date1).isInSameMonthAs(&quot;2003-04-27&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same month.
     */
    public FDateAssert isInSameMonthAs(final String dateAsString) {
        return isInSameMonthAs(parse(dateAsString));
    }

    /**
     * Verifies that actual and given {@code FDate} have the same day of month, month and year fields values.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-04-26T23:17:00&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-04-26T12:30:00&quot;);
     * 
     * assertThat(date1).isInSameDayAs(date2);
     * </pre>
     * 
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same day of month.
     */
    public FDateAssert isInSameDayAs(final FDate other) {
        dates.assertIsInSameDayAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameDayAs(FDate)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-04-26T23:17:00&quot;);
     * assertThat(date1).isInSameDayAs(&quot;2003-04-26&quot;);
     * </pre>
     * 
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same day of month.
     */
    public FDateAssert isInSameDayAs(final String dateAsString) {
        return isInSameDayAs(parse(dateAsString));
    }

    /**
     * Verifies that actual and given {@code FDate} are chronologically in the same hour (i.e. their time difference <=
     * 1 hour).
     * <p/>
     * This assertion succeeds as time difference is exactly = 1h:
     * 
     * <pre>
     * FDate date1 = parseFDatetime("2003-04-26T13:00:00");
     * FDate date2 = parseFDatetime("2003-04-26T14:00:00");
     * assertThat(date1).isInSameHourWindowAs(date2)
     * </pre>
     * 
     * Two dates can have different hour fields and yet be in the same chronological hour, example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime("2003-04-26T13:00:00");
     * FDate date2 = parseFDatetime("2003-04-26T12:59:59");
     * // succeeds as time difference == 1s
     * assertThat(date1).isInSameHourWindowAs(date2)
     * </pre>
     * 
     * This assertion fails as time difference is more than one hour:
     * 
     * <pre>
     * FDate date1 = parseFDatetime("2003-04-26T13:00:00");
     * FDate date2 = parseFDatetime("2003-04-26T14:00:01");
     * assertThat(date1).isInSameHourWindowAs(date2)
     * </pre>
     * 
     * To compare date's hour fields only (without day, month and year), you can write :
     * 
     * <pre>
     * assertThat(myFDate).isWithinHour(hourOfDayOf(otherFDate))
     * </pre>
     * 
     * see {@link org.assertj.core.util.FDatesAssertions#hourOfDayOf(java.util.FDate) hourOfDayOf} to get the hour of a given
     * FDate.
     * <p/>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}).
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same hour.
     */
    public FDateAssert isInSameHourWindowAs(final FDate other) {
        dates.assertIsInSameHourWindowAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameHourWindowAs(java.util.FDate)} but given date is represented as String either
     * with one of the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same day of month.
     */
    public FDateAssert isInSameHourWindowAs(final String dateAsString) {
        return isInSameHourWindowAs(parse(dateAsString));
    }

    /**
     * Verifies that actual and given {@code FDate} have same hour, day, month and year fields values.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-01-01T12:00:00&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-01-01T12:30:00&quot;);
     * 
     * // succeeds
     * assertThat(date1).isInSameHourAs(date2);
     * </pre>
     * 
     * <b>This assertion does not make a true chronological comparison</b> since two dates can have different hour
     * fields and yet be in the same chronological hour, e.g:
     * 
     * <pre>
     * // dates in the same hour time window but with different hour fields
     * FDate date1 = parseFDatetime(&quot;2003-01-01T12:00:00&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-01-01T11:59:00&quot;);
     * </pre>
     * 
     * If you want to assert that two dates are chronologically in the same hour time window use
     * {@link #isInSameHourWindowAs(java.util.FDate) isInSameHourWindowAs} assertion (note that if
     * <code>isInSameHourAs</code> succeeds then <code>isInSameHourWindowAs</code> will succeed too).
     * <p/>
     * If you want to compare hour only (without day, month and year), you could write :
     * <code>assertThat(myFDate).isWithinHour(hourOfDayOf(otherFDate))</code><br>
     * see {@link org.assertj.core.util.FDatesAssertions#hourOfDayOf(FDate)} to get the hour of a given FDate.
     * <p/>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same hour.
     */
    public FDateAssert isInSameHourAs(final FDate other) {
        dates.assertIsInSameHourAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameHourAs(FDate)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same hour.
     */
    public FDateAssert isInSameHourAs(final String dateAsString) {
        return isInSameHourAs(parse(dateAsString));
    }

    /**
     * Verifies that actual and given {@code FDate} are chronologically in the same hour (i.e. their time difference <=
     * 1 hour).
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-01-01T12:01:00&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-01-01T12:01:30&quot;);
     * 
     * // succeeds because date time difference &lt; 1 min
     * assertThat(date1).isInSameMinuteWindowAs(date2);
     * </pre>
     * 
     * Two dates can have different minute fields and yet be in the same chronological minute, example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime("2003-01-01T12:01:00");
     * FDate date3 = parseFDatetime("2003-01-01T12:00:59");
     * 
     * // succeeds as time difference == 1s even though minutes fields differ
     * assertThat(date1).isInSameMinuteWindowAs(date3)
     * </pre>
     * 
     * This assertion fails as time difference is >= one minute:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-01-01T12:01:00&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-01-01T12:02:00&quot;);
     * 
     * // fails, time difference should hae been &lt; 1 min
     * assertThat(date1).isInSameMinuteWindowAs(date2); // ERROR
     * </pre>
     * 
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}).
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same minute.
     */
    public FDateAssert isInSameMinuteWindowAs(final FDate other) {
        dates.assertIsInSameMinuteWindowAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameMinuteWindowAs(FDate)} but given date is represented as String either with one
     * of the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same minute.
     */
    public FDateAssert isInSameMinuteWindowAs(final String dateAsString) {
        return isInSameMinuteWindowAs(parse(dateAsString));
    }

    /**
     * Verifies that actual and given {@code FDate} have same minute, same hour, day, month and year fields values.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetime(&quot;2003-01-01T12:01:00&quot;);
     * FDate date2 = parseFDatetime(&quot;2003-01-01T12:01:30&quot;);
     * 
     * // succeeds because the all the fields up to minutes are the same
     * assertThat(date1).isInSameMinuteAs(date2);
     * </pre>
     * 
     * <b>It does not make a true chronological comparison</b> since two dates can have different minute fields and yet
     * be in the same chronological minute, e.g:
     * 
     * <pre>
     * // dates in the same minute time window but with different minute fields
     * FDate date1 = parseFDatetime(&quot;2003-01-01T12:01:00&quot;);
     * FDate date3 = parseFDatetime(&quot;2003-01-01T12:00:59&quot;);
     * 
     * // fails because minutes fields differ even though time difference is only 1s !
     * assertThat(date1).isInSameMinuteAs(date3); // ERROR
     * </pre>
     * 
     * If you want to assert that two dates are in the same minute time window use
     * {@link #isInSameMinuteWindowAs(java.util.FDate) isInSameMinuteWindowAs} assertion (note that if
     * <code>isInSameMinuteAs</code> succeeds then <code>isInSameMinuteWindowAs</code> will succeed too).
     * <p/>
     * If you want to compare minute field only (without hour, day, month and year), you could write :
     * <code>assertThat(myFDate).isWithinMinute(minuteOf(otherFDate))</code><br>
     * using {@link org.assertj.core.util.FDatesAssertions#minuteOf(FDate)} to get the minute of a given FDate.
     * <p/>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}).
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same minute.
     */
    public FDateAssert isInSameMinuteAs(final FDate other) {
        dates.assertIsInSameMinuteAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameMinuteAs(FDate)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @return this assertion object.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same minute.
     */
    public FDateAssert isInSameMinuteAs(final String dateAsString) {
        return isInSameMinuteAs(parse(dateAsString));
    }

    /**
     * Verifies that actual and given {@code FDate} are chronologically strictly in the same second (i.e. their time
     * difference < 1 second).
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = parseFDatetimeWithMs(&quot;2003-04-26T13:01:02.123&quot;);
     * FDate date2 = parseFDatetimeWithMs(&quot;2003-04-26T13:01:02.456&quot;);
     * 
     * // succeeds as time difference is &lt; 1s
     * assertThat(date1).isInSameSecondWindowAs(date2);
     * </pre>
     * 
     * Two dates can have different second fields and yet be in the same chronological second, example:
     * 
     * <pre>
     * FDate date1 = parseFDatetimeWithMs(&quot;2003-04-26T13:01:02.999&quot;);
     * FDate date2 = parseFDatetimeWithMs(&quot;2003-04-26T13:01:03.000&quot;);
     * 
     * // succeeds as time difference is 1ms &lt; 1s
     * assertThat(date1).isInSameSecondWindowAs(date2);
     * </pre>
     * 
     * Those assertions fail as time difference is greater or equal to one second:
     * 
     * <pre>
     * FDate date1 = parseFDatetimeWithMs(&quot;2003-04-26T13:01:01.000&quot;);
     * FDate date2 = parseFDatetimeWithMs(&quot;2003-04-26T13:01:02.000&quot;);
     * 
     * // fails as time difference = 1s
     * assertThat(date1).isInSameSecondWindowAs(date2); // ERROR
     * 
     * FDate date3 = parseFDatetimeWithMs(&quot;2003-04-26T13:01:02.001&quot;);
     * // fails as time difference &gt; 1s
     * assertThat(date1).isInSameSecondWindowAs(date3); // ERROR
     * </pre>
     * 
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same second.
     */
    public FDateAssert isInSameSecondWindowAs(final FDate other) {
        dates.assertIsInSameSecondWindowAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameSecondWindowAs(FDate)} but given date is represented as String either with one
     * of the supported defaults date format or a user custom date format (set with method
     * {@link #withDateFormat(DateFormat)}).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same second.
     */
    public FDateAssert isInSameSecondWindowAs(final String dateAsString) {
        return isInSameSecondWindowAs(parse(dateAsString));
    }

    /**
     * Verifies that actual and given {@code FDate} have same second, minute, hour, day, month and year fields values.
     * 
     * <pre>
     * FDate date1 = parseFDatetimeWithMs(&quot;2003-01-01T12:00:01.000&quot;);
     * FDate date2 = parseFDatetimeWithMs(&quot;2003-01-01T12:00:01.250&quot;);
     * 
     * // succeeds because the all the time fields up to seconds are the same
     * assertThat(date1).isInSameSecondAs(date2);
     * </pre>
     * 
     * <b>It does not make a true chronological comparison</b> since two dates can have different second fields and yet
     * be in the same chronological second, e.g:
     * 
     * <pre>
     * FDate date1 = parseFDatetimeWithMs(&quot;2003-01-01T12:00:01.000&quot;);
     * FDate date3 = parseFDatetimeWithMs(&quot;2003-01-01T12:00:00.999&quot;);
     * 
     * // fails because seconds fields differ even though time difference is only 1ms !
     * assertThat(date1).isInSameSecondAs(date3); // ERROR
     * </pre>
     * 
     * If you want to assert that two dates are in the same second time window use
     * {@link #isInSameSecondWindowAs(java.util.FDate) isInSameSecondWindowAs} assertion.
     * <p/>
     * If you want to compare second fields only (without minute, hour, day, month and year), you could write :
     * <code>assertThat(myFDate).isWithinSecond(secondOf(otherFDate))</code><br>
     * using {@link org.assertj.core.util.FDatesAssertions#secondOf(FDate)} to get the second of a given FDate.
     * <p/>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}).
     *
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same second.
     */
    public FDateAssert isInSameSecondAs(final FDate other) {
        dates.assertIsInSameSecondAs(info, actual, other);
        return myself;
    }

    /**
     * Same assertion as {@link #isInSameSecondAs(FDate)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     */
    public FDateAssert isInSameSecondAs(final String dateAsString) {
        return isInSameSecondAs(parse(dateAsString));
    }

    /**
     * Verifies that the actual {@code FDate} is close to the other date by less than delta (expressed in milliseconds),
     * if difference is equals to delta it's ok.
     * <p>
     * One can use handy {@link TimeUnit} to convert a duration in milliseconds, for example you can express a delta of
     * 5 seconds with <code>TimeUnit.SECONDS.toMillis(5)</code>.
     * <p>
     * Note that using a custom comparator has no effect on this assertion (see {@link #usingComparator(Comparator)}.
     * <p/>
     * Example:
     * 
     * <pre>
     * FDate date1 = new FDate();
     * FDate date2 = new FDate(date1.getTime() + 100);
     * 
     * // assertion will pass
     * assertThat(date1).isCloseTo(date2, 80);
     * assertThat(date1).isCloseTo(date2, 100);
     * 
     * // assertion will fail
     * assertThat(date1).isCloseTo(date2, 101);
     * </pre>
     *
     * @param other
     *            the date to compare actual to
     * @param deltaInMilliseconds
     *            the delta used for date comparison, expressed in milliseconds
     * @return this assertion object.
     * @throws NullPointerException
     *             if {@code FDate} parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} week is not close to the given date by less than delta.
     */
    public FDateAssert isCloseTo(final FDate other, final long deltaInMilliseconds) {
        dates.assertIsCloseTo(info, actual, other, deltaInMilliseconds);
        return myself;
    }

    /**
     * Same assertion as {@link #isCloseTo(FDate, long)} but given date is represented as String either with one of the
     * supported defaults date format or a user custom date format (set with method {@link #withDateFormat(DateFormat)}
     * ).
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @param dateAsString
     *            the given FDate represented as String in default or custom date format.
     * @param deltaInMilliseconds
     *            the delta used for date comparison, expressed in milliseconds
     * @return this assertion object.
     * @throws NullPointerException
     *             if dateAsString parameter is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} week is not close to the given date by less than delta.
     */
    public FDateAssert isCloseTo(final String dateAsString, final long deltaInMilliseconds) {
        return isCloseTo(parse(dateAsString), deltaInMilliseconds);
    }

    /**
     * Verifies that the actual {@code FDate} has the same time as the given timestamp.
     * <p/>
     * Both time or timestamp express a number of milliseconds since January 1, 1970, 00:00:00 GMT.
     * <p/>
     * Example:
     * 
     * <pre>
     * assertThat(new FDate(42)).hasTime(42);
     * </pre>
     *
     * @param timestamp
     *            the timestamp to compare actual time to.
     * @return this assertion object.
     * @throws AssertionError
     *             if the actual {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} time is not equal to the given timestamp.
     * @see FDate#getTime()
     */
    public FDateAssert hasTime(final long timestamp) {
        dates.assertHasTime(info, actual, timestamp);
        return myself;
    }

    /**
     * Instead of using default date formats for the date String based FDate assertions like {@link #isEqualTo(String)},
     * AssertJ is gonna use any date formats registered with one of these methods :
     * <ul>
     * <li>{@link #withDateFormat(String)}</li>
     * <li>this method</li>
     * <li>{@link #registerCustomDateFormat(java.text.DateFormat)}</li>
     * <li>{@link #registerCustomDateFormat(String)}</li>
     * </ul>
     * <p/>
     * Beware that :
     * <ul>
     * <li>this will be the case for <b>all future FDate assertions in the test suite</b></li>
     * <li>once a custom date format is registered, the default date formats are not used anymore</li>
     * </ul>
     * <p/>
     * To revert to default format, call {@link #useDefaultDateFormatsOnly()} or {@link #withDefaultDateFormatsOnly()}.
     *
     * @param userCustomDateFormat
     *            the new FDate format used for String based FDate assertions.
     * @return this assertion object.
     */
    public FDateAssert withDateFormat(final DateFormat userCustomDateFormat) {
        registerCustomDateFormat(userCustomDateFormat);
        return myself;
    }

    /**
     * Instead of using default date formats for the date String based FDate assertions like {@link #isEqualTo(String)},
     * AssertJ is gonna use any date formats registered with one of these methods :
     * <ul>
     * <li>this method</li>
     * <li>{@link #withDateFormat(java.text.DateFormat)}</li>
     * <li>{@link #registerCustomDateFormat(java.text.DateFormat)}</li>
     * <li>{@link #registerCustomDateFormat(String)}</li>
     * </ul>
     * <p/>
     * Beware that :
     * <ul>
     * <li>this will be the case for <b>all future FDate assertions in the test suite</b></li>
     * <li>once a custom date format is registered, the default date formats are not used anymore</li>
     * </ul>
     * <p/>
     * To revert to default format, call {@link #useDefaultDateFormatsOnly()} or {@link #withDefaultDateFormatsOnly()}.
     *
     * @param userCustomDateFormatPattern
     *            the new FDate format string pattern used for String based FDate assertions.
     * @return this assertion object.
     */
    public FDateAssert withDateFormat(final String userCustomDateFormatPattern) {
        checkNotNull(userCustomDateFormatPattern, DATE_FORMAT_PATTERN_SHOULD_NOT_BE_NULL);
        return withDateFormat(new SimpleDateFormat(userCustomDateFormatPattern));
    }

    /**
     * Add the given date format to the ones used to parse date String in String based FDate assertions like
     * {@link #isEqualTo(String)}.
     * <p/>
     * User date formats are used before default ones in the order they have been registered (first registered, first
     * used).
     * <p/>
     * AssertJ is gonna use any date formats registered with one of these methods :
     * <ul>
     * <li>{@link #withDateFormat(String)}</li>
     * <li>{@link #withDateFormat(java.text.DateFormat)}</li>
     * <li>this method</li>
     * <li>{@link #registerCustomDateFormat(String)}</li>
     * </ul>
     * <p/>
     * Beware that AssertJ will use the newly registered format for <b>all remaining FDate assertions in the test
     * suite</b>
     * <p/>
     * To revert to default formats only, call {@link #useDefaultDateFormatsOnly()} or
     * {@link #withDefaultDateFormatsOnly()}.
     * <p/>
     * Code examples:
     * 
     * <pre>
     * FDate date = ... // set to 2003 April the 26th
     * assertThat(date).isEqualTo("2003-04-26");
     * 
     * try {
     *   // date with a custom format : failure since the default formats don't match.
     *   assertThat(date).isEqualTo("2003/04/26");
     * } catch (AssertionError e) {
     *   assertThat(e).hasMessage("Failed to parse 2003/04/26 with any of these date formats: " +
     *                            "[yyyy-MM-dd'T'HH:mm:ss.SSS, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd]");
     * }
     * 
     * // registering a custom date format to make the assertion pass
     * registerCustomDateFormat(new SimpleDateFormat("yyyy/MM/dd")); // registerCustomDateFormat("yyyy/MM/dd") would work to.
     * assertThat(date).isEqualTo("2003/04/26");
     * 
     * // the default formats are still available and should work
     * assertThat(date).isEqualTo("2003-04-26");
     * </pre>
     *
     * @param userCustomDateFormat
     *            the new FDate format used for String based FDate assertions.
     */
    public static void registerCustomDateFormat(final DateFormat userCustomDateFormat) {
        checkNotNull(userCustomDateFormat, DATE_FORMAT_SHOULD_NOT_BE_NULL);
        userDateFormats.get().add(userCustomDateFormat);
    }

    /**
     * Add the given date format to the ones used to parse date String in String based FDate assertions like
     * {@link #isEqualTo(String)}.
     * <p/>
     * User date formats are used before default ones in the order they have been registered (first registered, first
     * used).
     * <p/>
     * AssertJ is gonna use any date formats registered with one of these methods :
     * <ul>
     * <li>{@link #withDateFormat(String)}</li>
     * <li>{@link #withDateFormat(java.text.DateFormat)}</li>
     * <li>{@link #registerCustomDateFormat(java.text.DateFormat)}</li>
     * <li>this method</li>
     * </ul>
     * <p/>
     * Beware that AssertJ will use the newly registered format for <b>all remaining FDate assertions in the test
     * suite</b>
     * <p/>
     * To revert to default formats only, call {@link #useDefaultDateFormatsOnly()} or
     * {@link #withDefaultDateFormatsOnly()}.
     * <p/>
     * Code examples:
     * 
     * <pre>
     * FDate date = ... // set to 2003 April the 26th
     * assertThat(date).isEqualTo("2003-04-26");
     * 
     * try {
     *   // date with a custom format : failure since the default formats don't match.
     *   assertThat(date).isEqualTo("2003/04/26");
     * } catch (AssertionError e) {
     *   assertThat(e).hasMessage("Failed to parse 2003/04/26 with any of these date formats: " +
     *                            "[yyyy-MM-dd'T'HH:mm:ss.SSS, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd]");
     * }
     * 
     * // registering a custom date format to make the assertion pass
     * registerCustomDateFormat("yyyy/MM/dd");
     * assertThat(date).isEqualTo("2003/04/26");
     * 
     * // the default formats are still available and should work
     * assertThat(date).isEqualTo("2003-04-26");
     * </pre>
     *
     * @param userCustomDateFormatPattern
     *            the new FDate format pattern used for String based FDate assertions.
     */
    public static void registerCustomDateFormat(final String userCustomDateFormatPattern) {
        checkNotNull(userCustomDateFormatPattern, DATE_FORMAT_PATTERN_SHOULD_NOT_BE_NULL);
        registerCustomDateFormat(new SimpleDateFormat(userCustomDateFormatPattern));
    }

    /**
     * Remove all registered custom date formats => use only the defaults date formats to parse string as date.
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     */
    public static void useDefaultDateFormatsOnly() {
        userDateFormats.get().clear();
    }

    /**
     * Remove all registered custom date formats => use only the defaults date formats to parse string as date.
     * <p/>
     * Beware that the default formats are expressed in the current local timezone.
     * <p/>
     * Defaults date format (expressed in the local time zone) are :
     * <ul>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss.SSS</code></li>
     * <li><code>yyyy-MM-dd'T'HH:mm:ss</code></li>
     * <li><code>yyyy-MM-dd</code></li>
     * </ul>
     * <p/>
     * Example of valid string date representations:
     * <ul>
     * <li><code>2003-04-26T03:01:02.999</code></li>
     * <li><code>2003-04-26T13:01:02</code></li>
     * <li><code>2003-04-26</code></li>
     * </ul>
     *
     * @return this assertion
     */
    public FDateAssert withDefaultDateFormatsOnly() {
        useDefaultDateFormatsOnly();
        return myself;
    }

    /**
     * Thread safe utility method to parse a FDate with {@link #userDateFormats} first, then
     * {@link #DEFAULT_DATE_FORMATS}.
     * <p>
     * Returns <code>null</code> if dateAsString parameter is <code>null</code>.
     *
     * @param dateAsString
     *            the string to parse as a FDate with {@link #userDateFormats}
     * @return the corresponding FDate, null if dateAsString parameter is null.
     * @throws AssertionError
     *             if the string can't be parsed as a FDate
     */
    @VisibleForTesting
    FDate parse(final String dateAsString) {
        if (dateAsString == null) {
            return null;
        }
        // parse with date format specified by user if any, otherwise use default formats
        // no synchronization needed as userCustomDateFormat is thread local
        FDate date = parseFDateWith(dateAsString, userDateFormats.get());
        if (date != null) {
            return date;
        }
        // no matching user date format, let's try default format
        date = parseFDateWithDefaultDateFormats(dateAsString);
        if (date != null) {
            return date;
        }
        // no matching date format, throw an error
        throw new AssertionError("Failed to parse " + dateAsString + " with any of these date formats: "
                + info.representation().toStringOf(dateFormatsInOrderOfUsage()));
    }

    private FDate parseFDateWithDefaultDateFormats(final String dateAsString) {
        synchronized (DEFAULT_DATE_FORMATS) {
            return parseFDateWith(dateAsString, DEFAULT_DATE_FORMATS);
        }
    }

    private List<DateFormat> dateFormatsInOrderOfUsage() {
        final List<DateFormat> allDateFormatsInOrderOfUsage = newArrayList(userDateFormats.get());
        allDateFormatsInOrderOfUsage.addAll(DEFAULT_DATE_FORMATS);
        return allDateFormatsInOrderOfUsage;
    }

    private FDate parseFDateWith(final String dateAsString, final Collection<DateFormat> dateFormats) {
        for (final DateFormat defaultDateFormat : dateFormats) {
            try {
                return FDate.valueOf(defaultDateFormat.parse(dateAsString));
            } catch (final ParseException e) {
                // ignore and try next date format
                continue;
            }
        }
        return null;
    }

    @Override
    public FDateAssert usingComparator(final Comparator<? super FDate> customComparator) {
        super.usingComparator(customComparator);
        this.dates = new FDatesAssertions(new ComparatorBasedComparisonStrategy(customComparator));
        return myself;
    }

    @Override
    public FDateAssert usingDefaultComparator() {
        super.usingDefaultComparator();
        this.dates = FDatesAssertions.instance();
        return myself;
    }

}
