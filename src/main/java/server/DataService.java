package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import model.*;

import java.util.LinkedList;
import java.util.List;

public class DataService extends AbstractVerticle {

    private final int port;
    private static final int MAX_SIZE = 10;
    private final SmartGarden smartGarden;
    private final LinkedList<InputData> values;
    private final StrategySelector strategySelector;

    public DataService(final int port) {
        this.values = new LinkedList<>();
        this.port = port;

        this.smartGarden = new SmartGardenImpl(
                List.of(new OnOffObjectImpl("Lamp 1"), new OnOffObjectImpl("Lamp 2")),
                        List.of(new IntensityObjectImpl(0,4,"Lamp 3"),
                                new IntensityObjectImpl(0,4,"Lamp 4")),
                                new IntensityObjectImpl(0,4, "Irrigation system"));
        this.strategySelector = new StrategySelectorImpl();
    }

    @Override
    public void start() {
        final Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        router.post("/api/data").handler(this::handleAddNewData);
        router.get("/api/data").handler(this::handleGetData);
        this.vertx.createHttpServer()
                .requestHandler(router)
                .listen(this.port);

        this.log("Service ready.");
    }

    private void handleAddNewData(final RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();
        // log("new msg "+routingContext.getBodyAsString());
        final JsonObject res = routingContext.getBodyAsJson();
        if (res == null) {
            this.sendError(400, response);
        } else {
            final int temp = res.getInteger("temp");
            final int lum = res.getInteger("lum");
            final long time = System.currentTimeMillis();
            final InputData value = new InputDataImpl(temp, lum, time);

            this.values.addFirst(value);
            if (this.values.size() > MAX_SIZE) {
                this.values.removeLast();
            }

            this.log("New value: " + value);
            this.selectStrategy(value);
            response.setStatusCode(200)
                    .end();
        }
    }

    private void selectStrategy(final InputData value) {
        final SmartGardenStrategy strategy = this.strategySelector.select(this.smartGarden,value);
        strategy.getOperations().forEach(o -> {
            this.log(o.getLog());
            o.execute();
        });
    }

    private void handleGetData(final RoutingContext routingContext) {
        final JsonArray arr = new JsonArray();
        for (final InputData p: this.values) {
            final JsonObject data = new JsonObject();
            data.put("temp", p.getTemperature());
            data.put("lum", p.getLuminosity());
            arr.add(data);
        }
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(arr.encodePrettily());
    }

    private void sendError(final int statusCode, final HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    private void log(final String msg) {
        System.out.println("[DATA SERVICE] "+msg);
    }

}