package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentCity {
    public static final String STRUCTURE = "city";
    private static final String OVERWORLD_BIOMES = "#minecraft:is_overworld";
    private static final String DEFAULT_PAVING = "minecraft:dirt_path";
    public static final String SIDEWALK_END = "sidewalk";
    private static final String BARRIER_END = "barrier";
    private static final String EMPTY = "empty";
    private static final int CARGO_MOST = 8;
    private static final long INFESTED_SALT = 0x2031B1E5L;
    private static final int INFESTED_ODDS = 50;
    private static final Set<String> MISSING = new LinkedHashSet<>();
    private static boolean laying;
    @Nullable private static Vec3i stationSpan;

    private ContentCity() {}

    public static boolean wanted() { return ContentControl.flag(ContentControl.STRUCTURES, "terrainAdaptation", Config.worldgen.terrainAdaptation()); }

    public static boolean idle() { return !laying; }

    public static String paving() {
        String named = ContentControl.text(ContentControl.VILLAGES, "villagePathBlock", Config.worldgen.villagePathBlock()).trim();
        return named.isEmpty() ? DEFAULT_PAVING : named;
    }

    public static String paving(boolean alley) {
        if (!alley) { return paving(); }
        String named = ContentControl.text(ContentControl.VILLAGES, "villagePathAlleyBlock", Config.worldgen.villagePathAlleyBlock()).trim();
        return named.isEmpty() ? paving() : named;
    }

    public static boolean pathChosen() { return !ContentControl.text(ContentControl.VILLAGES, "villagePathBlock", Config.worldgen.villagePathBlock()).trim().isEmpty(); }

    public static boolean pavingChosen(boolean alley) { return pathChosen() || (alley && !ContentControl.text(ContentControl.VILLAGES, "villagePathAlleyBlock", Config.worldgen.villagePathAlleyBlock()).trim().isEmpty()); }

    public static String lineBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathLineBlock", Config.worldgen.villagePathLineBlock()).trim(); }

    public static String sidewalkBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathSidewalkBlock", Config.worldgen.villagePathSidewalkBlock()).trim(); }

    public static String centerBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathCenterBlock", Config.worldgen.villagePathCenterBlock()).trim(); }

    public static int centerDash() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathCenterDash", Config.worldgen.villagePathCenterDash())); }

    public static String lampBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathLampBlock", Config.worldgen.villagePathLampBlock()).trim(); }

    public static String lampTopBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathLampTopBlock", Config.worldgen.villagePathLampTopBlock()).trim(); }

    public static String lampSideBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathLampSideBlock", Config.worldgen.villagePathLampSideBlock()).trim(); }

    public static String lampStructure() { return ContentControl.text(ContentControl.VILLAGES, "villagePathLampStructure", Config.worldgen.villagePathLampStructure()).trim(); }

    public static int lampHeight() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathLampHeight", Config.worldgen.villagePathLampHeight())); }

    public static String bridgeBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeBlock", Config.worldgen.villagePathBridgeBlock()).trim(); }

    public static BlockState planks(@Nullable CityPlan plan) {
        String type = plan == null ? "" : plan.villageType();
        if ("savanna".equals(type)) { return Blocks.ACACIA_PLANKS.defaultBlockState(); }
        if ("taiga".equals(type)) { return Blocks.SPRUCE_PLANKS.defaultBlockState(); }
        return Blocks.OAK_PLANKS.defaultBlockState();
    }

    public static BlockState support(@Nullable CityPlan plan) { return CityPalette.stateOr(supportBlock(), plan != null && "desert".equals(plan.villageType()) ? Blocks.SANDSTONE.defaultBlockState() : Blocks.GRAVEL.defaultBlockState()); }

    public static String bridgeSidewalkBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock()).trim(); }

    public static String bridgeBarrierBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock()).trim(); }

    public static int bridgeBarrierHeight() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeBarrierHeight", Config.worldgen.villagePathBridgeBarrierHeight())); }

    public static int bridgeDrop() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeDrop", Config.worldgen.villagePathBridgeDrop())); }

    public static String supportBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathSupportBlock", Config.worldgen.villagePathSupportBlock()).trim(); }

    public static String frameBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeFrameBlock", Config.worldgen.villagePathBridgeFrameBlock()).trim(); }

    public static String frameTopBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeFrameTopBlock", Config.worldgen.villagePathBridgeFrameTopBlock()).trim(); }

    public static int frameHeight() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameHeight", Config.worldgen.villagePathBridgeFrameHeight())); }

    public static int frameRun() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameRun", Config.worldgen.villagePathBridgeFrameRun())); }

    public static int frameLeast() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameLeast", Config.worldgen.villagePathBridgeFrameLeast())); }

    public static String tunnelBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathTunnelBlock", Config.worldgen.villagePathTunnelBlock()).trim(); }

    public static int tunnelDepth() { return tunnelBlock().isEmpty() ? 0 : Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathTunnelDepth", Config.worldgen.villagePathTunnelDepth())); }

    public static String tunnelLightBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathTunnelLightBlock", Config.worldgen.villagePathTunnelLightBlock()).trim(); }

    public static int tunnelLightRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathTunnelLightRun", Config.worldgen.villagePathTunnelLightRun())); }


    public static String railBedBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayBedBlock" : "villageRailBedBlock", sub ? Config.worldgen.villageSubwayBedBlock() : Config.worldgen.villageRailBedBlock()).trim(); }


    public static String railBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayBlock" : "villageRailBlock", sub ? Config.worldgen.villageSubwayBlock() : Config.worldgen.villageRailBlock()).trim(); }


    public static String railTieBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTieBlock" : "villageRailTieBlock", sub ? Config.worldgen.villageSubwayTieBlock() : Config.worldgen.villageRailTieBlock()).trim(); }


    public static String railShoulderBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayShoulderBlock" : "villageRailShoulderBlock", sub ? Config.worldgen.villageSubwayShoulderBlock() : Config.worldgen.villageRailShoulderBlock()).trim(); }


    public static String railPowerBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayPowerBlock" : "villageRailPowerBlock", sub ? Config.worldgen.villageSubwayPowerBlock() : Config.worldgen.villageRailPowerBlock()).trim(); }


    public static String railPowerBase(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayPowerBase" : "villageRailPowerBase", sub ? Config.worldgen.villageSubwayPowerBase() : Config.worldgen.villageRailPowerBase()).trim(); }


    public static boolean trackInBed(BlockState track, boolean sub) {
        String named = ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTrackSeat" : "villageRailTrackSeat", sub ? Config.worldgen.villageSubwayTrackSeat() : Config.worldgen.villageRailTrackSeat()).trim().toLowerCase(Locale.ROOT);
        if (named.isEmpty() || "auto".equals(named)) { return !(track.getBlock() instanceof BaseRailBlock); }
        if (named.startsWith("i")) { return true; }
        if (named.startsWith("o")) { return false; }
        if (MISSING.add("seat|" + named)) { ContentLog.LOGGER.error("villageRailTrackSeat '{}' is not auto, on or in, so the track is seated the way its block asks for", named); }
        return !(track.getBlock() instanceof BaseRailBlock);
    }

    public static boolean railsAlongX(RandomSource roll, boolean sub) {
        String named = ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayDirection" : "villageRailDirection", sub ? Config.worldgen.villageSubwayDirection() : Config.worldgen.villageRailDirection()).trim().toLowerCase(Locale.ROOT);
        if (named.isEmpty() || "any".equals(named)) { return roll.nextBoolean(); }
        if (named.startsWith("e") || named.startsWith("w")) { return true; }
        if (named.startsWith("n") || named.startsWith("s")) { return false; }
        if (MISSING.add("direction|" + named)) { ContentLog.LOGGER.error("villageRailDirection '{}' is not ew, ns or any, so the lines run as they roll", named); }
        return roll.nextBoolean();
    }

    public static int railAskedWidth(boolean sub) { return Math.max(3, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayWidth" : "villageRailWidth", sub ? Config.worldgen.villageSubwayWidth() : Config.worldgen.villageRailWidth())); }

    public static int railTrackCount(boolean sub) {
        int asked = railTracks(sub);
        if (asked > 0) { return asked; }
        return railAskedWidth(sub) >= 5 ? 2 : 1;
    }

    public static int railBed(boolean sub) { return Math.max(railAskedWidth(sub), (railTrackCount(sub) - 1) * railTrackGap(sub) + 3); }

    public static int railBedHalf(boolean sub) { return (railBed(sub) - 1) / 2; }

    public static int stationReach() { return stationsOn() ? outFromLine(railBedHalf(true)) + ContentCityStairsPiece.WIDE + 1 : 0; }


    public static int railTieRun(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTieRun" : "villageRailTieRun", sub ? Config.worldgen.villageSubwayTieRun() : Config.worldgen.villageRailTieRun())); }


    public static int railTracks(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTracks" : "villageRailTracks", sub ? Config.worldgen.villageSubwayTracks() : Config.worldgen.villageRailTracks())); }


    public static int railTrackGap(boolean sub) { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTrackGap" : "villageRailTrackGap", sub ? Config.worldgen.villageSubwayTrackGap() : Config.worldgen.villageRailTrackGap())); }


    public static int railShoulderWidth(boolean sub) { return CityPalette.stateOr(railShoulderBlock(sub), Blocks.AIR.defaultBlockState()).isAir() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayShoulderWidth" : "villageRailShoulderWidth", sub ? Config.worldgen.villageSubwayShoulderWidth() : Config.worldgen.villageRailShoulderWidth())); }


    public static int railPowerRun(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayPowerRun" : "villageRailPowerRun", sub ? Config.worldgen.villageSubwayPowerRun() : Config.worldgen.villageRailPowerRun())); }


    public static int railClimb(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayClimb" : "villageRailClimb", sub ? Config.worldgen.villageSubwayClimb() : Config.worldgen.villageRailClimb())); }

    public static String railSupportBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailSupportBlock", Config.worldgen.villageRailSupportBlock()).trim(); }

    public static String railDeckBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailDeckBlock", Config.worldgen.villageRailDeckBlock()).trim(); }

    public static String railBarrierBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailBarrierBlock", Config.worldgen.villageRailBarrierBlock()).trim(); }

    public static String railFrameBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailBridgeFrameBlock", Config.worldgen.villageRailBridgeFrameBlock()).trim(); }

    public static String railFrameTopBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailBridgeFrameTopBlock", Config.worldgen.villageRailBridgeFrameTopBlock()).trim(); }

    public static int railFrameHeight() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameHeight", Config.worldgen.villageRailBridgeFrameHeight())); }

    public static int railFrameRun() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameRun", Config.worldgen.villageRailBridgeFrameRun())); }

    public static int railFrameLeast() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameLeast", Config.worldgen.villageRailBridgeFrameLeast())); }

    public static String railTunnelBlock() { return railTunnelBlock(false); }

    public static String railTunnelBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTunnelBlock" : "villageRailTunnelBlock", sub ? Config.worldgen.villageSubwayTunnelBlock() : Config.worldgen.villageRailTunnelBlock()).trim(); }

    public static int railTunnelDepth() { return CityPalette.stateOr(railTunnelBlock(), Blocks.AIR.defaultBlockState()).isAir() ? 0 : Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailTunnelDepth", Config.worldgen.villageRailTunnelDepth())); }

    public static int subwayLines() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayLines", Config.worldgen.villageSubwayLines())); }

    public static int subwayDepth() { return Math.max(6, ContentControl.number(ContentControl.VILLAGES, "villageSubwayDepth", Config.worldgen.villageSubwayDepth())); }

    public static boolean subways() { return subwayLines() > 0; }

    public static int stationLength() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationLength", Config.worldgen.villageSubwayStationLength())); }

    public static int stationRun() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationRun", Config.worldgen.villageSubwayStationRun())); }

    public static int platformWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayPlatformWidth", Config.worldgen.villageSubwayPlatformWidth())); }

    public static String platformBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayPlatformBlock", Config.worldgen.villageSubwayPlatformBlock()).trim(); }

    public static boolean stations() { return subways() && stationsOn(); }

    private static boolean stationsOn() { return stationSpan != null && stationLength() > 0 && platformWidth() > 0; }

    @Nullable public static Vec3i stationSpan() { return stationSpan; }

    public static String railingBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayRailingBlock", Config.worldgen.villageSubwayRailingBlock()).trim(); }

    public static String benchBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayBenchBlock", Config.worldgen.villageSubwayBenchBlock()).trim(); }

    public static String benchEndBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayBenchEndBlock", Config.worldgen.villageSubwayBenchEndBlock()).trim(); }

    public static int benchLength() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayBenchLength", Config.worldgen.villageSubwayBenchLength())); }

    public static int subwaySurfaces() { return Mth.clamp(ContentControl.number(ContentControl.VILLAGES, "villageSubwaySurfaces", Config.worldgen.villageSubwaySurfaces()), 0, 100); }

    public static String stationStructure() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayStation", Config.worldgen.villageSubwayStation()).trim(); }

    public static int stationFoot() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationFoot", Config.worldgen.villageSubwayStationFoot())); }

    public static int stationRepeat() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationRepeat", Config.worldgen.villageSubwayStationRepeat())); }

    public static int outFromLine(int bedHalf) { return Math.max((CityPlan.streetFullWidth() - 1) / 2 + 4, bedHalf + platformWidth() + 3); }

    public static String sewerBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerBlock", Config.worldgen.villageSewerBlock()).trim(); }

    public static boolean sewers() { return !sewerBlock().isEmpty(); }

    public static boolean sewerWellEntrance() { return ContentControl.flag(ContentControl.VILLAGES, "villageSewerWellEntrance", Config.worldgen.villageSewerWellEntrance()); }

    public static int sewerDepth() { return Math.max(4, ContentControl.number(ContentControl.VILLAGES, "villageSewerDepth", Config.worldgen.villageSewerDepth())); }

    public static int sewerHeight() { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, "villageSewerHeight", Config.worldgen.villageSewerHeight())); }

    public static int sewerWidth() {
        int asked = Math.max(3, ContentControl.number(ContentControl.VILLAGES, "villageSewerWidth", Config.worldgen.villageSewerWidth()));
        return (asked & 1) == 0 ? asked + 1 : asked;
    }

    public static String sewerWaterBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerWaterBlock", Config.worldgen.villageSewerWaterBlock()).trim(); }

    public static String sewerWalkBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerWalkBlock", Config.worldgen.villageSewerWalkBlock()).trim(); }

    public static String sewerLightBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerLightBlock", Config.worldgen.villageSewerLightBlock()).trim(); }

    public static int sewerLightRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageSewerLightRun", Config.worldgen.villageSewerLightRun())); }

    public static String sewerLadderBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerLadderBlock", Config.worldgen.villageSewerLadderBlock()).trim(); }

    public static String sewerCoverBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerCoverBlock", Config.worldgen.villageSewerCoverBlock()).trim(); }

    public static void ironCover(BlockState cover) {
        if (cover.getBlock().defaultMapColor() != MapColor.METAL || !MISSING.add("cover|iron")) { return; }
        ContentLog.LOGGER.error("villageSewerCoverBlock '{}' is iron, which no player can open by hand, so the manholes will be shut to anyone without a redstone signal", sewerCoverBlock());
    }

    public static String vergeBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathVergeBlock", Config.worldgen.villagePathVergeBlock()).trim(); }

    public static String vergeWaterBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathVergeWaterBlock", Config.worldgen.villagePathVergeWaterBlock()).trim(); }

    public static String sewerMossBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerMossBlock", Config.worldgen.villageSewerMossBlock()).trim(); }

    public static int sewerMossChance() { return Mth.clamp(ContentControl.number(ContentControl.VILLAGES, "villageSewerMossChance", Config.worldgen.villageSewerMossChance()), 0, 100); }

    public static String sewerVineBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerVineBlock", Config.worldgen.villageSewerVineBlock()).trim(); }

    public static int sewerVineChance() { return Mth.clamp(ContentControl.number(ContentControl.VILLAGES, "villageSewerVineChance", Config.worldgen.villageSewerVineChance()), 0, 100); }


    public static String railTunnelLightBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTunnelLightBlock" : "villageRailTunnelLightBlock", sub ? Config.worldgen.villageSubwayTunnelLightBlock() : Config.worldgen.villageRailTunnelLightBlock()).trim(); }


    public static int railTunnelLightRun(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTunnelLightRun" : "villageRailTunnelLightRun", sub ? Config.worldgen.villageSubwayTunnelLightRun() : Config.worldgen.villageRailTunnelLightRun())); }

    @Nullable public static String wellStructure(RandomSource roll) {
        String picked = PickDef.pick(weighted("villageWellStructure", ContentControl.list(ContentControl.VILLAGES, "villageWellStructure", Config.worldgen.villageWellStructure())), roll);
        return picked == null || picked.isEmpty() || EMPTY.equals(picked) ? null : picked;
    }

    private static List<PickDef> weighted(String key, List<String> entries) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : entries) {
            String text = entry.trim();
            if (text.isEmpty()) { continue; }
            int at = text.indexOf('=');
            String name = at <= 0 ? "" : text.substring(0, at).trim();
            String weight = at <= 0 ? "" : text.substring(at + 1).trim();
            if (name.isEmpty() || weight.isEmpty()) {
                if (MISSING.add(key + "|" + entry)) { ContentLog.LOGGER.error("{} entry '{}' is not written as name=weight, ignoring it", key, entry); }
                continue;
            }
            int asked;
            try { asked = Integer.parseInt(weight); }
            catch (NumberFormatException notNumber) {
                if (MISSING.add(key + "|" + entry)) { ContentLog.LOGGER.error("{} entry '{}' gives a weight of '{}', which is not a whole number, ignoring the entry", key, entry, weight); }
                continue;
            }
            if (asked < 1) {
                if (MISSING.add(key + "|" + entry)) { ContentLog.LOGGER.error("{} entry '{}' asks for a weight of {}, which is below 1, ignoring the entry", key, entry, asked); }
                continue;
            }
            picks.add(new PickDef(name, asked));
        }
        return picks;
    }

    public static TerrainAdjustment adaptation() {
        TerrainAdjustment held = adjustment(villageMode());
        return held == null ? TerrainAdjustment.BEARD_THIN : held;
    }

    public static boolean encapsulates() { return "encapsulate".equals(villageMode()); }

    @Nullable private static String villageMode() {
        String found = null;
        for (String entry : ContentControl.list(ContentControl.STRUCTURES, "structureAdaptation", Config.worldgen.structureAdaptation())) {
            int at = entry.indexOf('=');
            if (at <= 0 || !entry.substring(0, at).trim().equalsIgnoreCase("villages")) { continue; }
            String mode = entry.substring(at + 1).trim().toLowerCase(Locale.ROOT);
            if (adjustment(mode) != null) { found = mode; }
        }
        return found;
    }

    @Nullable private static TerrainAdjustment adjustment(@Nullable String mode) {
        if (mode == null) { return null; }
        String named = ContentFormats.adaptation(mode);
        for (TerrainAdjustment held : TerrainAdjustment.values()) {
            if (held.getSerializedName().equals(named)) { return held; }
        }
        return null;
    }

    @Nullable public static String deadEnd(RandomSource roll, boolean paved, boolean railed) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villagePathDeadEnds", Config.worldgen.villagePathDeadEnds())) {
            String text = entry.trim();
            if (!(SIDEWALK_END.equals(text) && paved) && !(BARRIER_END.equals(text) && railed)) { continue; }
            picks.add(new PickDef(text, 1));
        }
        String picked = PickDef.pick(picks, roll);
        return picked == null || picked.isEmpty() ? null : picked;
    }

    public record Cargo(String name, int height) {}

    public static String pierLoot() { return ContentControl.text(ContentControl.VILLAGES, "villagePathPierLoot", Config.worldgen.villagePathPierLoot()).trim(); }

    @Nullable public static String pierStyle(RandomSource roll) {
        List<PickDef> picks = new ArrayList<>();
        for (String style : pierStyles()) { picks.add(new PickDef(style, 1)); }
        return PickDef.pick(picks, roll);
    }

    public static Set<String> pierStyles() {
        Set<String> styles = new LinkedHashSet<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villagePathPiers", Config.worldgen.villagePathPiers())) {
            String text = entry.trim().toLowerCase(Locale.ROOT);
            if (text.isEmpty()) { continue; }
            if (!ContentCityPierPiece.RAILED.equals(text) && !ContentCityPierPiece.PILINGS.equals(text) && !ContentCityPierPiece.BOARDWALK.equals(text)) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villagePathPiers names style '{}', which is not railed, pilings or boardwalk, so it is left out", entry); }
                continue;
            }
            styles.add(text);
        }
        return styles;
    }

    @Nullable public static Cargo pierCargo(RandomSource roll) {
        List<Cargo> stood = new ArrayList<>();
        String picked = PickDef.pick(cargo(stood), roll);
        if (picked == null) { return null; }
        Cargo held = stood.get(Integer.parseInt(picked));
        return held.name().isEmpty() ? null : held;
    }

    private static List<PickDef> cargo(List<Cargo> stood) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villagePathPierCargo", Config.worldgen.villagePathPierCargo())) {
            String text = entry.trim();
            int at = text.lastIndexOf('=');
            if (at <= 0) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villagePathPierCargo entry '{}' is not written as block=weight, so it is left out", entry); }
                continue;
            }
            String named = text.substring(0, at).trim();
            String rest = text.substring(at + 1).trim();
            int comma = rest.indexOf(',');
            int height = 1;
            if (comma >= 0) {
                height = cargoHeight(named, rest.substring(comma + 1).trim());
                rest = rest.substring(0, comma).trim();
            }
            int weight;
            try { weight = Integer.parseInt(rest); }
            catch (NumberFormatException notNumber) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villagePathPierCargo entry '{}' carries a weight that is not a number, so it is left out", entry); }
                continue;
            }
            if (weight < 1) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villagePathPierCargo entry '{}' asks for a weight of {}, which is below 1, ignoring the entry", entry, weight); }
                continue;
            }
            picks.add(new PickDef(Integer.toString(stood.size()), weight));
            stood.add(new Cargo("empty".equals(named) ? "" : named, height));
        }
        return picks;
    }

    private static int cargoHeight(String named, String written) {
        int asked;
        try { asked = Integer.parseInt(written); }
        catch (NumberFormatException wrong) {
            if (MISSING.add("cargo|" + named + "|" + written)) { ContentLog.LOGGER.error("villagePathPierCargo gives {} a height of '{}', which is not a whole number, so one block is stood there", named, written); }
            return 1;
        }
        if (asked >= 1 && asked <= CARGO_MOST) { return asked; }
        if (MISSING.add("cargo|" + named + "|" + written)) { ContentLog.LOGGER.error("villagePathPierCargo gives {} a height of {}, which is not between 1 and {}, so one block is stood there", named, asked, CARGO_MOST); }
        return 1;
    }

    public static List<String> decorNames() { return ContentControl.list(ContentControl.VILLAGES, "villageDecor", Config.worldgen.villageDecor()); }

    @Nullable public static String decor(RandomSource roll) {
        String picked = PickDef.pick(weighted("villageDecor", decorNames()), roll);
        return picked == null || picked.isEmpty() || EMPTY.equals(picked) ? null : picked;
    }

    public static void missingDecor(String named) {
        if (MISSING.add(named)) { ContentLog.LOGGER.error("villageDecor names '{}', which no pack registers as worldgen, so nothing is scattered for it", named); }
    }

    public static String layoutNamed() { return ContentControl.text(ContentControl.VILLAGES, "villageLayout", Config.worldgen.villageLayout()).trim(); }

    @Nullable public static CityMapDef layout() {
        String named = layoutNamed();
        if (named.isEmpty()) { return null; }
        CityMapDef def = ContentCityMaps.byName(named);
        if (def == null && MISSING.add(named)) { ContentLog.LOGGER.error("villageLayout names city map '{}', which no pack provides, so districts are planned as usual", named); }
        return def;
    }

    public static void begin() {
        ContentCityBlocks.forget();
        CityPalette.forget();
        ContentStates.forget();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) { ContentVillages.joinVillages(server.registryAccess()); }
        stationSpan = null;
        laying = wanted();
        if (!laying) { return; }
        int cargo = cargo(new ArrayList<>()).size();
        if (cargo > 0) { ContentLog.LOGGER.info("Piers carry {} kind(s) of cargo", cargo); }
        int decor = weighted("villageDecor", decorNames()).size();
        if (decor > 0) { ContentLog.LOGGER.info("Villages scatter {} kind(s) of decoration along their roads", decor); }
        StructureTemplateManager templates = server == null ? null : server.getStructureManager();
        stationSpan = ContentCityTemplates.stationBuild(templates);
        ContentVillages.measure(templates);
        ContentVillages.vanilla(server == null ? Map.of() : ContentCityTemplates.vanillaHouses(server, templates));
        CityPlan.reset();
        int spacing = CityPlan.spacing();
        if (spacing <= 0) {
            Summary.info("city", "terrainAdaptation is on but villageCitySpacing is 0, so no city districts are seeded");
            laying = false;
            return;
        }
        CityMapDef map = layout();
        if (map != null) {
            Summary.info("city", "Laying city map " + map.key() + ": " + map.cellsWide() + "x" + map.cellsDeep() + " cells of " + map.cell() + " blocks, one city in every " + spacing + " square, paved with " + paving());
            return;
        }
        Summary.info("city", "Laying city streets: one district of " + CityPlan.district() + " blocks in every " + spacing + " square, paved with " + paving());
    }

    public static void residents(WorldGenLevel level, VillageDef def, BoundingBox box, IntFunction<BlockPos> spot) {
        if (def.villagers() <= 0) { return; }
        EntityType<?> kind = EntityType.VILLAGER;
        boolean vanilla = def.villagerEntity().isEmpty();
        if (!vanilla) {
            kind = EntityType.byString(def.villagerEntity()).orElse(null);
            if (kind == null) {
                if (MISSING.add(def.key() + "|" + def.villagerEntity())) { ContentLog.LOGGER.error("Village plot {} wants {} to live in it, which nothing registers", def.key(), def.villagerEntity()); }
                return;
            }
        }
        int districtX = CityPlan.districtOf(box.minX(), true);
        int districtZ = CityPlan.districtOf(box.minZ(), false);
        boolean infested = vanilla && Math.floorMod(Hashes.mix(level.getSeed() ^ INFESTED_SALT, districtX, 0, districtZ), INFESTED_ODDS) == 0;
        for (int index = 0; index < def.villagers(); index++) {
            BlockPos at = spot.apply(index);
            if (!box.isInside(at)) { continue; }
            Entity made = (infested ? EntityType.ZOMBIE_VILLAGER : kind).create(level.getLevel());
            if (made == null) { return; }
            made.moveTo(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D, 0.0F, 0.0F);
            if (made instanceof Mob mob) { ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(at), MobSpawnType.STRUCTURE, null, null); }
            if (vanilla) { ContentVillagers.professed(made, level.getRandom()); }
            if (infested && made instanceof Mob mob) { mob.setPersistenceRequired(); }
            level.addFreshEntity(made);
        }
    }

    public static void missing(VillageDef def) {
        if (MISSING.add(def.key().toString())) { ContentLog.LOGGER.error("Village plot {} names structure '{}', which could not be loaded, so its plots stay empty", def.key(), def.structure()); }
    }

    public static void missingLamp(String named) {
        if (MISSING.add(named)) { ContentLog.LOGGER.error("villagePathLampStructure names '{}', which could not be loaded, so the lamp blocks are stacked instead", named); }
    }

    public static void missingStation(String named) {
        if (MISSING.add(named)) { ContentLog.LOGGER.error("villageSubwayStation '{}' could not be loaded, so no subway stations are built", named); }
    }

    public static void missingWell(String named) {
        if (MISSING.add(named)) { ContentLog.LOGGER.error("villageWellStructure names '{}', which could not be loaded, so the plaza is left bare", named); }
    }

    public static void generate() {
        JsonObject structure = new JsonObject();
        structure.addProperty("type", ResourceDataPackLoader.MOD_ID + ":" + STRUCTURE);
        structure.addProperty("biomes", OVERWORLD_BIOMES);
        structure.addProperty("step", "surface_structures");
        structure.add("spawn_overrides", new JsonObject());
        structure.addProperty("terrain_adaptation", "beard_thin");
        GeneratedResources.put(PackType.SERVER_DATA, ResourceDataPackLoader.MOD_ID, "worldgen/structure/" + STRUCTURE + ".json", structure.toString());

        GeneratedResources.put(PackType.SERVER_DATA, ResourceDataPackLoader.MOD_ID, "worldgen/structure_set/" + STRUCTURE + ".json", set().toString());
    }

    private static JsonObject set() {
        JsonObject entry = new JsonObject();
        entry.addProperty("structure", ResourceDataPackLoader.MOD_ID + ":" + STRUCTURE);
        entry.addProperty("weight", 1);
        JsonArray structures = new JsonArray();
        structures.add(entry);
        JsonObject placement = new JsonObject();
        placement.addProperty("type", "minecraft:random_spread");
        placement.addProperty("spacing", 1);
        placement.addProperty("separation", 0);
        placement.addProperty("salt", 0x0C17E5);
        JsonObject set = new JsonObject();
        set.add("placement", placement);
        set.add("structures", structures);
        return set;
    }
}
