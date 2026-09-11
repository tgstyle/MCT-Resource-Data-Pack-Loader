package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.CityMapDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Mth;
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
import java.util.function.IntFunction;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentCity {
    public static final String STRUCTURE = "city";
    private static final String OVERWORLD_BIOMES = "#minecraft:is_overworld";
    private static final String DEFAULT_PAVING = "minecraft:dirt_path";
    private static final Set<String> MISSING = new LinkedHashSet<>();
    private static boolean laying;

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

    public static String bridgeSidewalkBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeSidewalkBlock", Config.worldgen.villagePathBridgeSidewalkBlock()).trim(); }

    public static String bridgeBarrierBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeBarrierBlock", Config.worldgen.villagePathBridgeBarrierBlock()).trim(); }

    public static int bridgeBarrierHeight() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeBarrierHeight", Config.worldgen.villagePathBridgeBarrierHeight())); }

    public static int bridgeDrop() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeDrop", Config.worldgen.villagePathBridgeDrop())); }

    public static String supportBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathSupportBlock", Config.worldgen.villagePathSupportBlock()).trim(); }

    public static String frameBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeFrameBlock", Config.worldgen.villagePathBridgeFrameBlock()).trim(); }

    public static String frameTopBlock() { return ContentControl.text(ContentControl.VILLAGES, "villagePathBridgeFrameTopBlock", Config.worldgen.villagePathBridgeFrameTopBlock()).trim(); }

    public static int frameHeight() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameHeight", Config.worldgen.villagePathBridgeFrameHeight())); }

    public static int frameRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameRun", Config.worldgen.villagePathBridgeFrameRun())); }

    public static int frameLeast() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villagePathBridgeFrameLeast", Config.worldgen.villagePathBridgeFrameLeast())); }

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


    public static String railTrackSeat(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTrackSeat" : "villageRailTrackSeat", sub ? Config.worldgen.villageSubwayTrackSeat() : Config.worldgen.villageRailTrackSeat()).trim().toLowerCase(Locale.ROOT); }


    public static int railTieRun(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTieRun" : "villageRailTieRun", sub ? Config.worldgen.villageSubwayTieRun() : Config.worldgen.villageRailTieRun())); }


    public static int railTracks(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTracks" : "villageRailTracks", sub ? Config.worldgen.villageSubwayTracks() : Config.worldgen.villageRailTracks())); }


    public static int railTrackGap(boolean sub) { return Math.max(2, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTrackGap" : "villageRailTrackGap", sub ? Config.worldgen.villageSubwayTrackGap() : Config.worldgen.villageRailTrackGap())); }


    public static int railShoulderWidth(boolean sub) { return railShoulderBlock(sub).isEmpty() ? 0 : Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayShoulderWidth" : "villageRailShoulderWidth", sub ? Config.worldgen.villageSubwayShoulderWidth() : Config.worldgen.villageRailShoulderWidth())); }


    public static int railPowerRun(boolean sub) { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayPowerRun" : "villageRailPowerRun", sub ? Config.worldgen.villageSubwayPowerRun() : Config.worldgen.villageRailPowerRun())); }


    public static int railClimb(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayClimb" : "villageRailClimb", sub ? Config.worldgen.villageSubwayClimb() : Config.worldgen.villageRailClimb())); }

    public static String railSupportBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailSupportBlock", Config.worldgen.villageRailSupportBlock()).trim(); }

    public static String railDeckBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailDeckBlock", Config.worldgen.villageRailDeckBlock()).trim(); }

    public static String railBarrierBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailBarrierBlock", Config.worldgen.villageRailBarrierBlock()).trim(); }

    public static String railFrameBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailBridgeFrameBlock", Config.worldgen.villageRailBridgeFrameBlock()).trim(); }

    public static String railFrameTopBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageRailBridgeFrameTopBlock", Config.worldgen.villageRailBridgeFrameTopBlock()).trim(); }

    public static int railFrameHeight() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameHeight", Config.worldgen.villageRailBridgeFrameHeight())); }

    public static int railFrameRun() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameRun", Config.worldgen.villageRailBridgeFrameRun())); }

    public static int railFrameLeast() { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailBridgeFrameLeast", Config.worldgen.villageRailBridgeFrameLeast())); }

    public static String railTunnelBlock() { return railTunnelBlock(false); }

    public static String railTunnelBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTunnelBlock" : "villageRailTunnelBlock", sub ? Config.worldgen.villageSubwayTunnelBlock() : Config.worldgen.villageRailTunnelBlock()).trim(); }

    public static int railTunnelDepth() { return railTunnelBlock().isEmpty() ? 0 : Math.max(1, ContentControl.number(ContentControl.VILLAGES, "villageRailTunnelDepth", Config.worldgen.villageRailTunnelDepth())); }

    public static int subwayLines() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayLines", Config.worldgen.villageSubwayLines())); }

    public static int subwayDepth() { return Math.max(6, ContentControl.number(ContentControl.VILLAGES, "villageSubwayDepth", Config.worldgen.villageSubwayDepth())); }

    public static boolean subways() { return subwayLines() > 0 && !railTunnelBlock(true).isEmpty(); }

    public static int stationLength() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationLength", Config.worldgen.villageSubwayStationLength())); }

    public static int stationRun() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayStationRun", Config.worldgen.villageSubwayStationRun())); }

    public static int platformWidth() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayPlatformWidth", Config.worldgen.villageSubwayPlatformWidth())); }

    public static String platformBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayPlatformBlock", Config.worldgen.villageSubwayPlatformBlock()).trim(); }

    public static boolean stations() { return subways() && stationLength() > 0 && platformWidth() > 0; }

    public static String stairBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayStairBlock", Config.worldgen.villageSubwayStairBlock()).trim(); }

    public static String railingBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayRailingBlock", Config.worldgen.villageSubwayRailingBlock()).trim(); }

    public static String benchBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayBenchBlock", Config.worldgen.villageSubwayBenchBlock()).trim(); }

    public static String benchEndBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayBenchEndBlock", Config.worldgen.villageSubwayBenchEndBlock()).trim(); }

    public static int benchLength() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwayBenchLength", Config.worldgen.villageSubwayBenchLength())); }

    public static int subwaySurfaces() { return Math.min(100, Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSubwaySurfaces", Config.worldgen.villageSubwaySurfaces()))); }

    public static String stationStructure() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayStation", Config.worldgen.villageSubwayStation()).trim(); }

    public static String stationEntrance() { return ContentControl.text(ContentControl.VILLAGES, "villageSubwayEntrance", Config.worldgen.villageSubwayEntrance()).trim(); }

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

    public static String sewerMossBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerMossBlock", Config.worldgen.villageSewerMossBlock()).trim(); }

    public static int sewerMossChance() { return Math.min(100, Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSewerMossChance", Config.worldgen.villageSewerMossChance()))); }

    public static String sewerVineBlock() { return ContentControl.text(ContentControl.VILLAGES, "villageSewerVineBlock", Config.worldgen.villageSewerVineBlock()).trim(); }

    public static int sewerVineChance() { return Math.min(100, Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villageSewerVineChance", Config.worldgen.villageSewerVineChance()))); }


    public static String railTunnelLightBlock(boolean sub) { return ContentControl.text(ContentControl.VILLAGES, sub ? "villageSubwayTunnelLightBlock" : "villageRailTunnelLightBlock", sub ? Config.worldgen.villageSubwayTunnelLightBlock() : Config.worldgen.villageRailTunnelLightBlock()).trim(); }


    public static int railTunnelLightRun(boolean sub) { return Math.max(1, ContentControl.number(ContentControl.VILLAGES, sub ? "villageSubwayTunnelLightRun" : "villageRailTunnelLightRun", sub ? Config.worldgen.villageSubwayTunnelLightRun() : Config.worldgen.villageRailTunnelLightRun())); }

    @Nullable public static String wellStructure(RandomSource roll) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villageWellStructure", Config.worldgen.villageWellStructure())) {
            String text = entry.trim();
            if (text.isEmpty()) { continue; }
            int at = text.lastIndexOf('=');
            if (at < 0) {
                picks.add(new PickDef(text, 1));
                continue;
            }
            try { picks.add(new PickDef(text.substring(0, at).trim(), Math.max(1, Integer.parseInt(text.substring(at + 1).trim())))); }
            catch (NumberFormatException notNumber) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villageWellStructure entry '{}' is not a name or a name=weight, so it is left out", entry); }
            }
        }
        String picked = PickDef.pick(picks, roll);
        return picked == null || picked.isEmpty() ? null : picked;
    }

    @Nullable public static String deadEnd(RandomSource roll) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villagePathDeadEnds", Config.worldgen.villagePathDeadEnds())) {
            String text = entry.trim();
            if (!text.isEmpty()) { picks.add(new PickDef(text, 1)); }
        }
        String picked = PickDef.pick(picks, roll);
        return picked == null || picked.isEmpty() ? null : picked;
    }

    public static void missingDeadEnd(String named) {
        if (MISSING.add(named)) { ContentLog.LOGGER.error("villagePathDeadEnds names '{}', which could not be loaded, so a cul-de-sac closes that street instead", named); }
    }

    public record Cargo(String name, int height) {}

    public static String pierLoot() { return ContentControl.text(ContentControl.VILLAGES, "villagePathPierLoot", Config.worldgen.villagePathPierLoot()).trim(); }

    @Nullable public static String pierStyle(RandomSource roll) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villagePathPiers", Config.worldgen.villagePathPiers())) {
            String text = entry.trim().toLowerCase(Locale.ROOT);
            if (text.isEmpty()) { continue; }
            if (!ContentCityPierPiece.RAILED.equals(text) && !ContentCityPierPiece.PILINGS.equals(text) && !ContentCityPierPiece.BOARDWALK.equals(text)) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villagePathPiers names style '{}', which is not railed, pilings or boardwalk, so it is left out", entry); }
                continue;
            }
            picks.add(new PickDef(text, 1));
        }
        return PickDef.pick(picks, roll);
    }

    @Nullable public static Cargo pierCargo(RandomSource roll) {
        List<PickDef> picks = new ArrayList<>();
        List<Cargo> stood = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villagePathPierCargo", Config.worldgen.villagePathPierCargo())) {
            String text = entry.trim();
            int at = text.indexOf('=');
            if (at <= 0) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villagePathPierCargo entry '{}' is not written as block=weight, so it is left out", entry); }
                continue;
            }
            String named = text.substring(0, at).trim();
            String rest = text.substring(at + 1).trim();
            int comma = rest.indexOf(',');
            int height = 1;
            try {
                if (comma >= 0) {
                    height = Mth.clamp(Integer.parseInt(rest.substring(comma + 1).trim()), 1, 8);
                    rest = rest.substring(0, comma).trim();
                }
                picks.add(new PickDef(Integer.toString(stood.size()), Math.max(1, Integer.parseInt(rest))));
            }
            catch (NumberFormatException notNumber) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villagePathPierCargo entry '{}' carries a weight or height that is not a number, so it is left out", entry); }
                continue;
            }
            stood.add(new Cargo("empty".equals(named) ? "" : named, height));
        }
        String picked = PickDef.pick(picks, roll);
        if (picked == null) { return null; }
        Cargo held = stood.get(Integer.parseInt(picked));
        return held.name().isEmpty() ? null : held;
    }

    public static List<String> decorNames() { return ContentControl.list(ContentControl.VILLAGES, "villageDecor", Config.worldgen.villageDecor()); }

    @Nullable public static String decor(RandomSource roll) {
        List<PickDef> picks = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.VILLAGES, "villageDecor", Config.worldgen.villageDecor())) {
            String text = entry.trim();
            if (text.isEmpty()) { continue; }
            int at = text.lastIndexOf('=');
            if (at < 0) {
                picks.add(new PickDef(text, 1));
                continue;
            }
            try { picks.add(new PickDef(text.substring(0, at).trim(), Math.max(1, Integer.parseInt(text.substring(at + 1).trim())))); }
            catch (NumberFormatException notNumber) {
                if (MISSING.add(entry)) { ContentLog.LOGGER.error("villageDecor entry '{}' is not a name or a name=weight, so it is left out", entry); }
            }
        }
        String picked = PickDef.pick(picks, roll);
        return picked == null || picked.isEmpty() || "empty".equals(picked) ? null : picked;
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
        laying = wanted();
        if (!laying) { return; }
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
        if (!def.villagerEntity().isEmpty()) {
            kind = EntityType.byString(def.villagerEntity()).orElse(null);
            if (kind == null) {
                if (MISSING.add(def.key() + "|" + def.villagerEntity())) { ContentLog.LOGGER.error("Village plot {} wants {} to live in it, which nothing registers", def.key(), def.villagerEntity()); }
                return;
            }
        }
        for (int index = 0; index < def.villagers(); index++) {
            BlockPos at = spot.apply(index);
            if (!box.isInside(at)) { continue; }
            Entity made = kind.create(level.getLevel());
            if (made == null) { return; }
            made.moveTo(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D, 0.0F, 0.0F);
            if (made instanceof Mob mob) { ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(at), MobSpawnType.STRUCTURE, null, null); }
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
        if (MISSING.add(named)) { ContentLog.LOGGER.error("villageSubwayStation '{}' could not be loaded, so stations are carved instead of laid from the build", named); }
    }

    public static void stationTooShort(String named, int tall, int climb, int grown) {
        if (MISSING.add(named + "@" + climb)) { ContentLog.LOGGER.info("The station build '{}' is {} block(s) tall and this station climbs {}, which the build cannot reach even grown to {}, so this one is carved instead", named, tall, climb, grown); }
    }

    public static void missingEntrance(String named) {
        if (MISSING.add(named)) { ContentLog.LOGGER.error("villageSubwayEntrance '{}' could not be loaded, so the subway stairs come up bare", named); }
    }

    public static void entranceOnStreet() {
        if (MISSING.add("entrance@street")) { ContentLog.LOGGER.info("A subway entrance would have stood on a street, so it is left off and the stairs come up bare"); }
    }

    public static void entranceWithBuild() {
        if (MISSING.add("entrance@build")) { ContentLog.LOGGER.info("villageSubwayStation names a build, which carries its own way in, so villageSubwayEntrance is left off rather than stood beside it as a shut box"); }
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
