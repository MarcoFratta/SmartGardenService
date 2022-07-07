package model;

import io.vertx.core.json.JsonObject;

public interface Mapper<T> {
    JsonObject toJson(T x);
    T toObject(JsonObject x) throws IllegalArgumentException;
}
