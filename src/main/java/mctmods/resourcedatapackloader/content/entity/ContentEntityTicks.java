package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.worldgen.beard.PredictedChunk;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityAITasks;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEntityItem;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Names;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ContentEntityTicks {
    private static int TICKS = 1;
    private static final int VANILLA_THINK = 3;
    private static final List<String> KINDS = Collections.unmodifiableList(Arrays.asList("items", "experience", "projectiles"));
    private static final Map<World, Map<Long, Boolean>> FAR = new HashMap<>();
    private static final Map<World, Long> CHECKED = new HashMap<>();
    private static final int SNAPSHOT = 100;
    private static Set<String> kinds;
    private static Set<String> spared;
    private static long considered;
    private static long slowed;

    private ContentEntityTicks() {}

    public static void reload() {
        kinds = null;
        spared = null;
        considered = 0L;
        slowed = 0L;
        FAR.clear();
        CHECKED.clear();
    }

    @SubscribeEvent public static void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        FAR.remove(event.getWorld());
        CHECKED.remove(event.getWorld());
        PredictedChunk.forget();
    }

    public static int ticks() { return TICKS; }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        TICKS++;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || server.getTickCounter() % SNAPSHOT != 0) { return; }
        double mean = MathHelper.average(server.tickTimeArray) * 1.0E-6D;
        double rate = Math.min(20.0D, 1000.0D / Math.max(50.0D, mean));
        int entities = 0;
        int chunks = 0;
        for (WorldServer world : server.worlds) {
            entities += world.loadedEntityList.size();
            chunks += world.getChunkProvider().getLoadedChunkCount();
        }
        ContentLog.LOGGER.debug(String.format("Every second the server manages %.1f rounds of %.1f ms, holding %d chunk(s) and %d entity/entities. Of %d asked about since the last look, %d were given a slower pace",
                rate, mean, chunks, entities, considered, slowed));
        considered = 0L;
        slowed = 0L;
    }

    public static boolean slowedNow(Entity entity) {
        if (entity == null || entity.world == null || entity.world.isRemote) { return false; }
        if (ContentControl.off(ContentControl.ENTITIES)) { return false; }
        if (!ContentControl.flag(ContentControl.ENTITIES, "slowDistantEntities", Config.entities.slowDistantEntities)) { return false; }
        World world = entity.world;
        int chunkX = entity.chunkCoordX;
        int chunkZ = entity.chunkCoordZ;
        int rate = ContentControl.number(ContentControl.ENTITIES, "slowRate", Config.entities.slowRate);
        if (entity instanceof EntityLiving) {
            boolean thinkSlower = !spared(entity) && rate > 1 && far(world, chunkX, chunkZ);
            think((EntityLiving) entity, thinkSlower ? VANILLA_THINK * rate : VANILLA_THINK);
            considered++;
            if (thinkSlower) { slowed++; }
            return false;
        }
        if (spared(entity) || !kindSlowed(entity)) { return false; }
        considered++;
        if (rate <= 1 || !far(world, chunkX, chunkZ)) { return false; }
        boolean skip = Math.floorMod((long) chunkX * 31L + (long) chunkZ + world.getTotalWorldTime(), rate) != 0L;
        if (skip) { slowed++; }
        return skip;
    }

    private static void think(EntityLiving mob, int rate) {
        ((IEntityAITasks) mob.tasks).rdpl$setTickRate(rate);
        ((IEntityAITasks) mob.targetTasks).rdpl$setTickRate(rate);
    }

    private static boolean far(World world, int chunkX, int chunkZ) {
        int recheck = ContentControl.number(ContentControl.ENTITIES, "slowRecheck", Config.entities.slowRecheck);
        long now = world.getTotalWorldTime() / Math.max(1, recheck);
        Long last = CHECKED.get(world);
        Map<Long, Boolean> known = FAR.get(world);
        if (last == null || last != now || known == null) {
            known = new HashMap<>();
            FAR.put(world, known);
            CHECKED.put(world, now);
        }
        Long key = ChunkPos.asLong(chunkX, chunkZ);
        Boolean answer = known.get(key);
        if (answer != null) { return answer; }
        answer = measure(world, chunkX, chunkZ);
        known.put(key, answer);
        return answer;
    }

    private static boolean measure(World world, int chunkX, int chunkZ) {
        if (world.getPersistentChunks().containsKey(new ChunkPos(chunkX, chunkZ))) { return false; }
        if (world.playerEntities.isEmpty()) { return true; }
        double blocks = ContentControl.number(ContentControl.ENTITIES, "slowDistance", Config.entities.slowDistance);
        double reach = blocks * blocks;
        double middleX = (chunkX << 4) + 8;
        double middleZ = (chunkZ << 4) + 8;
        for (EntityPlayer player : world.playerEntities) {
            double awayX = player.posX - middleX;
            double awayZ = player.posZ - middleZ;
            if (awayX * awayX + awayZ * awayZ <= reach) { return false; }
        }
        return true;
    }

    public static void age(Entity entity) {
        if (entity instanceof EntityItem) {
            IEntityItem item = (IEntityItem) entity;
            if (item.rdpl$getAge() != Short.MIN_VALUE) { item.rdpl$setAge(item.rdpl$getAge() + 1); }
            return;
        }
        if (entity instanceof EntityXPOrb) { ((EntityXPOrb) entity).xpOrbAge++; }
    }

    private static boolean kindSlowed(Entity entity) {
        if (kinds == null) {
            kinds = Names.lower(ContentControl.list(ContentControl.ENTITIES, "slowedKinds", Config.entities.slowedKinds));
            for (String kind : kinds) {
                if (KINDS.contains(kind)) { continue; }
                ContentLog.LOGGER.error("slowedKinds names '{}', which is not one of {}, so nothing is slowed for it. Anything that thinks for itself is already given a slower pace without being named, and machines are never slowed", kind, KINDS);
            }
        }
        if (entity instanceof EntityItem) { return kinds.contains("items"); }
        if (entity instanceof EntityXPOrb) { return kinds.contains("experience"); }
        if (entity instanceof IProjectile) { return kinds.contains("projectiles"); }
        return false;
    }

    private static boolean spared(Entity entity) {
        if (entity.isBeingRidden() || entity.isRiding() || entity.hasCustomName() || entity.isGlowing()) { return true; }
        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            if (living.isNoDespawnRequired() || living.getLeashed() || living.getAttackTarget() != null) { return true; }
        }
        if (entity instanceof EntityLivingBase && !((EntityLivingBase) entity).getActivePotionEffects().isEmpty()) { return true; }
        if (spared == null) { spared = Names.lower(ContentControl.list(ContentControl.ENTITIES, "neverSlowed", Config.entities.neverSlowed)); }
        if (spared.isEmpty()) { return false; }
        ResourceLocation name = EntityList.getKey(entity);
        return name != null && spared.contains(name.toString().toLowerCase(Locale.ROOT));
    }
}
