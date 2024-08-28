package de.invesdwin.util.log;

public interface ILogFactory {

    ILog getLog(String name);

    default ILog getLog(final Class<?> clazz) {
        return getLog(clazz.getName());
    }

    default ILog getLog(final Object obj) {
        return getLog(obj.getClass());
    }

}
