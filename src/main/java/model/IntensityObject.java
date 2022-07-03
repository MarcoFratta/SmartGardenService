package model;

public interface IntensityObject extends OnOffObject {
    int getIntensity();
    void setIntensity(int intensity);
    int getMaxIntensity();
    int getMinIntensity();
}
