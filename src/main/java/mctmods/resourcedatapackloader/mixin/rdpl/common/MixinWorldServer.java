package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.lighting.LightingManager;
import mctmods.resourcedatapackloader.content.rubic.server.CubeGC;
import mctmods.resourcedatapackloader.content.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.server.PlayerCubeMap;
import mctmods.resourcedatapackloader.content.rubic.server.SpawnCubes;
import mctmods.resourcedatapackloader.content.rubic.world.CubeWorldEntitySpawner;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IWorldEntitySpawner;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldProvider;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.IntRange;
import mctmods.resourcedatapackloader.util.NotRubicWorldException;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.XYZMap;
import mctmods.resourcedatapackloader.util.XZMap;
import mctmods.resourcedatapackloader.util.world.CubeSplitTickList;
import mctmods.resourcedatapackloader.util.world.CubeSplitTickSet;
import static mctmods.resourcedatapackloader.util.Coords.cubeToMinBlock;
import static mctmods.resourcedatapackloader.util.ReflectionUtil.cast;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import javax.annotation.Nonnull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.init.Blocks;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mutable;
import java.util.HashSet;
import java.util.Objects;
import javax.annotation.Nullable;
import java.util.Set;
import net.minecraft.world.GameRules;

@SuppressWarnings({"ConstantConditions", "DataFlowIssue"}) @Mixin(WorldServer.class) public abstract class MixinWorldServer extends MixinWorld implements IRubicWorldInternal.Server, IRubicWorldServer {
    @Inject(method = "createSpawnPosition", at = @At("RETURN")) private void rdpl$spawnWhereAsked(WorldSettings settings, CallbackInfo ci) {
        String wanted = ContentTerrain.worldSpawn();
        if (wanted.isEmpty()) { return; }

        WorldServer self = (WorldServer) (Object) this;
        BlockPos asked = ContentTerrain.spawnFrom(wanted, self.provider.getAverageGroundLevel());
        if (asked == null) { return; }

        self.getWorldInfo().setSpawn(asked);
        Summary.info("terrain.spawn", "Spawning every new world at " + asked.getX() + ", " + asked.getY() + ", " + asked.getZ() + ", which is what a pack asks for");
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;setWorldTime(J)V"))
    private void rdpl$holdTimeWhileMaking(WorldServer world, long time) {
        if (ContentPregen.busyIn(world)) { return; }
        world.setWorldTime(time);
    }

    @Inject(method = "updateWeather", at = @At("HEAD"), cancellable = true) private void rdpl$holdWeatherWhileMaking(CallbackInfo ci) {
        if (ContentPregen.busyIn((WorldServer) (Object) this)) { ci.cancel(); }
    }

    @Inject(method = "initialize", at = @At("RETURN")) private void rdpl$borderAsAsked(WorldSettings settings, CallbackInfo ci) {
        int wanted = ContentTerrain.worldBorder();
        if (wanted <= 0) { return; }
        if (wanted > Config.worldgen.worldBorderLimit) {
            ContentLog.LOGGER.error("A pack asks for a world border {} block(s) across, which is past the {} allowed by worldBorderLimit, so the border is left where the game puts it", wanted, Config.worldgen.worldBorderLimit);
            return;
        }
        WorldServer self = (WorldServer) (Object) this;
        self.getWorldInfo().setBorderSize(wanted);
        self.getWorldBorder().setTransition(wanted);
        Summary.info("terrain.border", "Standing the world border " + wanted + " block(s) across in every new world, which is what a pack asks for");
    }

    @Shadow @Final private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;
    @Shadow @Mutable @Final private List<NextTickListEntry> pendingTickListEntriesThisTick;
    @Unique private int rdpl$immediateTickDepth;
    @Unique private final Map<Long, List<NextTickListEntry>> rdpl$byChunk = new HashMap<>();

    @SuppressWarnings("UnusedAssignment") @WrapOperation(method = "updateBlockTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;updateTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"))
    private void rdpl$boundImmediateTicks(Block block, World worldIn, BlockPos pos, IBlockState state, Random rand, Operation<Void> original) {
        if (rdpl$isRubicWorld() && rdpl$immediateTickDepth >= 32) {
            scheduledUpdatesAreImmediate = false;
            try {
                ((WorldServer) (Object) this).scheduleUpdate(pos, block, 1);
            } finally {
                scheduledUpdatesAreImmediate = true;
            }
            return;
        }
        rdpl$immediateTickDepth++;
        try {
            original.call(block, worldIn, pos, state, rand);
        } finally {
            rdpl$immediateTickDepth--;
        }
    }
    @Unique private int rdpl$builtFromCount = -1;
    @Unique private long rdpl$builtOnTick = -1L;

    @Inject(method = "getPendingBlockUpdates(Lnet/minecraft/world/gen/structure/StructureBoundingBox;Z)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private void rdpl$answerFromTheIndex(StructureBoundingBox structureBB, boolean remove, CallbackInfoReturnable<List<NextTickListEntry>> cir) {
        if (remove) {
            rdpl$builtFromCount = -1;
            return;
        }
        if (pendingTickListEntriesTreeSet.isEmpty() && pendingTickListEntriesThisTick.isEmpty()) {
            cir.setReturnValue(null);
            return;
        }
        int waiting = pendingTickListEntriesTreeSet.size();
        long now = ((WorldServer) (Object) this).getTotalWorldTime();
        if (waiting != rdpl$builtFromCount || now != rdpl$builtOnTick) {
            rdpl$byChunk.clear();
            rdpl$gather(pendingTickListEntriesTreeSet);
            rdpl$builtFromCount = waiting;
            rdpl$builtOnTick = now;
        }
        List<NextTickListEntry> found = null;
        int buckets = 0;
        for (int x = structureBB.minX >> 4; x <= structureBB.maxX >> 4; x++) {
            for (int z = structureBB.minZ >> 4; z <= structureBB.maxZ >> 4; z++) {
                List<NextTickListEntry> here = rdpl$byChunk.get(ChunkPos.asLong(x, z));
                if (here == null) { continue; }
                buckets++;
                for (NextTickListEntry entry : here) {
                    if (rdpl$outside(entry, structureBB)) { continue; }
                    if (found == null) { found = new ArrayList<>(); }
                    found.add(entry);
                }
            }
        }
        if (found != null && buckets > 1) { Collections.sort(found); }
        for (NextTickListEntry entry : pendingTickListEntriesThisTick) {
            if (rdpl$outside(entry, structureBB)) { continue; }
            if (found == null) { found = new ArrayList<>(); }
            found.add(entry);
        }
        cir.setReturnValue(found);
    }

    @Unique private static boolean rdpl$outside(NextTickListEntry entry, StructureBoundingBox structureBB) {
        return entry.position.getX() < structureBB.minX || entry.position.getX() >= structureBB.maxX || entry.position.getZ() < structureBB.minZ || entry.position.getZ() >= structureBB.maxZ;
    }

    @Unique private void rdpl$gather(Iterable<NextTickListEntry> entries) {
        for (NextTickListEntry entry : entries) {
            long key = ChunkPos.asLong(entry.position.getX() >> 4, entry.position.getZ() >> 4);
            rdpl$byChunk.computeIfAbsent(key, at -> new ArrayList<>()).add(entry);
        }
    }

    @Inject(method = "tickUpdates", at = @At("RETURN")) private void rdpl$forgetAfterTicking(boolean runAllPending, CallbackInfoReturnable<Boolean> cir) { rdpl$builtFromCount = -1; }

    @Shadow @Mutable @Final private PlayerChunkMap playerChunkMap;
    @Shadow @Mutable @Final private WorldEntitySpawner entitySpawner;
    @Unique private Map<Chunk, Set<ICube>> rdpl$forcedChunksCubes;
    @Unique private XYZMap<ICube> rdpl$forcedCubes;
    @Unique private XZMap<IColumn> rdpl$forcedColumns;
    @Unique private CubeGC rdpl$worldChunkGc;
    @Unique private SpawnCubes rdpl$spawnArea;
    @Unique private boolean rdpl$runningCompatibilityGenerator;

    @Shadow protected abstract void playerCheckLight();

    @Shadow public abstract boolean addWeatherEffect(Entity entityIn);

    @Shadow public abstract boolean spawnEntity(Entity entityIn);

    @Shadow @Mutable @Final private Set<NextTickListEntry> pendingTickListEntriesHashSet;

    @Shadow protected abstract boolean canAddEntity(Entity entityIn);

    @Override public void rdpl$initRubicWorldServer(@Nonnull IntRange heightRange, @Nonnull IntRange generationRange) {
        super.rdpl$initRubicWorld(heightRange, generationRange);
        this.rdpl$isRubicWorld = true;
        IWorldEntitySpawner spawner = new CubeWorldEntitySpawner();
        IWorldEntitySpawner.Handler spawnHandler = cast(entitySpawner);
        spawnHandler.rdpl$setEntitySpawner(spawner);
        this.chunkProvider = new CubeProviderServer((WorldServer) (Object) this,
                Objects.requireNonNull(((IRubicWorldProvider) this.provider).rdpl$createCubeGenerator(), "cube generator for rubic world"));
        this.playerChunkMap = new PlayerCubeMap((WorldServer) (Object) this);
        this.rdpl$forcedChunksCubes = new HashMap<>();
        this.rdpl$forcedCubes = new XYZMap<>(0.75f, 64*1024);
        this.rdpl$forcedColumns = new XZMap<>(0.75f, 2048);
        this.pendingTickListEntriesHashSet = new CubeSplitTickSet();
        this.pendingTickListEntriesThisTick = new CubeSplitTickList();
        this.rdpl$worldChunkGc = new CubeGC(rdpl$getCubeCache());
        this.rdpl$lightingManager = new LightingManager((World) (Object) this);
    }

    @Override public void rdpl$setSpawnArea(@Nonnull SpawnCubes spawn) { this.rdpl$spawnArea = spawn; }

    @Override @Nonnull public SpawnCubes rdpl$getSpawnArea() { return rdpl$spawnArea; }

    @Override @Nonnull public CubeSplitTickSet rdpl$getScheduledTicks() { return (CubeSplitTickSet) pendingTickListEntriesHashSet; }

    @Override @Nonnull public CubeSplitTickList rdpl$getThisTickScheduledTicks() { return (CubeSplitTickList) pendingTickListEntriesThisTick; }

    @Override public void rdpl$tickRubicWorld() {
        if (!this.rdpl$isRubicWorld()) { throw new NotRubicWorldException(); }
        rdpl$getLightingManager().onTick();
        if (this.rdpl$spawnArea != null) { this.rdpl$spawnArea.update((World) (Object) this); }
    }

    @Override @Nonnull public CubeProviderServer rdpl$getCubeCache() {
        if (!this.rdpl$isRubicWorld()) { throw new NotRubicWorldException(); }
        return (CubeProviderServer) this.chunkProvider;
    }

    @Override public void rdpl$removeForcedCube(@Nonnull ICube cube) {
        if (!rdpl$forcedChunksCubes.get(cube.getColumn()).remove(cube)) {
            Rubic.LOGGER.error("Trying to remove forced cube {}, but it's not forced!", cube.getCoords()); }
        rdpl$forcedCubes.remove(cube);
        if (rdpl$forcedChunksCubes.get(cube.getColumn()).isEmpty()) {
            rdpl$forcedChunksCubes.remove(cube.getColumn());
            rdpl$forcedColumns.remove(cube.getColumn());
        }
    }

    @Override public void rdpl$addForcedCube(@Nonnull ICube cube) {
        if (!rdpl$forcedChunksCubes.computeIfAbsent(cube.getColumn(), chunk -> new HashSet<>()).add(cube)) {
            Rubic.LOGGER.error("Trying to add forced cube {}, but it's already forced!", cube.getCoords());
        }
        rdpl$forcedCubes.put(cube);
        rdpl$forcedColumns.put(cube.getColumn());
    }

    @Override @Nonnull public XYZMap<ICube> rdpl$getForcedCubes() { return rdpl$forcedCubes; }

    @Override @Nonnull public XZMap<IColumn> rdpl$getForcedColumns() { return rdpl$forcedColumns; }

    @Override public void rdpl$unloadOldCubes() { rdpl$worldChunkGc.chunkGc(); }

    @Override @Nonnull public CompatGenerationScope rdpl$doCompatibilityGeneration() {
        rdpl$runningCompatibilityGenerator = true;
        return () -> rdpl$runningCompatibilityGenerator = false;
    }

    @Override public boolean rdpl$isCompatGenerationScope() { return rdpl$runningCompatibilityGenerator; }

    @Inject(method = "updateBlocks", at = @At("HEAD"), cancellable = true) private void updateBlocksRubic(CallbackInfo cbi) {
        if (!rdpl$isRubicWorld()) { return; }
        cbi.cancel();
        this.playerCheckLight();
        int tickSpeed = this.getGameRules().getInt("randomTickSpeed");
        boolean raining = this.isRaining();
        boolean thundering = this.isThundering();
        this.profiler.startSection("pollingChunks");
        PlayerCubeMap.TickableChunkContainer chunks = ((PlayerCubeMap) this.playerChunkMap).getTickableChunks();
        for (Chunk chunk : chunks.columns()) { rdpl$tickColumn(raining, thundering, chunk); }
        this.profiler.endStartSection("pollingCubes");
        if (tickSpeed > 0) {
            long worldTime = worldInfo.getWorldTotalTime();
            for (ICube cube : chunks.forcedCubes()) { rdpl$tickCube(tickSpeed, cube, worldTime); }
            for (ICube cube : chunks.playerTickableCubes()) {
                if (cube == null) { break; }
                rdpl$tickCube(tickSpeed, cube, worldTime);
            }
        }
        this.profiler.endSection();
    }

    @Unique private void rdpl$tickCube(int tickSpeed, ICube cube, long worldTime) {
        if (!((Cube) cube).checkAndUpdateTick(worldTime)) { return; }
        int chunkBlockX = cubeToMinBlock(cube.getX());
        int chunkBlockZ = cubeToMinBlock(cube.getZ());
        this.profiler.startSection("tickBlocks");
        ExtendedBlockStorage ebs = cube.getStorage();
        if (ebs != Chunk.NULL_BLOCK_STORAGE && ebs.needsRandomTick()) {
            for (int i = 0; i < tickSpeed; ++i) { rdpl$tickNextBlock(chunkBlockX, chunkBlockZ, ebs); }
        }
        this.profiler.endSection();
    }

    @Unique private void rdpl$tickNextBlock(int chunkBlockX, int chunkBlockZ, ExtendedBlockStorage ebs) {
        this.updateLCG = this.updateLCG * 3 + 1013904223;
        int rand = this.updateLCG >> 2;
        int localX = rand & 15;
        int localZ = rand >> 8 & 15;
        int localY = rand >> 16 & 15;
        IBlockState state = ebs.get(localX, localY, localZ);
        Block block = state.getBlock();
        this.profiler.startSection("randomTick");
        if (block.getTickRandomly()) {
            block.randomTick((World) (Object) this,
                    new BlockPos(localX + chunkBlockX, localY + ebs.getYLocation(), localZ + chunkBlockZ), state, this.rand);
        }
        this.profiler.endSection();
    }

    @Unique private void rdpl$tickColumn(boolean raining, boolean thundering, Chunk chunk) {
        int chunkBlockX = chunk.x * 16;
        int chunkBlockZ = chunk.z * 16;
        this.profiler.startSection("checkNextLight");
        chunk.enqueueRelightChecks();
        this.profiler.endStartSection("tickChunk");
        chunk.onTick(false);
        this.profiler.endStartSection("thunder");
        if (this.provider.canDoLightning(chunk) && raining && thundering && this.rand.nextInt(100000) == 0) {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int rand = this.updateLCG >> 2;
            BlockPos strikePos =
                    this.rdpl$adjustPosToNearbyEntityRubic(new BlockPos(chunkBlockX + (rand & 15), 0, chunkBlockZ + (rand >> 8 & 15)));
            if (strikePos != null && this.isRainingAt(strikePos)) {
                DifficultyInstance difficultyinstance = this.getDifficultyForLocation(strikePos);
                if (this.getGameRules().getBoolean("doMobSpawning")
                        && this.rand.nextDouble() < (double) difficultyinstance.getAdditionalDifficulty() * 0.01D) {
                    EntitySkeletonHorse skeletonHorse = new EntitySkeletonHorse((World) (Object) this);
                    skeletonHorse.setTrap(true);
                    skeletonHorse.setGrowingAge(0);
                    skeletonHorse.setPosition(strikePos.getX(), strikePos.getY(), strikePos.getZ());
                    if (this.canAddEntity(skeletonHorse)) { this.spawnEntity(skeletonHorse); }
                    this.addWeatherEffect(new EntityLightningBolt((World) (Object) this,
                            strikePos.getX(), strikePos.getY(), strikePos.getZ(), true));
                }
                else {
                    this.addWeatherEffect(new EntityLightningBolt((World) (Object) this,
                            strikePos.getX(), strikePos.getY(), strikePos.getZ(), false));
                }
            }
        }
        this.profiler.endStartSection("iceandsnow");
        if (this.provider.canDoRainSnowIce(chunk) && this.rand.nextInt(16) == 0) {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int j2 = this.updateLCG >> 2;
            BlockPos block = this.getPrecipitationHeight(new BlockPos(chunkBlockX + (j2 & 15), 0, chunkBlockZ + (j2 >> 8 & 15)));
            BlockPos blockBelow = block.down();
            if (this.isAreaLoaded(blockBelow, 1)) {
                if (this.canBlockFreezeNoWater(blockBelow)) { this.setBlockState(blockBelow, Blocks.ICE.getDefaultState()); }
            }
            if (raining && isBlockLoaded(block) && this.canSnowAt(block, true)) { this.setBlockState(block, Blocks.SNOW_LAYER.getDefaultState()); }
            if (raining && isBlockLoaded(blockBelow) && this.getBiome(blockBelow).canRain()) {
                this.getBlockState(blockBelow).getBlock().fillWithRain((World) (Object) this, blockBelow);
            }
        }
        this.profiler.endSection();
    }

    @Unique @Nullable private BlockPos rdpl$adjustPosToNearbyEntityRubic(BlockPos strikeTarget) {
        Chunk column = this.rdpl$getCubeCache().getColumn(Coords.blockToCube(strikeTarget.getX()), Coords.blockToCube(strikeTarget.getZ()),
                ICubeProviderServer.Requirement.GET_CACHED);
        if (column == null) { return null; }
        strikeTarget = column.getPrecipitationHeight(strikeTarget);
        Cube cube = this.rdpl$getCubeCache().getLoadedCube(CubePos.fromBlockCoords(strikeTarget));
        if (cube == null) { return null; }
        AxisAlignedBB aabb = (new AxisAlignedBB(strikeTarget)).grow(3.0D);
        Iterable<EntityLivingBase> setOfLiving = cube.getEntityContainer().getEntitySet().getByClass(EntityLivingBase.class);
        for (EntityLivingBase entity : setOfLiving) {
            if (!entity.isEntityAlive()) { continue; }
            BlockPos entityPos = entity.getPosition();
            if (entityPos.getY() < column.getHeightValue(Coords.blockToLocal(entityPos.getX()), Coords.blockToLocal(entityPos.getZ()))) { continue; }
            if (entity.getEntityBoundingBox().intersects(aabb)) { return entityPos; }
        }
        return strikeTarget;
    }

    @Redirect(method = "updateBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getInt(Ljava/lang/String;)I"), require = 1)
    public int redirectGetRandomTickSpeed(GameRules gameRules, String name) { return this.rdpl$isRubicWorld() ? 0 : gameRules.getInt(name); }
}
