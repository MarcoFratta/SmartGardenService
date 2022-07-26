package server;

import io.vertx.core.Vertx;
import logger.Logger;
import logger.SingletonLogger;
import logger.Type;
import model.SmartGarden;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/*
 * Data Service as a vertx event-loop
 */
public class RunService {

    public static Service<SmartGarden> start(final SmartGarden garden, final int port) {
        final Logger logger = SingletonLogger.getLogger();
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            final String ip = socket.getLocalAddress().getHostAddress();
            logger.log(Type.INFO, "SERVICE", "Starting service at: http://" + ip + ":8080");
        } catch (final UnknownHostException | SocketException e) {
            logger.log(Type.ERROR, "SERVICE", "Error starting service");
            SingletonLogger.getLogger().log(Type.ERROR_INFO, "SERVICE", e.getMessage());
        }
        final Vertx vertx = Vertx.vertx();

        final GardenService service = new GardenService(port, garden);
        vertx.deployVerticle(service);
        return service;
    }
}