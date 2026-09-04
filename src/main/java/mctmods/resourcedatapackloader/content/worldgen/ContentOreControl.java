package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.*;

public final class ContentOreControl {
    public static final String MINECRAFT = "minecraft";
    public static final String UNKNOWN = "unknown";
    private static final TemplateMemo<int[]> SCOPE = new TemplateMemo<>();
    private static final TemplateMemo<Boolean> SCOPE_BLACKLIST = new TemplateMemo<>();
    private static final ThreadLocal<String> PACK = new ThreadLocal<>();
    private static final Blocked BLOCKED = new Blocked();
    private static final Set<String> REPORTED = new HashSet<>();
    private static Set<String> whitelist;
    private static Set<String> oreTypes;

    private ContentOreControl() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.ORES)) { return false; }
        return ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres) || ContentControl.list(ContentControl.ORES, "oreTypes", Config.worldgen.oreTypes).length > 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true) public static void onGenerateMinable(OreGenEvent.GenerateMinable event) {
        if (oreTypes == null) { load(); }
        if (outsideScope(event.getWorld().provider.getDimension())) { return; }
        if (typeBlocked(event.getType().name())) {
            deny(event, event.getType().name(), owner(event.getGenerator()));
            return;
        }
        if (!ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres)) { return; }
        String owner = owner(event.getGenerator());
        if (whitelist.contains(owner)) { return; }
        deny(event, event.getType().name(), owner);
    }

    private static boolean outsideScope(int dimension) {
        int[] allowed = SCOPE.get(() -> ContentControl.numbers(ContentControl.ORES, "blockOreDimensions", Config.worldgen.blockOreDimensions));
        if (allowed.length == 0) { return false; }
        return listed(allowed, dimension) == SCOPE_BLACKLIST.get(() -> ContentControl.flag(ContentControl.ORES, "blockOreDimensionsAreBlacklist", Config.worldgen.blockOreDimensionsAreBlacklist));
    }

    private static boolean listed(int[] allowed, int dimension) {
        for (int wanted : allowed) {
            if (wanted == dimension) { return true; }
        }
        return false;
    }

    private static boolean typeBlocked(String type) {
        if (oreTypes.isEmpty()) { return false; }
        return oreTypes.contains(type) == ContentControl.flag(ContentControl.ORES, "oreTypesAreBlacklist", Config.worldgen.oreTypesAreBlacklist);
    }

    private static void deny(OreGenEvent.GenerateMinable event, String type, String owner) {
        event.setResult(Event.Result.DENY);
        denied(type, owner);
    }

    public static boolean blocks(String type, String owner, int dimension) {
        if (!enabled()) { return false; }
        if (oreTypes == null) { load(); }
        if (outsideScope(dimension)) { return false; }
        if (typeBlocked(type)) { return true; }
        if (!ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres)) { return false; }
        return !whitelist.contains(owner);
    }

    public static void denied(String type, String owner) {
        String key = owner + " " + type;
        BLOCKED.count(key);
        if (ContentControl.flag(ContentControl.ORES, "logBlockedOres", Config.worldgen.logBlockedOres) && REPORTED.add(key)) {
            ContentLog.LOGGER.info("Blocking {} ore generation from {}. Use /rdplserver oregen to see running totals", type, owner);
        }
    }

    public static Map<String, Integer> blocked() { return BLOCKED.map(); }

    private static void load() {
        whitelist = Settings.lower(ContentControl.list(ContentControl.ORES, "oreWhitelist", Config.worldgen.oreWhitelist));
        oreTypes = new LinkedHashSet<>();
        for (String name : ContentControl.list(ContentControl.ORES, "oreTypes", Config.worldgen.oreTypes)) { oreTypes.add(name.toUpperCase(Locale.ROOT)); }
        if (ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres)) {
            Summary.info("oregen", "Blocking ore generation except from " + (whitelist.isEmpty() ? "nothing" : whitelist));
        }
        if (!oreTypes.isEmpty()) { Summary.info("oregen.types", (ContentControl.flag(ContentControl.ORES, "oreTypesAreBlacklist", Config.worldgen.oreTypesAreBlacklist) ? "Blocking these ore types outright: " : "Allowing only these ore types to generate: ") + oreTypes); }
        if (!ContentControl.flag(ContentControl.ORES, "blockOres", Config.worldgen.blockOres) && !whitelist.contains(MINECRAFT)) {
            ContentLog.LOGGER.warn("oreWhitelist leaves out {}, but blockOres is off, so nothing is being blocked by mod. Turn blockOres on for the whitelist to mean anything", MINECRAFT);
        }
    }

    public static void beginPack(String namespace) { PACK.set(namespace); }

    public static void endPack() { PACK.remove(); }

    private static String owner(WorldGenerator generator) {
        String pack = PACK.get();
        if (pack != null) { return pack; }
        if (generator == null) { return UNKNOWN; }
        return ContentGeneratorControl.owner(generator.getClass());
    }
}
