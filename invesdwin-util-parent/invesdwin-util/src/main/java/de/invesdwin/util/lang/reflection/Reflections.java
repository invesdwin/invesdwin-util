package de.invesdwin.util.lang.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import org.agrona.UnsafeAccess;
import org.burningwave.core.assembler.StaticComponentContainer;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.norva.beanpath.BeanPathReflections;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.comparator.AComparator;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.reflection.internal.AReflectionsStaticFacade;
import de.invesdwin.util.lang.string.Strings;

@StaticFacadeDefinition(name = "de.invesdwin.util.lang.reflection.internal.AReflectionsStaticFacade", targets = {
        org.fest.reflect.core.Reflection.class, BeanPathReflections.class,
        org.springframework.core.GenericTypeResolver.class })
@Immutable
public final class Reflections extends AReflectionsStaticFacade {

    public static final IComparator<Method> METHOD_COMPARATOR = new AComparator<Method>() {
        @Override
        public int compareTypedNotNullSafe(final Method o1, final Method o2) {
            final String name1 = o1.getName();
            final String name2 = o2.getName();
            final int nameCompare = name1.compareTo(name2);
            if (nameCompare != 0) {
                return nameCompare;
            }
            final String signature1 = o1.toGenericString();
            final String signature2 = o2.toGenericString();
            return signature1.compareTo(signature2);
        }
    };

    /**
     * https://stackoverflow.com/questions/2591083/getting-java-version-at-runtime/21112531
     */
    public static final double JAVA_VERSION;
    /**
     * https://stackoverflow.com/questions/3776204/how-to-find-out-if-debug-mode-is-enabled
     */
    public static final boolean JAVA_DEBUG_MODE;
    /**
     * https://stackoverflow.com/questions/13029915/how-to-programmatically-test-if-assertions-are-enabled
     */
    public static final boolean JAVA_ASSERT_MODE;

    private static final String[] INSTANCE_FIELD_NAMES = new String[] { "INSTANCE", "GET" };
    private static final String[] INSTANCE_METHOD_NAMES = new String[] { "getInstance", "newInstance", "get" };

    @GuardedBy("this.class")
    private static boolean modulesExported = false;

    static {
        JAVA_VERSION = determineJavaVersion();
        JAVA_DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .toString()
                .indexOf("jdwp") >= 0;
        boolean assertOn = false;
        //CHECKSTYLE:OFF
        assert assertOn = true;
        //CHECKSTYLE:ON
        JAVA_ASSERT_MODE = assertOn;
    }

    private Reflections() {}

    private static double determineJavaVersion() {
        try {
            //CHECKSTYLE:OFF
            final String version = System.getProperty("java.specification.version");
            //CHECKSTYLE:ON
            return Double.parseDouble(version);
        } catch (final Throwable t) {
            return 1.8D; //use oldest
        }
    }

    /**
     * https://dev.to/jjbrt/how-to-avoid-resorting-to-add-exports-and-add-opens-in-jdk-16-and-later-j3m
     */
    public static synchronized void disableJavaModuleSystemRestrictions() {
        if (!modulesExported) {
            if (JAVA_VERSION > 8) {
                //https://github.com/burningwave/core/issues/10
                if (StaticComponentContainer.Modules != null) {
                    StaticComponentContainer.Modules.exportAllToAll();
                }
            }
            modulesExported = true;
        }
    }

    public static boolean classExists(final String className) {
        try {
            Assertions.assertThat(Class.forName(className, false, getDefaultClassLoader())).isNotNull();
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Don't touch spring classes so that we can upgrade from java 8 to java 17 with this.
     * 
     * Extracted from org.springframework.util.ClassUtils.getDefaultClassLoader().
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (final Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = Reflections.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (final Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
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

    public static void assertObjectNotReferenced(final Object obj, final Object in) {
        if (obj == in) {
            return;
        }
        final Set<Object> visitedIdentitySet = ILockCollectionFactory.getInstance(false).newIdentitySet();
        visitedIdentitySet.add(obj);
        assertObjectNotReferencedRecursive(obj, in, visitedIdentitySet);
    }

    private static void assertObjectNotReferencedRecursive(final Object obj, final Object in,
            final Set<Object> visitedIdentitySet) {
        final Class<?> inClass = in.getClass();
        for (final Field field : inClass.getDeclaredFields()) {
            if (field.getType().isPrimitive() || field.getType().getName().startsWith("java")) {
                continue;
            }
            makeAccessible(field);
            final Object parent = getField(field, in);
            if (parent == null || parent == in) {
                continue;
            } else if (parent == obj) {
                throw new IllegalArgumentException("[" + obj.getClass().getName() + ":" + obj
                        + "] is reference leaked by [" + in.getClass().getName() + ":" + in + "]");
            } else if (visitedIdentitySet.add(parent)) {
                try {
                    assertObjectNotReferencedRecursive(obj, parent, visitedIdentitySet);
                } catch (final Throwable t) {
                    throw new RuntimeException("Via [" + parent.getClass().getName() + ":" + parent + "]", t);
                }
            }
        }
    }

    @SuppressWarnings("restriction")
    public static sun.misc.Unsafe getUnsafe() {
        //CHECKSTYLE:OFF
        return UnsafeAccess.UNSAFE;
        //CHECKSTYLE:ON
    }

    public static String getClassSimpleNameNonBlank(final Class<?> clazz) {
        Class<?> curClazz = clazz;
        String className = null;
        while (Strings.isBlank(className) && curClazz != null) {
            className = curClazz.getSimpleName();
            curClazz = curClazz.getSuperclass();
        }
        return className;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOrCreateInstance(final Class<T> type) {
        try {
            for (final String instanceFieldName : INSTANCE_FIELD_NAMES) {
                final Field instanceField = Reflections.findField(type, instanceFieldName);
                if (instanceField != null && Reflections.isPublic(instanceField)
                        && Reflections.isStatic(instanceField)) {
                    return (T) instanceField.get(null);
                }
            }
            for (final String instanceMethodName : INSTANCE_METHOD_NAMES) {
                final Method instanceMethod = Reflections.findMethod(type, instanceMethodName);
                if (instanceMethod != null && Reflections.isPublic(instanceMethod)
                        && Reflections.isStatic(instanceMethod)) {
                    return (T) instanceMethod.invoke(null);
                }
            }
            return type.getConstructor().newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * https://stackoverflow.com/a/62434122
     */
    @SuppressWarnings({ "unchecked", "restriction" })
    public static <T> T unsafeClone(final T object) {
        try {
            final T instance = (T) getUnsafe().allocateInstance(object.getClass());
            Class<?> clazz = object.getClass();
            while (!clazz.equals(Object.class)) {
                for (final Field field : clazz.getDeclaredFields()) {
                    if (Reflections.isFinal(field)) {
                        continue;
                    }
                    if (Reflections.isStatic(field)) {
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(instance, field.get(object));
                }
                clazz = clazz.getSuperclass();
            }
            return instance;
        } catch (final Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
