package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.core.util.ConfigLate;
import mctmods.resourcedatapackloader.core.util.EarlyConfig;
import mctmods.resourcedatapackloader.core.util.ModJars;
import mctmods.resourcedatapackloader.core.util.WriteBoxWatch;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.zip.ZipFile;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

public class RDPLMixinPlugin implements IMixinConfigPlugin {
    private static final List<String> GATED_CONFIGS = Arrays.asList("rdpl", "rubiclight", "galacticraft", "vanillagrowth", "vanillatweaks");
    private static final List<String> SIDES = Arrays.asList("mixins", "client", "server");
    private static final Set<String> OPTIMIZATIONS = new HashSet<>(Arrays.asList(
            "MixinWorldLight", "MixinChunkLight",
            "MixinBlockState", "MixinBlockStateContainer", "MixinChunkPrimer",
            "MixinThreadedFileIOBase", "MixinRegionFile", "MixinRegionFileCache",
            "MixinChunkProviderLookup"));
    private static final Set<String> LIGHT = new HashSet<>(Arrays.asList("MixinWorldLight", "MixinChunkLight"));
    private static final Set<String> PATHFINDING = new HashSet<>(Arrays.asList("MixinWalkNodeProcessorRaw", "MixinBlockPathNode"));
    private static final Map<String, Predicate<String>> GATES = gates();
    private static final Object2BooleanMap<String> RUBIC_CONDITIONS = new Object2BooleanLinkedOpenHashMap<>();
    private static final String CUBIC_CHUNKS_MARKER = "io/github/opencubicchunks/cubicchunks/core/CubicChunks.class";
    private static final String RUBIC_NO_OPTIFINE = "MixinRenderGlobalNoOptifine";
    private static final String RUBIC_BETTER_FPS = "MixinTileEntityBeaconBetterFps";
    private static final Set<String> RUBIC_OPTIFINE = new HashSet<>(Arrays.asList(
            "MixinRenderGlobalOptifineE", "MixinRenderChunkOptifine", "MixinRenderChunkUtils", "MixinExtendedBlockStorage",
            "MixinRenderList", "MixinVboRenderList", "MixinViewFrustumOptifine", "MixinChunkVisibility"));
    private static final Set<String> RUBIC = new HashSet<>(Arrays.asList(
            "IASMEventHandler", "IBlockAccessMinMaxHeight", "IEventBus", "IGameRegistry", "MixinAnvilSaveHandler", "MixinBiomeDecorator",
            "MixinBiomeTemperatureConfig", "MixinBlockBeaconAsyncUpdate", "MixinBlockChorusFlower", "MixinBlockFallingHeightLimits", "MixinBlockGrass",
            "MixinBlockLilyPadHeightLimits", "MixinBlockMushroom", "MixinBlockPistonBaseHeightFix", "MixinBlockPortalSizeHeightLimits",
            "MixinBlockSkullWitherSpawnLimits", "MixinBlockStaticLiquid", "MixinChunk", "MixinChunkCache", "MixinChunkVisibility",
            "MixinCommandBase", "MixinCommandClone", "MixinCommandCompare", "MixinCommandFill", "MixinCommandTeleport", "MixinCommandTP", "MixinDebugRenderChunkBorder",
            "MixinDedicatedServerHeightLimits", "MixinDragonFightManagerRubic", "MixinEmptyChunk", "MixinEntityBat", "MixinEntityBoat", "MixinEntityDragon", "MixinEntityFallingBlock", "MixinEntityLivingBaseRubic",
            "MixinEntityMinecartKillFix", "MixinEntityPlayerSP", "MixinEntityRendererRubic", "MixinEntityRubic", "MixinEntityTracker", "MixinEntityTrackerEntry",
            "MixinExtendedBlockStorage", "MixinForgeChunkManager", "MixinForgeEventFactory", "MixinGuiOverlayDebug", "MixinItemChorusFruit", "MixinItemMap",
            "MixinMinecraftServer", "MixinNetHandlerPlayServer", "MixinPacketBufferBlockPosWrite", "MixinPathNavigateGround",
            "MixinPhaseHoldingPattern", "MixinPhaseLanding", "MixinPhaseLandingApproach", "MixinPhaseTakeoff",
            "MixinPlayerList", "MixinRandomPositionGenerator", "MixinRegionFileCacheRubic", "MixinRenderChunk", "MixinRenderChunkOptifine",
            "MixinRenderChunkUtils", "MixinRenderGlobal", "MixinRenderGlobalNoOptifine", "MixinRenderGlobalOptifineE", "MixinRenderGlobalVertViewDistance",
            "MixinRenderList", "MixinRenderWorker", "MixinStructureStartRubic", "MixinTeleporterRubic", "MixinTerrainGen", "MixinTicket",
            "MixinTileEntityBeacon", "MixinTileEntityBeaconBetterFps", "MixinTileEntityEndGateway", "MixinTileEntityStructure", "MixinVboRenderList", "MixinViewFrustum",
            "MixinViewFrustumOptifine", "MixinWalkNodeProcessorRubic", "MixinWorld", "MixinWorldClient",
            "MixinWorldEntitySpawner", "MixinWorldGenBigMushroom", "MixinWorldGenBirchTree", "MixinWorldGenBlockBlobRubic", "MixinWorldGenBonusChest", "MixinWorldGenCanopyTree",
            "MixinWorldGenDeadBush", "MixinWorldGenDesertWells", "MixinWorldGenDoublePlants", "MixinWorldGenDungeonsRubic", "MixinWorldGenFlowers",
            "MixinWorldGenHugeTrees", "MixinWorldGenIcePath", "MixinWorldGenIceSpike", "MixinWorldGenLakesRubic", "MixinWorldGenSavannaTree", "MixinWorldGenShrub", "MixinWorldGenSpikes",
            "MixinWorldGenSwamp", "MixinWorldGenTaiga1", "MixinWorldGenTaiga2", "MixinWorldGenTallGrass", "MixinWorldGenTrees",
            "MixinWorldProvider", "MixinWorldServer", "MixinWorldSettings",
            "MixinChunkLightRubic", "MixinWorldLightRubic", "MixinChunkCacheLightRubic",
            "MixinBlockVec3", "MixinThreadFindSeal", "MixinTransformerHooksCamera"));
    private static Boolean cubicChunks;
    private static boolean rubicLoaded;
    private static Boolean lightingReplaced;
    private static Boolean sponge;
    private static Boolean pathfindingReplaced;
    private static Boolean paperSpawn;
    private static Boolean optimizationsOff;
    private static Boolean universalTweaks;

    @Override public void onLoad(String mixinPackage) {
        if (rubicLoaded) { return; }
        checkGateNames();
        rubicLoaded = true;
        if (!cubicChunksPresent()) { loadRubicGating(); }
    }

    private static Map<String, Predicate<String>> gates() {
        Map<String, Predicate<String>> gates = new HashMap<>();
        gates.put("MixinChunkWriteBox", name -> WriteBoxWatch.watching());
        gates.put("MixinBlockCactus", name -> standDown(name, universalTweaksPresent()));
        gates.put("MixinBlockReed", name -> standDown(name, universalTweaksPresent()));
        gates.put("MixinBlockGrassPath", name -> standDown(name, universalTweaksPresent() && tweakOn("Lenient Paths")));
        gates.put("MixinBlockLeaves", name -> standDown(name, universalTweaksPresent() && tweakOn("Fast Leaf Decay")));
        gates.put("MixinMinecraftServerBoot", name -> !paperSpawnRule());
        gates.put("MixinMinecraftServerPaperBoot", name -> paperSpawnRule());
        gates.put("MixinWorldServerWeather", name -> !spongePresent());
        gates.put("MixinNetHandlerPlayServerReach", name -> !spongePresent());
        return gates;
    }

    private static void checkGateNames() {
        Set<String> stale = new TreeSet<>(RUBIC);
        stale.addAll(RUBIC_OPTIFINE);
        stale.add(RUBIC_NO_OPTIFINE);
        stale.add(RUBIC_BETTER_FPS);
        stale.addAll(OPTIMIZATIONS);
        stale.addAll(LIGHT);
        stale.addAll(PATHFINDING);
        stale.addAll(GATES.keySet());
        for (String config : GATED_CONFIGS) { stale.removeAll(listedMixins("mixins.resourcedatapackloader." + config + ".json")); }
        if (!stale.isEmpty()) { throw gateError("The mixin plugin gates names that no mixin config lists: " + stale); }
    }

    private static Set<String> listedMixins(String file) {
        InputStream in = MixinService.getService().getResourceAsStream(file);
        if (in == null) { throw gateError("The mixin plugin could not find " + file); }
        Set<String> listed = new HashSet<>();
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            for (String side : SIDES) {
                if (!json.has(side)) { continue; }
                for (JsonElement entry : json.getAsJsonArray(side)) {
                    String name = entry.getAsString();
                    listed.add(name.substring(name.lastIndexOf('.') + 1));
                }
            }
        }
        catch (IOException failed) { throw gateError("The mixin plugin could not read " + file); }
        return listed;
    }

    private static Error gateError(String message) {
        MCTMixin.LOGGER.fatal(message);
        return new Error(message);
    }

    public static boolean cubicChunksPresent() {
        if (cubicChunks == null) {
            cubicChunks = Launch.classLoader.getResource(CUBIC_CHUNKS_MARKER) != null || inModJars();
            if (cubicChunks) { MCTMixin.LOGGER.warn("CubicChunks is installed, so it makes the cubic worlds and this mod's rubic worlds stand down. The rest of this mod still runs"); }
        }
        return cubicChunks;
    }

    private static boolean inModJars() {
        try {
            for (File jar : ModJars.list()) {
                try (ZipFile zip = new ZipFile(jar)) {
                    if (zip.getEntry(CUBIC_CHUNKS_MARKER) != null) { return true; }
                }
                catch (Exception unreadable) { MCTMixin.LOGGER.debug("Could not open {} while looking for CubicChunks", jar.getName()); }
            }
            return false;
        }
        catch (Exception failed) {
            MCTMixin.LOGGER.warn("Could not scan the mods folder for CubicChunks, assuming it is absent", failed);
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
                optifineVersion = optifineVersion.replaceAll("_pre\\d*$", "");
                optifineVersion = optifineVersion.substring(optifineVersion.length() - 2);
                MCTMixin.LOGGER.info("Detected Optifine version: {}", optifineVersion);
            }
            catch (ClassNotFoundException ignored) {}
            catch (Exception e) {
                MCTMixin.LOGGER.error("Optifine detected, but could not detect version. It may not work. Assuming Optifine E1...", e);
                optifineVersion = "E1";
            }
        }
        optifine = optifineVersion != null;
        if (optifine && optifineVersion.compareTo("E1") < 0) { throw new RuntimeException("OptiFine " + optifineVersion + " is not supported. Use OptiFine 1.12.2 HD U G5."); }
        if (optifine && optifineVersion.compareTo("G5") > 0) { MCTMixin.LOGGER.error("Unknown optifine version: {}, it may not work. Assuming E1-G5.", optifineVersion); }
        RUBIC_CONDITIONS.defaultReturnValue(true);
        RUBIC_CONDITIONS.put(RUBIC_NO_OPTIFINE, !optifine);
        for (String name : RUBIC_OPTIFINE) { RUBIC_CONDITIONS.put(name, optifine); }
        boolean betterFpsBeacon = false;
        try {
            Class betterFpsConditions = Class.forName("guichaguri.betterfps.transformers.Conditions");
            Method getFixSetting = betterFpsConditions.getMethod("shouldPatch", String.class);
            betterFpsBeacon = (boolean) getFixSetting.invoke(null, "fastBeacon");
        }
        catch (Exception ignored) {}
        RUBIC_CONDITIONS.put(RUBIC_BETTER_FPS, betterFpsBeacon);
    }

    @Override public String getRefMapperConfig() { return null; }

    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String simple = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        if (RUBIC.contains(simple) && cubicChunksPresent()) { return false; }
        if (RUBIC_CONDITIONS.containsKey(simple)) { return RUBIC_CONDITIONS.getBoolean(simple); }
        Predicate<String> gate = GATES.get(simple);
        if (gate != null) { return gate.test(simple); }
        if (PATHFINDING.contains(simple)) {
            if (pathfindingReplaced == null) {
                pathfindingReplaced = Launch.classLoader.getResource("me/jellysquid/mods/lithium/common/ai/pathing/PathNodeCache.class") != null
                        || (Launch.classLoader.getResource("me/elephant1214/paperfixes/mixin/common/lithium/path_node_cache/WalkNodeProcessorMixin.class") != null
                        && settingOn());
                if (pathfindingReplaced) { MCTMixin.LOGGER.info("Another mod already works out what the blocks under a path mean, so that half of the pathfinding fast path is standing down for it. What this mod remembers between lookups stays on"); }
            }
            if (pathfindingReplaced) { return false; }
        }
        if (OPTIMIZATIONS.contains(simple)) {
            if (optimizationsOff == null) {
                optimizationsOff = optimizationsDisabled();
                if (optimizationsOff) { MCTMixin.LOGGER.warn("disableOptimizations is set, so every pregen and generation optimization is standing down. Why would you do this?"); }
            }
            if (optimizationsOff) { return false; }
        }
        if (!LIGHT.contains(simple)) { return true; }
        if (lightingReplaced == null) {
            lightingReplaced = Launch.classLoader.getResource("dev/redstudio/alfheim/mixin/WorldMixin.class") != null
                    || Launch.classLoader.getResource("me/jellysquid/mods/phosphor/mod/PhosphorMod.class") != null
                    || Launch.classLoader.getResource("com/sumirelabs/pulsar/light/WorldLightManager.class") != null
                    || spongePresent() || cubicChunksPresent();
            if (lightingReplaced) { MCTMixin.LOGGER.info("Another mod has taken over the light engine, so the pregeneration lighting fast path is standing down for it"); }
        }
        return !lightingReplaced;
    }

    private static boolean spongePresent() {
        if (sponge == null) { sponge = Launch.classLoader.getResource("org/spongepowered/mod/SpongeMod.class") != null; }
        return sponge;
    }

    private static boolean standDown(String simple, boolean taken) {
        if (taken) { MCTMixin.LOGGER.info("Universal Tweaks already covers this, so {} is left out", simple); }
        return !taken;
    }

    private static boolean universalTweaksPresent() {
        if (universalTweaks == null) { universalTweaks = Launch.classLoader.getResource("mod/acgaming/universaltweaks/UniversalTweaks.class") != null; }
        return universalTweaks;
    }

    private static boolean paperSpawnRule() {
        if (paperSpawn == null) {
            paperSpawn = Launch.classLoader.getResource("me/elephant1214/paperfixes/mixin/common/world/spawn_chunk_gr/MinecraftServerMixin.class") != null
                    && cfgFlag("paperfixes.cfg", "B:spawnChunkGamerule=", true);
            if (paperSpawn) { MCTMixin.LOGGER.info("PaperFixes already sets the spawn area size, so spawnChunkRadius reaches the world start another way: it decides when the pack sets it, and PaperFixes' spawnChunkRadius game rule decides otherwise"); }
        }
        return paperSpawn;
    }

    private static boolean settingOn() { return cfgFlag("paperfixes.cfg", "B:pathNodeCache=", true); }

    private static boolean tweakOn(String name) { return cfgFlag("Universal Tweaks - Tweaks.cfg", "B:\"" + name + "\"=", true); }

    private static boolean optimizationsDisabled() { return cfgFlag(ConfigLate.FILE, "B:disableOptimizations=", false); }

    private static boolean cfgFlag(String file, String prefix, boolean fallback) { return EarlyConfig.flag(Launch.minecraftHome, file, prefix, fallback); }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override public List<String> getMixins() { return null; }

    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
