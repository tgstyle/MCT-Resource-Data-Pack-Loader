package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.*;
import mctmods.resourcedatapackloader.content.rubic.world.column.ColumnTileEntityMap;
import mctmods.resourcedatapackloader.content.rubic.world.column.CubeMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.*;
import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Coords;
import static mctmods.resourcedatapackloader.util.Coords.blockToCube;
import static mctmods.resourcedatapackloader.util.Coords.blockToLocal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import javax.annotation.Nonnull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Intrinsic;
import java.util.Collection;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Unique;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.google.common.base.Predicate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent.Load;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.objectweb.asm.Opcodes;

@Mixin(value = Chunk.class, priority = 999) @Implements({@Interface(iface = IColumn.class, prefix = "chunk$"), @Interface(iface = IColumnInternal.class, prefix = "chunk_internal$")})
public abstract class MixinChunk {
    @Shadow public abstract ChunkPos getPos();

    @Inject(method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At("HEAD"))
    private void rdpl$traceCascade(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, CallbackInfo ci) {
        ChunkPos parent = IChunk.rdpl$getPopulating();
        if (parent != null) { ContentCascade.report(parent, getPos()); }
    }

    @Unique private static final Set<String> rdpl$told = new HashSet<>();

    @Inject(method = "logCascadingWorldGeneration", at = @At("HEAD"), remap = false) private void rdpl$whoAsked(CallbackInfo ci) {
        if (!ContentLog.LOGGER.debugEnabled() || rdpl$told.size() >= 12) { return; }
        Throwable trace = new Throwable("who reached for land that was not there");
        StringBuilder key = new StringBuilder();
        int named = 0;
        for (StackTraceElement frame : trace.getStackTrace()) {
            String owner = frame.getClassName();
            if (owner.startsWith("net.minecraft.") || owner.startsWith("java.") || owner.startsWith("mctmods.")) { continue; }
            key.append(owner).append('.').append(frame.getMethodName()).append(' ');
            if (++named >= 4) { break; }
        }
        if (named == 0) { key.append("nothing outside the game itself"); }
        if (!rdpl$told.add(key.toString())) { return; }
        ContentLog.LOGGER.debug("Land was made in the middle of making other land, by a caller not seen before. This is number {} of the different ones", rdpl$told.size(), trace);
    }

    @Shadow private boolean isTerrainPopulated;

    @SuppressWarnings("ConstantValue") @Inject(method = "populate(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At("HEAD"), cancellable = true)
    private void rdpl$dressNothingWhileLighting(IChunkProvider chunkProvider, IChunkGenerator chunkGenrator, CallbackInfo ci) {
        if (isTerrainPopulated || !ContentPregen.dressLater((Chunk) (Object) this)) { return; }
        ContentChunkWatch.dressingHeldOff();
        ci.cancel();
    }

    @Inject(method = "onTick", at = @At("HEAD"))
    private void rdpl$dressWhenStranded(boolean skipRecheckGaps, CallbackInfo ci) {
        if (isTerrainPopulated || world.isRemote || ContentPregen.lightingOnly()) { return; }
        if (((x + z) & 15) != (int) (world.getTotalWorldTime() & 15L)) { return; }
        IChunkProvider provider = world.getChunkProvider();
        if (!(provider instanceof ChunkProviderServer)) { return; }
        ChunkProviderServer server = (ChunkProviderServer) provider;
        Chunk self = (Chunk) (Object) this;
        if (RubicWorldControl.rubicWorld(server) || server.getLoadedChunk(x, z) != self) { return; }
        self.populate(server, server.chunkGenerator);
        if (isTerrainPopulated) { ContentLog.LOGGER.debug("Chunk {}, {} sat undressed in a player's view and is dressed on its tick", x, z); }
    }

    @Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;checkLight()V"))
    private void rdpl$lightAfterDressing(Chunk chunk) {
        isTerrainPopulated = true;
        ContentChunkWatch.lightDeferred();
    }

    @Redirect(method = "populate(Lnet/minecraft/world/gen/IChunkGenerator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/IChunkGenerator;populate(II)V"))
    private void rdpl$timeDecoration(IChunkGenerator generator, int x, int z) {
        if (!ContentChunkWatch.watching()) {
            generator.populate(x, z);
            return;
        }
        long start = System.nanoTime();
        generator.populate(x, z);
        ContentChunkWatch.decorated(System.nanoTime() - start);
    }

    @Shadow @Final private ExtendedBlockStorage[] storageArrays;
    @Shadow @Final public static ExtendedBlockStorage NULL_BLOCK_STORAGE;
    @Shadow @Final public int x;
    @Shadow @Final public int z;
    @Shadow @Final @Mutable private Map<BlockPos, TileEntity> tileEntities;
    @Shadow @Final private int[] heightMap;
    @Shadow @Final private int[] precipitationHeightMap;
    @Shadow @Final private World world;
    @Shadow private boolean loaded;
    @Shadow private boolean ticked;
    @Shadow private boolean isLightPopulated;
    @Shadow private boolean dirty;
    @Unique private CubeMap rdpl$cubeMap;
    @Unique private IHeightMap rdpl$opacityIndex;
    @Unique private Cube rdpl$cachedCube;
    @Unique private StagingHeightMap rdpl$stagingHeightMap;
    @Unique private boolean rdpl$isColumn = false;
    @Unique private boolean rdpl$pregenDone;
    @Unique private ChunkPrimer rdpl$compatGenerationPrimer;
    @Unique private boolean rdpl$compatArraysFilled;
    @Unique private int rdpl$floorCube = Integer.MIN_VALUE;
    @Unique private int rdpl$ceilingCube;

    @Shadow public abstract byte[] getBiomeArray();

    @Unique @SuppressWarnings("unchecked") public <T extends World & IRubicWorldInternal> T rdpl$getRubicWorld() { return (T) this.world; }

    @Unique private boolean rdpl$cubeLoadedAt(int blockY) {
        ICube cube = ((IColumn) this).getLoadedCube(blockToCube(blockY));
        return cube != null && cube.isCubeLoaded();
    }

    @Unique private int rdpl$clampCubeY(int cubeY) { return MathHelper.clamp(cubeY, blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()), blockToCube(rdpl$getRubicWorld().rdpl$getMaxHeight())); }

    @Unique private boolean rdpl$compatGenerating() { return rdpl$compatGenerationPrimer != null; }

    @Unique private void rdpl$fillCompatArrays() {
        rdpl$compatArraysFilled = true;
        boolean skylight = world.provider.hasSkyLight();
        for (int y = 0; y < 256; y++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localX = 0; localX < 16; localX++) {
                    IBlockState state = rdpl$compatGenerationPrimer.getBlockState(localX, y, localZ);
                    if (state.getMaterial() == Material.AIR) { continue; }
                    if (storageArrays[y >> 4] == NULL_BLOCK_STORAGE) { storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4, skylight); }
                    storageArrays[y >> 4].set(localX, y & 15, localZ, state);
                }
            }
        }
    }

    @Unique @Nullable private ExtendedBlockStorage getEBS_Rubic(int index) {
        if (rdpl$compatGenerating()) {
            if (!rdpl$compatArraysFilled) { rdpl$fillCompatArrays(); }
            return index >= 0 && index < 16 ? storageArrays[index] : NULL_BLOCK_STORAGE;
        }
        if (rdpl$floorCube == Integer.MIN_VALUE) {
            rdpl$floorCube = blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight());
            rdpl$ceilingCube = blockToCube(rdpl$getRubicWorld().rdpl$getMaxHeight());
        }
        if (index < rdpl$floorCube || index >= rdpl$ceilingCube) { return NULL_BLOCK_STORAGE; }
        if (!rdpl$isColumn) { return storageArrays[index - rdpl$floorCube]; }
        if (rdpl$cachedCube != null && rdpl$cachedCube.getY() == index) { return rdpl$cachedCube.getStorage(); }
        Cube cube = rdpl$cubeMap.get(index);
        if (cube == null) { cube = rdpl$getRubicWorld().rdpl$getCubeCache().getCube(this.x, index, this.z); }
        if (!(cube instanceof BlankCube)) { rdpl$cachedCube = cube; }
        return cube.getStorage();
    }

    @Unique private void setEBS_Rubic(int index, ExtendedBlockStorage ebs) {
        if (rdpl$compatGenerating()) {
            if (!rdpl$compatArraysFilled) { rdpl$fillCompatArrays(); }
            if (index >= 0 && index < 16) { storageArrays[index] = ebs; }
            return;
        }
        if (!rdpl$isColumn) {
            storageArrays[index - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight())] = ebs;
            return;
        }
        if (index < blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()) || index >= blockToCube(rdpl$getRubicWorld().rdpl$getMaxHeight())) { return; }
        if (index >= 0 && index < 16) { storageArrays[index] = ebs; }
        if (rdpl$cachedCube != null && rdpl$cachedCube.getY() == index) {
            rdpl$cachedCube.setStorage(ebs);
            return;
        }
        Cube loaded = rdpl$getRubicWorld().rdpl$getCubeCache().getLoadedCube(this.x, index, this.z);
        if (loaded == null) { return; }
        if (loaded.getStorage() == null) { loaded.setStorage(ebs); }
        else {
            throw new IllegalStateException(String.format(
                    "Attempted to set a Cube ExtendedBlockStorage that already exists. "
                            + "This is not supported. "
                            + "CubePos(%d, %d, %d), loadedCube(%s), loadedCubeStorage(%s)",
                    this.x, index, this.z,
                    loaded, loaded.getStorage()));
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;II)V", at = @At(value = "RETURN"))
    private void rubicChunkColumn_construct(World worldIn, int x, int z, CallbackInfo cbi) {
        if (worldIn == null) { return; }
        if (!((IRubicWorld) worldIn).rdpl$isRubicWorld()) { return; }
        this.rdpl$isColumn = true;
        this.rdpl$cubeMap = new CubeMap();
        if (worldIn.isRemote) { this.rdpl$opacityIndex = new ClientHeightMap((Chunk) (Object) this, heightMap); }
        else { this.rdpl$opacityIndex = new ServerHeightMap(heightMap); }
        this.rdpl$stagingHeightMap = new StagingHeightMap();
        this.tileEntities = new ColumnTileEntityMap((IColumn) this);
        Arrays.fill(getBiomeArray(), (byte) -1);
    }

    @Redirect(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ChunkPrimer;II)V",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=get",
                    target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;"
            ))
    private ExtendedBlockStorage init_getStorage(ExtendedBlockStorage[] ebs, int y) {
        return ebs[y - (this.rdpl$isColumn ? 0 : Coords.blockToCube(((IMinMaxHeight) world).rdpl$getMinHeight()))];
    }

    @Redirect(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ChunkPrimer;II)V",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=set",
                    target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;"
            ))
    private void getBlockState_getMaxHeight(ExtendedBlockStorage[] ebs, int y, ExtendedBlockStorage val) {
        ebs[y - (this.rdpl$isColumn ? 0 : Coords.blockToCube(((IMinMaxHeight) world).rdpl$getMinHeight()))] = val;
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ChunkPrimer;II)V",
            constant = @Constant(intValue = 16, ordinal = 0), require = 1)
    private int getInitChunkLoopEnd(int _16, World worldIn, ChunkPrimer primer, int x, int z) {
        if (((IRubicWorldInternal.IServer) worldIn).rdpl$isCompatGenerationScope()) {
            this.rdpl$compatGenerationPrimer = primer;
            return -1;
        }
        return _16;
    }

    @Inject(method = "getTopFilledSegment", at = @At(value = "HEAD"), cancellable = true) private void getTopFilledSegment_Rubic(CallbackInfoReturnable<Integer> cbi) {
        if (!rdpl$isColumn || rdpl$compatGenerating()) { return; }
        int blockY = Coords.NO_HEIGHT;
        for (int localX = 0; localX < Cube.SIZE; localX++) {
            for (int localZ = 0; localZ < Cube.SIZE; localZ++) {
                int y = this.rdpl$opacityIndex.getTopBlockY(localX, localZ);
                if (y > blockY) { blockY = y; }
            }
        }
        if (blockY < rdpl$getRubicWorld().rdpl$getMinHeight()) {
            int ret = Coords.cubeToMinBlock(Coords.blockToCube(this.rdpl$getRubicWorld().provider.getAverageGroundLevel()));
            cbi.setReturnValue(ret);
            return;
        }
        int ret = Coords.cubeToMinBlock(Coords.blockToCube(blockY));
        cbi.setReturnValue(ret);
    }

    @Inject(method = "generateSkylightMap", at = @At(value = "HEAD"), cancellable = true) private void generateSkylightMap_Rubic_Replace(CallbackInfo cbi) {
        if (rdpl$isColumn) { cbi.cancel(); }
    }

    @Nullable @Redirect(method = "generateSkylightMap", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
            opcode = Opcodes.GETFIELD, args = "array=get"
    ))
    private ExtendedBlockStorage generateSkylightMapRedirectEBSAccess(ExtendedBlockStorage[] array, int index) { return getEBS_Rubic(index); }

    @Inject(method = "propagateSkylightOcclusion", at = @At(value = "HEAD"), cancellable = true) private void propagateSkylightOcclusion_Rubic_Replace(int x, int z, CallbackInfo cbi) {
        if (rdpl$isColumn) { cbi.cancel(); }
    }

    @ModifyVariable(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;set(IIILnet/minecraft/block/state/IBlockState;)V"
            ),
            name = "flag"
    )
    private boolean setBlockStateInjectGenerateSkylightMapVanilla(boolean flag) {
        if (!rdpl$isColumn) { return flag; }
        return false;
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;relightBlock(III)V"))
    private void setBlockState_Rubic_relightBlockReplace(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir,
                                                         @Local(name = "i") int i, @Local(name = "j") int j, @Local(name = "k") int k, @Local(name = "i1") int i1) {
        if (rdpl$isColumn && !rdpl$compatGenerating() && ((IColumn) this).getCube(blockToCube(j)).isInitialLightingDone()) {
            if (i1 == j + 1) { rdpl$getRubicWorld().rdpl$getLightingManager().doOnBlockSetLightUpdates((Chunk) (Object) this, i, getHeightValue(i, k), j, k); }
            else { rdpl$getRubicWorld().rdpl$getLightingManager().doOnBlockSetLightUpdates((Chunk) (Object) this, i, i1, j, k); }
        }
    }

    @Redirect(method = "setBlockState", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;getLightFor(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I"))
    private int setBlockState_Rubic_noGetLightFor(Chunk instance, EnumSkyBlock type, BlockPos pos) {
        if (!rdpl$isColumn) { return instance.getLightFor(type, pos); }
        return 0;
    }

    @Redirect(method = "getBlockLightOpacity(III)I", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;loaded:Z", opcode = Opcodes.GETFIELD))
    private boolean getBlockLightOpacity_isChunkLoadedCubeRedirect(Chunk chunk, int x, int y, int z) {
        if (!rdpl$isColumn) { return loaded; }
        return rdpl$cubeLoadedAt(y);
    }

    @ModifyConstant(method = "getBlockState(III)Lnet/minecraft/block/state/IBlockState;",
            constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO),
            require = 1)
    private int getBlockState_getMinHeight(int zero) { return rdpl$isColumn ? Integer.MIN_VALUE : rdpl$getRubicWorld().rdpl$getMinHeight(); }

    @Redirect(method = "getBlockState(III)Lnet/minecraft/block/state/IBlockState;",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=length",
                    target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;"
            ))
    private int getBlockState_getMaxHeight(ExtendedBlockStorage[] ebs) {
        return rdpl$isColumn ? Integer.MAX_VALUE : (ebs.length - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()));
    }

    @Redirect(method = "getBlockState(III)Lnet/minecraft/block/state/IBlockState;",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=get",
                    target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;"
            ))
    private ExtendedBlockStorage getBlockState_getStorage(ExtendedBlockStorage[] ebs, int y) { return getEBS_Rubic(y); }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;set"
            + "(IIILnet/minecraft/block/state/IBlockState;)V", shift = At.Shift.AFTER))
    private void onEBSSet_setBlockState_setOpacity(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir) {
        if (!rdpl$isColumn || rdpl$compatGenerating()) { return; }
        this.dirty = true;
        if (((IColumn) this).getCube(blockToCube(pos.getY())).isSurfaceTracked()) {
            rdpl$opacityIndex.onOpacityChange(blockToLocal(pos.getX()), pos.getY(), blockToLocal(pos.getZ()), state.getLightOpacity(world, pos));
            rdpl$getRubicWorld().rdpl$getLightingManager().onHeightUpdate(pos);
        }
        else { rdpl$stagingHeightMap.onOpacityChange(blockToLocal(pos.getX()), pos.getY(), blockToLocal(pos.getZ()), state.getLightOpacity(world, pos)); }
    }

    @Redirect(method = "setBlockState", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
            opcode = Opcodes.GETFIELD, args = "array=get"
    ))
    private ExtendedBlockStorage setBlockState_Rubic_EBSGetRedirect(ExtendedBlockStorage[] array, int index) { return getEBS_Rubic(index); }

    @Redirect(method = "setBlockState", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
            opcode = Opcodes.GETFIELD, args = "array=set"
    ))
    private void setBlockState_Rubic_EBSSetRedirect(ExtendedBlockStorage[] array, int index, ExtendedBlockStorage val) { setEBS_Rubic(index, val); }

    @Inject(method = "setBlockState", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
            opcode = Opcodes.GETFIELD, args = "array=set"
    ), cancellable = true)
    private void setBlockState_Rubic_EBSSetInject(BlockPos pos, IBlockState state, CallbackInfoReturnable<IBlockState> cir) {
        if (rdpl$isColumn && !rdpl$compatGenerating() && rdpl$getRubicWorld().rdpl$getCubeCache().getLoadedCube(blockToCube(pos.getX()), blockToCube(pos.getY()), blockToCube(pos.getZ())) == null) { cir.setReturnValue(null); }
    }

    @Redirect(method = "setBlockState", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;dirty:Z", opcode = Opcodes.PUTFIELD)) private void setIsModifiedFromSetBlockState_Field(Chunk chunk, boolean isModifiedIn, BlockPos pos, IBlockState state) {
        if (rdpl$isColumn && !rdpl$compatGenerating()) { rdpl$getRubicWorld().rdpl$getCubeFromBlockCoords(pos).markDirty(); }
        else { dirty = isModifiedIn; }
    }

    @Inject(method = "getLightSubtracted", at = @At("HEAD")) private void onGetLightSubtracted(BlockPos pos, int amount, CallbackInfoReturnable<Integer> cir) {
        if (!rdpl$isColumn) { return; }
        rdpl$getRubicWorld().rdpl$getLightingManager().onGetLightSubtracted();
    }

    @Redirect(method = "setLightFor", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
            opcode = Opcodes.GETFIELD, args = "array=get"
    ))
    @Nullable private ExtendedBlockStorage setLightFor_Rubic_EBSGetRedirect(ExtendedBlockStorage[] array, int index) { return getEBS_Rubic(index); }

    @Redirect(method = "setLightFor", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
            opcode = Opcodes.GETFIELD, args = "array=set"
    ))
    private void setLightFor_Rubic_EBSSetRedirect(ExtendedBlockStorage[] array, int index, ExtendedBlockStorage ebs) { setEBS_Rubic(index, ebs); }

    @Redirect(method = "setLightFor", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;dirty:Z", opcode = Opcodes.PUTFIELD)) private void setIsModifiedFromSetLightFor_Field(Chunk chunk, boolean isModifiedIn, EnumSkyBlock type, BlockPos pos, int value) {
        if (rdpl$isColumn && !rdpl$compatGenerating()) { rdpl$getRubicWorld().rdpl$getCubeFromBlockCoords(pos).markDirty(); }
        else { dirty = isModifiedIn; }
    }

    @Nullable @Redirect(method = "getLightSubtracted", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
            opcode = Opcodes.GETFIELD, args = "array=get"
    ))
    private ExtendedBlockStorage getLightSubtracted_Rubic_EBSGetRedirect(ExtendedBlockStorage[] array, int index) { return getEBS_Rubic(index); }

    @ModifyConstant(method = "addEntity",
            constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO, intValue = 0),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE:LAST",
                            target = "Lnet/minecraft/util/math/MathHelper;floor(D)I"),
                    to = @At(
                            value = "FIELD:FIRST",
                            target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;",
                            opcode = Opcodes.GETFIELD)
            ),
            require = 1
    )
    private int addEntity_getMinY(int zero) { return blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()); }

    @Redirect(method = "addEntity",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=length",
                    target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;"
            ),
            require = 2)
    private int addEntity_getMaxHeight(ClassInheritanceMultiMap<?>[] entityLists) {
        return rdpl$isColumn ? blockToCube(rdpl$getRubicWorld().rdpl$getMaxHeight()) : (entityLists.length - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()));
    }

    @Redirect(method = "addEntity",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=get",
                    target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;"
            ),
            require = 1)
    private ClassInheritanceMultiMap<?> addEntity_getEntityList(ClassInheritanceMultiMap<?>[] entityLists, int idx, Entity entityIn) {
        if (!rdpl$isColumn) { return entityLists[idx - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight())]; }
        else if (rdpl$cachedCube != null && rdpl$cachedCube.getY() == idx) {
            rdpl$cachedCube.getEntityContainer().addEntity(entityIn);
            return null;
        }
        else {
            rdpl$getRubicWorld().rdpl$getCubeCache().getCube(this.x, idx, this.z).getEntityContainer().addEntity(entityIn);
            return null;
        }
    }

    @Redirect(method = "addEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/ClassInheritanceMultiMap;add(Ljava/lang/Object;)Z"
            ),
            require = 1)
    private boolean addEntity_getEntityList(ClassInheritanceMultiMap<Object> obj, Object p_add_1_) {
        if (!rdpl$isColumn) { return obj.add(p_add_1_); }
        assert obj == null;
        return true;
    }

    @ModifyConstant(method = "removeEntityAtIndex",
            constant = @Constant(expandZeroConditions = Constant.Condition.LESS_THAN_ZERO, intValue = 0),
            require = 2,
            slice = @Slice(
                    from = @At("HEAD"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/ClassInheritanceMultiMap;remove(Ljava/lang/Object;)Z")
            )
    )
    private int removeEntityAtIndex_getMinY(int zero) { return blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()); }

    @Redirect(method = "removeEntityAtIndex",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=length",
                    target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;"
            ),
            require = 2)
    private int removeEntityAtIndex_getMaxHeight(ClassInheritanceMultiMap<?>[] entityLists) {
        return rdpl$isColumn ? blockToCube(rdpl$getRubicWorld().rdpl$getMaxHeight()) : (entityLists.length - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight()));
    }

    @Redirect(method = "removeEntityAtIndex",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD, args = "array=get",
                    target = "Lnet/minecraft/world/chunk/Chunk;entityLists:[Lnet/minecraft/util/ClassInheritanceMultiMap;"
            ),
            require = 1)
    private ClassInheritanceMultiMap<?> removeEntityAtIndex_getEntityList(ClassInheritanceMultiMap<?>[] entityLists, int idx, Entity entityIn,
                                                                          int index) {
        if (!rdpl$isColumn) { return entityLists[idx - Coords.blockToCube(rdpl$getRubicWorld().rdpl$getMinHeight())]; }
        else if (rdpl$cachedCube != null && rdpl$cachedCube.getY() == idx) {
            rdpl$cachedCube.getEntityContainer().remove(entityIn);
            return null;
        }
        else {
            rdpl$getRubicWorld().rdpl$getCubeCache().getCube(this.x, idx, this.z).getEntityContainer().remove(entityIn);
            return null;
        }
    }

    @Redirect(method = "removeEntityAtIndex",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/ClassInheritanceMultiMap;remove(Ljava/lang/Object;)Z"
            ),
            require = 1)
    private boolean removeEntityAtIndex_getEntityList(ClassInheritanceMultiMap<Object> obj, Object p_remove_1_) {
        if (!rdpl$isColumn) { return obj.remove(p_remove_1_); }
        assert obj == null;
        return true;
    }

    @Inject(method = "getTileEntity", at = @At("HEAD"), cancellable = true)
    private void getTileEntity_CompatTemplate(BlockPos pos, Chunk.EnumCreateEntityType creationMode, CallbackInfoReturnable<TileEntity> cir) {
        if (rdpl$compatGenerating()) { cir.setReturnValue(null); }
    }

    @Inject(method = "addTileEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;)V", at = @At("HEAD"), cancellable = true)
    private void addTileEntity_CompatTemplate(BlockPos pos, TileEntity tileEntityIn, CallbackInfo cbi) {
        if (rdpl$compatGenerating()) { cbi.cancel(); }
    }

    @Redirect(method = "addTileEntity(Lnet/minecraft/tileentity/TileEntity;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;loaded:Z", opcode = Opcodes.GETFIELD))
    private boolean addTileEntity_isChunkLoadedCubeRedirect(Chunk chunk, TileEntity tileEntityIn) {
        if (!rdpl$isColumn) { return loaded; }
        return rdpl$cubeLoadedAt(tileEntityIn.getPos().getY());
    }

    @Redirect(method = "removeTileEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;loaded:Z", opcode = Opcodes.GETFIELD)) private boolean removeTileEntity_isChunkLoadedCubeRedirect(Chunk chunk, BlockPos pos) {
        if (!rdpl$isColumn) { return loaded; }
        return rdpl$cubeLoadedAt(pos.getY());
    }

    @SuppressWarnings("unchecked") @Inject(method = "getEntityLists", at = @At("HEAD"), cancellable = true) private void rdpl$entitiesFromCubes(CallbackInfoReturnable<ClassInheritanceMultiMap<Entity>[]> cir) {
        if (!rdpl$isColumn) { return; }
        Collection<Cube> cubes = rdpl$cubeMap.all();
        ClassInheritanceMultiMap<Entity>[] lists = new ClassInheritanceMultiMap[cubes.size()];
        int at = 0;
        for (Cube cube : cubes) { lists[at++] = cube.getEntitySet(); }
        cir.setReturnValue(lists);
    }

    @Inject(method = "onLoad", at = @At("HEAD"), cancellable = true) private void onChunkLoad_Rubic(CallbackInfo cbi) {
        if (!rdpl$isColumn) { return; }
        cbi.cancel();
        this.loaded = true;
        for (Cube cube : rdpl$cubeMap) { cube.onLoad(); }
        MinecraftForge.EVENT_BUS.post(new Load((Chunk) (Object) this));
    }

    @Inject(method = "onUnload", at = @At("HEAD"), cancellable = true) private void onChunkUnload_Rubic(CallbackInfo cbi) {
        if (!rdpl$isColumn) { return; }
        cbi.cancel();
        this.loaded = false;
        for (Cube cube : rdpl$cubeMap) { cube.onUnload(); }
        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload((Chunk) (Object) this));
    }

    @Inject(method = "getEntitiesWithinAABBForEntity", at = @At("HEAD"), cancellable = true) private void getEntitiesWithinAABBForEntity_Rubic(@Nullable Entity entityIn, AxisAlignedBB aabb,
                                                                                                                                               List<Entity> listToFill, Predicate<? super Entity> filter, CallbackInfo cbi) {
        if (!rdpl$isColumn) { return; }
        cbi.cancel();
        int minY = MathHelper.floor((aabb.minY - World.MAX_ENTITY_RADIUS) / Cube.SIZE_D);
        int maxY = MathHelper.floor((aabb.maxY + World.MAX_ENTITY_RADIUS) / Cube.SIZE_D);
        minY = rdpl$clampCubeY(minY);
        maxY = rdpl$clampCubeY(maxY);
        for (Cube cube : rdpl$cubeMap.cubes(minY, maxY)) {
            if (cube.getEntityContainer().getEntitySet().isEmpty()) { continue; }
            for (Entity entity : cube.getEntityContainer().getEntitySet()) {
                if (!entity.getEntityBoundingBox().intersects(aabb) || entity == entityIn) { continue; }
                if (filter == null || filter.apply(entity)) { listToFill.add(entity); }
                Entity[] parts = entity.getParts();
                if (parts != null) {
                    for (Entity part : parts) {
                        if (part != entityIn && part.getEntityBoundingBox().intersects(aabb)
                                && (filter == null || filter.apply(part))) { listToFill.add(part); }
                    }
                }
            }
        }
    }

    @Inject(method = "getEntitiesOfTypeWithinAABB", at = @At("HEAD"), cancellable = true) private <T extends Entity> void getEntitiesOfTypeWithinAAAB_Rubic(Class<? extends T> entityClass,
                                                                                                                                                            AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> filter, CallbackInfo cbi) {
        if (!rdpl$isColumn) { return; }
        cbi.cancel();
        int minY = MathHelper.floor((aabb.minY - World.MAX_ENTITY_RADIUS) / Cube.SIZE_D);
        int maxY = MathHelper.floor((aabb.maxY + World.MAX_ENTITY_RADIUS) / Cube.SIZE_D);
        minY = rdpl$clampCubeY(minY);
        maxY = rdpl$clampCubeY(maxY);
        for (Cube cube : rdpl$cubeMap.cubes(minY, maxY)) {
            for (T t : cube.getEntityContainer().getEntitySet().getByClass(entityClass)) {
                if (t.getEntityBoundingBox().intersects(aabb) && (filter == null || filter.apply(t))) { listToFill.add(t); }
            }
        }
    }

    @Inject(method = "getPrecipitationHeight", at = @At(value = "HEAD"), cancellable = true) private void getPrecipitationHeight_Rubic_Replace(BlockPos pos, CallbackInfoReturnable<BlockPos> cbi) {
        if (!rdpl$isColumn) { return; }
        int localX = blockToLocal(pos.getX());
        int localZ = blockToLocal(pos.getZ());
        int held = precipitationHeightMap[localX | localZ << 4];
        if (held == -999) {
            held = rdpl$findPrecipitationHeight(localX, pos.getY(), localZ);
            precipitationHeightMap[localX | localZ << 4] = held;
        }
        cbi.setReturnValue(new BlockPos(pos.getX(), held, pos.getZ()));
    }

    @Unique private int rdpl$findPrecipitationHeight(int localX, int blockY, int localZ) {
        int lightTop = ((IColumn) this).getHeightValue(localX, blockY, localZ);
        int highest = Integer.MIN_VALUE;
        for (Cube cube : rdpl$cubeMap) {
            if (cube.getY() > highest) { highest = cube.getY(); }
        }
        if (highest == Integer.MIN_VALUE) { return lightTop; }
        for (Cube cube : rdpl$cubeMap.cubes(highest, blockToCube(lightTop))) {
            ExtendedBlockStorage storage = cube.getStorage();
            if (storage == null || storage.isEmpty()) { continue; }
            for (int y = 15; y >= 0; y--) {
                int worldY = Coords.cubeToMinBlock(cube.getY()) + y;
                if (worldY < lightTop) { return lightTop; }
                Material material = storage.get(localX, y, localZ).getMaterial();
                if (material.blocksMovement() || material.isLiquid()) { return worldY + 1; }
            }
        }
        return lightTop;
    }

    @Inject(method = "onTick", at = @At(value = "RETURN")) private void onTick_Rubic_TickCubes(boolean skipRecheckGaps, CallbackInfo cbi) {
        if (!rdpl$isColumn) { return; }
        this.ticked = true;
        this.isLightPopulated = true;
    }

    /**
     * @author tgstyle
     * @reason Scan emptiness through the cube-backed storage lookup across the full rubic height range.
     */
    @Overwrite public boolean isEmptyBetween(int startY, int endY) {
        if (startY < rdpl$getRubicWorld().rdpl$getMinHeight()) { startY = rdpl$getRubicWorld().rdpl$getMinHeight(); }
        if (endY >= rdpl$getRubicWorld().rdpl$getMaxHeight()) { endY = rdpl$getRubicWorld().rdpl$getMaxHeight() - 1; }
        for (int i = startY; i <= endY; i += Cube.SIZE) {
            ExtendedBlockStorage extendedblockstorage = getEBS_Rubic(blockToCube(i));
            if (extendedblockstorage != NULL_BLOCK_STORAGE && extendedblockstorage != null && !extendedblockstorage.isEmpty()) { return false; }
        }
        return true;
    }

    @Inject(method = "setStorageArrays", at = @At(value = "HEAD")) private void setStorageArrays_Rubic_NotSupported(ExtendedBlockStorage[] newStorageArrays, CallbackInfo cbi) {
        if (rdpl$isColumn) { throw new UnsupportedOperationException("setting storage arrays it not supported with rubic"); }
    }

    @Redirect(method = "removeInvalidTileEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;loaded:Z", opcode = Opcodes.GETFIELD)) private boolean removeInvalidTileEntity_isChunkLoadedCubeRedirect(Chunk chunk, BlockPos pos) {
        if (!rdpl$isColumn) { return loaded; }
        return rdpl$cubeLoadedAt(pos.getY());
    }

    @Inject(method = "enqueueRelightChecks", at = @At(value = "HEAD"), cancellable = true) private void enqueueRelightChecks_Rubic(CallbackInfo cbi) {
        if (!rdpl$isColumn) { return; }
        cbi.cancel();
    }

    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;II)V", constant = @Constant(intValue = 16),
            slice = @Slice(to = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
                    opcode = Opcodes.PUTFIELD
            )),
            allow = 1, require = 1)
    private int modifySectionArrayLength(int sixteen, World worldIn, int x, int z) {
        if (worldIn == null) { return sixteen; }
        if (!((IRubicWorld) worldIn).rdpl$isRubicWorld()) {
            IMinMaxHeight y = (IMinMaxHeight) worldIn;
            return Coords.blockToCube(y.rdpl$getMaxHeight()) - Coords.blockToCube(y.rdpl$getMinHeight());
        }
        return sixteen;
    }

    public int chunk_internal$getTopYWithStaging(int localX, int localZ) {
        if (!rdpl$isColumn) { return heightMap[localZ << 4 | localX] - 1; }
        return Math.max(rdpl$opacityIndex.getTopBlockY(localX, localZ), rdpl$stagingHeightMap.getTopBlockY(localX, localZ));
    }

    /**
     * @author tgstyle
     * @reason Answer from the opacity index with staged cubes included instead of the vanilla height map.
     */
    @Overwrite public int getHeightValue(int localX, int localZ) { return rdpl$topBlockY(localX, localZ) + 1; }

    @Unique private int rdpl$topBlockY(int localX, int localZ) {
        int top = chunk_internal$getTopYWithStaging(localX, localZ);
        return top == Coords.NO_HEIGHT ? rdpl$getRubicWorld().rdpl$getMinHeight() - 1 : top;
    }

    @Intrinsic public int chunk$getHeightValue(int localX, int localZ) { return rdpl$topBlockY(localX, localZ) + 1; }

    public int chunk$getHeightValue(int localX, int blockY, int localZ) { return rdpl$topBlockY(localX, localZ) + 1; }

    public boolean chunk_internal$isRubicColumn() { return rdpl$isColumn; }

    public boolean chunk_internal$pregenDone() { return rdpl$pregenDone; }

    public void chunk_internal$markPregenDone() { rdpl$pregenDone = true; }

    public ChunkPrimer chunk_internal$getCompatGenerationPrimer() { return rdpl$compatGenerationPrimer; }

    @Nullable public ExtendedBlockStorage chunk_internal$getStorageForCube(int cubeY) { return getEBS_Rubic(cubeY); }

    public void chunk_internal$setStorageForCube(int cubeY, ExtendedBlockStorage storage) { setEBS_Rubic(cubeY, storage); }

    public void chunk_internal$syncCompatGenerationWrites() {
        if (!rdpl$compatArraysFilled) { return; }
        for (int section = 0; section < 16; section++) {
            ExtendedBlockStorage storage = storageArrays[section];
            if (storage == null || storage == NULL_BLOCK_STORAGE) { continue; }
            for (int localY = 0; localY < 16; localY++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    for (int localX = 0; localX < 16; localX++) {
                        int y = (section << 4) + localY;
                        IBlockState held = storage.get(localX, localY, localZ);
                        if (rdpl$compatGenerationPrimer.getBlockState(localX, y, localZ) != held) { rdpl$compatGenerationPrimer.setBlockState(localX, y, localZ, held); }
                    }
                }
            }
        }
        rdpl$compatArraysFilled = false;
    }

    public void chunk_internal$removeFromStagingHeightmap(ICube cube) { rdpl$stagingHeightMap.removeStagedCube(cube); }

    public void chunk_internal$addToStagingHeightmap(ICube cube) { rdpl$stagingHeightMap.addStagedCube(cube); }

    @Unique private void rdpl$invalidateCachedCube() { rdpl$cachedCube = null; }

    public ICube chunk$getLoadedCube(int cubeY) {
        if (rdpl$cachedCube != null && rdpl$cachedCube.getY() == cubeY) { return rdpl$cachedCube; }
        return rdpl$getRubicWorld().rdpl$getCubeCache().getLoadedCube(x, cubeY, z);
    }

    @Nonnull public ICube chunk$getCube(int cubeY) {
        if (rdpl$cachedCube != null && rdpl$cachedCube.getY() == cubeY) { return rdpl$cachedCube; }
        return rdpl$getRubicWorld().rdpl$getCubeCache().getCube(x, cubeY, z);
    }

    public void chunk$addCube(@Nonnull ICube cube) {
        this.rdpl$cubeMap.put((Cube) cube);
        Arrays.fill(precipitationHeightMap, -999);
    }

    public ICube chunk$removeCube(int cubeY) {
        if (rdpl$cachedCube != null && rdpl$cachedCube.getY() == cubeY) { rdpl$invalidateCachedCube(); }
        Arrays.fill(precipitationHeightMap, -999);
        return this.rdpl$cubeMap.remove(cubeY);
    }

    public boolean chunk$hasLoadedCubes() { return !rdpl$cubeMap.isEmpty(); }

    public boolean chunk$shouldTick() {
        for (Cube cube : rdpl$cubeMap) {
            if (cube.getTickets().shouldTick()) { return true; }
        }
        return false;
    }

    @Nonnull public IHeightMap chunk$getOpacityIndex() { return this.rdpl$opacityIndex; }

    @Nonnull public Collection<? extends ICube> chunk$getLoadedCubes() { return this.rdpl$cubeMap.all(); }

    @Nonnull public Iterable<? extends ICube> chunk$getLoadedCubes(int startY, int endY) { return this.rdpl$cubeMap.cubes(startY, endY); }

    public void chunk$preCacheCube(@Nonnull ICube cube) { this.rdpl$cachedCube = (Cube) cube; }

    @Intrinsic public int chunk$getX() { return x; }

    @Intrinsic public int chunk$getZ() { return z; }
}
