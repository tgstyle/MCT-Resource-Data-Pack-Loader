package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ContentEntityTicks {
    private static final List<String> KINDS = List.of("items", "experience", "projectiles");
    private static final int SNAPSHOT = 100;
    private static final Map<Level, Long2BooleanOpenHashMap> FAR = new HashMap<>();
    private static final Map<Level, Long> CHECKED = new HashMap<>();
    private static Slowing slowing;
    private static long considered;
    private static long slowed;

    private ContentEntityTicks() {}

    private record Slowing(boolean on, int rate, int recheck, double reach, Set<String> kinds, Set<String> spared) {}

    public static void reload() {
        slowing = null;
        considered = 0L;
        slowed = 0L;
        FAR.clear();
        CHECKED.clear();
    }

    private static Slowing slowing() {
        if (slowing != null) { return slowing; }
        boolean on = !ContentControl.off(ContentControl.ENTITIES) && ContentControl.flag(ContentControl.ENTITIES, "slowDistantEntities", Config.entities.slowDistantEntities());
        int rate = Math.max(1, ContentControl.number(ContentControl.ENTITIES, "slowRate", Config.entities.slowRate()));
        int recheck = Math.max(1, ContentControl.number(ContentControl.ENTITIES, "slowRecheck", Config.entities.slowRecheck()));
        double distance = Math.max(64, ContentControl.number(ContentControl.ENTITIES, "slowDistance", Config.entities.slowDistance()));
        Set<String> kinds = new HashSet<>();
        for (String kind : ContentControl.list(ContentControl.ENTITIES, "slowedKinds", Config.entities.slowedKinds())) {
            String lowered = kind.trim().toLowerCase(Locale.ROOT);
            if (KINDS.contains(lowered)) { kinds.add(lowered); }
            else { ContentLog.LOGGER.error("slowedKinds names '{}', which is not one of {}, so nothing is slowed for it. Anything that thinks for itself is already given a slower pace without being named, and machines are never slowed", kind, KINDS); }
        }
        Set<String> spared = new HashSet<>();
        for (String name : ContentControl.list(ContentControl.ENTITIES, "neverSlowed", Config.entities.neverSlowed())) { spared.add(name.trim().toLowerCase(Locale.ROOT)); }
        slowing = new Slowing(on, rate, recheck, distance * distance, kinds, spared);
        if (on && rate > 1) { ContentLog.LOGGER.debug("Entities more than {} block(s) from every player are given one tick in {}", (int) distance, rate); }
        return slowing;
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof Level level)) { return; }
        FAR.remove(level);
        CHECKED.remove(level);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % SNAPSHOT != 0 || (considered == 0L && slowed == 0L)) { return; }
        double mean = server.getAverageTickTimeNanos() / 1.0E6D;
        double rate = Math.min(20.0D, 1000.0D / Math.max(50.0D, mean));
        int chunks = 0;
        for (ServerLevel level : server.getAllLevels()) { chunks += level.getChunkSource().getLoadedChunksCount(); }
        ContentLog.LOGGER.debug(String.format(Locale.ROOT, "Every second the server manages %.1f rounds of %.1f ms, holding %d chunk(s). Of %d entities asked about since the last look, %d were given a slower pace", rate, mean, chunks, considered, slowed));
        considered = 0L;
        slowed = 0L;
    }

    public static boolean frozen(Entity entity) { return !(entity instanceof Player) && ContentPregen.holdsStill(entity.level()); }

    public static boolean thinksSlower(Mob mob) {
        if (mob.level().isClientSide()) { return false; }
        if (frozen(mob)) { return true; }
        Slowing slowing = slowing();
        if (!slowing.on() || slowing.rate() <= 1 || spared(mob, slowing)) { return false; }
        considered++;
        if (near(mob.level(), mob.chunkPosition(), slowing)) { return false; }
        boolean skip = Math.floorMod(mob.getId() + mob.level().getGameTime(), slowing.rate()) != 0L;
        if (skip) { slowed++; }
        return skip;
    }

    public static boolean slowedNow(Entity entity) {
        if (entity.level().isClientSide()) { return false; }
        if (frozen(entity)) { return true; }
        Slowing slowing = slowing();
        if (!slowing.on() || slowing.rate() <= 1 || !kindSlowed(entity, slowing) || spared(entity, slowing)) { return false; }
        considered++;
        if (near(entity.level(), entity.chunkPosition(), slowing)) { return false; }
        boolean skip = Math.floorMod(entity.getId() + entity.level().getGameTime(), slowing.rate()) != 0L;
        if (skip) { slowed++; }
        return skip;
    }

    private static boolean near(Level level, ChunkPos chunk, Slowing slowing) {
        long now = level.getGameTime() / slowing.recheck();
        Long last = CHECKED.get(level);
        Long2BooleanOpenHashMap known = FAR.get(level);
        if (last == null || last != now || known == null) {
            known = new Long2BooleanOpenHashMap();
            FAR.put(level, known);
            CHECKED.put(level, now);
        }
        long key = chunk.toLong();
        if (known.containsKey(key)) { return known.get(key); }
        boolean answer = !measure(level, chunk, slowing);
        known.put(key, answer);
        return answer;
    }

    private static boolean measure(Level level, ChunkPos chunk, Slowing slowing) {
        if (level instanceof ServerLevel server && server.getForcedChunks().contains(chunk.toLong())) { return false; }
        if (level.players().isEmpty()) { return true; }
        double middleX = chunk.getMiddleBlockX() + 0.5D;
        double middleZ = chunk.getMiddleBlockZ() + 0.5D;
        for (Player player : level.players()) {
            double awayX = player.getX() - middleX;
            double awayZ = player.getZ() - middleZ;
            if (awayX * awayX + awayZ * awayZ <= slowing.reach()) { return false; }
        }
        return true;
    }

    private static boolean kindSlowed(Entity entity, Slowing slowing) {
        if (entity instanceof ItemEntity) { return slowing.kinds().contains("items"); }
        if (entity instanceof ExperienceOrb) { return slowing.kinds().contains("experience"); }
        if (entity instanceof Projectile) { return slowing.kinds().contains("projectiles"); }
        return false;
    }

    private static boolean spared(Entity entity, Slowing slowing) {
        if (entity.isVehicle() || entity.isPassenger() || entity.hasCustomName() || entity.isCurrentlyGlowing()) { return true; }
        if (entity instanceof Mob mob && (mob.isPersistenceRequired() || mob.isLeashed() || mob.getTarget() != null)) { return true; }
        if (entity instanceof LivingEntity living && !living.getActiveEffects().isEmpty()) { return true; }
        if (slowing.spared().isEmpty()) { return false; }
        ResourceLocation name = EntityType.getKey(entity.getType());
        return slowing.spared().contains(name.toString().toLowerCase(Locale.ROOT));
    }
}
