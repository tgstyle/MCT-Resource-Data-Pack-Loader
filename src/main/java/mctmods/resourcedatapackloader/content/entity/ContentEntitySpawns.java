package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.port.Ids;
import mctmods.resourcedatapackloader.util.BiomeNames;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ContentEntitySpawns() implements BiomeModifier {
    public static final String ID = "entity_spawns";
    public static final MapCodec<ContentEntitySpawns> CODEC = MapCodec.unit(new ContentEntitySpawns());
    private static final Map<String, String> SHOWN = Map.<String, String>ofEntries(
            Map.entry("ocean", "minecraft:ocean"), Map.entry("plains", "minecraft:plains"), Map.entry("desert", "minecraft:desert"), Map.entry("extreme hills", "minecraft:extreme_hills"),
            Map.entry("forest", "minecraft:forest"), Map.entry("taiga", "minecraft:taiga"), Map.entry("swampland", "minecraft:swampland"), Map.entry("river", "minecraft:river"),
            Map.entry("hell", "minecraft:hell"), Map.entry("the end", "minecraft:sky"), Map.entry("frozenocean", "minecraft:frozen_ocean"), Map.entry("frozenriver", "minecraft:frozen_river"),
            Map.entry("ice plains", "minecraft:ice_flats"), Map.entry("ice mountains", "minecraft:ice_mountains"), Map.entry("mushroomisland", "minecraft:mushroom_island"), Map.entry("mushroomislandshore", "minecraft:mushroom_island_shore"),
            Map.entry("beach", "minecraft:beaches"), Map.entry("deserthills", "minecraft:desert_hills"), Map.entry("foresthills", "minecraft:forest_hills"), Map.entry("taigahills", "minecraft:taiga_hills"),
            Map.entry("extreme hills edge", "minecraft:smaller_extreme_hills"), Map.entry("jungle", "minecraft:jungle"), Map.entry("junglehills", "minecraft:jungle_hills"), Map.entry("jungleedge", "minecraft:jungle_edge"),
            Map.entry("deep ocean", "minecraft:deep_ocean"), Map.entry("stone beach", "minecraft:stone_beach"), Map.entry("cold beach", "minecraft:cold_beach"), Map.entry("birch forest", "minecraft:birch_forest"),
            Map.entry("birch forest hills", "minecraft:birch_forest_hills"), Map.entry("roofed forest", "minecraft:roofed_forest"), Map.entry("cold taiga", "minecraft:taiga_cold"), Map.entry("cold taiga hills", "minecraft:taiga_cold_hills"),
            Map.entry("mega taiga", "minecraft:redwood_taiga"), Map.entry("mega taiga hills", "minecraft:redwood_taiga_hills"), Map.entry("extreme hills+", "minecraft:extreme_hills_with_trees"), Map.entry("savanna", "minecraft:savanna"),
            Map.entry("savanna plateau", "minecraft:savanna_rock"), Map.entry("mesa", "minecraft:mesa"), Map.entry("mesa plateau f", "minecraft:mesa_rock"), Map.entry("mesa plateau", "minecraft:mesa_clear_rock"),
            Map.entry("the void", "minecraft:void"), Map.entry("sunflower plains", "minecraft:mutated_plains"), Map.entry("desert m", "minecraft:mutated_desert"), Map.entry("extreme hills m", "minecraft:mutated_extreme_hills"),
            Map.entry("flower forest", "minecraft:mutated_forest"), Map.entry("taiga m", "minecraft:mutated_taiga"), Map.entry("swampland m", "minecraft:mutated_swampland"), Map.entry("ice plains spikes", "minecraft:mutated_ice_flats"),
            Map.entry("jungle m", "minecraft:mutated_jungle"), Map.entry("jungleedge m", "minecraft:mutated_jungle_edge"), Map.entry("birch forest m", "minecraft:mutated_birch_forest"), Map.entry("birch forest hills m", "minecraft:mutated_birch_forest_hills"),
            Map.entry("roofed forest m", "minecraft:mutated_roofed_forest"), Map.entry("cold taiga m", "minecraft:mutated_taiga_cold"), Map.entry("mega spruce taiga", "minecraft:mutated_redwood_taiga"), Map.entry("redwood taiga hills m", "minecraft:mutated_redwood_taiga_hills"),
            Map.entry("extreme hills+ m", "minecraft:mutated_extreme_hills_with_trees"), Map.entry("savanna m", "minecraft:mutated_savanna"), Map.entry("savanna plateau m", "minecraft:mutated_savanna_rock"), Map.entry("mesa (bryce)", "minecraft:mutated_mesa"),
            Map.entry("mesa plateau f m", "minecraft:mutated_mesa_rock"), Map.entry("mesa plateau m", "minecraft:mutated_mesa_clear_rock"));

    public static int generate(Collection<EntityVariantDef> defs) {
        int spawning = 0;
        for (EntityVariantDef def : defs) {
            if (def.spawns().isEmpty()) { continue; }
            spawning++;
            for (SpawnEntryDef entry : def.spawns()) {
                if (category(entry.creatureType()) == null) { ContentLog.LOGGER.error("Entity variant {} has spawn type '{}', which is not one of monster, creature, ambient or water, so that entry adds nothing", def.key(), entry.creatureType()); }
            }
            for (String type : def.biomeTypes()) {
                if (tag(type) == null) { ContentLog.LOGGER.error("Entity variant {} names biome type '{}', which no biome tag on this line answers to", def.key(), type); }
            }
        }
        if (spawning == 0) { return 0; }
        JsonObject modifier = new JsonObject();
        modifier.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + ID);
        GeneratedResources.put(PackType.SERVER_DATA, ResourceDataPackLoader.MOD_ID, ContentFormats.BIOME_MODIFIERS + "/" + ID + ".json", modifier.toString());
        return spawning;
    }

    @Override public void modify(@Nonnull Holder<Biome> biome, @Nonnull Phase phase, @Nonnull ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) { return; }
        ResourceLocation id = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        for (EntityType<?> type : ContentEntities.types().values()) {
            EntityVariantDef def = ContentEntities.def(type);
            if (def == null || def.spawns().isEmpty() || !matches(biome, id, def)) { continue; }
            for (SpawnEntryDef entry : def.spawns()) {
                MobCategory category = category(entry.creatureType());
                if (category != null) { builder.getMobSpawnSettings().addSpawn(category, new MobSpawnSettings.SpawnerData(type, entry.weight(), entry.min(), Math.max(entry.min(), entry.max()))); }
            }
        }
    }

    public static void onServerStarted(ServerStartedEvent event) {
        Registry<Biome> registry = event.getServer().registryAccess().registryOrThrow(Registries.BIOME);
        for (EntityType<?> type : ContentEntities.types().values()) {
            EntityVariantDef def = ContentEntities.def(type);
            if (def == null || def.spawns().isEmpty()) { continue; }
            if (registry.holders().noneMatch(holder -> matches(holder, holder.key().location(), def))) { ContentLog.LOGGER.error("Entity variant {} names biomes nothing matches, so it will not spawn on its own", def.key()); }
        }
    }

    @Override @Nonnull public MapCodec<? extends BiomeModifier> codec() { return CODEC; }

    private static boolean matches(Holder<Biome> biome, @Nullable ResourceLocation id, EntityVariantDef def) {
        if (def.biomes().isEmpty() && def.biomeTypes().isEmpty()) { return true; }
        String named = id == null ? "" : id.toString();
        for (String wanted : def.biomes()) {
            if (named.equals(biomeId(wanted))) { return true; }
        }
        if (id != null && BiomeNames.named(id, def.biomes())) { return true; }
        for (String type : def.biomeTypes()) {
            TagKey<Biome> tag = tag(type);
            if (tag != null && biome.is(tag)) { return true; }
        }
        return false;
    }

    private static String biomeId(String wanted) {
        String asked = wanted.trim().toLowerCase(Locale.ROOT);
        String legacy = SHOWN.get(asked);
        return Ids.biome(legacy == null ? asked : legacy);
    }

    @Nullable private static TagKey<Biome> tag(String type) {
        String named = ContentFormats.biomeTag(type);
        ResourceLocation id = named == null ? null : ResourceLocation.tryParse(named.startsWith("#") ? named.substring(1) : named);
        return id == null ? null : TagKey.create(Registries.BIOME, id);
    }

    @Nullable private static MobCategory category(String name) {
        if ("water".equals(name)) { return MobCategory.WATER_CREATURE; }
        for (MobCategory category : MobCategory.values()) {
            if (category.getName().equalsIgnoreCase(name)) { return category; }
        }
        return null;
    }
}
