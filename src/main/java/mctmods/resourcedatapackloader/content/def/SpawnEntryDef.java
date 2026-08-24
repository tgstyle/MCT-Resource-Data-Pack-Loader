package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.Locale;
import javax.annotation.Nullable;


public final class SpawnEntryDef {
    public final String creatureType;
    public final String entity;
    public final int weight;
    public final int min;
    public final int max;

    public SpawnEntryDef(String creatureType, String entity, int weight, int min, int max) {
        this.creatureType = creatureType;
        this.entity = entity;
        this.weight = weight;
        this.min = min;
        this.max = max;
    }

    @Nullable public static EnumCreatureType creatureType(String name) {
        String wanted = name.trim().replace("_", "").toLowerCase(Locale.ROOT);
        if ("water".equals(wanted)) { return EnumCreatureType.WATER_CREATURE; }
        for (EnumCreatureType type : EnumCreatureType.values()) {
            if (type.name().replace("_", "").toLowerCase(Locale.ROOT).equals(wanted)) { return type; }
        }
        return null;
    }

    @Nullable public static Class<? extends EntityLiving> living(String owner, Object named, String name) {
        ResourceLocation location = new ResourceLocation(name);
        if (!ForgeRegistries.ENTITIES.containsKey(location)) {
            ContentLog.LOGGER.error("{} {} names spawn entity '{}', which is not registered, skipping that entry", owner, named, name);
            return null;
        }
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(location);
        if (entry == null) { return null; }
        Class<?> type = entry.getEntityClass();
        if (!EntityLiving.class.isAssignableFrom(type)) {
            ContentLog.LOGGER.error("{} {} names spawn entity '{}', which is not a living entity, skipping that entry", owner, named, name);
            return null;
        }
        return type.asSubclass(EntityLiving.class);
    }
}
