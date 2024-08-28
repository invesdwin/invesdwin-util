package de.invesdwin.util.log;

import org.slf4j.Marker;

public interface ILogLevel {

    boolean isEnabled(org.slf4j.Logger logger);

    boolean isEnabled(org.slf4j.Logger logger, Marker marker);

    void log(org.slf4j.Logger logger, String msg);

    void log(org.slf4j.Logger logger, String format, Object arg);

    void log(org.slf4j.Logger logger, String format, Object arg1, Object arg2);

    void log(org.slf4j.Logger logger, String format, Object... args);

    void log(org.slf4j.Logger logger, String msg, Throwable t);

    void log(org.slf4j.Logger logger, Marker marker, String msg);

    void log(org.slf4j.Logger logger, Marker marker, String format, Object arg);

    void log(org.slf4j.Logger logger, Marker marker, String format, Object arg1, Object arg2);

    void log(org.slf4j.Logger logger, Marker marker, String format, Object... args);

    void log(org.slf4j.Logger logger, Marker marker, String msg, Throwable t);

}
