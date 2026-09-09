package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ContentCityStructure extends Structure {
    public static final Codec<ContentCityStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(settingsCodec(instance)).apply(instance, ContentCityStructure::new));
    public static final StructureType<ContentCityStructure> TYPE = () -> CODEC;

    private static final int LAMP_LEAST = 7;
    private static final int LAMP_SPREAD = 6;
    private static final long SALT = 0x51A3C7E9B1D0F42BL;
    private static final int TRESTLE_DROP = 3;
    private static final int SPAN_LEAST = 4;
    private static final int VERGE_RUN = 3;

    public ContentCityStructure(StructureSettings settings) { super(settings); }

    @Override @Nonnull protected Optional<GenerationStub> findGenerationPoint(@Nonnull GenerationContext context) {
        if (!ContentCity.laying()) { return Optional.empty(); }
        ChunkPos chunk = context.chunkPos();
        if (Math.floorMod(chunk.x, CityPlan.CHUNKS) != 0 || Math.floorMod(chunk.z, CityPlan.CHUNKS) != 0) { return Optional.empty(); }
        CityPlan plan = CityPlan.of(context.seed(), Math.floorDiv(chunk.x, CityPlan.CHUNKS), Math.floorDiv(chunk.z, CityPlan.CHUNKS));
        if (plan == null) { return Optional.empty(); }
        BlockPos origin = new BlockPos(plan.windowX(), context.chunkGenerator().getSeaLevel(), plan.windowZ());
        return Optional.of(new GenerationStub(origin, builder -> pieces(context, plan, builder)));
    }

    private static void pieces(GenerationContext context, CityPlan plan, StructurePiecesBuilder builder) {
        Map<CityPlan.Junction, Integer> levels = new HashMap<>();
        for (CityPlan.Junction junction : plan.junctions()) { levels.put(junction, junctionLevel(context, junction)); }
        int run = CityPlan.flatRun();
        Map<CityPlan.Rail, int[]> rails = new HashMap<>();
        for (CityPlan.Rail rail : plan.rails()) { rails.put(rail, layRail(context, plan, rail, builder)); }
        Map<CityPlan.Line, int[]> profiles = new HashMap<>();
        for (CityPlan.Line line : plan.alongX()) { profiles.put(line, lay(context, plan, line, levels, run, rails, builder)); }
        for (CityPlan.Line line : plan.alongZ()) { profiles.put(line, lay(context, plan, line, levels, run, rails, builder)); }
        for (CityPlan.Plot plot : plan.plots()) { seat(context, plan, plot, profiles, builder); }
        for (Map.Entry<CityPlan.Line, int[]> held : profiles.entrySet()) { verges(held.getKey(), plan, held.getValue(), builder); }
        plaza(context, plan, levels, builder);
        for (Map.Entry<CityPlan.Junction, Integer> crossing : levels.entrySet()) {
            CityPlan.Junction junction = crossing.getKey();
            if (!junction.alongX().covers(junction.alongZ().middle()) || !junction.alongZ().covers(junction.alongX().middle())) { continue; }
            if (!plan.emits(junction.fromX(), junction.fromZ())) { continue; }
            PathIntersectDef design = ContentPathIntersects.forJunction(context.seed(), junction.fromX(), junction.fromZ());
            if (design == null) { continue; }
            builder.addPiece(new ContentCityIntersectPiece(crossing.getValue(), design.key().toString(), junction.fromX(), junction.toX(), junction.fromZ(), junction.toZ(), design.mouth().size()));
        }
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

    private static int[] lay(GenerationContext context, CityPlan plan, CityPlan.Line line, Map<CityPlan.Junction, Integer> levels, int run, Map<CityPlan.Rail, int[]> rails, StructurePiecesBuilder builder) {
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
        for (Map.Entry<CityPlan.Rail, int[]> laid : rails.entrySet()) {
            CityPlan.Rail rail = laid.getKey();
            if (rail.alongX() == line.alongX()) { continue; }
            int[] over = laid.getValue();
            int seat = Math.floorMod(line.middle() - (rail.alongX() ? plan.originX() : plan.originZ()) + CityPlan.railTail(), over.length);
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
        int from = 0;
        for (int at = 1; at <= profile.length; at++) {
            boolean ends = at == profile.length || profile[at] != profile[from] || mine[at] != mine[from] || bridged[at] != bridged[from] || bored[at] != bored[from];
            if (!ends) { continue; }
            if (mine[from] && line.covers(start + from) && line.covers(start + at - 1) && plan.emitsAlong(line, start + from)) { builder.addPiece(piece(line, start + from, start + at - 1, profile[from], paving, bridged[from], bored[from])); }
            from = at;
        }
        lamps(context, plan, line, start, profile, mine, builder);
        ends(context, plan, line, start, profile, paving, bridged, builder);
        return profile;
    }

    private static void ends(GenerationContext context, CityPlan plan, CityPlan.Line line, int start, int[] profile, String paving, boolean[] bridged, StructurePiecesBuilder builder) {
        if (!line.endsLow() && !line.endsHigh()) { return; }
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

    private static void lamps(GenerationContext context, CityPlan plan, CityPlan.Line line, int start, int[] profile, boolean[] mine, StructurePiecesBuilder builder) {
        CityCross cross = CityCross.of(line.width(), line.alley());
        if (cross.curb() <= 0) { return; }
        String named = ContentCity.lampStructure();
        ResourceLocation template = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        if (template != null && context.structureTemplateManager().get(template).isEmpty()) {
            ContentCity.missingLamp(named);
            template = null;
        }
        if (template == null && ContentCity.lampBlock().isEmpty()) { return; }
        int height = ContentCity.lampHeight();
        RandomSource roll = RandomSource.create(context.seed() ^ SALT ^ (line.at() * 341873128712L + (line.alongX() ? 1L : 2L)));
        for (int at = LAMP_LEAST + roll.nextInt(LAMP_SPREAD); at < profile.length; at += LAMP_LEAST + roll.nextInt(LAMP_SPREAD)) {
            if (!mine[at]) { continue; }
            for (int side = -1; side <= 1; side += 2) {
                int across = line.middle() + side * cross.lampOffset();
                int x = line.alongX() ? start + at : across;
                int z = line.alongX() ? across : start + at;
                if (!plan.emits(x, z)) { continue; }
                if (template != null) { builder.addPiece(new ContentCityPlotPiece(context.structureTemplateManager(), template, Rotation.NONE, 100, new BlockPos(x, profile[at] + 1, z), "")); }
                else { builder.addPiece(new ContentCityLampPiece(x, profile[at] + 1, z, height)); }
            }
        }
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

    private static void plaza(GenerationContext context, CityPlan plan, Map<CityPlan.Junction, Integer> levels, StructurePiecesBuilder builder) {
        CityPlan.Junction middle = new CityPlan.Junction(plan.plazaRow(), plan.plazaColumn());
        Integer level = levels.get(middle);
        if (level == null || !middle.alongX().covers(middle.alongZ().middle()) || !middle.alongZ().covers(middle.alongX().middle())) { return; }
        RandomSource roll = RandomSource.create(context.seed() ^ SALT ^ (plan.originX() * 341873128712L + plan.originZ() * 132897987541L));
        String named = ContentCity.wellStructure(roll);
        if (named == null) { return; }
        ResourceLocation template = ResourceLocation.tryParse(named);
        if (template == null || context.structureTemplateManager().get(template).isEmpty()) {
            ContentCity.missingWell(named);
            return;
        }
        Vec3i span = context.structureTemplateManager().get(template).orElseThrow().getSize(Rotation.NONE);
        int x = (middle.fromX() + middle.toX()) / 2 - span.getX() / 2;
        int z = (middle.fromZ() + middle.toZ()) / 2 - span.getZ() / 2;
        if (!plan.emits(x, z)) { return; }
        builder.addPiece(new ContentCityPlotPiece(context.structureTemplateManager(), template, Rotation.NONE, 100, new BlockPos(x, level, z), ""));
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

    private static int[] layRail(GenerationContext context, CityPlan plan, CityPlan.Rail rail, StructurePiecesBuilder builder) {
        int tail = CityPlan.railTail();
        int start = (rail.alongX() ? plan.originX() : plan.originZ()) - tail;
        int[] profile = new int[CityPlan.DISTRICT + tail * 2];
        boolean[] held = new boolean[profile.length];
        int[] ground = new int[profile.length];
        for (int at = 0; at < profile.length; at++) { profile[at] = alongRail(context, rail, start + at); }
        CityGrade.flatRuns(profile, start, ContentCity.railClimb());
        CityGrade.climb(profile, held, ContentCity.railClimb());
        boolean[] bridged = railBridges(context, rail, start, profile, held, ground);
        CityGrade.climb(profile, held, ContentCity.railClimb());
        boolean[] bored = CityGrade.buriedRuns(profile, ground, held, bridged, ContentCity.railTunnelDepth());
        for (int at = 0; at < bored.length; at++) { held[at] |= bored[at]; }
        CityGrade.climb(profile, held, ContentCity.railClimb());
        int from = 0;
        for (int at = 1; at <= profile.length; at++) {
            boolean ends = at == profile.length || profile[at] != profile[from] || bridged[at] != bridged[from] || bored[at] != bored[from];
            if (!ends) { continue; }
            builder.addPiece(railPiece(rail, start + from, start + at - 1, profile[from], bridged[from], bored[from]));
            from = at;
        }
        return profile;
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
        if (rail.alongX()) { return new ContentCityRailPiece(from, rail.at(), to, rail.last(), level, rail.middle(), true, rail.width(), bridged, bored); }
        return new ContentCityRailPiece(rail.at(), from, rail.last(), to, level, rail.middle(), false, rail.width(), bridged, bored);
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
        return context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
    }

    @Override @Nonnull public StructureType<?> type() { return TYPE; }
}
