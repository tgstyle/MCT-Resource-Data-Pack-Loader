package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import mctmods.resourcedatapackloader.common.util.ItemSelection;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public record ZoneListDef(@Nullable ItemSelection allow, @Nullable ItemSelection deny, boolean denyAll, @Nullable ItemSelection on, boolean close) {
    public boolean empty() { return allow == null && deny == null && !denyAll && on == null; }

    public boolean refusesBlock(BlockState state) { return refuses(selection -> selection.matches(state.getBlock())); }

    public boolean refusesItem(ItemStack stack) { return refuses(selection -> selection.matches(stack)); }

    public boolean sitsOff(BlockState below) { return on != null && !on.matches(below.getBlock()); }

    private boolean refuses(Predicate<ItemSelection> listed) { return denyAll || allow != null && !listed.test(allow) || deny != null && listed.test(deny); }
}
