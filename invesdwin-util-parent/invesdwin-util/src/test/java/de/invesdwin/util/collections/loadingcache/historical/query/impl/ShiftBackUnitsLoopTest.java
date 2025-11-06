package de.invesdwin.util.collections.loadingcache.historical.query.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterable;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDateBuilder;

@NotThreadSafe
public class ShiftBackUnitsLoopTest {

    static {
        Reflections.disableJavaModuleSystemRestrictions();
    }

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(ShiftBackUnitsLoopTest.class);

    private static final List<FDate> DATES;

    static {
        DATES = new ArrayList<>();
        for (int i = 2000; i <= 2010; i++) {
            DATES.add(FDateBuilder.newDate(i));
        }
        Collections.reverse(DATES);
    }

    @Test
    public void testShiftBackward() {
        for (int shiftBackUnits = 0; shiftBackUnits < DATES.size(); shiftBackUnits++) {
            for (int i = 0; i < DATES.size(); i++) {
                final FDate request = DATES.get(i);
                LOG.warn("*** " + i + "+" + shiftBackUnits + ": " + request + " ***");
                final ShiftBackUnitsLoop<FDate> loop = new ShiftBackUnitsLoop<FDate>(request, shiftBackUnits, (t) -> t);
                loop.loop(request(request));
                final FDate prevValue = loop.getPrevValue();
                LOG.info("prevValue " + prevValue);

                final int expectedIndex = i + shiftBackUnits;
                if (expectedIndex >= DATES.size()) {
                    Assertions.checkEquals(DATES.get(DATES.size() - 1), prevValue);
                } else {
                    Assertions.checkEquals(DATES.get(expectedIndex), prevValue);
                }

                /////////////// minus
                final FDate requestMinus = request.addMilliseconds(-1);
                final ShiftBackUnitsLoop<FDate> loopMinus = new ShiftBackUnitsLoop<FDate>(requestMinus, shiftBackUnits,
                        (t) -> t);
                loopMinus.loop(request(requestMinus));
                final FDate prevValueMinus = loopMinus.getPrevValue();
                LOG.info("prevValueMinus " + prevValueMinus);

                final int expectedIndexMinus;
                if (shiftBackUnits == 0 || i == 0) {
                    expectedIndexMinus = expectedIndex + 1;
                } else {
                    expectedIndexMinus = i + shiftBackUnits + 1;
                }
                if (expectedIndexMinus >= DATES.size()) {
                    if (shiftBackUnits == 0 || i >= DATES.size() - 1) {
                        Assertions.checkNull(prevValueMinus);
                    } else {
                        Assertions.checkEquals(DATES.get(DATES.size() - 1), prevValueMinus);
                    }
                } else {
                    Assertions.checkEquals(DATES.get(expectedIndexMinus), prevValueMinus);
                }

                ///////// plus
                final FDate requestPlus = request.addMilliseconds(1);
                final ShiftBackUnitsLoop<FDate> loopPlus = new ShiftBackUnitsLoop<FDate>(requestPlus, shiftBackUnits,
                        (t) -> t);
                loopPlus.loop(request(requestPlus));
                final FDate prevValuePlus = loopPlus.getPrevValue();
                LOG.info("prevValuePlus " + prevValuePlus);

                final int expectedIndexPlus = i + shiftBackUnits;
                if (expectedIndexPlus >= DATES.size()) {
                    Assertions.checkEquals(DATES.get(DATES.size() - 1), prevValuePlus);
                } else {
                    Assertions.checkEquals(DATES.get(expectedIndexPlus), prevValuePlus);
                }
            }
        }
    }

    private ICloseableIterable<? extends FDate> request(final FDate request) {
        final ICloseableIterable<FDate> dates = WrapperCloseableIterable.maybeWrap(DATES);
        return new ASkippingIterable<FDate>(dates) {
            @Override
            protected boolean skip(final FDate element) {
                return element.isAfterNotNullSafe(request);
            }
        };
    }

}
