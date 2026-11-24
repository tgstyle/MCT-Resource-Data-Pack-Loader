package mctmods.resourcedatapackloader.common.worldgen.feature;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.worldgen.TemplateMemo;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentGeneratorControl;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.Blocked;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.text.Listing;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentOreControl {
    private static final TemplateMemo<Set<Integer>> SCOPE = new TemplateMemo<>();
    private static final TemplateMemo<Boolean> SCOPE_BLACKLIST = new TemplateMemo<>();
    private static final ThreadLocal<String> PACK = new ThreadLocal<>();
    private static final Blocked BLOCKED = new Blocked();
    private static final Set<String> REPORTED = new HashSet<>();
    private static Set<String> whitelist;
    private static Set<String> oreTypes;

    private ContentOreControl() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.ORES)) { return false; }
        return blockOres() || ContentControl.list(ContentControl.ORES, "oreTypes", Config.worldgen.oreTypes).length > 0;
    }

    private static boolean blockOres() { return ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres); }

    private static boolean oreTypesAreBlacklist() { return ContentControl.flag(ContentControl.ORES, "oreTypesAreBlacklist", Config.worldgen.oreTypesAreBlacklist); }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true) public static void onGenerateMinable(OreGenEvent.GenerateMinable event) {
        String type = event.getType().name();
        if (allows(type, () -> owner(event.getGenerator()), event.getWorld().provider.getDimension())) { return; }
        deny(event, type, owner(event.getGenerator()));
    }

    private static boolean allows(String type, Supplier<String> owner, int dimension) {
        if (oreTypes == null) { load(); }
        if (outsideScope(dimension)) { return true; }
        if (typeBlocked(type)) { return false; }
        if (!blockOres()) { return true; }
        return whitelist.contains(owner.get());
    }

    private static boolean outsideScope(int dimension) {
        Set<Integer> allowed = SCOPE.get(() -> ContentControl.numberSet(ContentControl.ORES, "blockOreDimensions", Config.worldgen.blockOreDimensions));
        if (allowed.isEmpty()) { return false; }
        return Listing.blocks(allowed.contains(dimension), SCOPE_BLACKLIST.get(() -> ContentControl.flag(ContentControl.ORES, "blockOreDimensionsAreBlacklist", Config.worldgen.blockOreDimensionsAreBlacklist)));
    }

    private static boolean typeBlocked(String type) {
        if (oreTypes.isEmpty()) { return false; }
        return Listing.blocks(oreTypes.contains(type), oreTypesAreBlacklist());
    }

    private static void deny(OreGenEvent.GenerateMinable event, String type, String owner) {
        event.setResult(Event.Result.DENY);
        denied(type, owner);
    }

    public static boolean passes(String type, String owner, int dimension) {
        return !enabled() || allows(type, () -> owner, dimension);
    }

    public static void denied(String type, String owner) {
        String key = owner + " " + type;
        BLOCKED.count(key);
        if (ContentControl.flag(ContentControl.ORES, "logBlockedOres", Config.worldgen.logBlockedOres) && REPORTED.add(key)) { ContentLog.LOGGER.info("Blocking {} ore generation from {}. Use /rdplserver oregen to see running totals", type, owner); }
    }

    public static Map<String, Integer> blocked() { return BLOCKED.map(); }

    private static void load() {
        whitelist = Settings.lower(ContentControl.list(ContentControl.ORES, "oreWhitelist", Config.worldgen.oreWhitelist));
        oreTypes = new LinkedHashSet<>();
        for (String name : ContentControl.list(ContentControl.ORES, "oreTypes", Config.worldgen.oreTypes)) { oreTypes.add(name.toUpperCase(Locale.ROOT)); }
        if (blockOres()) { Summary.info("oregen", "Blocking ore generation except from " + (whitelist.isEmpty() ? "nothing" : whitelist)); }
        if (!oreTypes.isEmpty()) { Summary.info("oregen.types", (oreTypesAreBlacklist() ? "Blocking these ore types outright: " : "Allowing only these ore types to generate: ") + oreTypes); }
        if (!blockOres() && !whitelist.contains(IdNames.VANILLA)) { ContentLog.LOGGER.warn("oreWhitelist leaves out {}, but blockOres is off, so nothing is being blocked by mod. Turn blockOres on for the whitelist to mean anything", IdNames.VANILLA); }
    }

    public static void beginPack(String namespace) { PACK.set(namespace); }

    public static void endPack() { PACK.remove(); }

    private static String owner(WorldGenerator generator) {
        String pack = PACK.get();
        if (pack != null) { return pack; }
        if (generator == null) { return ContentGeneratorControl.UNKNOWN; }
        return ContentGeneratorControl.owner(generator.getClass());
    }
}
