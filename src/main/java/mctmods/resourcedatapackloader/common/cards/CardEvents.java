package mctmods.resourcedatapackloader.common.cards;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.board.ContentDice;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentRanks;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.ExposureDef;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.PlayerLogout;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.common.util.world.PlayerNear;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class CardEvents {
    private static final ResourceLocation CARD = ResourceDataPackLoader.id("card");
    private static final Map<String, ItemStack> STACKS = new HashMap<>();
    private static final Map<UUID, Map<String, Integer>> LEVELS = PlayerLogout.track(new HashMap<>());

    private CardEvents() {}

    public static void joined(EntityPlayerMP player) {
        for (CardRule rule : CardRules.on(CardRule.LOGIN)) { CardFire.fire(rule, player); }
        List<CardRule> rules = CardRules.on(CardRule.FIRST_JOIN);
        if (rules.isEmpty() || player.getStatFile().readStat(StatList.LEAVE_GAME) > 0) { return; }
        for (CardRule rule : rules) { CardFire.fire(rule, player); }
    }

    @SubscribeEvent public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) { return; }
        for (CardRule rule : CardRules.on(CardRule.DIMENSION_ENTER)) { CardFire.fire(rule, (EntityPlayerMP) event.player); }
    }

    @SubscribeEvent public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.player instanceof EntityPlayerMP) || event.isEndConquered()) { return; }
        for (CardRule rule : CardRules.on(CardRule.RESPAWN)) { CardFire.fire(rule, (EntityPlayerMP) event.player); }
    }

    @SubscribeEvent public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            for (CardRule rule : CardRules.on(CardRule.DEATH)) { CardFire.fire(rule, (EntityPlayerMP) event.getEntity()); }
        }
        if (!(event.getSource().getTrueSource() instanceof EntityPlayerMP)) { return; }
        List<CardRule> rules = CardRules.on(CardRule.KILL);
        ResourceLocation fallen = rules.isEmpty() ? null : EntityList.getKey(event.getEntity());
        if (fallen == null) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
        for (CardRule rule : rules) {
            if (!fallen.equals(new ResourceLocation(rule.entity))) { continue; }
            if (!CardRule.wants(rule.rank, ContentRanks.rankOf(event.getEntity()))) { continue; }
            if (rule.count > 1 && CardStorage.tallyFor(player, rule.key) < rule.count) { continue; }
            if (rule.count > 1) { CardStorage.clearTallyFor(player, rule.key); }
            CardFire.fire(rule, player);
        }
    }

    public static void rolled(EntityPlayerMP player, String what, int total) {
        if (what.isEmpty()) { return; }
        String named = ContentDice.notation(what);
        for (CardRule rule : CardRules.on(CardRule.ROLL)) {
            if (!CardRule.wants(rule.roll, named)) { continue; }
            if (rule.atLeast != null && total < rule.atLeast) { continue; }
            if (rule.atMost != null && total > rule.atMost) { continue; }
            CardFire.fire(rule, player);
        }
    }

    public static void orderDone(EntityPlayerMP player, String sign) {
        for (CardRule rule : CardRules.on(CardRule.ORDER_DONE)) {
            if (rule.order.isEmpty() || rule.order.equalsIgnoreCase(sign)) { CardFire.fire(rule, player); }
        }
    }

    public static void raidEnded(EntityPlayerMP player, String raid, boolean won) {
        for (CardRule rule : CardRules.on(won ? CardRule.RAID_WON : CardRule.RAID_LOST)) {
            if (CardRule.wants(rule.raid, raid)) { CardFire.fire(rule, player); }
        }
    }

    public static void downed(EntityPlayerMP player) {
        for (CardRule rule : CardRules.on(CardRule.DOWNED)) { CardFire.fire(rule, player); }
    }

    public static void revived(EntityPlayerMP player) {
        for (CardRule rule : CardRules.on(CardRule.REVIVED)) { CardFire.fire(rule, player); }
    }

    public static void portway(EntityPlayerMP player, String trigger, String block, String network, String reason) {
        for (CardRule rule : CardRules.on(trigger)) {
            if (!CardRule.wants(rule.portway, block)) { continue; }
            if (!CardRule.wants(rule.network, network)) { continue; }
            if (!CardRule.wants(rule.reason, reason)) { continue; }
            CardFire.fire(rule, player);
        }
    }

    public static void exposureLevel(EntityPlayerMP player, ExposureDef def, int level) {
        String hazard = def.name;
        Integer before = LEVELS.computeIfAbsent(player.getUniqueID(), id -> new HashMap<>()).put(hazard, level);
        if (before == null ? level == 0 : before == level) { return; }
        if (before != null) {
            for (CardRule rule : CardRules.on(CardRule.EXPOSURE_LEVEL)) {
                if (!CardRule.wants(rule.exposure, hazard)) { continue; }
                if (rule.level == null || rule.level == level) { CardFire.fire(rule, player); }
            }
        }
        String named = level > 0 ? def.levels.get(level - 1).onEnter : def.onClear;
        if (!named.isEmpty()) { Functions.runAs(player, named, "Exposure " + hazard); }
    }

    public static void roundStart(MinecraftServer server) {
        List<CardRule> rules = CardRules.on(CardRule.ROUND_START);
        if (rules.isEmpty()) { return; }
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        for (CardRule rule : rules) { CardScan.fireAll(rule, players); }
    }

    public static void environmentChanged(MinecraftServer server, int dimension, List<String> entered) {
        List<CardRule> rules = CardRules.on(CardRule.ENVIRONMENT_CHANGE);
        if (rules.isEmpty()) { return; }
        List<EntityPlayerMP> here = PlayerNear.playersIn(server, dimension, player -> true);
        if (here.isEmpty()) { return; }
        for (CardRule rule : rules) {
            if (rule.environment.isEmpty() || entered.contains(rule.environment)) { CardScan.fireAll(rule, here); }
        }
    }

    @SubscribeEvent public static void onAdvancement(AdvancementEvent event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        String earned = event.getAdvancement().getId().toString();
        for (CardRule rule : CardRules.on(CardRule.ADVANCEMENT)) {
            if (rule.advancement.equals(earned)) { CardFire.fire(rule, (EntityPlayerMP) event.getEntityPlayer()); }
        }
    }

    @SubscribeEvent public static void onCraft(PlayerEvent.ItemCraftedEvent event) { if (event.player instanceof EntityPlayerMP) { item(CardRules.on(CardRule.CRAFT), (EntityPlayerMP) event.player, event.crafting); } }

    @SubscribeEvent public static void onPickup(PlayerEvent.ItemPickupEvent event) { if (event.player instanceof EntityPlayerMP) { item(CardRules.on(CardRule.PICKUP), (EntityPlayerMP) event.player, event.getStack()); } }

    private static void item(List<CardRule> rules, EntityPlayerMP player, ItemStack stack) {
        if (rules.isEmpty() || stack.isEmpty()) { return; }
        for (CardRule rule : rules) {
            ItemStack wanted = STACKS.computeIfAbsent(rule.item, item -> ContentStacks.parse(CARD, item, 1));
            if (!wanted.isEmpty() && Stacks.matches(wanted, stack)) { CardFire.fire(rule, player); }
        }
    }
}
