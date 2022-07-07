package server;

import model.SensorBoardData;

public interface StrategySelectorBuilder {

    StrategySelector<SensorBoardData> sensorsDataSelector();

}
