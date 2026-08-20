package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.rubic.world.ServerHeightMap;
import mctmods.resourcedatapackloader.content.rubic.world.cube.BlankCube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IHeightMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import org.jetbrains.annotations.NotNull;

public class EmptyColumn extends Chunk implements IColumnInternal {
	private final ICube emptyCube;
	private final IHeightMap opacityIndex;

	public EmptyColumn(World worldIn, int x, int z) {
		super(worldIn, x, z);
		this.emptyCube = new BlankCube(this);
		this.opacityIndex = new ServerHeightMap(getHeightMap());
	}

	@Override public int getHeightValue(int x, int z) { return 0; }

	@Override public int getHeightValue(int localX, int blockY, int localZ) { return 0; }

	@Override public boolean shouldTick() { return false; }

	@Override @NotNull public IHeightMap getOpacityIndex() { return opacityIndex; }

	@Override @NotNull public Collection<? extends ICube> getLoadedCubes() { return Collections.emptyList(); }

	@Override @NotNull public Iterable<? extends ICube> getLoadedCubes(int startY, int endY) { return Collections.emptyList(); }

	@Nullable @Override public ICube getLoadedCube(int cubeY) { return null; }

	@Override @NotNull public ICube getCube(int cubeY) { return emptyCube; }

	@Override public void addCube(@NotNull ICube cube) { throw new RuntimeException("This should never be called!"); }

	@Nullable @Override public ICube removeCube(int cubeY) { return null; }

	@Override public boolean hasLoadedCubes() { return false; }

	@Override public void preCacheCube(@NotNull ICube cube) {
	}

	@Override public int getX() { return 0; }

	@Override public int getZ() { return 0; }

	@Override public ChunkPrimer getCompatGenerationPrimer() { return null; }

	@Override public void removeFromStagingHeightmap(ICube cube) {
	}

	@Override public void addToStagingHeightmap(ICube cube) {
	}

	@Override public int getTopYWithStaging(int localX, int localZ) { return 0; }

	@Override public boolean isRubicColumn() { return false; }

	@Override public void generateHeightMap() {
	}

	@Override public void generateSkylightMap() {
	}

	@Override @Nonnull public IBlockState getBlockState(@NotNull BlockPos pos) { return Blocks.AIR.getDefaultState(); }

	@Override public int getBlockLightOpacity(@NotNull BlockPos pos) { return 255; }

	@Override public int getLightFor(EnumSkyBlock type, @NotNull BlockPos pos) { return type.defaultLightValue; }

	@Override public void setLightFor(@NotNull EnumSkyBlock type, @NotNull BlockPos pos, int value) {
	}

	@Override public int getLightSubtracted(@NotNull BlockPos pos, int amount) { return 0; }

	@Override public void addEntity(@NotNull Entity entityIn) {
	}

	@Override public void removeEntity(@NotNull Entity entityIn) {
	}

	@Override public void removeEntityAtIndex(@NotNull Entity entityIn, int index) {
	}

	@Override public boolean canSeeSky(@NotNull BlockPos pos) { return false; }

	@Nullable @Override public TileEntity getTileEntity(@NotNull BlockPos pos, @NotNull Chunk.EnumCreateEntityType creationMode) { return null; }

	@Override public void addTileEntity(@NotNull TileEntity tileEntityIn) {
	}

	@Override public void addTileEntity(@NotNull BlockPos pos, @NotNull TileEntity tileEntityIn) {
	}

	@Override public void removeTileEntity(@NotNull BlockPos pos) {
	}

	@Override public void onLoad() {
	}

	@Override public void onUnload() {
	}

	@Override public void markDirty() {
	}

	@Override public void getEntitiesWithinAABBForEntity(Entity entityIn, @NotNull AxisAlignedBB aabb,
	                                                     @NotNull List<Entity> listToFill, @NotNull Predicate<? super Entity> filter) {
	}

	@Override public <T extends Entity> void getEntitiesOfTypeWithinAABB(@NotNull Class<? extends T> entityClass, @NotNull AxisAlignedBB aabb, @NotNull List<T> listToFill, @NotNull Predicate<? super T> filter) {
	}

	@Override public boolean needsSaving(boolean p_76601_1_) { return false; }

	@Override @Nonnull public Random getRandomWithSeed(long seed) {
		return new Random(this.getWorld().getSeed() + ((long) this.x * this.x * 4987142) + (this.x * 5947611L) + (long) this.z * this.z * 4392871L + (this.z * 389711L) ^ seed);
	}

	@Override public boolean isEmpty() { return true; }

	@Override public boolean isEmptyBetween(int startY, int endY) { return true; }
}
