package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(PlayerChunkMapEntry.class) public interface IPlayerChunkMapEntry {
    @Accessor("players") List<EntityPlayerMP> getPlayerList();
    @Accessor void setLastUpdateInhabitedTime(long time);
    @Accessor void setSentToPlayers(boolean value);
    @Accessor(remap = false) boolean isLoading();
    @Accessor(remap = false) Runnable getLoadedRunnable();
    @Accessor Chunk getChunk();
    @Accessor void setChunk(Chunk chunk);
    @Accessor ChunkPos getPos();
}
