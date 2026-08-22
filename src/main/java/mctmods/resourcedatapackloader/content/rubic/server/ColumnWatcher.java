package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.rubic.server.chunkio.async.CubeIoQueue;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IPlayerChunkMapEntry;
import mctmods.resourcedatapackloader.network.MessageColumn;
import mctmods.resourcedatapackloader.network.MessageHeightMapUpdate;
import mctmods.resourcedatapackloader.network.MessageUnloadColumn;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.AddressTools;
import mctmods.resourcedatapackloader.util.interfaces.IBucketSorterEntry;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.interfaces.IXZAddressable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;
import java.util.BitSet;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ColumnWatcher extends PlayerChunkMapEntry implements IXZAddressable, IBucketSorterEntry {
    @Nonnull private final PlayerCubeMap playerCubeMap;
    @Nonnull private final BitSet dirtyColumns = new BitSet(256);

    ColumnWatcher(@Nonnull PlayerCubeMap playerCubeMap, ChunkPos pos) {
        super(playerCubeMap, pos.x, pos.z);
        this.playerCubeMap = playerCubeMap;
    }

    private IPlayerChunkMapEntry self() { return (IPlayerChunkMapEntry) this; }

    @Override public boolean providePlayerChunk(boolean canGenerate) {
        if (self().isLoading()) { return false; }
        if (self().getChunk() != null) { return true; }
        if (canGenerate) {
            Chunk chunk = this.playerCubeMap.getWorldServer().getChunkProvider().provideChunk(self().getPos().x, self().getPos().z);
            if (chunk.isEmpty()) { return false; }
            self().setChunk(chunk);
        }
        else { self().setChunk(this.playerCubeMap.getWorldServer().getChunkProvider().loadChunk(self().getPos().x, self().getPos().z)); }
        return self().getChunk() != null;
    }

    @Override public void addPlayer(@Nonnull EntityPlayerMP player) {
        if (self().getPlayerList().contains(player)) {
            ContentLog.LOGGER.debug("Failed to expand player. {} already is in chunk {}, {}", player,
                    this.getPos().x,
                    this.getPos().z);
            return;
        }
        if (self().getPlayerList().isEmpty()) { self().setLastUpdateInhabitedTime(playerCubeMap.getWorldServer().getTotalWorldTime()); }
        self().getPlayerList().add(player);
        if (this.isSentToPlayers()) {
            Chunk chunk = Objects.requireNonNull(this.getChunk());
            RDPLNetwork.sendTo(new MessageColumn(chunk), player);
            MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(chunk, player));
        }
    }

    @Override public void removePlayer(@Nonnull EntityPlayerMP player) {
        if (!self().getPlayerList().contains(player)) { return; }
        if (this.getChunk() == null) {
            self().getPlayerList().remove(player);
            if (self().getPlayerList().isEmpty()) {
                if (self().isLoading()) {
                    CubeIoQueue.dropQueuedColumnLoad(
                            playerCubeMap.getWorldServer(), getPos().x, getPos().z, (c) -> self().getLoadedRunnable().run());
                }
                this.playerCubeMap.removeEntry(this);
            }
            return;
        }
        if (this.isSentToPlayers()) { RDPLNetwork.sendTo(new MessageUnloadColumn(getPos()), player); }
        self().getPlayerList().remove(player);
        MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(this.getChunk(), player));
        if (self().getPlayerList().isEmpty()) { playerCubeMap.removeEntry(this); }
    }

    @Override public boolean sendToPlayers() {
        if (this.isSentToPlayers()) { return true; }
        if (getChunk() == null) { return false; }
        try {
            MessageColumn message = new MessageColumn(this.getChunk());
            for (EntityPlayerMP player : self().getPlayerList()) { RDPLNetwork.sendTo(message, player); }
            self().setSentToPlayers(true);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        return true;
    }

    @Override @Deprecated public void sendToPlayer(@Nonnull EntityPlayerMP player) {
    }

    @Override @Deprecated public void blockChanged(int x, int y, int z) {
        CubeWatcher watcher = playerCubeMap.getCubeWatcher(CubePos.fromBlockCoords(x, y, z));
        if (watcher != null) { watcher.blockChanged(x, y, z); }
    }

    @Override public void update() {
        if (!this.isSentToPlayers()) { return; }
        if (this.dirtyColumns.isEmpty()) { return; }
        assert getChunk() != null;
        for (EntityPlayerMP player : self().getPlayerList()) { RDPLNetwork.sendTo(new MessageHeightMapUpdate(dirtyColumns, getChunk()), player); }
        this.dirtyColumns.clear();
    }

    @Override public int getX() { return this.getPos().x; }

    @Override public int getZ() { return this.getPos().z; }

    void heightChanged(int localX, int localZ) {
        if (!isSentToPlayers()) { return; }
        if (this.dirtyColumns.isEmpty()) { playerCubeMap.addToUpdateEntry(this); }
        this.dirtyColumns.set(AddressTools.getLocalAddress(localX, localZ));
    }

    private long[] bucketDataEntry = null;

    @Override public long[] getBucketEntryData() { return bucketDataEntry; }

    @Override public void setBucketEntryData(long[] data) { bucketDataEntry = data; }
}
