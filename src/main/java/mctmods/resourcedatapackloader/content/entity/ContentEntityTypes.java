package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentEntityTypes {
    private ContentEntityTypes() {}

    public static void register(IForgeRegistry<EntityEntry> registry) {
        if (!ContentEntities.load()) { return; }
        int made = 0;
        int network = 0;
        for (Map.Entry<ResourceLocation, EntityVariantDef> entry : ContentEntities.DEFS.entrySet()) {
            EntityVariantDef def = entry.getValue();
            EntityEntry base = Registries.find(ForgeRegistries.ENTITIES, def.base);
            if (base == null) {
                ContentLog.LOGGER.error("Entity variant {} is based on {}, which nothing registers, leaving it out", entry.getKey(), def.base);
                continue;
            }
            Class<? extends Entity> made$class = EntityClassMaker.make(base.getEntityClass(), entry.getKey().getNamespace() + "_" + entry.getKey().getPath(), def.ignoresSpawnRules, def.hostile);
            if (made$class == null) { continue; }
            EntityEntryBuilder<Entity> builder = EntityEntryBuilder.create();
            builder.entity(made$class).id(entry.getKey(), network++)
                    .name(entry.getKey().getNamespace() + "." + entry.getKey().getPath())
                    .tracker(def.trackingRange, def.trackingFrequency, def.trackVelocity);
            if (def.egg) { builder.egg(eggColor(def, true), eggColor(def, false)); }
            registry.register(builder.build());
            ContentEntities.BY_CLASS.put(made$class, def);
            addSpawns(made$class, def);
            made++;
            ContentLog.LOGGER.debug("Entity variant {} read from the pack with attributes {} and equipment {}", entry.getKey(), def.attributes, def.equipment);
        }
        if (throwsReturning()) { registry.register(EntityEntryBuilder.create().entity(EntityReturningThrow.class).id(new ResourceLocation(ResourceDataPackLoader.MOD_ID, "returning_throw"), network).name(ResourceDataPackLoader.MOD_ID + ".returning_throw").tracker(64, 1, true).build()); }
        if (made > 0) { Summary.info("entities.registered", "Registered " + made + " entity variant(s) from packs"); }
    }

    private static boolean throwsReturning() {
        for (EntityVariantDef def : ContentEntities.DEFS.values()) {
            if (def.throwsItems && def.throwReturns) { return true; }
        }
        return false;
    }

    private static void addSpawns(Class<? extends Entity> type, EntityVariantDef def) {
        if (def.spawns.isEmpty() || !EntityLiving.class.isAssignableFrom(type)) { return; }
        List<Biome> biomes = biomes(def);
        if (biomes.isEmpty()) { return; }
        for (SpawnEntryDef entry : def.spawns) {
            net.minecraft.entity.EnumCreatureType creature = creatureType(entry.creatureType);
            if (creature == null) {
                ContentLog.LOGGER.error("Entity variant {} has spawn type '{}', which is not one of monster, creature, ambient or water", def.registryName, entry.creatureType);
                continue;
            }
            EntityRegistry.addSpawn(type.asSubclass(EntityLiving.class), entry.weight, entry.min, entry.max, creature, biomes.toArray(new Biome[0]));
        }
    }

    private static List<Biome> biomes(EntityVariantDef def) {
        List<Biome> found = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (matches(biome, def)) { found.add(biome); }
        }
        if (found.isEmpty()) { ContentLog.LOGGER.error("Entity variant {} names biomes nothing matches, so it will not spawn on its own", def.registryName); }
        return found;
    }

    private static boolean matches(Biome biome, EntityVariantDef def) {
        if (def.biomes.isEmpty() && def.biomeTypes.isEmpty()) { return true; }
        ResourceLocation name = biome.getRegistryName();
        for (String wanted : def.biomes) {
            if (name != null && wanted.equalsIgnoreCase(name.toString())) { return true; }
            if (wanted.equalsIgnoreCase(ContentBiomeControl.shownName(biome))) { return true; }
        }
        for (String wanted : def.biomeTypes) {
            for (net.minecraftforge.common.BiomeDictionary.Type type : net.minecraftforge.common.BiomeDictionary.getTypes(biome)) {
                if (type.getName().equalsIgnoreCase(wanted)) { return true; }
            }
        }
        return false;
    }

    private static int eggColor(EntityVariantDef def, boolean primary) {
        int wanted = primary ? def.eggPrimary : def.eggSecondary;
        if (wanted >= 0) { return wanted; }
        EntityList.EntityEggInfo info = EntityList.ENTITY_EGGS.get(def.base);
        if (info == null) { return primary ? 0xFFFFFF : 0x808080; }
        return primary ? info.primaryColor : info.secondaryColor;
    }

    @Nullable private static net.minecraft.entity.EnumCreatureType creatureType(String name) { return Enums.byName(net.minecraft.entity.EnumCreatureType.class, name); }
}
