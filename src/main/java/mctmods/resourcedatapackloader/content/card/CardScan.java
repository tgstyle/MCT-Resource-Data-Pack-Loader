package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CardScan {
    private static final int EVERY = 20;
    private static final long DAY_TICKS = 24000L;
    private static final long MINUTE_TICKS = 1200L;
    private static final String PLAY_FROM = "rdplPlayFrom";
    private static final Map<UUID, Map<String, Boolean>> INSIDE = new HashMap<>();
    private static final Map<ResourceKey<Level>, Long> CLOCKS = new HashMap<>();
    private static int ticks;

    private CardScan() {}

    public static void onTick(ServerTickEvent.Post event) {
        if (++ticks < EVERY) { return; }
        ticks = 0;
        List<CardRule> scanned = CardRules.scanned();
        boolean timed = CardRules.timed();
        if (scanned.isEmpty() && !timed) { return; }
        MinecraftServer server = event.getServer();
        if (!scanned.isEmpty()) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) { scan(player, scanned); }
        }
        if (timed) { clocks(server); }
    }

    private static void scan(ServerPlayer player, List<CardRule> rules) {
        Map<String, Boolean> inside = INSIDE.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
        boolean reading = ContentIntroPlay.reading(player.getUUID());
        for (CardRule rule : rules) {
            if (reading && CardRule.PLAY_TIME.equals(rule.trigger)) { continue; }
            boolean now = inside(rule, player);
            Boolean before = inside.put(rule.key, now);
            if (now && Boolean.FALSE.equals(before)) { CardFire.fire(rule, player); }
        }
    }

    private static boolean inside(CardRule rule, ServerPlayer player) {
        return switch (rule.trigger) {
            case CardRule.BIOME_ENTER -> CardPlace.biomeMatches(rule.biomes, player.level().getBiome(player.blockPosition()));
            case CardRule.STRUCTURE_ENTER -> CardPlace.inStructure(player, rule.structures, rule.radius);
            case CardRule.Y_LEVEL -> rule.below != null && player.getY() < rule.below || rule.above != null && player.getY() > rule.above;
            case CardRule.PLAY_TIME -> (played(player) - PlayerPersisted.of(player, PLAY_FROM).getInt(PLAY_FROM)) / MINUTE_TICKS >= rule.minutes;
            default -> {
                Integer score = CardPlace.score(player, rule.objective);
                yield score != null && score >= rule.score;
            }
        };
    }

    private static void clocks(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            List<ServerPlayer> here = level.players();
            if (here.isEmpty()) {
                CLOCKS.remove(level.dimension());
                continue;
            }
            long now = level.getDayTime();
            Long before = CLOCKS.put(level.dimension(), now);
            if (before == null || now <= before) { continue; }
            if (now - before <= EVERY * 2L) { crossings(CardRules.on(CardRule.TIME_OF_DAY), here, before, now); }
            long day = now / DAY_TICKS;
            if (day == before / DAY_TICKS) { continue; }
            for (CardRule rule : CardRules.on(CardRule.DAY)) {
                if (rule.day < 0L || rule.day == day) { fireAll(rule, here); }
            }
        }
    }

    private static void crossings(List<CardRule> rules, List<ServerPlayer> here, long before, long now) {
        for (CardRule rule : rules) {
            long next = before - before % DAY_TICKS + rule.time;
            if (next <= before) { next += DAY_TICKS; }
            if (next <= now) { fireAll(rule, here); }
        }
    }

    private static void fireAll(CardRule rule, List<ServerPlayer> here) {
        for (ServerPlayer player : List.copyOf(here)) {
            CardFire.fire(rule, player);
            if (!CardRule.PLAYER.equals(rule.audience)) { return; }
        }
    }

    private static int played(ServerPlayer player) { return player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)); }

    static void startClock(ServerPlayer player) {
        CompoundTag persisted = PlayerPersisted.of(player, PLAY_FROM);
        if (!persisted.contains(PLAY_FROM)) { persisted.putInt(PLAY_FROM, played(player)); }
    }

    static void forget(UUID player) { INSIDE.remove(player); }
}
