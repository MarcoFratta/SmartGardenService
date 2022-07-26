package model;

public interface Mapper<X, T> {
    X to(T x);

    T from(X x) throws IllegalArgumentException;
}
