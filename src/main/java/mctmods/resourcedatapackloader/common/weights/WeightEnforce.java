package mctmods.resourcedatapackloader.common.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.weights.WeightDef;

import com.google.common.base.Strings;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class WeightEnforce {
    private static final String TOO_HEAVY = "rdpl.weight.too_heavy";
    private static final long MESSAGE_GAP = 1_000_000_000L;

    private WeightEnforce() {}

    private static boolean refusesPickup(WeightDef settings) { return settings.hardLimit > 0.0D && (WeightDef.HARD_BOTH.equals(settings.hardLimitMode) || WeightDef.HARD_PICKUP.equals(settings.hardLimitMode)); }

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

    private static void refuse(EntityPlayer player, PlayerWeights.Entry entry) {
        long now = System.nanoTime();
        if (now - entry.refused < MESSAGE_GAP) { return; }
        entry.refused = now;
        player.sendStatusMessage(new TextComponentTranslation(TOO_HEAVY), true);
    }
}
