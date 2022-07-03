package server;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class StrategySelectorImpl implements StrategySelector{

    @Override
    public SmartGardenStrategy select(final SmartGarden garden, final InputData input) {
        final SmartGardenOperationBuilder builder = new SmartGardenOperationBuilderImpl();
        final SmartGarden tmp = new SmartGardenImpl(garden);
        final List<SmartGardenOperation> operationList = new ArrayList<>();
        if(input.getLuminosity() < 5){
            garden.getOnOffLamps().forEach(l -> operationList.add(builder.switchLampState(l, State.ON)));
            garden.getIntensityLamps().forEach(l -> operationList.add(builder.setLampLuminosity(l, input.getLuminosity())));
        }
        if(input.getLuminosity() < 2 ){
            operationList.add(builder.switchIrrigationState(garden.getIrrigationSystem(), State.ON));
            if(input.getTemperature() >= 0 && input.getTemperature() <= 5){
                operationList.add(builder.setIrrigationSpeed(garden.getIrrigationSystem(), input.getTemperature()));
            }
        }

        if(input.getTemperature() == 5 && garden.getIrrigationSystem().getState() == State.OFF){
            operationList.add(builder.switchGardenState(garden, GardenState.ALARM));
        }
     return new SmartGardenStrategyImpl(tmp,operationList);
    }
}
