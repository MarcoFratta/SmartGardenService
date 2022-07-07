package server;

import model.SmartGarden;

@FunctionalInterface
public interface StrategySelector<T> {
    SmartGardenStrategy select(SmartGarden garden, T input);
}
