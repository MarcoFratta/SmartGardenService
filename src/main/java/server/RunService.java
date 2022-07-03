package server;

import io.vertx.core.Vertx;

/*
 * Data Service as a vertx event-loop
 */
public class RunService {


    public static void main(final String[] args) {
        final Vertx vertx = Vertx.vertx();
        final DataService service = new DataService(8080);
        vertx.deployVerticle(service);
    }
}