package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import mctmods.resourcedatapackloader.common.util.ItemSelection;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ZoneListDef {
    @Nullable private final ItemSelection allow;
    @Nullable private final ItemSelection deny;
    private final boolean denyAll;
    @Nullable private final ItemSelection on;
    public final boolean close;

    public ZoneListDef(@Nullable ItemSelection allow, @Nullable ItemSelection deny, boolean denyAll, @Nullable ItemSelection on, boolean close) {
        this.close = close;
        this.allow = allow;
        this.deny = deny;
        this.denyAll = denyAll;
        this.on = on;
    }

    public boolean empty() { return allow == null && deny == null && !denyAll && on == null; }

    public boolean refusesBlock(IBlockState state) { return refuses(selection -> listed(selection, state)); }

    public boolean refusesItem(ItemStack stack) { return refuses(selection -> selection.matches(stack)); }

    public boolean sitsOff(IBlockState below) { return on != null && !listed(on, below); }

    private boolean refuses(Predicate<ItemSelection> listed) { return denyAll || (allow != null && !listed.test(allow)) || (deny != null && listed.test(deny)); }

    private static boolean listed(ItemSelection selection, IBlockState state) {
        Block block = state.getBlock();
        if (selection.matches(block)) { return true; }
        Item item = Item.getItemFromBlock(block);
        return item != Items.AIR && selection.matches(new ItemStack(item, 1, block.damageDropped(state)));
    }
}
