package mctmods.resourcedatapackloader.common.blocks.shield;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.event.level.ChunkEvent;

public final class ShieldEvents {
    private ShieldEvents() {}

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof Level level && event.getChunk() instanceof LevelChunk chunk) { ShieldIndexes.scan(level, chunk); }
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof Level level) { ShieldIndexes.drop(level, event.getChunk().getPos()); }
    }
}
