package mctmods.resourcedatapackloader.content.rubic.world.cube;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.EntityContainer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.content.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes3D;
import mctmods.resourcedatapackloader.util.AddressTools;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.compat.CompatHandler;
import mctmods.resourcedatapackloader.util.ticket.TicketList;
import static mctmods.resourcedatapackloader.util.Coords.blockToLocal;
import static mctmods.resourcedatapackloader.util.Coords.cubeToMaxBlock;
import static mctmods.resourcedatapackloader.util.Coords.cubeToMinBlock;
import static mctmods.resourcedatapackloader.util.Coords.localToBlock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Cube implements ICube {
    @Nullable protected static final ExtendedBlockStorage NULL_STORAGE = null;
    @Nullable private byte[] blockBiomeArray = null;
    @Nonnull private final TicketList tickets;
    private boolean isModified = false;
    private boolean isPopulated = false;
    private boolean isFullyPopulated = false;
    private boolean isInitialLightingDone = false;
    @Nonnull private final World world;
    @Nonnull private final Chunk column;
    @Nonnull private final CubePos coords;
    @Nullable private ExtendedBlockStorage storage;
    @Nonnull private final EntityContainer entities;
    @Nonnull private final Map<BlockPos, TileEntity> tileEntityMap;
    @Nonnull private final ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue;
    private final ICubeLightTrackingInfo cubeLightData;
    private boolean isCubeLoaded;
    private boolean isSurfaceTracked = false;
    private long lastTicked = Long.MIN_VALUE;
    private final CapabilityDispatcher capabilities;

    public Cube(Chunk column, int cubeY) {
        this.world = column.getWorld();
        this.column = column;
        this.coords = new CubePos(column.x, cubeY, column.z);
        this.tickets = new TicketList(this);
        this.entities = new EntityContainer();
        this.tileEntityMap = new HashMap<>();
        this.tileEntityPosQueue = new ConcurrentLinkedQueue<>();
        this.cubeLightData = ((IRubicWorldInternal) world).rdpl$getLightingManager().createLightData();
        this.storage = NULL_STORAGE;
        AttachCapabilitiesEvent<ICube> event = new AttachCapabilitiesEvent<>(ICube.class, this);
        MinecraftForge.EVENT_BUS.post(event);
        this.capabilities = !event.getCapabilities().isEmpty() ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
    }

    public Cube(Chunk column, int cubeY, CubePrimer primer) {
        this(column, cubeY);
        IBlockState air = Blocks.AIR.getDefaultState();
        for (int y = Cube.SIZE - 1; y >= 0; y--) {
            for (int z = 0; z < Cube.SIZE; z++) {
                for (int x = 0; x < Cube.SIZE; x++) {
                    IBlockState newstate = primer.getBlockState(x, y, z);
                    if (newstate != air) {
                        if (storage == NULL_STORAGE) { newStorage(); }
                        storage.set(x, y, z, newstate);
                    }
                }
            }
        }
        if (primer.hasBiomes()) {
            for (int biomeX = 0; biomeX < 4; biomeX++) {
                for (int biomeY = 0; biomeY < 4; biomeY++) {
                    for (int biomeZ = 0; biomeZ < 4; biomeZ++) {
                        Biome biome = primer.getBiome(biomeX, biomeY, biomeZ);
                        if (biome != null) { setBiome(biomeX, biomeY, biomeZ, biome); }
                    }
                }
            }
        }
        ContentBiomes3D.apply(this, world);
        isModified = true;
    }

    protected Cube(@Nonnull TicketList tickets, @Nonnull World world, @Nonnull Chunk column, @Nonnull CubePos coords,
                   @Nonnull EntityContainer entities, @Nonnull Map<BlockPos, TileEntity> tileEntityMap,
                   @Nonnull ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue) {
        this.tickets = tickets;
        this.world = world;
        this.column = column;
        this.coords = coords;
        this.storage = null;
        this.entities = entities;
        this.tileEntityMap = tileEntityMap;
        this.tileEntityPosQueue = tileEntityPosQueue;
        this.cubeLightData = null;
        AttachCapabilitiesEvent<ICube> event = new AttachCapabilitiesEvent<>(ICube.class, this);
        MinecraftForge.EVENT_BUS.post(event);
        this.capabilities = !event.getCapabilities().isEmpty() ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
    }

    @Override public IBlockState getBlockState(BlockPos pos) { return this.getBlockState(pos.getX(), pos.getY(), pos.getZ()); }

    @Override @Nullable public IBlockState setBlockState(BlockPos pos, IBlockState newstate) { return column.setBlockState(pos, newstate); }

    @Override public IBlockState getBlockState(int blockX, int localOrBlockY, int blockZ) {
        if (storage == NULL_STORAGE) { return Blocks.AIR.getDefaultState(); }
        return storage.get(blockToLocal(blockX), blockToLocal(localOrBlockY), blockToLocal(blockZ));
    }

    @Override public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        ((IRubicWorldInternal) world).rdpl$getLightingManager().onGetLight();
        return getCachedLightFor(type, pos);
    }

    public int getCachedLightFor(EnumSkyBlock type, BlockPos pos) {
        int x = blockToLocal(pos.getX());
        int y = blockToLocal(pos.getY());
        int z = blockToLocal(pos.getZ());
        ExtendedBlockStorage storage = this.storage;
        if (storage == null) { return ((IColumnInternal) column).getTopYWithStaging(x, z) > pos.getY() ? type.defaultLightValue : 0; }
        else if (type == EnumSkyBlock.SKY) { return !this.world.provider.hasSkyLight() ? 0 : storage.getSkyLight(x, y, z); }
        else { return type == EnumSkyBlock.BLOCK ? storage.getBlockLight(x, y, z) : type.defaultLightValue; }
    }

    @Override public void setLightFor(EnumSkyBlock lightType, BlockPos pos, int light) { column.setLightFor(lightType, pos, light); }

    @Nullable private TileEntity createTileEntity(BlockPos pos) {
        IBlockState blockState = getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.hasTileEntity(blockState)) { return block.createTileEntity(this.world, blockState); }
        return null;
    }

    @Override @Nullable public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType createType) { return column.getTileEntity(pos, createType); }

    @Override public void addTileEntity(TileEntity tileEntityIn) {
        this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
        if (this.isCubeLoaded) { this.world.addTileEntity(tileEntityIn); }
    }

    private void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        if (tileEntityIn.getWorld() != this.world) { tileEntityIn.setWorld(this.world); }
        tileEntityIn.setPos(pos);
        if (this.getBlockState(pos).getBlock().hasTileEntity(this.getBlockState(pos))) {
            if (this.tileEntityMap.containsKey(pos)) { this.tileEntityMap.get(pos).invalidate(); }
            tileEntityIn.validate();
            this.tileEntityMap.put(pos, tileEntityIn);
        }
    }

    public void tickCubeCommon() {
        while (!this.tileEntityPosQueue.isEmpty()) {
            BlockPos blockpos = this.tileEntityPosQueue.poll();
            IBlockState state = this.getBlockState(blockpos);
            Block block = state.getBlock();
            if (this.getTileEntity(blockpos, Chunk.EnumCreateEntityType.CHECK) == null &&
                    block.hasTileEntity(state)) {
                TileEntity tileentity = this.createTileEntity(blockpos);
                this.world.setTileEntity(blockpos, tileentity);
                this.world.markBlockRangeForRenderUpdate(blockpos, blockpos);
            }
        }
    }

    public void tickCubeServer() {
        if (!isFullyPopulated) { return; }
        tickCubeCommon();
    }

    @Override public Biome getBiome(BlockPos pos) {
        if (this.blockBiomeArray == null)
            return this.getColumn().getBiome(pos, world.getBiomeProvider());
        int biomeX = Coords.blockToLocalBiome3d(pos.getX());
        int biomeY = Coords.blockToLocalBiome3d(pos.getY());
        int biomeZ = Coords.blockToLocalBiome3d(pos.getZ());
        int biomeId = this.blockBiomeArray[AddressTools.getBiomeAddress3d(biomeX, biomeY, biomeZ)] & 255;
        Biome biome = Biome.getBiome(biomeId);
        return biome == null ? Biomes.PLAINS : biome;
    }

    @Override public void setBiome(int localBiomeX, int localBiomeY, int localBiomeZ, Biome biome) {
        if (this.blockBiomeArray == null)
            this.blockBiomeArray = new byte[4 * 4 * 4];
        this.blockBiomeArray[AddressTools.getBiomeAddress3d(localBiomeX, localBiomeY, localBiomeZ)] = (byte) Biome.REGISTRY.getIDForObject(biome);
    }

    @Nullable public byte[] getBiomeArray() { return this.blockBiomeArray; }

    public void setBiomeArray(byte[] biomeArray) {
        if (this.blockBiomeArray == null) {
            this.blockBiomeArray = biomeArray.clone();
            return;
        }
        if (this.blockBiomeArray.length != biomeArray.length) {
            Rubic.LOGGER.warn("Could not set level cube biomes, array length is {} instead of {}", biomeArray.length,
                    this.blockBiomeArray.length);
        }
        else { System.arraycopy(biomeArray, 0, this.blockBiomeArray, 0, this.blockBiomeArray.length); }
    }

    @Override public boolean isEmpty() { return storage == null || this.storage.isEmpty(); }

    @Override public BlockPos localAddressToBlockPos(int localAddress) {
        int x = localToBlock(this.coords.getX(), AddressTools.getLocalX(localAddress));
        int y = localToBlock(this.coords.getY(), AddressTools.getLocalY(localAddress));
        int z = localToBlock(this.coords.getZ(), AddressTools.getLocalZ(localAddress));
        return new BlockPos(x, y, z);
    }

    @SuppressWarnings("unchecked") public <T extends World & IRubicWorld> T getWorld() { return (T) this.world; }

    @SuppressWarnings({"unchecked", "deprecation", "RedundantSuppression"}) @Override public <T extends Chunk & IColumn> T getColumn() {
        return (T) this.column;
    }

    @Override public int getX() { return this.coords.getX(); }

    @Override public int getY() { return this.coords.getY(); }

    @Override public int getZ() { return this.coords.getZ(); }

    @Override @Nonnull public CubePos getCoords() { return this.coords; }

    @Override @Nullable public ExtendedBlockStorage getStorage() { return this.storage; }

    public void setStorage(@Nullable ExtendedBlockStorage ebs) {
        this.isModified = true;
        this.storage = ebs;
        if (ebs != null) { ((IRubicWorldInternal) world).rdpl$getLightingManager().onCreateCubeStorage(this, ebs); }
    }

    public void setStorageFromSave(@Nullable ExtendedBlockStorage ebs) { this.storage = ebs; }

    private void newStorage() { storage = new ExtendedBlockStorage(cubeToMinBlock(getY()), world.provider.hasSkyLight()); }

    @Override @Nonnull public Map<BlockPos, TileEntity> getTileEntityMap() { return this.tileEntityMap; }

    @Override public ClassInheritanceMultiMap<Entity> getEntitySet() { return this.entities.getEntitySet(); }

    public EntityContainer getEntityContainer() { return this.entities; }

    public boolean checkAndUpdateTick(long totalTime) {
        boolean ret = totalTime != this.lastTicked;
        this.lastTicked = totalTime;
        return ret;
    }

    public void onLoad() {
        if (isCubeLoaded) {
            Rubic.LOGGER.error("Attempting to load already loaded cube at {}", this.getCoords());
            return;
        }
        this.world.addTileEntities(this.tileEntityMap.values());
        this.world.loadEntities(this.entities.getEntities());
        this.isCubeLoaded = true;
        boolean raised = false;
        if (!isSurfaceTracked) {
            IColumnInternal held = getColumn();
            int[] before = new int[256];
            for (int i = 0; i < 256; i++) { before[i] = held.getTopYWithStaging(i & 15, i >> 4); }
            held.addToStagingHeightmap(this);
            for (int i = 0; i < 256 && !raised; i++) { raised = held.getTopYWithStaging(i & 15, i >> 4) > before[i]; }
        }
        ((IRubicWorldInternal) world).rdpl$getLightingManager().onCubeLoad(this, raised);
        CompatHandler.onCubeLoad(new ChunkEvent.Load(getColumn()));
    }

    @SuppressWarnings("deprecation") public void trackSurface() {
        if (storage != NULL_STORAGE && !storage.isEmpty()) {
            IHeightMap opindex = ((IColumn) column).getOpacityIndex();
            int miny = getCoords().getMinBlockY();
            column.setModified(true);
            for (int x = 0; x < Cube.SIZE; x++) {
                for (int z = 0; z < Cube.SIZE; z++) {
                    for (int y = Cube.SIZE - 1; y >= 0; y--) {
                        IBlockState newstate = storage.get(x, y, z);
                        opindex.onOpacityChange(x, miny + y, z, newstate.getLightOpacity());
                    }
                }
            }
        }
        isSurfaceTracked = true;
        ((IColumnInternal) getColumn()).removeFromStagingHeightmap(this);
        ((IRubicWorldInternal) world).rdpl$getLightingManager().onTrackCubeSurface(this);
    }

    public void onUnload() {
        ((IRubicWorldInternal) this.world).rdpl$getLightingManager().onCubeUnload();
        if (!isCubeLoaded) {
            Rubic.LOGGER.error("Attempting to unload already unloaded cube at {}", this.getCoords());
            return;
        }
        this.isCubeLoaded = false;
        if (!isSurfaceTracked) { trackSurface(); }
        this.world.unloadEntities(this.entities.getEntities());
        for (Entity entity : this.entities.getEntities()) { entity.addedToChunk = false; }
        for (TileEntity blockEntity : this.tileEntityMap.values()) { this.world.markTileEntityForRemoval(blockEntity); }
        ((IColumnInternal) getColumn()).removeFromStagingHeightmap(this);
    }

    @Override public boolean needsSaving() { return this.entities.needsSaving(true, this.world.getTotalWorldTime(), this.isModified) || cubeLightData.needsSaving(this); }

    public void markSaved() {
        this.entities.markSaved(this.world.getTotalWorldTime());
        this.isModified = false;
        this.cubeLightData.markSaved(this);
    }

    public void markDirty() { this.isModified = true; }

    @Nonnull public TicketList getTickets() { return tickets; }

    public void markForRenderUpdate() {
        this.world.markBlockRangeForRenderUpdate(
                cubeToMinBlock(this.coords.getX()), cubeToMinBlock(this.coords.getY()), cubeToMinBlock(this.coords.getZ()),
                cubeToMaxBlock(this.coords.getX()), cubeToMaxBlock(this.coords.getY()), cubeToMaxBlock(this.coords.getZ())
        );
    }

    public ICubeLightTrackingInfo getCubeLightData() { return this.cubeLightData; }

    public void setClientCube() {
        this.isPopulated = true;
        this.isFullyPopulated = true;
        this.isInitialLightingDone = true;
        this.isSurfaceTracked = true;
    }

    @Override public boolean isPopulated() { return isPopulated; }

    public void setPopulated(boolean populated) {
        this.isPopulated = populated;
        this.isModified = true;
    }

    @Override public boolean isFullyPopulated() { return this.isFullyPopulated; }

    public void setFullyPopulated(boolean populated) {
        this.isFullyPopulated = populated;
        this.isModified = true;
    }

    public void setSurfaceTracked(boolean value) { this.isSurfaceTracked = value; }

    @Override public boolean isSurfaceTracked() { return this.isSurfaceTracked; }

    @Override public boolean isInitialLightingDone() { return isInitialLightingDone; }

    public void setInitialLightingDone(boolean initialLightingDone) {
        this.isInitialLightingDone = initialLightingDone;
        this.isModified = true;
    }

    public void setCubeLoaded() { this.isCubeLoaded = true; }

    @Override public boolean isCubeLoaded() { return this.isCubeLoaded; }

    @Override @Nullable public CapabilityDispatcher getCapabilities() { return this.capabilities; }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return this.capabilities != null && this.capabilities.hasCapability(capability, facing);
    }

    @Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return this.capabilities == null ? null : this.capabilities.getCapability(capability, facing);
    }

    public interface ICubeLightTrackingInfo {
        boolean needsSaving(ICube cube);
        void markSaved(ICube cube);
    }
}
