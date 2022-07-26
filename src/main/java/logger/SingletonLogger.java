package logger;

import java.util.ArrayList;
import java.util.List;

public class SingletonLogger implements Logger {

    private static Logger logger;
    private final List<LoggerListener> listeners;
    private final List<Log> logs;

    private SingletonLogger() {
        this.listeners = new ArrayList<>();
        this.logs = new ArrayList<>();
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = new SingletonLogger();
        }
        return logger;
    }

    @Override
    public void log(final Type t, final String tag, final String msg) {
        this.log(new LogImpl(t, tag, msg));
    }

    @Override
    public void observe(final LoggerListener l) {
        this.listeners.add(l);
        this.logs.forEach(l::action);
    }

    @Override
    public void log(final Log log) {
        this.logs.add(log);
        this.listeners.forEach(l -> l.action(log));
    }
}
