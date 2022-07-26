package serial;


import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import logger.SingletonLogger;
import logger.Type;

public class SerialGardenMonitoringAgent extends Thread {

	private final CommChannel channel;
	private final WebClient client;
	private final int port;
	private final String host;

	public SerialGardenMonitoringAgent(final CommChannel channel) {
		this.channel = channel;
		this.host = "localhost"; // "b1164b27.ngrok.io";
		this.port = 8080;
		final Vertx vertx = Vertx.vertx();
		this.client = WebClient.create(vertx);
	}

	public void run() {
		while (true) {
			try {
				final String msg = this.channel.receiveMsg();
				SingletonLogger.getLogger().log(Type.INFO, "SERIAL",
						"received from serial: " + msg);
				final JsonObject object = new JsonObject(msg);
				this.client.post(this.port, this.host, "/api/gardenShort")
						.sendJson(object)
						.onSuccess(response -> {
							SingletonLogger.getLogger()
									.log(Type.INFO, "SERIAL", "sending from serial to server: " + msg);
						});
			} catch (final Exception ex) {
				SingletonLogger.getLogger().log(Type.ERROR, "SERIAL", "input error");
				SingletonLogger.getLogger().log(Type.ERROR_INFO, "SERIAL", ex.getMessage());
			}
		}
	}
}
