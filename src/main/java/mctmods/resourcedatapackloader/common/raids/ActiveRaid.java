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
import mctmods.resourcedatapackloader.common.util.entity.Functions;
import mctmods.resourcedatapackloader.common.util.entity.Spawns;
import mctmods.resourcedatapackloader.common.util.loot.Rewards;
import mctmods.resourcedatapackloader.common.util.messages.BossBars;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ActiveRaid {
    private static final String RAIDER = "rdplRaid";
    public static final String DOOR = "rdplRaidDoor";
    static final String DEF_KEY = "Raid";
    static final int SUPERVISE = 20;
    private static final int VILLAGE_SEARCH = 32;
    private static final int QUIET_BEFORE_VICTORY = 40;
    private static final int CELEBRATION = 600;
    private static final int STRAY_BEYOND = 16;
    private static final int SPAWN_TRIES = 20;
    private static final int BELL_RISE = 16;
    private static final int FOLLOW_STEP = 8;
    private static final int RECHECK = 200;
    private final RaidDef def;
    private final BossInfoServer bar;
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
    @Nullable private List<BlockPos> bells;

    enum Status { ONGOING, VICTORY, LOSS, STOPPED }

    private ActiveRaid(RaidDef def, RaidTarget target, int waves, int points) {
        this.def = def;
        this.kind = target.kind();
        this.base = baseFor(def, kind);
        this.targetId = target.targetUuid();
        this.waves = waves;
        this.points = points;
        this.score = target.score();
        this.center = target.center();
        this.radius = target.radius();
        this.owner = target.ownerHandle();
        this.cooldown = def.waveDelay;
        this.bar = def.bossBar == null ? BossBars.create(new TextComponentString(def.name), def.color, BossInfo.Overlay.NOTCHED_10) : BossBars.create(def.bossBar, new TextComponentString(def.name));
    }

    public static boolean isRaider(Entity entity) { return entity.getEntityData().hasKey(RAIDER); }

    public static void tagRaider(Entity entity, BlockPos center) { entity.getEntityData().setTag(RAIDER, NBTUtil.createPosTag(center)); }

    public static void untagRaider(Entity entity) { entity.getEntityData().removeTag(RAIDER); }

    @Nullable public static BlockPos home(Entity entity) {
        NBTTagCompound data = entity.getEntityData();
        return data.hasKey(RAIDER) ? NBTUtil.getPosFromTag(data.getCompoundTag(RAIDER)) : null;
    }

    @Nullable public static ActiveRaid of(Entity entity) {
        BlockPos home = entity.world instanceof WorldServer ? home(entity) : null;
        return home == null ? null : RaidStorage.get((WorldServer) entity.world).raidOf(home);
    }

    public static void start(WorldServer world, RaidDef def, BlockPos center, boolean free) { start(world, def, new RaidTarget(free ? RaidTarget.Kind.FREE : RaidTarget.Kind.VILLAGE, center, VILLAGE_SEARCH, 0, null, 0)); }

    public static void start(WorldServer world, RaidDef def, RaidTarget target) {
        BlockPos center = target.center();
        RaidSwitchDef change = ContentRaidSwitch.at(world, center);
        int scaled = change.waves(def.waves.size());
        BaseDef base = baseFor(def, target.kind());
        if (base != null && base.fullScore > 0) { scaled = Math.max(1, (int) Math.round(scaled * Math.min(1.0D, target.score() / base.fullScore))); }
        ActiveRaid raid = new ActiveRaid(def, target, scaled, change.points(def.points));
        RaidStorage.get(world).add(raid);
        ContentRaidSwitch.started(world, center, def.reach);
        ContentLog.LOGGER.info("Raid {} begins {} {}, {}, {} with {} wave(s) for {} point(s)", def.registryName, raid.kind == RaidTarget.Kind.FREE ? "with no village at" : "on " + raid.site() + " at", center.getX(), center.getY(), center.getZ(), raid.waves, raid.points);
    }

    @Nullable private static BaseDef baseFor(RaidDef def, RaidTarget.Kind kind) { return kind.guarded() ? def.base() : null; }

    private String site() { return kind == RaidTarget.Kind.BASE ? "the base" : kind == RaidTarget.Kind.PLAYER ? "the player" : "the village"; }

    BlockPos center() { return center; }

    public int owner() { return owner; }

    public RaidTarget.Kind kind() { return kind; }

    @Nullable public UUID target() { return targetId; }

    public RaidDef def() { return def; }

    boolean stopped() { return status == Status.STOPPED; }

    boolean underway(BlockPos at) { return status == Status.ONGOING && wave > 0 && !raiders.isEmpty() && at.distanceSq(center) < (double) reach() * reach(); }

    void supervise(WorldServer world) {
        if (base == null) { return; }
        owner = RaidOwner.of(world.getMinecraftServer(), owner);
        resolved = true;
        if (status != Status.ONGOING) { return; }
        if (owner == 0) {
            stop(world, null);
            return;
        }
        if (RaidDefenders.online(world, owner, targetId)) { paused = -1; }
        else if (!"continue".equals(base.onLogoff)) {
            if (paused < 0) { paused = world.getTotalWorldTime(); }
            if ("end".equals(base.onLogoff) || world.getTotalWorldTime() - paused >= base.logoffGrace) { stop(world, "the owner left"); }
            return;
        }
        if (kind == RaidTarget.Kind.PLAYER && base.follow) { follow(world); }
        if (wave < 1 || !"defenders".equals(base.loss)) { return; }
        if (RaidDefenders.count(world, center, reach(), radius, owner, targetId) > 0) {
            lost = 0;
            return;
        }
        lost += SUPERVISE;
        if (lost >= base.loseGrace) { end(world, Status.LOSS); }
    }

    private void follow(WorldServer world) {
        if (anchored) { return; }
        if (world.getTotalWorldTime() % RECHECK == 0 && RaidStorage.get(world).crowds(this, def.reach)) {
            anchored = true;
            return;
        }
        EntityPlayer player = followed(world);
        if (player == null || player.getDistanceSq(center) <= FOLLOW_STEP * FOLLOW_STEP) { return; }
        center = new BlockPos(player);
        bells = null;
        forRaiders(world, entity -> tagRaider(entity, center));
    }

    private void forRaiders(WorldServer world, Consumer<Entity> action) {
        for (UUID id : raiders) {
            Entity entity = world.getEntityFromUuid(id);
            if (entity != null) { action.accept(entity); }
        }
    }

    @Nullable private EntityPlayer followed(WorldServer world) { return base != null && base.follow && kind == RaidTarget.Kind.PLAYER && targetId != null ? world.getPlayerEntityByUUID(targetId) : null; }

    private int reach() { return base == null ? def.reach : Math.max(def.reach, radius); }

    private double stray() {
        int span = (base == null ? def.reach : Math.max(def.reach, radius + 2 * def.spawnDistance + 5 + FOLLOW_STEP)) + STRAY_BEYOND;
        return (double) span * span;
    }

    private void release(WorldServer world) {
        forRaiders(world, ActiveRaid::untagRaider);
        raiders.clear();
        if (base != null) { BaseLedger.get(world).markRaided(world.getMinecraftServer(), owner, world.getTotalWorldTime()); }
    }

    void tick(WorldServer world) {
        if (status == Status.STOPPED) { return; }
        if (base != null && !resolved) { return; }
        if (status != Status.ONGOING) {
            celebrate(world);
            return;
        }
        boolean loaded = world.isBlockLoaded(center);
        bar.setVisible(loaded);
        if (!loaded) { return; }
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            stop(world, "the world is peaceful");
            return;
        }
        if (kind == RaidTarget.Kind.VILLAGE) {
            Village village = nearestVillage(world, center, VILLAGE_SEARCH);
            if (village == null) {
                if (wave > 0) { end(world, Status.LOSS); }
                else { stop(world, "the village is gone before the first wave"); }
                return;
            }
            center = village.getCenter();
            radius = village.getVillageRadius();
            if (wave > 0 && village.getNumVillagers() == 0) {
                end(world, Status.LOSS);
                return;
            }
        }
        if (paused >= 0) { return; }
        ticksActive++;
        if (def.timeout > 0 && ticksActive >= def.timeout) {
            stop(world, "it ran " + def.timeout + " ticks");
            return;
        }
        List<EntityLivingBase> alive = alive(world);
        if (alive.isEmpty() && wave < waves) {
            if (cooldown > 0) {
                cooldown--;
                bar.setName(new TextComponentString(def.title));
                BossBars.progress(bar, (def.waveDelay - cooldown) / (float) def.waveDelay);
                if (cooldown % 20 == 0) { players(world); }
                return;
            }
            if (!spawnWave(world)) { return; }
            for (BlockPos bell : bells(world)) { ContentRaids.ring(world, bell); }
            cooldown = def.waveDelay;
            alive = alive(world);
        }
        if (ticksActive % 20 == 0) {
            players(world);
            float health = 0.0F;
            for (EntityLivingBase one : alive) { health += one.getHealth(); }
            BossBars.progress(bar, waveHealth <= 0.0F ? 0.0F : health / waveHealth);
            bar.setName(new TextComponentString(!alive.isEmpty() && alive.size() <= 2 ? def.title + " - Raiders Remaining: " + alive.size() : def.title));
        }
        if (wave >= waves && alive.isEmpty() && ++quiet >= QUIET_BEFORE_VICTORY) { end(world, Status.VICTORY); }
    }

    private List<EntityLivingBase> alive(WorldServer world) {
        List<EntityLivingBase> found = new ArrayList<>();
        double stray = stray();
        raiders.removeIf(id -> {
            Entity entity = world.getEntityFromUuid(id);
            if (entity == null) { return paused < 0; }
            if (!(entity instanceof EntityLivingBase) || !entity.isEntityAlive()) { return true; }
            if (entity.getDistanceSq(center) > stray) {
                untagRaider(entity);
                return true;
            }
            found.add((EntityLivingBase) entity);
            return false;
        });
        return found;
    }

    private boolean spawnWave(WorldServer world) {
        BlockPos origin = origin(world);
        int rings = base == null || base.insideSpawn ? 3 : 2;
        BlockPos at = null;
        for (int ring = 0; ring < rings && at == null; ring++) { at = spawnSpot(world, origin, ring); }
        if (at == null) {
            stop(world, "no spot around " + site() + " could take wave " + (wave + 1));
            return false;
        }
        waveHealth = 0.0F;
        int made = 0;
        for (RaidDef.Group group : def.waves.get(Math.min(wave, def.waves.size() - 1))) {
            int count = group.count.pick(world.rand);
            for (int i = 0; i < count; i++) {
                Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(group.entity), world);
                if (entity == null) {
                    ContentLog.LOGGER.error("Raid {} sends {} in wave {}, which nothing registers, so it does not come", def.registryName, group.entity, wave + 1);
                    break;
                }
                entity.setLocationAndAngles(at.getX() + 0.5D + world.rand.nextInt(3) - 1, at.getY(), at.getZ() + 0.5D + world.rand.nextInt(3) - 1, world.rand.nextFloat() * 360.0F, 0.0F);
                Spawns.initialise(world, entity, at);
                if (entity instanceof EntityLiving) { ((EntityLiving) entity).enablePersistence(); }
                tagRaider(entity, center);
                world.spawnEntity(entity);
                raiders.add(entity.getUniqueID());
                if (entity instanceof EntityLivingBase) { waveHealth += ((EntityLivingBase) entity).getMaxHealth(); }
                made++;
            }
        }
        wave++;
        quiet = 0;
        horn(world, at);
        ContentLog.LOGGER.info("Raid {} on {} at {}, {}, {} sends wave {} of {}: {} raider(s) at {}, {}, {}", def.registryName, site(), center.getX(), center.getY(), center.getZ(), wave, waves, made, at.getX(), at.getY(), at.getZ());
        return true;
    }

    @Nullable public static Village nearestVillage(WorldServer world, BlockPos at, int reach) {
        Village nearest = null;
        double best = Double.MAX_VALUE;
        for (Village village : world.getVillageCollection().getVillageList()) {
            double distance = village.getCenter().distanceSq(at);
            float span = reach + village.getVillageRadius();
            if (distance < best && distance <= span * span) {
                nearest = village;
                best = distance;
            }
        }
        return nearest;
    }

    private List<BlockPos> bells(WorldServer world) {
        if (bells != null) { return bells; }
        List<BlockPos> found = new ArrayList<>();
        int reach = radius;
        for (BlockPos at : BlockPos.getAllInBoxMutable(center.add(-reach, -BELL_RISE, -reach), center.add(reach, BELL_RISE, reach))) {
            if (world.isBlockLoaded(at) && ContentRaids.isBell(world.getBlockState(at))) { found.add(at.toImmutable()); }
        }
        bells = found;
        return found;
    }

    private BlockPos origin(WorldServer world) {
        EntityPlayer player = followed(world);
        return player == null ? center : new BlockPos(player);
    }

    @Nullable private BlockPos spawnSpot(WorldServer world, BlockPos origin, int ring) {
        int distance = base == null ? def.spawnDistance * (ring == 0 ? 2 : 2 - ring) : radius + def.spawnDistance * (2 - ring);
        for (int i = 0; i < SPAWN_TRIES; i++) {
            float angle = world.rand.nextFloat() * ((float) Math.PI * 2.0F);
            int x = origin.getX() + MathHelper.floor(MathHelper.cos(angle) * distance) + world.rand.nextInt(5);
            int z = origin.getZ() + MathHelper.floor(MathHelper.sin(angle) * distance) + world.rand.nextInt(5);
            BlockPos top = world.getHeight(new BlockPos(x, 0, z));
            if (!world.isAreaLoaded(top, 10)) { continue; }
            if (ring < 2 && (base == null ? nearestVillage(world, top, 0) != null : RaidOwner.same(world.getMinecraftServer(), ClaimGuard.covers(world, top), owner))) { continue; }
            if (!world.getBlockState(top.down()).isSideSolid(world, top.down(), EnumFacing.UP) || world.getBlockState(top).getMaterial().isLiquid()) { continue; }
            return top;
        }
        return null;
    }

    private void horn(WorldServer world, BlockPos at) {
        if (def.sound.isEmpty()) { return; }
        SoundEvent sound = Registries.find(ForgeRegistries.SOUND_EVENTS, def.sound);
        if (sound == null) {
            ContentLog.LOGGER.error("Raid {} names the sound {}, which nothing registers, so the waves come quietly", def.registryName, def.sound);
            return;
        }
        for (EntityPlayerMP player : inReach(world)) { world.playSound(null, player.posX + (at.getX() - player.posX) / 13.0D, player.posY, player.posZ + (at.getZ() - player.posZ) / 13.0D, sound, SoundCategory.NEUTRAL, 64.0F, 1.0F); }
    }

    private void end(WorldServer world, Status ending) {
        release(world);
        status = ending;
        celebration = 0;
        List<EntityPlayerMP> present = inReach(world);
        List<EntityPlayerMP> paid = base == null || base.rewardAll ? present : RaidDefenders.inReach(world, center, reach(), owner, targetId);
        if (base != null && ending != Status.VICTORY) {
            paid = new ArrayList<>(paid);
            for (EntityPlayerMP player : RaidDefenders.inWorld(world, owner, targetId)) {
                if (!paid.contains(player)) { paid.add(player); }
            }
            for (EntityPlayerMP player : paid) {
                if (!present.contains(player)) { CardEvents.raidEnded(player, def.registryName.toString(), false); }
            }
        }
        String function = ending == Status.VICTORY ? def.wins : def.loses;
        ContentLog.LOGGER.info("Raid {} on {} at {}, {}, {} ends in {} after {} of {} wave(s), with {} player(s) in reach", def.registryName, site(), center.getX(), center.getY(), center.getZ(), ending == Status.VICTORY ? "victory" : "defeat", wave, waves, present.size());
        if (ending == Status.VICTORY) {
            for (EntityPlayerMP player : paid) { reward(world, player); }
        }
        for (EntityPlayerMP player : present) { CardEvents.raidEnded(player, def.registryName.toString(), ending == Status.VICTORY); }
        if (function.isEmpty()) { return; }
        for (EntityPlayerMP player : paid) { Functions.runAs(player, function, "Raid " + def.registryName); }
    }

    private void reward(WorldServer world, EntityPlayerMP player) {
        ContentScoring.raidWon(player, points);
        Rewards.loot(world, player, def.loot);
    }

    private void celebrate(WorldServer world) {
        if (++celebration >= CELEBRATION) {
            stop(world, null);
            return;
        }
        if (celebration % 20 != 0) { return; }
        players(world);
        bar.setVisible(true);
        if (status == Status.VICTORY) {
            bar.setPercent(0.0F);
            bar.setName(new TextComponentString(def.title + " - Victory"));
        }
        else { bar.setName(new TextComponentString(def.title + " - Defeat")); }
    }

    private void players(WorldServer world) { BossBars.sync(bar, inReach(world)); }

    private List<EntityPlayerMP> inReach(WorldServer world) { return BossBars.near(world, center, reach()); }

    private void stop(WorldServer world, @Nullable String why) {
        if (why != null) { ContentLog.LOGGER.info("Raid {} on {} at {}, {}, {} stops: {}", def.registryName, site(), center.getX(), center.getY(), center.getZ(), why); }
        if (status == Status.ONGOING) { release(world); }
        status = Status.STOPPED;
        BossBars.clear(bar);
    }

    NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(DEF_KEY, def.registryName.toString());
        tag.setTag("Center", NBTUtil.createPosTag(center));
        tag.setInteger("Waves", waves);
        tag.setInteger("Points", points);
        tag.setInteger("Wave", wave);
        tag.setInteger("Cooldown", cooldown);
        tag.setLong("Ticks", ticksActive);
        tag.setInteger("Quiet", quiet);
        tag.setInteger("Celebration", celebration);
        tag.setFloat("WaveHealth", waveHealth);
        tag.setString("Status", status.name());
        tag.setString("Kind", kind.key());
        tag.setInteger("Owner", owner);
        if (targetId != null) { tag.setString("Target", targetId.toString()); }
        tag.setInteger("Radius", radius);
        tag.setInteger("Score", score);
        if (paused >= 0) { tag.setLong("Paused", paused); }
        tag.setInteger("Lost", lost);
        NBTTagList ids = new NBTTagList();
        for (UUID id : raiders) { ids.appendTag(NBTUtil.createUUIDTag(id)); }
        tag.setTag("Raiders", ids);
        return tag;
    }

    @Nullable static ActiveRaid read(NBTTagCompound tag, @Nullable RaidDef def) {
        if (def == null) {
            ContentLog.LOGGER.warn("A saved raid names {}, which no pack provides any more, so it is dropped", tag.getString(DEF_KEY));
            return null;
        }
        RaidTarget.Kind kind = tag.hasKey("Kind") ? RaidTarget.Kind.parse(tag.getString("Kind")) : tag.getBoolean("Free") ? RaidTarget.Kind.FREE : RaidTarget.Kind.VILLAGE;
        UUID target = tag.hasKey("Target") ? UUID.fromString(tag.getString("Target")) : null;
        RaidTarget saved = new RaidTarget(kind, NBTUtil.getPosFromTag(tag.getCompoundTag("Center")), tag.hasKey("Radius") ? tag.getInteger("Radius") : VILLAGE_SEARCH, tag.getInteger("Owner"), target, tag.getInteger("Score"));
        ActiveRaid raid = new ActiveRaid(def, saved, tag.hasKey("Waves") ? tag.getInteger("Waves") : def.waves.size(), tag.hasKey("Points") ? tag.getInteger("Points") : def.points);
        raid.wave = tag.getInteger("Wave");
        raid.cooldown = tag.getInteger("Cooldown");
        raid.ticksActive = tag.getLong("Ticks");
        raid.quiet = tag.getInteger("Quiet");
        raid.celebration = tag.getInteger("Celebration");
        raid.waveHealth = tag.getFloat("WaveHealth");
        raid.paused = tag.hasKey("Paused") ? tag.getLong("Paused") : -1;
        raid.lost = tag.getInteger("Lost");
        try { raid.status = Status.valueOf(tag.getString("Status")); }
        catch (IllegalArgumentException unknown) { raid.status = Status.STOPPED; }
        NBTTagList ids = tag.getTagList("Raiders", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < ids.tagCount(); i++) { raid.raiders.add(NBTUtil.getUUIDFromTag(ids.getCompoundTagAt(i))); }
        return raid;
    }
}
