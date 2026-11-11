package mctmods.resourcedatapackloader.common.blocks.shield;

import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ShieldEvents {
    private ShieldEvents() {}

    @SubscribeEvent public static void onChunkLoad(ChunkEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || GenHeights.rubic(world)) { return; }
        ShieldIndexes.scan(world, event.getChunk());
    }

    @SubscribeEvent public static void onChunkUnload(ChunkEvent.Unload event) {
        World world = event.getWorld();
        if (GenHeights.rubic(world)) { return; }
        ShieldIndexes.drop(world, event.getChunk().x, event.getChunk().z);
    }
}
