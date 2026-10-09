package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.BiomeDictionary;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public final class BeardBiome {
    private static final Map<Biome, String> SECTIONS = new HashMap<>();
    private static final ThreadLocal<String> BUILDING = new ThreadLocal<>();
    private static final BlockPos.MutableBlockPos LOOKUP = new BlockPos.MutableBlockPos();
    private static Object sectionsFrom;

    private BeardBiome() {}

    private static void enter(@Nullable World world, int x, int z) { if (ContentControl.hasBiomeSettings()) { BUILDING.set(at(world, x, z)); } }

    public static void within(@Nullable World world, StructureBoundingBox clip, Runnable laid) { within(world, (clip.minX + clip.maxX) / 2, (clip.minZ + clip.maxZ) / 2, laid); }

    public static void within(@Nullable World world, int x, int z, Runnable laid) {
        enter(world, x, z);
        try { laid.run(); }
        finally { BUILDING.remove(); }
    }

    public static boolean moved(@Nullable World world, int x, int z) {
        if (!ContentControl.hasBiomeSettings()) { return false; }
        String here = at(world, x, z);
        if (Objects.equals(here, BUILDING.get())) { return false; }
        BUILDING.set(here);
        return true;
    }

    @Nullable public static String building() { return BUILDING.get(); }

    @Nullable private static String at(@Nullable World world, int x, int z) {
        if (world == null) { return null; }
        LOOKUP.setPos(x, 0, z);
        return sectionFor(world.getBiome(LOOKUP));
    }

    @Nullable private static synchronized String sectionFor(@Nullable Biome biome) {
        if (biome == null) { return null; }
        Object mark = ContentControl.biomeSettingsMark();
        if (mark != sectionsFrom) {
            SECTIONS.clear();
            sectionsFrom = mark;
        }
        String held = SECTIONS.get(biome);
        if (held != null) { return held.isEmpty() ? null : held; }
        String found = matched(biome);
        SECTIONS.put(biome, found == null ? "" : found);
        if (ContentLog.LOGGER.debugEnabled()) {
            if (found != null) { ContentLog.LOGGER.debug("The biome {} takes the village settings under biomes.{}", biome.getRegistryName(), found); }
            else { ContentLog.LOGGER.debug("The biome {} matches no biomes section, so it builds with the plain village settings. It would answer to {} or any of {}", biome.getRegistryName(), biome.getRegistryName(), BiomeDictionary.getTypes(biome)); }
        }
        return found;
    }

    @Nullable private static String matched(Biome biome) {
        ResourceLocation named = biome.getRegistryName();
        if (named != null) {
            if (ContentControl.hasBiomeSection(named.toString())) { return named.toString(); }
            if (ContentControl.hasBiomeSection(named.getPath())) { return named.getPath(); }
        }
        for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome)) {
            String asked = type.getName();
            if (ContentControl.hasBiomeSection(asked)) { return asked; }
            String lowered = asked.toLowerCase(Locale.ROOT);
            if (ContentControl.hasBiomeSection(lowered)) { return lowered; }
        }
        return null;
    }
}
