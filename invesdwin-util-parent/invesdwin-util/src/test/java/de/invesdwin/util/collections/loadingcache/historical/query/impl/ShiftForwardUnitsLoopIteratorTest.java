package de.invesdwin.util.collections.loadingcache.historical.query.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.skip.ASkippingIterable;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDateBuilder;

@NotThreadSafe
public class ShiftForwardUnitsLoopIteratorTest {

    static {
        Reflections.disableJavaModuleSystemRestrictions();
    }

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(ShiftForwardUnitsLoopIteratorTest.class);

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
                final ShiftForwardUnitsLoopIterator<FDate> loop = new ShiftForwardUnitsLoopIterator<FDate>(request,
                        shiftForwardUnits, (t) -> t, request(request).iterator());
                final List<FDate> nextValues = Lists.toListWithoutHasNext(loop);
                final FDate nextValue = getLast(nextValues);
                LOG.info("nextValue " + nextValue);

                final int expectedIndex = i + shiftForwardUnits;
                if (expectedIndex >= DATES.size()) {
                    Assertions.checkEquals(DATES.get(DATES.size() - 1), nextValue);
                } else {
                    Assertions.checkEquals(DATES.get(expectedIndex), nextValue);
                }
                Assertions.checkEquals(DATES.subList(i, Integers.min(expectedIndex + 1, DATES.size())), nextValues);

                /////////////// minus
                final FDate requestMinus = request.addMilliseconds(-1);
                final ShiftForwardUnitsLoopIterator<FDate> loopMinus = new ShiftForwardUnitsLoopIterator<FDate>(
                        requestMinus, shiftForwardUnits, (t) -> t, request(requestMinus).iterator());
                final List<FDate> nextValuesMinus = Lists.toListWithoutHasNext(loopMinus);
                final FDate nextValueMinus = getLast(nextValuesMinus);
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
                Assertions.checkEquals(DATES.subList(i, Integers.min(expectedIndexMinus + 1, DATES.size())),
                        nextValuesMinus);

                ///////// plus
                final FDate requestPlus = request.addMilliseconds(1);
                final ShiftForwardUnitsLoopIterator<FDate> loopPlus = new ShiftForwardUnitsLoopIterator<FDate>(
                        requestPlus, shiftForwardUnits, (t) -> t, request(requestPlus).iterator());
                final List<FDate> nextValuesPlus = Lists.toListWithoutHasNext(loopPlus);
                final FDate nextValuePlus = getLast(nextValuesPlus);
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
                Assertions.checkEquals(DATES.subList(i + 1, Integers.min(expectedIndexPlus + 1, DATES.size())),
                        nextValuesPlus);
            }
        }
    }

    private FDate getLast(final List<FDate> values) {
        if (values.isEmpty()) {
            return null;
        } else {
            return values.get(values.size() - 1);
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
