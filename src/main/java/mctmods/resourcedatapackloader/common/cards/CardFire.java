package mctmods.resourcedatapackloader.common.cards;

import mctmods.resourcedatapackloader.common.util.PlayerLogout;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class CardFire {
    private static final Map<UUID, Set<String>> SESSION = PlayerLogout.track(new HashMap<>());

    private CardFire() {}

    static void fire(CardRule rule, ServerPlayer subject) {
        if (blocked(rule, subject)) { return; }
        CardLook look = CardLook.plain();
        sets(rule, subject);
        CardSend.dress(look, rule, subject);
        for (ServerPlayer player : CardSend.audience(rule, subject)) { CardSend.send(look, player); }
        ContentLog.LOGGER.debug("Card rule {} fired for {}", rule.key, subject.getName().getString());
        runs(rule, subject);
    }

    public static void builtin(String key, ServerPlayer subject, CardLook look) {
        CardRule rule = CardRules.builtin(key);
        if (rule == null || blocked(rule, subject)) { return; }
        present(rule, subject, look, Map.of());
    }

    public static void show(CardRule rule, ServerPlayer subject) { show(rule, subject, Map.of()); }

    public static void show(CardRule rule, ServerPlayer subject, Map<String, String> args) { present(rule, subject, CardLook.plain(), args); }

    private static void present(CardRule rule, ServerPlayer subject, CardLook look, Map<String, String> args) {
        sets(rule, subject);
        CardSend.dress(look, rule, subject, args);
        CardSend.send(look, subject);
        runs(rule, subject);
    }

    static void meter(CardRule rule, ServerPlayer subject, Map<String, String> args) {
        if (!offPlace(rule, subject)) { show(rule, subject, args); }
    }

    private static boolean offPlace(CardRule rule, ServerPlayer subject) {
        if (CardPlace.elsewhere(rule.dimension, subject)) { return true; }
        return rule.when != null && rule.when.fails(subject);
    }

    private static boolean blocked(CardRule rule, ServerPlayer subject) {
        if (offPlace(rule, subject)) { return true; }
        long now = clock(subject.level().getServer());
        String key = rule.key;
        if (CardRule.ONCE_PER_PLAYER.equals(rule.repeat) && CardStorage.firedFor(subject, key)) { return true; }
        if (CardRule.ONCE_PER_WORLD.equals(rule.repeat) && CardStorage.firedInWorld(subject.level().getServer(), key)) { return true; }
        if (CardRule.ONCE_PER_SESSION.equals(rule.repeat) && SESSION.getOrDefault(subject.getUUID(), Set.of()).contains(key)) { return true; }
        if (rule.cooldown > 0 && CardStorage.firedFor(subject, key) && now - CardStorage.lastFor(subject, key) < (long) rule.cooldown * SharedConstants.TICKS_PER_SECOND) { return true; }
        if (CardRule.ONCE_PER_WORLD.equals(rule.repeat)) { CardStorage.stampWorld(subject.level().getServer(), key, now); }
        if (CardRule.ONCE_PER_SESSION.equals(rule.repeat)) { SESSION.computeIfAbsent(subject.getUUID(), _ -> new HashSet<>()).add(key); }
        if (rule.cooldown > 0 || CardRule.ONCE_PER_PLAYER.equals(rule.repeat)) { CardStorage.stampFor(subject, key, now); }
        return false;
    }

    public static long clock(MinecraftServer server) { return server.overworld().getGameTime(); }

    private static void sets(CardRule rule, ServerPlayer subject) { if (!rule.sets.isEmpty()) { rule.sets.apply(subject, "Card rule " + rule.key); } }

    private static void runs(CardRule rule, ServerPlayer subject) { if (!rule.runs.isEmpty()) { Functions.runAs(subject, rule.runs, "Card rule " + rule.key); } }

    public static void afterIntro(ServerPlayer player) {
        CardScan.startClock(player);
        CardSend.release(player);
    }
}
