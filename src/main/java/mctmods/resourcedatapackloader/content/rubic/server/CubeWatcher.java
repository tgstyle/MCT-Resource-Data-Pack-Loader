package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.rubic.entity.IRubicEntityTracker;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.async.CubeIoQueue;
import mctmods.resourcedatapackloader.content.rubic.world.CubeUnWatchEvent;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeWatcher;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.network.MessageCubeBlockChange;
import mctmods.resourcedatapackloader.network.MessageUnloadCube;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.AddressTools;
import mctmods.resourcedatapackloader.util.interfaces.IBucketSorterEntry;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.interfaces.ITicket;

import com.google.common.base.Predicate;
import gnu.trove.list.TShortList;
import gnu.trove.list.array.TShortArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class CubeWatcher implements ITicket, ICubeWatcher, IBucketSorterEntry {
    private final CubeProviderServer cubeCache;
    private final PlayerCubeMap playerCubeMap;
    @Nullable private Cube cube;
    private final ObjectArrayList<EntityPlayerMP> players = ObjectArrayList.wrap(new EntityPlayerMP[0]);
    private final ObjectArrayList<EntityPlayerMP> playersToAdd = ObjectArrayList.wrap(new EntityPlayerMP[1], 0);
    private final TShortList dirtyBlocks = new TShortArrayList(64);
    private final CubePos cubePos;
    private long previousWorldTime = 0;
    private boolean sentToPlayers = false;
    private boolean loading = true;
    private boolean invalid = false;

    CubeWatcher(PlayerCubeMap playerCubeMap, CubePos cubePos) {
        this.cubePos = cubePos;
        this.playerCubeMap = playerCubeMap;
        this.cubeCache = ((IRubicWorldInternal.Server) playerCubeMap.getWorldServer()).rdpl$getCubeCache();
        Consumer<Cube> consumer = (c) -> {
            if (this.invalid) { return; }
            this.cube = c;
            this.loading = false;
            if (this.cube != null) { this.cube.getTickets().add(this); }
        };
        this.cubeCache.asyncGetCube(
                cubePos.getX(), cubePos.getY(), cubePos.getZ(),
                ICubeProviderServer.Requirement.LOAD,
                consumer);
    }

    void scheduleAddPlayer(EntityPlayerMP player) {
        if (!playersToAdd.contains(player)) { playersToAdd.add(player); }
    }

    void removeScheduledAddPlayer(EntityPlayerMP player) { playersToAdd.rem(player); }

    void addScheduledPlayers() {
        if (!playersToAdd.isEmpty()) {
            for (EntityPlayerMP player : playersToAdd.elements()) {
                if (player == null) { break; }
                addPlayer(player);
            }
            playersToAdd.clear();
        }
    }

    void addPlayer(EntityPlayerMP player) {
        if (this.players.contains(player)) {
            ContentLog.LOGGER.debug("Failed to add player. {} already is in cube at {}", player, cubePos);
            return;
        }
        if (this.players.isEmpty()) { this.previousWorldTime = this.getWorldTime(); }
        this.players.add(player);
        if (this.sentToPlayers) {
            this.sendToPlayer(player);
            ((IRubicEntityTracker) playerCubeMap.getWorldServer().getEntityTracker())
                    .sendLeashedEntitiesInCube(player, this.getCube());
        }
    }

    void removePlayer(EntityPlayerMP player) {
        if (!this.players.contains(player)) {
            removeScheduledAddPlayer(player);
            if (this.players.isEmpty()) { playerCubeMap.removeEntry(this); }
            return;
        }
        Cube cube = this.cube;
        if (cube == null) {
            this.players.remove(player);
            if (this.players.isEmpty()) { playerCubeMap.removeEntry(this); }
            return;
        }
        if (this.sentToPlayers) {
            RDPLNetwork.sendTo(new MessageUnloadCube(this.cubePos), player);
            playerCubeMap.removeSchedulesSendCubeToPlayer(cube, player);
        }
        this.players.remove(player);
        MinecraftForge.EVENT_BUS.post(new CubeUnWatchEvent(cube, player));
        if (this.players.isEmpty()) { playerCubeMap.removeEntry(this); }
    }

    void invalidate() {
        if (loading) {
            CubeIoQueue.dropQueuedCubeLoad(this.playerCubeMap.getWorldServer(),
                    cubePos.getX(), cubePos.getY(), cubePos.getZ(),
                    c -> this.cube = c);
        }
        invalid = true;
        playersToAdd.clear();
    }

    boolean providePlayerCube(boolean canGenerate) {
        if (loading) { return false; }
        if (isWaitingForColumn()) { return false; }
        if (this.cube != null && (!canGenerate || !isWaitingForCube())) { return true; }
        int cubeX = cubePos.getX();
        int cubeY = cubePos.getY();
        int cubeZ = cubePos.getZ();
        playerCubeMap.getWorldServer().profiler.startSection("getCube");
        if (canGenerate) {
            this.cube = this.cubeCache.getCube(cubeX, cubeY, cubeZ, ICubeProviderServer.Requirement.LIGHT);
            assert this.cube != null;
            if (this.cube instanceof BlankCube) {
                this.cube = null;
                return false;
            }
            if (!this.cube.isFullyPopulated()) { return false; }
        }
        else { this.cube = this.cubeCache.getCube(cubeX, cubeY, cubeZ, ICubeProviderServer.Requirement.LOAD); }
        if (this.cube != null) { this.cube.getTickets().add(this); }
        playerCubeMap.getWorldServer().profiler.endStartSection("light");
        playerCubeMap.getWorldServer().profiler.endSection();
        return this.cube != null;
    }

    @Override public boolean isSentToPlayers() { return sentToPlayers; }

    boolean isWaitingForCube() { return this.cube == null || !this.cube.isFullyPopulated() || !this.cube.isInitialLightingDone() || !this.cube.isSurfaceTracked(); }

    boolean isWaitingForColumn() {
        ColumnWatcher columnEntry = playerCubeMap.getColumnWatcher(this.cubePos.chunkPos());
        return columnEntry == null || !columnEntry.isSentToPlayers();
    }

    SendToPlayersResult sendToPlayers() {
        if (this.sentToPlayers) { return SendToPlayersResult.ALREADY_DONE; }
        if (isWaitingForCube()) { return SendToPlayersResult.WAITING; }
        if (isWaitingForColumn()) { return SendToPlayersResult.WAITING; }
        this.dirtyBlocks.clear();
        this.sentToPlayers = true;
        for (EntityPlayerMP playerEntry : this.players) { sendToPlayer(playerEntry); }
        return SendToPlayersResult.CUBE_SENT;
    }

    private void sendToPlayer(EntityPlayerMP player) {
        if (!this.sentToPlayers) { return; }
        assert cube != null;
        playerCubeMap.scheduleSendCubeToPlayer(cube, player);
    }

    void updateInhabitedTime() {
        final long now = getWorldTime();
        if (this.cube == null) {
            this.previousWorldTime = now;
            return;
        }
        long inhabitedTime = this.cube.getColumn().getInhabitedTime();
        inhabitedTime += now - this.previousWorldTime;
        this.cube.getColumn().setInhabitedTime(inhabitedTime);
        this.previousWorldTime = now;
    }

    void blockChanged(int localX, int localY, int localZ) {
        if (this.dirtyBlocks.isEmpty()) { playerCubeMap.addToUpdateEntry(this); }
        this.dirtyBlocks.add((short) AddressTools.getLocalAddress(localX, localY, localZ));
    }

    void update() {
        if (!this.sentToPlayers) { return; }
        Cube cube = this.cube;
        assert cube != null;
        if (this.dirtyBlocks.isEmpty()) { return; }
        World world = cube.getWorld();
        if (this.dirtyBlocks.size() >= ForgeModContainer.clumpingThreshold) { this.players.forEach(entry -> playerCubeMap.scheduleSendCubeToPlayer(cube, entry)); }
        else {
            MessageCubeBlockChange packet = null;
            for (EntityPlayerMP player : this.players) {
                if (packet == null) { packet = new MessageCubeBlockChange(cube, this.dirtyBlocks); }
                RDPLNetwork.sendTo(packet, player);
            }
            this.dirtyBlocks.forEach(localAddress -> {
                BlockPos pos = cube.localAddressToBlockPos(localAddress);
                IBlockState state = cube.getBlockState(pos);
                if (state.getBlock().hasTileEntity(state)) { sendBlockEntityToAllPlayers(world.getTileEntity(pos)); }
                return true;
            });
        }
        this.dirtyBlocks.clear();
    }

    private void sendBlockEntityToAllPlayers(@Nullable TileEntity blockEntity) {
        if (blockEntity == null) { return; }
        Packet<?> packet = blockEntity.getUpdatePacket();
        if (packet == null) { return; }
        sendPacketToAllPlayers(packet);
    }

    boolean containsPlayer(EntityPlayerMP player) { return this.players.contains(player); }

    boolean hasPlayerMatching(Predicate<EntityPlayerMP> predicate) {
        for (EntityPlayerMP e : players.elements()) {
            if (e == null) { break; }
            if (predicate.apply(e)) { return true; }
        }
        return false;
    }

    boolean hasNoPlayerMatchingInRange(Predicate<EntityPlayerMP> predicate, int range) {
        double d = range*range;
        double cx = cubePos.getXCenter();
        double cy = cubePos.getYCenter();
        double cz = cubePos.getZCenter();
        for (EntityPlayerMP e : players.elements()) {
            if (e == null) { break; }
            if (predicate.apply(e)) {
                double dist = cx - e.posX;
                dist *= dist;
                if (dist > d) { continue; }
                double dy = cy - e.posY;
                dist += dy * dy;
                if (dist > d) { continue; }
                double dz = cz - e.posZ;
                dist += dz * dz;
                if (dist > d) { continue; }
                return false;
            }
        }
        return true;
    }

    @Override @Nullable public Cube getCube() { return this.cube; }

    private long getWorldTime() { return playerCubeMap.getWorldServer().getWorldTime(); }

    private void sendPacketToAllPlayers(Packet<?> packet) {
        for (EntityPlayerMP entry : this.players) { entry.connection.sendPacket(packet); }
    }

    CubePos getCubePos() { return cubePos; }

    @Override public int getX() { return this.cubePos.getX(); }

    @Override public int getY() { return this.cubePos.getY(); }

    @Override public int getZ() { return this.cubePos.getZ(); }

    @Override public boolean shouldTick() { return false; }

    private long[] bucketDataEntry = null;

    @Override public long[] getBucketEntryData() { return bucketDataEntry; }

    @Override public void setBucketEntryData(long[] data) { bucketDataEntry = data; }

    public enum SendToPlayersResult {
        ALREADY_DONE, CUBE_SENT, WAITING
    }
}
