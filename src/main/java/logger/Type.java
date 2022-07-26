package logger;

public enum Type {
    ERROR("Error", "#bf3737"),
    ACTION("Action", "#60bf37"),
    INFO("Info", "#000000"),
    ERROR_INFO("Stacktrace", "#b8339f");


    private final String s;
    private final String color;

    Type(final String s, final String color) {
        this.s = s;
        this.color = color;
    }

    public String getName() {
        return this.s;
    }

    public String getColor() {
        return this.color;
    }
}
