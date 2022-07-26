package logic;

import logger.Log;

public class AbstractOperation implements SmartGardenOperation {
    private final Log log;
    private final Runnable action;

    public AbstractOperation(final Log log, final Runnable action) {
        this.log = log;
        this.action = action;
    }


    @Override
    public Log getLog() {
        return this.log;
    }

    @Override
    public void execute() {
        this.action.run();
    }
}
