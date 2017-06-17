package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ABooleansStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBooleans;
import de.invesdwin.util.math.internal.CheckedCastBooleansObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABooleansStaticFacade", targets = {
        CheckedCastBooleans.class, CheckedCastBooleansObj.class, com.google.common.primitives.Booleans.class })
@Immutable
public final class Booleans extends ABooleansStaticFacade {

    public static final ADelegateComparator<Boolean> COMPARATOR = new ADelegateComparator<Boolean>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Boolean e) {
            return e;
        }
    };

    private Booleans() {}

    public static boolean[] toArray(final Collection<Boolean> vector) {
        if (vector == null) {
            return null;
        }
        return ABooleansStaticFacade.toArray(vector);
    }

    public static boolean[] toArrayVector(final Collection<Boolean> vector) {
        return toArray(vector);
    }

    public static boolean[][] toArrayMatrix(final List<? extends List<Boolean>> matrix) {
        if (matrix == null) {
            return null;
        }
        final boolean[][] arrayMatrix = new boolean[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Boolean> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Boolean> asList(final boolean... vector) {
        if (vector == null) {
            return null;
        } else {
            return ABooleansStaticFacade.asList(vector);
        }
    }

    public static List<Boolean> asListVector(final boolean[] vector) {
        return asList(vector);
    }

    public static List<List<Boolean>> asListMatrix(final boolean[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Boolean>> matrixAsList = new ArrayList<List<Boolean>>(matrix.length);
        for (final boolean[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

}
