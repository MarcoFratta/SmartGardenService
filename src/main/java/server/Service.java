package server;

import java.util.function.Consumer;

public interface Service<T> {

    void addListener(Consumer<T> action);
}
