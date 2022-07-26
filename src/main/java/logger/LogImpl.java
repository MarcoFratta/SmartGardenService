package logger;

public class LogImpl implements Log {
    private final Type type;
    private final String tag;
    private final String msg;

    public LogImpl(final Type type, final String tag, final String msg) {
        this.type = type;
        this.tag = tag;
        this.msg = msg;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
