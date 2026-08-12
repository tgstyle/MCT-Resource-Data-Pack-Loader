package mctmods.resourcedatapackloader.mixin;

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RDPLMixinPlugin implements IMixinConfigPlugin {
    private static final Set<String> OPTIMIZATIONS = new HashSet<>(Arrays.asList(
            "MixinWorldLight", "MixinChunkLight", "MixinChunkColumnLight", "MixinChunkDressLight",
            "MixinBlockState", "MixinBlockStateContainer", "MixinBlockStatePalette", "MixinChunkPrimer",
            "MixinThreadedFileIOBase", "MixinRegionFile", "MixinRegionFileCache", "MixinRegionFileWrite",
            "MixinChunkProviderLookup"));
    private static final Set<String> PATHFINDING = new HashSet<>(Arrays.asList("MixinWalkNodeProcessorRaw", "MixinBlockPathNode"));
    private Boolean lightingReplaced;
    private Boolean pathfindingReplaced;
    private Boolean optimizationsOff;
    private Boolean universalTweaks;

    @Override public void onLoad(String mixinPackage) {}

    @Override public String getRefMapperConfig() { return null; }

    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
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
        if (!mixinClassName.endsWith(".MixinWorldLight") && !mixinClassName.endsWith(".MixinChunkLight") && !mixinClassName.endsWith(".MixinChunkColumnLight") && !mixinClassName.endsWith(".MixinChunkDressLight")) { return true; }
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

    private static boolean settingOn() {
        try {
            File held = new File(Launch.minecraftHome, "config/" + "paperfixes.cfg");
            if (!held.isFile()) { return true; }

            String wanted = "B:" + "pathNodeCache" + "=";
            for (String line : Files.readAllLines(held.toPath(), StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.startsWith(wanted)) { return trimmed.endsWith("true"); }
            }
        }
        catch (Exception ignored) {}
        return true;
    }

    private static boolean tweakOn(String name) {
        try {
            File held = new File(Launch.minecraftHome, "config/Universal Tweaks - Tweaks.cfg");
            if (!held.isFile()) { return true; }

            String wanted = "B:\"" + name + "\"=";
            for (String line : Files.readAllLines(held.toPath(), StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.startsWith(wanted)) { return trimmed.endsWith("true"); }
            }
        }
        catch (Exception ignored) {}
        return true;
    }

    private static boolean optimizationsDisabled() {
        try {
            File held = new File(Launch.minecraftHome, "config/mct_resourcedatapackloader_mixin.cfg");
            if (!held.isFile()) { return false; }

            for (String line : Files.readAllLines(held.toPath(), StandardCharsets.UTF_8)) {
                if (line.trim().equals("B:disableOptimizations=true")) { return true; }
            }
        }
        catch (Exception ignored) {}
        return false;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override public List<String> getMixins() { return null; }

    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
