package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.util.Stacks;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CardEvents {
    private static final ResourceLocation CARD = new ResourceLocation("rdpl", "card");
    private static final Map<String, ItemStack> STACKS = new HashMap<>();

    private CardEvents() {}

    public static void joined(EntityPlayerMP player) {
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
            if (rule.count > 1 && CardStorage.tallyFor(player, rule.key) < rule.count) { continue; }
            if (rule.count > 1) { CardStorage.clearTallyFor(player, rule.key); }
            CardFire.fire(rule, player);
        }
    }

    @SubscribeEvent public static void onAdvancement(AdvancementEvent event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        String earned = event.getAdvancement().getId().toString();
        for (CardRule rule : CardRules.on(CardRule.ADVANCEMENT)) {
            if (rule.advancement.equals(earned)) { CardFire.fire(rule, (EntityPlayerMP) event.getEntityPlayer()); }
        }
    }

    @SubscribeEvent public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (event.player instanceof EntityPlayerMP) { item(CardRules.on(CardRule.CRAFT), (EntityPlayerMP) event.player, event.crafting); }
    }

    @SubscribeEvent public static void onPickup(PlayerEvent.ItemPickupEvent event) {
        if (event.player instanceof EntityPlayerMP) { item(CardRules.on(CardRule.PICKUP), (EntityPlayerMP) event.player, event.getStack()); }
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        CardFire.forget(event.player.getUniqueID());
        CardScan.forget(event.player.getUniqueID());
        CardSend.forget(event.player.getUniqueID());
    }

    private static void item(List<CardRule> rules, EntityPlayerMP player, ItemStack stack) {
        if (rules.isEmpty() || stack.isEmpty()) { return; }
        for (CardRule rule : rules) {
            ItemStack wanted = STACKS.computeIfAbsent(rule.item, item -> ContentStacks.parse(CARD, item, 1));
            if (!wanted.isEmpty() && Stacks.matches(wanted, stack)) { CardFire.fire(rule, player); }
        }
    }
}
