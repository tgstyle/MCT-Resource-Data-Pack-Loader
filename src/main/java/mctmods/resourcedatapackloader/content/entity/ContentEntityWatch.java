package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.MinecraftServer;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import javax.annotation.Nullable;

final class ContentEntityWatch {
    private static final Long2LongOpenHashMap WAS_AT = new Long2LongOpenHashMap();
    private static final Long2LongOpenHashMap TICKED = new Long2LongOpenHashMap();
    private static final Long2LongOpenHashMap GAPS = new Long2LongOpenHashMap();
    private static final Long2LongOpenHashMap FACED = new Long2LongOpenHashMap();
    private static final Long2LongOpenHashMap HEALTHS = new Long2LongOpenHashMap();

    private ContentEntityWatch() {}

    static void tally(@Nullable MinecraftServer server) {
        if (server == null) { return; }
        for (ServerLevel level : server.getAllLevels()) {
            StringBuilder where = new StringBuilder();
            for (net.minecraft.server.level.ServerPlayer player : level.players()) { where.append(' ').append(player.getGameProfile().getName()).append('@').append(player.chunkPosition()); }
            ContentLog.LOGGER.debug("{} holds {} chunk(s) at entity-ticking status, spawn chunk {}, players:{}", level.dimension().location(), level.getChunkSource().getTickingGenerated(), new ChunkPos(level.getSharedSpawnPos()), where.isEmpty() ? " none" : where);
        }
        int mobs = 0;
        int aimed = 0;
        int pathless = 0;
        int reaching = 0;
        int slowed = 0;
        int still = 0;
        int walked = 0;
        int ticking = 0;
        int closing = 0;
        int milling = 0;
        int inRange = 0;
        int inTickingChunk = 0;
        int spun = 0;
        int hurt = 0;
        java.util.List<Mob> here = new java.util.ArrayList<>();
        it.unimi.dsi.fastutil.longs.LongOpenHashSet seen = new it.unimi.dsi.fastutil.longs.LongOpenHashSet();
        long away = 0L;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof Mob mob) || !ContentEntities.BY_TYPE.containsKey(mob.getType())) { continue; }
                mobs++;
                seen.add(mob.getId());
                here.add(mob);
                long hp = HEALTHS.put(mob.getId(), (long) (mob.getHealth() * 100.0F));
                if (hp != HEALTHS.defaultReturnValue() && hp > (long) (mob.getHealth() * 100.0F)) { hurt++; }
                long was = WAS_AT.put(mob.getId(), mob.blockPosition().asLong());
                if (was != WAS_AT.defaultReturnValue() && was != mob.blockPosition().asLong()) { walked++; }
                long faced = FACED.put(mob.getId(), (long) mob.getYRot());
                if (was != WAS_AT.defaultReturnValue() && was == mob.blockPosition().asLong() && faced != FACED.defaultReturnValue() && Math.abs(net.minecraft.util.Mth.wrapDegrees(mob.getYRot() - faced)) >= 90.0F) { spun++; }
                long ticked = TICKED.put(mob.getId(), mob.tickCount);
                if (ticked != TICKED.defaultReturnValue() && ticked != mob.tickCount) { ticking++; }
                if (level.getChunkSource().chunkMap.getDistanceManager().inEntityTickingRange(mob.chunkPosition().toLong())) { inRange++; }
                LevelChunk stood = level.getChunkSource().getChunkNow(mob.chunkPosition().x, mob.chunkPosition().z);
                if (stood != null && stood.getFullStatus().isOrAfter(FullChunkStatus.ENTITY_TICKING)) { inTickingChunk++; }
                LivingEntity aim = mob.getTarget();
                if (aim == null) { continue; }
                aimed++;
                double gap = Math.sqrt(mob.distanceToSqr(aim));
                away += (long) gap;
                long before = GAPS.put(mob.getId(), (long) (gap * 100.0D));
                if (before != GAPS.defaultReturnValue()) {
                    if (before - (long) (gap * 100.0D) > 100L) { closing++; }
                    else if (Math.abs(before - (long) (gap * 100.0D)) <= 100L) { milling++; }
                }
                if (mob.getNavigation().isDone()) { pathless++; }
                if (ContentEntityTicks.thinksSlower(mob)) { slowed++; }
                if (mob.isNoAi()) { still++; }
                if (gap <= mob.getBbWidth() * 2.0F + aim.getBbWidth()) { reaching++; }
            }
        }
        int gone = 0;
        for (it.unimi.dsi.fastutil.longs.LongIterator held = WAS_AT.keySet().iterator(); held.hasNext();) {
            long id = held.nextLong();
            if (seen.contains(id)) { continue; }
            held.remove();
            FACED.remove(id);
            HEALTHS.remove(id);
            TICKED.remove(id);
            GAPS.remove(id);
            gone++;
        }
        int thickest = 0;
        double spread = 0.0D;
        if (!here.isEmpty()) {
            double middleX = 0.0D;
            double middleZ = 0.0D;
            for (Mob one : here) { middleX += one.getX(); middleZ += one.getZ(); }
            middleX /= here.size();
            middleZ /= here.size();
            for (Mob one : here) { spread += Math.sqrt((one.getX() - middleX) * (one.getX() - middleX) + (one.getZ() - middleZ) * (one.getZ() - middleZ)); }
            spread /= here.size();
            for (Mob one : here) {
                int near = 0;
                for (Mob other : here) { if (one.distanceToSqr(other) <= 64.0D) { near++; } }
                thickest = Math.max(thickest, near);
            }
        }
        if (!here.isEmpty()) {
            double leastX = Double.MAX_VALUE;
            double mostX = -Double.MAX_VALUE;
            double leastZ = Double.MAX_VALUE;
            double mostZ = -Double.MAX_VALUE;
            for (Mob one : here) {
                leastX = Math.min(leastX, one.getX());
                mostX = Math.max(mostX, one.getX());
                leastZ = Math.min(leastZ, one.getZ());
                mostZ = Math.max(mostZ, one.getZ());
            }
            int[][] cells = new int[11][11];
            double wideX = Math.max(1.0D, mostX - leastX);
            double wideZ = Math.max(1.0D, mostZ - leastZ);
            for (Mob one : here) {
                int col = Math.min(10, (int) ((one.getX() - leastX) / wideX * 11.0D));
                int row = Math.min(10, (int) ((one.getZ() - leastZ) / wideZ * 11.0D));
                cells[row][col]++;
            }
            StringBuilder drawn = new StringBuilder();
            for (int[] row : cells) {
                if (!drawn.isEmpty()) { drawn.append('/'); }
                for (int count : row) { drawn.append(count == 0 ? "." : count > 9 ? "+" : Character.forDigit(count, 10)); }
            }
            ContentLog.LOGGER.debug("Where they stand, {} mob(s) over x {} to {} and z {} to {}, eleven cells each way: {}",
                    here.size(), (int) leastX, (int) mostX, (int) leastZ, (int) mostZ, drawn);
        }
        if (!ContentEntities.FELL_TO.isEmpty()) {
            ContentLog.LOGGER.debug("What has killed pack mobs so far: {}", ContentEntities.FELL_TO);
        }
        if (mobs == 0) { return; }
        ContentLog.LOGGER.debug("Of {} pack mob(s), {} hold a target, {} of those have no path to walk to it, {} stand close enough to strike, and a target is {} block(s) off on average; {} of {} still ticking moved since the last look, {} closed on their target and {} held the same distance, {} stand in entity-ticking range and {} in a chunk at entity-ticking status, {} are gone since the last look and {} turned 90 degrees or more without leaving their block, {} lost health since the last look, the thickest crowd holds {} within 8 blocks and a mob stands {} block(s) from the middle of them all on average, {} were given a slower pace this tick and {} have no AI at all",
                mobs, aimed, pathless, reaching, aimed == 0 ? 0L : away / aimed, walked, ticking, closing, milling, inRange, inTickingChunk, gone, spun, hurt, thickest, String.format(java.util.Locale.ROOT, "%.1f", spread), slowed, still);
    }
}
