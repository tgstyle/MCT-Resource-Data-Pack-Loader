package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public final class CarriedWeight {
    private static final int DEPTH = 3;
    private static final String ITEMS = "Items";

    private CarriedWeight() {}

    private record Scope(WeightDef settings, HolderLookup.Provider access) {}

    public static List<ItemStack> worn(Player player) {
        List<ItemStack> found = new ArrayList<>();
        for (ItemStack stack : player.getInventory().armor) {
            if (!stack.isEmpty()) { found.add(stack); }
        }
        found.addAll(ContentCurios.worn(player, stack -> !stack.isEmpty()));
        return found;
    }

    public static long total(Player player, WeightDef settings, List<ItemStack> worn) {
        Scope scope = new Scope(settings, player.registryAccess());
        long sum = 0L;
        for (ItemStack stack : player.getInventory().items) { sum += weigh(stack, scope, 0); }
        for (ItemStack stack : player.getInventory().offhand) { sum += weigh(stack, scope, 0); }
        sum += weigh(player.containerMenu.getCarried(), scope, 0);
        sum += weigh(ContentCarry.carried(player), scope, 0);
        long wornSum = 0L;
        for (ItemStack stack : worn) { wornSum += weigh(stack, scope, 0); }
        return sum + Math.round(wornSum * settings.wornFactor);
    }

    public static long single(Player player, ItemStack stack, WeightDef settings) { return stack.isEmpty() ? 0L : ContentWeights.of(stack) + held(stack, new Scope(settings, player.registryAccess()), 0); }

    private static long weigh(ItemStack stack, Scope scope, int depth) {
        if (stack.isEmpty()) { return 0L; }
        return ((long) ContentWeights.of(stack) + held(stack, scope, depth)) * stack.getCount();
    }

    private static long held(ItemStack stack, Scope scope, int depth) { return depth < DEPTH ? Math.round(contents(stack, scope, depth) * scope.settings.containerFactor) : 0L; }

    private static long contents(ItemStack stack, Scope scope, int depth) {
        if (WeightDef.CONTENTS_NONE.equals(scope.settings.countContents)) { return 0L; }
        if (stack.getItem() instanceof ContentContainerItem) { return stacks(stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems(), scope, depth); }
        if (!WeightDef.CONTENTS_ALL.equals(scope.settings.countContents)) { return 0L; }
        IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler != null) {
            long sum = 0L;
            for (int slot = 0; slot < handler.getSlots(); slot++) { sum += weigh(handler.getStackInSlot(slot), scope, depth + 1); }
            return sum;
        }
        long sum = stacks(stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems(), scope, depth);
        sum += stacks(stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).items(), scope, depth);
        CustomData tile = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return tile == null ? sum : sum + listed(tile.copyTag().getList(ITEMS, Tag.TAG_COMPOUND).stream().map(CompoundTag.class::cast).toList(), scope, depth);
    }

    private static long stacks(Iterable<ItemStack> held, Scope scope, int depth) {
        long sum = 0L;
        for (ItemStack stack : held) { sum += weigh(stack, scope, depth + 1); }
        return sum;
    }

    private static long listed(List<CompoundTag> list, Scope scope, int depth) {
        long sum = 0L;
        for (CompoundTag tag : list) { sum += weigh(ItemStack.parse(scope.access, tag).orElse(ItemStack.EMPTY), scope, depth + 1); }
        return sum;
    }
}
