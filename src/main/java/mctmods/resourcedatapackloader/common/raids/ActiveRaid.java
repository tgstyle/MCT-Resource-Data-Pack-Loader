package mctmods.resourcedatapackloader.common.raids;

import mctmods.resourcedatapackloader.common.cards.CardEvents;
import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.BaseDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidDef;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.RaidSwitchDef;
import mctmods.resourcedatapackloader.common.raids.base.BaseLedger;
import mctmods.resourcedatapackloader.common.raids.base.RaidOwner;
import mctmods.resourcedatapackloader.common.rules.ContentRaidSwitch;
import mctmods.resourcedatapackloader.common.scoring.ContentScoring;
import mctmods.resourcedatapackloader.common.sounds.ContentSounds;
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.loot.Rewards;
import mctmods.resourcedatapackloader.common.util.messages.BossBars;
import mctmods.resourcedatapackloader.common.util.world.PosTags;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.ForgeEventFactory;

public final class ActiveRaid {
    public static final String RAIDER = "rdplRaid";
    public static final String DOOR = "rdplRaidDoor";
    static final int SUPERVISE = 20;
    private static final int VILLAGE_SEARCH = 32;
    private static final int QUIET_BEFORE_VICTORY = 40;
    private static final int CELEBRATION = 600;
    private static final int STRAY_BEYOND = 16;
    private static final int SPAWN_TRIES = 20;
    private static final int BELL_RISE = 16;
    private static final int LOOK_EVERY = 20;
    private static final int LOADED_AROUND = 10;
    private static final int FOLLOW_STEP = 8;
    private static final int RECHECK = 200;
    private final RaidDef def;
    private final ServerBossEvent bar;
    private final Set<UUID> raiders = new LinkedHashSet<>();
    private final RaidTarget.Kind kind;
    @Nullable private final BaseDef base;
    @Nullable private final UUID targetId;
    private final int waves;
    private final int points;
    private final int score;
    private BlockPos center;
    private int radius;
    private int owner;
    private int wave;
    private int cooldown;
    private long ticksActive;
    private int quiet;
    private int celebration;
    private float waveHealth;
    private long paused = -1;
    private int lost;
    private boolean anchored;
    private boolean resolved;
    private Status status = Status.ONGOING;
    @Nullable private RaidVillage village;
    @Nullable private List<BlockPos> bells;

    enum Status { ONGOING, VICTORY, LOSS, STOPPED }

    private ActiveRaid(RaidDef def, RaidTarget target, int waves, int points) {
        this.def = def;
        this.kind = target.kind();
        this.base = guarded(kind) ? def.base() : null;
        this.targetId = target.targetUuid();
        this.waves = waves;
        this.points = points;
        this.score = target.score();
        this.center = target.center();
        this.radius = target.radius();
        this.owner = target.ownerHandle();
        this.cooldown = def.waveDelay();
        this.bar = def.bossBar() == null ? BossBars.create(Component.literal(def.name()), def.color(), BossEvent.BossBarOverlay.NOTCHED_10) : BossBars.create(def.bossBar(), Component.literal(def.name()));
    }

    public static void start(ServerLevel level, RaidDef def, BlockPos center, boolean free) { start(level, def, new RaidTarget(free ? RaidTarget.Kind.FREE : RaidTarget.Kind.VILLAGE, center, VILLAGE_SEARCH, 0, null, free, 0)); }

    public static void start(ServerLevel level, RaidDef def, RaidTarget target) {
        BlockPos center = target.center();
        RaidSwitchDef change = ContentRaidSwitch.at(level, center);
        int scaled = change.waves(def.waves().size());
        BaseDef base = guarded(target.kind()) ? def.base() : null;
        if (base != null && base.fullScore() > 0) { scaled = Math.max(1, (int) Math.round(scaled * Math.min(1.0D, target.score() / base.fullScore()))); }
        ActiveRaid raid = new ActiveRaid(def, target, scaled, change.points(def.points()));
        RaidStorage.get(level).add(raid);
        ContentRaidSwitch.started(level, center, def.reach());
        ContentLog.LOGGER.info("Raid {} begins {} {}, {}, {} with {} wave(s) for {} point(s)", def.key(), raid.kind == RaidTarget.Kind.FREE ? "with no village at" : "on " + raid.site() + " at", center.getX(), center.getY(), center.getZ(), raid.waves, raid.points);
    }

    private static boolean guarded(RaidTarget.Kind kind) { return kind == RaidTarget.Kind.BASE || kind == RaidTarget.Kind.PLAYER; }

    private String site() { return kind == RaidTarget.Kind.BASE ? "the base" : kind == RaidTarget.Kind.PLAYER ? "the player" : "the village"; }

    BlockPos center() { return center; }

    public int owner() { return owner; }

    public RaidTarget.Kind kind() { return kind; }

    @Nullable public UUID target() { return targetId; }

    public RaidDef def() { return def; }

    boolean stopped() { return status == Status.STOPPED; }

    boolean underway(BlockPos at) { return status == Status.ONGOING && wave > 0 && !raiders.isEmpty() && at.distSqr(center) < (double) reach() * reach(); }

    void supervise(ServerLevel level) {
        if (base == null) { return; }
        owner = RaidOwner.of(level.getServer(), owner);
        resolved = true;
        if (status != Status.ONGOING) { return; }
        if (owner == 0) {
            stop(level, null);
            return;
        }
        if (RaidDefenders.online(level, owner, targetId)) { paused = -1; }
        else if (!"continue".equals(base.onLogoff())) {
            if (paused < 0) { paused = level.getGameTime(); }
            if ("end".equals(base.onLogoff()) || level.getGameTime() - paused >= base.logoffGrace()) { stop(level, "the owner left"); }
            return;
        }
        if (kind == RaidTarget.Kind.PLAYER && base.follow()) { follow(level); }
        if (wave < 1 || !"defenders".equals(base.loss())) { return; }
        if (RaidDefenders.count(level, center, reach(), radius, owner, targetId) > 0) {
            lost = 0;
            return;
        }
        lost += SUPERVISE;
        if (lost >= base.loseGrace()) { end(level, Status.LOSS); }
    }

    private void follow(ServerLevel level) {
        if (anchored) { return; }
        if (level.getGameTime() % RECHECK == 0 && RaidStorage.get(level).crowds(this, def.reach())) {
            anchored = true;
            return;
        }
        Player player = followed(level);
        if (player == null || center.distToLowCornerSqr(player.getX(), player.getY(), player.getZ()) <= FOLLOW_STEP * FOLLOW_STEP) { return; }
        center = player.blockPosition();
        bells = null;
        for (UUID id : raiders) {
            Entity entity = level.getEntity(id);
            if (entity != null) { entity.getPersistentData().put(RAIDER, PosTags.write(center)); }
        }
    }

    @Nullable private Player followed(ServerLevel level) { return base != null && base.follow() && kind == RaidTarget.Kind.PLAYER && targetId != null ? level.getPlayerByUUID(targetId) : null; }

    private int reach() { return base == null ? def.reach() : Math.max(def.reach(), radius); }

    private double stray() {
        int span = (base == null ? def.reach() : Math.max(def.reach(), radius + 2 * def.spawnDistance() + 5 + FOLLOW_STEP)) + STRAY_BEYOND;
        return (double) span * span;
    }

    private void release(ServerLevel level) {
        for (UUID id : raiders) {
            Entity entity = level.getEntity(id);
            if (entity == null) { continue; }
            entity.getPersistentData().remove(RAIDER);
        }
        raiders.clear();
        if (base != null) { BaseLedger.get(level).markRaided(level.getServer(), owner, level.getGameTime()); }
    }

    void tick(ServerLevel level) {
        if (status == Status.STOPPED) { return; }
        if (base != null && !resolved) { return; }
        if (status != Status.ONGOING) {
            celebrate(level);
            return;
        }
        boolean loaded = level.isLoaded(center);
        bar.setVisible(loaded);
        if (!loaded) { return; }
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            stop(level, "the world is peaceful");
            return;
        }
        if (kind == RaidTarget.Kind.VILLAGE) {
            if (village == null || ticksActive % LOOK_EVERY == 0) { village = RaidVillage.nearest(level, center, VILLAGE_SEARCH); }
            if (village == null) {
                if (wave > 0) { end(level, Status.LOSS); }
                else { stop(level, "the village is gone before the first wave"); }
                return;
            }
            center = village.center();
            radius = village.radius();
            if (wave > 0 && ticksActive % LOOK_EVERY == 0 && village.villagers(level) == 0) {
                end(level, Status.LOSS);
                return;
            }
        }
        if (paused >= 0) { return; }
        ticksActive++;
        if (def.timeout() > 0 && ticksActive >= def.timeout()) {
            stop(level, "it ran " + def.timeout() + " ticks");
            return;
        }
        List<LivingEntity> alive = alive(level);
        if (alive.isEmpty() && wave < waves) {
            if (cooldown > 0) {
                cooldown--;
                bar.setName(Component.literal(def.title()));
                bar.setProgress(Mth.clamp((def.waveDelay() - cooldown) / (float) def.waveDelay(), 0.0F, 1.0F));
                if (cooldown % 20 == 0) { players(level); }
                return;
            }
            if (spawnWave(level)) {
                for (BlockPos bell : bells(level)) { ContentRaids.ring(level, bell); }
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
            bar.setName(Component.literal(!alive.isEmpty() && alive.size() <= 2 ? def.title() + " - Raiders Remaining: " + alive.size() : def.title()));
            if (underway(center)) { ContentRaids.hide(level, center, def.reach()); }
        }
        if (wave >= waves && alive.isEmpty() && ++quiet >= QUIET_BEFORE_VICTORY) { end(level, Status.VICTORY); }
    }

    private List<LivingEntity> alive(ServerLevel level) {
        List<LivingEntity> found = new ArrayList<>();
        double stray = stray();
        raiders.removeIf(id -> {
            Entity entity = level.getEntity(id);
            if (entity == null) { return paused < 0; }
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
        BlockPos origin = origin(level);
        int rings = base == null || base.insideSpawn() ? 3 : 2;
        BlockPos at = null;
        for (int ring = 0; ring < rings && at == null; ring++) { at = spawnSpot(level, origin, ring); }
        if (at == null) {
            stop(level, "no spot around " + site() + " could take wave " + (wave + 1));
            return false;
        }
        waveHealth = 0.0F;
        int made = 0;
        RandomSource random = level.getRandom();
        for (RaidDef.Group group : def.waves().get(Math.min(wave, def.waves().size() - 1))) {
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
                    ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(at), MobSpawnType.EVENT, null, null);
                    mob.setPersistenceRequired();
                }
                entity.getPersistentData().put(RAIDER, PosTags.write(center));
                level.addFreshEntity(entity);
                raiders.add(entity.getUUID());
                if (entity instanceof LivingEntity living) { waveHealth += living.getMaxHealth(); }
                made++;
            }
        }
        wave++;
        quiet = 0;
        horn(level, at);
        ContentLog.LOGGER.info("Raid {} on {} at {}, {}, {} sends wave {} of {}: {} raider(s) at {}, {}, {}", def.key(), site(), center.getX(), center.getY(), center.getZ(), wave, waves, made, at.getX(), at.getY(), at.getZ());
        return true;
    }

    private List<BlockPos> bells(ServerLevel level) {
        if (bells != null) { return bells; }
        List<BlockPos> found = new ArrayList<>();
        int reach = radius;
        for (BlockPos at : BlockPos.betweenClosed(center.offset(-reach, -BELL_RISE, -reach), center.offset(reach, BELL_RISE, reach))) {
            if (level.isLoaded(at) && ContentRaids.isBell(level.getBlockState(at))) { found.add(at.immutable()); }
        }
        bells = found;
        return found;
    }

    private BlockPos origin(ServerLevel level) {
        Player player = followed(level);
        return player == null ? center : player.blockPosition();
    }

    @Nullable private BlockPos spawnSpot(ServerLevel level, BlockPos origin, int ring) {
        int distance = base == null ? def.spawnDistance() * (ring == 0 ? 2 : 2 - ring) : radius + def.spawnDistance() * (2 - ring);
        RandomSource random = level.getRandom();
        for (int i = 0; i < SPAWN_TRIES; i++) {
            float angle = random.nextFloat() * ((float) Math.PI * 2.0F);
            int x = origin.getX() + Mth.floor(Mth.cos(angle) * distance) + random.nextInt(5);
            int z = origin.getZ() + Mth.floor(Mth.sin(angle) * distance) + random.nextInt(5);
            if (unloadedAround(level, x, z)) { continue; }
            BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z));
            if (ring < 2 && (base == null ? RaidTargets.lived(level, top) != null : RaidOwner.same(level.getServer(), ClaimGuard.covers(level, top), owner))) { continue; }
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
        SoundEvent sound = ContentSounds.find(def.sound());
        if (sound == null) {
            ContentLog.LOGGER.error("Raid {} names the sound {}, which nothing registers, so the waves come quietly", def.key(), def.sound());
            return;
        }
        for (ServerPlayer player : inReach(level)) { level.playSound(null, player.getX() + (at.getX() - player.getX()) / 13.0D, player.getY(), player.getZ() + (at.getZ() - player.getZ()) / 13.0D, sound, SoundSource.NEUTRAL, 64.0F, 1.0F); }
    }

    private void end(ServerLevel level, Status ending) {
        release(level);
        status = ending;
        celebration = 0;
        List<ServerPlayer> present = inReach(level);
        List<ServerPlayer> paid = base == null || base.rewardAll() ? present : RaidDefenders.inReach(level, center, reach(), owner, targetId);
        if (base != null && ending != Status.VICTORY) {
            paid = new ArrayList<>(paid);
            for (ServerPlayer player : RaidDefenders.inWorld(level, owner, targetId)) {
                if (!paid.contains(player)) { paid.add(player); }
            }
            for (ServerPlayer player : paid) {
                if (!present.contains(player)) { CardEvents.raidEnded(player, def.key().toString(), false); }
            }
        }
        String function = ending == Status.VICTORY ? def.wins() : def.loses();
        ContentLog.LOGGER.info("Raid {} on {} at {}, {}, {} ends in {} after {} of {} wave(s), with {} player(s) in reach", def.key(), site(), center.getX(), center.getY(), center.getZ(), ending == Status.VICTORY ? "victory" : "defeat", wave, waves, present.size());
        if (ending == Status.VICTORY) {
            for (ServerPlayer player : paid) { reward(level, player); }
        }
        for (ServerPlayer player : present) { CardEvents.raidEnded(player, def.key().toString(), ending == Status.VICTORY); }
        if (function.isEmpty()) { return; }
        for (ServerPlayer player : paid) { Functions.runAs(player, function, "Raid " + def.key()); }
    }

    private void reward(ServerLevel level, ServerPlayer player) {
        ContentScoring.raidWon(player, points);
        Rewards.loot(level, player, def.loot(), "Raid " + def.key());
    }

    private void celebrate(ServerLevel level) {
        if (++celebration >= CELEBRATION) {
            stop(level, null);
            return;
        }
        if (celebration % 20 != 0) { return; }
        players(level);
        bar.setVisible(true);
        if (status == Status.VICTORY) {
            bar.setProgress(0.0F);
            bar.setName(Component.literal(def.title() + " - Victory"));
        }
        else { bar.setName(Component.literal(def.title() + " - Defeat")); }
    }

    private void players(ServerLevel level) { BossBars.sync(bar, inReach(level)); }

    private List<ServerPlayer> inReach(ServerLevel level) { return BossBars.near(level, center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D, reach()); }

    private void stop(ServerLevel level, @Nullable String why) {
        if (why != null) { ContentLog.LOGGER.info("Raid {} on {} at {}, {}, {} stops: {}", def.key(), site(), center.getX(), center.getY(), center.getZ(), why); }
        if (status == Status.ONGOING) { release(level); }
        status = Status.STOPPED;
        BossBars.clear(bar);
    }

    CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Raid", def.key().toString());
        tag.put("Center", PosTags.write(center));
        tag.putInt("Waves", waves);
        tag.putInt("Points", points);
        tag.putInt("Wave", wave);
        tag.putInt("Cooldown", cooldown);
        tag.putLong("Ticks", ticksActive);
        tag.putInt("Quiet", quiet);
        tag.putInt("Celebration", celebration);
        tag.putFloat("WaveHealth", waveHealth);
        tag.putString("Status", status.name());
        tag.putString("Kind", kind.key());
        tag.putInt("Owner", owner);
        if (targetId != null) { tag.putString("Target", targetId.toString()); }
        tag.putInt("Radius", radius);
        tag.putInt("Score", score);
        if (paused >= 0) { tag.putLong("Paused", paused); }
        tag.putInt("Lost", lost);
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
        RaidTarget.Kind kind = tag.contains("Kind") ? RaidTarget.Kind.parse(tag.getString("Kind")) : tag.getBoolean("Free") ? RaidTarget.Kind.FREE : RaidTarget.Kind.VILLAGE;
        UUID target = tag.contains("Target") ? UUID.fromString(tag.getString("Target")) : null;
        RaidTarget saved = new RaidTarget(kind, PosTags.read(tag.getCompound("Center")), tag.contains("Radius") ? tag.getInt("Radius") : VILLAGE_SEARCH, tag.getInt("Owner"), target, kind == RaidTarget.Kind.FREE, tag.getInt("Score"));
        ActiveRaid raid = new ActiveRaid(def, saved, tag.contains("Waves") ? tag.getInt("Waves") : def.waves().size(), tag.contains("Points") ? tag.getInt("Points") : def.points());
        raid.wave = tag.getInt("Wave");
        raid.cooldown = tag.getInt("Cooldown");
        raid.ticksActive = tag.getLong("Ticks");
        raid.quiet = tag.getInt("Quiet");
        raid.celebration = tag.getInt("Celebration");
        raid.waveHealth = tag.getFloat("WaveHealth");
        raid.paused = tag.contains("Paused") ? tag.getLong("Paused") : -1;
        raid.lost = tag.getInt("Lost");
        try { raid.status = Status.valueOf(tag.getString("Status")); }
        catch (IllegalArgumentException unknown) { raid.status = Status.STOPPED; }
        ListTag ids = tag.getList("Raiders", Tag.TAG_INT_ARRAY);
        for (Tag id : ids) { raid.raiders.add(NbtUtils.loadUUID(id)); }
        return raid;
    }
}
