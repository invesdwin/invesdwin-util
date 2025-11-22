package de.invesdwin.util.collections.loadingcache.historical.query.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterable;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDateBuilder;

@NotThreadSafe
public class ShiftForwardUnitsLoopTest {

    static {
        Reflections.disableJavaModuleSystemRestrictions();
    }

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(ShiftForwardUnitsLoopTest.class);

    private static final List<FDate> DATES;

    static {
        DATES = new ArrayList<>();
        for (int i = 2000; i <= 2010; i++) {
            DATES.add(FDateBuilder.newDate(i));
        }
    }

    @Test
    public void testShiftForward() {
        for (int shiftForwardUnits = 0; shiftForwardUnits < DATES.size(); shiftForwardUnits++) {
            for (int i = 0; i < DATES.size(); i++) {
                final FDate request = DATES.get(i);
                LOG.warn("*** " + i + "+" + shiftForwardUnits + ": " + request + " ***");
                final ShiftForwardUnitsLoop<FDate> loop = new ShiftForwardUnitsLoop<FDate>(request, shiftForwardUnits,
                        (t) -> t);
                loop.loop(request(request));
                final FDate nextValue = loop.getNextValue();
                LOG.info("nextValue " + nextValue);

                final int expectedIndex = i + shiftForwardUnits;
                if (expectedIndex >= DATES.size()) {
                    Assertions.checkEquals(DATES.get(DATES.size() - 1), nextValue);
                } else {
                    Assertions.checkEquals(DATES.get(expectedIndex), nextValue);
                }

                /////////////// minus
                final FDate requestMinus = request.addMilliseconds(-1);
                final ShiftForwardUnitsLoop<FDate> loopMinus = new ShiftForwardUnitsLoop<FDate>(requestMinus,
                        shiftForwardUnits, (t) -> t);
                loopMinus.loop(request(requestMinus));
                final FDate nextValueMinus = loopMinus.getNextValue();
                LOG.info("nextValueMinus " + nextValueMinus);

                final int expectedIndexMinus;
                if (shiftForwardUnits == 0 || i == 0) {
                    expectedIndexMinus = expectedIndex;
                } else {
                    expectedIndexMinus = i + shiftForwardUnits;
                }
                if (expectedIndexMinus >= DATES.size()) {
                    Assertions.checkEquals(DATES.get(DATES.size() - 1), nextValueMinus);
                } else {
                    Assertions.checkEquals(DATES.get(expectedIndexMinus), nextValueMinus);
                }

                ///////// plus
                final FDate requestPlus = request.addMilliseconds(1);
                final ShiftForwardUnitsLoop<FDate> loopPlus = new ShiftForwardUnitsLoop<FDate>(requestPlus,
                        shiftForwardUnits, (t) -> t);
                loopPlus.loop(request(requestPlus));
                final FDate nextValuePlus = loopPlus.getNextValue();
                LOG.info("nextValuePlus " + nextValuePlus);

                final int expectedIndexPlus = i + Integers.max(shiftForwardUnits + 1, 1);
                if (expectedIndexPlus >= DATES.size()) {
                    if (shiftForwardUnits == 0 || i >= DATES.size() - 1) {
                        Assertions.checkNull(nextValuePlus);
                    } else {
                        Assertions.checkEquals(DATES.get(DATES.size() - 1), nextValuePlus);
                    }
                } else {
                    Assertions.checkEquals(DATES.get(expectedIndexPlus), nextValuePlus);
                }
            }
        }
    }

    private ICloseableIterable<? extends FDate> request(final FDate request) {
        final ICloseableIterable<FDate> dates = WrapperCloseableIterable.maybeWrap(DATES);
        return new ASkippingIterable<FDate>(dates) {
            @Override
            protected boolean skip(final FDate element) {
                return element.isBeforeNotNullSafe(request);
            }
        };
    }

}
