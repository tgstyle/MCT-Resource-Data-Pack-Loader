package mctmods.resourcedatapackloader.content.rubic.entity;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IRubicEntityTracker {
    void sendLeashedEntitiesInCube(EntityPlayerMP player, ICube cube);

    void setVertViewDistance(int viewDistance);

    interface IEntry { void setMaxVertRange(int maxVertTrackingDistanceThreshold); }
}
