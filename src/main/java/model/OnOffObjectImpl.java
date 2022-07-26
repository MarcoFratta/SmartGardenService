package model;

public class OnOffObjectImpl implements OnOffObject {

    private State state;
    private final String name;

    public OnOffObjectImpl(final State state, final String name) {
        this.state = state;
        this.name = name;
    }

    public OnOffObjectImpl(final String name) {
        this(State.OFF,name);
    }

    public OnOffObjectImpl(final OnOffObject onOffObject) {
        this(onOffObject.getState(), onOffObject.getName());
    }

    @Override
    public void switchState(final State state) {
        this.state = state;
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "OnOffObjectImpl{" +
                "state=" + this.state +
                ", name='" + this.name + '\'' +
                '}';
    }
}
