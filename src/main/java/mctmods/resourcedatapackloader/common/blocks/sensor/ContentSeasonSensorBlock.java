package mctmods.resourcedatapackloader.common.blocks.sensor;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.environment.ContentSeasons;
import mctmods.resourcedatapackloader.common.environment.SeasonState;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SeasonSensorDef;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SuppressWarnings("deprecation") public class ContentSeasonSensorBlock extends Block implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private static final int POLL = 100;
    private final BlockDef def;
    private final SeasonSensorDef sensor;

    public ContentSeasonSensorBlock(BlockDef def) {
        super(def.material, def.mapColor);
        this.def = def;
        this.sensor = def.sensor == null ? SeasonSensorDef.PLAIN : def.sensor;
        ContentSetup.applyNamed(this, def);
        setDefaultState(blockState.getBaseState().withProperty(POWER, 0));
    }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return new ItemBlock(this); }

    @Override @Nonnull protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, POWER); }

    @Override @Nonnull public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(POWER, meta & 15); }

    @Override public int getMetaFromState(IBlockState state) { return state.getValue(POWER); }

    @Override public boolean canProvidePower(@Nonnull IBlockState state) { return true; }

    @Override public int getWeakPower(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) { return state.getValue(POWER); }

    @Override public boolean hasComparatorInputOverride(@Nonnull IBlockState state) { return true; }

    @Override public int getComparatorInputOverride(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) { return state.getValue(POWER); }

    @Override public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { refresh(world, pos, state); }

    @Override public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) { refresh(world, pos, state); }

    private void refresh(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) { return; }
        world.scheduleUpdate(pos, this, POLL);
        int power = power(world);
        if (power == state.getValue(POWER)) { return; }
        world.setBlockState(pos, state.withProperty(POWER, power), BlockFlags.UPDATE_ALL);
        world.notifyNeighborsOfStateChange(pos, this, false);
    }

    private int power(World world) {
        SeasonState season = ContentSeasons.state(sensor.dimension, world.provider.getDimension());
        return season == null ? 0 : sensor.power(season.name(), season.index(), season.count());
    }
}
