package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ACharactersStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastCharacters;
import de.invesdwin.util.math.internal.CheckedCastCharactersObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ACharactersStaticFacade", targets = {
        CheckedCastCharacters.class, CheckedCastCharactersObj.class, com.google.common.primitives.Chars.class })

@Immutable
public final class Characters extends ACharactersStaticFacade {

    public static final ADelegateComparator<Character> COMPARATOR = new ADelegateComparator<Character>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Character e) {
            return e;
        }
    };

    private Characters() {}

    public static char[] toArray(final Collection<Character> vector) {
        if (vector == null) {
            return null;
        }
        return ACharactersStaticFacade.toArray(vector);
    }

    public static char[] toArrayVector(final Collection<Character> vector) {
        return toArray(vector);
    }

    public static char[][] toArrayMatrix(final List<? extends List<Character>> matrix) {
        if (matrix == null) {
            return null;
        }
        final char[][] arrayMatrix = new char[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Character> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<Character> asList(final char... vector) {
        if (vector == null) {
            return null;
        } else {
            return ACharactersStaticFacade.asList(vector);
        }
    }

    public static List<Character> asListVector(final char[] vector) {
        return asList(vector);
    }

    public static List<List<Character>> asListMatrix(final char[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<Character>> matrixAsList = new ArrayList<List<Character>>(matrix.length);
        for (final char[] vector : matrix) {
            matrixAsList.add(asList(vector));
        }
        return matrixAsList;
    }

    public static Character min(final Character... times) {
        Character minTime = null;
        for (final Character time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Character min(final Character time1, final Character time2) {
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

    public static Character max(final Character... times) {
        Character maxTime = null;
        for (final Character time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Character max(final Character time1, final Character time2) {
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

    public static Character between(final Character value, final Character min, final Character max) {
        return max(min(value, max), min);
    }

}
