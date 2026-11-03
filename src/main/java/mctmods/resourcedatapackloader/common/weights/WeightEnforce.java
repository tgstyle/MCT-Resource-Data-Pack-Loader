package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;

import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

public final class WeightEnforce {
    private static final String TOO_HEAVY = "rdpl.weight.too_heavy";
    private static final long MESSAGE_GAP = 1_000_000_000L;

    private WeightEnforce() {}

    private static boolean refusesPickup(WeightDef settings) { return settings.hardLimit > 0.0D && (WeightDef.HARD_BOTH.equals(settings.hardLimitMode) || WeightDef.HARD_PICKUP.equals(settings.hardLimitMode)); }

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

    private static void refuse(Player player, PlayerWeights.Entry entry) {
        long now = System.nanoTime();
        if (now - entry.refused < MESSAGE_GAP) { return; }
        entry.refused = now;
        player.displayClientMessage(Component.translatable(TOO_HEAVY), true);
    }
}
