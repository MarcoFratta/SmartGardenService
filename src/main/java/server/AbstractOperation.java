package server;

public class AbstractOperation implements SmartGardenOperation{
    private final String log;
    private final Runnable action;

    public AbstractOperation(final String log, final Runnable action) {
        this.log = log;
        this.action = action;
    }


    @Override
    public String getLog() {
        return "[Action] " + this.log;
    }

    @Override
    public void execute() {
        this.action.run();
    }
}
