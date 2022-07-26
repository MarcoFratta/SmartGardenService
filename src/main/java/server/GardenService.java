package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import logger.Logger;
import logger.SingletonLogger;
import logger.Type;
import logic.SmartGardenStrategy;
import logic.StrategySelector;
import logic.StrategySelectorBuilder;
import logic.StrategySelectorBuilderImpl;
import model.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class GardenService extends AbstractVerticle implements Service<SmartGarden> {

    private final int port;
    private static final int MAX_SIZE = 10;
    private final LinkedList<SmartGarden> smartGardenOldStatus;
    private final Logger logger;
    private final LinkedList<SensorBoardData> values;
    private final StrategySelector<SensorBoardData> strategySelector;
    private final Mapper<JsonObject, SensorBoardData> sensorBoardDataMapper;
    private final Mapper<JsonObject, SmartGarden> smartGardenMapper;
    private final Mapper<JsonObject, SmartGarden> shortSmartGardenMapper;
    private final Mapper<String, GardenState> gardenStateMapper;
    private final List<Consumer<SmartGarden>> listeners;
    private SmartGarden actualGarden;


    public GardenService(final int port, final SmartGarden garden) {
        this.values = new LinkedList<>();
        this.port = port;
        this.smartGardenOldStatus = new LinkedList<>();
        final StrategySelectorBuilder strategyBuilder = new StrategySelectorBuilderImpl();
        final MappersBuilder mappersBuilder = new MappersBuilderImpl();
        this.strategySelector = strategyBuilder.sensorsDataSelector();
        this.sensorBoardDataMapper = mappersBuilder.sensorBoardData();
        this.smartGardenMapper = mappersBuilder.smartGarden();
        this.logger = SingletonLogger.getLogger();
        this.actualGarden = garden;
        this.listeners = new ArrayList<>();
        this.shortSmartGardenMapper = mappersBuilder.smartGardenShort();
        this.gardenStateMapper = mappersBuilder.smartGardenState();
    }

    @Override
    public void start() {
        final Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        router.post("/api/inputData").handler(this::handleAddNewSensorInput);
        router.post("/api/garden").handler(this::newSmartGarden);
        router.post("/api/gardenShort").handler(this::newSmartGardenShort);
        router.get("/api/inputData").handler(this::handleGetData);
        router.get("/api/garden").handler(this::getSmartGarden);
        router.get("/api/gardenStatus").handler(this::getGardenStatus);
        router.get("/api/gardenState").handler(this::getGardenState);
        router.get("/api/gardenShort").handler(this::getGardenStatusShort);
        this.logger.log(Type.INFO, "SERVICE", "service ready");
        this.vertx.createHttpServer()
                .requestHandler(router)
                .listen(this.port);
    }

    private void newSmartGardenShort(final RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.getBodyAsJson();
        if (res == null) {
            this.sendError(400, response);
            return;
        }
        try {
            final SmartGarden value = this.shortSmartGardenMapper.from(res);
            this.logger.log(Type.INFO, "INPUT", "New garden: " + value);
            this.smartGardenOldStatus.add(new SmartGardenImpl(this.actualGarden));
            this.actualGarden = value;
            response.setStatusCode(200).end();
        } catch (final Exception e) {
            this.logger.log(Type.ERROR, "SERVICE", "Error receiving new garden info");
            this.logger.log(Type.ERROR_INFO, "SERVICE", e.getMessage());
            this.sendError(500, response);
        }
    }

    private void getGardenState(final RoutingContext routingContext) {
        routingContext.response()
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("content-type", "text/plain")
                .end(this.gardenStateMapper.to(this.actualGarden.getState()));
    }

    private void getGardenStatusShort(final RoutingContext routingContext) {
        routingContext.response()
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("content-type", "application/json")
                .end(this.shortSmartGardenMapper.to(this.actualGarden).toString());
    }

    private void getGardenStatus(final RoutingContext routingContext) {
        final JsonArray arr = new JsonArray();
        arr.add(this.smartGardenMapper.to(this.actualGarden));
        this.smartGardenOldStatus.forEach(v -> arr.add(this.smartGardenMapper.to(v)));
        routingContext.response()
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("content-type", "application/json")
                .end(arr.encodePrettily());
    }

    private void newSmartGarden(final RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.getBodyAsJson();
        if (res == null) {
            this.sendError(400, response);
            return;
        }
        try {
            final SmartGarden value = this.smartGardenMapper.from(res);
            this.logger.log(Type.INFO, "INPUT", "New garden: " + value);
            this.smartGardenOldStatus.add(new SmartGardenImpl(this.actualGarden));
            this.actualGarden = value;
            response.setStatusCode(200).end();
        } catch (final Exception e) {
            this.logger.log(Type.ERROR, "SERVICE", "Error receiving new garden info");
            this.logger.log(Type.ERROR_INFO, "SERVICE", e.getMessage());
            this.sendError(500, response);
        }
    }

    private void getSmartGarden(final RoutingContext routingContext) {
        routingContext.response()
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("content-type", "application/json")
                .end(this.smartGardenMapper.to(this.actualGarden)
                        .encodePrettily());
    }

    private void handleAddNewSensorInput(final RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();
        final JsonObject res = routingContext.getBodyAsJson();
        if (res == null) {
            this.sendError(400, response);
            return;
        }
        try {
            final SensorBoardData value = this.sensorBoardDataMapper.from(res);
            this.values.addFirst(value);
            if (this.values.size() > MAX_SIZE) {
                this.values.removeLast();
            }
            this.logger.log(Type.INFO, "INPUT", "New value: " + value);
            this.selectStrategy(value);
            response.setStatusCode(200).end();
        } catch (final Exception e) {
            this.logger.log(Type.ERROR, "SERVICE", "Error receiving new sensors info");
            this.logger.log(Type.ERROR_INFO, "SERVICE", e.getMessage());
            this.sendError(500, response);
        }
    }

    private void selectStrategy(final SensorBoardData value) {
        final SmartGardenStrategy strategy = this.strategySelector
                .select(this.actualGarden, value);
        strategy.getOperations().forEach(o -> {
            this.logger.log(o.getLog());
            o.execute();
        });
        this.smartGardenOldStatus.add(strategy.getOldStatus());
        this.listeners.forEach(l -> l.accept(this.actualGarden));
    }

    private void handleGetData(final RoutingContext routingContext) {
        final JsonArray arr = new JsonArray();
        this.values.forEach(v -> arr.add(this.sensorBoardDataMapper.to(v)));
        routingContext.response()
                .putHeader("Access-Control-Allow-Origin", "*")
                .putHeader("content-type", "application/json")
                .end(arr.encodePrettily());
    }

    private void sendError(final int statusCode, final HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    public void addListener(final Consumer<SmartGarden> listener) {
        this.listeners.add(listener);
        listener.accept(this.actualGarden);
    }
}