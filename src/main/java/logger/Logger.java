package logger;

public interface Logger {

    void log(Type t, String tag, String msg);

    void observe(LoggerListener l);

    void log(Log log);
}
