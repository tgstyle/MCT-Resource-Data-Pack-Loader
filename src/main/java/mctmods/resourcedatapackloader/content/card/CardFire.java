package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;

import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class CardFire {
    private static final int TICKS_PER_SECOND = 20;
    private static final Map<UUID, Set<String>> SESSION = new HashMap<>();

    private CardFire() {}

    static void fire(CardRule rule, ServerPlayer subject) {
        if (blocked(rule, subject)) { return; }
        CardLook look = CardLook.plain();
        CardSend.dress(look, rule, subject);
        for (ServerPlayer player : CardSend.audience(rule, subject)) { CardSend.send(look, player); }
        ContentLog.LOGGER.debug("Card rule {} fired for {}", rule.key, subject.getName().getString());
        runs(rule, subject);
    }

    public static void builtin(String key, ServerPlayer subject, CardLook look) {
        CardRule rule = CardRules.builtin(key);
        if (rule == null || blocked(rule, subject)) { return; }
        CardSend.dress(look, rule, subject);
        CardSend.send(look, subject);
        runs(rule, subject);
    }

    public static void show(CardRule rule, ServerPlayer subject) {
        CardLook look = CardLook.plain();
        CardSend.dress(look, rule, subject);
        CardSend.send(look, subject);
        runs(rule, subject);
    }

    private static boolean blocked(CardRule rule, ServerPlayer subject) {
        if (!CardPlace.inDimension(rule.dimension, subject)) { return true; }
        if (rule.when != null && !rule.when.passes(subject)) { return true; }
        long now = subject.server.overworld().getGameTime();
        String key = rule.key;
        if (CardRule.ONCE_PER_PLAYER.equals(rule.repeat) && CardStorage.firedFor(subject, key)) { return true; }
        if (CardRule.ONCE_PER_WORLD.equals(rule.repeat) && CardStorage.firedInWorld(subject.server, key)) { return true; }
        if (CardRule.ONCE_PER_SESSION.equals(rule.repeat) && SESSION.getOrDefault(subject.getUUID(), Set.of()).contains(key)) { return true; }
        if (rule.cooldown > 0 && CardStorage.firedFor(subject, key) && now - CardStorage.lastFor(subject, key) < (long) rule.cooldown * TICKS_PER_SECOND) { return true; }
        if (CardRule.ONCE_PER_WORLD.equals(rule.repeat)) { CardStorage.stampWorld(subject.server, key, now); }
        if (CardRule.ONCE_PER_SESSION.equals(rule.repeat)) { SESSION.computeIfAbsent(subject.getUUID(), k -> new HashSet<>()).add(key); }
        if (rule.cooldown > 0 || CardRule.ONCE_PER_PLAYER.equals(rule.repeat)) { CardStorage.stampFor(subject, key, now); }
        return false;
    }

    private static void runs(CardRule rule, ServerPlayer subject) {
        if (!rule.runs.isEmpty()) { Functions.runAs(subject, rule.runs, "Card rule " + rule.key); }
    }

    public static void afterIntro(ServerPlayer player) {
        CardScan.startClock(player);
        CardSend.release(player);
    }

    static void forget(UUID player) { SESSION.remove(player); }
}
