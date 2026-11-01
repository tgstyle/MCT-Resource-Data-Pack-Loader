package mctmods.resourcedatapackloader.common.rubic.server;

import mctmods.resourcedatapackloader.common.rubic.server.chunkio.async.CubeIoQueue;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.network.world.ColumnMessage;
import mctmods.resourcedatapackloader.core.network.world.HeightMapUpdateMessage;
import mctmods.resourcedatapackloader.core.network.world.UnloadColumnMessage;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.address.AddressTools;
import mctmods.resourcedatapackloader.core.util.address.IBucketSorterEntry;
import mctmods.resourcedatapackloader.core.util.address.IXZAddressable;

import java.util.BitSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class ColumnWatcher extends PlayerChunkMapEntry implements IXZAddressable, IBucketSorterEntry {
    @Nonnull private final PlayerCubeMap playerCubeMap;
    @Nonnull private final BitSet dirtyColumns = new BitSet(256);

    ColumnWatcher(@Nonnull PlayerCubeMap playerCubeMap, ChunkPos pos) {
        super(playerCubeMap, pos.x, pos.z);
        this.playerCubeMap = playerCubeMap;
    }

    @Override public boolean providePlayerChunk(boolean canGenerate) {
        if (this.loading) { return false; }
        if (this.chunk != null) { return true; }
        if (canGenerate) {
            Chunk provided = this.playerCubeMap.getWorldServer().getChunkProvider().provideChunk(this.pos.x, this.pos.z);
            if (provided.isEmpty()) { return false; }
            this.chunk = provided;
        }
        else { this.chunk = this.playerCubeMap.getWorldServer().getChunkProvider().loadChunk(this.pos.x, this.pos.z); }
        return this.chunk != null;
    }

    @Override public void addPlayer(@Nonnull EntityPlayerMP player) {
        if (this.players.contains(player)) {
            ContentLog.LOGGER.debug("Failed to expand player. {} already is in chunk {}, {}", player,
                    this.getPos().x,
                    this.getPos().z);
            return;
        }
        if (this.players.isEmpty()) { this.lastUpdateInhabitedTime = playerCubeMap.getWorldServer().getTotalWorldTime(); }
        this.players.add(player);
        if (this.isSentToPlayers()) {
            Chunk chunk = Objects.requireNonNull(this.getChunk());
            RDPLNetwork.sendTo(new ColumnMessage(chunk), player);
            MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(chunk, player));
        }
    }

    @Override public void removePlayer(@Nonnull EntityPlayerMP player) {
        if (!this.players.contains(player)) { return; }
        if (this.getChunk() == null) {
            this.players.remove(player);
            if (this.players.isEmpty()) {
                if (this.loading) { CubeIoQueue.dropQueuedColumnLoad( playerCubeMap.getWorldServer(), getPos().x, getPos().z, (c) -> this.loadedRunnable.run()); }
                this.playerCubeMap.removeEntry(this);
            }
            return;
        }
        if (this.isSentToPlayers()) { RDPLNetwork.sendTo(new UnloadColumnMessage(getPos()), player); }
        this.players.remove(player);
        MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(this.getChunk(), player));
        if (this.players.isEmpty()) { playerCubeMap.removeEntry(this); }
    }

    @Override public boolean sendToPlayers() {
        if (this.isSentToPlayers()) { return true; }
        if (getChunk() == null) { return false; }
        try {
            ColumnMessage message = new ColumnMessage(this.getChunk());
            for (EntityPlayerMP player : this.players) {
                RDPLNetwork.sendTo(message, player);
                MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(this.getChunk(), player));
            }
            this.sentToPlayers = true;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        return true;
    }

    @Override @Deprecated public void sendToPlayer(@Nonnull EntityPlayerMP player) {}

    @Override @Deprecated public void blockChanged(int x, int y, int z) {
        CubeWatcher watcher = playerCubeMap.getCubeWatcher(Coords.blockToCube(x), Coords.blockToCube(y), Coords.blockToCube(z));
        if (watcher != null) { watcher.blockChanged(x, y, z); }
    }

    @Override public void update() {
        if (!this.isSentToPlayers()) { return; }
        if (this.dirtyColumns.isEmpty()) { return; }
        assert getChunk() != null;
        HeightMapUpdateMessage update = new HeightMapUpdateMessage(dirtyColumns, getChunk());
        for (EntityPlayerMP player : this.players) { RDPLNetwork.sendTo(update, player); }
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
