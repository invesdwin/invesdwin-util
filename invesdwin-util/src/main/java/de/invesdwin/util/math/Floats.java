package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.AFloatsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastFloats;
import de.invesdwin.util.math.internal.CheckedCastFloatsObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AFloatsStaticFacade", targets = {
        CheckedCastFloats.class, CheckedCastFloatsObj.class, com.google.common.primitives.Floats.class })
@Immutable
public final class Floats extends AFloatsStaticFacade {

    public static final ADelegateComparator<Float> COMPARATOR = new ADelegateComparator<Float>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Float e) {
            return e;
        }
    };

    private Floats() {}

    public static float[] toArray(final Collection<? extends Number> vector) {
        if (vector == null) {
            return null;
        }
        return AFloatsStaticFacade.toArray(vector);
    }

    public static float[] toArrayVector(final Collection<Float> vector) {
        return toArray(vector);
    }

    public static float[][] toArrayMatrix(final List<? extends List<Float>> matrix) {
        if (matrix == null) {
            return null;
        }
        final float[][] arrayMatrix = new float[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Float> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Float> asList(final float... vector) {
        if (vector == null) {
            return null;
        } else {
            return AFloatsStaticFacade.asList(vector);
        }
    }

    public static List<Float> asListVector(final float[] vector) {
        return asList(vector);
    }

    public static List<List<Float>> asListMatrix(final float[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Float>> matrixAsList = new ArrayList<List<Float>>(matrix.length);
        for (final float[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static Float max(final Float first, final Float second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Float min(final Float first, final Float second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    public static Float between(final Float value, final Float min, final Float max) {
        return max(min(value, max), min);
    }

}
