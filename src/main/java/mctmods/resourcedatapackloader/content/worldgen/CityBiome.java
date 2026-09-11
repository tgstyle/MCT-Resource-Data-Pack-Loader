package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public final class CityBiome {
    private static final Map<ResourceLocation, String> SECTIONS = new HashMap<>();
    private static final ThreadLocal<BlockPos.MutableBlockPos> LOOKUP = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    @Nullable private static Object sectionsFrom;

    private CityBiome() {}

    public static void enter(WorldGenLevel level, int x, int z) {
        if (ContentControl.hasBiomeSettings()) { ContentControl.enterSection(at(level, x, z)); }
    }

    public static void leave() { ContentControl.leaveSection(); }

    public static boolean moved(WorldGenLevel level, int x, int z) {
        if (!ContentControl.hasBiomeSettings()) { return false; }
        String here = at(level, x, z);
        if (Objects.equals(here, ContentControl.buildingSection())) { return false; }
        ContentControl.enterSection(here);
        return true;
    }

    @Nullable private static String at(WorldGenLevel level, int x, int z) {
        BlockPos.MutableBlockPos at = LOOKUP.get();
        at.set(x, level.getLevel().getChunkSource().getGenerator().getSeaLevel(), z);
        return sectionFor(level.getBiome(at));
    }

    @Nullable private static synchronized String sectionFor(Holder<Biome> biome) {
        ResourceLocation named = biome.unwrapKey().map(ResourceKey::location).orElse(null);
        if (named == null) { return null; }
        Object mark = ContentControl.biomeSettingsMark();
        if (mark != sectionsFrom) {
            SECTIONS.clear();
            sectionsFrom = mark;
        }
        String held = SECTIONS.get(named);
        if (held != null) { return held.isEmpty() ? null : held; }
        String found = matched(biome, named);
        SECTIONS.put(named, found == null ? "" : found);
        if (found != null) { ContentLog.LOGGER.debug("The biome {} takes the village settings under biomes.{}", named, found); }
        else { ContentLog.LOGGER.debug("The biome {} matches no biomes section, so it builds with the plain village settings. It would answer to {}, {}, or a biome type it carries as a tag", named, named, named.getPath()); }
        return found;
    }

    @Nullable private static String matched(Holder<Biome> biome, ResourceLocation named) {
        if (ContentControl.hasBiomeSection(named.toString())) { return named.toString(); }
        if (ContentControl.hasBiomeSection(named.getPath())) { return named.getPath(); }
        for (String section : ContentControl.biomeSectionNames()) {
            if (section.contains(":")) {
                ResourceLocation tag = ResourceLocation.tryParse(section.startsWith("#") ? section.substring(1) : section);
                if (tag != null && biome.is(TagKey.create(Registries.BIOME, tag))) { return section; }
                continue;
            }
            String mapped = ContentFormats.biomeTag(section.toLowerCase(Locale.ROOT));
            ResourceLocation tag = mapped == null ? null : ResourceLocation.tryParse(mapped);
            if (tag != null && biome.is(TagKey.create(Registries.BIOME, tag))) { return section; }
        }
        return null;
    }
}
