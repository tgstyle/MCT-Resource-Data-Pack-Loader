package mctmods.resourcedatapackloader.content.rubic.world.interfaces;


public interface IMinMaxHeight {
    default int rdpl$getMinHeight() { return 0; }
    default int rdpl$getMaxHeight() { return 256; }
}
