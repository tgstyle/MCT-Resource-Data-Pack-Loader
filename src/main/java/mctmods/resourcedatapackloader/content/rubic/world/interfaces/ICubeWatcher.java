package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.interfaces.IXYZAddressable;

import javax.annotation.Nullable;

public interface ICubeWatcher extends IXYZAddressable {
    boolean isSentToPlayers();

    @Nullable ICube getCube();

    @Override int getX();

    @Override int getY();

    @Override int getZ();
}
