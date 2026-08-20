package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.CubePos;
import mctmods.resourcedatapackloader.util.interfaces.IXYZAddressable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import java.util.Map;
import javax.annotation.Nullable;

public interface ICube extends IXYZAddressable, ICapabilityProvider {
    int SIZE = 16;
    double SIZE_D = 16.0D;

    IBlockState getBlockState(BlockPos pos);

    @Nullable IBlockState setBlockState(BlockPos pos, IBlockState newstate);

    IBlockState getBlockState(int blockX, int localOrBlockY, int blockZ);

    int getLightFor(EnumSkyBlock lightType, BlockPos pos);

    void setLightFor(EnumSkyBlock lightType, BlockPos pos, int light);

    @Nullable TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType createType);

    void addTileEntity(TileEntity tileEntity);

    boolean isEmpty();

    BlockPos localAddressToBlockPos(int localAddress);

    <T extends World & IRubicWorld> T getWorld();

    @SuppressWarnings({"deprecation", "RedundantSuppression"}) <T extends Chunk & IColumn> T getColumn();

    int getX();

    int getY();

    int getZ();

    CubePos getCoords();

    @Nullable ExtendedBlockStorage getStorage();

    Map<BlockPos, TileEntity> getTileEntityMap();

    ClassInheritanceMultiMap<Entity> getEntitySet();

    boolean needsSaving();

    boolean isPopulated();

    boolean isFullyPopulated();

    boolean isSurfaceTracked();

    boolean isInitialLightingDone();

    boolean isCubeLoaded();

    Biome getBiome(BlockPos pos);

    void setBiome(int localBiomeX, int localBiomeY, int localBiomeZ, Biome biome);

    @Deprecated default void setBiome(int localBiomeX, int localBiomeZ, Biome biome) {
        for (int biomeY = 0; biomeY < 4; biomeY++) { setBiome(localBiomeX >> 1, biomeY, localBiomeZ >> 1, biome); }
    }

    @Nullable CapabilityDispatcher getCapabilities();
}
