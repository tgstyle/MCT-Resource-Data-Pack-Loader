package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.def.ExposureDef;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ExposureSpread {
    private static final ResourceLocation PLAYER = new ResourceLocation("minecraft", "player");
    private static final String CAUGHT = "RDPLCaught";
    private static final String CAUGHT_UNTIL = "RDPLCaughtUntil";
    private static final Map<ExposureDef, Map<Class<?>, Integer>> CARRIER_LEVELS = new IdentityHashMap<>();
    private static final Map<ExposureDef, Map<Class<?>, Boolean>> CATCHERS = new IdentityHashMap<>();

    private ExposureSpread() {}

    public static int carrierLevel(ExposureDef def, EntityLivingBase living) {
        if (def.carriers.isEmpty()) { return 0; }
        return CARRIER_LEVELS.computeIfAbsent(def, held -> {
            warnUnknown(held, held.carriers.keySet(), "carrier");
            return new IdentityHashMap<>();
        }).computeIfAbsent(living.getClass(), type -> def.carriers.getOrDefault(id(living), 0));
    }

    public static boolean catches(ExposureDef def, EntityLivingBase living) {
        return CATCHERS.computeIfAbsent(def, held -> {
            warnUnknown(held, held.catchers, "catcher");
            return new IdentityHashMap<>();
        }).computeIfAbsent(living.getClass(), type -> def.catchers.contains(id(living)));
    }

    public static int caughtLevel(ExposureDef def, EntityLivingBase living) {
        NBTTagCompound data = living.getEntityData();
        String tag = CAUGHT + def.name;
        if (!data.hasKey(tag)) { return 0; }
        if (data.getLong(CAUGHT_UNTIL + def.name) > living.world.getTotalWorldTime()) { return data.getInteger(tag); }
        data.removeTag(tag);
        data.removeTag(CAUGHT_UNTIL + def.name);
        return 0;
    }

    public static void spread(ExposureDef def, EntityLivingBase source, int level) {
        if (level <= 0) { return; }
        World world = source.world;
        double reach = def.contagionRange;
        long until = world.getTotalWorldTime() + def.contagionDuration;
        for (EntityLivingBase near : world.getEntitiesWithinAABB(EntityLivingBase.class, source.getEntityBoundingBox().grow(reach), near -> near != null && near != source && near.isEntityAlive())) {
            if (!catches(def, near) || near.getDistanceSq(source) > reach * reach || world.rand.nextFloat() >= def.contagionChance) { continue; }
            NBTTagCompound data = near.getEntityData();
            data.setInteger(CAUGHT + def.name, Math.max(level, caughtLevel(def, near)));
            data.setLong(CAUGHT_UNTIL + def.name, until);
        }
    }

    public static int weatherLevel(ExposureDef def, EntityLivingBase living) {
        if (def.weather.isEmpty()) { return 0; }
        if (!def.weatherDimensions.isEmpty() && !def.weatherDimensions.contains(living.dimension)) { return 0; }
        World world = living.world;
        if (!world.isRainingAt(new BlockPos(living.posX, living.posY + living.getEyeHeight(), living.posZ))) { return 0; }
        int level = def.weather.getOrDefault("rain", 0);
        if (world.isThundering()) { level = Math.max(level, def.weather.getOrDefault("thunder", 0)); }
        return level;
    }

    @Nullable private static ResourceLocation id(EntityLivingBase living) { return living instanceof EntityPlayer ? PLAYER : EntityList.getKey(living); }

    private static void warnUnknown(ExposureDef def, Iterable<ResourceLocation> names, String what) {
        for (ResourceLocation name : names) {
            if (!PLAYER.equals(name) && !ForgeRegistries.ENTITIES.containsKey(name)) { ContentLog.LOGGER.error("Exposure {} names entity {} as a {}, which is not registered, so it is ignored", def.registryName, name, what); }
        }
    }
}
