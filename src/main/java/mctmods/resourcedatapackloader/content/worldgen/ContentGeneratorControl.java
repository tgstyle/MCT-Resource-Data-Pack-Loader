package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Names;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.File;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ContentGeneratorControl {
    private static final String UNKNOWN = "unknown";
    private static final Blocked BLOCKED = new Blocked();
    private static final Map<Class<?>, String> OWNERS = new HashMap<>();
    private static final Set<String> REPORTED = new HashSet<>();
    private static Set<String> whitelist;
    private static Set<String> named;
    private static Set<Integer> dimensions;

    private ContentGeneratorControl() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.GENERATORS)) { return false; }

        return ContentControl.flag(ContentControl.GENERATORS, "blockWorldGenerators", Config.worldgen.blockWorldGenerators) || ContentControl.list(ContentControl.GENERATORS, "blockedGenerators", Config.worldgen.blockedGenerators).length > 0;
    }

    public static void load() {
        whitelist = Names.lower(ContentControl.list(ContentControl.GENERATORS, "generatorWhitelist", Config.worldgen.generatorWhitelist));
        named = Names.lower(ContentControl.list(ContentControl.GENERATORS, "blockedGenerators", Config.worldgen.blockedGenerators));
        dimensions = new HashSet<>();
        for (int dimension : ContentControl.numbers(ContentControl.GENERATORS, "blockGeneratorDimensions", Config.worldgen.blockGeneratorDimensions)) { dimensions.add(dimension); }
        BLOCKED.clear();
        REPORTED.clear();

        if (ContentControl.flag(ContentControl.GENERATORS, "blockWorldGenerators", Config.worldgen.blockWorldGenerators)) { Summary.info("generators", "Blocking third party world generation except from " + whitelist); }
        else if (!named.isEmpty()) { Summary.info("generators", "Blocking world generation from " + named); }
    }

    public static boolean rejects(IWorldGenerator generator, World world) {
        if (whitelist == null) { load(); }
        if (generator == null || world == null) { return false; }
        if (!inScope(world)) { return false; }

        String owner = owner(generator.getClass());
        if (owner.equals("resourcedatapackloader")) { return false; }

        String type = generator.getClass().getName().toLowerCase(Locale.ROOT);
        if (!named.isEmpty() && (named.contains(owner) || matches(type))) {
            count(owner, generator);
            return true;
        }
        if (!ContentControl.flag(ContentControl.GENERATORS, "blockWorldGenerators", Config.worldgen.blockWorldGenerators) || whitelist.contains(owner)) { return false; }

        count(owner, generator);
        return true;
    }

    public static void report() {
        if (ContentControl.flag(ContentControl.GENERATORS, "logBlockedGenerators", Config.worldgen.logBlockedGenerators) && BLOCKED.total() > 0) { BLOCKED.report("world generator call(s)"); }
    }

    private static boolean matches(String type) {
        for (String entry : named) {
            if (type.contains(entry)) { return true; }
        }
        return false;
    }

    private static boolean inScope(World world) {
        if (dimensions.isEmpty()) { return true; }

        return dimensions.contains(world.provider.getDimension()) != ContentControl.flag(ContentControl.GENERATORS, "blockGeneratorDimensionsAreBlacklist", Config.worldgen.blockGeneratorDimensionsAreBlacklist);
    }

    private static void count(String owner, IWorldGenerator generator) {
        BLOCKED.count(owner);
        if (!ContentControl.flag(ContentControl.GENERATORS, "logBlockedGenerators", Config.worldgen.logBlockedGenerators)) { return; }
        if (REPORTED.add(owner + "/" + generator.getClass().getName())) {
            ContentLog.LOGGER.info("Blocking world generator {} from {}. Use /rdpl oregen to see running totals", generator.getClass().getSimpleName(), owner);
        }
    }

    private static String owner(Class<?> type) {
        String cached = OWNERS.get(type);
        if (cached != null) { return cached; }

        String owner = resolve(type);
        OWNERS.put(type, owner);
        return owner;
    }

    private static String resolve(Class<?> type) {
        if (type.getName().startsWith("net.minecraft.")) { return "minecraft"; }

        CodeSource source = type.getProtectionDomain() == null ? null : type.getProtectionDomain().getCodeSource();
        if (source == null || source.getLocation() == null) { return UNKNOWN; }

        String location = source.getLocation().getPath();
        for (ModContainer container : Loader.instance().getModList()) {
            File file = container.getSource();
            if (file == null) { continue; }
            if (location.endsWith(file.getName()) || location.contains(file.getName())) { return container.getModId().toLowerCase(Locale.ROOT); }
        }
        return UNKNOWN;
    }
}
