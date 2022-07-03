package model;

public class InputDataImpl implements InputData{

    private static final int MAX_TEMP = 5;
    private static final int MAX_LUM = 8;

    private final int temperature;
    private final int luminosity;
    private final long time;

    public InputDataImpl(final int temperature, final int luminosity, final long time) throws IllegalArgumentException{
        if(temperature > MAX_TEMP || luminosity > MAX_LUM){
            throw new IllegalArgumentException("Invalid values");
        }
        this.temperature = temperature;
        this.luminosity = luminosity;
        this.time = time;
    }

    @Override
    public int getTemperature() {
        return this.temperature;
    }

    @Override
    public int getLuminosity() {
        return this.luminosity;
    }

    @Override
    public long getTimeStamp() {
        return this.time;
    }

    @Override
    public String toString() {
        return "( temperature =" + this.temperature +
                ", luminosity=" + this.luminosity +
                " )";
    }
}
