package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ContentCityStructure extends Structure {
    public static final MapCodec<ContentCityStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(settingsCodec(instance)).apply(instance, ContentCityStructure::new));
    public static final StructureType<ContentCityStructure> TYPE = () -> CODEC;

    private static final int LAMP_LEAST = 7;
    private static final int LAMP_SPREAD = 6;
    private static final long SALT = 0x51A3C7E9B1D0F42BL;
    private static final int LEAST_OPEN = 8;
    private static final int SLIDE = 48;
    private static final int TRESTLE_DROP = 3;
    private static final int SPAN_LEAST = 4;
    private static final int VERGE_RUN = 3;

    public ContentCityStructure(StructureSettings settings) { super(settings); }

    @Override @Nonnull protected Optional<GenerationStub> findGenerationPoint(@Nonnull GenerationContext context) {
        if (ContentCity.idle()) { return Optional.empty(); }
        ChunkPos chunk = context.chunkPos();
        if (Math.floorMod(chunk.x, CityPlan.chunks()) != CityPlan.chunks() / 2 || Math.floorMod(chunk.z, CityPlan.chunks()) != CityPlan.chunks() / 2) { return Optional.empty(); }
        CityPlan plan = CityPlan.of(context.seed(), Math.floorDiv(chunk.x, CityPlan.chunks()), Math.floorDiv(chunk.z, CityPlan.chunks()));
        if (plan == null) { return Optional.empty(); }
        BlockPos origin = new BlockPos(plan.windowX(), context.chunkGenerator().getSeaLevel(), plan.windowZ());
        return Optional.of(new GenerationStub(origin, builder -> pieces(context, plan, builder)));
    }

    private static void pieces(GenerationContext context, CityPlan plan, StructurePiecesBuilder builder) {
        Map<CityPlan.Junction, Integer> levels = new HashMap<>();
        for (CityPlan.Junction junction : plan.junctions()) { levels.put(junction, junctionLevel(context, junction)); }
        int run = CityPlan.flatRun();
        Map<CityPlan.Rail, int[]> rails = new HashMap<>();
        List<BoundingBox> claims = new ArrayList<>();
        for (CityPlan.Rail rail : plan.rails()) { rails.put(rail, layRail(context, plan, rail, claims, builder)); }
        Well well = well(context, plan, levels);
        Map<CityPlan.Line, int[]> profiles = new HashMap<>();
        List<StructurePiece> hatches = new ArrayList<>();
        for (CityPlan.Line line : plan.alongX()) { profiles.put(line, lay(context, plan, line, levels, run, rails, well, hatches, builder)); }
        for (CityPlan.Line line : plan.alongZ()) { profiles.put(line, lay(context, plan, line, levels, run, rails, well, hatches, builder)); }
        int yielded = 0;
        for (CityPlan.Plot plot : plan.plots()) {
            if (claimed(claims, plot)) {
                yielded++;
                continue;
            }
            seat(context, plan, plot, profiles, builder);
        }
        if (yielded > 0) { ContentLog.LOGGER.debug("{} plot(s) of the district at {}, {} make way for open track and station stairs", yielded, plan.originX(), plan.originZ()); }
        plaza(context, plan, well, builder);
        for (Map.Entry<CityPlan.Junction, Integer> crossing : levels.entrySet()) {
            CityPlan.Junction junction = crossing.getKey();
            if (!junction.alongX().covers(junction.alongZ().middle()) || !junction.alongZ().covers(junction.alongX().middle())) { continue; }
            if (junction.alongX().alley() || junction.alongZ().alley() || (well != null && plan.plazaAt(junction.alongX()) && plan.plazaAt(junction.alongZ()))) { continue; }
            if (!plan.emits(junction.fromX(), junction.fromZ())) { continue; }
            PathIntersectDef design = ContentPathIntersects.forJunction(context.seed(), junction.fromX(), junction.fromZ());
            if (design == null) { continue; }
            builder.addPiece(new ContentCityIntersectPiece(crossing.getValue(), design.key().toString(), junction.fromX(), junction.toX(), junction.fromZ(), junction.toZ(), design.mouth().size()));
        }
        for (StructurePiece hatch : hatches) { builder.addPiece(hatch); }
        for (Map.Entry<CityPlan.Line, int[]> held : profiles.entrySet()) { verges(held.getKey(), plan, held.getValue(), builder); }
    }

    private static int junctionLevel(GenerationContext context, CityPlan.Junction junction) {
        int[] taken = {
            surface(context, junction.fromX(), junction.fromZ()),
            surface(context, junction.toX(), junction.fromZ()),
            surface(context, junction.fromX(), junction.toZ()),
            surface(context, junction.toX(), junction.toZ()),
            surface(context, (junction.fromX() + junction.toX()) / 2, (junction.fromZ() + junction.toZ()) / 2),
        };
        Arrays.sort(taken);
        return taken[taken.length / 2];
    }

    private static int[] lay(GenerationContext context, CityPlan plan, CityPlan.Line line, Map<CityPlan.Junction, Integer> levels, int run, Map<CityPlan.Rail, int[]> rails, @Nullable Well well, List<StructurePiece> hatches, StructurePiecesBuilder builder) {
        int start = plan.spanStart(line);
        int span = plan.spanLength(line);
        int[] profile = new int[span];
        boolean[] held = new boolean[span];
        boolean[] mine = new boolean[span];
        Arrays.fill(mine, true);
        for (int at = 0; at < profile.length; at++) { profile[at] = alongLine(context, line, start + at); }
        CityGrade.flatRuns(profile, start, run);
        List<CityPlan.Line> crossing = plan.crossing(line);
        for (CityPlan.Line other : crossing) {
            CityPlan.Junction junction = line.alongX() ? new CityPlan.Junction(line, other) : new CityPlan.Junction(other, line);
            Integer level = levels.get(junction);
            if (level == null || !line.covers(other.middle()) || !other.covers(line.middle())) { continue; }
            CityGrade.pin(profile, held, start, other.at(), other.last(), level);
            if (!other.beats(line)) { continue; }
            for (int row = other.at(); row <= other.last(); row++) {
                if (row >= start && row < start + mine.length) { mine[row - start] = false; }
            }
        }
        boolean[] sewered = mine.clone();
        for (Map.Entry<CityPlan.Rail, int[]> laid : rails.entrySet()) {
            CityPlan.Rail rail = laid.getKey();
            if (rail.subway() || rail.alongX() == line.alongX()) { continue; }
            int[] over = laid.getValue();
            int seat = Math.floorMod(line.middle() - (rail.alongX() ? plan.originX() : plan.originZ()) + plan.tail(rail, true), over.length);
            CityGrade.pin(profile, held, start, rail.at(), rail.last(), over[seat]);
            for (int row = rail.at(); row <= rail.last(); row++) {
                if (row >= start && row < start + mine.length) { mine[row - start] = false; }
            }
        }
        CityGrade.reconcile(profile, held);
        CityGrade.smooth(profile, held);
        int[] ground = new int[profile.length];
        boolean[] bridged = bridges(context, line, start, profile, held, ground);
        CityGrade.smooth(profile, held);
        boolean[] bored = CityGrade.bore(profile, ground, held, bridged, ContentCity.tunnelDepth());
        int boredRows = 0;
        for (boolean row : bored) { boredRows += row ? 1 : 0; }
        if (boredRows > 0) { ContentLog.LOGGER.debug("Bored {} row(s) of tunnel on the street at {}, {}", boredRows, line.alongX() ? plan.originX() : line.at(), line.alongX() ? line.at() : plan.originZ()); }
        for (int at = 0; at < bored.length; at++) { held[at] |= bored[at]; }
        CityGrade.smooth(profile, held);
        String paving = ContentCity.paving(line.alley());
        boolean[] paved = mine.clone();
        if (well != null && plan.plazaAt(line)) {
            BoundingBox square = well.box();
            int reach = CityPlan.plazaPaved();
            int low = (line.alongX() ? square.minX() : square.minZ()) - reach;
            int high = (line.alongX() ? square.maxX() : square.maxZ()) + reach;
            for (int row = low; row <= high; row++) {
                if (row >= start && row < start + paved.length) { paved[row - start] = false; }
            }
        }
        int from = 0;
        int pieces = 0;
        for (int at = 1; at <= profile.length; at++) {
            boolean ends = at == profile.length || profile[at] != profile[from] || paved[at] != paved[from] || bridged[at] != bridged[from] || bored[at] != bored[from];
            if (!ends) { continue; }
            int low = Math.max(start + from, line.from());
            int high = Math.min(start + at - 1, line.to());
            if (paved[from] && low <= high && plan.emitsAlong(line, low)) {
                builder.addPiece(piece(line, low, high, profile[from], paving, bridged[from], bored[from]));
                pieces++;
            }
            from = at;
        }
        if (line.alley()) { ContentLog.LOGGER.debug("The alley at {} running {} to {} is laid in {} piece(s) at y {} to {}", line.at(), line.from(), line.to(), pieces, profile[Math.clamp(line.from() - start, 0, profile.length - 1)], profile[Math.clamp(line.to() - start, 0, profile.length - 1)]); }
        if (ContentCity.sewers()) { sewers(plan, line, start, profile, sewered, bridged, rails, well, hatches, builder); }
        lamps(context, plan, line, start, profile, paved, well, builder);
        ends(context, plan, line, start, profile, paving, bridged, builder);
        return profile;
    }

    private static void sewers(CityPlan plan, CityPlan.Line line, int start, int[] profile, boolean[] mine, boolean[] bridged, Map<CityPlan.Rail, int[]> rails, @Nullable Well well, List<StructurePiece> hatches, StructurePiecesBuilder builder) {
        int[] bores = bores(plan, line, rails);
        int[] rows = sewerRows(plan, line, well);
        if (line.alongX()) {
            for (int row : hatchRows(plan, line, well)) {
                int index = row - start;
                if (!line.covers(row) || index < 0 || index >= profile.length || !mine[index] || bridged[index] || !plan.emitsAlong(line, row)) { continue; }
                hatches.add(new ContentCitySewerHatchPiece(row, line.middle() + 1, profile[index]));
            }
        }
        int from = 0;
        for (int at = 1; at <= profile.length; at++) {
            boolean ends = at == profile.length || profile[at] != profile[from] || mine[at] != mine[from] || bridged[at] != bridged[from];
            if (!ends) { continue; }
            int least = Math.max(start + from, line.from());
            int most = Math.min(start + at - 1, line.to());
            if (mine[from] && !bridged[from] && least <= most && plan.emitsAlong(line, least)) {
                int fromX = line.alongX() ? least : line.middle();
                int fromZ = line.alongX() ? line.middle() : least;
                int toX = line.alongX() ? most : line.middle();
                int toZ = line.alongX() ? line.middle() : most;
                builder.addPiece(new ContentCitySewerPiece(fromX, fromZ, toX, toZ, profile[from], line.middle(), line.alongX(), rows, bores));
            }
            from = at;
        }
        joins(plan, line, start, profile, mine, bridged, rows, bores, builder);
    }

    private static void joins(CityPlan plan, CityPlan.Line line, int start, int[] profile, boolean[] mine, boolean[] bridged, int[] rows, int[] bores, StructurePiecesBuilder builder) {
        for (CityPlan.Line other : plan.crossing(line)) {
            if (!other.covers(line.middle())) { continue; }
            for (int end = 0; end < 2; end++) {
                boolean low = end == 0;
                int edge = low ? line.from() : line.to();
                int beyond = low ? edge - 1 : edge + 1;
                if (beyond < other.at() || beyond > other.last()) { continue; }
                int least = low ? other.middle() + 1 : beyond;
                int most = low ? beyond : other.middle() - 1;
                int index = edge - start;
                if (least > most || index < 0 || index >= profile.length || !mine[index] || bridged[index] || !plan.emitsAlong(line, least)) { continue; }
                int fromX = line.alongX() ? least : line.middle();
                int fromZ = line.alongX() ? line.middle() : least;
                int toX = line.alongX() ? most : line.middle();
                int toZ = line.alongX() ? line.middle() : most;
                builder.addPiece(new ContentCitySewerPiece(fromX, fromZ, toX, toZ, profile[index], line.middle(), line.alongX(), rows, bores));
                ContentLog.LOGGER.debug("The sewer under the {} at {} runs on from row {} to {} to join the sewer under the {} at {}", line.alley() ? "alley" : "street", line.at(), edge, low ? least : most, other.alley() ? "alley" : "street", other.at());
            }
        }
    }

    private static int[] sewerRows(CityPlan plan, CityPlan.Line line, @Nullable Well well) {
        List<Integer> rows = new ArrayList<>();
        for (CityPlan.Line other : plan.crossing(line)) {
            if (other.from() > line.last() + 1 || other.to() < line.at() - 1) { continue; }
            rows.add(other.middle());
        }
        if (well != null && ContentCity.sewerWellEntrance() && (line.equals(plan.plazaRow()) || line.equals(plan.plazaColumn()))) {
            BoundingBox box = well.box();
            rows.add((line.alongX() ? box.minX() : box.minZ()) - ContentCitySewerLoopPiece.LOOP);
            rows.add((line.alongX() ? box.maxX() : box.maxZ()) + ContentCitySewerLoopPiece.LOOP);
        }
        int[] found = new int[rows.size()];
        for (int at = 0; at < found.length; at++) { found[at] = rows.get(at); }
        return found;
    }

    private static int[] hatchRows(CityPlan plan, CityPlan.Line line, @Nullable Well well) {
        boolean plazaStreet = well != null && (line.equals(plan.plazaRow()) || line.equals(plan.plazaColumn()));
        List<Integer> rows = new ArrayList<>();
        for (CityPlan.Line other : plan.crossing(line)) {
            if (plazaStreet && (other.equals(plan.plazaRow()) || other.equals(plan.plazaColumn()))) { continue; }
            if (other.from() > line.last() + 1 || other.to() < line.at() - 1) { continue; }
            rows.add(other.middle());
        }
        int[] found = new int[rows.size()];
        for (int at = 0; at < found.length; at++) { found[at] = rows.get(at); }
        return found;
    }

    private static int[] bores(CityPlan plan, CityPlan.Line line, Map<CityPlan.Rail, int[]> rails) {
        List<Integer> found = new ArrayList<>();
        int reach = (CityPlan.railWidth(true) - 1) / 2 + 1;
        for (Map.Entry<CityPlan.Rail, int[]> held : rails.entrySet()) {
            CityPlan.Rail rail = held.getKey();
            if (!rail.subway() || rail.alongX() == line.alongX()) { continue; }
            int[] over = held.getValue();
            int start = (rail.alongX() ? plan.originX() : plan.originZ()) - plan.tail(rail, true);
            int seat = line.middle() - start;
            if (seat < 0 || seat >= over.length) { continue; }
            found.add(rail.middle());
            found.add(over[seat] - 1);
            found.add(over[seat] + ContentCityRailPiece.CLEAR + 1);
            found.add(reach);
        }
        int[] out = new int[found.size()];
        for (int at = 0; at < out.length; at++) { out[at] = found.get(at); }
        return out;
    }

    private static void ends(GenerationContext context, CityPlan plan, CityPlan.Line line, int start, int[] profile, String paving, boolean[] bridged, StructurePiecesBuilder builder) {
        if (line.alley() || (!line.endsLow() && !line.endsHigh())) { return; }
        RandomSource roll = RandomSource.create(context.seed() ^ SALT ^ (line.at() * 132897987541L + (line.alongX() ? 3L : 5L)));
        String closed = ContentCity.deadEnd(roll);
        int reach = Math.max(2, line.width() - 1);
        for (int side = 0; side < 2; side++) {
            boolean low = side == 0;
            if (low ? !line.endsLow() : !line.endsHigh()) { continue; }
            int along = low ? line.from() : line.to();
            int at = Mth.clamp(along - start, 0, profile.length - 1);
            int x = line.alongX() ? along : line.middle();
            int z = line.alongX() ? line.middle() : along;
            if (!plan.emits(x, z)) { continue; }
            if (bridged[at]) {
                String style = ContentCity.pierStyle(roll);
                if (style != null) {
                    int run = at;
                    while (run - (low ? -1 : 1) >= 0 && run - (low ? -1 : 1) < profile.length && bridged[run - (low ? -1 : 1)] && line.covers(start + run - (low ? -1 : 1))) { run -= low ? -1 : 1; }
                    int from = Math.min(at, run) + start;
                    int to = Math.max(at, run) + start;
                    builder.addPiece(pier(line, from, to, profile[at], style, along));
                    ContentLog.LOGGER.debug("A street runs out over water at {}, {} and ends as a {} pier over rows {} to {}", x, z, style, from, to);
                    continue;
                }
            }
            if (closed == null) {
                builder.addPiece(new ContentCityBulbPiece(x, z, profile[at], reach, paving, ContentCity.sidewalkBlock()));
                continue;
            }
            ResourceLocation template = ResourceLocation.tryParse(closed);
            if (template == null || context.structureTemplateManager().get(template).isEmpty()) {
                ContentCity.missingDeadEnd(closed);
                builder.addPiece(new ContentCityBulbPiece(x, z, profile[at], reach, paving, ContentCity.sidewalkBlock()));
                continue;
            }
            Vec3i span = context.structureTemplateManager().get(template).orElseThrow().getSize(Rotation.NONE);
            builder.addPiece(new ContentCityPlotPiece(context.structureTemplateManager(), template, Rotation.NONE, 100, new BlockPos(x - span.getX() / 2, profile[at], z - span.getZ() / 2), ""));
        }
    }

    private static ContentCityPierPiece pier(CityPlan.Line line, int from, int to, int level, String style, int head) {
        if (line.alongX()) { return new ContentCityPierPiece(from, line.at(), to, line.last(), level, line.middle(), true, line.width(), style, head); }
        return new ContentCityPierPiece(line.at(), from, line.last(), to, level, line.middle(), false, line.width(), style, head);
    }

    private static void lamps(GenerationContext context, CityPlan plan, CityPlan.Line line, int start, int[] profile, boolean[] mine, @Nullable Well well, StructurePiecesBuilder builder) {
        CityCross cross = CityCross.of(line.width(), line.alley());
        if (line.alley() || cross.curb() <= 0) { return; }
        String named = ContentCity.lampStructure();
        ResourceLocation template = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        if (template != null && context.structureTemplateManager().get(template).isEmpty()) {
            ContentCity.missingLamp(named);
            template = null;
        }
        if (template == null && ContentCity.lampBlock().isEmpty()) { return; }
        int height = ContentCity.lampHeight();
        Vec3i span = template == null ? new Vec3i(3, height + 1, 3) : context.structureTemplateManager().get(template).orElseThrow().getSize(Rotation.NONE);
        int offset = template == null ? -1 : 0;
        int skipped = 0;
        RandomSource roll = RandomSource.create(context.seed() ^ SALT ^ (line.at() * 341873128712L + (line.alongX() ? 1L : 2L)));
        for (int at = LAMP_LEAST + roll.nextInt(LAMP_SPREAD); at < profile.length; at += LAMP_LEAST + roll.nextInt(LAMP_SPREAD)) {
            if (!mine[at] || !line.covers(start + at)) { continue; }
            for (int side = -1; side <= 1; side += 2) {
                int across = line.middle() + side * cross.lampOffset();
                int x = line.alongX() ? start + at : across;
                int z = line.alongX() ? across : start + at;
                if (!plan.emits(x, z)) { continue; }
                BoundingBox stood = new BoundingBox(x + offset, 0, z + offset, x + offset + span.getX() - 1, 0, z + offset + span.getZ() - 1);
                if (cleared(plan, line, well, stood)) {
                    skipped++;
                    continue;
                }
                if (template != null) { builder.addPiece(new ContentCityPlotPiece(context.structureTemplateManager(), template, Rotation.NONE, 100, new BlockPos(x, profile[at] + 1, z), "")); }
                else { builder.addPiece(new ContentCityLampPiece(x, profile[at] + 1, z, height)); }
            }
        }
        if (skipped > 0) { ContentLog.LOGGER.debug("The street at {} leaves out {} lamp(s) that would stand on the plaza, on another street or alley, or across the district edge, where a piece laid later clears the air over its paving", line.at(), skipped); }
    }

    private static boolean cleared(CityPlan plan, CityPlan.Line line, @Nullable Well well, BoundingBox stood) {
        if ((line.alongX() ? stood.minX() : stood.minZ()) < line.from() || (line.alongX() ? stood.maxX() : stood.maxZ()) > line.to()) { return true; }
        if (well != null) {
            int reach = CityPlan.plazaPaved() + CityPlan.walkWidth() + ContentCityPlazaPiece.MOUTH_MOST;
            BoundingBox box = well.box();
            if (stood.maxX() >= box.minX() - reach && stood.minX() <= box.maxX() + reach && stood.maxZ() >= box.minZ() - reach && stood.minZ() <= box.maxZ() + reach) { return true; }
        }
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line other : lines) {
                if (other.equals(line)) { continue; }
                int leastX = other.alongX() ? other.from() : other.at();
                int mostX = other.alongX() ? other.to() : other.last();
                int leastZ = other.alongX() ? other.at() : other.from();
                int mostZ = other.alongX() ? other.last() : other.to();
                if (stood.maxX() >= leastX && stood.minX() <= mostX && stood.maxZ() >= leastZ && stood.minZ() <= mostZ) { return true; }
            }
        }
        return false;
    }

    public static boolean plantedAt(long seed, int x, int z) {
        if (ContentCity.decorNames().isEmpty()) { return false; }
        CityPlan plan = CityPlan.of(seed, Math.floorDiv(x, CityPlan.district()), Math.floorDiv(z, CityPlan.district()));
        if (plan == null) { return false; }
        boolean spot = false;
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line line : lines) {
                int along = line.alongX() ? x : z;
                int across = line.alongX() ? z : x;
                if (Math.abs(across - line.middle()) != CityCross.of(line.width(), line.alley()).curb() + 1) { continue; }
                if (Math.floorMod(along, VERGE_RUN) != 0 || !line.covers(along) || !plan.emitsAlong(line, along)) { continue; }
                spot = true;
                break;
            }
            if (spot) { break; }
        }
        if (!spot) { return false; }
        String named = ContentCity.decor(RandomSource.create(ContentCityBlocks.spot(x, z)));
        return named != null && ContentWorldgen.byName(named) != null;
    }

    private static void verges(CityPlan.Line line, CityPlan plan, int[] profile, StructurePiecesBuilder builder) {
        if (ContentCity.decorNames().isEmpty()) { return; }
        CityCross cross = CityCross.of(line.width(), line.alley());
        int verge = cross.curb() + 1;
        int start = plan.spanStart(line);
        List<Integer> spots = new ArrayList<>();
        List<Integer> levels = new ArrayList<>();
        List<Integer> across = new ArrayList<>();
        for (int at = 0; at < profile.length; at++) {
            int along = start + at;
            if (Math.floorMod(along, VERGE_RUN) != 0 || !line.covers(along) || !plan.emitsAlong(line, along)) { continue; }
            for (int side = -1; side <= 1; side += 2) {
                spots.add(along);
                levels.add(profile[at]);
                across.add(line.middle() + side * verge);
            }
        }
        if (spots.isEmpty()) { return; }
        int[] alongs = spots.stream().mapToInt(Integer::intValue).toArray();
        int[] heights = levels.stream().mapToInt(Integer::intValue).toArray();
        int[] sides = across.stream().mapToInt(Integer::intValue).toArray();
        int lowAlong = alongs[0];
        int highAlong = alongs[alongs.length - 1];
        int lowAcross = line.middle() - verge;
        int highAcross = line.middle() + verge;
        int lowY = heights[0];
        int highY = heights[0];
        for (int held : heights) {
            lowY = Math.min(lowY, held);
            highY = Math.max(highY, held);
        }
        BoundingBox box = line.alongX() ? new BoundingBox(lowAlong, lowY, lowAcross, highAlong, highY + 2, highAcross)
                                        : new BoundingBox(lowAcross, lowY, lowAlong, highAcross, highY + 2, highAlong);
        builder.addPiece(new ContentCityDecorPiece(line.alongX(), alongs, heights, sides, box));
    }

    private record Well(ResourceLocation template, BoundingBox box, int level) {}

    @Nullable private static Well well(GenerationContext context, CityPlan plan, Map<CityPlan.Junction, Integer> levels) {
        CityPlan.Junction middle = new CityPlan.Junction(plan.plazaRow(), plan.plazaColumn());
        Integer level = levels.get(middle);
        if (level == null || !middle.alongX().covers(middle.alongZ().middle()) || !middle.alongZ().covers(middle.alongX().middle())) {
            ContentLog.LOGGER.debug("The district at {}, {} has no plaza: its middle streets do not cross", plan.originX(), plan.originZ());
            return null;
        }
        RandomSource roll = RandomSource.create(context.seed() ^ SALT ^ (plan.originX() * 341873128712L + plan.originZ() * 132897987541L));
        String named = ContentCity.wellStructure(roll);
        if (named == null) { return null; }
        ResourceLocation template = ResourceLocation.tryParse(named);
        if (template == null || context.structureTemplateManager().get(template).isEmpty()) {
            ContentCity.missingWell(named);
            return null;
        }
        Vec3i span = context.structureTemplateManager().get(template).orElseThrow().getSize(Rotation.NONE);
        int x = (middle.fromX() + middle.toX()) / 2 - span.getX() / 2;
        int z = (middle.fromZ() + middle.toZ()) / 2 - span.getZ() / 2;
        if (!plan.emits(x, z)) { return null; }
        return new Well(template, new BoundingBox(x, level, z, x + span.getX() - 1, level + span.getY() - 1, z + span.getZ() - 1), level);
    }

    private static void plaza(GenerationContext context, CityPlan plan, @Nullable Well well, StructurePiecesBuilder builder) {
        if (well == null) { return; }
        BoundingBox box = well.box();
        builder.addPiece(new ContentCityPlotPiece(context.structureTemplateManager(), well.template(), Rotation.NONE, 100, new BlockPos(box.minX(), well.level() + 1, box.minZ()), ""));
        PathIntersectDef design = ContentPathIntersects.forJunction(context.seed(), box.minX(), box.minZ());
        builder.addPiece(new ContentCityPlazaPiece(box.minX(), box.minZ(), box.maxX(), box.maxZ(), well.level(), CityPlan.plazaPaved(), CityPlan.walkWidth(), CityCross.of(CityPlan.streetFullWidth(), false).core(), design == null ? "" : design.key().toString(), plan.plazaColumn().middle(), plan.plazaRow().middle()));
        ContentLog.LOGGER.debug("The well {} stands at {}, {} to {}, {} on the plaza of the district at {}, {}, at level {}", well.template(), box.minX(), box.minZ(), box.maxX(), box.maxZ(), plan.originX(), plan.originZ(), well.level());
        if (!ContentCity.sewers() || !ContentCity.sewerWellEntrance()) { return; }
        builder.addPiece(new ContentCitySewerLoopPiece(box.minX(), box.minZ(), box.maxX(), box.maxZ(), well.level(), plan.plazaColumn().middle(), plan.plazaRow().middle()));
    }

    private static void seat(GenerationContext context, CityPlan plan, CityPlan.Plot plot, Map<CityPlan.Line, int[]> profiles, StructurePiecesBuilder builder) {
        if (!plan.emits((plot.fromX() + plot.toX()) / 2, (plot.fromZ() + plot.toZ()) / 2)) { return; }
        int[] profile = profiles.get(plot.street());
        if (profile == null) { return; }
        int start = plan.spanStart(plot.street());
        int along = Mth.clamp(plot.middleAlong() - start, 0, profile.length - 1);
        int level = profile[along];
        if (!plot.def().template()) {
            builder.addPiece(new ContentCityFarmPiece(plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ(), level, plot.def().height(),
                    plot.def().edge(), plot.def().soil(), plot.def().ground(), String.join(",", plot.def().crops()), plot.def().water(), Math.max(1, plot.def().rowWidth()), plot.def().key().toString()));
            return;
        }
        ResourceLocation template = ResourceLocation.tryParse(plot.def().structure());
        if (template == null || context.structureTemplateManager().get(template).isEmpty()) {
            ContentCity.missing(plot.def());
            return;
        }
        Rotation turn = facing(plot);
        Vec3i span = context.structureTemplateManager().get(template).orElseThrow().getSize(turn);
        BlockPos corner = new BlockPos(plot.fromX() + ContentImprint.backX(turn, span), level, plot.fromZ() + ContentImprint.backZ(turn, span));
        builder.addPiece(new ContentCityPlotPiece(context.structureTemplateManager(), template, turn, plot.def().integrity(), corner, plot.def().key().toString()));
    }

    private static Rotation facing(CityPlan.Plot plot) {
        if (plot.street().alongX()) { return plot.lower() ? Rotation.NONE : Rotation.CLOCKWISE_180; }
        return plot.lower() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
    }

    private static ContentCityPiece piece(CityPlan.Line line, int from, int to, int level, String paving, boolean bridged, boolean bored) {
        if (line.alongX()) { return new ContentCityPiece(from, line.at(), to, line.last(), level, paving, line.middle(), true, line.alley(), line.width(), bridged, bored); }
        return new ContentCityPiece(line.at(), from, line.last(), to, level, paving, line.middle(), false, line.alley(), line.width(), bridged, bored);
    }

    private static int[] layRail(GenerationContext context, CityPlan plan, CityPlan.Rail rail, List<BoundingBox> claims, StructurePiecesBuilder builder) {
        boolean sub = rail.subway();
        int climb = ContentCity.railClimb(sub);
        int tailLow = plan.tail(rail, true);
        int tailHigh = plan.tail(rail, false);
        int start = (rail.alongX() ? plan.originX() : plan.originZ()) - tailLow;
        int[] profile = new int[CityPlan.district() + tailLow + tailHigh];
        boolean[] held = new boolean[profile.length];
        int[] ground = new int[profile.length];
        int under = sub ? ContentCity.subwayDepth() : 0;
        int[] rising = sub ? surfacing(context, plan, rail, start, profile.length) : null;
        for (int at = 0; at < profile.length; at++) {
            int top = alongRail(context, rail, start + at);
            ground[at] = top;
            profile[at] = top - (surfaced(rising, start + at) ? 0 : under);
        }
        CityGrade.flatRuns(profile, start, climb);
        CityGrade.climb(profile, held, climb);
        boolean[] bridged = new boolean[profile.length];
        boolean[] bored = new boolean[profile.length];
        if (sub) { for (int at = 0; at < bored.length; at++) { bored[at] = !(surfaced(rising, start + at) && profile[at] >= ground[at] - 1); } }
        else {
            bridged = railBridges(context, rail, start, profile, held, ground);
            CityGrade.climb(profile, held, climb);
            bored = CityGrade.buriedRuns(profile, ground, held, bridged, ContentCity.railTunnelDepth());
        }
        for (int at = 0; at < bored.length; at++) { held[at] |= bored[at]; }
        CityGrade.climb(profile, held, climb);
        int from = 0;
        for (int at = 1; at <= profile.length; at++) {
            boolean ends = at == profile.length || profile[at] != profile[from] || bridged[at] != bridged[from] || bored[at] != bored[from];
            if (!ends) { continue; }
            builder.addPiece(railPiece(rail, start + from, start + at - 1, profile[from], bridged[from], bored[from]));
            from = at;
        }
        open(rail, start, bored, claims);
        if (sub && ContentCity.stations()) { stations(context, plan, rail, start, profile, rising, claims, builder); }
        return profile;
    }

    private static void open(CityPlan.Rail rail, int start, boolean[] bored, List<BoundingBox> claims) {
        int from = 0;
        for (int at = 1; at <= bored.length; at++) {
            boolean ends = at == bored.length || bored[at] != bored[from];
            if (!ends) { continue; }
            if (!bored[from]) {
                claims.add(rail.alongX() ? new BoundingBox(start + from, 0, rail.at(), start + at - 1, 0, rail.last()) : new BoundingBox(rail.at(), 0, start + from, rail.last(), 0, start + at - 1));
            }
            from = at;
        }
    }

    @Nullable private static int[] streetsOver(CityPlan plan, CityPlan.Rail rail) {
        int half = ContentCity.stationLength() / 2;
        for (CityPlan.Line line : rail.alongX() ? plan.alongX() : plan.alongZ()) {
            if (line.alley() || rail.middle() < line.at() || rail.middle() > line.last()) { continue; }
            return new int[] {line.from() + half, line.to() - half};
        }
        int least = Integer.MAX_VALUE;
        int most = Integer.MIN_VALUE;
        for (CityPlan.Line line : rail.alongX() ? plan.alongZ() : plan.alongX()) {
            if (line.alley() || !line.covers(rail.middle())) { continue; }
            least = Math.min(least, line.middle());
            most = Math.max(most, line.middle());
        }
        return least > most ? null : new int[] {least, most};
    }

    private static void stations(GenerationContext context, CityPlan plan, CityPlan.Rail rail, int start, int[] profile, @Nullable int[] rising, List<BoundingBox> claims, StructurePiecesBuilder builder) {
        int half = ContentCity.stationLength() / 2;
        int bedHalf = (rail.width() - 1) / 2 - ContentCity.railShoulderWidth(true);
        if (bedHalf < 0 || half <= 0) { return; }
        int[] streets = streetsOver(plan, rail);
        if (streets == null) {
            ContentLog.LOGGER.debug("The subway line at {} of the district at {}, {} has no street over it, so it gets no stations", rail.at(), plan.originX(), plan.originZ());
            return;
        }
        int least = Math.max(start + half, streets[0]);
        int most = Math.min(start + profile.length - 1 - half, streets[1]);
        if (rising != null) {
            int ramp = ContentCity.subwayDepth() * ContentCity.railClimb(true);
            if (rising[1] > 0) { most = Math.min(most, rising[0] - ramp - 1); }
            else { least = Math.max(least, rising[0] + ramp + 1); }
        }
        for (int wanted : hearts(plan, rail, least, most)) {
            int[] found = claimAt(context, plan, rail, wanted, start, profile, least, most, bedHalf, claims);
            if (found == null) { continue; }
            int heart = found[0];
            int level = profile[heart - start];
            int from = heart - half;
            int to = heart + half;
            int fromX = rail.alongX() ? from : rail.at();
            int fromZ = rail.alongX() ? rail.at() : from;
            int toX = rail.alongX() ? to : rail.last();
            int toZ = rail.alongX() ? rail.last() : to;
            builder.addPiece(new ContentCityStationPiece(fromX, fromZ, toX, toZ, level, rail.middle(), rail.alongX(), bedHalf));
            if (ContentCity.stationStructure().isEmpty()) {
                builder.addPiece(new ContentCityStairsPiece(level, found[3], heart, found[1], found[2], rail.middle(), rail.alongX(), bedHalf));
                entrance(context, plan, rail, heart, found[1], found[3], builder);
            }
            else {
                Vec3i built = stationSpan(context);
                builder.addPiece(new ContentCityStampPiece(level, found[3], heart, found[1], found[2], rail.middle(), rail.alongX(), bedHalf, built.getX(), built.getZ()));
                if (!ContentCity.stationEntrance().isEmpty()) { ContentCity.entranceWithBuild(); }
            }
        }
    }

    private static Vec3i stationSpan(GenerationContext context) {
        ResourceLocation named = ResourceLocation.tryParse(ContentCity.stationStructure());
        if (named == null) { return Vec3i.ZERO; }
        return context.structureTemplateManager().get(named).map(held -> held.getSize(Rotation.NONE)).orElse(Vec3i.ZERO);
    }

    private static void entrance(GenerationContext context, CityPlan plan, CityPlan.Rail rail, int row, int near, int top, StructurePiecesBuilder builder) {
        String named = ContentCity.stationEntrance();
        if (named.isEmpty()) { return; }
        ResourceLocation template = ResourceLocation.tryParse(named);
        if (template == null || context.structureTemplateManager().get(template).isEmpty()) {
            ContentCity.missingEntrance(named);
            return;
        }
        Vec3i span = context.structureTemplateManager().get(template).orElseThrow().getSize(Rotation.NONE);
        int alongMid = row + ContentCityStairsPiece.RUN / 2;
        int acrossMid = near + (ContentCityStairsPiece.WIDE - 1) / 2;
        int leastX = (rail.alongX() ? alongMid : acrossMid) - span.getX() / 2;
        int leastZ = (rail.alongX() ? acrossMid : alongMid) - span.getZ() / 2;
        BoundingBox stood = new BoundingBox(leastX, 0, leastZ, leastX + span.getX() - 1, 0, leastZ + span.getZ() - 1);
        if (onStreet(plan, stood)) {
            ContentCity.entranceOnStreet();
            return;
        }
        builder.addPiece(new ContentCityEntrancePiece(context.structureTemplateManager(), template, new BlockPos(leastX, top + 1, leastZ), row, near, top, rail.alongX()));
    }

    @Nullable private static int[] claimAt(GenerationContext context, CityPlan plan, CityPlan.Rail rail, int wanted, int start, int[] profile, int least, int most, int bedHalf, List<BoundingBox> claims) {
        int out = ContentCity.outFromLine(bedHalf);
        Vec3i built = ContentCity.stationStructure().isEmpty() ? Vec3i.ZERO : stationSpan(context);
        boolean stamped = built.getX() > 0 && built.getZ() > 0;
        int alongReach = stamped ? built.getX() : ContentCityStairsPiece.RUN + 1;
        int acrossReach = stamped ? built.getZ() : ContentCityStairsPiece.WIDE;
        for (int slide = 0; slide <= SLIDE; slide++) {
            for (int way = 0; way < (slide == 0 ? 1 : 2); way++) {
                int heart = wanted + (way == 0 ? slide : -slide);
                if (heart < least || heart > most) { continue; }
                if (heart - 1 < start || heart + alongReach >= start + profile.length) { continue; }
                for (int side = 0; side < 2; side++) {
                    int turn = side == 0 ? 1 : -1;
                    int near = turn > 0 ? rail.middle() + out : rail.middle() - out - ContentCityStairsPiece.WIDE + 1;
                    int leastAcross = near - 1;
                    int mostAcross = near + acrossReach;
                    BoundingBox claim = rail.alongX()
                            ? new BoundingBox(heart - 1, 0, leastAcross, heart + alongReach, 0, mostAcross)
                            : new BoundingBox(leastAcross, 0, heart - 1, mostAcross, 0, heart + alongReach);
                    if (onStreet(plan, claim)) { continue; }
                    int mid = near + ContentCityStairsPiece.WIDE / 2;
                    int top = surface(context, rail.alongX() ? heart : mid, rail.alongX() ? mid : heart);
                    if (top - profile[heart - start] < ContentCityStairsPiece.CLIMB_LEAST) { continue; }
                    claims.add(claim);
                    ContentLog.LOGGER.debug("A station on the line at {} claims rows {} to {} and across {} to {} for its way up, between rows {} and {}, the street at y {} under it", rail.at(), heart - 1, heart + alongReach, leastAcross, mostAcross, least, most, top);
                    return new int[] {heart, near, turn, top};
                }
            }
        }
        return null;
    }

    private static List<Integer> hearts(CityPlan plan, CityPlan.Rail rail, int least, int most) {
        List<Integer> found = new ArrayList<>();
        if (ContentCity.stationLength() / 2 <= 0 || most < least) { return found; }
        CityPlan.Line across = rail.alongX() ? plan.plazaColumn() : plan.plazaRow();
        int anchor = across.middle();
        int run = ContentCity.stationRun();
        found.add(Math.clamp(anchor, least, most));
        if (run <= 0) { return found; }
        for (int heart = anchor - run; heart >= least; heart -= run) { found.add(heart); }
        for (int heart = anchor + run; heart <= most; heart += run) { found.add(heart); }
        return found;
    }

    @Nullable private static int[] surfacing(GenerationContext context, CityPlan plan, CityPlan.Rail rail, int least, int span) {
        int chance = ContentCity.subwaySurfaces();
        if (chance <= 0) { return null; }
        RandomSource roll = RandomSource.create(context.seed() ^ SALT ^ (rail.at() * 341873128712L + (rail.alongX() ? 11L : 13L)));
        if (roll.nextInt(100) >= chance) { return null; }
        int depth = ContentCity.subwayDepth();
        int ramp = depth * ContentCity.railClimb(true);
        int shortest = ramp + LEAST_OPEN;
        int most = least + span - 1;
        if (most - least < shortest) { return null; }
        int run = ramp + Math.max(LEAST_OPEN, CityPlan.railTail(true));
        int half = ContentCity.stationLength() / 2;
        int[] streets = streetsOver(plan, rail);
        List<Integer> hearts = ContentCity.stations() && streets != null ? hearts(plan, rail, Math.max(least + half, streets[0]), Math.min(most - half, streets[1])) : List.of();
        int stationLeast = Integer.MAX_VALUE;
        int stationMost = Integer.MIN_VALUE;
        for (int heart : hearts) {
            stationLeast = Math.min(stationLeast, heart - half);
            stationMost = Math.max(stationMost, heart + half);
        }
        boolean anyStation = !hearts.isEmpty();
        int lowRow = streets == null ? least + run : Math.min(least + run, streets[0] - 1);
        int highRow = streets == null ? most - run : Math.max(most - run, streets[1] + 1);
        boolean canLow = lowRow - least >= shortest && (!anyStation || stationLeast > lowRow + ramp);
        boolean canHigh = most - highRow >= shortest && (!anyStation || stationMost < highRow - ramp);
        if (!canHigh && !canLow) { return null; }
        boolean high = canHigh && (!canLow || roll.nextBoolean());
        return high ? new int[] {highRow, 1} : new int[] {lowRow, -1};
    }

    private static boolean surfaced(@Nullable int[] rising, int row) {
        if (rising == null) { return false; }
        return rising[1] > 0 ? row >= rising[0] : row <= rising[0];
    }

    private static boolean onStreet(CityPlan plan, BoundingBox claim) {
        int keep = CityPlan.plazaReach() + CityPlan.WELL_HALF;
        int size = CityPlan.district();
        int plazaX = plan.plazaColumn().middle();
        int plazaZ = plan.plazaRow().middle();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int x = plazaX + dx * size;
                int z = plazaZ + dz * size;
                if (claim.maxX() >= x - keep && claim.minX() <= x + keep && claim.maxZ() >= z - keep && claim.minZ() <= z + keep) { return true; }
            }
        }
        for (CityPlan.Line line : plan.alongX()) {
            if (claim.maxZ() < line.at() || claim.minZ() > line.last()) { continue; }
            if (!line.alley() || (claim.maxX() >= line.from() && claim.minX() <= line.to())) { return true; }
        }
        for (CityPlan.Line line : plan.alongZ()) {
            if (claim.maxX() < line.at() || claim.minX() > line.last()) { continue; }
            if (!line.alley() || (claim.maxZ() >= line.from() && claim.minZ() <= line.to())) { return true; }
        }
        return false;
    }

    private static boolean claimed(List<BoundingBox> claims, CityPlan.Plot plot) {
        for (BoundingBox claim : claims) {
            if (claim.maxX() >= plot.fromX() && claim.minX() <= plot.toX() && claim.maxZ() >= plot.fromZ() && claim.minZ() <= plot.toZ()) { return true; }
        }
        return false;
    }

    private static boolean[] railBridges(GenerationContext context, CityPlan.Rail rail, int start, int[] profile, boolean[] held, int[] ground) {
        boolean[] bridged = new boolean[profile.length];
        for (int at = 0; at < profile.length; at++) {
            int across = rail.middle();
            int x = rail.alongX() ? start + at : across;
            int z = rail.alongX() ? across : start + at;
            int floor = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
            int top = surface(context, x, z);
            ground[at] = top;
            bridged[at] = top > floor || profile[at] - floor >= TRESTLE_DROP;
        }
        shortSpans(bridged);
        int from = 0;
        while (from < bridged.length) {
            if (!bridged[from]) {
                from++;
                continue;
            }
            int to = from;
            while (to + 1 < bridged.length && bridged[to + 1]) { to++; }
            int deck = profile[from];
            for (int at = from; at <= to; at++) { deck = Math.max(deck, profile[at]); }
            for (int at = from; at <= to; at++) {
                profile[at] = deck;
                held[at] = true;
            }
            from = to + 1;
        }
        return bridged;
    }

    private static ContentCityRailPiece railPiece(CityPlan.Rail rail, int from, int to, int level, boolean bridged, boolean bored) {
        if (rail.alongX()) { return new ContentCityRailPiece(from, rail.at(), to, rail.last(), level, rail.middle(), true, rail.width(), bridged, bored, rail.subway()); }
        return new ContentCityRailPiece(rail.at(), from, rail.last(), to, level, rail.middle(), false, rail.width(), bridged, bored, rail.subway());
    }

    private static int alongRail(GenerationContext context, CityPlan.Rail rail, int row) {
        int[] taken = new int[rail.width()];
        for (int at = 0; at < rail.width(); at++) {
            int across = rail.at() + at;
            taken[at] = rail.alongX() ? surface(context, row, across) : surface(context, across, row);
        }
        Arrays.sort(taken);
        return taken[taken.length / 2];
    }

    private static void shortSpans(boolean[] bridged) {
        int from = 0;
        while (from < bridged.length) {
            if (!bridged[from]) {
                from++;
                continue;
            }
            int to = from;
            while (to + 1 < bridged.length && bridged[to + 1]) { to++; }
            if (to - from + 1 < SPAN_LEAST) {
                for (int at = from; at <= to; at++) { bridged[at] = false; }
            }
            from = to + 1;
        }
    }

    private static boolean[] bridges(GenerationContext context, CityPlan.Line line, int start, int[] profile, boolean[] held, int[] ground) {
        boolean[] bridged = new boolean[profile.length];
        int drop = ContentCity.bridgeDrop();
        for (int at = 0; at < profile.length; at++) {
            int across = line.middle();
            int x = line.alongX() ? start + at : across;
            int z = line.alongX() ? across : start + at;
            int floor = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
            int top = surface(context, x, z);
            ground[at] = top;
            bridged[at] = top > floor || (drop > 0 && profile[at] - floor >= drop);
        }
        shortSpans(bridged);
        int from = 0;
        while (from < bridged.length) {
            if (!bridged[from]) {
                from++;
                continue;
            }
            int to = from;
            while (to + 1 < bridged.length && bridged[to + 1]) { to++; }
            int deck = profile[from];
            for (int at = from; at <= to; at++) { deck = Math.max(deck, profile[at]); }
            for (int at = from; at <= to; at++) {
                profile[at] = deck;
                held[at] = true;
            }
            from = to + 1;
        }
        return bridged;
    }

    private static int alongLine(GenerationContext context, CityPlan.Line line, int row) {
        int[] taken = new int[line.width()];
        for (int at = 0; at < line.width(); at++) {
            int across = line.at() + at;
            taken[at] = line.alongX() ? surface(context, row, across) : surface(context, across, row);
        }
        Arrays.sort(taken);
        return taken[taken.length / 2];
    }

    private static int surface(GenerationContext context, int x, int z) {
        return context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()) - 1;
    }

    @Override @Nonnull public StructureType<?> type() { return TYPE; }
}
