package mctmods.resourcedatapackloader.common.util.loot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class BlockDrops {
    private BlockDrops() {}

    public static List<ItemStack> roll(World world, BlockPos pos, IBlockState state, int fortune, @Nullable EntityPlayer player) {
        NonNullList<ItemStack> drops = NonNullList.create();
        state.getBlock().getDrops(drops, world, pos, state, fortune);
        float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, fortune, 1.0F, false, player);
        List<ItemStack> kept = new ArrayList<>();
        for (ItemStack drop : drops) {
            if (!drop.isEmpty() && world.rand.nextFloat() <= chance) { kept.add(drop); }
        }
        return kept;
    }
}
