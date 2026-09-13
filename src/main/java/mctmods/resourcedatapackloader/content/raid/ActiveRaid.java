package mctmods.resourcedatapackloader.content.raid;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.def.RaidDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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
    private final RaidDef def;
    private final BossInfoServer bar;
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

    enum Status { ONGOING, VICTORY, LOSS, STOPPED }

    public ActiveRaid(RaidDef def, BlockPos center) {
        this.def = def;
        this.center = center;
        this.cooldown = def.waveDelay;
        this.bar = new BossInfoServer(new TextComponentString(def.name), def.color, BossInfo.Overlay.NOTCHED_10);
    }

    BlockPos center() { return center; }

    boolean stopped() { return status == Status.STOPPED; }

    boolean underway(BlockPos at) { return status == Status.ONGOING && wave > 0 && !raiders.isEmpty() && at.distanceSq(center) < (double) def.reach * def.reach; }

    void tick(WorldServer world) {
        if (status == Status.STOPPED) { return; }
        if (status != Status.ONGOING) {
            celebrate(world);
            return;
        }
        boolean loaded = world.isBlockLoaded(center);
        bar.setVisible(loaded);
        if (!loaded) { return; }
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            stop("the world is peaceful");
            return;
        }
        Village village = nearestVillage(world, center, VILLAGE_SEARCH);
        if (village == null) {
            if (wave > 0) { end(world, Status.LOSS); }
            else { stop("the village is gone before the first wave"); }
            return;
        }
        center = village.getCenter();
        if (wave > 0 && village.getNumVillagers() == 0) {
            end(world, Status.LOSS);
            return;
        }
        ticksActive++;
        if (def.timeout > 0 && ticksActive >= def.timeout) {
            stop("it ran " + def.timeout + " ticks");
            return;
        }
        List<EntityLivingBase> alive = alive(world);
        if (alive.isEmpty() && wave < def.waves.size()) {
            if (cooldown > 0) {
                cooldown--;
                bar.setName(new TextComponentString(def.name));
                bar.setPercent(MathHelper.clamp((def.waveDelay - cooldown) / (float) def.waveDelay, 0.0F, 1.0F));
                if (cooldown % 20 == 0) { players(world); }
                return;
            }
            if (!spawnWave(world)) { return; }
            for (BlockPos bell : bells(world, village)) { ContentRaids.ring(world, bell); }
            cooldown = def.waveDelay;
            alive = alive(world);
        }
        if (ticksActive % 20 == 0) {
            players(world);
            float health = 0.0F;
            for (EntityLivingBase one : alive) { health += one.getHealth(); }
            bar.setPercent(waveHealth <= 0.0F ? 0.0F : MathHelper.clamp(health / waveHealth, 0.0F, 1.0F));
            bar.setName(new TextComponentString(!alive.isEmpty() && alive.size() <= 2 ? def.name + " - Raiders Remaining: " + alive.size() : def.name));
        }
        if (wave >= def.waves.size() && alive.isEmpty() && ++quiet >= QUIET_BEFORE_VICTORY) { end(world, Status.VICTORY); }
    }

    private List<EntityLivingBase> alive(WorldServer world) {
        List<EntityLivingBase> found = new ArrayList<>();
        double stray = (double) (def.reach + STRAY_BEYOND) * (def.reach + STRAY_BEYOND);
        raiders.removeIf(id -> {
            Entity entity = world.getEntityFromUuid(id);
            if (!(entity instanceof EntityLivingBase) || !entity.isEntityAlive()) { return true; }
            if (entity.getDistanceSq(center) > stray) {
                entity.getEntityData().removeTag(RAIDER);
                return true;
            }
            found.add((EntityLivingBase) entity);
            return false;
        });
        return found;
    }

    private boolean spawnWave(WorldServer world) {
        BlockPos at = null;
        for (int ring = 0; ring < 3 && at == null; ring++) { at = spawnSpot(world, ring); }
        if (at == null) {
            stop("no spot around the village could take wave " + (wave + 1));
            return false;
        }
        waveHealth = 0.0F;
        int made = 0;
        for (RaidDef.Group group : def.waves.get(wave)) {
            int count = group.count.pick(world.rand);
            for (int i = 0; i < count; i++) {
                Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(group.entity), world);
                if (entity == null) {
                    ContentLog.LOGGER.error("Raid {} sends {} in wave {}, which nothing registers, so it does not come", def.registryName, group.entity, wave + 1);
                    break;
                }
                entity.setLocationAndAngles(at.getX() + 0.5D + world.rand.nextInt(3) - 1, at.getY(), at.getZ() + 0.5D + world.rand.nextInt(3) - 1, world.rand.nextFloat() * 360.0F, 0.0F);
                if (entity instanceof EntityLiving) {
                    EntityLiving living = (EntityLiving) entity;
                    living.onInitialSpawn(world.getDifficultyForLocation(at), null);
                    living.enablePersistence();
                }
                entity.getEntityData().setTag(RAIDER, NBTUtil.createPosTag(center));
                world.spawnEntity(entity);
                raiders.add(entity.getUniqueID());
                if (entity instanceof EntityLivingBase) { waveHealth += ((EntityLivingBase) entity).getMaxHealth(); }
                made++;
            }
        }
        wave++;
        quiet = 0;
        horn(world, at);
        ContentLog.LOGGER.info("Raid {} on the village at {}, {}, {} sends wave {} of {}: {} raider(s) at {}, {}, {}", def.registryName, center.getX(), center.getY(), center.getZ(), wave, def.waves.size(), made, at.getX(), at.getY(), at.getZ());
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

    private List<BlockPos> bells(WorldServer world, Village village) {
        if (bells != null) { return bells; }
        List<BlockPos> found = new ArrayList<>();
        int reach = village.getVillageRadius();
        for (BlockPos at : BlockPos.getAllInBoxMutable(center.add(-reach, -BELL_RISE, -reach), center.add(reach, BELL_RISE, reach))) {
            if (world.isBlockLoaded(at) && ContentRaids.isBell(world.getBlockState(at))) { found.add(at.toImmutable()); }
        }
        bells = found;
        return found;
    }

    @Nullable private BlockPos spawnSpot(WorldServer world, int ring) {
        int reach = ring == 0 ? 2 : 2 - ring;
        for (int i = 0; i < SPAWN_TRIES; i++) {
            float angle = world.rand.nextFloat() * ((float) Math.PI * 2.0F);
            int x = center.getX() + MathHelper.floor(MathHelper.cos(angle) * def.spawnDistance * reach) + world.rand.nextInt(5);
            int z = center.getZ() + MathHelper.floor(MathHelper.sin(angle) * def.spawnDistance * reach) + world.rand.nextInt(5);
            BlockPos top = world.getHeight(new BlockPos(x, 0, z));
            if (!world.isAreaLoaded(top, 10)) { continue; }
            if (ring < 2 && nearestVillage(world, top, 0) != null) { continue; }
            if (!world.getBlockState(top.down()).isSideSolid(world, top.down(), EnumFacing.UP) || world.getBlockState(top).getMaterial().isLiquid()) { continue; }
            return top;
        }
        return null;
    }

    private void horn(WorldServer world, BlockPos at) {
        if (def.sound.isEmpty()) { return; }
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(def.sound));
        if (sound == null) {
            ContentLog.LOGGER.error("Raid {} names the sound {}, which nothing registers, so the waves come quietly", def.registryName, def.sound);
            return;
        }
        for (EntityPlayerMP player : inReach(world)) { world.playSound(null, player.posX + (at.getX() - player.posX) / 13.0D, player.posY, player.posZ + (at.getZ() - player.posZ) / 13.0D, sound, SoundCategory.NEUTRAL, 64.0F, 1.0F); }
    }

    private void end(WorldServer world, Status ending) {
        status = ending;
        celebration = 0;
        List<EntityPlayerMP> present = inReach(world);
        String function = ending == Status.VICTORY ? def.wins : def.loses;
        ContentLog.LOGGER.info("Raid {} on the village at {}, {}, {} ends in {} after {} of {} wave(s), with {} player(s) in reach", def.registryName, center.getX(), center.getY(), center.getZ(), ending == Status.VICTORY ? "victory" : "defeat", wave, def.waves.size(), present.size());
        if (function.isEmpty()) { return; }
        for (EntityPlayerMP player : present) { Functions.runAs(player, function, "Raid " + def.registryName); }
    }

    private void celebrate(WorldServer world) {
        if (++celebration >= CELEBRATION) {
            stop(null);
            return;
        }
        if (celebration % 20 != 0) { return; }
        players(world);
        bar.setVisible(true);
        if (status == Status.VICTORY) {
            bar.setPercent(0.0F);
            bar.setName(new TextComponentString(def.name + " - Victory"));
        }
        else { bar.setName(new TextComponentString(def.name + " - Defeat")); }
    }

    private void players(WorldServer world) {
        List<EntityPlayerMP> present = inReach(world);
        for (EntityPlayerMP watching : new ArrayList<>(bar.getPlayers())) {
            if (!present.contains(watching)) { bar.removePlayer(watching); }
        }
        for (EntityPlayerMP player : present) { bar.addPlayer(player); }
    }

    private List<EntityPlayerMP> inReach(WorldServer world) {
        List<EntityPlayerMP> present = new ArrayList<>();
        double reach = (double) def.reach * def.reach;
        for (EntityPlayer player : world.playerEntities) {
            if (player instanceof EntityPlayerMP && player.isEntityAlive() && !player.isSpectator() && player.getDistanceSq(center) < reach) { present.add((EntityPlayerMP) player); }
        }
        return present;
    }

    void stop(@Nullable String why) {
        if (why != null) { ContentLog.LOGGER.info("Raid {} on the village at {}, {}, {} stops: {}", def.registryName, center.getX(), center.getY(), center.getZ(), why); }
        status = Status.STOPPED;
        for (EntityPlayerMP watching : new ArrayList<>(bar.getPlayers())) { bar.removePlayer(watching); }
    }

    NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Raid", def.registryName.toString());
        tag.setTag("Center", NBTUtil.createPosTag(center));
        tag.setInteger("Wave", wave);
        tag.setInteger("Cooldown", cooldown);
        tag.setLong("Ticks", ticksActive);
        tag.setInteger("Quiet", quiet);
        tag.setInteger("Celebration", celebration);
        tag.setFloat("WaveHealth", waveHealth);
        tag.setString("Status", status.name());
        NBTTagList ids = new NBTTagList();
        for (UUID id : raiders) { ids.appendTag(NBTUtil.createUUIDTag(id)); }
        tag.setTag("Raiders", ids);
        return tag;
    }

    @Nullable static ActiveRaid read(NBTTagCompound tag, @Nullable RaidDef def) {
        if (def == null) {
            ContentLog.LOGGER.warn("A saved raid names {}, which no pack provides any more, so it is dropped", tag.getString("Raid"));
            return null;
        }
        ActiveRaid raid = new ActiveRaid(def, NBTUtil.getPosFromTag(tag.getCompoundTag("Center")));
        raid.wave = tag.getInteger("Wave");
        raid.cooldown = tag.getInteger("Cooldown");
        raid.ticksActive = tag.getLong("Ticks");
        raid.quiet = tag.getInteger("Quiet");
        raid.celebration = tag.getInteger("Celebration");
        raid.waveHealth = tag.getFloat("WaveHealth");
        try { raid.status = Status.valueOf(tag.getString("Status")); }
        catch (IllegalArgumentException unknown) { raid.status = Status.STOPPED; }
        NBTTagList ids = tag.getTagList("Raiders", 10);
        for (int i = 0; i < ids.tagCount(); i++) { raid.raiders.add(NBTUtil.getUUIDFromTag(ids.getCompoundTagAt(i))); }
        return raid;
    }
}
