package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.rubic.lighting.LightingManager;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldSettings;
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;
import mctmods.resourcedatapackloader.content.worldgen.beard.PredictedChunk;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.IntRange;
import mctmods.resourcedatapackloader.util.NotRubicWorldException;
import static mctmods.resourcedatapackloader.util.Coords.blockToCube;
import static mctmods.resourcedatapackloader.util.Coords.cubeToMinBlock;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.world.border.WorldBorder;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;

@SuppressWarnings({"ConstantConditions", "DataFlowIssue"}) @Mixin(World.class) public abstract class MixinWorld implements IRubicWorldInternal, IRubicWorld {
    @Unique private static boolean rdpl$told;
    @Unique private static ChunkPos rdpl$during;
    @Unique private static int rdpl$lastX = Integer.MIN_VALUE;
    @Unique @Nullable private static Chunk rdpl$lastChunk;
    @Unique private static int rdpl$lastZ = Integer.MIN_VALUE;
    @Unique private static final int rdpl$NOTIFY_NEIGHBORS = 1;
    @Unique private static final int rdpl$SUPPRESS_OBSERVERS = 16;

    @Inject(method = "getDifficulty", at = @At("HEAD"), cancellable = true) private void rdpl$difficultyAsAsked(CallbackInfoReturnable<EnumDifficulty> cir) {
        World self = (World) (Object) this;
        if (self.provider == null) { return; }
        EnumDifficulty asked = ContentTerrain.difficultyFor(self.provider.getDimension());
        if (asked != null) { cir.setReturnValue(asked); }
    }

    @Inject(method = "updateEntities", at = @At("HEAD"), cancellable = true) private void rdpl$standStillWhileLandIsMade(CallbackInfo ci) {
        if (((World) (Object) this).isRemote || !ContentPregen.busy()) { return; }
        rdpl$letGoOfUnloadedEntities();
        ci.cancel();
    }

    @Unique private void rdpl$letGoOfUnloadedEntities() {
        if (unloadedEntityList.isEmpty()) { return; }
        loadedEntityList.removeAll(unloadedEntityList);
        for (Entity leaving : unloadedEntityList) {
            if (leaving.addedToChunk && isChunkLoaded(leaving.chunkCoordX, leaving.chunkCoordZ, true)) { ((World) (Object) this).getChunk(leaving.chunkCoordX, leaving.chunkCoordZ).removeEntity(leaving); }
        }
        for (Entity leaving : unloadedEntityList) { onEntityRemoved(leaving); }
        unloadedEntityList.clear();
    }

    @Redirect(method = "updateEntityWithOptionalForce", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"))
    private void rdpl$slowDistant(Entity entity) {
        if (ContentEntityTicks.slowedNow(entity)) { ContentEntityTicks.age(entity); }
        else { entity.onUpdate(); }
    }

    @Redirect(method = "getBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk rdpl$leaveLandAlone(World self, BlockPos pos) {
        ChunkPos populating = IChunk.rdpl$getPopulating();
        if (populating == null) { return self.getChunk(pos); }
        if (pos.getX() >> 4 == populating.x && pos.getZ() >> 4 == populating.z) { return self.getChunk(pos); }
        return rdpl$predictInstead(self, pos, populating);
    }

    @Unique private Chunk rdpl$predictInstead(World self, BlockPos pos, ChunkPos populating) {
        if (populating != rdpl$during) {
            rdpl$during = populating;
            rdpl$lastX = Integer.MIN_VALUE;
            rdpl$lastZ = Integer.MIN_VALUE;
            rdpl$lastChunk = null;
        }
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        if (chunkX == populating.x && chunkZ == populating.z) { return self.getChunk(pos); }
        if (chunkX == rdpl$lastX && chunkZ == rdpl$lastZ && rdpl$lastChunk != null) { return rdpl$lastChunk; }
        Chunk loaded = self.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
        if (loaded != null) {
            rdpl$lastX = chunkX;
            rdpl$lastZ = chunkZ;
            rdpl$lastChunk = loaded;
            return loaded;
        }
        if (!rdpl$told) {
            rdpl$told = true;
            ContentLog.LOGGER.info("Something read land that has not been made yet while another chunk was being filled in. Making it there and then would have made everything around it in turn, so it is answered from the shape the land will take instead");
        }
        return PredictedChunk.of(self);
    }

    @Inject(method = "isSpawnChunk", at = @At("HEAD"), cancellable = true) private void rdpl$spawnChunkRadius(int x, int z, CallbackInfoReturnable<Boolean> cir) {
        World world = (World) (Object) this;
        int radius = ContentSpawnChunks.radius(world.provider.getDimension());
        if (radius == ContentSpawnChunks.VANILLA) { return; }
        if (radius <= 0) {
            cir.setReturnValue(false);
            return;
        }
        BlockPos spawn = world.getSpawnPoint();
        int offsetX = x * 16 + 8 - spawn.getX();
        int offsetZ = z * 16 + 8 - spawn.getZ();
        cir.setReturnValue(offsetX >= -radius && offsetX <= radius && offsetZ >= -radius && offsetZ <= radius);
    }

    @Inject(method = "getGameRules", at = @At("HEAD"), cancellable = true) private void rdpl$dimensionRules(CallbackInfoReturnable<GameRules> cir) {
        GameRules rules = ContentGameRules.forWorld((World) (Object) this);
        if (rules != null) { cir.setReturnValue(rules); }
    }

    @ModifyVariable(method = "markAndNotifyBlock", at = @At("HEAD"), argsOnly = true, index = 5, remap = false) private int rdpl$suppressObserverScan(int flags) {
        if (IChunk.rdpl$getPopulating() == null) { return flags; }
        return (flags | rdpl$SUPPRESS_OBSERVERS) & ~rdpl$NOTIFY_NEIGHBORS;
    }

    @Shadow protected IChunkProvider chunkProvider;
    @Shadow protected boolean scheduledUpdatesAreImmediate;
    @Shadow @Final @Mutable public WorldProvider provider;
    @Shadow @Final public Random rand;
    @Shadow @Final public boolean isRemote;
    @Shadow protected WorldInfo worldInfo;
    @Shadow @Final public Profiler profiler;
    @Shadow protected int updateLCG;

    @Shadow public abstract GameRules getGameRules();

    @Shadow public abstract boolean isRaining();

    @Shadow public abstract boolean isThundering();

    @Shadow public abstract boolean isRainingAt(BlockPos position);

    @Shadow public abstract DifficultyInstance getDifficultyForLocation(BlockPos pos);

    @Shadow public abstract boolean isAreaLoaded(BlockPos center, int radius);

    @Shadow public abstract boolean canBlockFreezeNoWater(BlockPos pos);

    @Shadow public abstract boolean canSnowAt(BlockPos pos, boolean checkLight);

    @Shadow protected abstract boolean isChunkLoaded(int i, int i1, boolean allowEmpty);
    @Shadow @Final public List<Entity> loadedEntityList;
    @Shadow @Final protected List<Entity> unloadedEntityList;
    @Shadow public abstract void onEntityRemoved(Entity entityIn);

    @Unique @Nullable protected LightingManager rdpl$lightingManager;
    @Unique protected boolean rdpl$isRubicWorld;
    @Unique protected int rdpl$minHeight = 0;
    @Unique protected int rdpl$maxHeight = 256;
    @Unique protected int rdpl$fakedMaxHeight = 0;
    @Unique private int rdpl$maxGenerationHeight = 256;

    @Shadow public abstract boolean isValid(BlockPos pos);

    @Shadow public abstract BlockPos getPrecipitationHeight(BlockPos pos);

    @Shadow public abstract boolean setBlockState(BlockPos pos, IBlockState state);

    @Shadow public abstract boolean isBlockLoaded(BlockPos pos);

    @Shadow public abstract Biome getBiome(BlockPos pos);

    @Shadow public abstract Chunk getChunk(BlockPos pos);

    @Unique protected void rdpl$initRubicWorld(IntRange heightRange, IntRange generationRange) {
        ((IRubicWorldSettings) worldInfo).rdpl$setRubic(true);
        this.rdpl$minHeight = heightRange.getMin();
        this.rdpl$maxHeight = heightRange.getMax();
        this.rdpl$fakedMaxHeight = this.rdpl$maxHeight;
        this.rdpl$maxGenerationHeight = generationRange.getMax();
    }

    @Override public boolean rdpl$isRubicWorld() { return this.rdpl$isRubicWorld; }

    @Override public int rdpl$getMinHeight() { return this.rdpl$minHeight; }

    @Override public int rdpl$getMaxHeight() { return this.rdpl$maxHeight; }

    @Override public int rdpl$getMaxGenerationHeight() { return this.rdpl$maxGenerationHeight; }

    @Override @Nonnull public ICubeProviderInternal rdpl$getCubeCache() {
        if (!this.rdpl$isRubicWorld()) { throw new NotRubicWorldException(); }
        return (ICubeProviderInternal) this.chunkProvider;
    }

    @Override @Nonnull public LightingManager rdpl$getLightingManager() {
        if (!this.rdpl$isRubicWorld()) { throw new NotRubicWorldException(); }
        assert this.rdpl$lightingManager != null;
        return this.rdpl$lightingManager;
    }

    @Override public boolean rdpl$testForCubes(@Nonnull CubePos start, @Nonnull CubePos end, @Nonnull Predicate<? super ICube> cubeAllowed) {
        int minCubeX = start.getX();
        int minCubeY = start.getY();
        int minCubeZ = start.getZ();
        int maxCubeX = end.getX();
        int maxCubeY = end.getY();
        int maxCubeZ = end.getZ();
        for (int cubeX = minCubeX; cubeX <= maxCubeX; cubeX++) {
            for (int cubeY = minCubeY; cubeY <= maxCubeY; cubeY++) {
                for (int cubeZ = minCubeZ; cubeZ <= maxCubeZ; cubeZ++) {
                    Cube cube = this.rdpl$getCubeCache().getLoadedCube(cubeX, cubeY, cubeZ);
                    if (!cubeAllowed.test(cube)) { return false; }
                }
            }
        }
        return true;
    }

    @Override @Nonnull public Cube rdpl$getCubeFromCubeCoords(int cubeX, int cubeY, int cubeZ) { return this.rdpl$getCubeCache().getCube(cubeX, cubeY, cubeZ); }

    @Override @Nonnull public Cube rdpl$getCubeFromBlockCoords(@Nonnull BlockPos pos) {
        return this.rdpl$getCubeFromCubeCoords(blockToCube(pos.getX()), blockToCube(pos.getY()), blockToCube(pos.getZ()));
    }

    @Override public void rdpl$tickRubicWorld() { throw new NoSuchMethodError("World.rdpl$tickRubicWorld: Classes extending World need to implement rdpl$tickRubicWorld in Rubic"); }

    @Override public void rdpl$fakeWorldHeight(int height) { this.rdpl$fakedMaxHeight = height; }

    /**
     * @author tgstyle
     * @reason Answer the faked compatibility height when one is active, else the provider height.
     */
    @Overwrite public int getHeight() {
        if (rdpl$fakedMaxHeight != 0) { return rdpl$fakedMaxHeight; }
        return this.provider.getHeight();
    }

    @Inject(method = "markChunkDirty", at = @At("HEAD"), cancellable = true) private void onMarkChunkDirty(BlockPos pos, TileEntity unusedTileEntity, CallbackInfo ci) {
        if (this.rdpl$isRubicWorld()) {
            Cube cube = this.rdpl$getCubeCache().getLoadedCube(CubePos.fromBlockCoords(pos));
            if (cube != null) { cube.markDirty(); }
            ci.cancel();
        }
    }

    /**
     * @author tgstyle
     * @reason Route block state lookups through the cube provider on rubic worlds.
     */
    @Overwrite public IBlockState getBlockState(BlockPos pos) {
        if (this.isOutsideBuildHeight(pos)) { return Objects.requireNonNull(Blocks.AIR).getDefaultState(); }
        if (this.rdpl$isRubicWorld) {
            ICube cube = ((ICubeProviderInternal) this.chunkProvider)
                    .getCube(Coords.blockToCube(pos.getX()), Coords.blockToCube(pos.getY()), Coords.blockToCube(pos.getZ()));
            return cube.getBlockState(pos);
        }
        else {
            Chunk chunk = this.getChunk(pos);
            return chunk.getBlockState(pos);
        }
    }

    @Inject(method = "getTopSolidOrLiquidBlock", at = @At("HEAD"), cancellable = true) private void getTopSolidOrLiquidBlockRubic(BlockPos pos, CallbackInfoReturnable<BlockPos> cir) {
        if (!rdpl$isRubicWorld()) { return; }
        Chunk chunk = this.getChunk(pos);
        BlockPos currentPos = getPrecipitationHeight(pos);
        int minY = currentPos.getY() - 64;
        while (currentPos.getY() >= minY) {
            BlockPos nextPos = currentPos.down();
            IBlockState state = chunk.getBlockState(nextPos);
            if (state.getMaterial().blocksMovement()
                    && !state.getBlock().isLeaves(state, (IBlockAccess) this, nextPos)
                    && !state.getBlock().isFoliage((IBlockAccess) this, nextPos)) { break; }
            currentPos = nextPos;
        }
        cir.setReturnValue(currentPos);
    }

    @Override public boolean rdpl$isBlockColumnLoaded(@Nonnull BlockPos pos) { return rdpl$isBlockColumnLoaded(pos, true); }

    @Unique public boolean rdpl$isBlockColumnLoaded(@Nonnull BlockPos pos, boolean allowEmpty) {
        return this.isChunkLoaded(blockToCube(pos.getX()), blockToCube(pos.getZ()), allowEmpty);
    }

    @Unique private int updateEntity_entityPosY;
    @Unique private int updateEntity_entityPosX;
    @Unique private int updateEntity_entityPosZ;

    @Shadow private boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty) { throw new Error(); }

    @Group(name = "updateEntity", max = 2, min = 2) @Redirect(method = "updateEntityWithOptionalForce",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isAreaLoaded(IIIIIIZ)Z"),
            require = 1)
    private boolean canUpdateEntity(World _this, int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd,
                                    boolean allowEmpty) {
        if (!this.rdpl$isRubicWorld()) { return isAreaLoaded(xStart, yStart, zStart, xEnd, yEnd, zEnd, allowEmpty); }
        BlockPos entityPos = new BlockPos(updateEntity_entityPosX, updateEntity_entityPosY, updateEntity_entityPosZ);
        if (!isValid(entityPos)) { return true; }
        int r = (xEnd - xStart) >> 1;
        return isAreaLoaded(updateEntity_entityPosX - r, updateEntity_entityPosY - r, updateEntity_entityPosZ - r,
                updateEntity_entityPosX + r, updateEntity_entityPosY + r, updateEntity_entityPosZ + r, true);
    }

    @Group(name = "updateEntity") @Inject(method = "updateEntityWithOptionalForce",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getPersistentChunks()Lcom/google/common/collect/ImmutableSetMultimap;", remap = false),
            require = 1)
    private void onIsAreaLoadedForUpdateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate, CallbackInfo ci) {
        updateEntity_entityPosY = MathHelper.floor(entityIn.posY);
        updateEntity_entityPosX = MathHelper.floor(entityIn.posX);
        updateEntity_entityPosZ = MathHelper.floor(entityIn.posZ);
    }

    /**
     * @author tgstyle
     * @reason Test against the rubic min and max heights instead of the fixed vanilla range.
     */
    @Overwrite public boolean isOutsideBuildHeight(BlockPos pos) { return pos.getY() >= rdpl$getMaxHeight() || pos.getY() < rdpl$getMinHeight(); }

    /**
     * @author tgstyle
     * @reason Clamp light lookups to the rubic height range before asking the chunk.
     */
    @Overwrite public int getLight(BlockPos pos) {
        if (pos.getY() < this.rdpl$getMinHeight()) { return 0; }
        if (pos.getY() >= this.rdpl$getMaxHeight()) { return EnumSkyBlock.SKY.defaultLightValue; }
        return this.getChunk(pos).getLightSubtracted(pos, 0);
    }

    @Group(name = "getLightForHeightOverride", min = 2, max = 2) @ModifyConstant(method = "getLightFor",
            constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.LESS_THAN_ZERO))
    private int getLightForGetMinYReplace(int origY) { return this.rdpl$getMinHeight(); }

    @Group(name = "isLoaded", max = 1) @Inject(method = "isAreaLoaded(IIIIIIZ)Z", at = @At(value = "HEAD"), cancellable = true, require = 1)
    private void isAreaLoadedInject(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty,
                                    @Nonnull CallbackInfoReturnable<Boolean> cbi) {
        if (!this.rdpl$isRubicWorld()) { return; }
        boolean ret = (this.isRemote && allowEmpty) ||
                this.rdpl$testForCubes(
                        xStart, yStart, zStart,
                        xEnd, yEnd, zEnd,
                        Objects::nonNull);
        cbi.setReturnValue(ret);
    }

    @Inject(method = "isBlockLoaded(Lnet/minecraft/util/math/BlockPos;Z)Z", cancellable = true, at = @At(value = "HEAD"))
    private void isBlockLoaded(BlockPos pos, boolean allowEmpty, CallbackInfoReturnable<Boolean> cbi) {
        if (!rdpl$isRubicWorld()) { return; }
        ICube cube = this.rdpl$getCubeCache().getLoadedCube(blockToCube(pos.getX()), blockToCube(pos.getY()), blockToCube(pos.getZ()));
        if (allowEmpty) { cbi.setReturnValue(cube != null); }
        else { cbi.setReturnValue(cube != null && !(cube instanceof BlankCube)); }
    }

    @Unique private boolean rdpl$columnOrCubeLoaded(int chunkX, int blockY, int chunkZ, boolean allowEmpty) {
        if (!rdpl$isRubicWorld()) { return this.isChunkLoaded(chunkX, chunkZ, allowEmpty); }
        ICube cube = this.rdpl$getCubeCache().getLoadedCube(chunkX, blockToCube(blockY), chunkZ);
        if (allowEmpty) { return cube != null; }
        return cube != null && !(cube instanceof BlankCube);
    }

    @Redirect(method = "updateEntityWithOptionalForce",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isChunkLoaded(IIZ)Z", ordinal = 0))
    private boolean updateEntityWithOptionalForce_isChunkLoaded0(World world, int chunkX, int chunkZ, boolean allowEmpty, Entity entityIn, boolean forceUpdate) {
        assert this == (Object) world;
        return rdpl$columnOrCubeLoaded(chunkX, cubeToMinBlock(entityIn.chunkCoordY), chunkZ, allowEmpty);
    }

    @Redirect(method = "updateEntityWithOptionalForce",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isChunkLoaded(IIZ)Z", ordinal = 1))
    private boolean updateEntityWithOptionalForce_isChunkLoaded1(World world, int chunkX, int chunkZ, boolean allowEmpty, Entity entityIn, boolean forceUpdate) {
        assert this == (Object) world;
        return rdpl$columnOrCubeLoaded(chunkX, MathHelper.floor(entityIn.posY), chunkZ, allowEmpty);
    }

    @Unique private int updateEntities_entityChunkBlockY;

    @Inject(method = "updateEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isChunkLoaded(IIZ)Z", ordinal = 1),
            require = 1)
    private void updateEntities_isChunkLoaded1_getLocals(CallbackInfo cbi, @Local(name = "entity2") Entity entity2) {
        updateEntities_entityChunkBlockY = cubeToMinBlock(entity2.chunkCoordY);
    }

    @Redirect(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isChunkLoaded(IIZ)Z", ordinal = 1))
    private boolean updateEntities_isChunkLoaded(World world, int chunkX, int chunkZ, boolean allowEmpty) {
        assert this == (Object) world;
        return rdpl$columnOrCubeLoaded(chunkX, updateEntities_entityChunkBlockY, chunkZ, allowEmpty);
    }

    @Inject(method = "getBiome", at = @At("HEAD"), cancellable = true) private void getBiome(BlockPos pos, CallbackInfoReturnable<Biome> ci) {
        if (!this.rdpl$isRubicWorld())
            return;
        ICube cube = this.rdpl$getCubeCache().getLoadedCube(Coords.blockToCube(pos.getX()),Coords.blockToCube(pos.getY()),Coords.blockToCube(pos.getZ()));
        if (cube == null)
            return;
        Biome biome = cube.getBiome(pos);
        ci.setReturnValue(biome);
    }

    @ModifyConstant(method = {"canSnowAtBody", "canBlockFreezeBody"}, constant = @Constant(intValue = 256), remap = false) private int canSnowAt_getMaxHeight(int _256) {
        return rdpl$getMaxHeight();
    }

    @ModifyConstant(method = {"canSnowAtBody", "canBlockFreezeBody"},
            constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO),
            remap = false)
    private int canSnowAt_getMinHeight(int zero) { return rdpl$getMinHeight(); }

    @Redirect(method = "spawnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isChunkLoaded(IIZ)Z"))
    private boolean spawnEntity_isChunkLoaded(World world, int chunkX, int chunkZ, boolean allowEmpty, Entity entityIn) {
        assert this == (Object) world;
        return rdpl$columnOrCubeLoaded(chunkX, MathHelper.floor(entityIn.posY), chunkZ, allowEmpty);
    }

    @Shadow public abstract WorldBorder getWorldBorder();

    @Shadow public abstract boolean isInsideWorldBorder(Entity entityToCheck);

    @Redirect(method = "markAndNotifyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;isPopulated()Z"))
    private boolean markNotifyBlock_CubeCheck(Chunk _this,
                                              BlockPos pos, Chunk chunk, IBlockState iblockstate,
                                              IBlockState newState, int flags) {
        if (!this.rdpl$isRubicWorld()) { return chunk.isPopulated(); }
        IColumn column = (IColumn) chunk;
        ICube cube = column.getCube(Coords.blockToCube(pos.getY()));
        return cube.isFullyPopulated();
    }

    /**
     * @author tgstyle
     * @reason Rebuild collision gathering around column-loaded checks so it works across the rubic height range.
     */
    @Overwrite private boolean getCollisionBoxes(@Nullable Entity entity, AxisAlignedBB aabb, boolean flagArg, List<AxisAlignedBB> aabbList) {
        int minX = MathHelper.floor(aabb.minX) - 1;
        int maxX = MathHelper.ceil(aabb.maxX) + 1;
        int minY = MathHelper.floor(aabb.minY) - 1;
        int maxY = MathHelper.ceil(aabb.maxY) + 1;
        int minZ = MathHelper.floor(aabb.minZ) - 1;
        int maxZ = MathHelper.ceil(aabb.maxZ) + 1;
        WorldBorder worldborder = this.getWorldBorder();
        boolean entityOutsideOfBorder = entity != null && entity.isOutsideBorder();
        boolean entityInsideOfBorder = entity != null && this.isInsideWorldBorder(entity);
        IBlockState iblockstate = Objects.requireNonNull(Blocks.STONE).getDefaultState();
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        try {
            for (int x = minX; x < maxX; ++x) {
                for (int z = minZ; z < maxZ; ++z) {
                    boolean isXboundary = x == minX || x == maxX - 1;
                    boolean isZBoundary = z == minZ || z == maxZ - 1;
                    if ((!isXboundary || !isZBoundary) && this.rdpl$isBlockColumnLoaded(pos.setPos(x, 64, z))) {
                        for (int y = minY; y < maxY; ++y) {
                            if ((!isXboundary && !isZBoundary || y != maxY - 1) && isBlockLoaded(pos.setPos(x, y, z))) {
                                if (flagArg) {
                                    if (x < -30000000 || x >= 30000000 || z < -30000000 || z >= 30000000) { return true; }
                                }
                                else if (entity != null && entityOutsideOfBorder == entityInsideOfBorder) { entity.setOutsideBorder(!entityInsideOfBorder); }
                                pos.setPos(x, y, z);
                                IBlockState iblockstate1;
                                if (!flagArg && !worldborder.contains(pos) && entityInsideOfBorder) { iblockstate1 = iblockstate; }
                                else { iblockstate1 = this.getBlockState(pos); }
                                iblockstate1
                                        .addCollisionBoxToList((World) (Object) this, pos, aabb, aabbList, entity, false);
                                net.minecraftforge.common.MinecraftForge.EVENT_BUS
                                        .post(new net.minecraftforge.event.world.GetCollisionBoxesEvent((World) (Object) this, null, aabb, aabbList));
                                if (flagArg && !aabbList.isEmpty()) { return true; }
                            }
                        }
                    }
                }
            }
        } finally {
            pos.release();
        }
        return !aabbList.isEmpty();
    }
}
