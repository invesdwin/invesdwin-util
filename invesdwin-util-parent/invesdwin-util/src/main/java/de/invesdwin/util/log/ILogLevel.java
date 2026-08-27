package de.invesdwin.util.log;

public interface ILogLevel {

    boolean isEnabled(ILog logger);

    void log(ILog logger, String msg);

    void log(ILog logger, String format, Object p0);

    void log(ILog logger, String format, Object p0, Object p1);

    void log(ILog logger, String format, Object p0, Object p1, Object p2);

    void log(ILog logger, String format, Object p0, Object p1, Object p2, Object p3);

    void log(ILog logger, String format, Object p0, Object p1, Object p2, Object p3, Object p4);

    void log(ILog logger, String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    void log(ILog logger, String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    void log(ILog logger, String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7);

    void log(ILog logger, String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8);

    void log(ILog logger, String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6,
            Object p7, Object p8, Object p9);

    void log(ILog logger, String format, Object... params);

}
