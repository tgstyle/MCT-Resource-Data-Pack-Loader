package mctmods.resourcedatapackloader.common.environment;

public interface ILightAreaHolder {
    ContentLightArea rdpl$lightArea();

    void rdpl$setLightArea(ContentLightArea area);

    boolean rdpl$quietLight();
}
