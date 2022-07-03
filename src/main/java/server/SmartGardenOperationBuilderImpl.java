package server;

import model.*;

public class SmartGardenOperationBuilderImpl implements SmartGardenOperationBuilder{
    @Override
    public SmartGardenOperation setLampLuminosity(final IntensityObject lamp, final int value) {
        return new AbstractOperation(lamp.getName() + " -> Setting intensity: " + lamp.getIntensity() +
                " -> " + value, () -> lamp.setIntensity(value));
    }

    @Override
    public SmartGardenOperation switchLampState(final OnOffObject lamp, final State state) {
        return new AbstractOperation(lamp.getName() + " -> Switching state: " + lamp.getState().toString().toLowerCase() +
                " -> " + state.toString().toLowerCase() ,
                () -> lamp.switchState(state));
    }

    @Override
    public SmartGardenOperation switchIrrigationState(final OnOffObject motor, final State state) {
        return this.switchLampState(motor, state);
    }

    @Override
    public SmartGardenOperation setIrrigationSpeed(final IntensityObject motor, final int speed) {
        return new AbstractOperation(motor.getName() + " -> Setting speed: " + motor.getIntensity() +
                " -> " + speed, () -> motor.setIntensity(speed));
    }

    @Override
    public SmartGardenOperation switchGardenState(final SmartGarden garden, final GardenState state) {
        return new AbstractOperation("Smart garden entering " + state.toString().toLowerCase() + " mode",
                () -> garden.setState(state));
    }
}
