package mctmods.resourcedatapackloader.content.interfaces;

import mctmods.resourcedatapackloader.content.worldgen.ContentLightArea;


public interface ILightAreaHolder {
    ContentLightArea rdpl$lightArea();

    void rdpl$setLightArea(ContentLightArea area);

    boolean rdpl$quietLight();
}
