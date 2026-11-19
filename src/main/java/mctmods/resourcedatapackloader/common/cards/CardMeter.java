package mctmods.resourcedatapackloader.common.cards;

import mctmods.resourcedatapackloader.common.intro.ContentIntroPlay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

final class CardMeter {
    private static final int SCAN = 20;
    private static final int GAUGE = 10;

    private CardMeter() {}

    static void show(MinecraftServer server, List<CardRule> rules) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (ContentIntroPlay.reading(player.getUUID())) { continue; }
            for (CardRule rule : rules) {
                if (player.tickCount % rule.every >= SCAN || rule.when == null) { continue; }
                ItemStack worn = rule.when.worn.found(player);
                if (!worn.isEmpty()) { CardFire.meter(rule, player, args(worn)); }
            }
        }
    }

    private static Map<String, String> args(ItemStack worn) {
        int max = worn.getMaxDamage();
        boolean wears = worn.isDamageableItem();
        int left = wears ? max - worn.getDamageValue() : 0;
        int percent = wears && max > 0 ? Math.max(0, Math.min(100, left * 100 / max)) : 100;
        int full = Math.round(percent * GAUGE / 100.0F);
        StringBuilder bar = new StringBuilder();
        for (int cell = 0; cell < GAUGE; cell++) { bar.append(cell < full ? '|' : '.'); }
        Map<String, String> args = new HashMap<>();
        args.put("worn", worn.getHoverName().getString());
        args.put("percent", Integer.toString(percent));
        args.put("left", Integer.toString(left));
        args.put("max", Integer.toString(wears ? max : 0));
        args.put("bar", bar.toString());
        return args;
    }
}
