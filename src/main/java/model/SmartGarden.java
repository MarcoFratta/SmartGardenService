package model;

import java.util.List;

public interface SmartGarden {
    List<OnOffObject> getOnOffLamps();
    List<IntensityObject> getIntensityLamps();
    IntensityObject getIrrigationSystem();
    GardenState getState();
    void setState(GardenState state);
}
