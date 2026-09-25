package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class ContentCityStructureLamps {
    private static final int LAMP_LEAST = 7;
    private static final int LAMP_SPREAD = 6;
    private static final int LAMP_SHORTEST = 4;
    private static final int DOOR_REACH = 2;

    private ContentCityStructureLamps() {}

    @Nullable private static ResourceLocation lampTemplate(GenerationContext context) {
        String named = ContentCity.lampStructure();
        ResourceLocation template = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        if (template != null && context.structureTemplateManager().get(template).isEmpty()) {
            ContentCity.missingLamp(named);
            return null;
        }
        return template;
    }

    static void lamps(GenerationContext context, CityPlan plan, CityPlan.Line line, @Nullable ContentCityStructure.Laid laid, Collection<CityRails.Laid> tracks, List<ContentCityStructure.Well> wells, Set<Long> doors, StructurePiecesBuilder builder) {
        if (laid == null) { return; }
        CityCross cross = CityCross.of(line);
        ResourceLocation template = lampTemplate(context);
        if (template == null && ContentCity.lampBlock().isEmpty()) { return; }
        int from = line.from();
        int to = line.to();
        if (to - from < LAMP_SHORTEST) { return; }
        RandomSource roll = RandomSource.create(context.seed() ^ ContentCityStructure.SALT ^ (line.at() * 341873128712L + (line.alongX() ? 1L : 2L)));
        List<Integer> along = new ArrayList<>();
        along.add(from);
        for (int step = from + LAMP_LEAST + roll.nextInt(LAMP_SPREAD); step < to - 1; step += LAMP_LEAST + roll.nextInt(LAMP_SPREAD)) { along.add(step); }
        along.add(to);
        for (CityPlan.Line other : plan.crossing(line)) {
            if (!other.covers(line.middle()) || other.last() < from || other.at() > to) { continue; }
            int meeting = Math.max(from, other.at());
            if (!along.contains(meeting)) { along.add(meeting); }
        }
        int skipped = 0;
        int raised = 0;
        for (int spot : along) {
            for (int side = -1; side <= 1; side += 2) {
                int row = curbRow(tracks, line, along, spot, side > 0);
                if (row == Integer.MIN_VALUE) { continue; }
                int index = row - laid.start();
                if (index < 0 || index >= laid.profile().length || laid.bored()[index] || laid.bridged()[index]) { continue; }
                int across = line.middle() + side * cross.lampOffset();
                int x = line.alongX() ? row : across;
                int z = line.alongX() ? across : row;
                if (!plan.emitsAlong(line, row)) { continue; }
                if (!standLamp(context, plan, line, wells, doors, template, x, laid.profile()[index] + 1, z, builder)) {
                    skipped++;
                    continue;
                }
                raised++;
                break;
            }
        }
        if (skipped > 0) { ContentLog.LOGGER.debug("The {} at {} raises {} lamp(s) and passes over {} side(s) that would stand on the plaza, on another street or alley, before a door or across the district edge", line.alley() ? "alley" : "street", line.at(), raised, skipped); }
    }

    private static int curbRow(Collection<CityRails.Laid> tracks, CityPlan.Line line, List<Integer> along, int spot, boolean high) {
        CityRails.Station opening = CityRails.openingAt(tracks, line, spot, high);
        if (opening == null) { return spot; }
        int least = opening.first();
        int most = opening.last();
        int near = spot - least <= most - spot ? least : most;
        int far = near == least ? most : least;
        for (int row : new int[] {near, far}) {
            if (row < line.from() || row > line.to() || CityRails.openingAt(tracks, line, row, high) != null) { continue; }
            boolean crowded = false;
            for (int other : along) {
                if (other != spot && Math.abs(other - row) < LAMP_LEAST) {
                    crowded = true;
                    break;
                }
            }
            if (!crowded) {
                ContentLog.LOGGER.debug("Slid the lamp at row {} of the {} at {} to row {}, clear of the subway station opening at row {}", spot, line.alley() ? "alley" : "street", line.at(), row, least);
                return row;
            }
        }
        ContentLog.LOGGER.debug("Skipped the lamp at row {} of the {} at {}, which stands in the subway station opening at row {} with no room to slide along the curb", spot, line.alley() ? "alley" : "street", line.at(), least);
        return Integer.MIN_VALUE;
    }

    private static boolean standLamp(GenerationContext context, CityPlan plan, @Nullable CityPlan.Line line, List<ContentCityStructure.Well> wells, Set<Long> doors, @Nullable ResourceLocation template, int x, int foot, int z, StructurePiecesBuilder builder) {
        int height = ContentCity.lampHeight();
        Vec3i span = template == null ? new Vec3i(3, height + 1, 3) : context.structureTemplateManager().get(template).orElseThrow().getSize(Rotation.NONE);
        int leastX = x - span.getX() / 2;
        int leastZ = z - span.getZ() / 2;
        BoundingBox stood = new BoundingBox(leastX, 0, leastZ, leastX + span.getX() - 1, 0, leastZ + span.getZ() - 1);
        if (cleared(plan, line, wells, stood) || beforeDoor(doors, x, z)) { return false; }
        if (template != null) { builder.addPiece(new ContentCityPlotPiece(context.structureTemplateManager(), template, Rotation.NONE, 100, new BlockPos(leastX, foot, leastZ), true)); }
        else { builder.addPiece(new ContentCityLampPiece(x, foot, z, height)); }
        return true;
    }

    static void bulbLamps(GenerationContext context, CityPlan plan, ContentCityStructure.Bulb bulb, List<ContentCityStructure.Well> wells, Set<Long> doors, StructurePiecesBuilder builder) {
        ResourceLocation template = lampTemplate(context);
        if (template == null && ContentCity.lampBlock().isEmpty()) { return; }
        ContentCityBulbPiece.Court court = bulb.court();
        boolean walked = CityCross.of(bulb.line()).walk() > 0;
        int ring = walked ? court.reach() : court.reach() + 1;
        int spots = Math.max(1, (int) Math.round(2.0D * Math.PI * ring));
        RandomSource roll = RandomSource.create(context.seed() ^ ContentCityStructure.SALT ^ ((long) court.centerX() << 32 ^ court.centerZ()));
        int raised = 0;
        for (int step = 0; step + LAMP_SHORTEST < spots; step += LAMP_LEAST + roll.nextInt(LAMP_SPREAD)) {
            double angle = 2.0D * Math.PI * step / spots;
            int x = Integer.MIN_VALUE;
            int z = Integer.MIN_VALUE;
            for (int back = 0; back <= 2 && x == Integer.MIN_VALUE; back++) {
                int triedX = court.centerX() + (int) Math.round((ring - back) * Math.cos(angle));
                int triedZ = court.centerZ() + (int) Math.round((ring - back) * Math.sin(angle));
                if (walked && court.unpavedAt(triedX, triedZ)) { continue; }
                x = triedX;
                z = triedZ;
            }
            if (x == Integer.MIN_VALUE || court.throatAt(x, z)) { continue; }
            if (standLamp(context, plan, null, wells, doors, template, x, court.level() + 1, z, builder)) { raised++; }
        }
        if (raised > 0) { ContentLog.LOGGER.debug("Raised {} lamp post(s) around the cul-de-sac at {}, {}", raised, court.centerX(), court.centerZ()); }
    }

    private static boolean beforeDoor(Set<Long> doors, int x, int z) {
        if (doors.isEmpty()) { return false; }
        for (int dx = -DOOR_REACH; dx <= DOOR_REACH; dx++) {
            for (int dz = -DOOR_REACH; dz <= DOOR_REACH; dz++) {
                if (doors.contains(ChunkPos.asLong(x + dx, z + dz))) { return true; }
            }
        }
        return false;
    }

    private static boolean cleared(CityPlan plan, @Nullable CityPlan.Line line, List<ContentCityStructure.Well> wells, BoundingBox stood) {
        if (line != null && ((line.alongX() ? stood.minX() : stood.minZ()) < line.from() || (line.alongX() ? stood.maxX() : stood.maxZ()) > line.to())) { return true; }
        int reach = CityPlan.plazaPaved() + CityPlan.walkWidth() + ContentCityPlazaPiece.MOUTH_MOST;
        for (ContentCityStructure.Well well : wells) {
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

    static void verges(CityPlan.Line line, CityPlan plan, int[] profile, @Nullable ContentCityStructure.Laid laid, List<ContentCityStructure.Seated> seated, List<ContentCityStructure.Well> wells, Set<Long> doors, StructurePiecesBuilder builder) {
        if (ContentCity.decorNames().isEmpty()) { return; }
        CityCross cross = CityCross.of(line);
        int verge = cross.curb() + 1;
        int start = plan.spanStart(line);
        int reach = CityPlan.plazaReach();
        IntList spots = new IntArrayList();
        IntList levels = new IntArrayList();
        IntList across = new IntArrayList();
        for (int at = 0; at < profile.length; at++) {
            int along = start + at;
            if (Math.floorMod(along, ContentCityStructure.VERGE_RUN) != 0 || !line.covers(along) || !plan.emitsAlong(line, along)) { continue; }
            if (laid != null && laid.bored()[at]) { continue; }
            for (int side = -1; side <= 1; side += 2) {
                int sideways = line.middle() + side * verge;
                int x = line.alongX() ? along : sideways;
                int z = line.alongX() ? sideways : along;
                if (underPiece(seated, wells, reach, x, z) || beforeDoor(doors, x, z)) { continue; }
                spots.add(along);
                levels.add(profile[at]);
                across.add(sideways);
            }
        }
        if (spots.isEmpty()) { return; }
        int[] alongs = spots.toIntArray();
        int[] heights = levels.toIntArray();
        int[] sides = across.toIntArray();
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

    private static boolean underPiece(List<ContentCityStructure.Seated> seated, List<ContentCityStructure.Well> wells, int reach, int x, int z) {
        for (ContentCityStructure.Seated held : seated) {
            BoundingBox box = held.box();
            if (x >= box.minX() && x <= box.maxX() && z >= box.minZ() && z <= box.maxZ()) { return true; }
        }
        for (ContentCityStructure.Well well : wells) {
            BoundingBox box = well.box();
            if (x >= box.minX() - reach && x <= box.maxX() + reach && z >= box.minZ() - reach && z <= box.maxZ() + reach) { return true; }
        }
        return false;
    }
}
