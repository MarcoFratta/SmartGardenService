package model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import logger.SingletonLogger;
import logger.Type;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappersBuilderImpl implements MappersBuilder{
    @Override
    public Mapper<JsonObject, SmartGarden> smartGarden() {
        final var onOffMapper = this.onOffObject();
        final var intensityMapper = this.intensityObject();
        return new Mapper<>() {
            @Override
            public JsonObject to(final SmartGarden x) {
                final JsonArray onOffLamps = new JsonArray();
                final JsonArray intensityLamps = new JsonArray();
                x.getOnOffLamps().stream().map(onOffMapper::to).forEach(onOffLamps::add);
                x.getIntensityLamps().stream().map(intensityMapper::to).forEach(intensityLamps::add);
                return new JsonObject()
                        .put("state", x.getState())
                        .put("onOffValues", onOffLamps)
                        .put("intensityValues", intensityLamps)
                        .put("motor", intensityMapper.to(x.getIrrigationSystem()));
            }

            @Override
            public SmartGarden from(final JsonObject x) {
                try {
                    final GardenState state = GardenState.valueOf(x.getString("s"));
                    final JsonArray intensityLamps = x.getJsonArray("i");
                    final JsonArray onOffLamps = x.getJsonArray("onOffValues");
                    final JsonObject motor = x.getJsonObject("motor");
                    return MappersBuilderImpl.this.buildGardenObj(state, onOffLamps, intensityLamps, motor, onOffMapper, intensityMapper);
                } catch (final Exception e) {
                    SingletonLogger.getLogger().log(Type.ERROR_INFO, "MAPPER", e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("invalid json object");
                }
            }
        };
    }

    @Override
    public Mapper<JsonObject, SmartGarden> smartGardenShort() {
        final var onOffMapper = this.onOffObjectShort();
        final var intensityMapper = this.intensityObjectShort();
        return new Mapper<>() {
            @Override
            public JsonObject to(final SmartGarden x) {
                final JsonArray onOffLamps = new JsonArray();
                final JsonArray intensityLamps = new JsonArray();
                x.getOnOffLamps().stream().map(onOffMapper::to).forEach(onOffLamps::add);
                x.getIntensityLamps().stream().map(intensityMapper::to).forEach(intensityLamps::add);
                return new JsonObject()
                        .put("s", x.getState().ordinal())
                        .put("a", onOffLamps)
                        .put("b", intensityLamps)
                        .put("c", intensityMapper.to(x.getIrrigationSystem()));
            }

            @Override
            public SmartGarden from(final JsonObject x) {
                try {
                    final GardenState state = GardenState.values()[x.getInteger("s")];
                    final JsonArray onOffLamps = x.getJsonArray("a");
                    final JsonArray intensityLamps = x.getJsonArray("b");
                    final JsonObject motor = x.getJsonObject("c");
                    return MappersBuilderImpl.this.buildGardenObj(state, onOffLamps, intensityLamps, motor, onOffMapper, intensityMapper);
                } catch (final Exception e) {
                    SingletonLogger.getLogger().log(Type.ERROR_INFO, "MAPPER", e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("invalid json object");
                }
            }
        };
    }

    private SmartGarden buildGardenObj(final GardenState state, final JsonArray onOffLamps,
                                       final JsonArray intensityLamps,
                                       final JsonObject motor,
                                       final Mapper<JsonObject, OnOffObject> onOffMapper,
                                       final Mapper<JsonObject, IntensityObject> intensityMapper) {
        return new SmartGardenImpl(Stream.iterate(0, t -> t += 1)
                .limit(onOffLamps.size())
                .map(i -> onOffMapper.from(onOffLamps.getJsonObject(i)))
                .collect(Collectors.toList()), Stream.iterate(0, t -> t += 1)
                .limit(intensityLamps.size())
                .map(i -> intensityMapper.from(intensityLamps.getJsonObject(i)))
                .collect(Collectors.toList()), intensityMapper.from(motor),
                state);
    }

    @Override
    public Mapper<JsonObject, SensorBoardData> sensorBoardData() {
        return new Mapper<>() {
            @Override
            public JsonObject to(final SensorBoardData x) {
                return new JsonObject().put("temp", x.getTemperature())
                        .put("lum", x.getLuminosity());
            }

            @Override
            public SensorBoardData from(final JsonObject x) throws IllegalArgumentException {
                try {
                    final int temp = x.getInteger("temp");
                    final int lum = x.getInteger("lum");
                    final long time = System.currentTimeMillis();
                    return new SensorBoardDataImpl(temp, lum, time);
                } catch (final Exception e) {
                    SingletonLogger.getLogger().log(Type.ERROR_INFO, "MAPPER", e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("Invalid json object");
                }
            }
        };
    }

    @Override
    public Mapper<JsonObject, OnOffObject> onOffObject() {
        return new Mapper<>() {
            @Override
            public JsonObject to(final OnOffObject x) {
                return new JsonObject().put("state", x.getState().toString())
                        .put("name", x.getName());
            }

            @Override
            public OnOffObject from(final JsonObject x) throws IllegalArgumentException {
                try {
                    final State state = State.valueOf(x.getString("state"));
                    final String name = x.getString("name");
                    return new OnOffObjectImpl(state, name);
                } catch (final Exception e) {
                    SingletonLogger.getLogger().log(Type.ERROR_INFO, "MAPPER", e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("Invalid json object");
                }
            }
        };
    }

    @Override
    public Mapper<JsonObject, IntensityObject> intensityObject() {
        final var onOffMapper = this.onOffObject();
        return new Mapper<>() {
            @Override
            public JsonObject to(final IntensityObject x) {
                return onOffMapper.to(x).put("intensity", x.getIntensity())
                        .put("max", x.getMaxIntensity()).put("min", x.getMinIntensity());
            }

            @Override
            public IntensityObject from(final JsonObject x) throws IllegalArgumentException {
                try {
                    final String name = x.getString("name");
                    final State state = State.valueOf(x.getString("state"));
                    final int intensity = x.getInteger("intensity");
                    final int max = x.getInteger("max");
                    final int min = x.getInteger("min");
                    return new IntensityObjectImpl(name, state, intensity, max, min);
                } catch (final Exception e) {
                    SingletonLogger.getLogger().log(Type.ERROR_INFO, "MAPPER", e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("Invalid json object");
                }
            }
        };
    }

    @Override
    public Mapper<JsonObject, OnOffObject> onOffObjectShort() {
        return new Mapper<>() {
            @Override
            public JsonObject to(final OnOffObject x) {
                return new JsonObject().put("s", x.getState().equals(State.ON))
                        .put("n", x.getName());
            }

            @Override
            public OnOffObject from(final JsonObject x) throws IllegalArgumentException {
                try {
                    final State state = x.getBoolean("s") ? State.ON : State.OFF;
                    final String name = x.getString("n");
                    return new OnOffObjectImpl(state, name);
                } catch (final Exception e) {
                    SingletonLogger.getLogger().log(Type.ERROR_INFO, "MAPPER", e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("Invalid json object");
                }
            }
        };
    }

    @Override
    public Mapper<JsonObject, IntensityObject> intensityObjectShort() {
        final var onOffMapper = this.onOffObjectShort();
        return new Mapper<>() {
            @Override
            public JsonObject to(final IntensityObject x) {
                return onOffMapper.to(x).put("i", x.getIntensity())
                        .put("x", x.getMaxIntensity()).put("y", x.getMinIntensity());
            }

            @Override
            public IntensityObject from(final JsonObject x) throws IllegalArgumentException {
                try {
                    final String name = x.getString("n");
                    final State state = x.getBoolean("s") ? State.ON : State.OFF;
                    final int intensity = x.getInteger("i");
                    final int max = x.getInteger("x");
                    final int min = x.getInteger("y");
                    return new IntensityObjectImpl(name, state, intensity, max, min);
                } catch (final Exception e) {
                    SingletonLogger.getLogger().log(Type.ERROR_INFO, "MAPPER", e.getMessage());
                    e.printStackTrace();
                    throw new IllegalArgumentException("Invalid json object");
                }
            }
        };
    }

    @Override
    public Mapper<String, GardenState> smartGardenState() {
        return new Mapper<>() {
            @Override
            public String to(final GardenState x) {
                return x.toString();
            }

            @Override
            public GardenState from(final String x) throws IllegalArgumentException {
                return GardenState.valueOf(x);
            }
        };
    }
}
