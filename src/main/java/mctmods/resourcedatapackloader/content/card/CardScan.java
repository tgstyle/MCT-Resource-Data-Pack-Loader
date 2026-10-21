package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentTimeWindows;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CardScan {
    private static final int EVERY = 20;
    private static final long MINUTE_TICKS = 1200L;
    private static final String PLAY_FROM = "rdplPlayFrom";
    private static final Map<UUID, Map<String, Boolean>> INSIDE = new HashMap<>();
    private static final Map<Integer, Long> CLOCKS = new HashMap<>();
    private static final Map<Integer, Long> SCALED = new HashMap<>();
    private static int ticks;

    private CardScan() {}

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ++ticks < EVERY) { return; }
        ticks = 0;
        List<CardRule> scanned = CardRules.scanned();
        boolean timed = CardRules.timed();
        if (scanned.isEmpty() && !timed) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        if (!scanned.isEmpty()) {
            for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { scan(player, scanned); }
        }
        if (timed) { clocks(server); }
    }

    private static void scan(EntityPlayerMP player, List<CardRule> rules) {
        Map<String, Boolean> inside = INSIDE.computeIfAbsent(player.getUniqueID(), k -> new HashMap<>());
        boolean reading = ContentIntroPlay.reading(player.getUniqueID());
        for (CardRule rule : rules) {
            if (reading && CardRule.PLAY_TIME.equals(rule.trigger)) { continue; }
            boolean now = inside(rule, player);
            Boolean before = inside.put(rule.key, now);
            if (now && Boolean.FALSE.equals(before)) { CardFire.fire(rule, player); }
        }
    }

    private static boolean inside(CardRule rule, EntityPlayerMP player) {
        switch (rule.trigger) {
            case CardRule.BIOME_ENTER: return CardPlace.biomeMatches(rule.biomes, player.world.getBiome(player.getPosition()));
            case CardRule.STRUCTURE_ENTER: return CardPlace.inStructure(player, rule.structures, rule.radius);
            case CardRule.Y_LEVEL: return rule.below != null && player.posY < rule.below || rule.above != null && player.posY > rule.above;
            case CardRule.PLAY_TIME: return (played(player) - PlayerPersisted.of(player).getInteger(PLAY_FROM)) / MINUTE_TICKS >= rule.minutes;
            default:
                Integer score = Standing.score(player, rule.objective);
                return score != null && score >= rule.score;
        }
    }

    private static void clocks(MinecraftServer server) {
        for (WorldServer world : server.worlds) {
            int dimension = world.provider.getDimension();
            if (world.playerEntities.isEmpty()) {
                CLOCKS.remove(dimension);
                SCALED.remove(dimension);
                continue;
            }
            long now = world.getWorldTime();
            long scaled = ContentTimeWindows.clock(world, ContentDimensions.of(world));
            Long before = CLOCKS.put(dimension, now);
            Long scaledBefore = SCALED.put(dimension, scaled);
            if (before == null || scaledBefore == null || now <= before) { continue; }
            List<EntityPlayerMP> here = new ArrayList<>();
            for (EntityPlayer one : world.playerEntities) {
                if (one instanceof EntityPlayerMP) { here.add((EntityPlayerMP) one); }
            }
            if (now - before <= EVERY * 2L) { crossings(CardRules.on(CardRule.TIME_OF_DAY), here, scaledBefore, scaled); }
            long day = now / DimensionTraitsDef.VANILLA_DAY;
            if (day == before / DimensionTraitsDef.VANILLA_DAY) { continue; }
            for (CardRule rule : CardRules.on(CardRule.DAY)) {
                if (rule.day < 0L || rule.day == day) { fireAll(rule, here); }
            }
        }
    }

    private static void crossings(List<CardRule> rules, List<EntityPlayerMP> here, long before, long now) {
        for (CardRule rule : rules) {
            long next = before - before % DimensionTraitsDef.VANILLA_DAY + rule.time;
            if (next <= before) { next += DimensionTraitsDef.VANILLA_DAY; }
            if (next <= now) { fireAll(rule, here); }
        }
    }

    private static void fireAll(CardRule rule, List<EntityPlayerMP> here) {
        for (EntityPlayerMP player : here) {
            CardFire.fire(rule, player);
            if (!CardRule.PLAYER.equals(rule.audience)) { return; }
        }
    }

    private static int played(EntityPlayerMP player) { return player.getStatFile().readStat(StatList.PLAY_ONE_MINUTE); }

    static void startClock(EntityPlayerMP player) {
        NBTTagCompound persisted = PlayerPersisted.of(player);
        if (!persisted.hasKey(PLAY_FROM)) { persisted.setInteger(PLAY_FROM, played(player)); }
    }

    static void forget(UUID player) { INSIDE.remove(player); }
}
