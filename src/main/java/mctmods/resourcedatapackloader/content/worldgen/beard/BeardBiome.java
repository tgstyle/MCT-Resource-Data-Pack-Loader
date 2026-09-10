package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class BeardBiome {
    private static final Map<Biome, String> SECTIONS = new HashMap<>();
    private static final ThreadLocal<String> BUILDING = new ThreadLocal<>();
    private static final ThreadLocal<BlockPos.MutableBlockPos> LOOKUP = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    private static Object sectionsFrom;

    private BeardBiome() {}

    public static void enter(@Nullable World world, int x, int z) {
        if (ContentControl.hasBiomeSettings()) { BUILDING.set(at(world, x, z)); }
    }

    public static void leave() { BUILDING.remove(); }

    public static boolean moved(@Nullable World world, int x, int z) {
        if (!ContentControl.hasBiomeSettings()) { return false; }
        String here = at(world, x, z);
        if (java.util.Objects.equals(here, BUILDING.get())) { return false; }
        BUILDING.set(here);
        return true;
    }

    @Nullable public static String building() { return BUILDING.get(); }

    @Nullable private static String at(@Nullable World world, int x, int z) {
        if (world == null) { return null; }
        BlockPos.MutableBlockPos at = LOOKUP.get();
        at.setPos(x, 0, z);
        return sectionFor(world.getBiome(at));
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
