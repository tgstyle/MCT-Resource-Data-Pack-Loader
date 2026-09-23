package mctmods.resourcedatapackloader.content.raid;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.EventHooks;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ActiveRaid {
    public static final String RAIDER = "rdplRaid";
    private static final int VILLAGE_SEARCH = 32;
    private static final int QUIET_BEFORE_VICTORY = 40;
    private static final int CELEBRATION = 600;
    private static final int STRAY_BEYOND = 16;
    private static final int SPAWN_TRIES = 20;
    private static final int BELL_RISE = 16;
    private static final int LOOK_EVERY = 20;
    private static final int LOADED_AROUND = 10;
    private final RaidDef def;
    private final ServerBossEvent bar;
    private final Set<UUID> raiders = new LinkedHashSet<>();
    private BlockPos center;
    private int wave;
    private int cooldown;
    private long ticksActive;
    private int quiet;
    private int celebration;
    private float waveHealth;
    private Status status = Status.ONGOING;
    @Nullable private List<BlockPos> bells;
    @Nullable private RaidVillage village;

    enum Status { ONGOING, VICTORY, LOSS, STOPPED }

    public ActiveRaid(RaidDef def, BlockPos center) {
        this.def = def;
        this.center = center;
        this.cooldown = def.waveDelay();
        this.bar = new ServerBossEvent(Component.literal(def.name()), def.color(), BossEvent.BossBarOverlay.NOTCHED_10);
    }

    BlockPos center() { return center; }

    boolean stopped() { return status == Status.STOPPED; }

    boolean underway(BlockPos at) { return status == Status.ONGOING && wave > 0 && !raiders.isEmpty() && at.distSqr(center) < (double) def.reach() * def.reach(); }

    void tick(ServerLevel level) {
        if (status == Status.STOPPED) { return; }
        if (status != Status.ONGOING) {
            celebrate(level);
            return;
        }
        boolean loaded = level.isLoaded(center);
        bar.setVisible(loaded);
        if (!loaded) { return; }
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            stop("the world is peaceful");
            return;
        }
        if (village == null || ticksActive % LOOK_EVERY == 0) { village = RaidVillage.nearest(level, center, VILLAGE_SEARCH); }
        if (village == null) {
            if (wave > 0) { end(level, Status.LOSS); }
            else { stop("the village is gone before the first wave"); }
            return;
        }
        center = village.center();
        if (wave > 0 && ticksActive % LOOK_EVERY == 0 && village.villagers(level) == 0) {
            end(level, Status.LOSS);
            return;
        }
        ticksActive++;
        if (def.timeout() > 0 && ticksActive >= def.timeout()) {
            stop("it ran " + def.timeout() + " ticks");
            return;
        }
        List<LivingEntity> alive = alive(level);
        if (alive.isEmpty() && wave < def.waves().size()) {
            if (cooldown > 0) {
                cooldown--;
                bar.setName(Component.literal(def.name()));
                bar.setProgress(Mth.clamp((def.waveDelay() - cooldown) / (float) def.waveDelay(), 0.0F, 1.0F));
                if (cooldown % 20 == 0) { players(level); }
                return;
            }
            if (spawnWave(level)) {
                for (BlockPos bell : bells(level, village)) { ContentRaids.ring(level, bell); }
                cooldown = def.waveDelay();
                alive = alive(level);
            }
            else { return; }
        }
        if (ticksActive % 20 == 0) {
            players(level);
            float health = 0.0F;
            for (LivingEntity one : alive) { health += one.getHealth(); }
            bar.setProgress(waveHealth <= 0.0F ? 0.0F : Mth.clamp(health / waveHealth, 0.0F, 1.0F));
            bar.setName(Component.literal(!alive.isEmpty() && alive.size() <= 2 ? def.name() + " - Raiders Remaining: " + alive.size() : def.name()));
            if (underway(center)) { ContentRaids.hide(level, center, def.reach()); }
        }
        if (wave >= def.waves().size() && alive.isEmpty() && ++quiet >= QUIET_BEFORE_VICTORY) { end(level, Status.VICTORY); }
    }

    private List<LivingEntity> alive(ServerLevel level) {
        List<LivingEntity> found = new ArrayList<>();
        double stray = (double) (def.reach() + STRAY_BEYOND) * (def.reach() + STRAY_BEYOND);
        raiders.removeIf(id -> {
            Entity entity = level.getEntity(id);
            if (!(entity instanceof LivingEntity living) || !entity.isAlive()) { return true; }
            if (center.distToLowCornerSqr(entity.getX(), entity.getY(), entity.getZ()) > stray) {
                entity.getPersistentData().remove(RAIDER);
                return true;
            }
            found.add(living);
            return false;
        });
        return found;
    }

    private boolean spawnWave(ServerLevel level) {
        BlockPos at = null;
        for (int ring = 0; ring < 3 && at == null; ring++) { at = spawnSpot(level, ring); }
        if (at == null) {
            stop("no spot around the village could take wave " + (wave + 1));
            return false;
        }
        waveHealth = 0.0F;
        int made = 0;
        RandomSource random = level.getRandom();
        for (RaidDef.Group group : def.waves().get(wave)) {
            int count = group.count().pick(random);
            EntityType<?> type = EntityType.byString(group.entity()).orElse(null);
            for (int i = 0; i < count; i++) {
                Entity entity = type == null ? null : type.create(level);
                if (entity == null) {
                    ContentLog.LOGGER.error("Raid {} sends {} in wave {}, which nothing registers, so it does not come", def.key(), group.entity(), wave + 1);
                    break;
                }
                entity.moveTo(at.getX() + 0.5D + random.nextInt(3) - 1, at.getY(), at.getZ() + 0.5D + random.nextInt(3) - 1, random.nextFloat() * 360.0F, 0.0F);
                if (entity instanceof Mob mob) {
                    EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(at), MobSpawnType.EVENT, null);
                    mob.setPersistenceRequired();
                }
                entity.getPersistentData().put(RAIDER, position(center));
                level.addFreshEntity(entity);
                raiders.add(entity.getUUID());
                if (entity instanceof LivingEntity living) { waveHealth += living.getMaxHealth(); }
                made++;
            }
        }
        wave++;
        quiet = 0;
        horn(level, at);
        ContentLog.LOGGER.info("Raid {} on the village at {}, {}, {} sends wave {} of {}: {} raider(s) at {}, {}, {}", def.key(), center.getX(), center.getY(), center.getZ(), wave, def.waves().size(), made, at.getX(), at.getY(), at.getZ());
        return true;
    }

    public static CompoundTag position(BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("X", pos.getX());
        tag.putInt("Y", pos.getY());
        tag.putInt("Z", pos.getZ());
        return tag;
    }

    public static BlockPos position(CompoundTag tag) { return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z")); }

    private List<BlockPos> bells(ServerLevel level, RaidVillage around) {
        if (bells != null) { return bells; }
        List<BlockPos> found = new ArrayList<>();
        int reach = around.radius();
        for (BlockPos at : BlockPos.betweenClosed(center.offset(-reach, -BELL_RISE, -reach), center.offset(reach, BELL_RISE, reach))) {
            if (level.isLoaded(at) && ContentRaids.isBell(level.getBlockState(at))) { found.add(at.immutable()); }
        }
        bells = found;
        return found;
    }

    @Nullable private BlockPos spawnSpot(ServerLevel level, int ring) {
        int reach = ring == 0 ? 2 : 2 - ring;
        RandomSource random = level.getRandom();
        for (int i = 0; i < SPAWN_TRIES; i++) {
            float angle = random.nextFloat() * ((float) Math.PI * 2.0F);
            int x = center.getX() + Mth.floor(Mth.cos(angle) * def.spawnDistance() * reach) + random.nextInt(5);
            int z = center.getZ() + Mth.floor(Mth.sin(angle) * def.spawnDistance() * reach) + random.nextInt(5);
            if (unloadedAround(level, x, z)) { continue; }
            BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z));
            if (ring < 2 && RaidVillage.nearest(level, top, 0) != null) { continue; }
            if (!level.getBlockState(top.below()).isFaceSturdy(level, top.below(), Direction.UP) || !level.getFluidState(top).isEmpty()) { continue; }
            return top;
        }
        return null;
    }

    private static boolean unloadedAround(ServerLevel level, int x, int z) {
        for (int offX = -LOADED_AROUND; offX <= LOADED_AROUND; offX += LOADED_AROUND) {
            for (int offZ = -LOADED_AROUND; offZ <= LOADED_AROUND; offZ += LOADED_AROUND) {
                if (!level.isLoaded(new BlockPos(x + offX, 0, z + offZ))) { return true; }
            }
        }
        return false;
    }

    private void horn(ServerLevel level, BlockPos at) {
        if (def.sound().isEmpty()) { return; }
        SoundEvent sound = Registered.find(BuiltInRegistries.SOUND_EVENT, ResourceLocation.tryParse(def.sound()));
        if (sound == null) {
            ContentLog.LOGGER.error("Raid {} names the sound {}, which nothing registers, so the waves come quietly", def.key(), def.sound());
            return;
        }
        for (ServerPlayer player : inReach(level)) { level.playSound(null, player.getX() + (at.getX() - player.getX()) / 13.0D, player.getY(), player.getZ() + (at.getZ() - player.getZ()) / 13.0D, sound, SoundSource.NEUTRAL, 64.0F, 1.0F); }
    }

    private void end(ServerLevel level, Status ending) {
        status = ending;
        celebration = 0;
        List<ServerPlayer> present = inReach(level);
        String function = ending == Status.VICTORY ? def.wins() : def.loses();
        ContentLog.LOGGER.info("Raid {} on the village at {}, {}, {} ends in {} after {} of {} wave(s), with {} player(s) in reach", def.key(), center.getX(), center.getY(), center.getZ(), ending == Status.VICTORY ? "victory" : "defeat", wave, def.waves().size(), present.size());
        if (function.isEmpty()) { return; }
        for (ServerPlayer player : present) { Functions.runAs(player, function, "Raid " + def.key()); }
    }

    private void celebrate(ServerLevel level) {
        if (++celebration >= CELEBRATION) {
            stop(null);
            return;
        }
        if (celebration % 20 != 0) { return; }
        players(level);
        bar.setVisible(true);
        if (status == Status.VICTORY) {
            bar.setProgress(0.0F);
            bar.setName(Component.literal(def.name() + " - Victory"));
        }
        else { bar.setName(Component.literal(def.name() + " - Defeat")); }
    }

    private void players(ServerLevel level) {
        List<ServerPlayer> present = inReach(level);
        for (ServerPlayer watching : new ArrayList<>(bar.getPlayers())) {
            if (!present.contains(watching)) { bar.removePlayer(watching); }
        }
        for (ServerPlayer player : present) { bar.addPlayer(player); }
    }

    private List<ServerPlayer> inReach(ServerLevel level) {
        List<ServerPlayer> present = new ArrayList<>();
        double reach = (double) def.reach() * def.reach();
        for (ServerPlayer player : level.players()) {
            if (player.isAlive() && !player.isSpectator() && center.distToLowCornerSqr(player.getX(), player.getY(), player.getZ()) < reach) { present.add(player); }
        }
        return present;
    }

    void stop(@Nullable String why) {
        if (why != null) { ContentLog.LOGGER.info("Raid {} on the village at {}, {}, {} stops: {}", def.key(), center.getX(), center.getY(), center.getZ(), why); }
        status = Status.STOPPED;
        for (ServerPlayer watching : new ArrayList<>(bar.getPlayers())) { bar.removePlayer(watching); }
    }

    CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Raid", def.key().toString());
        tag.put("Center", position(center));
        tag.putInt("Wave", wave);
        tag.putInt("Cooldown", cooldown);
        tag.putLong("Ticks", ticksActive);
        tag.putInt("Quiet", quiet);
        tag.putInt("Celebration", celebration);
        tag.putFloat("WaveHealth", waveHealth);
        tag.putString("Status", status.name());
        ListTag ids = new ListTag();
        for (UUID id : raiders) { ids.add(NbtUtils.createUUID(id)); }
        tag.put("Raiders", ids);
        return tag;
    }

    @Nullable static ActiveRaid read(CompoundTag tag, @Nullable RaidDef def) {
        if (def == null) {
            ContentLog.LOGGER.warn("A saved raid names {}, which no pack provides any more, so it is dropped", tag.getString("Raid"));
            return null;
        }
        ActiveRaid raid = new ActiveRaid(def, position(tag.getCompound("Center")));
        raid.wave = tag.getInt("Wave");
        raid.cooldown = tag.getInt("Cooldown");
        raid.ticksActive = tag.getLong("Ticks");
        raid.quiet = tag.getInt("Quiet");
        raid.celebration = tag.getInt("Celebration");
        raid.waveHealth = tag.getFloat("WaveHealth");
        try { raid.status = Status.valueOf(tag.getString("Status")); }
        catch (IllegalArgumentException unknown) { raid.status = Status.STOPPED; }
        ListTag ids = tag.getList("Raiders", Tag.TAG_INT_ARRAY);
        for (Tag id : ids) { raid.raiders.add(NbtUtils.loadUUID(id)); }
        return raid;
    }
}
