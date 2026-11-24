package mctmods.resourcedatapackloader.common.entities;

import mctmods.resourcedatapackloader.common.anvils.ContentAnvils;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityApply;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityBars;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityBehavior;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityPhases;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentMobExperience;
import mctmods.resourcedatapackloader.common.entities.spawning.EntitySwap;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnGateDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.TeamDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserEntities;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.scripts.ScriptEventHandlers;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;
import mctmods.resourcedatapackloader.common.util.ContentAttributes;
import mctmods.resourcedatapackloader.common.util.ServerTicks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Enums;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Registries;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ContentEntities {
    public static final int THROW = 5;
    static final Map<ResourceLocation, EntityVariantDef> DEFS = new LinkedHashMap<>();
    public static final Map<Class<?>, EntityVariantDef> BY_CLASS = new LinkedHashMap<>();
    private static final Map<String, ResourceLocation> TEXTURES = new LinkedHashMap<>();
    private static final Map<String, ResourceLocation> NAMES = new LinkedHashMap<>();
    private static final Map<String, SoundEvent> SOUNDS = new LinkedHashMap<>();
    private static boolean loaded;
    @Nullable private static EntityLivingBase struck;
    private static long struckAt;
    private static boolean struckEffects;
    private static boolean struckFire;
    private static final Map<Integer, Look> LOOKS = new HashMap<>();
    private static final Map<String, Integer> FELL_TO = new LinkedHashMap<>();
    private static final int ENGAGE_EVERY = 100;
    private static int engageWatch;

    private ContentEntities() {}

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.entities) { return false; }
        Json.eachFile(PackManager.ENTITIES, "entity file", (key, contents) -> {
            EntityVariantDef def = ContentParserEntities.entityVariant(key, contents);
            if (def == null) { return; }
            if (missing(def)) {
                ContentLog.LOGGER.debug("Entity variant {} needs {}, which is not here, so it is left out", key, def.requires);
                return;
            }
            DEFS.put(key, def);
        });
        Summary.loaded("entities", DEFS.size(), "entity variant(s) from packs");
        return !DEFS.isEmpty();
    }

    @Nullable public static EntityVariantDef variantOf(Entity entity) { return BY_CLASS.get(entity.getClass()); }

    public static Collection<EntityVariantDef> variants() { return DEFS.values(); }

    @Nullable public static ResourceLocation texture(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        if (def == null || def.texture.isEmpty()) { return null; }
        return TEXTURES.computeIfAbsent(def.texture, ResourceLocation::new);
    }

    public static float baseBabyChance(Entity entity, float chance) {
        EntityVariantDef def = variantOf(entity);
        return def == null || def.keepsBaseBaby ? chance : 0.0F;
    }

    @Nullable public static ResourceLocation lootTable(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        if (def == null || def.lootTable.isEmpty()) { return null; }
        return NAMES.computeIfAbsent(def.lootTable, ResourceLocation::new);
    }

    @Nullable public static SoundEvent soundEvent(Entity entity, int which) {
        EntityVariantDef def = variantOf(entity);
        if (def == null) { return null; }
        String name = which == 0 ? def.ambientSound : which == 1 ? def.hurtSound : which == 2 ? def.deathSound : which == 3 ? def.targetSound : which == 4 ? def.explodeSound : def.throwSound;
        if (name.isEmpty()) { return null; }
        if (SOUNDS.containsKey(name)) { return SOUNDS.get(name); }
        ResourceLocation key = new ResourceLocation(name);
        SoundEvent event = Registries.find(ForgeRegistries.SOUND_EVENTS, key);
        if (event == null) { ContentLog.LOGGER.error("Entity variant {} names sound {}, which nothing registers", def.registryName, key); }
        SOUNDS.put(name, event);
        return event;
    }

    public static SoundEvent soundEvent(Entity entity, int which, Supplier<SoundEvent> vanilla) {
        SoundEvent own = soundEvent(entity, which);
        return own != null ? own : vanilla.get();
    }

    public static int attackInterval(EntityLivingBase attacker) {
        IAttributeInstance speed = attacker.getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_SPEED.getName());
        if (speed == null || speed.getAttributeValue() <= 0.0D) { return MathUtil.TICKS_PER_SECOND; }
        return Math.max(1, (int) Math.round((double) MathUtil.TICKS_PER_SECOND / speed.getAttributeValue()));
    }

    public static boolean immune(Entity entity, String damageType) {
        EntityVariantDef def = variantOf(entity);
        if (def == null || def.immuneTo.isEmpty()) { return false; }
        for (String wanted : def.immuneTo) {
            if (wanted.equalsIgnoreCase(damageType)) { return true; }
        }
        return false;
    }

    public static float scale(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        if (def == null) { return 1.0F; }
        return scale(entity, def);
    }

    private static float scale(Entity entity, EntityVariantDef def) { return entity.isSprinting() ? def.angryScale : def.scale; }

    public static int threatLeast(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def == null ? 0 : def.threatLeast;
    }

    public static int threatHostile(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def == null ? 0 : def.threatHostile;
    }

    public static SpawnGateDef spawnGate(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def == null ? SpawnGateDef.NONE : def.gate;
    }

    public static boolean leashable(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.leashable;
    }

    public static boolean steerable(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.steerable;
    }

    @SubscribeEvent public static void onBars(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        MinecraftServer server = ServerTicks.every(20);
        if (server == null) { return; }
        long now = server.getEntityWorld().getTotalWorldTime();
        ContentEntityBars.sweep(now);
        ContentEntityPhases.sweep(server);
    }

    @SubscribeEvent public static void onEngagement(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || BY_CLASS.isEmpty() || !ContentLog.LOGGER.debugEnabled()) { return; }
        if (++engageWatch % ENGAGE_EVERY != 0) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        int mobs = 0;
        int aimed = 0;
        int pathless = 0;
        int reaching = 0;
        int walked = 0;
        int spun = 0;
        int hurt = 0;
        List<EntityLiving> here = new ArrayList<>();
        long away = 0L;
        Set<Integer> seen = new HashSet<>();
        for (WorldServer world : server.worlds) {
            if (world == null) { continue; }
            for (Entity entity : world.loadedEntityList) {
                if (!(entity instanceof EntityLiving) || variantOf(entity) == null) { continue; }
                EntityLiving mob = (EntityLiving) entity;
                mobs++;
                seen.add(mob.getEntityId());
                here.add(mob);
                long at = mob.getPosition().toLong();
                Look before = LOOKS.put(mob.getEntityId(), new Look(at, mob.rotationYaw, mob.getHealth()));
                if (before != null && before.health > mob.getHealth()) { hurt++; }
                if (before != null && before.at != at) { walked++; }
                if (before != null && before.at == at && Math.abs(MathHelper.wrapDegrees(mob.rotationYaw - before.yaw)) >= 90.0F) { spun++; }
                EntityLivingBase aim = mob.getAttackTarget();
                if (aim == null) { continue; }
                aimed++;
                double gap = Math.sqrt(mob.getDistanceSq(aim));
                away += (long) gap;
                if (mob.getNavigator().noPath()) { pathless++; }
                if (gap <= mob.width * 2.0F + aim.width) { reaching++; }
            }
        }
        int gone = 0;
        for (Iterator<Integer> held = LOOKS.keySet().iterator(); held.hasNext();) {
            if (seen.contains(held.next())) { continue; }
            held.remove();
            gone++;
        }
        int thickest = 0;
        double spread = 0.0D;
        if (!here.isEmpty()) {
            double middleX = 0.0D;
            double middleZ = 0.0D;
            for (EntityLiving one : here) { middleX += one.posX; middleZ += one.posZ; }
            middleX /= here.size();
            middleZ /= here.size();
            for (EntityLiving one : here) { spread += Math.sqrt((one.posX - middleX) * (one.posX - middleX) + (one.posZ - middleZ) * (one.posZ - middleZ)); }
            spread /= here.size();
            for (EntityLiving one : here) {
                int near = 0;
                for (EntityLiving other : here) { if (one.getDistanceSq(other) <= 64.0D) { near++; } }
                thickest = Math.max(thickest, near);
            }
        }
        if (!here.isEmpty()) {
            double leastX = Double.MAX_VALUE;
            double mostX = -Double.MAX_VALUE;
            double leastZ = Double.MAX_VALUE;
            double mostZ = -Double.MAX_VALUE;
            for (EntityLiving one : here) {
                leastX = Math.min(leastX, one.posX);
                mostX = Math.max(mostX, one.posX);
                leastZ = Math.min(leastZ, one.posZ);
                mostZ = Math.max(mostZ, one.posZ);
            }
            int[][] cells = new int[11][11];
            double wideX = Math.max(1.0D, mostX - leastX);
            double wideZ = Math.max(1.0D, mostZ - leastZ);
            for (EntityLiving one : here) {
                int col = Math.min(10, (int) ((one.posX - leastX) / wideX * 11.0D));
                int row = Math.min(10, (int) ((one.posZ - leastZ) / wideZ * 11.0D));
                cells[row][col]++;
            }
            StringBuilder drawn = new StringBuilder();
            for (int[] row : cells) {
                if (drawn.length() > 0) { drawn.append('/'); }
                for (int count : row) { drawn.append(count == 0 ? "." : count > 9 ? "+" : Character.forDigit(count, 10)); }
            }
            ContentLog.LOGGER.debug("Where they stand, {} mob(s) over x {} to {} and z {} to {}, eleven cells each way: {}",
                    here.size(), (int) leastX, (int) mostX, (int) leastZ, (int) mostZ, drawn);
        }
        if (!FELL_TO.isEmpty()) { ContentLog.LOGGER.debug("What has killed pack mobs so far: {}", FELL_TO); }
        if (mobs == 0) { return; }
        ContentLog.LOGGER.debug("Of {} pack mob(s), {} hold a target, {} of those have no path to walk to it, {} stand close enough to strike, and a target is {} block(s) off on average; {} moved since the last look, {} are gone since the last look and {} turned 90 degrees or more without leaving their block, {} lost health since the last look, the thickest crowd holds {} within 8 blocks and a mob stands {} block(s) from the middle of them all on average",
                mobs, aimed, pathless, reaching, aimed == 0 ? 0L : away / aimed, walked, gone, spun, hurt, thickest, String.format(Locale.ROOT, "%.1f", spread));
    }

    @SubscribeEvent public static void onDeathWatch(LivingDeathEvent event) {
        if (!ContentLog.LOGGER.debugEnabled() || variantOf(event.getEntityLiving()) == null) { return; }
        FELL_TO.merge(event.getSource().damageType, 1, Integer::sum);
    }

    @SubscribeEvent public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!(living instanceof EntityLiving)) { return; }
        EntityVariantDef def = variantOf(living);
        if (def == null) { return; }
        if (!def.phases.isEmpty() && !living.world.isRemote) { ContentEntityPhases.tick((EntityLiving) living, def); }
        if (def.combat.bossBar != null && !living.world.isRemote) { ContentEntityBars.tick(living, def); }
        if (def.collectsExperience && !living.world.isRemote && living.isEntityAlive()) {
            ContentMobExperience.collect((EntityLiving) living);
            ContentAnvils.gather((EntityLiving) living);
        }
        if (def.scale == def.angryScale && def.scale == 1.0F && def.baby <= 0.0F && !def.amphibious && def.despawnTicks <= 0 && def.targetSound.isEmpty()) { return; }
        if (living.world.isRemote) {
            if (def.scale != 1.0F || def.scale != def.angryScale) { ContentEntityApply.resize(living, scale(living, def)); }
            return;
        }
        if (def.despawnTicks > 0 && timeIsUp(living, def)) {
            if (!def.despawnDropsLead && living instanceof EntityLiving) { ((EntityLiving) living).clearLeashed(true, false); }
            ScriptEventHandlers.despawn((EntityLiving) living);
            living.setDead();
            return;
        }
        if (def.amphibious) { ContentEntityApply.amphibious((EntityLiving) living); }
        if (def.baby > 0.0F && living.getEntityData().getBoolean(ContentEntityApply.YOUNG) && living instanceof EntityAgeable && ((EntityAgeable) living).getGrowingAge() >= 0) { ((EntityAgeable) living).setGrowingAge(-24000); }
        boolean angry = stillRoused((EntityLiving) living);
        if (angry && !living.isSprinting()) { cry(living); }
        if (angry != living.isSprinting()) { living.setSprinting(angry); }
        ContentEntityApply.resize(living, scale(living, def));
    }

    private static void cry(EntityLivingBase living) {
        SoundEvent cry = soundEvent(living, 3);
        EntityVariantDef def = variantOf(living);
        if (cry == null || def == null) { return; }
        float carries = (float) Math.max(1.0D, ContentAttributes.followRange(living) / 16.0D);
        float varies = def.targetVaries;
        float pitch = varies <= 0.0F ? 1.0F : (float) Math.pow(2.0D, (living.getRNG().nextFloat() * 2.0F - 1.0F) * varies / 12.0D);
        living.world.playSound(null, living.posX, living.posY, living.posZ, cry, living.getSoundCategory(), carries, pitch);
    }

    @Nullable public static SoundEvent explodeSound(@Nullable Entity exploder) {
        if (exploder instanceof EntityTNTPrimed) { exploder = ((EntityTNTPrimed) exploder).getTntPlacedBy(); }
        return exploder == null ? null : soundEvent(exploder, 4);
    }

    public static void fromSpawner(boolean adding) { SPAWNER.set(adding); }

    @SubscribeEvent public static void onEnderTeleport(EnderTeleportEvent event) { if (event.getEntityLiving() instanceof EntityEnderman && !teleports(event.getEntityLiving())) { event.setCanceled(true); } }

    private static List<PickDef> kin(EntityVariantDef def) {
        List<PickDef> kept = new ArrayList<>();
        List<TeamDef> sides = ContentTeams.claiming(def.registryName.toString());
        for (PickDef choice : def.becomes) {
            EntityVariantDef other = DEFS.get(new ResourceLocation(choice.name));
            if (other == null || !other.base.equals(def.base)) { continue; }
            List<TeamDef> theirs = ContentTeams.claiming(other.registryName.toString());
            if (sides.isEmpty() ? theirs.isEmpty() : sides.stream().anyMatch(theirs::contains)) { kept.add(choice); }
        }
        return kept;
    }

    public static boolean collectsExperience(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.collectsExperience;
    }

    public static boolean bright(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.bright;
    }

    public static float jumpMultiplier(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def == null ? 1.0F : def.jumpMultiplier;
    }

    public static float fallDamage(Entity entity, float original) {
        EntityVariantDef def = variantOf(entity);
        if (def == null) { return original; }
        return def.swoops ? 0.0F : original * def.fallDamage;
    }

    public static float sound(Entity entity, float original, boolean pitch) {
        EntityVariantDef def = variantOf(entity);
        if (def == null) { return original; }
        return original * (pitch ? def.soundPitch : def.soundVolume);
    }

    public static float waterSlowdown(Entity entity, float original) {
        EntityVariantDef def = variantOf(entity);
        return def == null ? original : def.waterSlowdown;
    }

    public static int experience(Entity entity, int original) {
        EntityVariantDef def = variantOf(entity);
        return def == null || def.experience < 0 ? original : def.experience;
    }

    public static int maxFallHeight(Entity entity, int original) {
        EntityVariantDef def = variantOf(entity);
        return def == null || def.maxFallHeight < 0 ? original : def.maxFallHeight;
    }

    public static boolean breathesUnderwater(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && (def.breathesUnderwater || def.swims || def.amphibious);
    }

    public static boolean sinks(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.breathesUnderwater && !def.swims;
    }

    public static boolean despawns(Entity entity, boolean original) {
        EntityVariantDef def = variantOf(entity);
        return def == null ? original : def.despawns && original;
    }

    @Nullable public static EnumCreatureAttribute creatureAttribute(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        if (def == null || def.creatureAttribute.isEmpty()) { return null; }
        EnumCreatureAttribute value = Enums.byName(EnumCreatureAttribute.class, def.creatureAttribute);
        if (value != null) { return value; }
        ContentLog.LOGGER.error("Entity variant {} names creature attribute '{}', which is not one of undefined, undead, arthropod or illager", def.registryName, def.creatureAttribute);
        return null;
    }

    public static boolean fireproof(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.fireproof;
    }

    public static boolean hidesArmor(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.hideArmor;
    }

    public static boolean hidesHeld(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.hideHeld;
    }

    public static int tint(Entity entity, String part) {
        EntityVariantDef def = variantOf(entity);
        if (def == null || def.tint == 0 || !def.tintParts.contains(part)) { return 0; }
        return def.tint;
    }

    @SubscribeEvent public static void onStruck(LivingAttackEvent event) {
        Entity by = event.getSource().getTrueSource();
        if (by == null || event.getEntityLiving().world.isRemote) { return; }
        EntityVariantDef def = variantOf(by);
        if (def == null || (def.hitEffects && def.hitFire)) { return; }
        struck = event.getEntityLiving();
        struckAt = struck.world.getTotalWorldTime();
        struckEffects = !def.hitEffects;
        struckFire = !def.hitFire;
    }

    public static boolean struckFireless(Entity entity) { return struckFire && entity == struck && entity.world.getTotalWorldTime() == struckAt; }

    @SubscribeEvent public static void onShot(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityArrow)) { return; }
        EntityArrow arrow = (EntityArrow) event.getEntity();
        Entity shooter = arrow.shootingEntity;
        if (!(shooter instanceof EntityLivingBase)) { return; }
        EntityVariantDef def = variantOf(shooter);
        if (def == null || !ContentEntityBehavior.declaresAttackDamage(def)) { return; }
        IAttributeInstance damage = ((EntityLivingBase) shooter).getAttributeMap().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        if (damage == null) { return; }
        arrow.setDamage(arrow.getDamage() - 2.0D + damage.getAttributeValue());
    }

    public static double attackReachSqr(EntityLivingBase attacker, EntityLivingBase target, double vanilla) {
        EntityVariantDef def = variantOf(attacker);
        if (def == null || def.attackReach <= 0.0F) { return vanilla; }
        return (double) def.attackReach * def.attackReach + target.width;
    }

    public static float knockback(@Nullable Entity by, float strength) {
        EntityVariantDef def = by == null ? null : variantOf(by);
        return def == null || def.knockback < 0.0F ? strength : def.knockback;
    }

    public static boolean lyingAsleep(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.sleepsByDay && entity.isSneaking();
    }

    public static boolean walks(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def != null && def.walks;
    }

    @Nullable public static Boolean climbs(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def == null ? null : def.climbs;
    }

    public static boolean teleports(Entity entity) {
        EntityVariantDef def = variantOf(entity);
        return def == null || def.teleports;
    }

    @SubscribeEvent public static void onEffect(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (struckEffects && living == struck && living.world.getTotalWorldTime() == struckAt) {
            event.setResult(Event.Result.DENY);
            return;
        }
        EntityVariantDef def = variantOf(living);
        if (def == null || def.ignoresEffects.isEmpty()) { return; }
        ResourceLocation potion = event.getPotionEffect().getPotion().getRegistryName();
        String id = potion == null ? "" : potion.toString();
        if (def.effects.containsKey(id)) { return; }
        for (String ignored : def.ignoresEffects) {
            if (ignored.equalsIgnoreCase("all") || ignored.equalsIgnoreCase(id)) {
                event.setResult(Event.Result.DENY);
                return;
            }
        }
    }

    @SubscribeEvent public static void onJoin(EntityJoinWorldEvent event) {
        EntityVariantDef def = variantOf(event.getEntity());
        if (def == null) { return; }
        if (!event.getWorld().isRemote && swapped(event, def)) { return; }
        ContentEntityApply.remember(event.getEntity(), def);
        if (event.getWorld().isRemote) {
            ContentEntityApply.resize(event.getEntity(), scale(event.getEntity(), def));
            return;
        }
        ContentEntityApply.apply(event.getEntity(), def);
        if (!def.phases.isEmpty()) { ContentEntityPhases.join(event.getEntity()); }
    }

    private static boolean swapped(EntityJoinWorldEvent event, EntityVariantDef def) {
        if (def.becomes.isEmpty() || EntitySwap.active()) { return false; }
        List<PickDef> choices = SPAWNER.get() == Boolean.TRUE ? kin(def) : def.becomes;
        String chosen = PickDef.pick(choices, event.getWorld().rand, null);
        if (chosen == null || chosen.equals(def.registryName.toString())) { return false; }
        ResourceLocation wanted = new ResourceLocation(chosen);
        if (!EntityList.isRegistered(wanted)) {
            ContentLog.LOGGER.error("Entity variant {} can become {}, which nothing registers, so it stays as it is", def.registryName, chosen);
            return false;
        }
        if (!EntitySwap.spawn(event.getWorld(), event.getEntity(), wanted, false)) { return false; }
        event.setCanceled(true);
        return true;
    }

    private static final int ROUSED = 60;
    private static final String BORN = "rdplBorn";
    private static final String CALM = "rdplCalmAt";
    private static final long CALM_STEP = 10L;
    private static final ThreadLocal<Boolean> SPAWNER = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private static boolean stillRoused(EntityLiving living) {
        long now = living.world.getTotalWorldTime();
        if (living.getAttackTarget() != null) {
            NBTTagCompound held = living.getEntityData();
            if (held.getLong(CALM) + CALM_STEP < now + ROUSED) { held.setLong(CALM, now + ROUSED); }
            return true;
        }
        return living.getEntityData().getLong(CALM) > now;
    }

    private static boolean timeIsUp(EntityLivingBase living, EntityVariantDef def) {
        NBTTagCompound held = living.getEntityData();
        long now = living.world.getTotalWorldTime();
        if (!held.hasKey(BORN)) {
            held.setLong(BORN, now);
            return false;
        }
        return now - held.getLong(BORN) >= def.despawnTicks;
    }

    @Nullable public static Class<? extends EntityLivingBase> living(String name, EntityVariantDef def) {
        ResourceLocation location = new ResourceLocation(name);
        if (IdNames.VANILLA.equals(location.getNamespace()) && "player".equals(location.getPath())) { return EntityPlayer.class; }
        EntityEntry entry = Registries.find(ForgeRegistries.ENTITIES, location);
        if (entry == null || !EntityLivingBase.class.isAssignableFrom(entry.getEntityClass())) {
            ContentLog.LOGGER.error("Entity variant {} wants to attack '{}', which is not a living entity that is registered", def.registryName, name);
            return null;
        }
        return entry.getEntityClass().asSubclass(EntityLivingBase.class);
    }

    private static boolean missing(EntityVariantDef def) { return !ContentRegistry.available(def.requires, def.registryName); }

    private static final class Look {
        final long at;
        final float yaw;
        final float health;

        Look(long at, float yaw, float health) {
            this.at = at;
            this.yaw = yaw;
            this.health = health;
        }
    }
}
