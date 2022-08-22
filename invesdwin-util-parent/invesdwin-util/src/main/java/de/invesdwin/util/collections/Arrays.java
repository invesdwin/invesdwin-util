package de.invesdwin.util.collections;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AArraysStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.AArraysStaticFacade", targets = {
        java.util.Arrays.class, java.lang.reflect.Array.class, org.apache.commons.lang3.ArrayUtils.class,
        org.apache.commons.math3.util.MathArrays.class })
public class Arrays extends AArraysStaticFacade {

    @SuppressWarnings("unchecked")
    public static <T> T[] concat(final Class<T> type, final T[]... arrays) {
        if (arrays.length == 0) {
            return (T[]) newInstance(type, 0);
        }

        int finalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            //should be an empty array
            finalLength += arrays[i].length;
        }

        if (finalLength == 0) {
            return arrays[0];
        }

        final T[] dest = (T[]) newInstance(type, finalLength);
        int destPos = 0;

        for (int i = 0; i < arrays.length; i++) {
            final T[] array = arrays[i];
            System.arraycopy(array, 0, dest, destPos, array.length);
            destPos += array.length;
        }
        return dest;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] concat(final T[]... arrays) {
        if (arrays.length == 0) {
            final Class<?> arrayType = arrays.getClass().getComponentType().getComponentType();
            return (T[]) newInstance(arrayType, 0);
        }

        int finalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            finalLength += arrays[i].length;
        }

        if (finalLength == 0) {
            //should be an empty array
            return arrays[0];
        }

        final T[] dest = Arrays.copyOf(arrays[0], finalLength);
        int destPos = arrays[0].length;

        for (int i = 1; i < arrays.length; i++) {
            final T[] array = arrays[i];
            System.arraycopy(array, 0, dest, destPos, array.length);
            destPos += array.length;
        }
        return dest;
    }

    @SuppressWarnings("unchecked")
    public static <T> java.util.List<T> asList(final T... a) {
        if (a == null) {
            return null;
        }
        if (a.length == 0) {
            return Collections.emptyList();
        }
        return java.util.Arrays.asList(a);
    }

}
