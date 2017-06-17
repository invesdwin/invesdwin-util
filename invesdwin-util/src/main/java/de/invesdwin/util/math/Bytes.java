package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ABytesStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBytes;
import de.invesdwin.util.math.internal.CheckedCastBytesObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABytesStaticFacade", targets = { CheckedCastBytes.class,
        CheckedCastBytesObj.class, com.google.common.primitives.Bytes.class })
@Immutable
public final class Bytes extends ABytesStaticFacade {

    public static final ADelegateComparator<Byte> COMPARATOR = new ADelegateComparator<Byte>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Byte e) {
            return e;
        }
    };

    private Bytes() {}

    public static byte[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return ABytesStaticFacade.toArray(vector);
    }

    public static byte[] toArrayVector(final Collection<Byte> vector) {
        return toArray(vector);
    }

    public static byte[][] toArrayMatrix(final List<? extends List<Byte>> matrix) {
        if (matrix == null) {
            return null;
        }
        final byte[][] arrayMatrix = new byte[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Byte> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<java.lang.Byte> asList(final byte... vector) {
        if (vector == null) {
            return null;
        } else {
            return ABytesStaticFacade.asList(vector);
        }
    }

    public static List<Byte> asListVector(final byte[] vector) {
        return asList(vector);
    }

    public static List<List<Byte>> asListMatrix(final byte[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Byte>> matrixAsList = new ArrayList<List<Byte>>(matrix.length);
        for (final byte[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static Byte min(final Byte... times) {
        Byte minTime = null;
        for (final Byte time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Byte min(final Byte time1, final Byte time2) {
        if (time1 == null) {
            return time2;
        } else if (time2 == null) {
            return time1;
        }

        if (time1 < time2) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Byte max(final Byte... times) {
        Byte maxTime = null;
        for (final Byte time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Byte max(final Byte time1, final Byte time2) {
        if (time1 == null) {
            return time2;
        } else if (time2 == null) {
            return time1;
        }

        if (time1 > time2) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Byte between(final Byte value, final Byte min, final Byte max) {
        return max(min(value, max), min);
    }

}
