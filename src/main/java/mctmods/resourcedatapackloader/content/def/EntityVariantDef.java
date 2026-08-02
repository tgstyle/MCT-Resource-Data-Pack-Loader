package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;

public final class EntityVariantDef {
    public final ResourceLocation registryName;
    public final ResourceLocation base;
    public final String name;
    public final String texture;
    public final float jumpMultiplier;
    public final float fallDamage;
    public final float soundVolume;
    public final float soundPitch;
    public final float waterSlowdown;
    public final int experience;
    public final int maxFallHeight;
    public final float absorption;
    public final String creatureAttribute;
    public final boolean breathesUnderwater;
    public final boolean despawns;
    public final boolean noAI;
    public final boolean leftHanded;
    public final boolean fireproof;
    public final boolean invulnerable;
    public final boolean glowing;
    public final boolean invisible;
    public final float dropChance;
    public final float scale;
    public final float angryScale;
    public final boolean leashable;
    public final boolean steerable;
    public final float width;
    public final float height;
    public final Map<String, Integer> effects;
    public final Map<String, Float> pathPriorities;
    public final boolean egg;
    public final int eggPrimary;
    public final int eggSecondary;
    public final int trackingRange;
    public final int trackingFrequency;
    public final boolean trackVelocity;
    public final Map<String, Double> attributes;
    public final boolean hostile;
    public final boolean passive;
    public final List<String> targets;
    public final boolean persistent;
    public final boolean silent;
    public final boolean picksUpLoot;
    public final boolean hideArmor;
    public final boolean showName;
    public final Map<String, String> equipment;
    public final List<SpawnEntryDef> spawns;
    public final List<String> biomes;
    public final List<String> biomeTypes;
    public final List<String> requires;

    public EntityVariantDef(ResourceLocation registryName, ResourceLocation base, String name, String texture, float jumpMultiplier, float fallDamage, float soundVolume, float soundPitch, float waterSlowdown,
                            int experience, int maxFallHeight, float absorption, String creatureAttribute, boolean breathesUnderwater, boolean despawns,
                            boolean noAI, boolean leftHanded, boolean fireproof, boolean invulnerable, boolean glowing, boolean invisible, float dropChance,
                            float scale, float angryScale, boolean leashable, boolean steerable, float width, float height, Map<String, Integer> effects, Map<String, Float> pathPriorities, boolean egg, int eggPrimary, int eggSecondary,
                            int trackingRange, int trackingFrequency, boolean trackVelocity, Map<String, Double> attributes,
                            boolean hostile, boolean passive, List<String> targets, boolean persistent, boolean silent,
                            boolean picksUpLoot, boolean hideArmor, boolean showName, Map<String, String> equipment, List<SpawnEntryDef> spawns,
                            List<String> biomes, List<String> biomeTypes, List<String> requires) {
        this.registryName = registryName;
        this.base = base;
        this.name = name;
        this.texture = texture;
        this.jumpMultiplier = jumpMultiplier;
        this.fallDamage = fallDamage;
        this.soundVolume = soundVolume;
        this.soundPitch = soundPitch;
        this.waterSlowdown = waterSlowdown;
        this.experience = experience;
        this.maxFallHeight = maxFallHeight;
        this.absorption = absorption;
        this.creatureAttribute = creatureAttribute;
        this.breathesUnderwater = breathesUnderwater;
        this.despawns = despawns;
        this.noAI = noAI;
        this.leftHanded = leftHanded;
        this.fireproof = fireproof;
        this.invulnerable = invulnerable;
        this.glowing = glowing;
        this.invisible = invisible;
        this.dropChance = dropChance;
        this.scale = scale;
        this.angryScale = angryScale;
        this.leashable = leashable;
        this.steerable = steerable;
        this.width = width;
        this.height = height;
        this.effects = effects;
        this.pathPriorities = pathPriorities;
        this.egg = egg;
        this.eggPrimary = eggPrimary;
        this.eggSecondary = eggSecondary;
        this.trackingRange = trackingRange;
        this.trackingFrequency = trackingFrequency;
        this.trackVelocity = trackVelocity;
        this.attributes = attributes;
        this.hostile = hostile;
        this.passive = passive;
        this.targets = targets;
        this.persistent = persistent;
        this.silent = silent;
        this.picksUpLoot = picksUpLoot;
        this.hideArmor = hideArmor;
        this.showName = showName;
        this.equipment = equipment;
        this.spawns = spawns;
        this.biomes = biomes;
        this.biomeTypes = biomeTypes;
        this.requires = requires;
    }
}
