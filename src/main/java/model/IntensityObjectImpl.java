package model;

public class IntensityObjectImpl extends OnOffObjectImpl implements IntensityObject {
    private final int maxIntensity;
    private final int minIntensity;
    private int intensity;

    public IntensityObjectImpl(final int minIntensity, final int maxIntensity, final String name) {
        super(State.ON, name);
        this.maxIntensity = maxIntensity;
        this.minIntensity = minIntensity;
        this.intensity = 0;
    }

    public IntensityObjectImpl(final IntensityObject motor) {
        this(motor.getMinIntensity(), motor.getMaxIntensity(), motor.getName());
        this.intensity = motor.getIntensity();
    }


    @Override
    public int getIntensity() {
        return this.intensity;
    }

    @Override
    public void setIntensity(final int intensity) {
        if(this.getState().equals(State.ON) &&
                intensity <= this.maxIntensity &&
                intensity >= this.minIntensity){
            this.intensity = intensity;
        }
    }

    @Override
    public int getMaxIntensity() {
        return this.maxIntensity;
    }

    @Override
    public int getMinIntensity() {
        return this.minIntensity;
    }
}
