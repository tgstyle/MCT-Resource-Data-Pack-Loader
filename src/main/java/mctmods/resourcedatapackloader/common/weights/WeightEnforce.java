package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.gui.ContainerPouch;
import mctmods.resourcedatapackloader.common.gui.PouchInventory;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;

import com.google.common.base.Strings;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class WeightEnforce {
    private static final String TOO_HEAVY = "rdpl.weight.too_heavy";
    private static final long MESSAGE_GAP = 1_000_000_000L;
    @Nullable private static ClickType clicked;
    private static int button;
    private static boolean resync;

    private WeightEnforce() {}

    private static boolean refusesPickup(WeightDef settings) { return settings.hardLimit > 0.0D && (WeightDef.HARD_BOTH.equals(settings.hardLimitMode) || WeightDef.HARD_PICKUP.equals(settings.hardLimitMode)); }

    private static boolean refusesTaking(WeightDef settings) { return settings.hardLimit > 0.0D && (WeightDef.HARD_BOTH.equals(settings.hardLimitMode) || WeightDef.HARD_TAKE.equals(settings.hardLimitMode)); }

    @SubscribeEvent public static void onPickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote || !ContentWeights.any()) { return; }
        WeightDef settings = ContentWeights.settings();
        if (!refusesPickup(settings) || !PlayerWeights.counted(player, settings)) { return; }
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        if (entry.limit <= 0) { return; }
        EntityItem ground = event.getItem();
        if (!Strings.isNullOrEmpty(ground.getOwner()) && ground.lifespan - ground.age > 200 && !ground.getOwner().equals(player.getName())) { return; }
        ItemStack stack = ground.getItem();
        long unit = CarriedWeight.single(stack, settings);
        if (unit <= 0L) { return; }
        long room = (long) (settings.hardLimit * entry.limit) - entry.total;
        if (unit * stack.getCount() <= room) { return; }
        event.setCanceled(true);
        int fit = (int) Math.min(stack.getCount(), Math.max(0L, room / unit));
        if (fit <= 0) {
            refuse(player, entry);
            return;
        }
        ItemStack part = stack.copy();
        part.setCount(fit);
        player.inventory.addItemStackToInventory(part);
        int taken = fit - part.getCount();
        if (taken <= 0) {
            refuse(player, entry);
            return;
        }
        ItemStack picked = stack.copy();
        picked.setCount(taken);
        stack.shrink(taken);
        ground.setItem(stack);
        FMLCommonHandler.instance().firePlayerItemPickupEvent(player, ground, picked);
        StatBase stat = StatList.getObjectsPickedUpStats(picked.getItem());
        if (stat != null) { player.addStat(stat, taken); }
        PlayerWeights.mark(player);
    }

    public static void beginClick(EntityPlayer player, ClickType type, int pressed) {
        if (!(player instanceof EntityPlayerMP)) { return; }
        clicked = type;
        resync = false;
        button = pressed;
    }

    public static void endClick(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) { return; }
        clicked = null;
        if (!resync) { return; }
        resync = false;
        ((EntityPlayerMP) player).sendAllContents(player.openContainer, player.openContainer.getInventory());
    }

    public static boolean refusesTake(Slot slot, EntityPlayer player) {
        if (clicked == null || !(player instanceof EntityPlayerMP) || player instanceof FakePlayer || slot.inventory == player.inventory || !ContentWeights.any()) { return false; }
        WeightDef settings = ContentWeights.settings();
        if (!refusesTaking(settings) || !PlayerWeights.counted(player, settings)) { return false; }
        ItemStack stack = slot.getStack();
        ItemStack cursor = player.inventory.getItemStack();
        ItemStack given = given(slot, cursor, player);
        double gain = (CarriedWeight.single(stack, settings) * taken(slot, stack, cursor) - CarriedWeight.single(given, settings) * Math.min(given.getCount(), slot.getItemStackLimit(given))) * (1.0D - kept(slot, player, settings));
        if (gain <= 0.0D) { return false; }
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        if (entry.limit <= 0) { return false; }
        if (gain <= (long) (settings.hardLimit * entry.limit) - entry.total) {
            PlayerWeights.mark(player);
            return false;
        }
        resync = true;
        refuse(player, entry);
        return true;
    }

    private static int taken(Slot slot, ItemStack stack, ItemStack cursor) {
        int count = stack.getCount();
        if (clicked == ClickType.QUICK_MOVE || clicked == ClickType.SWAP) { return count; }
        if (clicked == ClickType.PICKUP_ALL) { return Math.min(count, cursor.getMaxStackSize() - cursor.getCount()); }
        if (clicked != ClickType.PICKUP) { return 0; }
        if (cursor.isEmpty()) { return button == 0 ? count : (count + 1) / 2; }
        return slot.isItemValid(cursor) && ItemStack.areItemsEqual(stack, cursor) && ItemStack.areItemStackTagsEqual(stack, cursor) ? 0 : count;
    }

    private static ItemStack given(Slot slot, ItemStack cursor, EntityPlayer player) {
        if (clicked == ClickType.SWAP) { return player.inventory.getStackInSlot(button); }
        return clicked == ClickType.PICKUP && slot.isItemValid(cursor) ? cursor : ItemStack.EMPTY;
    }

    private static double kept(Slot slot, EntityPlayer player, WeightDef settings) {
        if (!(slot.inventory instanceof PouchInventory) || WeightDef.CONTENTS_NONE.equals(settings.countContents)) { return 0.0D; }
        return player.openContainer instanceof ContainerPouch && ((ContainerPouch) player.openContainer).worn() >= 0 ? settings.containerFactor * settings.wornFactor : settings.containerFactor;
    }

    private static void refuse(EntityPlayer player, PlayerWeights.Entry entry) {
        long now = System.nanoTime();
        if (now - entry.refused < MESSAGE_GAP) { return; }
        entry.refused = now;
        player.sendStatusMessage(new TextComponentTranslation(TOO_HEAVY), true);
    }
}
