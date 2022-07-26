package serial;

import logger.SingletonLogger;
import logger.Type;
import server.Writer;

public class SerialWriter implements Writer {

    private final CommChannel channel;

    public SerialWriter(final CommChannel channel) {
        this.channel = channel;
    }

    public void write(final String msg) {
        try {
            SingletonLogger.getLogger().log(Type.ACTION, "Serial",
                    "Sending message to serial" + msg);
            this.channel.sendMsg(msg);
        } catch (final Exception ex) {
            SingletonLogger.getLogger().log(Type.ERROR, "Serial",
                    "Error sending message");
            SingletonLogger.getLogger().log(Type.ERROR_INFO,
                    "SERVICE", ex.getMessage());
        }
    }
}
