package logger;

public interface Log {
    Type getType();

    String getTag();

    String getMsg();
}
