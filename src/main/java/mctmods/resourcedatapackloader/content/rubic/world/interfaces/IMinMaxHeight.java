package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.compat.CompatHandler;

public interface IMinMaxHeight {
    default int rdpl$getMinHeight() { return 0; }
    default int rdpl$getMaxHeight() { return CompatHandler.VANILLA_HEIGHT; }
}
