package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowthDef;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation") public class ContentVineBlock extends BlockVine implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;
    private final GrowthDef growth;

    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    public ContentVineBlock(BlockDef def, GrowthDef growth) {
        this.def = def;
        this.growth = growth;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + def.at(0).name);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setTickRandomly(growth.maxHeight > 1 || growth.spread > 0);
        ContentSetup.apply(this, def.creativeTab);
        ContentSetup.properties(this, def);
    }

    @Override public BlockDef getDef() { return def; }

    public static boolean attachable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlockFaceShape(world, pos, face) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(state.getBlock());
    }

    @Override @Nullable public ItemBlock createItem() { return new ItemBlock(this); }

    @Override public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        if (world.isRemote) { return; }
        if (random.nextInt(Math.max(1, growth.stages)) != 0) { return; }
        if (growth.spread > 0 && spread(world, pos, random)) { return; }
        if (growth.maxHeight <= 1) { return; }
        BlockPos below = pos.down();
        if (!world.isAirBlock(below)) { return; }
        int hanging = 1;
        while (world.getBlockState(pos.up(hanging)).getBlock() == this) { hanging++; }
        if (hanging >= growth.maxHeight) { return; }
        world.setBlockState(below, state, BlockFlags.UPDATE_CLIENTS);
    }

    private boolean spread(World world, BlockPos pos, Random random) {
        if (crowded(world, pos)) { return false; }
        EnumFacing side = EnumFacing.Plane.HORIZONTAL.random(random);
        BlockPos beside = pos.offset(side);
        if (!world.isAirBlock(beside)) { return false; }
        IBlockState carried = getDefaultState();
        boolean anchored = false;
        for (EnumFacing wall : EnumFacing.Plane.HORIZONTAL) {
            if (!attachable(world, beside.offset(wall), wall.getOpposite())) { continue; }
            carried = carried.withProperty(getPropertyFor(wall), Boolean.TRUE);
            anchored = true;
        }
        if (!anchored) { return false; }
        world.setBlockState(beside, carried, BlockFlags.UPDATE_CLIENTS);
        return true;
    }

    private boolean crowded(World world, BlockPos pos) {
        int found = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (world.getBlockState(pos.add(dx, dy, dz)).getBlock() != this) { continue; }
                    if (++found >= growth.spread) { return true; }
                }
            }
        }
        return false;
    }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) { ContentSetup.growthDrops(this, def, growth, drops); }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return new ItemStack(this); }
}
