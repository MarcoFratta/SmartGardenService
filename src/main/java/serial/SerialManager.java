package serial;

import logger.SingletonLogger;
import logger.Type;
import model.MappersBuilderImpl;
import model.SmartGarden;
import server.Service;
import server.Writer;

public class SerialManager {

    public static void start(final String portName, final int rate, final Service<SmartGarden> service) {
        SerialCommChannel channel = null;
        final var mapper = new MappersBuilderImpl().smartGardenShort();
        try {
            SingletonLogger.getLogger().log(Type.INFO, "SERIAL",
                    "Starting serial manager on port " + portName + " at " + rate + " baud");

            channel = new SerialCommChannel(portName, rate);
        } catch (final Exception e) {
            SingletonLogger.getLogger().log(Type.ERROR, "SERIAL",
                    "Cannot start serial manager on port " + portName);
            SingletonLogger.getLogger().log(Type.ERROR_INFO, "SERIAL", e.getMessage());
        }
        if (channel != null) {
            final var t = new SerialGardenMonitoringAgent(channel);
            t.start();
            final Writer writer = new SerialWriter(channel);
            service.addListener(g -> {
                try {
                    writer.write(mapper.to(g).encodePrettily());
                } catch (final Exception e) {
                    SingletonLogger.getLogger().log(Type.ERROR, "SERIAL",
                            "Cannot write serial on port " + portName);
                    SingletonLogger.getLogger().log(Type.ERROR_INFO, "SERIAL", e.getMessage());
                }
            });
        }
    }
}
