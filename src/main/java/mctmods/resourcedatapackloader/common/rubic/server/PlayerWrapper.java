package mctmods.resourcedatapackloader.common.rubic.server;

import static mctmods.resourcedatapackloader.common.util.world.Coords.blockToCube;
import mctmods.resourcedatapackloader.common.util.world.CubePos;

import net.minecraft.entity.player.EntityPlayerMP;

final class PlayerWrapper {
    final EntityPlayerMP playerEntity;
    double managedPosY;

    PlayerWrapper(EntityPlayerMP player) { this.playerEntity = player; }

    void updateManagedPos() {
        this.playerEntity.managedPosX = playerEntity.posX;
        this.managedPosY = playerEntity.posY;
        this.playerEntity.managedPosZ = playerEntity.posZ;
    }

    int getManagedCubePosX() { return blockToCube(this.playerEntity.managedPosX); }

    int getManagedCubePosY() { return blockToCube(this.managedPosY); }

    int getManagedCubePosZ() { return blockToCube(this.playerEntity.managedPosZ); }

    CubePos getManagedCubePos() { return new CubePos(getManagedCubePosX(), getManagedCubePosY(), getManagedCubePosZ()); }

    boolean cubePosChanged() {
        return blockToCube(playerEntity.posX) != this.getManagedCubePosX()
                || blockToCube(playerEntity.posY) != this.getManagedCubePosY()
                || blockToCube(playerEntity.posZ) != this.getManagedCubePosZ();
    }
}
