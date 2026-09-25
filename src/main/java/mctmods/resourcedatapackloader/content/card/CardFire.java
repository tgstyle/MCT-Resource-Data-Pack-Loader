package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class CardFire {
    private static final int TICKS_PER_SECOND = 20;
    private static final Map<UUID, Set<String>> SESSION = new HashMap<>();

    private CardFire() {}

    static void fire(CardRule rule, EntityPlayerMP subject) {
        if (blocked(rule, subject)) { return; }
        CardLook look = CardLook.plain();
        CardSend.dress(look, rule, subject);
        for (EntityPlayerMP player : CardSend.audience(rule, subject)) { CardSend.send(look, player); }
        ContentLog.LOGGER.debug("Card rule {} fired for {}", rule.key, subject.getName());
        runs(rule, subject);
    }

    public static void builtin(String key, EntityPlayerMP subject, CardLook look) {
        CardRule rule = CardRules.builtin(key);
        if (rule == null || blocked(rule, subject)) { return; }
        CardSend.dress(look, rule, subject);
        CardSend.send(look, subject);
        runs(rule, subject);
    }

    public static void show(CardRule rule, EntityPlayerMP subject) {
        CardLook look = CardLook.plain();
        CardSend.dress(look, rule, subject);
        CardSend.send(look, subject);
        runs(rule, subject);
    }

    private static boolean blocked(CardRule rule, EntityPlayerMP subject) {
        if (!CardPlace.inDimension(rule.dimension, subject.dimension)) { return true; }
        if (rule.when != null && !rule.when.passes(subject)) { return true; }
        long now = clock(subject.world);
        String key = rule.key;
        if (CardRule.ONCE_PER_PLAYER.equals(rule.repeat) && CardStorage.firedFor(subject, key)) { return true; }
        if (CardRule.ONCE_PER_WORLD.equals(rule.repeat) && CardStorage.firedInWorld(subject.world, key)) { return true; }
        if (CardRule.ONCE_PER_SESSION.equals(rule.repeat) && SESSION.getOrDefault(subject.getUniqueID(), Collections.emptySet()).contains(key)) { return true; }
        if (rule.cooldown > 0 && CardStorage.firedFor(subject, key) && now - CardStorage.lastFor(subject, key) < (long) rule.cooldown * TICKS_PER_SECOND) { return true; }
        if (CardRule.ONCE_PER_WORLD.equals(rule.repeat)) { CardStorage.stampWorld(subject.world, key, now); }
        if (CardRule.ONCE_PER_SESSION.equals(rule.repeat)) { SESSION.computeIfAbsent(subject.getUniqueID(), k -> new HashSet<>()).add(key); }
        if (rule.cooldown > 0 || CardRule.ONCE_PER_PLAYER.equals(rule.repeat)) { CardStorage.stampFor(subject, key, now); }
        return false;
    }

    private static long clock(World world) {
        World overworld = DimensionManager.getWorld(0);
        return (overworld == null ? world : overworld).getTotalWorldTime();
    }

    private static void runs(CardRule rule, EntityPlayerMP subject) {
        if (!rule.runs.isEmpty()) { Functions.runAs(subject, rule.runs, "Card rule " + rule.key); }
    }

    public static void afterIntro(EntityPlayerMP player) {
        CardScan.startClock(player);
        CardSend.release(player);
    }

    static void forget(UUID player) { SESSION.remove(player); }
}
