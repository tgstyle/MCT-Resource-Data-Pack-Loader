package mctmods.resourcedatapackloader.core.optifine.interfaces;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;


public interface IOptifineRenderChunk {
    ICube getCube();

    boolean isRubic();

    int getRegionX();

    int getRegionY();
}
