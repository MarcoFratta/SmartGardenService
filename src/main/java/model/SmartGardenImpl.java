package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SmartGardenImpl implements SmartGarden{

    private final List<OnOffObject> onOffLamps;
    private final List<IntensityObject> intensityLamps;
    private final IntensityObject motor;
    private GardenState state;

    public SmartGardenImpl(final List<OnOffObject> onOffLamps, final List<IntensityObject> intensityLamps,
                           final IntensityObject motor, final GardenState state) {
        this.onOffLamps = onOffLamps.stream().map(OnOffObjectImpl::new).collect(Collectors.toList());
        this.intensityLamps = intensityLamps.stream().map(IntensityObjectImpl::new).collect(Collectors.toList());
        this.motor = new IntensityObjectImpl(motor);
        this.state = state;
    }

    public SmartGardenImpl(final SmartGarden garden) {
        this(garden.getOnOffLamps(), garden.getIntensityLamps(), garden.getIrrigationSystem(), garden.getState());
    }

    public SmartGardenImpl() {
        this(new ArrayList<>(), new ArrayList<>(),
                new IntensityObjectImpl(0, 5, "Motor"), GardenState.AUTO);
    }


    @Override
    public List<OnOffObject> getOnOffLamps() {
        return this.onOffLamps;
    }

    @Override
    public List<IntensityObject> getIntensityLamps() {
        return this.intensityLamps;
    }

    @Override
    public IntensityObject getIrrigationSystem() {
        return this.motor;
    }

    @Override
    public GardenState getState() {
        return this.state;
    }

    @Override
    public void setState(final GardenState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "SmartGardenImpl{" +
                "onOffLamps=" + this.onOffLamps +
                ", intensityLamps=" + this.intensityLamps +
                ", motor=" + this.motor +
                ", state=" + this.state +
                '}';
    }
}
