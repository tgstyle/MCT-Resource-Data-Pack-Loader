package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.gui.ContentContainerMenu;
import mctmods.resourcedatapackloader.common.items.ContentPouchInventory;
import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

public final class WeightEnforce {
    private static final String TOO_HEAVY = "rdpl.weight.too_heavy";
    private static final long MESSAGE_GAP = 1_000_000_000L;
    @Nullable private static ClickType clicked;
    private static int button;

    private WeightEnforce() {}

    private static boolean refusesPickup(WeightDef settings) { return settings.hardLimit > 0.0D && (WeightDef.HARD_BOTH.equals(settings.hardLimitMode) || WeightDef.HARD_PICKUP.equals(settings.hardLimitMode)); }

    private static boolean refusesTaking(WeightDef settings) { return settings.hardLimit > 0.0D && (WeightDef.HARD_BOTH.equals(settings.hardLimitMode) || WeightDef.HARD_TAKE.equals(settings.hardLimitMode)); }

    public static void onPickup(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide() || !ContentWeights.any()) { return; }
        WeightDef settings = ContentWeights.settings();
        if (!refusesPickup(settings) || !PlayerWeights.counted(player, settings)) { return; }
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        if (entry.limit <= 0) { return; }
        ItemEntity ground = event.getItemEntity();
        if (ground.hasPickUpDelay() || ground.getTarget() != null && !ground.getTarget().equals(player.getUUID())) { return; }
        ItemStack stack = ground.getItem();
        long unit = CarriedWeight.single(player, stack, settings);
        if (unit <= 0L) { return; }
        long room = (long) (settings.hardLimit * entry.limit) - entry.total;
        if (unit * stack.getCount() <= room) { return; }
        event.setCanPickup(TriState.FALSE);
        int fit = Math.clamp(room / unit, 0, stack.getCount());
        if (fit <= 0) {
            refuse(player, entry);
            return;
        }
        ItemStack part = stack.copy();
        part.setCount(fit);
        player.getInventory().add(part);
        int taken = fit - part.getCount();
        if (taken <= 0) {
            refuse(player, entry);
            return;
        }
        ItemStack picked = stack.copy();
        picked.setCount(taken);
        stack.shrink(taken);
        ground.setItem(stack);
        EventHooks.fireItemPickupPost(ground, player, picked);
        player.awardStat(Stats.ITEM_PICKED_UP.get(picked.getItem()), taken);
        PlayerWeights.mark(player);
    }

    public static void beginClick(Player player, ClickType type, int pressed) {
        if (!(player instanceof ServerPlayer)) { return; }
        clicked = type == ClickType.QUICK_CRAFT ? ClickType.PICKUP : type;
        button = pressed;
    }

    public static void endClick(Player player) {
        if (!(player instanceof ServerPlayer)) { return; }
        clicked = null;
    }

    public static boolean refusesTake(Slot slot, Player player) {
        if (clicked == null || !(player instanceof ServerPlayer) || player instanceof FakePlayer || slot.container == player.getInventory() || !ContentWeights.any()) { return false; }
        WeightDef settings = ContentWeights.settings();
        if (!refusesTaking(settings) || !PlayerWeights.counted(player, settings)) { return false; }
        ItemStack stack = slot.getItem();
        ItemStack cursor = player.containerMenu.getCarried();
        ItemStack given = given(slot, cursor, player);
        double gain = (CarriedWeight.single(player, stack, settings) * taken(slot, stack, cursor) - CarriedWeight.single(player, given, settings) * Math.min(given.getCount(), slot.getMaxStackSize(given))) * (1.0D - kept(slot, player, settings));
        if (gain <= 0.0D) { return false; }
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        if (entry.limit <= 0) { return false; }
        if (gain <= (long) (settings.hardLimit * entry.limit) - entry.total) {
            PlayerWeights.mark(player);
            return false;
        }
        refuse(player, entry);
        return true;
    }

    private static int taken(Slot slot, ItemStack stack, ItemStack cursor) {
        int count = stack.getCount();
        if (clicked == ClickType.QUICK_MOVE || clicked == ClickType.SWAP) { return count; }
        if (clicked == ClickType.PICKUP_ALL) { return Math.min(count, cursor.getMaxStackSize() - cursor.getCount()); }
        if (clicked != ClickType.PICKUP) { return 0; }
        if (cursor.isEmpty()) { return button == 0 ? count : (count + 1) / 2; }
        return slot.mayPlace(cursor) && ItemStack.isSameItemSameComponents(stack, cursor) ? 0 : count;
    }

    private static ItemStack given(Slot slot, ItemStack cursor, Player player) {
        if (clicked == ClickType.SWAP) { return player.getInventory().getItem(button); }
        return clicked == ClickType.PICKUP && slot.mayPlace(cursor) ? cursor : ItemStack.EMPTY;
    }

    private static double kept(Slot slot, Player player, WeightDef settings) {
        if (!(slot.container instanceof ContentPouchInventory) || WeightDef.CONTENTS_NONE.equals(settings.countContents)) { return 0.0D; }
        return player.containerMenu instanceof ContentContainerMenu menu && menu.worn() >= 0 ? settings.containerFactor * settings.wornFactor : settings.containerFactor;
    }

    private static void refuse(Player player, PlayerWeights.Entry entry) {
        long now = System.nanoTime();
        if (now - entry.refused < MESSAGE_GAP) { return; }
        entry.refused = now;
        player.displayClientMessage(Component.translatable(TOO_HEAVY), true);
    }
}
