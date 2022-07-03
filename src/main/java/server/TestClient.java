package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class TestClient extends AbstractVerticle {

    public static void main(final String[] args) throws Exception {

        final String host = "localhost"; // "b1164b27.ngrok.io";
        final int port = 8080;

        final Vertx vertx = Vertx.vertx();

        final JsonObject item = new JsonObject();
        item.put("temp",((int) (Math.random()*5)));
        item.put("lum",((int) (Math.random()*8)));

        final WebClient client = WebClient.create(vertx);

        System.out.println("Posting new data item... ");
        client
                .post(port, host, "/api/data")
                .sendJson(item)
                .onSuccess(response -> {
                    System.out.println("Posting - Received response with status code: " + response.statusCode());
                });

        Thread.sleep(1000);

        System.out.println("Getting data items... ");
        client
                .get(port, host, "/api/data")
                .send()
                .onSuccess(res -> {
                    System.out.println("Getting - Received response with status code: " + res.statusCode());
                    final JsonArray response = res.bodyAsJsonArray();
                    System.out.println(response.encodePrettily());
                })
                .onFailure(err ->
                        System.out.println("Something went wrong " + err.getMessage()));
    }

}