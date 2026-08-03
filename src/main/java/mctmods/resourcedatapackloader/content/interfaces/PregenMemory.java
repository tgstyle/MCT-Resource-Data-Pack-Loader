package mctmods.resourcedatapackloader.content.interfaces;

public interface PregenMemory {
    int rdpl$landMadeTo(int dimension);

    void rdpl$setLandMadeTo(int dimension, int radius);

    int rdpl$landMadeAt(int dimension);

    void rdpl$setLandMadeAt(int dimension, int reached);
}
