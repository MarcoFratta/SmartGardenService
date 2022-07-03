package server;

import model.InputData;
import model.SmartGarden;

@FunctionalInterface
public interface StrategySelector {
    SmartGardenStrategy select(SmartGarden garden, InputData input);
}
