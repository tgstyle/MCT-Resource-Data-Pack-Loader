package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ContentChunkSaves {
    private static volatile int waiting;
    private static volatile int mark = -1;

    private ContentChunkSaves() {}

    public static boolean hurry() {
        int limit = mark;
        return limit > 0 && waiting >= limit;
    }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        if (mark < 0) {
            mark = Math.max(0, ContentControl.number(ContentControl.CHUNKS, "hurryWritesAbove", Config.chunks.hurryWritesAbove));
            if (mark > 0) { Summary.info("chunks.hurry", "Writing chunks out without pausing whenever more than " + mark + " are waiting"); }
        }
        int pending = 0;
        for (WorldServer world : server.worlds) {
            ChunkProviderServer provider = world.getChunkProvider();
            if (provider.chunkLoader instanceof AnvilChunkLoader) { pending += ((AnvilChunkLoader) provider.chunkLoader).getPendingSaveCount(); }
        }
        waiting = pending;
    }
}
