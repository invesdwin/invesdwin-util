package de.invesdwin.util.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.springframework.util.ClassUtils;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.norva.beanpath.BeanPathReflections;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.internal.AReflectionsStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.lang.internal.AReflectionsStaticFacade", targets = {
        org.fest.reflect.core.Reflection.class, BeanPathReflections.class,
        org.springframework.core.GenericTypeResolver.class })
@Immutable
public final class Reflections extends AReflectionsStaticFacade {

    private Reflections() {}

    public static boolean classExists(final String className) {
        try {
            Assertions.assertThat(Class.forName(className, false, ClassUtils.getDefaultClassLoader())).isNotNull();
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> classForName(final String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassLoader getRootClassLoader(final ClassLoader classLoader) {
        ClassLoader parent = classLoader;
        while (true) {
            final ClassLoader newParent = parent.getParent();
            if (newParent == null) {
                return parent;
            } else {
                parent = newParent;
            }
        }
    }

    /**
     * @return the previous annotation in place on this specific class
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T addAnnotation(final Class<?> clazz, final T newAnnotation) {
        try {
            final Field field = Class.class.getDeclaredField("annotations");
            field.setAccessible(true);
            final Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) field.get(clazz);
            return (T) annotations.put(newAnnotation.annotationType(), newAnnotation);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the previous annotation in place on this specific class
     */
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T addAnnotation(final Method method, final T newAnnotation) {
        try {
            final Field field = Method.class.getDeclaredField("declaredAnnotations");
            field.setAccessible(true);
            final Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) field.get(method);
            return (T) annotations.put(newAnnotation.annotationType(), newAnnotation);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void makeAccessibleFinal(final Field field) {
        try {
            makeAccessible(field);
            Field modifiersField;
            modifiersField = Field.class.getDeclaredField("modifiers");
            makeAccessible(modifiersField);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (final NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            handleReflectionException(e);
        }
    }

    public static boolean isSynchronized(final Method method) {
        return Modifier.isSynchronized(method.getModifiers());
    }

    public static <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        // Get the caller by inspecting the stackTrace
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Iterate on the stack trace. the outer most annotation wins here
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            Class<?> classCaller = null;

            // Get the current class
            try {
                classCaller = Class.forName(stackTrace[i].getClassName());
            } catch (final ClassNotFoundException cnfe) {
                // Corner case : we just have to go higher in the stack in this case.
                continue;
            }

            // Get the current method
            final String methodCaller = stackTrace[i].getMethodName();

            T instance = null;

            // Check if we have any annotation associated with the method
            final Method method = findMethodByName(classCaller, methodCaller);
            if (method != null) {
                instance = getAnnotation(method, annotationType);
            }

            if (instance == null) {
                instance = getAnnotation(classCaller, annotationType);
            }

            if (instance != null) {
                return instance;
            }
        }

        return null;
    }

}
