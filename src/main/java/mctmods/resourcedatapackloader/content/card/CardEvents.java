package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.ContentStacks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CardEvents {
    private static final ResourceLocation CARD = ResourceLocation.fromNamespaceAndPath("rdpl", "card");
    private static final Map<String, ItemStack> STACKS = new HashMap<>();

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
            if (rule.count > 1 && CardStorage.tallyFor(player, rule.key) < rule.count) { continue; }
            if (rule.count > 1) { CardStorage.clearTallyFor(player, rule.key); }
            CardFire.fire(rule, player);
        }
    }

    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        String earned = event.getAdvancement().getId().toString();
        for (CardRule rule : CardRules.on(CardRule.ADVANCEMENT)) {
            if (rule.advancement.equals(earned)) { CardFire.fire(rule, player); }
        }
    }

    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { item(CardRules.on(CardRule.CRAFT), player, event.getCrafting()); }
    }

    public static void onPickup(PlayerEvent.ItemPickupEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { item(CardRules.on(CardRule.PICKUP), player, event.getStack()); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CardFire.forget(event.getEntity().getUUID());
        CardScan.forget(event.getEntity().getUUID());
        CardSend.forget(event.getEntity().getUUID());
    }

    private static void item(List<CardRule> rules, ServerPlayer player, ItemStack stack) {
        if (rules.isEmpty() || stack.isEmpty()) { return; }
        for (CardRule rule : rules) {
            ItemStack wanted = STACKS.computeIfAbsent(rule.item, item -> ContentStacks.parse(CARD, item, 1));
            if (!wanted.isEmpty() && ContentStacks.matches(stack, wanted)) { CardFire.fire(rule, player); }
        }
    }
}
