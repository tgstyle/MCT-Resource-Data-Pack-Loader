package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ContentCityStructureSeat {
    private static final int FOOTING_STEP = 4;
    private static final int[] SLIDES = {2, 4, 6, 8, 10, 12, -2, -4, -6, -8, -10, -12};
    private static final int STRIP_MOST = 5;
    private static final int DECK_HEAD = 3;
    private static final int CORNER_REACH = 3;
    private static final int STREET_BESIDE = 2;
    private static final int STREET_CLAMP = 4;
    private static final int LEAN = 3;
    @Nullable private static volatile List<Block> doorBlocks;

    private ContentCityStructureSeat() {}

    @Nullable static ContentCityStructure.Seated seat(GenerationContext context, CityGround ground, CityPlan plan, CityPlan.Plot plot, Map<CityPlan.Line, ContentCityStructure.Laid> streets, List<ContentCityStructure.Well> wells) {
        ContentCityStructure.District district = ContentCityStructureSite.district(context, ground, plan);
        CityPlan.Plot placed = placed(context, ground, plan, district, plot);
        if (placed == null) { return null; }
        ContentCityStructure.Street fronted = district.streets().get(placed.street());
        int[] profile = fronted.grade();
        int level = floor(ground, plan, district, placed, profile[Mth.clamp(placed.middleAlong() - fronted.start(), 0, profile.length - 1)]);
        BoundingBox footprint = new BoundingBox(placed.fromX(), level, placed.fromZ(), placed.toX(), level + placed.def().height(), placed.toZ());
        Rotation turn = facing(placed);
        List<CityPlan> plans = ContentCityStructureSite.around(ground, plan, placed);
        int[] keep = ContentCityStructureSite.keepAround(plans, placed, wells);
        int[] roads = ContentCityStructureSite.roadsAround(plans, placed);
        if (!placed.def().template()) {
            return new ContentCityStructure.Seated(List.of(new ContentCityFarmPiece(placed.fromX(), placed.fromZ(), placed.toX(), placed.toZ(), level, placed.def().height(),
                    placed.def().edge(), placed.def().soil(), placed.def().ground(), String.join(",", placed.def().crops()), placed.def().water(), Math.max(1, placed.def().rowWidth()), placed.def().key().toString(), turn, keep, roads)), footprint, Set.of(), placed, level);
        }
        ResourceLocation template = ResourceLocation.tryParse(placed.def().structure());
        StructureMapDef map = template == null ? null : ContentStructureMaps.def(template);
        if (map != null) {
            StructurePiecesBuilder cells = new StructurePiecesBuilder();
            ContentMapStructure.lay(context, map, Hashes.mix(context.seed(), placed.fromX(), 2, placed.fromZ()), turn, placed.fromX(), placed.fromZ(), level - map.ground() * map.cell(), cells);
            return new ContentCityStructure.Seated(new ArrayList<>(cells.build().pieces()), footprint, Set.of(), placed, level);
        }
        if (template == null || context.structureTemplateManager().get(template).isEmpty()) {
            ContentCity.missing(placed.def());
            return null;
        }
        StructureTemplate built = context.structureTemplateManager().get(template).orElseThrow();
        Rotation laid = turn.getRotated(ContentVillages.turnOf(placed.def()));
        Vec3i span = built.getSize(laid);
        BlockPos corner = new BlockPos(placed.fromX() + ContentImprint.backX(laid, span), level, placed.fromZ() + ContentImprint.backZ(laid, span));
        int[] strip = strip(placed);
        ContentCityStructure.Laid street = streets.get(placed.street());
        int[] deck = new int[0];
        byte[] decked = new byte[0];
        if (strip.length == 4 && street != null) {
            boolean alongX = placed.street().alongX();
            int least = alongX ? strip[0] : strip[1];
            int most = alongX ? strip[2] : strip[3];
            int first = least - CORNER_REACH;
            int last = most + CORNER_REACH;
            deck = new int[last - first + 1 + DECK_HEAD];
            decked = new byte[last - first + 1];
            deck[0] = alongX ? 1 : 0;
            deck[1] = first;
            deck[2] = placed.lower() ? placed.street().last() : placed.street().at();
            for (int along = first; along <= last; along++) {
                if ((along < least || along > most) && (along < street.start() || along >= street.start() + street.profile().length)) {
                    deck[DECK_HEAD + along - first] = Integer.MIN_VALUE;
                    continue;
                }
                int index = Mth.clamp(along - street.start(), 0, street.profile().length - 1);
                deck[DECK_HEAD + along - first] = street.profile()[index];
                decked[along - first] = (byte) (street.bridged()[index] ? 1 : 0);
            }
        }
        StructurePiece piece = new ContentCityPlotPiece(context.structureTemplateManager(), template, laid, placed.def().integrity(), corner, placed.def().key().toString(), ContentVillages.vanillaHouse(placed.def()), level, keep, roads);
        List<StructurePiece> pieces = strip.length == 4 ? List.of(piece, new ContentCityDeckPiece(level, keep, strip, deck, decked, DECK_HEAD)) : List.of(piece);
        return new ContentCityStructure.Seated(pieces, piece.getBoundingBox(), doors(built, corner, laid), placed, level);
    }

    private static int floor(CityGround ground, CityPlan plan, ContentCityStructure.District district, CityPlan.Plot placed, int graded) {
        int sea = ground.sea();
        int beside = besideGrade(plan, district, placed);
        if (beside != Integer.MIN_VALUE) {
            if (beside >= sea || groundAverage(ground, placed) >= sea) {
                if (beside != graded) { ContentLog.LOGGER.debug("Village plot {} at {}, {} stands at the grade of the street beside it, y {}, instead of y {} on the street it fronts", placed.def().key(), placed.fromX(), placed.fromZ(), beside, graded); }
                return beside;
            }
            ContentLog.LOGGER.debug("Village plot {} at {}, {} stands on water, so it is held up to the surface at y {} instead of y {} on the grade of the street beside it", placed.def().key(), placed.fromX(), placed.fromZ(), sea, beside);
            return sea;
        }
        int found = Math.max(groundAverage(ground, placed), sea) - 1;
        if (ContentVillages.vanillaField(placed.def())) {
            ContentLog.LOGGER.debug("Village field {} at {}, {} has no street within {} blocks, so it stands on its own ground at y {} from the noise surface instead of y {} on the grade of the street it fronts, never leaned over its low side", placed.def().key(), placed.fromX(), placed.fromZ(), STREET_BESIDE, found, graded);
            return found;
        }
        int lowest = lowestIn(ground, placed);
        int leaned = found <= lowest + LEAN ? found : Math.min(found, streetClamped(plan, district, placed, lowest + LEAN));
        if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Village plot {} at {}, {} has no street within {} blocks, so it stands on its own ground at y {} from the noise surface{} instead of y {} on the grade of the street it fronts", placed.def().key(), placed.fromX(), placed.fromZ(), STREET_BESIDE, leaned, leaned == found ? "" : ", leaned down from y " + found + " over its low side", graded); }
        return leaned;
    }

    private static int besideGrade(CityPlan plan, ContentCityStructure.District district, CityPlan.Plot placed) {
        List<CityPlan.Line> lines = new ArrayList<>();
        lines.add(placed.street());
        lines.addAll(plan.alongX());
        lines.addAll(plan.alongZ());
        for (CityPlan.Line line : lines) {
            ContentCityStructure.Street street = district.streets().get(line);
            if (street == null || gap(line, placed) > STREET_BESIDE) { continue; }
            int[] grade = street.grade();
            int middle = line.alongX() ? (placed.fromX() + placed.toX()) / 2 : (placed.fromZ() + placed.toZ()) / 2;
            return grade[Mth.clamp(middle - street.start(), 0, grade.length - 1)];
        }
        return Integer.MIN_VALUE;
    }

    private static int gap(CityPlan.Line line, CityPlan.Plot placed) {
        int fromX = line.alongX() ? line.from() : line.at();
        int toX = line.alongX() ? line.to() : line.last();
        int fromZ = line.alongX() ? line.at() : line.from();
        int toZ = line.alongX() ? line.last() : line.to();
        return Math.max(Math.max(fromX - placed.toX(), placed.fromX() - toX), Math.max(fromZ - placed.toZ(), placed.fromZ() - toZ));
    }

    private static int groundAverage(CityGround ground, CityPlan.Plot placed) { return groundAverage(ground, placed.fromX(), placed.fromZ(), placed.toX(), placed.toZ()); }

    static int groundAverage(CityGround ground, int fromX, int fromZ, int toX, int toZ) {
        long total = 0;
        int count = 0;
        for (int x = fromX; x <= toX; x++) {
            for (int z = fromZ; z <= toZ; z++) {
                total += ground.floor(x, z) + 1;
                count++;
            }
        }
        return (int) (total / count);
    }

    private static int lowestIn(CityGround ground, CityPlan.Plot placed) {
        int lowest = Integer.MAX_VALUE;
        for (int x = placed.fromX(); x <= placed.toX(); x++) {
            for (int z = placed.fromZ(); z <= placed.toZ(); z++) { lowest = Math.min(lowest, ground.floor(x, z)); }
        }
        return Math.max(lowest, ground.sea());
    }

    private static int streetClamped(CityPlan plan, ContentCityStructure.District district, CityPlan.Plot placed, int leaned) {
        ContentCityStructure.Well nearest = null;
        long closest = Long.MAX_VALUE;
        for (ContentCityStructure.Well well : district.wells()) {
            long dx = (well.box().minX() + well.box().maxX()) / 2 - (placed.fromX() + placed.toX()) / 2;
            long dz = (well.box().minZ() + well.box().maxZ()) / 2 - (placed.fromZ() + placed.toZ()) / 2;
            if (dx * dx + dz * dz < closest) {
                closest = dx * dx + dz * dz;
                nearest = well;
            }
        }
        if (nearest == null) { return leaned; }
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line line : lines) {
                if (gap(line, placed) <= STREET_CLAMP) { return Math.max(leaned, nearest.level() - 1); }
            }
        }
        return leaned;
    }

    @Nullable private static CityPlan.Plot fitted(CityGround ground, CityPlan plan, CityPlan.Plot plot, int[] profile, int start, List<ContentCityStructure.Well> wells) {
        int misfit = misfit(ground, plot, profile, start);
        if (misfit == 0) { return plot; }
        int toward = 1;
        if (!wells.isEmpty()) {
            BoundingBox box = wells.get(0).box();
            int middle = plot.street().alongX() ? (box.minX() + box.maxX()) / 2 : (box.minZ() + box.maxZ()) / 2;
            toward = middle - plot.middleAlong() >= 0 ? 1 : -1;
        }
        CityPlan.Plot best = misfit == Integer.MAX_VALUE ? null : plot;
        int bestMisfit = misfit;
        for (int step : SLIDES) {
            CityPlan.Plot tried = plot.slid(step * toward);
            if (plan.blocks(tried, plot) || tried.middleAlong() < start || tried.middleAlong() >= start + profile.length) { continue; }
            int triedMisfit = misfit(ground, tried, profile, start);
            if (triedMisfit < bestMisfit) {
                bestMisfit = triedMisfit;
                best = tried;
                if (bestMisfit == 0) { break; }
            }
        }
        if (best != null && best != plot) { ContentLog.LOGGER.debug("Village plot {} at {}, {} slid to {}, {} along its street to a better fit, {} block(s) of apron in total instead of {}", plot.def().key(), plot.fromX(), plot.fromZ(), best.fromX(), best.fromZ(), bestMisfit, misfit == Integer.MAX_VALUE ? "too deep" : String.valueOf(misfit)); }
        return best;
    }

    private static int misfit(CityGround ground, CityPlan.Plot plot, int[] profile, int start) {
        int allow = plot.def().apron();
        int stand = profile[Mth.clamp(plot.middleAlong() - start, 0, profile.length - 1)];
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int total = 0;
        for (int x = plot.fromX(); x <= plot.toX() + FOOTING_STEP - 1; x += FOOTING_STEP) {
            for (int z = plot.fromZ(); z <= plot.toZ() + FOOTING_STEP - 1; z += FOOTING_STEP) {
                int found = ground.surface(Math.min(x, plot.toX()), Math.min(z, plot.toZ()));
                lowest = Math.min(lowest, found);
                highest = Math.max(highest, found);
                int gap = stand - found;
                if (gap > allow || -gap > allow || highest - lowest > allow) { return Integer.MAX_VALUE; }
                total += Math.abs(gap);
            }
        }
        return total;
    }

    @Nullable static CityPlan.Plot placed(GenerationContext context, CityGround ground, CityPlan plan, ContentCityStructure.District district, CityPlan.Plot plot) {
        if (!plan.emits((plot.fromX() + plot.toX()) / 2, (plot.fromZ() + plot.toZ()) / 2)) { return null; }
        ContentCityStructure.Street street = district.streets().get(plot.street());
        if (street == null) { return null; }
        CityPlan.Plot placed = plot.back() ? plot : fitted(ground, plan, plot, street.grade(), street.start(), district.wells());
        if (placed == null) {
            ContentLog.LOGGER.debug("Village plot {} at {}, {} would stand on ground more than {} block(s) off its street's grade and found no better fit within 12 along its street, so it is not built", plot.def().key(), plot.fromX(), plot.fromZ(), plot.def().apron());
            return null;
        }
        return tunneled(context, ground, plan, district, placed) ? null : placed;
    }

    private static boolean tunneled(GenerationContext context, CityGround ground, CityPlan plan, ContentCityStructure.District own, CityPlan.Plot placed) {
        Map<CityPlan.Line, ContentCityStructure.Street> ownStreets = own.streets();
        CityPlan.Line beside = null;
        for (CityPlan other : ContentCityStructureSite.around(ground, plan, placed)) {
            ContentCityStructure.District district = other == plan ? own : null;
            for (List<CityPlan.Line> lines : List.of(other.alongX(), other.alongZ())) {
                for (CityPlan.Line line : lines) {
                    if (other != plan && ownStreets.containsKey(line)) { continue; }
                    boolean alongX = line.alongX();
                    if ((alongX ? placed.fromZ() : placed.fromX()) > line.last() + ContentCityStructure.FRONT_REACH || (alongX ? placed.toZ() : placed.toX()) < line.at() - ContentCityStructure.FRONT_REACH) { continue; }
                    if (district == null) { district = ContentCityStructureSite.district(context, ground, other); }
                    ContentCityStructure.Street street = district.streets().get(line);
                    if (street == null) { continue; }
                    String fronted = boredRows(line, street, (alongX ? placed.fromX() : placed.fromZ()) - 1, (alongX ? placed.toX() : placed.toZ()) + 1);
                    if (!fronted.isEmpty()) {
                        ContentLog.LOGGER.debug("Village plot {} at {}, {} to {}, {} makes way for the street tunnel of the road at {}, {}: the road is roofed over the row(s){} its frontage stands on, so those rows are buried and offer it no way out", placed.def().key(), placed.fromX(), placed.fromZ(), placed.toX(), placed.toZ(), alongX ? line.from() : line.at(), alongX ? line.at() : line.from(), fronted);
                        return true;
                    }
                    if (beside == null && ContentLog.LOGGER.debugEnabled() && !boredRows(line, street, line.from(), line.to()).isEmpty()) { beside = line; }
                }
            }
        }
        if (beside != null) { ContentLog.LOGGER.debug("Village plot {} at {}, {} to {}, {} stands beside the tunneled road at {}, {} and is kept: none of the road's roofed rows lie on its frontage", placed.def().key(), placed.fromX(), placed.fromZ(), placed.toX(), placed.toZ(), beside.alongX() ? beside.from() : beside.at(), beside.alongX() ? beside.at() : beside.from()); }
        return false;
    }

    private static String boredRows(CityPlan.Line line, ContentCityStructure.Street street, int least, int most) {
        StringBuilder rows = new StringBuilder();
        int first = Math.max(Math.max(street.start(), line.from()), least);
        int last = Math.min(Math.min(street.start() + street.bored().length - 1, line.to()), most);
        for (int row = first; row <= last; row++) {
            if (street.bored()[row - street.start()]) { rows.append(' ').append(row); }
        }
        return rows.toString();
    }

    private static int[] strip(CityPlan.Plot placed) {
        if (placed.back()) { return new int[0]; }
        CityPlan.Line street = placed.street();
        if (street.alongX()) {
            int near = placed.lower() ? street.last() + 1 : placed.toZ() + 1;
            int far = placed.lower() ? placed.fromZ() - 1 : street.at() - 1;
            return far < near || far - near >= STRIP_MOST ? new int[0] : new int[] {placed.fromX(), near, placed.toX(), far};
        }
        int near = placed.lower() ? street.last() + 1 : placed.toX() + 1;
        int far = placed.lower() ? placed.fromX() - 1 : street.at() - 1;
        return far < near || far - near >= STRIP_MOST ? new int[0] : new int[] {near, placed.fromZ(), far, placed.toZ()};
    }

    private static Set<Long> doors(StructureTemplate built, BlockPos corner, Rotation turn) {
        List<Block> kinds = doorBlocks;
        if (kinds == null) {
            List<Block> found = new ArrayList<>();
            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                if (block.defaultBlockState().is(BlockTags.DOORS)) { found.add(block); }
            }
            kinds = List.copyOf(found);
            doorBlocks = kinds;
        }
        Set<Long> found = new HashSet<>();
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(turn);
        for (Block kind : kinds) {
            for (StructureTemplate.StructureBlockInfo info : built.filterBlocks(corner, settings, kind)) { found.add(ChunkPos.asLong(info.pos().getX(), info.pos().getZ())); }
        }
        return found;
    }

    private static Rotation facing(CityPlan.Plot plot) {
        if (plot.street().alongX()) { return plot.frontsLow() ? Rotation.NONE : Rotation.CLOCKWISE_180; }
        return plot.frontsLow() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
    }
}
