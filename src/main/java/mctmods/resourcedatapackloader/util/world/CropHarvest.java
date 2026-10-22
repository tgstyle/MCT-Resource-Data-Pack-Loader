package mctmods.resourcedatapackloader.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import java.util.Collections;
import javax.annotation.Nullable;

public final class CropHarvest {
    private static final String AGE = "age";

    private CropHarvest() {}

    @Nullable private static PropertyInteger ageOf(IBlockState state) {
        for (IProperty<?> property : state.getPropertyKeys()) {
            if (property instanceof PropertyInteger && AGE.equals(property.getName())) { return (PropertyInteger) property; }
        }
        return null;
    }

    public static boolean ripe(IBlockState state, int age) {
        PropertyInteger property = ageOf(state);
        if (property == null) { return false; }
        return state.getValue(property) >= (age >= 0 ? age : Collections.max(property.getAllowedValues()));
    }

    @SuppressWarnings("deprecation") public static void harvest(World world, BlockPos pos, IBlockState state, @Nullable EntityPlayer player, int fortune, boolean replant) {
        NonNullList<ItemStack> drops = NonNullList.create();
        state.getBlock().getDrops(drops, world, pos, state, fortune);
        float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, fortune, 1.0F, false, player);
        PropertyInteger property = ageOf(state);
        ItemStack seed = replant && property != null ? state.getBlock().getItem(world, pos, state) : ItemStack.EMPTY;
        boolean seedTaken = seed.isEmpty();
        for (ItemStack drop : drops) {
            if (drop.isEmpty() || world.rand.nextFloat() > chance) { continue; }
            ItemStack kept = drop.copy();
            if (!seedTaken && ItemStack.areItemsEqual(kept, seed)) {
                kept.shrink(1);
                seedTaken = true;
            }
            if (!kept.isEmpty()) { Block.spawnAsEntity(world, pos, kept); }
        }
        world.playEvent(2001, pos, Block.getStateId(state));
        if (replant && property != null) { world.setBlockState(pos, state.withProperty(property, Collections.min(property.getAllowedValues())), 3); }
        else { world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3); }
    }
}
