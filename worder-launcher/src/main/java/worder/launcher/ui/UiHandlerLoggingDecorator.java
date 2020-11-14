/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <UiHandlerLoggingDecorator.java>
 * Created: <11/11/2020, 08:24:43 PM>
 * Modified: <14/11/2020, 09:32:54 PM>
 * Version: <185>
 */

package worder.launcher.ui;

import worder.launcher.logging.SimpleLogger;

public class UiHandlerLoggingDecorator implements UiHandler {
    private final UiHandler uiHandler;
    private final SimpleLogger simpleLogger;


    public UiHandlerLoggingDecorator(UiHandler uiHandler, SimpleLogger simpleLogger) {
        this.uiHandler = uiHandler;
        this.simpleLogger = simpleLogger;
    }


    @Override
    public void show() {
        simpleLogger.log("SHOW", "Call of show() on " + uiHandler);
        uiHandler.show();
    }

    @Override
    public void dispose(long delay) {
        simpleLogger.log("DISPOSE", "Call of dispose(" + delay + ") on " + uiHandler);
        uiHandler.dispose(delay);
    }

    @Override
    public void status(String status) {
        simpleLogger.log("STATUS", status);
        uiHandler.status(status);
    }

    @Override
    public void error(String message) {
        simpleLogger.log("ERROR", message);
        uiHandler.error(message);
    }
}
