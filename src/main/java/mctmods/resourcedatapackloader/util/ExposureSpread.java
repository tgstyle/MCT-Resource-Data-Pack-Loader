package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.ExposureDef;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ExposureSpread {
    private static final String CAUGHT = "RDPLCaught";
    private static final String CAUGHT_UNTIL = "RDPLCaughtUntil";
    private static final Map<ExposureDef, Map<EntityType<?>, Integer>> CARRIER_LEVELS = new IdentityHashMap<>();
    private static final Map<ExposureDef, Map<EntityType<?>, Boolean>> CATCHERS = new IdentityHashMap<>();

    private ExposureSpread() {}

    public static int carrierLevel(ExposureDef def, LivingEntity living) {
        if (def.carriers().isEmpty()) { return 0; }
        return CARRIER_LEVELS.computeIfAbsent(def, held -> {
            warnUnknown(held, held.carriers().keySet(), "carrier");
            return new IdentityHashMap<>();
        }).computeIfAbsent(living.getType(), type -> def.carriers().getOrDefault(ForgeRegistries.ENTITY_TYPES.getKey(type), 0));
    }

    public static boolean catches(ExposureDef def, LivingEntity living) {
        return CATCHERS.computeIfAbsent(def, held -> {
            warnUnknown(held, held.catchers(), "catcher");
            return new IdentityHashMap<>();
        }).computeIfAbsent(living.getType(), type -> def.catchers().contains(ForgeRegistries.ENTITY_TYPES.getKey(type)));
    }

    public static int caughtLevel(ExposureDef def, LivingEntity living) {
        CompoundTag data = living.getPersistentData();
        String tag = CAUGHT + def.name();
        if (!data.contains(tag)) { return 0; }
        if (data.getLong(CAUGHT_UNTIL + def.name()) > living.level().getGameTime()) { return data.getInt(tag); }
        data.remove(tag);
        data.remove(CAUGHT_UNTIL + def.name());
        return 0;
    }

    public static void spread(ExposureDef def, LivingEntity source, int reached) {
        if (reached <= 0) { return; }
        Level level = source.level();
        double reach = def.contagionRange();
        long until = level.getGameTime() + def.contagionDuration();
        for (LivingEntity near : level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(reach), near -> near != source && near.isAlive())) {
            if (!catches(def, near) || near.distanceToSqr(source) > reach * reach || level.random.nextFloat() >= def.contagionChance()) { continue; }
            CompoundTag data = near.getPersistentData();
            data.putInt(CAUGHT + def.name(), Math.max(reached, caughtLevel(def, near)));
            data.putLong(CAUGHT_UNTIL + def.name(), until);
        }
    }

    public static int weatherLevel(ExposureDef def, LivingEntity living) {
        if (def.weather().isEmpty()) { return 0; }
        Level level = living.level();
        if (!def.weatherDimensions().isEmpty() && !def.weatherDimensions().contains(level.dimension().location())) { return 0; }
        if (!level.isRainingAt(BlockPos.containing(living.getX(), living.getEyeY(), living.getZ()))) { return 0; }
        int reached = def.weather().getOrDefault("rain", 0);
        if (level.isThundering()) { reached = Math.max(reached, def.weather().getOrDefault("thunder", 0)); }
        return reached;
    }

    private static void warnUnknown(ExposureDef def, Iterable<ResourceLocation> names, String what) {
        for (ResourceLocation name : names) {
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(name)) { ContentLog.LOGGER.error("Exposure {} names entity {} as a {}, which is not registered, so it is ignored", def.key(), name, what); }
        }
    }
}
