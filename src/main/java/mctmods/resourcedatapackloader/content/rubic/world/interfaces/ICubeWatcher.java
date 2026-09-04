package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.interfaces.IXYZAddressable;

public interface ICubeWatcher extends IXYZAddressable {

    @Override int getX();

    @Override int getY();

    @Override int getZ();
}
