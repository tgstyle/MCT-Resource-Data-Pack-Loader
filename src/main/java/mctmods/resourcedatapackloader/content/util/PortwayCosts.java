package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.def.CostDef;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class PortwayCosts {
    private static final String READY = "ready";
    private static final long TICKS = 20L;

    private PortwayCosts() {}

    public record Refusal(String reason, String text) {}

    public static boolean exempt(Player player) { return player.isCreative() || player.isSpectator(); }

    private static Item item(CostDef cost) {
        ResourceLocation id = cost.itemId().isEmpty() ? null : ResourceLocation.tryParse(cost.itemId());
        Item found = id == null ? null : ForgeRegistries.ITEMS.getValue(id);
        return found == null ? Items.AIR : found;
    }

    private static int carried(Player player, Item item) {
        int total = 0;
        for (ItemStack held : player.getInventory().items) {
            if (held.is(item)) { total += held.getCount(); }
        }
        return total;
    }

    private static int barCap(int level) { return level >= 30 ? 112 + (level - 30) * 9 : level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2; }

    private static int points(Player player) {
        long total = 0L;
        for (int level = 0; level < player.experienceLevel; level++) { total += barCap(level); }
        total += Math.round(player.experienceProgress * barCap(player.experienceLevel));
        return (int) Math.min(total, Integer.MAX_VALUE);
    }

    private static void takePoints(Player player, int taken) {
        int left = Math.max(0, points(player) - taken);
        player.totalExperience = left;
        int level = 0;
        int rest = left;
        while (rest >= barCap(level)) {
            rest -= barCap(level);
            level++;
        }
        player.experienceLevel = level;
        player.experienceProgress = (float) rest / barCap(level);
        player.giveExperienceLevels(0);
    }

    public static List<Component> lines(CostDef cost, double distance, boolean cross) {
        List<Component> lines = new ArrayList<>();
        int xp = cost.xp().of(distance, cross);
        if (xp > 0) { lines.add(Component.translatable(cost.xpPoints() ? (xp == 1 ? "rdpl.portway.pays.point" : "rdpl.portway.pays.points") : xp == 1 ? "rdpl.portway.pays.level" : "rdpl.portway.pays.levels", xp)); }
        int items = item(cost) == Items.AIR ? 0 : cost.item().of(distance, cross);
        if (items > 0) { lines.add(Component.translatable("rdpl.portway.pays.item", items, item(cost).getDescription())); }
        int health = cost.health().of(distance, cross);
        if (health > 0) { lines.add(Component.translatable("rdpl.portway.pays.health", health)); }
        int hunger = cost.hunger().of(distance, cross);
        if (hunger > 0) { lines.add(Component.translatable("rdpl.portway.pays.hunger", hunger)); }
        return lines;
    }

    private static long ready(ServerPlayer player) { return PlayerPersisted.read(player, Portways.SECTION).getLong(READY); }

    private static void setReady(ServerPlayer player, long at) {
        if (at > 0L) { PlayerPersisted.section(player, Portways.SECTION).putLong(READY, at); }
        else { PlayerPersisted.section(player, Portways.SECTION).remove(READY); }
    }

    public static void cooldown(ServerPlayer player, int seconds) { setReady(player, seconds > 0 ? CardFire.clock(player.server) + seconds * TICKS : 0L); }

    public static int waiting(ServerPlayer player) {
        if (exempt(player)) { return 0; }
        long left = ready(player) - CardFire.clock(player.server);
        return left > 0L ? (int) ((left + TICKS - 1L) / TICKS) : 0;
    }

    public static void stamp(ServerPlayer player, CostDef cost) {
        if (cost.cooldown() > 0 && !exempt(player)) { setReady(player, CardFire.clock(player.server) + cost.cooldown() * TICKS); }
    }

    @Nullable public static Refusal refusal(ServerPlayer player, CostDef cost, double distance, boolean cross) {
        if (exempt(player)) { return null; }
        int seconds = waiting(player);
        if (seconds > 0) { return new Refusal("cooldown", Lang.tr(player, seconds == 1 ? "rdpl.portway.cooldown.one" : "rdpl.portway.cooldown", seconds)); }
        int xp = cost.xp().of(distance, cross);
        if (xp > 0 && (cost.xpPoints() ? points(player) : player.experienceLevel) < xp) { return new Refusal("cost", Lang.tr(player, cost.xpPoints() ? (xp == 1 ? "rdpl.portway.cost.xp.points.one" : "rdpl.portway.cost.xp.points") : xp == 1 ? "rdpl.portway.cost.xp.one" : "rdpl.portway.cost.xp", xp)); }
        int items = item(cost) == Items.AIR ? 0 : cost.item().of(distance, cross);
        if (items > 0 && carried(player, item(cost)) < items) { return new Refusal("cost", Lang.tr(player, "rdpl.portway.cost.item", items, item(cost).getDescription().getString())); }
        return null;
    }

    public static boolean pay(ServerPlayer player, CostDef cost, double distance, boolean cross) {
        if (exempt(player)) { return false; }
        int xp = cost.xp().of(distance, cross);
        if (xp > 0) {
            if (cost.xpPoints()) { takePoints(player, xp); }
            else { player.giveExperienceLevels(-xp); }
        }
        boolean items = takeItems(player, cost, distance, cross);
        float health = Math.min(cost.health().of(distance, cross), player.getHealth() - 1.0F);
        if (health > 0.0F) { player.setHealth(player.getHealth() - health); }
        int hunger = Math.min(cost.hunger().of(distance, cross), player.getFoodData().getFoodLevel());
        if (hunger > 0) { player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - hunger); }
        return xp > 0 || items || health > 0.0F || hunger > 0;
    }

    private static boolean takeItems(ServerPlayer player, CostDef cost, double distance, boolean cross) {
        Item item = item(cost);
        int owed = item == Items.AIR ? 0 : cost.item().of(distance, cross);
        boolean charged = owed > 0;
        for (ItemStack held : player.getInventory().items) {
            if (owed <= 0) { break; }
            if (!held.is(item)) { continue; }
            int taken = Math.min(owed, held.getCount());
            held.shrink(taken);
            owed -= taken;
        }
        return charged;
    }
}
