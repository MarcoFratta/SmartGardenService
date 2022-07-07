package model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class MappersBuilderImpl implements MappersBuilder{
    @Override
    public Mapper<SmartGarden> smartGarden() {
        final var onOffMapper = this.onOffObject();
        final var intensityMapper = this.intensityObject();
        return new Mapper<>() {
            @Override
            public JsonObject toJson(final SmartGarden x) {
                final JsonArray onOffLamps = new JsonArray();
                final JsonArray intensityLamps = new JsonArray();
                x.getOnOffLamps().stream().map(onOffMapper::toJson).forEach(onOffLamps::add);
                x.getIntensityLamps().stream().map(intensityMapper::toJson).forEach(intensityLamps::add);
                return new JsonObject()
                        .put("state", x.getState())
                        .put("onOffValues", onOffLamps)
                        .put("intensityValues", intensityLamps)
                        .put("motor", intensityMapper.toJson(x.getIrrigationSystem()));
            }
            @Override
            public SmartGarden toObject(final JsonObject x) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Mapper<SensorBoardData> sensorBoardData() {
        return new Mapper<>() {
            @Override
            public JsonObject toJson(final SensorBoardData x) {
                return new JsonObject().put("temp", x.getTemperature())
                        .put("lum", x.getLuminosity());
            }

            @Override
            public SensorBoardData toObject(final JsonObject x) throws IllegalArgumentException {
                try {
                    final int temp = x.getInteger("temp");
                    final int lum = x.getInteger("lum");
                    final long time = System.currentTimeMillis();
                    return new SensorBoardDataImpl(temp, lum, time);
                } catch (final Exception e) {
                    throw new IllegalArgumentException("Invalid json object");
                }
            }
        };
    }

    @Override
    public Mapper<OnOffObject> onOffObject() {
        return new Mapper<>() {
            @Override
            public JsonObject toJson(final OnOffObject x) {
                return new JsonObject().put("state", x.getState().toString())
                .put("name", x.getName());
            }

            @Override
            public OnOffObject toObject(final JsonObject x) throws IllegalArgumentException {
                try {
                    final State state = State.valueOf(x.getString("state"));
                    final String name = x.getString("name");
                    return new OnOffObjectImpl(state, name);
                } catch (final Exception e) {
                    throw new IllegalArgumentException("Invalid json object");
                }
            }
        };
    }

    @Override
    public Mapper<IntensityObject> intensityObject() {
        final var onOffMapper = this.onOffObject();
        return new Mapper<>() {
            @Override
            public JsonObject toJson(final IntensityObject x) {
                return onOffMapper.toJson(x).put("intensity", x.getIntensity());
            }

            @Override
            public IntensityObject toObject(final JsonObject x) throws IllegalArgumentException {
                try {
                    final String name = x.getString("name");
                    final State state = State.valueOf(x.getString("state"));
                    final int intensity = x.getInteger("intensity");
                    final int max = x.getInteger("max");
                    final int min = x.getInteger("min");
                    return new IntensityObjectImpl(name, state, intensity, max, min);
                } catch (final Exception e) {
                    throw new IllegalArgumentException("Invalid json object");
                }
            }
        };
    }
}
