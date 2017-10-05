package de.invesdwin.util.bean.internal;

import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;

import de.invesdwin.norva.beanpath.impl.object.BeanObjectContext;
import de.invesdwin.norva.beanpath.impl.object.BeanObjectProcessor;
import de.invesdwin.norva.beanpath.spi.element.IBeanPathElement;
import de.invesdwin.norva.beanpath.spi.element.IPropertyBeanPathElement;
import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Reflections;

@ThreadSafe
public class ValueObjectMerge {

    /**
     * @see <a href=
     *      "http://apache-commons.680414.n4.nabble.com/Setting-null-on-Integer-property-via-BeanUtils-setProperty-td955955.html">
     *      Null handling</a>
     */
    @GuardedBy("this.class")
    private static BeanUtilsBean beanUtilsBean;

    private final AValueObject thisVo;
    private final boolean overwrite;
    private final boolean clone;
    private final Set<String> recursionFilter;

    public ValueObjectMerge(final AValueObject thisVo, final boolean overwrite, final boolean clone,
            final Set<String> recursionFilter) {
        this.thisVo = thisVo;
        this.overwrite = overwrite;
        this.clone = clone;
        this.recursionFilter = recursionFilter;
    }

    private static synchronized BeanUtilsBean getBeanUtilsBean() {
        if (beanUtilsBean == null) {
            beanUtilsBean = new BeanUtilsBean();
            //Set defaults for BeanUtils.
            beanUtilsBean.getConvertUtils().register(false, true, 0);
        }
        return beanUtilsBean;
    }

    //CHECKSTYLE:OFF
    public void merge(final Object o) {
        //CHECKSTYLE:ON
        final BeanObjectContext thisCtx = new BeanObjectContext(thisVo);
        boolean thisProcessed = false;
        final BeanObjectContext thereCtx = new BeanObjectContext(o);
        new BeanObjectProcessor(thereCtx).withShallowOnly().process();
        for (final IBeanPathElement thereElement : thereCtx.getElementRegistry().getElements()) {
            final String propertyName = thereElement.getAccessor().getBeanPathFragment();
            if (!thereElement.isProperty() || !thereElement.getAccessor().hasPublicGetterOrField()) {
                continue;
            }
            if (Objects.REFLECTION_EXCLUDED_FIELDS.contains(propertyName)) {
                continue;
            }

            final IPropertyBeanPathElement therePropertyElement = (IPropertyBeanPathElement) thereElement;
            Object valueThere = therePropertyElement.getModifier().getValue();
            if (clone && valueThere != null) {
                if (valueThere instanceof AValueObject) {
                    final AValueObject cValueThere = (AValueObject) valueThere;
                    valueThere = cValueThere.shallowClone();
                } else /* if (valueThere instanceof Cloneable) */ {
                    try {
                        valueThere = Reflections.method("clone").in(valueThere).invoke();
                    } catch (final Throwable t) {
                        //ignore
                    }
                }
            }
            if (valueThere != null) {
                if (!thisProcessed) {
                    //process lazy
                    new BeanObjectProcessor(thisCtx).withShallowOnly().process();
                    thisProcessed = true;
                }
                final IBeanPathElement thisElement = thisCtx.getElementRegistry().getElement(propertyName);
                if (thisElement == null || !thisElement.isProperty()
                        || !thisElement.getAccessor().hasPublicSetterOrField()) {
                    continue;
                }

                copyValue(thisElement, valueThere);
            }
        }
    }

    private void copyValue(final IBeanPathElement thisElement, final Object valueThere) {
        boolean copy = true;
        final IPropertyBeanPathElement thisPropertyElement = (IPropertyBeanPathElement) thisElement;
        final Object valueThis = thisPropertyElement.getModifier().getValue();
        if (valueThis != null) {
            if (valueThis instanceof AValueObject) {
                final AValueObject vo = (AValueObject) valueThis;
                if (recursionFilter.add(Objects.toStringIdentity(vo))) {
                    new ValueObjectMerge(vo, overwrite, clone, recursionFilter).merge(valueThere);
                }
                copy = clone;
            } else {
                copy = overwrite;
            }
        }

        if (copy) {
            final Class<?> type = thisPropertyElement.getModifier().getAccessor().getRawType().getType();
            final Object convertedValue = convertValue(valueThere, type);
            thisPropertyElement.getModifier().setValue(convertedValue);
        }
    }

    private Object convertValue(final Object value, final Class<?> type) {
        if (value == null) {
            return null;
        }
        final Converter converter = getBeanUtilsBean().getConvertUtils().lookup(type);
        if (converter != null) {
            return converter.convert(type, value);
        } else {
            return value;
        }
    }

}
