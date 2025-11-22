package de.invesdwin.util.log.disabled;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Marker;

import de.invesdwin.util.log.ILogLevel;

@Immutable
public final class DisabledLogLevel implements ILogLevel {

    public static final DisabledLogLevel INSTANCE = new DisabledLogLevel();

    private DisabledLogLevel() {}

    @Override
    public boolean isEnabled(final org.slf4j.Logger logger) {
        return false;
    }

    @Override
    public boolean isEnabled(final org.slf4j.Logger logger, final Marker marker) {
        return false;
    }

    @Override
    public void log(final org.slf4j.Logger logger, final String msg) {}

    @Override
    public void log(final org.slf4j.Logger logger, final String format, final Object arg) {}

    @Override
    public void log(final org.slf4j.Logger logger, final String format, final Object arg1, final Object arg2) {}

    @Override
    public void log(final org.slf4j.Logger logger, final String format, final Object... args) {}

    @Override
    public void log(final org.slf4j.Logger logger, final String msg, final Throwable t) {}

    @Override
    public void log(final org.slf4j.Logger logger, final Marker marker, final String msg) {}

    @Override
    public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg) {}

    @Override
    public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg1,
            final Object arg2) {}

    @Override
    public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object... args) {}

    @Override
    public void log(final org.slf4j.Logger logger, final Marker marker, final String msg, final Throwable t) {}

}
