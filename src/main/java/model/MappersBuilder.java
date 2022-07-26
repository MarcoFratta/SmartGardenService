package model;

import io.vertx.core.json.JsonObject;

public interface MappersBuilder {
    Mapper<JsonObject, SmartGarden> smartGarden();

    Mapper<JsonObject, SmartGarden> smartGardenShort();

    Mapper<JsonObject, SensorBoardData> sensorBoardData();

    Mapper<JsonObject, OnOffObject> onOffObject();

    Mapper<JsonObject, IntensityObject> intensityObject();

    Mapper<JsonObject, OnOffObject> onOffObjectShort();

    Mapper<JsonObject, IntensityObject> intensityObjectShort();

    Mapper<String, GardenState> smartGardenState();
}
