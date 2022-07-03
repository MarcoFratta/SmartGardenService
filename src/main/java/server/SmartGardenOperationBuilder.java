package server;

import model.*;

public interface SmartGardenOperationBuilder {
    SmartGardenOperation setLampLuminosity(IntensityObject lamp, int value);
    SmartGardenOperation switchLampState(OnOffObject lamp, State state);
    SmartGardenOperation switchIrrigationState(OnOffObject motor, State state);
    SmartGardenOperation setIrrigationSpeed(IntensityObject motor, int velocity);
    SmartGardenOperation switchGardenState(SmartGarden garden, GardenState alarm);
}
