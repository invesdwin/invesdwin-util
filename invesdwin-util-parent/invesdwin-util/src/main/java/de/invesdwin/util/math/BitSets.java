package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.internal.ABitSetsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBitSets;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABitSetsStaticFacade", targets = {
        CheckedCastBitSets.class })
@Immutable
public final class BitSets extends ABitSetsStaticFacade {

    public static BitSet toArray(final Collection<Boolean> vector) {
        if (vector == null) {
            return null;
        }
        return checkedCastVector(vector);
    }

    public static BitSet toArrayVector(final Collection<Boolean> vector) {
        return toArray(vector);
    }

    public static BitSet[] toArrayMatrix(final List<? extends List<Boolean>> matrix) {
        if (matrix == null) {
            return null;
        }
        final BitSet[] arrayMatrix = new BitSet[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            final List<Boolean> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<BitSet> asListMatrix(final BitSet[] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<BitSet> matrixAsList = new ArrayList<BitSet>(matrix.length);
        for (final BitSet vector : matrix) {
            matrixAsList.add(vector);
        }
        return matrixAsList;
    }

    public static <T> BitSet[] fixInconsistentMatrixDimensions(final BitSet[] matrix) {
        return fixInconsistentMatrixDimensions(matrix, Booleans.DEFAULT_MISSING_VALUE);
    }

    public static BitSet[] fixInconsistentMatrixDimensions(final BitSet[] matrix, final boolean missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static <T> BitSet[] fixInconsistentMatrixDimensions(final BitSet[] matrix, final boolean missingValue,
            final boolean appendMissingValues) {
        final int rows = matrix.length;
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final BitSet vector = matrix[i];
            if (cols != 0 && cols != vector.size()) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.size());
        }
        if (!colsInconsistent) {
            return matrix;
        }
        final BitSet[] fixedMatrix = new BitSet[rows];
        for (int i = 0; i < matrix.length; i++) {
            final BitSet vector = matrix[i];
            final BitSet fixedVector;
            if (vector.size() == cols) {
                fixedVector = (BitSet) vector.clone();
            } else {
                fixedVector = new BitSet(cols);
                if (appendMissingValues) {
                    System.arraycopy(vector, 0, fixedVector, 0, vector.size());
                    if (missingValue) {
                        for (int j = vector.size() - 1; j < fixedVector.size(); j++) {
                            fixedVector.set(j);
                        }
                    }
                } else {
                    //prepend
                    final int missingValues = fixedVector.size() - vector.size();
                    if (missingValue) {
                        for (int j = 0; j < missingValues; j++) {
                            fixedVector.set(j);
                        }
                    }
                    System.arraycopy(vector, 0, fixedVector, missingValues, vector.size());
                }
            }
            fixedMatrix[i] = fixedVector;
        }
        return fixedMatrix;
    }

    public static List<BitSet> fixInconsistentMatrixDimensionsAsList(final List<? extends BitSet> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, Booleans.DEFAULT_MISSING_VALUE);
    }

    public static List<BitSet> fixInconsistentMatrixDimensionsAsList(final List<? extends BitSet> matrix,
            final boolean missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    @SuppressWarnings("unchecked")
    public static List<BitSet> fixInconsistentMatrixDimensionsAsList(final List<? extends BitSet> matrix,
            final boolean missingValue, final boolean appendMissingValues) {
        final int rows = matrix.size();
        int cols = 0;
        boolean colsInconsistent = false;
        for (int i = 0; i < rows; i++) {
            final BitSet vector = matrix.get(i);
            if (cols != 0 && cols != vector.size()) {
                colsInconsistent = true;
            }
            cols = Integers.max(cols, vector.size());
        }
        if (!colsInconsistent) {
            return (List<BitSet>) matrix;
        }
        final List<BitSet> fixedMatrix = new ArrayList<>(rows);
        for (int i = 0; i < matrix.size(); i++) {
            final BitSet vector = matrix.get(i);
            final BitSet fixedVector;
            if (vector.size() == cols) {
                fixedVector = (BitSet) vector.clone();
            } else {
                fixedVector = new BitSet(cols);
                if (appendMissingValues) {
                    System.arraycopy(vector, 0, fixedVector, 0, vector.size());
                    if (missingValue) {
                        for (int j = vector.size() - 1; j < fixedVector.size(); j++) {
                            fixedVector.set(j);
                        }
                    }
                } else {
                    //prepend
                    final int missingValues = fixedVector.size() - vector.size();
                    if (missingValue) {
                        for (int j = 0; j < missingValues; j++) {
                            fixedVector.set(j);
                        }
                    }
                    System.arraycopy(vector, 0, fixedVector, missingValues, vector.size());
                }
            }
            fixedMatrix.add(fixedVector);
        }
        return fixedMatrix;
    }

}
