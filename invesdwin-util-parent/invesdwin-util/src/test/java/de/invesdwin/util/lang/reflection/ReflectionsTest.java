package de.invesdwin.util.lang.reflection;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@Immutable
public class ReflectionsTest {

    @Test
    public void testClassExists() {
        Assertions.assertThat(Reflections.classExists(this.getClass().getName())).isTrue();
    }

    /**
     * The point of the field helpers: a private field of a superclass is not reachable through {@code getDeclaredField}
     * on the class the caller holds, and reading it needs setAccessible - which every caller would otherwise have to
     * remember.
     */
    @Test
    public void testFieldHelpersReachAnInheritedPrivateField() {
        final Subclass target = new Subclass();
        Assertions.assertThat(Reflections.findDeclaredField(Subclass.class, "inherited")).isNotNull();
        Assertions.assertThat(Reflections.getFieldValue(target, "inherited"))
                .as("read through the superclass boundary")
                .isEqualTo("initial");
        Reflections.setFieldValue(target, "inherited", "written");
        Assertions.assertThat(target.readInherited())
                .as("the write landed on the field the object itself reads")
                .isEqualTo("written");
        Assertions.assertThat(Reflections.findDeclaredField(Subclass.class, "notDeclared")).isNull();
        Assertions.assertThatThrownBy(() -> Reflections.getFieldValue(target, "notDeclared"))
                .as("a missing field is named, not a null pointer further out")
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static class Base {

        /**
         * Not a String: a final String initialised with a literal is a constant variable (JLS 15.28), so javac inlines
         * it at the read site and no reflective write can ever be observed there - reproduced on JDK 17.0.20.1 with
         * setAccessible(true) and Field.set: no exception, readInherited() still "initial".
         */
        private final Object inherited = "initial";

        Object readInherited() {
            return inherited;
        }
    }

    private static final class Subclass extends Base {
    }

}
