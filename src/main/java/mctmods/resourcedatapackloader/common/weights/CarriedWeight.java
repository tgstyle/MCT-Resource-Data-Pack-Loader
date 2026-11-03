package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.gui.PouchInventory;
import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;
import mctmods.resourcedatapackloader.core.integration.baubles.BaublesPouch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public final class CarriedWeight {
    private static final int DEPTH = 3;
    private static final String ITEMS = "Items";
    private static final String TILE = "BlockEntityTag";

    private CarriedWeight() {}

    public static List<ItemStack> worn(EntityPlayer player) {
        List<ItemStack> found = new ArrayList<>();
        for (ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty()) { found.add(stack); }
        }
        if (ContentContainerItem.baubled()) { found.addAll(BaublesPouch.wornStacks(player)); }
        return found;
    }

    public static long total(EntityPlayer player, WeightDef settings, List<ItemStack> worn) {
        long sum = 0L;
        for (ItemStack stack : player.inventory.mainInventory) { sum += weigh(stack, settings, 0); }
        for (ItemStack stack : player.inventory.offHandInventory) { sum += weigh(stack, settings, 0); }
        sum += weigh(player.inventory.getItemStack(), settings, 0);
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
        NBTTagCompound tag = stack.getTagCompound();
        if (stack.getItem() instanceof ContentContainerItem) { return tag == null ? 0L : listed(tag.getCompoundTag(PouchInventory.HELD).getTagList(ITEMS, Constants.NBT.TAG_COMPOUND), settings, depth); }
        if (!WeightDef.CONTENTS_ALL.equals(settings.countContents)) { return 0L; }
        IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (handler != null) {
            long sum = 0L;
            for (int slot = 0; slot < handler.getSlots(); slot++) { sum += weigh(handler.getStackInSlot(slot), settings, depth + 1); }
            return sum;
        }
        if (tag == null) { return 0L; }
        return listed(tag.getCompoundTag(TILE).getTagList(ITEMS, Constants.NBT.TAG_COMPOUND), settings, depth) + listed(tag.getTagList(ITEMS, Constants.NBT.TAG_COMPOUND), settings, depth);
    }

    private static long listed(NBTTagList list, WeightDef settings, int depth) {
        long sum = 0L;
        for (int index = 0; index < list.tagCount(); index++) { sum += weigh(new ItemStack(list.getCompoundTagAt(index)), settings, depth + 1); }
        return sum;
    }
}
