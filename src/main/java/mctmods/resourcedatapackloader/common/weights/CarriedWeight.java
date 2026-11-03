package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.common.items.ContentPouchInventory;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public final class CarriedWeight {
    private static final int DEPTH = 3;
    private static final String ITEMS = "Items";
    private static final String TILE = "BlockEntityTag";

    private CarriedWeight() {}

    public static List<ItemStack> worn(Player player) {
        List<ItemStack> found = new ArrayList<>();
        for (ItemStack stack : player.getInventory().armor) {
            if (!stack.isEmpty()) { found.add(stack); }
        }
        found.addAll(ContentCurios.worn(player, stack -> !stack.isEmpty()));
        return found;
    }

    public static long total(Player player, WeightDef settings, List<ItemStack> worn) {
        long sum = 0L;
        for (ItemStack stack : player.getInventory().items) { sum += weigh(stack, settings, 0); }
        for (ItemStack stack : player.getInventory().offhand) { sum += weigh(stack, settings, 0); }
        sum += weigh(player.containerMenu.getCarried(), settings, 0);
        sum += weigh(ContentCarry.carried(player), settings, 0);
        long wornSum = 0L;
        for (ItemStack stack : worn) { wornSum += weigh(stack, settings, 0); }
        return sum + Math.round(wornSum * settings.wornFactor);
    }

    public static long single(ItemStack stack, WeightDef settings) { return stack.isEmpty() ? 0L : ContentWeights.of(stack) + held(stack, settings, 0); }

    private static long weigh(ItemStack stack, WeightDef settings, int depth) {
        if (stack.isEmpty()) { return 0L; }
        return ((long) ContentWeights.of(stack) + held(stack, settings, depth)) * stack.getCount();
    }

    private static long held(ItemStack stack, WeightDef settings, int depth) { return depth < DEPTH ? Math.round(contents(stack, settings, depth) * settings.containerFactor) : 0L; }

    private static long contents(ItemStack stack, WeightDef settings, int depth) {
        if (WeightDef.CONTENTS_NONE.equals(settings.countContents)) { return 0L; }
        CompoundTag tag = stack.getTag();
        if (stack.getItem() instanceof ContentContainerItem) { return tag == null ? 0L : listed(tag.getCompound(ContentPouchInventory.HELD).getList(ITEMS, Tag.TAG_COMPOUND), settings, depth); }
        if (!WeightDef.CONTENTS_ALL.equals(settings.countContents)) { return 0L; }
        IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
        if (handler != null) {
            long sum = 0L;
            for (int slot = 0; slot < handler.getSlots(); slot++) { sum += weigh(handler.getStackInSlot(slot), settings, depth + 1); }
            return sum;
        }
        if (tag == null) { return 0L; }
        return listed(tag.getCompound(TILE).getList(ITEMS, Tag.TAG_COMPOUND), settings, depth) + listed(tag.getList(ITEMS, Tag.TAG_COMPOUND), settings, depth);
    }

    private static long listed(ListTag list, WeightDef settings, int depth) {
        long sum = 0L;
        for (int index = 0; index < list.size(); index++) { sum += weigh(ItemStack.of(list.getCompound(index)), settings, depth + 1); }
        return sum;
    }
}
