package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public final class ContentCityStructure extends Structure {
    public static final MapCodec<ContentCityStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(settingsCodec(instance)).apply(instance, ContentCityStructure::new));
    public static final StructureType<ContentCityStructure> TYPE = () -> CODEC;

    static final long SALT = 0x51A3C7E9B1D0F42BL;
    static final int VERGE_RUN = 3;
    private static final int COURT_DIG = 4;
    private static final int COURT_FILL = 2;
    static final int FRONT_REACH = 3;
    static final Map<CityPlan, District> DISTRICTS = Collections.synchronizedMap(new WeakHashMap<>());

    public ContentCityStructure(StructureSettings settings) { super(settings); }

    @Override @Nonnull protected Optional<GenerationStub> findGenerationPoint(@Nonnull GenerationContext context) {
        if (ContentCity.idle()) { return Optional.empty(); }
        ChunkPos chunk = context.chunkPos();
        int districtX = CityPlan.districtOf(chunk.getMinBlockX(), true);
        int districtZ = CityPlan.districtOf(chunk.getMinBlockZ(), false);
        int middle = CityPlan.chunks() / 2 * 16;
        if (chunk.getMinBlockX() != CityPlan.windowOf(districtX, true) + middle || chunk.getMinBlockZ() != CityPlan.windowOf(districtZ, false) + middle) { return Optional.empty(); }
        CityGround ground = CityGround.of(context);
        CityPlan found = CityPlan.of(ground, districtX, districtZ);
        if (found == null) { return Optional.empty(); }
        CityPlan plan = ContentCityStructureSite.settled(context, ground, found);
        BlockPos origin = new BlockPos(plan.windowX(), context.chunkGenerator().getSeaLevel(), plan.windowZ());
        return Optional.of(new GenerationStub(origin, builder -> pieces(context, ground, plan, builder)));
    }

    record Graded(int start, int[] profile, int[] ground, boolean[] held, boolean[] pinned, boolean[] mine, boolean[] bridged, boolean[] bored) {}

    record Laid(int start, int[] profile, int[] grade, boolean[] decked, boolean[] paved, boolean[] bridged, boolean[] bored, boolean[] roofed, boolean[] mine) {}

    record Bulb(ContentCityBulbPiece.Court court, CityPlan.Line line, BoundingBox box) {}

    record Seated(List<StructurePiece> pieces, BoundingBox box, Set<Long> doors, CityPlan.Plot plot, int level) {}

    record Street(int start, int[] grade, boolean[] bored) {}

    record District(Map<CityPlan.Line, Street> streets, List<Well> wells, List<BoundingBox> claims) {}

    record Well(@Nullable ResourceLocation template, int tall, BoundingBox box, int level, CityPlan.Junction middle) {}

    private static void pieces(GenerationContext context, CityGround ground, CityPlan plan, StructurePiecesBuilder builder) {
        Map<CityPlan.Junction, Integer> levels = new HashMap<>();
        Map<CityPlan.Line, int[]> setters = new HashMap<>();
        for (CityPlan.Junction junction : plan.junctions()) { levels.put(junction, CityPlan.junctionLevel(ground, junction, setters)); }
        int run = CityPlan.flatRun();
        Map<CityPlan.Rail, CityRails.Laid> rails = new HashMap<>();
        List<BoundingBox> claims = new ArrayList<>();
        for (CityPlan.Rail rail : plan.rails()) {
            CityRails.Laid track = CityRails.laid(ground, rail);
            if (track == null) { continue; }
            rails.put(rail, track);
            ContentCityStructureStations.claim(track, claims);
            if (plan.laysElsewhere(rail)) { continue; }
            ContentCityStructureStations.layRail(plan, track, false, builder);
            if (rail.subway() && ContentCity.stations() && CityLinks.trunkData(rail) == null) { ContentCityStructureStations.chambers(plan, track, builder); }
        }
        List<Well> wells = ContentCityStructurePlaza.wells(context, ground, plan, levels);
        ContentCityStructurePlaza.plazaLevels(wells, levels);
        aprons(ground, plan, levels, ContentCityStructurePlaza.boxes(wells));
        ContentCityStructureJoins.raiseJunctions(context, plan, levels, run, rails, wells);
        Map<Integer, Integer> decks = ContentCityStructureGrade.decks(context, plan, wells, levels);
        Map<CityPlan.Line, int[]> profiles = new HashMap<>();
        Map<CityPlan.Line, int[]> seats = new HashMap<>();
        Map<CityPlan.Line, Laid> laid = new HashMap<>();
        List<Bulb> bulbs = new ArrayList<>();
        List<StructurePiece> hatches = new ArrayList<>();
        for (CityPlan.Line line : plan.alongX()) { profiles.put(line, ContentCityStructurePaving.lay(context, plan, line, levels, run, rails, wells, decks, seats, laid, bulbs, hatches, builder)); }
        for (CityPlan.Line line : plan.alongZ()) { profiles.put(line, ContentCityStructurePaving.lay(context, plan, line, levels, run, rails, wells, decks, seats, laid, bulbs, hatches, builder)); }
        ContentCityStructurePaving.overpasses(plan, laid, builder);
        if (ContentCity.sewers()) { ContentCityStructureSewers.joins(plan, context.heightAccessor().getMinBuildHeight(), laid, wells, builder); }
        Map<CityPlan.Line, Street> streets = new HashMap<>();
        for (Map.Entry<CityPlan.Line, Laid> entry : laid.entrySet()) { streets.put(entry.getKey(), new Street(entry.getValue().start(), seats.get(entry.getKey()), entry.getValue().roofed())); }
        ContentCityStructureSite.courtStreets(ground, plan, streets, profiles);
        DISTRICTS.putIfAbsent(plan, new District(streets, wells, claims));
        for (CityRails.Laid track : rails.values()) {
            if (plan.laysElsewhere(track.rail())) { continue; }
            ContentCityStructureStations.layRail(plan, track, true, builder);
            if (track.rail().subway() && ContentCity.stations()) { ContentCityStructureStations.heads(ground, plan, track, laid, builder); }
        }
        int yielded = 0;
        List<Seated> seated = new ArrayList<>();
        Set<Long> doors = new HashSet<>();
        List<Seated> offered = new ArrayList<>();
        for (CityPlan.Plot plot : plan.plots()) {
            if (ContentCityStructureStations.claimed(claims, plot)) {
                yielded++;
                continue;
            }
            Seated found = ContentCityStructureSeat.seat(context, ground, plan, plot, laid, wells);
            if (found != null) { offered.add(found); }
        }
        for (Seated found : offered) {
            if (ContentCityStructureSite.buried(context, ground, plan, found, offered, wells, bulbs) || CityRails.givesWay(rails.values(), found.plot(), found.box(), found.level())) { continue; }
            seated.add(found);
            doors.addAll(found.doors());
        }
        if (yielded > 0) { ContentLog.LOGGER.debug("{} plot(s) of the district at {}, {} make way for open track and station stairs", yielded, plan.originX(), plan.originZ()); }
        List<Well> plazas = new ArrayList<>(wells);
        plazas.addAll(ContentCityStructurePlaza.near(context, ground, plan));
        for (CityPlan.Line line : plan.alongX()) { ContentCityStructureLamps.lamps(context, plan, line, laid.get(line), rails.values(), plazas, doors, builder); }
        for (CityPlan.Line line : plan.alongZ()) { ContentCityStructureLamps.lamps(context, plan, line, laid.get(line), rails.values(), plazas, doors, builder); }
        for (Bulb bulb : bulbs) { ContentCityStructureLamps.bulbLamps(context, plan, bulb, plazas, doors, builder); }
        for (Seated held : seated) { held.pieces().forEach(builder::addPiece); }
        int[] footprints = ContentCityStructurePlaza.footprints(claims, seated);
        int[] stations = ContentCityStructureStations.stations(rails.values());
        for (Well well : wells) { ContentCityStructurePlaza.plaza(context, ground, plan, well, well == wells.getFirst(), footprints, stations, ContentCityStructurePlaza.streetsOver(well, laid), builder); }
        for (Map.Entry<CityPlan.Junction, Integer> crossing : levels.entrySet()) {
            CityPlan.Junction junction = crossing.getKey();
            if (!junction.alongX().covers(junction.alongZ().middle()) || !junction.alongZ().covers(junction.alongX().middle())) { continue; }
            if (junction.alongX().alley() || junction.alongZ().alley() || ContentCityStructurePlaza.wellAt(wells, junction) != null) { continue; }
            if (ContentCityStructurePaving.decked(laid.get(junction.alongX()), junction.fromX(), junction.toX()) || ContentCityStructurePaving.decked(laid.get(junction.alongZ()), junction.fromZ(), junction.toZ())) { continue; }
            if (!plan.emits(junction.fromX(), junction.fromZ())) { continue; }
            int arms = CityCross.arms(ground, plan, junction.alongX(), junction.alongZ());
            if (Integer.bitCount(arms) < 3) { continue; }
            PathIntersectDef design = ContentPathIntersects.forJunction(context.seed(), junction.fromX(), junction.fromZ());
            if (design == null) { continue; }
            builder.addPiece(new ContentCityIntersectPiece(crossing.getValue(), design.key().toString(), junction.fromX(), junction.toX(), junction.fromZ(), junction.toZ(), design.mouth().size(), arms, CityCross.of(junction.alongZ()).core(), CityCross.of(junction.alongX()).core()));
        }
        for (StructurePiece hatch : hatches) { builder.addPiece(hatch); }
        for (Map.Entry<CityPlan.Line, int[]> held : profiles.entrySet()) { ContentCityStructureLamps.verges(held.getKey(), plan, held.getValue(), laid.get(held.getKey()), seated, plazas, doors, builder); }
    }

    static void aprons(CityGround ground, CityPlan plan, Map<CityPlan.Junction, Integer> levels, List<BoundingBox> wells) {
        List<CityPlan.Line> lines = new ArrayList<>(plan.alongX());
        lines.addAll(plan.alongZ());
        for (CityPlan.Line line : lines) {
            List<CityPlan.Line> met = new ArrayList<>(plan.crossing(line));
            met.sort(Comparator.comparingInt(CityPlan.Line::at));
            int previous = Integer.MIN_VALUE;
            int previousRow = 0;
            for (CityPlan.Line other : met) {
                CityPlan.Junction junction = line.alongX() ? new CityPlan.Junction(line, other) : new CityPlan.Junction(other, line);
                Integer level = levels.get(junction);
                if (level == null || !line.covers(other.middle()) || !other.covers(line.middle()) || !CityPlan.setter(junction).equals(line)) { continue; }
                int held = level;
                if (previous != Integer.MIN_VALUE && !CityPlan.inPlaza(wells, junction) && !ContentCityStructureJoins.wetCross(ground, junction)) {
                    int steps = other.at() - previousRow;
                    held = Mth.clamp(level, previous - steps, previous + steps);
                }
                if (held != level) {
                    levels.put(junction, held);
                    ContentLog.LOGGER.debug("The street along {} at {} flattens its junction with the street at {} to y {}, brought from y {} within reach of the square it holds before it", line.alongX() ? "x" : "z", line.at(), other.at(), held, level);
                }
                previous = held;
                previousRow = other.last();
            }
        }
    }

    static boolean steep(CityGround ground, int discX, int discZ, int radius, int level) {
        int reach = radius + ContentCityBulbPiece.VERGE;
        for (int z = discZ - reach; z <= discZ + reach; z += 2) {
            for (int x = discX - reach; x <= discX + reach; x += 2) {
                if ((x - discX) * (x - discX) + (z - discZ) * (z - discZ) > reach * reach + reach) { continue; }
                int stood = ground.floor(x, z);
                if (level - stood > COURT_FILL || stood - level > COURT_DIG) { return true; }
            }
        }
        return false;
    }

    static boolean dry(CityGround ground, int fromX, int fromZ, int discX, int discZ, int radius) {
        for (int z = fromZ; z <= discZ + radius; z += 2) {
            for (int x = fromX; x <= discX + radius; x += 2) {
                if ((x - discX) * (x - discX) + (z - discZ) * (z - discZ) > radius * radius + radius) { continue; }
                if (ground.floor(x, z) < ground.sea() - 1) { return false; }
            }
        }
        return true;
    }

    public static boolean plantedAt(CityGround ground, int x, int z) {
        if (ContentCity.decorNames().isEmpty()) { return false; }
        CityPlan plan = CityPlan.of(ground, CityPlan.districtOf(x, true), CityPlan.districtOf(z, false));
        if (plan == null) { return false; }
        boolean spot = false;
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line line : lines) {
                int along = line.alongX() ? x : z;
                int across = line.alongX() ? z : x;
                if (Math.abs(across - line.middle()) != CityCross.of(line).curb() + 1) { continue; }
                if (Math.floorMod(along, VERGE_RUN) != 0 || !line.covers(along) || !plan.emitsAlong(line, along)) { continue; }
                spot = true;
                break;
            }
            if (spot) { break; }
        }
        if (!spot) { return false; }
        String named = ContentCity.decor(RandomSource.create(ContentCityBlocks.spot(ground.seed(), x, z)));
        return named != null && ContentWorldgen.byName(named) != null;
    }

    @Override public void afterPlace(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull PiecesContainer pieces) {
        CityBiome.within(level, box, () -> CityPlotSeams.seams(level, box, pieces));
        for (StructurePiece piece : pieces.pieces()) {
            if (piece instanceof ContentCityPlotPiece plot) { plot.ringBeyond(level, box); }
            else if (piece instanceof ContentCityFarmPiece farm) { farm.ringBeyond(level, box); }
        }
    }

    @Override @Nonnull public StructureType<?> type() { return TYPE; }
}
