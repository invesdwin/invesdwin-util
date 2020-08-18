package de.invesdwin.util.math;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
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

}
