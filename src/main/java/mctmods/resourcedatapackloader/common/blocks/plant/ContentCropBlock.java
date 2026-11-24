package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.blocks.IContentBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.util.plant.CropStacks;
import mctmods.resourcedatapackloader.common.util.plant.TallPlants;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SuppressWarnings("deprecation") public class ContentCropBlock extends BlockCrops implements IContentBlock {
    public static final int MAX_VARIANTS = 1;
    private final BlockDef def;
    private final CropStacks stacks = new CropStacks();

    public ContentCropBlock(BlockDef def) {
        this.def = def;
        ContentSetup.plantNamed(this, def);
    }

    public CropStacks stacks() { return stacks; }

    @Override public BlockDef getDef() { return def; }

    @Override @Nullable public ItemBlock createItem() { return null; }

    @Override public int getMaxAge() { return def.cropMaxAge; }

    @Override public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
        super.updateTick(world, pos, state, rand);
        if (def.tall == null || world.getBlockState(pos).getBlock() != this) { return; }
        int age = getAge(world.getBlockState(pos));
        int limited = TallPlants.limit(world, pos, age);
        if (limited != age) { world.setBlockState(pos, withAge(limited), BlockFlags.UPDATE_CLIENTS); }
        TallPlants.sync(world, pos);
    }

    @Override public boolean canGrow(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) {
        if (!super.canGrow(world, pos, state, isClient)) { return false; }
        int next = getAge(state) + 1;
        return def.tall == null || TallPlants.limit(world, pos, next) == next;
    }

    @Override public void grow(@Nonnull World world, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (def.tall == null) {
            super.grow(world, rand, pos, state);
            return;
        }
        int age = Math.min(getAge(state) + getBonemealAgeIncrease(world), getMaxAge());
        world.setBlockState(pos, withAge(TallPlants.limit(world, pos, age)), BlockFlags.UPDATE_CLIENTS);
        TallPlants.sync(world, pos);
    }

    @Override public void onBlockAdded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote) { TallPlants.sync(world, pos); }
    }

    @Override @Nonnull public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        TallDef tall = def.tall;
        return tall != null && getAge(state) >= tall.riseAt(1) ? FULL_BLOCK_AABB : super.getBoundingBox(state, world, pos);
    }

    @Override @Nonnull protected Item getSeed() { return stacks.seedItem(); }

    @Override @Nonnull protected Item getCrop() { return stacks.cropItem(); }

    @Override @Nonnull public ItemStack getItem(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) { return stacks.seed(); }

    @Override public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        int age = getAge(state);
        if (age < getMaxAge()) {
            drops.add(stacks.seed());
            return;
        }
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;
        drops.add(stacks.crop());
        for (int i = 0; i < 3 + fortune; i++) {
            if (rand.nextInt(2 * getMaxAge()) <= age) { drops.add(stacks.seed()); }
        }
    }
}
