package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;

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
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation") public class ContentBlockVine extends BlockVine implements IContentBlock {
    @Override @SideOnly(Side.CLIENT) @Nonnull public BlockRenderLayer getRenderLayer() { return def.renderLayer; }

    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;
    private final GrowthDef growth;

    public ContentBlockVine(BlockDef def, GrowthDef growth) {
        this.def = def;
        this.growth = growth;
        setRegistryName(def.registryName);
        setTranslationKey(def.registryName + "." + def.at(0).name);
        if (def.soundType != null) { setSoundType(def.soundType); }
        setHardness(def.at(0).hardness);
        setTickRandomly(growth.maxHeight > 1);
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
        IBlockState copy = state;
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            if (!state.getValue(getPropertyFor(facing))) { continue; }
            copy = copy.withProperty(getPropertyFor(facing), Boolean.TRUE);
        }
        world.setBlockState(below, copy, 2);
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
        world.setBlockState(beside, carried, 2);
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
