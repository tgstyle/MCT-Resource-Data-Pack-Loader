package mctmods.resourcedatapackloader.common.rubic.world.interfaces;

import mctmods.resourcedatapackloader.core.integration.CompatHandler;

public interface IMinMaxHeight {
    default int rdpl$getMinHeight() { return 0; }
    default int rdpl$getMaxHeight() { return CompatHandler.VANILLA_HEIGHT; }
}
