package model;

public interface MappersBuilder {
    Mapper<SmartGarden> smartGarden();
    Mapper<SensorBoardData> sensorBoardData();
    Mapper<OnOffObject> onOffObject();
    Mapper<IntensityObject> intensityObject();
}
