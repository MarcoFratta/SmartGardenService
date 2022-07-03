package model;

public interface OnOffObject {
    void switchState(State state);
    State getState();
    String getName();
}
