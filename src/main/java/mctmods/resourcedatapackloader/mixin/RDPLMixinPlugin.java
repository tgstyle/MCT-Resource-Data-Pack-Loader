package mctmods.resourcedatapackloader.mixin;

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.io.File;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RDPLMixinPlugin implements IMixinConfigPlugin {
    private static final Set<String> OPTIMIZATIONS = new HashSet<>(Arrays.asList(
            "MixinWorldLight", "MixinChunkLight",
            "MixinBlockState", "MixinBlockStateContainer", "MixinBlockStatePalette", "MixinChunkPrimer",
            "MixinThreadedFileIOBase", "MixinRegionFile", "MixinRegionFileCache",
            "MixinChunkProviderLookup"));
    private static final Set<String> PATHFINDING = new HashSet<>(Arrays.asList("MixinWalkNodeProcessorRaw", "MixinBlockPathNode"));
    private static final Object2BooleanMap<String> RUBIC_CONDITIONS = new Object2BooleanLinkedOpenHashMap<>();
    private static final String CUBIC_CHUNKS_MARKER = "io/github/opencubicchunks/cubicchunks/core/CubicChunks.class";
    private static final String RUBIC_PACKAGE = "mctmods.resourcedatapackloader.mixin.rdpl.";
    private static final String RUBIC_LIGHT_PACKAGE = "mctmods.resourcedatapackloader.mixin.rubiclight.";
    private static Boolean cubicChunks;
    private static boolean rubicLoaded;
    private Boolean lightingReplaced;
    private Boolean pathfindingReplaced;
    private Boolean optimizationsOff;
    private Boolean universalTweaks;

    @Override public void onLoad(String mixinPackage) {
        if (mixinPackage.endsWith(".mixin.rdpl") && !rubicLoaded) {
            rubicLoaded = true;
            if (cubicChunksPresent()) { return; }
            loadRubicGating();
        }
    }

    public static boolean cubicChunksPresent() {
        if (cubicChunks == null) {
            cubicChunks = Launch.classLoader.getResource(CUBIC_CHUNKS_MARKER) != null || inModJars();
            if (cubicChunks) {
                LogManager.getLogger("RDPL").warn("CubicChunks is installed, so it makes the cubic worlds and this mod's own stand down. That pairing is not supported: use one or the other, and please do not report anything about this mod while CubicChunks is installed");
            }
        }
        return cubicChunks;
    }

    private static boolean inModJars() {
        try {
            for (File jar : mctmods.resourcedatapackloader.util.ModJars.list()) {
                try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(jar)) {
                    if (zip.getEntry(CUBIC_CHUNKS_MARKER) != null) { return true; }
                }
                catch (Exception unreadable) { LogManager.getLogger("RDPL").debug("Could not open {} while looking for CubicChunks", jar.getName()); }
            }
            return false;
        }
        catch (Exception failed) {
            LogManager.getLogger("RDPL").warn("Could not scan the mods folder for CubicChunks, assuming it is absent", failed);
            return false;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) private static void loadRubicGating() {
        boolean optifine;
        String optifineVersion = System.getProperty("rdpl.rubic.optifineVersion", null);
        if (optifineVersion == null) {
            try {
                Class optifineInstallerClass = Class.forName("optifine.Installer");
                Method getVersionHandler = optifineInstallerClass.getMethod("getOptiFineVersion");
                optifineVersion = (String) getVersionHandler.invoke(null, new Object[0]);
                optifineVersion = optifineVersion.replace("_pre", "");
                optifineVersion = optifineVersion.substring(optifineVersion.length() - 2);
                LogManager.getLogger("RDPL").info("Detected Optifine version: {}", optifineVersion);
            }
            catch (ClassNotFoundException e) { optifineVersion = null; }
            catch (Exception e) {
                LogManager.getLogger("RDPL").error("Optifine detected, but could not detect version. It may not work. Assuming Optifine E1...", e);
                optifineVersion = "E1";
            }
        }
        optifine = optifineVersion != null;
        if (optifine && optifineVersion.compareTo("E1") < 0) { throw new RuntimeException("OptiFine " + optifineVersion + " is not supported. Use OptiFine 1.12.2 HD U G5."); }
        if (optifine && optifineVersion.compareTo("G5") > 0) { LogManager.getLogger("RDPL").error("Unknown optifine version: {}, it may not work. Assuming E1-G5.", optifineVersion); }
        RUBIC_CONDITIONS.defaultReturnValue(true);
        String selectable = "mctmods.resourcedatapackloader.mixin.rdpl.";
        RUBIC_CONDITIONS.put(selectable + "client.MixinRenderGlobalNoOptifine", !optifine);
        RUBIC_CONDITIONS.put(selectable + "client.MixinRenderGlobalOptifineE", optifine);
        RUBIC_CONDITIONS.put(selectable + "client.MixinRenderChunkOptifine", optifine);
        RUBIC_CONDITIONS.put(selectable + "client.MixinRenderChunkUtils", optifine);
        RUBIC_CONDITIONS.put(selectable + "client.MixinExtendedBlockStorage", optifine);
        RUBIC_CONDITIONS.put(selectable + "client.MixinRenderList", optifine);
        RUBIC_CONDITIONS.put(selectable + "client.MixinViewFrustumOptifine", optifine);
        RUBIC_CONDITIONS.put(selectable + "client.MixinChunkVisibility", optifine);
        RUBIC_CONDITIONS.put(selectable + "client.IGuiVideoSettings", !optifine);
        boolean betterFpsBeacon = false;
        try {
            Class betterFpsConditions = Class.forName("guichaguri.betterfps.transformers.Conditions");
            Method getFixSetting = betterFpsConditions.getMethod("shouldPatch", String.class);
            betterFpsBeacon = (boolean) getFixSetting.invoke(null, "fastBeacon");
        }
        catch (Exception ignored) {}
        RUBIC_CONDITIONS.put(selectable + "common.MixinTileEntityBeaconBetterFps", betterFpsBeacon);
    }

    @Override public String getRefMapperConfig() { return null; }

    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if ((mixinClassName.startsWith(RUBIC_PACKAGE) || mixinClassName.startsWith(RUBIC_LIGHT_PACKAGE)) && cubicChunksPresent()) { return false; }
        if (RUBIC_CONDITIONS.containsKey(mixinClassName)) { return RUBIC_CONDITIONS.getBoolean(mixinClassName); }
        String simple = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        switch (simple) {
            case "MixinBlockCactus":
            case "MixinBlockReed":
                return standDown(simple, universalTweaksPresent());
            case "MixinBlockGrassPath":
                return standDown(simple, universalTweaksPresent() && tweakOn("Lenient Paths"));
            case "MixinBlockLeaves":
                return standDown(simple, universalTweaksPresent() && tweakOn("Fast Leaf Decay"));
        }
        if (PATHFINDING.contains(simple)) {
            if (pathfindingReplaced == null) {
                pathfindingReplaced = Launch.classLoader.getResource("me/jellysquid/mods/lithium/common/ai/pathing/PathNodeCache.class") != null
                        || (Launch.classLoader.getResource("me/elephant1214/paperfixes/mixin/common/lithium/path_node_cache/WalkNodeProcessorMixin.class") != null
                        && settingOn());
                if (pathfindingReplaced) { LogManager.getLogger("RDPL").info("Another mod already works out what the blocks under a path mean, so that half of the pathfinding fast path is standing down for it. What this mod remembers between lookups stays on"); }
            }
            if (pathfindingReplaced) { return false; }
        }
        if (OPTIMIZATIONS.contains(simple)) {
            if (optimizationsOff == null) {
                optimizationsOff = optimizationsDisabled();
                if (optimizationsOff) { LogManager.getLogger("RDPL").warn("disableOptimizations is set, so every pregen and generation optimization is standing down. Why would you do this?"); }
            }
            if (optimizationsOff) { return false; }
        }
        if (!mixinClassName.endsWith(".MixinWorldLight") && !mixinClassName.endsWith(".MixinChunkLight")) { return true; }
        if (lightingReplaced == null) {
            lightingReplaced = Launch.classLoader.getResource("dev/redstudio/alfheim/mixin/WorldMixin.class") != null
                    || Launch.classLoader.getResource("me/jellysquid/mods/phosphor/mod/PhosphorMod.class") != null;
            if (lightingReplaced) { LogManager.getLogger("RDPL").info("Another mod has taken over the light engine, so the pregeneration lighting fast path is standing down for it"); }
        }
        return !lightingReplaced;
    }

    private static boolean standDown(String simple, boolean taken) {
        if (taken) { LogManager.getLogger("RDPL").info("Universal Tweaks already covers this, so {} is left out", simple); }
        return !taken;
    }

    private boolean universalTweaksPresent() {
        if (universalTweaks == null) { universalTweaks = Launch.classLoader.getResource("mod/acgaming/universaltweaks/UniversalTweaks.class") != null; }
        return universalTweaks;
    }

    private static boolean settingOn() { return cfgFlag("paperfixes.cfg", "B:pathNodeCache=", true); }

    private static boolean tweakOn(String name) { return cfgFlag("Universal Tweaks - Tweaks.cfg", "B:\"" + name + "\"=", true); }

    private static boolean optimizationsDisabled() { return cfgFlag("mct_resourcedatapackloader_mixin.cfg", "B:disableOptimizations=", false); }

    private static boolean cfgFlag(String file, String prefix, boolean fallback) {
        try {
            File held = new File(Launch.minecraftHome, "config/" + file);
            if (!held.isFile()) { return fallback; }
            for (String line : Files.readAllLines(held.toPath(), StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.startsWith(prefix)) { return trimmed.endsWith("true"); }
            }
        }
        catch (Exception ignored) {}
        return fallback;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override public List<String> getMixins() { return null; }

    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
