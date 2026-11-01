package mctmods.resourcedatapackloader.common.rubic.world;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CubeWatchEvent extends Event {
    @Nullable private final ICube cube;
    private final EntityPlayerMP player;

    public CubeWatchEvent(@Nullable ICube cubeIn, EntityPlayerMP playerIn) {
        super();
        cube = cubeIn;
        player = playerIn;
    }

    @Nullable public ICube getCube() { return cube; }

    public EntityPlayerMP getPlayer() { return player; }
}
