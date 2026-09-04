package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.entity.IRubicEntityTracker;
import mctmods.resourcedatapackloader.content.rubic.server.interfaces.IRubicPlayerList;
import mctmods.resourcedatapackloader.content.rubic.visibility.CubeSelector;
import mctmods.resourcedatapackloader.content.rubic.visibility.CuboidalCubeSelector;
import mctmods.resourcedatapackloader.content.rubic.world.CubeWatchEvent;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.network.MessageCubes;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.WatchersSortingList2D;
import mctmods.resourcedatapackloader.util.WatchersSortingList3D;
import mctmods.resourcedatapackloader.util.XYZMap;
import mctmods.resourcedatapackloader.util.XZMap;
import static mctmods.resourcedatapackloader.util.Coords.blockToCube;
import static mctmods.resourcedatapackloader.util.Coords.blockToLocal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import static net.minecraft.util.math.MathHelper.clamp;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSetMultimap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class PlayerCubeMap extends PlayerChunkMap {
    private static final Predicate<EntityPlayerMP> NOT_SPECTATOR = player -> player != null && !player.isSpectator();
    private final CubeSelector cubeSelector = new CuboidalCubeSelector();
    private final TIntObjectMap<PlayerWrapper> players = new TIntObjectHashMap<>();
    final XYZMap<CubeWatcher> cubeWatchers = new XYZMap<>(0.7f, 25 * 25 * 25);
    final XZMap<ColumnWatcher> columnWatchers = new XZMap<>(0.7f, 25 * 25);
    private final Set<CubeWatcher> cubeWatchersToUpdate = new HashSet<>();
    private final Set<ColumnWatcher> columnWatchersToUpdate = new HashSet<>();

    private final WatchersSortingList3D<CubeWatcher> watchersToAddPlayersTo = new WatchersSortingList3D<>(0, () ->
            players.valueCollection().stream().map(p -> p.playerEntity).collect(Collectors.toList()));

    private final WatchersSortingList3D<CubeWatcher> cubesToSendToClients = new WatchersSortingList3D<>(1, () ->
            players.valueCollection().stream().map(p -> p.playerEntity).collect(Collectors.toList()));

    private final WatchersSortingList3D<CubeWatcher> cubesToGenerate = new WatchersSortingList3D<>(2, () ->
            players.valueCollection().stream().map(p -> p.playerEntity).collect(Collectors.toList()));

    private final WatchersSortingList2D<ColumnWatcher> columnsToSendToClients = new WatchersSortingList2D<>(3, () ->
            players.valueCollection().stream().map(p -> p.playerEntity).collect(Collectors.toList()));

    private final WatchersSortingList2D<ColumnWatcher> columnsToGenerate = new WatchersSortingList2D<>(4, () ->
            players.valueCollection().stream().map(p -> p.playerEntity).collect(Collectors.toList()));

    private final WatchersSortingList3D<CubeWatcher> tickableCubeTracker = new WatchersSortingList3D<>(5, () ->
            players.valueCollection().stream().map(p -> p.playerEntity).filter(NOT_SPECTATOR).collect(Collectors.toList()));

    private int horizontalViewDistance;
    private int verticalViewDistance;
    private long previousWorldTime = 0;
    private final CubeProviderServer cubeCache;
    private final Object2ObjectOpenHashMap<EntityPlayerMP, ObjectOpenHashSet<Cube>> cubesToSend = new Object2ObjectOpenHashMap<>(2);
    private Set<EntityPlayerMP> pendingPlayerAddToCubeMap = new HashSet<>();
    private final TickableChunkContainer tickableChunksCubesToReturn = new TickableChunkContainer();
    private final CubeGC chunkGc;

    public PlayerCubeMap(WorldServer worldServer) {
        super(worldServer);
        this.cubeCache = ((IRubicWorldInternal.IServer) worldServer).rdpl$getCubeCache();
        PlayerList playerList = Objects.requireNonNull(worldServer.getMinecraftServer(), "server").getPlayerList();
        this.setPlayerViewDistance(playerList.getViewDistance(), ((IRubicPlayerList) playerList).getVerticalViewDistance());
        this.chunkGc = new CubeGC(((IRubicWorldInternal.IServer) worldServer).rdpl$getCubeCache());
    }

    @Override @Deprecated @Nonnull public Iterator<Chunk> getChunkIterator() {
        Iterator<Chunk> chunkIt = this.cubeCache.getLoadedChunks().iterator();
        return new AbstractIterator<Chunk>() {
            @Override protected Chunk computeNext() {
                while (chunkIt.hasNext()) {
                    IColumn column = (IColumn) chunkIt.next();
                    if (column.shouldTick()) { return (Chunk) column; }
                }
                return this.endOfData();
            }
        };
    }

    public TickableChunkContainer getTickableChunks() {
        TickableChunkContainer tickableChunksCubes = this.tickableChunksCubesToReturn;
        tickableChunksCubes.clear();
        addTickableColumns(tickableChunksCubes);
        addTickableCubes(tickableChunksCubes);
        addForcedColumns(tickableChunksCubes);
        addForcedCubes(tickableChunksCubes);
        return tickableChunksCubes;
    }

    private void addForcedColumns(TickableChunkContainer tickableChunksCubes) {
        for(IColumn columns : ((IRubicWorldInternal.IServer) getWorldServer()).rdpl$getForcedColumns()) { tickableChunksCubes.addColumn((Chunk) columns); }
    }

    private void addForcedCubes(TickableChunkContainer tickableChunksCubes) {
        tickableChunksCubes.forcedCubes = ((IRubicWorldInternal.IServer) getWorldServer()).rdpl$getForcedCubes();
    }

    private void addTickableCubes(TickableChunkContainer tickableChunksCubes) {
        for (CubeWatcher watcher : (Iterable<CubeWatcher>) () -> tickableCubeTracker.iteratorUpToDistance(9)) {
            ICube cube = watcher.getCube();
            if (cube == null || watcher.hasNoPlayerMatchingInRange(NOT_SPECTATOR, 128)) { continue; }
            tickableChunksCubes.addCube(cube);
        }
    }

    private void addTickableColumns(TickableChunkContainer tickableChunksCubes) {
        for (ColumnWatcher watcher : columnWatchers) {
            Chunk chunk = watcher.getChunk();
            if (chunk == null || !watcher.hasPlayerMatchingInRange(128.0D, NOT_SPECTATOR)) { continue; }
            tickableChunksCubes.addColumn(chunk);
        }
    }

    @Override public void tick() {
        getWorldServer().profiler.startSection("playerCubeMapTick");
        boolean spectatorsGenerateChunks = getWorldServer().getGameRules().getBoolean("spectatorsGenerateChunks");
        Predicate<EntityPlayerMP> canGenerateChunkPredicate = player -> player != null && (spectatorsGenerateChunks ||!player.isSpectator());
        long currentTime = this.getWorldServer().getTotalWorldTime();
        getWorldServer().profiler.startSection("addPendingPlayers");
        if (!pendingPlayerAddToCubeMap.isEmpty()) {
            Set<EntityPlayerMP> players = pendingPlayerAddToCubeMap;
            pendingPlayerAddToCubeMap = new HashSet<>();
            for (EntityPlayerMP player : players) { addPlayer(player); }
        }
        getWorldServer().profiler.endStartSection("tickEntries");
        if (currentTime - this.previousWorldTime > 8000L) {
            this.previousWorldTime = currentTime;
            for (CubeWatcher playerInstance : this.cubeWatchers) {
                playerInstance.update();
                playerInstance.updateInhabitedTime();
            }
        }
        if (!cubeWatchersToUpdate.isEmpty()) {
            this.cubeWatchersToUpdate.forEach(CubeWatcher::update);
            this.cubeWatchersToUpdate.clear();
        }
        if (!columnWatchersToUpdate.isEmpty()) {
            this.columnWatchersToUpdate.forEach(ColumnWatcher::update);
            this.columnWatchersToUpdate.clear();
        }
        getWorldServer().profiler.endStartSection("sortTickableTracker");
        tickableCubeTracker.tick();
        getWorldServer().profiler.endStartSection("sortToGenerate");
        this.cubesToGenerate.tick();
        this.columnsToGenerate.tick();
        getWorldServer().profiler.endStartSection("sortToSend");
        this.cubesToSendToClients.tick();
        this.columnsToSendToClients.tick();
        this.watchersToAddPlayersTo.tick();
        getWorldServer().profiler.endStartSection("generate");
        if (!this.columnsToGenerate.isEmpty()) {
            getWorldServer().profiler.startSection("columns");
            Iterator<ColumnWatcher> iter = this.columnsToGenerate.iterator();
            while (iter.hasNext()) {
                ColumnWatcher entry = iter.next();
                boolean success = entry.getChunk() != null;
                if (!success) {
                    boolean canGenerate = entry.hasPlayerMatching(canGenerateChunkPredicate);
                    getWorldServer().profiler.startSection("generate");
                    success = entry.providePlayerChunk(canGenerate);
                    getWorldServer().profiler.endSection();
                }
                if (success) {
                    iter.remove();
                    if (entry.sendToPlayers()) { this.columnsToSendToClients.remove(entry); }
                }
            }
            getWorldServer().profiler.endSection();
        }
        if (!this.cubesToGenerate.isEmpty()) {
            getWorldServer().profiler.startSection("cubes");
            long stopTime = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(ContentControl.number(ContentControl.CHUNKS, "cubeGenMillisPerRound", 50));
            Iterator<CubeWatcher> iterator = this.cubesToGenerate.iterator();
            while (iterator.hasNext() && System.nanoTime() < stopTime) {
                CubeWatcher watcher = iterator.next();
                if (watcher.isWaitingForColumn()) { continue; }
                boolean success = !watcher.isWaitingForCube();
                if (!success) {
                    boolean canGenerate = watcher.hasPlayerMatching(canGenerateChunkPredicate);
                    getWorldServer().profiler.startSection("generate");
                    success = watcher.providePlayerCube(canGenerate);
                    getWorldServer().profiler.endSection();
                }
                if (success) {
                    CubeWatcher.SendToPlayersResult state = watcher.sendToPlayers();
                    if (state == CubeWatcher.SendToPlayersResult.WAITING || state == CubeWatcher.SendToPlayersResult.CUBE_SENT
                            || state == CubeWatcher.SendToPlayersResult.ALREADY_DONE) {
                        iterator.remove();
                        this.cubesToSendToClients.remove(watcher);
                    }
                }
            }
            getWorldServer().profiler.endSection();
        }
        getWorldServer().profiler.endStartSection("send");
        if (!this.columnsToSendToClients.isEmpty()) {
            getWorldServer().profiler.startSection("columns");
            Iterator<ColumnWatcher> it = this.columnsToSendToClients.iterator();
            while (it.hasNext()) {
                ColumnWatcher playerInstance = it.next();
                if (playerInstance.sendToPlayers()) { it.remove(); }
                else if (!columnsToGenerate.contains(playerInstance)) { columnsToGenerate.add(playerInstance); }
            }
            this.columnsToSendToClients.removeIf(ColumnWatcher::sendToPlayers);
            getWorldServer().profiler.endSection();
        }
        if (!this.cubesToSendToClients.isEmpty()) {
            getWorldServer().profiler.startSection("cubes");
            int toSend = ContentControl.number(ContentControl.CHUNKS, "cubesSentPerTick", 649);
            Iterator<CubeWatcher> it = this.cubesToSendToClients.iterator();
            while (it.hasNext() && toSend > 0) {
                CubeWatcher playerInstance = it.next();
                CubeWatcher.SendToPlayersResult state = playerInstance.sendToPlayers();
                if (state == CubeWatcher.SendToPlayersResult.ALREADY_DONE || state == CubeWatcher.SendToPlayersResult.CUBE_SENT) {
                    it.remove();
                    --toSend;
                }
            }
            getWorldServer().profiler.endSection();
        }
        if (!watchersToAddPlayersTo.isEmpty()) {
            int toSend = ContentControl.number(ContentControl.CHUNKS, "cubesSentPerTick", 649);
            for (Iterator<CubeWatcher> iter = watchersToAddPlayersTo.iterator(); toSend > 0 && iter.hasNext(); ) {
                CubeWatcher watcher = iter.next();
                watcher.addScheduledPlayers();
                CubeWatcher.SendToPlayersResult state = watcher.sendToPlayers();
                if (state == CubeWatcher.SendToPlayersResult.WAITING) {
                    if (!cubesToGenerate.contains(watcher)) { cubesToGenerate.add(watcher); }
                }
                if (state != CubeWatcher.SendToPlayersResult.ALREADY_DONE) { toSend--; }
                iter.remove();
            }
        }
        getWorldServer().profiler.endStartSection("unload");
        if (this.players.isEmpty()) {
            WorldProvider worldprovider = this.getWorldServer().provider;
            if (!worldprovider.canRespawnHere()) { this.getWorldServer().getChunkProvider().queueUnloadAll(); }
        }
        getWorldServer().profiler.endStartSection("sendCubes");
        if (!cubesToSend.isEmpty()) {
            for (EntityPlayerMP player : cubesToSend.keySet()) {
                Collection<Cube> cubes = cubesToSend.get(player);
                if (!players.containsKey(player.getEntityId())) {
                    Rubic.LOGGER.info("Skipping sending {} chunks to player {} that is no longer in this world!", cubes.size(), player.getName());
                    continue;
                }
                ((IRubicWorldInternal) getWorldServer()).rdpl$getLightingManager().onSendCubes();
                for (MessageCubes packet : MessageCubes.batched(cubes)) { RDPLNetwork.sendTo(packet, player); }
                for (Cube cube : cubes) {
                    ((IRubicEntityTracker) getWorldServer().getEntityTracker()).sendLeashedEntitiesInCube(player, cube);
                    CubeWatcher watcher = getCubeWatcher(cube.getCoords());
                    assert watcher != null;
                    MinecraftForge.EVENT_BUS.post(new CubeWatchEvent(cube, player));
                }
            }
            cubesToSend.clear();
        }
        getWorldServer().profiler.endSection();
        getWorldServer().profiler.endSection();
    }

    @Override public boolean contains(int cubeX, int cubeZ) { return this.columnWatchers.get(cubeX, cubeZ) != null; }

    @Nullable @Override public PlayerChunkMapEntry getEntry(int cubeX, int cubeZ) { return this.columnWatchers.get(cubeX, cubeZ); }

    private CubeWatcher getOrCreateCubeWatcher(CubePos cubePos) {
        CubeWatcher cubeWatcher = this.cubeWatchers.get(cubePos.getX(), cubePos.getY(), cubePos.getZ());
        if (cubeWatcher == null) {
            cubeWatcher = new CubeWatcher(this, cubePos);
            this.cubeWatchers.put(cubeWatcher);
            this.tickableCubeTracker.add(cubeWatcher);
            if (cubeWatcher.isWaitingForColumn() || cubeWatcher.isWaitingForCube()) { this.cubesToGenerate.add(cubeWatcher); }
            this.cubesToSendToClients.add(cubeWatcher);
        }
        return cubeWatcher;
    }

    private ColumnWatcher getOrCreateColumnWatcher(ChunkPos chunkPos) {
        ColumnWatcher columnWatcher = this.columnWatchers.get(chunkPos.x, chunkPos.z);
        if (columnWatcher == null) {
            columnWatcher = new ColumnWatcher(this, chunkPos);
            this.columnWatchers.put(columnWatcher);
            if (columnWatcher.getChunk() == null) { this.columnsToGenerate.add(columnWatcher); }
            if (!columnWatcher.sendToPlayers()) { this.columnsToSendToClients.add(columnWatcher); }
        }
        return columnWatcher;
    }

    @Override public void markBlockForUpdate(@Nonnull BlockPos pos) {
        CubeWatcher cubeWatcher = this.getCubeWatcher(CubePos.fromBlockCoords(pos));
        if (cubeWatcher != null) {
            int localX = blockToLocal(pos.getX());
            int localY = blockToLocal(pos.getY());
            int localZ = blockToLocal(pos.getZ());
            cubeWatcher.blockChanged(localX, localY, localZ);
        }
    }

    public void heightUpdated(int blockX, int blockZ) {
        ColumnWatcher columnWatcher = this.columnWatchers.get(blockToCube(blockX), blockToCube(blockZ));
        if (columnWatcher != null) {
            int localX = blockToLocal(blockX);
            int localZ = blockToLocal(blockZ);
            columnWatcher.heightChanged(localX, localZ);
        }
    }

    @Override public void addPlayer(EntityPlayerMP player) {
        if (player.world != this.getWorldServer()) {
            Rubic.bigWarning("Player world not the same ad PlayerCubeMap world! Adding anyway. This is very likely to cause issues! Player "
                            + "world dimension ID: %d, PlayerCubeMap dimension ID: %d", player.world.provider.getDimension(),
                    getWorldServer().provider.getDimension());
        }
        else if (!player.world.playerEntities.contains(player)) {
            ContentLog.LOGGER.debug("PlayerCubeMap (dimension {}): Adding player to pending to add list", getWorldServer().provider.getDimension());
            pendingPlayerAddToCubeMap.add(player);
            return;
        }
        PlayerWrapper playerWrapper = new PlayerWrapper(player);
        playerWrapper.updateManagedPos();
        CubePos playerCubePos = CubePos.fromEntity(player);
        this.cubeSelector.forAllVisibleFrom(playerCubePos, horizontalViewDistance, verticalViewDistance, (currentPos) -> {
            ColumnWatcher chunkWatcher = getOrCreateColumnWatcher(currentPos.chunkPos());
            if (!chunkWatcher.containsPlayer(player)) { chunkWatcher.addPlayer(player); }
            CubeWatcher cubeWatcher = getOrCreateCubeWatcher(currentPos);
            scheduleAddPlayerToWatcher(cubeWatcher, player);
        });
        this.players.put(player.getEntityId(), playerWrapper);
    }

    @Override public void removePlayer(EntityPlayerMP player) {
        PlayerWrapper playerWrapper = this.players.get(player.getEntityId());
        if (playerWrapper == null) { return; }
        CubePos playerCubePos = CubePos.fromEntityCoords(player.managedPosX, playerWrapper.managedPosY, player.managedPosZ);
        ObjectSet<ColumnWatcher> toSendUnload = new ObjectOpenHashSet<>((horizontalViewDistance*2+1) * (horizontalViewDistance*2+1) * 6);
        this.cubeSelector.forAllVisibleFrom(playerCubePos, horizontalViewDistance, verticalViewDistance, (cubePos) -> {
            CubeWatcher watcher = getCubeWatcher(cubePos);
            if (watcher != null) { removePlayerFromCubeWatcher(watcher, player); }
            ColumnWatcher columnWatcher = getColumnWatcher(cubePos.chunkPos());
            if (columnWatcher == null) { return; }
            toSendUnload.add(columnWatcher);
        });
        toSendUnload.stream()
                .filter(watcher->watcher.containsPlayer(player))
                .forEach(watcher->watcher.removePlayer(player));
        this.players.remove(player.getEntityId());
    }

    @Override public void updateMovingPlayer(EntityPlayerMP player) {
        PlayerWrapper playerWrapper = this.players.get(player.getEntityId());
        if (playerWrapper == null) { return; }
        if (!playerWrapper.cubePosChanged()) { return; }
        this.updatePlayer(playerWrapper, playerWrapper.getManagedCubePos(), CubePos.fromEntity(player));
        playerWrapper.updateManagedPos();
        this.chunkGc.tick();
    }

    private void updatePlayer(PlayerWrapper entry, CubePos oldPos, CubePos newPos) {
        getWorldServer().profiler.startSection("updateMovedPlayer");
        Set<CubePos> cubesToRemove = new HashSet<>();
        Set<CubePos> cubesToLoad = new HashSet<>();
        Set<ChunkPos> columnsToRemove = new HashSet<>();
        Set<ChunkPos> columnsToLoad = new HashSet<>();
        getWorldServer().profiler.startSection("findChanges");
        this.cubeSelector.findChanged(oldPos, newPos, horizontalViewDistance, verticalViewDistance, cubesToRemove, cubesToLoad, columnsToRemove,
                columnsToLoad);
        getWorldServer().profiler.endStartSection("createColumns");
        columnsToLoad.forEach(pos -> {
            ColumnWatcher columnWatcher = this.getOrCreateColumnWatcher(pos);
            assert columnWatcher.getPos().equals(pos);
            columnWatcher.addPlayer(entry.playerEntity);
        });
        getWorldServer().profiler.endStartSection("createCubes");
        cubesToLoad.forEach(pos -> {
            CubeWatcher cubeWatcher = this.getOrCreateCubeWatcher(pos);
            assert cubeWatcher.getCubePos().equals(pos);
            scheduleAddPlayerToWatcher(cubeWatcher, entry.playerEntity);
        });
        getWorldServer().profiler.endStartSection("removeCubes");
        cubesToRemove.forEach(pos -> {
            CubeWatcher cubeWatcher = this.getCubeWatcher(pos);
            if (cubeWatcher != null) {
                assert cubeWatcher.getCubePos().equals(pos);
                removePlayerFromCubeWatcher(cubeWatcher, entry.playerEntity);
            }
        });
        getWorldServer().profiler.endStartSection("removeColumns");
        columnsToRemove.forEach(pos -> {
            ColumnWatcher columnWatcher = this.getColumnWatcher(pos);
            if (columnWatcher != null) {
                assert columnWatcher.getPos().equals(pos);
                columnWatcher.removePlayer(entry.playerEntity);
            }
        });
        getWorldServer().profiler.endSection();
        getWorldServer().profiler.endSection();
    }

    private void removePlayerFromCubeWatcher(CubeWatcher cubeWatcher, EntityPlayerMP playerEntity) { cubeWatcher.removePlayer(playerEntity); }

    private void scheduleAddPlayerToWatcher(CubeWatcher cubeWatcher, EntityPlayerMP playerEntity) {
        watchersToAddPlayersTo.add(cubeWatcher);
        cubeWatcher.scheduleAddPlayer(playerEntity);
    }

    @Override public boolean isPlayerWatchingChunk(@Nonnull EntityPlayerMP player, int cubeX, int cubeZ) {
        ColumnWatcher columnWatcher = this.getColumnWatcher(new ChunkPos(cubeX, cubeZ));
        return columnWatcher != null &&
                columnWatcher.containsPlayer(player) &&
                columnWatcher.isSentToPlayers();
    }

    public boolean isPlayerWatchingCube(EntityPlayerMP player, int cubeX, int cubeY, int cubeZ) {
        CubeWatcher watcher = this.getCubeWatcher(new CubePos(cubeX, cubeY, cubeZ));
        return watcher != null &&
                watcher.containsPlayer(player) &&
                watcher.isSentToPlayers();
    }

    @Override @Deprecated public final void setPlayerViewRadius(int newHorizontalViewDistance) {
        this.setPlayerViewDistance(newHorizontalViewDistance, verticalViewDistance);
    }

    public final void setPlayerViewDistance(int newHorizontalViewDistance, int newVerticalViewDistance) {
        if (this.players == null) { return; }
        newHorizontalViewDistance = clamp(newHorizontalViewDistance, 3, Rubic.hasOptifine() ? 64 : 32);
        newVerticalViewDistance = clamp(newVerticalViewDistance, 3, Rubic.hasOptifine() ? 64 : 32);
        if (newHorizontalViewDistance == this.horizontalViewDistance && newVerticalViewDistance == this.verticalViewDistance) { return; }
        int oldHorizontalViewDistance = this.horizontalViewDistance;
        int oldVerticalViewDistance = this.verticalViewDistance;
        if ((newHorizontalViewDistance < oldHorizontalViewDistance && newVerticalViewDistance > oldVerticalViewDistance) ||
                (newHorizontalViewDistance > oldHorizontalViewDistance && newVerticalViewDistance < oldVerticalViewDistance)) {
            setPlayerViewDistance(newHorizontalViewDistance, oldVerticalViewDistance);
            setPlayerViewDistance(newHorizontalViewDistance, newVerticalViewDistance);
            return;
        }
        for (PlayerWrapper playerWrapper : this.players.valueCollection()) {
            EntityPlayerMP player = playerWrapper.playerEntity;
            CubePos playerPos = playerWrapper.getManagedCubePos();
            if (newHorizontalViewDistance > oldHorizontalViewDistance || newVerticalViewDistance > oldVerticalViewDistance) {
                this.cubeSelector.forAllVisibleFrom(playerPos, newHorizontalViewDistance, newVerticalViewDistance, pos -> {
                    ColumnWatcher columnWatcher = this.getOrCreateColumnWatcher(pos.chunkPos());
                    if (!columnWatcher.containsPlayer(player)) { columnWatcher.addPlayer(player); }
                    CubeWatcher cubeWatcher = this.getOrCreateCubeWatcher(pos);
                    if (!cubeWatcher.containsPlayer(player)) { scheduleAddPlayerToWatcher(cubeWatcher, player); }
                });
            }
            else {
                Set<CubePos> cubesToUnload = new HashSet<>();
                Set<ChunkPos> columnsToUnload = new HashSet<>();
                this.cubeSelector.findAllUnloadedOnViewDistanceDecrease(playerPos,
                        oldHorizontalViewDistance, newHorizontalViewDistance,
                        oldVerticalViewDistance, newVerticalViewDistance, cubesToUnload, columnsToUnload);
                cubesToUnload.forEach(pos -> {
                    CubeWatcher cubeWatcher = this.getCubeWatcher(pos);
                    if (cubeWatcher != null) { removePlayerFromCubeWatcher(cubeWatcher, player); }
                    else { Rubic.LOGGER.warn("cubeWatcher null on render distance change"); }
                });
                columnsToUnload.forEach(pos -> {
                    ColumnWatcher columnWatcher = this.getColumnWatcher(pos);
                    if (columnWatcher != null && columnWatcher.containsPlayer(player)) { columnWatcher.removePlayer(player); }
                    else { Rubic.LOGGER.warn("cubeWatcher null or doesn't contain player on render distance change"); }
                });
            }
        }
        this.horizontalViewDistance = newHorizontalViewDistance;
        this.verticalViewDistance = newVerticalViewDistance;
    }

    @Override public void entryChanged(@Nonnull PlayerChunkMapEntry entry) { throw new UnsupportedOperationException(); }

    @Override public void removeEntry(@Nonnull PlayerChunkMapEntry entry) { throw new UnsupportedOperationException(); }

    void addToUpdateEntry(CubeWatcher cubeWatcher) { this.cubeWatchersToUpdate.add(cubeWatcher); }

    void addToUpdateEntry(ColumnWatcher columnWatcher) { this.columnWatchersToUpdate.add(columnWatcher); }

    void removeEntry(CubeWatcher cubeWatcher) {
        watchersToAddPlayersTo.remove(cubeWatcher);
        cubeWatcher.invalidate();
        CubePos cubePos = cubeWatcher.getCubePos();
        cubeWatcher.updateInhabitedTime();
        this.tickableCubeTracker.remove(cubeWatcher);
        CubeWatcher removed = this.cubeWatchers.remove(cubePos.getX(), cubePos.getY(), cubePos.getZ());
        assert removed == cubeWatcher : "Removed unexpected cube watcher";
        this.cubeWatchersToUpdate.remove(cubeWatcher);
        this.cubesToGenerate.remove(cubeWatcher);
        this.cubesToSendToClients.remove(cubeWatcher);
        if (cubeWatcher.getCube() != null) { cubeWatcher.getCube().getTickets().remove(cubeWatcher); }
    }

    public void removeEntry(ColumnWatcher entry) {
        ChunkPos pos = entry.getPos();
        entry.updateChunkInhabitedTime();
        this.columnWatchers.remove(pos.x, pos.z);
        this.columnsToGenerate.remove(entry);
        this.columnsToSendToClients.remove(entry);
        this.columnWatchersToUpdate.remove(entry);
    }

    public void scheduleSendCubeToPlayer(Cube cube, EntityPlayerMP player) {
        ObjectOpenHashSet<Cube> cubes = cubesToSend.computeIfAbsent(player, k -> new ObjectOpenHashSet<>(1024));
        cubes.add(cube);
    }

    public void removeSchedulesSendCubeToPlayer(Cube cube, EntityPlayerMP player) {
        ObjectOpenHashSet<Cube> cubes = cubesToSend.get(player);
        if (cubes != null) { cubes.remove(cube); }
    }

    @Nullable public CubeWatcher getCubeWatcher(CubePos pos) { return this.cubeWatchers.get(pos.getX(), pos.getY(), pos.getZ()); }

    @Nullable public ColumnWatcher getColumnWatcher(ChunkPos pos) { return this.columnWatchers.get(pos.x, pos.z); }

    public boolean contains(CubePos coords) { return this.cubeWatchers.get(coords.getX(), coords.getY(), coords.getZ()) != null; }

    private static final class PlayerWrapper {
        final EntityPlayerMP playerEntity;
        private double managedPosY;

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

    public Iterator<Cube> getCubeIterator() {
        WorldServer world = this.getWorldServer();
        final Iterator<CubeWatcher> iterator = this.tickableCubeTracker.iterator();
        ImmutableSetMultimap<ChunkPos, Ticket> persistentChunksFor = ForgeChunkManager.getPersistentChunksFor(world);
        world.profiler.startSection("forcedChunkLoading");
        @SuppressWarnings("unchecked") final Iterator<Cube> persistentCubesIterator = persistentChunksFor.keys().stream()
                .filter(Objects::nonNull)
                .map(input -> (Collection<Cube>) ((IColumn) world.getChunk(input.x, input.z)).getLoadedCubes())
                .collect(ArrayList<Cube>::new, ArrayList::addAll, ArrayList::addAll)
                .iterator();
        world.profiler.endSection();
        return new AbstractIterator<Cube>() {
            Iterator<Cube> persistentCubes = persistentCubesIterator;
            boolean shouldSkip(@Nullable Cube cube){
                if (cube == null)
                    return true;
                if (cube.isEmpty())
                    return true;
                return !cube.isFullyPopulated();
            }
            @Override protected Cube computeNext() {
                while(persistentCubes != null && persistentCubes.hasNext()){
                    Cube cube = persistentCubes.next();
                    if (!persistentCubes.hasNext()) { persistentCubes = null; }
                    if(shouldSkip(cube))
                        continue;
                    return cube;
                }
                while (iterator.hasNext()) {
                    CubeWatcher watcher = iterator.next();
                    Cube cube = watcher.getCube();
                    if (shouldSkip(cube)) { continue; }
                    if (watcher.hasNoPlayerMatchingInRange(NOT_SPECTATOR, 128)) { continue; }
                    return cube;
                }
                return this.endOfData();
            }
        };
    }

    public static class TickableChunkContainer {
        private final ObjectArrayList<ICube> cubes = ObjectArrayList.wrap(new ICube[64*1024]);
        private XYZMap<ICube> forcedCubes;
        private final Set<Chunk> columns = Collections.newSetFromMap(new IdentityHashMap<>());

        private void clear() {
            this.cubes.clear();
            this.columns.clear();
        }

        private void addCube(ICube cube) { cubes.add(cube); }

        public void addColumn(Chunk column) { columns.add(column); }

        public Iterable<ICube> forcedCubes() { return forcedCubes; }

        public ICube[] playerTickableCubes() { return cubes.elements(); }

        public Iterable<Chunk> columns() { return columns; }
    }
}
