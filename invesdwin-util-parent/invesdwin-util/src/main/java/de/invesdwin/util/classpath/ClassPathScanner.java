package de.invesdwin.util.classpath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.TypeFilter;

@NotThreadSafe
public class ClassPathScanner
        extends org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider {

    private static List<TypeFilter> defaultExcludeFilters = new ArrayList<TypeFilter>();
    private boolean interfacesOnly;

    public ClassPathScanner() {
        super(false);
        for (final TypeFilter typeFilter : defaultExcludeFilters) {
            addExcludeFilter(typeFilter);
        }
        //see http://stackoverflow.com/questions/8807388/using-classpathscanningcandidatecomponentprovider-with-multiple-jar-files
        setResourceLoader(new PathMatchingResourcePatternResolver(getClass().getClassLoader()));
    }

    public static void setDefaultExcludeFilters(final List<TypeFilter> defaultExcludeFilters) {
        ClassPathScanner.defaultExcludeFilters = defaultExcludeFilters;
    }

    public static List<TypeFilter> getDefaultExcludeFilters() {
        return ClassPathScanner.defaultExcludeFilters;
    }

    @Override
    protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
        if (interfacesOnly) {
            return beanDefinition.getMetadata().isInterface();
        } else {
            return super.isCandidateComponent(beanDefinition);
        }
    }

    @Override
    protected boolean isCandidateComponent(final MetadataReader metadataReader) throws IOException {
        return super.isCandidateComponent(metadataReader);
    }

    public ClassPathScanner setInterfacesOnly() {
        interfacesOnly = true;
        return this;
    }

    public boolean isInterfacesOnly() {
        return interfacesOnly;
    }
}
