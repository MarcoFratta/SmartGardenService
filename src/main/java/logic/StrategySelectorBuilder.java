package logic;

import model.SensorBoardData;

public interface StrategySelectorBuilder {

    StrategySelector<SensorBoardData> sensorsDataSelector();

}
