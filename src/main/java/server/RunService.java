package server;

import io.vertx.core.Vertx;
import model.IntensityObjectImpl;
import model.OnOffObjectImpl;
import model.SmartGardenImpl;

import java.util.List;

/*
 * Data Service as a vertx event-loop
 */
public class RunService {


    public static void main(final String[] args) {
        final Vertx vertx = Vertx.vertx();
        final var garden = new SmartGardenImpl(
                List.of(new OnOffObjectImpl("Lamp 1"), new OnOffObjectImpl("Lamp 2")),
                List.of(new IntensityObjectImpl(0, 4, "Lamp 3"),
                        new IntensityObjectImpl(0, 4, "Lamp 4")),
                new IntensityObjectImpl(0, 4, "Irrigation system"));
        final GardenService service = new GardenService(8080, garden);
        vertx.deployVerticle(service);
    }
}