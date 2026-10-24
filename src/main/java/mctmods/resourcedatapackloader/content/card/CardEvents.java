package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.ExposureDef;
import mctmods.resourcedatapackloader.content.entity.ContentRanks;
import mctmods.resourcedatapackloader.util.Functions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CardEvents {
    private static final ResourceLocation CARD = ResourceDataPackLoader.id("card");
    private static final Map<String, ItemStack> STACKS = new HashMap<>();
    private static final Map<UUID, Map<String, Integer>> LEVELS = new HashMap<>();

    private CardEvents() {}

    public static void joined(ServerPlayer player) {
        List<CardRule> rules = CardRules.on(CardRule.FIRST_JOIN);
        if (rules.isEmpty() || player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) > 0) { return; }
        for (CardRule rule : rules) { CardFire.fire(rule, player); }
    }

    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        for (CardRule rule : CardRules.on(CardRule.DIMENSION_ENTER)) { CardFire.fire(rule, player); }
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isEndConquered()) { return; }
        for (CardRule rule : CardRules.on(CardRule.RESPAWN)) { CardFire.fire(rule, player); }
    }

    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer dead) {
            for (CardRule rule : CardRules.on(CardRule.DEATH)) { CardFire.fire(rule, dead); }
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) { return; }
        List<CardRule> rules = CardRules.on(CardRule.KILL);
        if (rules.isEmpty()) { return; }
        ResourceLocation fallen = EntityType.getKey(event.getEntity().getType());
        for (CardRule rule : rules) {
            if (!fallen.equals(ResourceLocation.tryParse(rule.entity))) { continue; }
            if (!rule.rank.isEmpty() && !rule.rank.equals(ContentRanks.rankOf(event.getEntity()))) { continue; }
            if (rule.count > 1 && CardStorage.tallyFor(player, rule.key) < rule.count) { continue; }
            if (rule.count > 1) { CardStorage.clearTallyFor(player, rule.key); }
            CardFire.fire(rule, player);
        }
    }

    public static void raidEnded(ServerPlayer player, String raid, boolean won) {
        for (CardRule rule : CardRules.on(won ? CardRule.RAID_WON : CardRule.RAID_LOST)) {
            if (rule.raid.isEmpty() || rule.raid.equals(raid)) { CardFire.fire(rule, player); }
        }
    }

    public static void exposureLevel(ServerPlayer player, ExposureDef def, int level) {
        String hazard = def.name();
        Integer before = LEVELS.computeIfAbsent(player.getUUID(), id -> new HashMap<>()).put(hazard, level);
        if (before == null ? level == 0 : before == level) { return; }
        if (before != null) {
            for (CardRule rule : CardRules.on(CardRule.EXPOSURE_LEVEL)) {
                if (!rule.exposure.isEmpty() && !rule.exposure.equals(hazard)) { continue; }
                if (rule.level == null || rule.level == level) { CardFire.fire(rule, player); }
            }
        }
        String named = level > 0 ? def.levels().get(level - 1).onEnter() : def.onClear();
        if (!named.isEmpty()) { Functions.runAs(player, named, "Exposure " + hazard); }
    }

    public static void roundStart(MinecraftServer server) {
        List<CardRule> rules = CardRules.on(CardRule.ROUND_START);
        if (rules.isEmpty()) { return; }
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (CardRule rule : rules) { CardScan.fireAll(rule, players); }
    }

    public static void downed(ServerPlayer player) {
        for (CardRule rule : CardRules.on(CardRule.DOWNED)) { CardFire.fire(rule, player); }
    }

    public static void revived(ServerPlayer player) {
        for (CardRule rule : CardRules.on(CardRule.REVIVED)) { CardFire.fire(rule, player); }
    }

    public static void orderDone(ServerPlayer player, String sign) {
        for (CardRule rule : CardRules.on(CardRule.ORDER_DONE)) {
            if (rule.order.isEmpty() || rule.order.equalsIgnoreCase(sign)) { CardFire.fire(rule, player); }
        }
    }

    public static void rolled(ServerPlayer player, String what, int total) {
        if (what.isEmpty()) { return; }
        String named = ContentDice.notation(what);
        for (CardRule rule : CardRules.on(CardRule.ROLL)) {
            if (!rule.roll.isEmpty() && !rule.roll.equals(named)) { continue; }
            if (rule.atLeast != null && total < rule.atLeast) { continue; }
            if (rule.atMost != null && total > rule.atMost) { continue; }
            CardFire.fire(rule, player);
        }
    }

    public static void portway(ServerPlayer player, String trigger, String block, String network, String reason) {
        for (CardRule rule : CardRules.on(trigger)) {
            if (!rule.portway.isEmpty() && !rule.portway.equals(block)) { continue; }
            if (!rule.network.isEmpty() && !rule.network.equals(network)) { continue; }
            if (!rule.reason.isEmpty() && !rule.reason.equals(reason)) { continue; }
            CardFire.fire(rule, player);
        }
    }

    public static void environmentChanged(MinecraftServer server, ResourceLocation dimension, List<String> entered) {
        List<CardRule> rules = CardRules.on(CardRule.ENVIRONMENT_CHANGE);
        if (rules.isEmpty()) { return; }
        List<ServerPlayer> here = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.level().dimension().location().equals(dimension)) { here.add(player); }
        }
        if (here.isEmpty()) { return; }
        for (CardRule rule : rules) {
            if (rule.environment.isEmpty() || entered.contains(rule.environment)) { CardScan.fireAll(rule, here); }
        }
    }

    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        String earned = event.getAdvancement().getId().toString();
        for (CardRule rule : CardRules.on(CardRule.ADVANCEMENT)) {
            if (rule.advancement.equals(earned)) { CardFire.fire(rule, player); }
        }
    }

    public static void onCraft(PlayerEvent.ItemCraftedEvent event) { if (event.getEntity() instanceof ServerPlayer player) { item(CardRules.on(CardRule.CRAFT), player, event.getCrafting()); } }

    public static void onPickup(PlayerEvent.ItemPickupEvent event) { if (event.getEntity() instanceof ServerPlayer player) { item(CardRules.on(CardRule.PICKUP), player, event.getStack()); } }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CardFire.forget(event.getEntity().getUUID());
        CardScan.forget(event.getEntity().getUUID());
        CardSend.forget(event.getEntity().getUUID());
        LEVELS.remove(event.getEntity().getUUID());
    }

    private static void item(List<CardRule> rules, ServerPlayer player, ItemStack stack) {
        if (rules.isEmpty() || stack.isEmpty()) { return; }
        for (CardRule rule : rules) {
            ItemStack wanted = STACKS.computeIfAbsent(rule.item, item -> ContentStacks.parse(CARD, item, 1));
            if (!wanted.isEmpty() && ContentStacks.matches(stack, wanted)) { CardFire.fire(rule, player); }
        }
    }
}
