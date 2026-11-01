package mctmods.resourcedatapackloader.core.integration.optifine;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;


public interface IOptifineRenderChunk {
    ICube getCube();

    boolean isRubic();

    int getRegionX();

    int getRegionY();
}
