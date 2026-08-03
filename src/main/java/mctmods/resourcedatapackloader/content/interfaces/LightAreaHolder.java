package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.ContentLightArea;

public interface LightAreaHolder {
    ContentLightArea rdpl$lightArea();

    void rdpl$setLightArea(ContentLightArea area);

    boolean rdpl$quietLight();
}
