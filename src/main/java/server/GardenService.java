package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import model.Mapper;
import model.MappersBuilderImpl;
import model.SensorBoardData;
import model.SmartGarden;

import java.util.LinkedList;

public class GardenService extends AbstractVerticle {

    private final int port;
    private static final int MAX_SIZE = 10;
    private final SmartGarden smartGarden;
    private final LinkedList<SensorBoardData> values;
    private final StrategySelector<SensorBoardData> strategySelector;
    private final Mapper<SensorBoardData> sensorBoardDataMapper;
    private final Mapper<SmartGarden> smartGardenMapper;

    public GardenService(final int port, final SmartGarden garden) {
        this.values = new LinkedList<>();
        this.port = port;
        this.smartGarden = garden;
        final var strategyBuilder = new StrategySelectorBuilderImpl();
        final var mappersBuilder = new MappersBuilderImpl();
        this.strategySelector = strategyBuilder.sensorsDataSelector();
        this.sensorBoardDataMapper = mappersBuilder.sensorBoardData();
        this.smartGardenMapper = mappersBuilder.smartGarden();
    }

    @Override
    public void start() {
        final Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        router.post("/api/inputData").handler(this::handleAddNewSensorInput);
        router.get("/api/inputData").handler(this::handleGetData);
        router.get("/api/garden").handler(this::getSmartGarden);
        this.log("Service ready.");
        this.vertx.createHttpServer()
                .requestHandler(router)
                .listen(this.port);
    }

    private void getSmartGarden(final RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(this.smartGardenMapper.toJson(this.smartGarden).encodePrettily());
    }

    private void handleAddNewSensorInput(final RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.getBodyAsJson();
        if (res == null) {
            this.sendError(400, response);
            return;
        }
        try {
            final var value = this.sensorBoardDataMapper.toObject(res);
            this.values.addFirst(value);
            if (this.values.size() > MAX_SIZE) {
                this.values.removeLast();
            }
            this.log("New value: " + value);
            this.selectStrategy(value);
            response.setStatusCode(200).end();
        } catch (final Exception e) {
            //e.printStackTrace();
            this.sendError(500, response);
        }
    }

    private void selectStrategy(final SensorBoardData value) {
        final SmartGardenStrategy strategy = this.strategySelector.select(this.smartGarden, value);
        strategy.getOperations().forEach(o -> {
            this.log(o.getLog());
            o.execute();
        });
    }

    private void handleGetData(final RoutingContext routingContext) {
        final JsonArray arr = new JsonArray();
        this.values.forEach(v -> arr.add(this.sensorBoardDataMapper.toJson(v)));
        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(arr.encodePrettily());
    }

    private void sendError(final int statusCode, final HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    private void log(final String msg) {
        System.out.println("[DATA SERVICE] " + msg);
    }
}