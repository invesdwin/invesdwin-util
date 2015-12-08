package de.invesdwin.util.bean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;
import org.mockito.Mockito;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;

// CHECKSTYLE:OFF abstract class name
@ThreadSafe
public class AValueObjectTest {
    //CHECKSTYLE:ON

    private boolean propertyChanged;

    @Test
    public void testDeepClone() {
        final CloneableVO vo = new CloneableVO();
        vo.setValue(5);
        vo.setMutableValue(new MutableInt(5));
        vo.setCloneableClass(new CloneableClass());
        vo.getCloneableClass().setOtherValue(8);
        vo.setCloneableVO(new CloneableVO());
        vo.getCloneableVO().setValue(6);
        final CloneableVO voClone = (CloneableVO) vo.clone();
        Assertions.assertThat(vo).isNotSameAs(voClone);
        //FST does not clone immutable values
        Assertions.assertThat(vo.getValue()).isSameAs(voClone.getValue());
        Assertions.assertThat(vo.getValue()).isEqualTo(voClone.getValue());
        //only mutable ones
        Assertions.assertThat(vo.getMutableValue()).isNotSameAs(voClone.getMutableValue());
        Assertions.assertThat(vo.getMutableValue()).isEqualTo(voClone.getMutableValue());
        //and cloneables
        Assertions.assertThat(vo.getCloneableClass()).isNotSameAs(voClone.getCloneableClass());
        Assertions.assertThat(vo.getCloneableClass()).isEqualTo(voClone.getCloneableClass());
        //and value objects
        Assertions.assertThat(vo.getCloneableVO()).isNotSameAs(voClone.getCloneableVO());
        Assertions.assertThat(vo.getCloneableVO()).isEqualTo(voClone.getCloneableVO());
    }

    @Test
    public void testShallowCloneableClass() {
        final CloneableClass vo = new CloneableClass();
        final CloneableVO value = new CloneableVO();
        value.setValue(5);
        vo.setValue(value);
        final CloneableClass voClone = (CloneableClass) vo.clone();
        Assertions.assertThat(vo).isNotSameAs(voClone);
        Assertions.assertThat(vo.getValue()).isSameAs(voClone.getValue());
        Assertions.assertThat(vo.getValue()).isEqualTo(voClone.getValue());
        Assertions.assertThat(vo.getValue().getValue()).isSameAs(voClone.getValue().getValue());
        Assertions.assertThat(vo.getValue().getValue()).isEqualTo(voClone.getValue().getValue());
    }

    @Test
    public void testShallowClone() {
        final CloneableVO vo = new CloneableVO();
        vo.setValue(5);
        vo.setMutableValue(new MutableInt(5));
        vo.setCloneableClass(new CloneableClass());
        vo.setCloneableVO(new CloneableVO());
        vo.getCloneableVO().setValue(6);
        final CloneableVO voClone = (CloneableVO) vo.shallowClone();
        Assertions.assertThat(vo).isNotSameAs(voClone);
        //shallow clone does not clone constants
        Assertions.assertThat(vo.getValue()).isSameAs(voClone.getValue());
        Assertions.assertThat(vo.getValue()).isEqualTo(voClone.getValue());
        //also not mutable values
        Assertions.assertThat(vo.getMutableValue()).isSameAs(voClone.getMutableValue());
        Assertions.assertThat(vo.getMutableValue()).isEqualTo(voClone.getMutableValue());
        //nor cloneables
        Assertions.assertThat(vo.getCloneableClass()).isSameAs(voClone.getCloneableClass());
        Assertions.assertThat(vo.getCloneableClass()).isEqualTo(voClone.getCloneableClass());
        //and not value objects
        Assertions.assertThat(vo.getCloneableVO()).isSameAs(voClone.getCloneableVO());
        Assertions.assertThat(vo.getCloneableVO()).isEqualTo(voClone.getCloneableVO());
    }

    @Test
    public void testShallowCloneReflective() {
        final CloneableVO vo = new CloneableVO();
        vo.setValue(5);
        vo.setMutableValue(new MutableInt(5));
        vo.setCloneableClass(new CloneableClass());
        vo.getCloneableClass().setOtherValue(8);
        vo.setCloneableVO(new CloneableVO());
        vo.getCloneableVO().setValue(6);
        final CloneableVO voClone = (CloneableVO) vo.shallowCloneReflective();
        Assertions.assertThat(vo).isNotSameAs(voClone);
        //refletive clone does not clone constants
        Assertions.assertThat(vo.getValue()).isSameAs(voClone.getValue());
        Assertions.assertThat(vo.getValue()).isEqualTo(voClone.getValue());
        //also it does not clone classes that do not implement cloneable
        Assertions.assertThat(vo.getMutableValue()).isSameAs(voClone.getMutableValue());
        Assertions.assertThat(vo.getMutableValue()).isEqualTo(voClone.getMutableValue());
        //but it does cloneables
        Assertions.assertThat(vo.getCloneableClass()).isNotSameAs(voClone.getCloneableClass());
        Assertions.assertThat(vo.getCloneableClass()).isEqualTo(voClone.getCloneableClass());
        //and value objects
        Assertions.assertThat(vo.getCloneableVO()).isNotSameAs(voClone.getCloneableVO());
        Assertions.assertThat(vo.getCloneableVO()).isEqualTo(voClone.getCloneableVO());
    }

    @Test
    public void testMergeFrom() {
        final Integer value = 5;
        final CloneableVO vo = new CloneableVO();
        vo.setValue(value);
        final CloneableVO newVo = new CloneableVO();
        newVo.mergeFrom(vo);
        Assertions.assertThat(value).isEqualTo(newVo.getValue());
        Assertions.assertThat(vo.getValue()).isEqualTo(newVo.getValue());

        newVo.setValue(null);
        vo.mergeFrom(newVo);
        Assertions.assertThat(newVo.getValue()).isNull();
        Assertions.assertThat(vo.getValue()).as("null values are not getting ignored!").isNotNull();

        final CloneableClass clazz = new CloneableClass();
        clazz.setOtherValue(1);
        vo.mergeFrom(clazz);
    }

    @Test
    public void testPropertyChangeSupport() {
        final CloneableVO vo = new CloneableVO();

        final PropertyChangeListener pcl = Mockito.mock(PropertyChangeListener.class);
        vo.addPropertyChangeListener(pcl);
        vo.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                Assertions.assertThat(evt.getPropertyName()).isEqualTo("value");
                Assertions.assertThat(evt.getOldValue()).isNull();
                Assertions.assertThat(evt.getNewValue()).isEqualTo(5);
                Assertions.assertThat(evt.getSource()).isSameAs(vo);
                System.out.println(Strings.asStringReflective(evt)); //SUPPRESS CHECKSTYLE single line
                propertyChanged = true;
            }
        });
        vo.setValue(5);
        Mockito.verify(pcl, Mockito.only()).propertyChange((PropertyChangeEvent) Mockito.any());
        Assertions.assertThat(propertyChanged).isTrue();
    }

    public static class CloneableVO extends AValueObject {
        private static final long serialVersionUID = 1L;

        private Integer value;

        private MutableInt mutableValue;

        private CloneableClass cloneableClass;

        private CloneableVO cloneableVO;

        public Integer getValue() {
            return value;
        }

        public void setValue(final Integer value) {
            final Integer oldValue = this.value;
            this.value = value;
            firePropertyChange("value", oldValue, value);
        }

        public MutableInt getMutableValue() {
            return mutableValue;
        }

        public void setMutableValue(final MutableInt mutableValue) {
            final MutableInt oldValue = this.mutableValue;
            this.mutableValue = mutableValue;
            firePropertyChange("mutableValue", oldValue, value);
        }

        public CloneableClass getCloneableClass() {
            return cloneableClass;
        }

        public void setCloneableClass(final CloneableClass cloneableClass) {
            this.cloneableClass = cloneableClass;
        }

        public CloneableVO getCloneableVO() {
            return cloneableVO;
        }

        public void setCloneableVO(final CloneableVO cloneableVO) {
            this.cloneableVO = cloneableVO;
        }

    }

    public static class CloneableClass implements Cloneable, Serializable {
        private CloneableVO value;
        private Integer otherValue;

        public CloneableVO getValue() {
            return value;
        }

        public void setValue(final CloneableVO value) {
            this.value = value;
        }

        public Integer getOtherValue() {
            return otherValue;
        }

        public void setOtherValue(final Integer otherValue) {
            this.otherValue = otherValue;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (final CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int hashCode() {
            return Objects.reflectionHashCode(this);
        }

        @Override
        public boolean equals(final Object obj) {
            return Objects.reflectionEquals(this, obj);
        }
    }

}
