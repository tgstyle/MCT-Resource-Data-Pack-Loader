package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;

import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public final class WeightEnforce {
    private static final String TOO_HEAVY = "rdpl.weight.too_heavy";
    private static final long MESSAGE_GAP = 1_000_000_000L;

    private WeightEnforce() {}

    private static boolean refusesPickup(WeightDef settings) { return settings.hardLimit > 0.0D && (WeightDef.HARD_BOTH.equals(settings.hardLimitMode) || WeightDef.HARD_PICKUP.equals(settings.hardLimitMode)); }

    public static void onPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !ContentWeights.any()) { return; }
        WeightDef settings = ContentWeights.settings();
        if (!refusesPickup(settings) || !PlayerWeights.counted(player, settings)) { return; }
        PlayerWeights.Entry entry = PlayerWeights.current(player);
        if (entry.limit <= 0) { return; }
        ItemEntity ground = event.getItem();
        if (ground.target != null && !ground.target.equals(player.getUUID())) { return; }
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
        ForgeEventFactory.firePlayerItemPickupEvent(player, ground, picked);
        player.awardStat(Stats.ITEM_PICKED_UP.get(picked.getItem()), taken);
        PlayerWeights.mark(player);
    }

    private static void refuse(Player player, PlayerWeights.Entry entry) {
        long now = System.nanoTime();
        if (now - entry.refused < MESSAGE_GAP) { return; }
        entry.refused = now;
        player.displayClientMessage(Component.translatable(TOO_HEAVY), true);
    }
}
