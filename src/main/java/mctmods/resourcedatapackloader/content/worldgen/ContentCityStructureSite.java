package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class ContentCityStructureSite {
    private static final int KEEP_REACH = 6;
    private static final int HILL_RING = 4;
    private static final int PULL_LEAST = 7;
    private static final int PULL_BESIDE = 3;
    private static final int REACH_STEP = 7;
    private static final int TUNNEL_REACH = 98;
    private static final Map<CityPlan, int[]> KEPT = Collections.synchronizedMap(new WeakHashMap<>());

    private ContentCityStructureSite() {}

    static ContentCityStructure.District district(GenerationContext context, CityGround ground, CityPlan plan) {
        CityPlan own = settled(context, ground, plan);
        ContentCityStructure.District held = ContentCityStructure.DISTRICTS.get(own);
        if (held != null) { return held; }
        ContentCityStructure.District made = ContentLog.LOGGER.quietly(() -> survey(context, ground, own));
        ContentCityStructure.District raced = ContentCityStructure.DISTRICTS.putIfAbsent(own, made);
        return raced == null ? made : raced;
    }

    private static ContentCityStructure.District survey(GenerationContext context, CityGround ground, CityPlan plan) {
        Grading grading = grading(context, ground, plan);
        Map<CityPlan.Line, ContentCityStructure.Street> streets = new HashMap<>();
        Map<CityPlan.Line, int[]> finals = new HashMap<>();
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line line : lines) {
                ContentCityStructure.Graded graded = ContentCityStructureGrade.graded(context, plan, line, grading.levels(), grading.run(), grading.rails(), grading.wells(), false);
                int[] profile = graded.profile();
                CityGrade.rampSteps(profile, graded.held(), graded.pinned());
                int[] grade = profile.clone();
                boolean[] decked = new boolean[profile.length];
                ContentCityStructureGrade.lift(plan, line, graded.start(), profile, decked, graded.bridged(), graded.bored(), grading.decks());
                streets.put(line, new ContentCityStructure.Street(graded.start(), grade, CityGrade.roofed(profile, CityGradeBore.openEnds(graded.ground(), graded.start(), line), graded.bridged(), ContentCity.tunnelDepth())));
                int[] built = profile.clone();
                boolean[] held = graded.held().clone();
                for (int at = 0; at < decked.length; at++) { held[at] |= decked[at]; }
                CityGrade.rampSteps(built, held, graded.pinned());
                finals.put(line, built);
            }
        }
        courtStreets(ground, plan, streets, finals);
        return new ContentCityStructure.District(streets, grading.wells(), grading.claims());
    }

    static void courtStreets(CityGround ground, CityPlan plan, Map<CityPlan.Line, ContentCityStructure.Street> streets, Map<CityPlan.Line, int[]> finals) {
        for (CityPlan.Court court : CityPlan.plannedCourts(plan)) {
            if (court.room() <= 0) { continue; }
            CityPlan.Line parent = null;
            for (CityPlan.Line line : finals.keySet()) {
                if (court.serves(line) && (parent == null || line.equals(court.street()))) { parent = line; }
            }
            if (parent == null) { continue; }
            int[] built = finals.get(parent);
            int start = streets.get(parent).start();
            CityPlan.Line way = court.line();
            int[] grade = new int[way.to() - way.from() + 1];
            BoundingBox box = ContentCityStructureEnds.court(parent, court.end(), court.dir(), court.room(), court.stem());
            Arrays.fill(grade, parent.covers(court.end()) ? built[Mth.clamp(court.end() - start, 0, built.length - 1)] : ContentCityStructureSeat.groundAverage(ground, box.minX(), box.minZ(), box.maxX(), box.maxZ()));
            streets.put(way, new ContentCityStructure.Street(way.from(), grade, new boolean[grade.length]));
        }
    }

    private record Grading(Map<CityPlan.Junction, Integer> levels, int run, Map<CityPlan.Rail, CityRails.Laid> rails, List<BoundingBox> claims, List<ContentCityStructure.Well> wells, Map<Integer, Integer> decks) {}

    private static Grading grading(GenerationContext context, CityGround ground, CityPlan plan) {
        ground.warm(plan.windowX() - 1, plan.windowZ() - 1, plan.windowX() + CityPlan.district(), plan.windowZ() + CityPlan.district());
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
        }
        List<ContentCityStructure.Well> wells = ContentCityStructurePlaza.wells(context, ground, plan, levels);
        ContentCityStructurePlaza.plazaLevels(wells, levels);
        ContentCityStructure.aprons(ground, plan, levels, ContentCityStructurePlaza.boxes(wells));
        ContentCityStructureJoins.raiseJunctions(context, plan, levels, run, rails, wells);
        return new Grading(levels, run, rails, claims, wells, ContentCityStructureGrade.decks(context, plan, wells, levels));
    }

    static CityPlan settled(GenerationContext context, CityGround ground, CityPlan plan) {
        CityPlan held = plan.settled();
        if (held != null) { return held; }
        Map<CityPlan.Line, CityPlan.Line> reached = new HashMap<>();
        Map<CityPlan.Line, CityPlan.Line> pulled = ContentLog.LOGGER.quietly(() -> pulls(context, ground, plan, reached));
        for (Map.Entry<CityPlan.Line, CityPlan.Line> cut : pulled.entrySet()) { ContentLog.LOGGER.debug("The street along {} at {} is pulled back from rows {} to {} to rows {} to {}, to the shore, since its deck cannot lie at one height between the junction it leaves and the land it reaches", cut.getKey().alongX() ? "x" : "z", cut.getKey().at(), cut.getKey().from(), cut.getKey().to(), cut.getValue().from(), cut.getValue().to()); }
        for (Map.Entry<CityPlan.Line, CityPlan.Line> moved : reached.entrySet()) { ContentLog.LOGGER.debug("The street along {} at {} is {} from rows {} to {} to rows {} to {}", moved.getKey().alongX() ? "x" : "z", moved.getKey().at(), moved.getValue().to() - moved.getValue().from() > moved.getKey().to() - moved.getKey().from() ? "lengthened to bore through the hill at its end and come out the other side" : "cut back at its open end to where its grade is walkable and its end is not buried in a hill", moved.getKey().from(), moved.getKey().to(), moved.getValue().from(), moved.getValue().to()); }
        pulled.putAll(reached);
        return plan.settle(plan.pulledBack(pulled));
    }

    private static Map<CityPlan.Line, CityPlan.Line> pulls(GenerationContext context, CityGround ground, CityPlan plan, Map<CityPlan.Line, CityPlan.Line> reached) {
        Grading grading = grading(context, ground, plan);
        Map<CityPlan.Line, CityPlan.Line> pulled = new HashMap<>();
        BoundingBox heart = grading.wells().isEmpty() ? null : grading.wells().getFirst().box();
        int middleX = heart == null ? plan.windowX() + CityPlan.district() / 2 : (heart.minX() + heart.maxX()) / 2;
        int middleZ = heart == null ? plan.windowZ() + CityPlan.district() / 2 : (heart.minZ() + heart.maxZ()) / 2;
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line line : lines) {
                ContentCityStructure.Graded graded = ContentCityStructureGrade.graded(context, plan, line, grading.levels(), grading.run(), grading.rails(), grading.wells(), false);
                int[] profile = graded.profile();
                boolean[] held = graded.held();
                CityGrade.rampSteps(profile, held, graded.pinned());
                boolean[] decked = new boolean[profile.length];
                ContentCityStructureGrade.lift(plan, line, graded.start(), profile, decked, graded.bridged(), graded.bored(), grading.decks());
                for (int at = 0; at < decked.length; at++) { held[at] |= decked[at]; }
                CityGrade.rampSteps(profile, held, graded.pinned());
                int middle = line.alongX() ? middleX : middleZ;
                boolean growsUp = Math.abs(line.from() - middle) <= Math.abs(line.to() - middle);
                int kept = shoreBeforeSlopedDeck(ground, line, graded.start(), profile, graded.bridged(), growsUp);
                if (kept < 0) { continue; }
                int trimmed = Math.max(kept, attachedRows(plan, line, grading.wells(), growsUp));
                if (trimmed >= line.to() - line.from() + 1 || trimmed < PULL_LEAST) { continue; }
                pulled.put(line, growsUp ? line.reaching(line.from(), line.from() + trimmed - 1).ending(line.endsLow(), CityPlan.End.BARE) : line.reaching(line.to() - trimmed + 1, line.to()).ending(CityPlan.End.BARE, line.endsHigh()));
            }
        }
        int depth = ContentCity.tunnelDepth();
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line line : lines) {
                if (pulled.containsKey(line)) { continue; }
                CityPlan.Line moved = line;
                if (line.endsLow() != CityPlan.End.MET) { moved = reach(context, plan, grading, line, moved, true, depth); }
                if (line.endsHigh() != CityPlan.End.MET) { moved = reach(context, plan, grading, line, moved, false, depth); }
                if (!moved.equals(line)) { reached.put(line, moved); }
            }
        }
        return pulled;
    }

    private static CityPlan.Line reach(GenerationContext context, CityPlan plan, Grading grading, CityPlan.Line drawn, CityPlan.Line line, boolean low, int depth) {
        int rows = line.to() - line.from() + 1;
        int attached = attachedRows(plan, drawn, grading.wells(), !low);
        int floor = attached == 0 ? 0 : attached + (low ? line.to() - drawn.to() : drawn.from() - line.from());
        for (int length = rows; length >= Math.max(REACH_STEP, floor); length -= REACH_STEP) {
            CityPlan.Line cut = stretched(line, low, length);
            ContentCityStructure.Graded graded = reachGrade(context, plan, grading, drawn, cut);
            if (!walkable(graded.profile(), graded.bridged())) { continue; }
            int buried = depth > 0 ? deadEnd(graded, cut, low, depth) : 0;
            if (buried == 0) { return cut; }
            int most = rows + throughRoom(plan, drawn, line, low, grading.wells());
            for (int longer = length + REACH_STEP; longer <= most; longer += REACH_STEP) {
                CityPlan.Line through = stretched(line, low, longer);
                ContentCityStructure.Graded bored = reachGrade(context, plan, grading, drawn, through);
                if (walkable(bored.profile(), bored.bridged()) && deadEnd(bored, through, low, depth) == 0) { return through; }
            }
            int foot = Math.max(length - buried, floor);
            if (foot >= REACH_STEP) { return stretched(line, low, Math.min(foot, length)); }
        }
        return line;
    }

    private static CityPlan.Line stretched(CityPlan.Line line, boolean low, int rows) { return low ? line.reaching(line.to() - rows + 1, line.to()) : line.reaching(line.from(), line.from() + rows - 1); }

    private static ContentCityStructure.Graded reachGrade(GenerationContext context, CityPlan plan, Grading grading, CityPlan.Line drawn, CityPlan.Line line) {
        Map<CityPlan.Junction, Integer> levels = new HashMap<>(grading.levels());
        for (Map.Entry<CityPlan.Junction, Integer> entry : grading.levels().entrySet()) {
            CityPlan.Junction junction = entry.getKey();
            if (junction.alongX().equals(drawn)) { levels.put(new CityPlan.Junction(line, junction.alongZ()), entry.getValue()); }
            else if (junction.alongZ().equals(drawn)) { levels.put(new CityPlan.Junction(junction.alongX(), line), entry.getValue()); }
        }
        return ContentCityStructureGrade.graded(context, plan, line, levels, grading.run(), grading.rails(), grading.wells(), false);
    }

    private static int deadEnd(ContentCityStructure.Graded graded, CityPlan.Line line, boolean low, int depth) {
        int[] profile = graded.profile();
        int[] ground = graded.ground();
        int last = (low ? line.from() : line.to()) - graded.start();
        if (last < 0 || last >= profile.length || !CityGradeBore.buriedAt(profile, ground, graded.bridged(), last, depth)) { return 0; }
        int back = low ? 1 : -1;
        int at = last;
        while (at + back >= 0 && at + back < profile.length) {
            int next = at + back;
            if (ground[next] == Integer.MIN_VALUE || ground[next] - profile[next] <= CityGrade.CAP) { break; }
            at = next;
        }
        return Math.abs(at - last) + 1;
    }

    private static int throughRoom(CityPlan plan, CityPlan.Line drawn, CityPlan.Line line, boolean low, List<ContentCityStructure.Well> wells) {
        List<BoundingBox> standing = new ArrayList<>();
        ContentCityStructureEnds.stand(plan, drawn, standing, new ArrayList<>());
        for (ContentCityStructure.Well well : wells) { standing.add(well.box()); }
        int[] bounds = reachBounds(plan, line);
        int room = 0;
        for (int extra = REACH_STEP; extra <= TUNNEL_REACH; extra += REACH_STEP) {
            int near = low ? line.from() - extra : line.to() + 1;
            int far = low ? line.from() - 1 : line.to() + extra;
            if (near < bounds[0] || far > bounds[1] || blocked(line, near, far, standing)) { break; }
            room = extra;
        }
        return room;
    }

    private static int[] reachBounds(CityPlan plan, CityPlan.Line line) {
        if (!plan.mapped()) { return new int[] {plan.spanStart(line), plan.spanStart(line) + plan.spanLength(line) - 1}; }
        int least = Integer.MAX_VALUE;
        int most = Integer.MIN_VALUE;
        for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
            for (CityPlan.Line other : lines) {
                least = Math.min(least, other.alongX() == line.alongX() ? other.from() : other.at());
                most = Math.max(most, other.alongX() == line.alongX() ? other.to() : other.last());
            }
        }
        boolean alongX = line.alongX();
        return new int[] {CityPlan.windowOf(CityPlan.districtOf(least, alongX), alongX), CityPlan.windowOf(CityPlan.districtOf(most, alongX) + 1, alongX) - 1};
    }

    private static boolean blocked(CityPlan.Line line, int near, int far, List<BoundingBox> standing) {
        BoundingBox strip = line.alongX() ? new BoundingBox(near, 0, line.at() - 1, far, 0, line.last() + 1) : new BoundingBox(line.at() - 1, 0, near, line.last() + 1, 0, far);
        for (BoundingBox box : standing) {
            if (box.maxX() >= strip.minX() && box.minX() <= strip.maxX() && box.maxZ() >= strip.minZ() && box.minZ() <= strip.maxZ()) { return true; }
        }
        return false;
    }

    private static boolean walkable(int[] profile, boolean[] bridged) {
        int held = Integer.MIN_VALUE;
        int boarded = Integer.MIN_VALUE;
        int left = Integer.MIN_VALUE;
        for (int at = 0; at < profile.length; at++) {
            if (bridged[at]) {
                if (boarded == Integer.MIN_VALUE) { boarded = profile[at]; }
                left = profile[at];
                continue;
            }
            boolean crossed = boarded != Integer.MIN_VALUE && Math.abs(boarded - held) <= 1 && Math.abs(profile[at] - left) <= 1;
            if (held != Integer.MIN_VALUE && Math.abs(profile[at] - held) > 1 && !crossed) { return false; }
            held = profile[at];
            boarded = Integer.MIN_VALUE;
        }
        return true;
    }

    private static int shoreBeforeSlopedDeck(CityGround ground, CityPlan.Line line, int start, int[] profile, boolean[] bridged, boolean growsUp) {
        int rows = line.to() - line.from() + 1;
        for (int n = 0; n < rows; n++) {
            if (!deckAt(bridged, start, pulledRow(line, growsUp, n))) { continue; }
            int end = n;
            while (end + 1 < rows && deckAt(bridged, start, pulledRow(line, growsUp, end + 1))) { end++; }
            int low = Integer.MAX_VALUE;
            int high = Integer.MIN_VALUE;
            for (int k = n; k <= end; k++) {
                int level = profile[pulledRow(line, growsUp, k) - start];
                low = Math.min(low, level);
                high = Math.max(high, level);
            }
            if (high > low) {
                int cut = n;
                while (cut > 0 && CityPlan.underwater(ground, line, pulledRow(line, growsUp, cut - 1))) { cut--; }
                return cut;
            }
            n = end;
        }
        return -1;
    }

    private static int pulledRow(CityPlan.Line line, boolean growsUp, int n) { return growsUp ? line.from() + n : line.to() - n; }

    private static boolean deckAt(boolean[] bridged, int start, int row) { return row >= start && row - start < bridged.length && bridged[row - start]; }

    private static int attachedRows(CityPlan plan, CityPlan.Line line, List<ContentCityStructure.Well> wells, boolean growsUp) {
        List<BoundingBox> standing = new ArrayList<>();
        ContentCityStructureEnds.stand(plan, line, standing, new ArrayList<>());
        for (ContentCityStructure.Well well : wells) { standing.add(well.box()); }
        int rows = 0;
        for (BoundingBox box : standing) {
            if ((line.alongX() ? box.maxZ() : box.maxX()) < line.at() - PULL_BESIDE || (line.alongX() ? box.minZ() : box.minX()) > line.last() + PULL_BESIDE) { continue; }
            int least = line.alongX() ? box.minX() : box.minZ();
            int most = line.alongX() ? box.maxX() : box.maxZ();
            if (most < line.from() || least > line.to()) { continue; }
            rows = Math.max(rows, growsUp ? most - line.from() + 1 : line.to() - least + 1);
        }
        return rows;
    }

    private static int[] kept(GenerationContext context, CityGround ground, CityPlan plan) {
        CityPlan own = settled(context, ground, plan);
        int[] held = KEPT.get(own);
        if (held != null) { return held; }
        int[] made = ContentLog.LOGGER.quietly(() -> {
            ContentCityStructure.District district = district(context, ground, own);
            IntList boxes = new IntArrayList();
            for (CityPlan.Plot plot : own.plots()) {
                if (ContentCityStructureStations.claimed(district.claims(), plot)) { continue; }
                CityPlan.Plot placed = ContentCityStructureSeat.placed(context, ground, own, district, plot);
                if (placed != null) { boxes.addAll(IntList.of(placed.fromX(), placed.fromZ(), placed.toX(), placed.toZ())); }
            }
            return boxes.toIntArray();
        });
        int[] raced = KEPT.putIfAbsent(own, made);
        return raced == null ? made : raced;
    }

    static boolean buried(GenerationContext context, CityGround ground, CityPlan plan, ContentCityStructure.Seated found, List<ContentCityStructure.Seated> offered, List<ContentCityStructure.Well> wells, List<ContentCityStructure.Bulb> bulbs) {
        if (!ContentCity.wanted()) { return false; }
        CityPlan.Plot placed = found.plot();
        int hill = groundAround(ground, placed, ringKeep(context, ground, plan, found, offered, wells, bulbs));
        int allow = placed.def().apron();
        if (hill == Integer.MIN_VALUE || hill - found.level() <= allow) { return false; }
        ContentLog.LOGGER.debug("Village plot {} at {}, {} to {}, {} makes way for the hill around it: it would stand at y {}, {} block(s) under the ground of y {} around its clearing, deeper than the {} block(s) of apron it may dig", placed.def().key(), placed.fromX(), placed.fromZ(), placed.toX(), placed.toZ(), found.level(), hill - found.level(), hill, allow);
        return true;
    }

    private static int[] ringKeep(GenerationContext context, CityGround ground, CityPlan plan, ContentCityStructure.Seated found, List<ContentCityStructure.Seated> offered, List<ContentCityStructure.Well> wells, List<ContentCityStructure.Bulb> bulbs) {
        CityPlan.Plot placed = found.plot();
        IntList boxes = new IntArrayList();
        for (ContentCityStructure.Seated other : offered) {
            CityPlan.Plot plot = other.plot();
            if (other != found && near(placed, plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ())) { boxes.addAll(IntList.of(plot.fromX(), plot.fromZ(), plot.toX(), plot.toZ())); }
        }
        List<CityPlan> plans = around(ground, plan, placed);
        List<ContentCityStructure.Well> plazas = new ArrayList<>(wells);
        for (CityPlan other : plans) {
            for (CityPlan.Rail rail : other.rails()) {
                if (rail.subway()) { continue; }
                int[] box = rail.alongX() ? new int[] {rail.from(), rail.at(), rail.to(), rail.last()} : new int[] {rail.at(), rail.from(), rail.last(), rail.to()};
                if (near(placed, box[0], box[1], box[2], box[3])) { for (int value : box) { boxes.add(value); } }
            }
            if (other == plan) { continue; }
            int[] kept = kept(context, ground, other);
            for (int at = 0; at + 3 < kept.length; at += 4) {
                if (near(placed, kept[at], kept[at + 1], kept[at + 2], kept[at + 3])) { boxes.addAll(IntList.of(kept[at], kept[at + 1], kept[at + 2], kept[at + 3])); }
            }
            plazas.addAll(district(context, ground, other).wells());
        }
        int reach = CityPlan.plazaReach();
        for (ContentCityStructure.Well well : plazas) {
            BoundingBox box = well.box();
            if (near(placed, box.minX() - reach, box.minZ() - reach, box.maxX() + reach, box.maxZ() + reach)) { boxes.addAll(IntList.of(box.minX() - reach, box.minZ() - reach, box.maxX() + reach, box.maxZ() + reach)); }
        }
        for (ContentCityStructure.Bulb bulb : bulbs) {
            ContentCityBulbPiece.Court court = bulb.court();
            int fromX = court.centerX() - court.reach();
            int fromZ = court.centerZ() - court.reach();
            int toX = court.centerX() + court.reach();
            int toZ = court.centerZ() + court.reach();
            if (near(placed, fromX, fromZ, toX, toZ)) { boxes.addAll(IntList.of(fromX, fromZ, toX, toZ)); }
        }
        for (int value : roadsAround(plans, placed)) { boxes.add(value); }
        return boxes.toIntArray();
    }

    private static int groundAround(CityGround ground, CityPlan.Plot placed, int[] keep) {
        int[] ring = new int[2 * (placed.toX() - placed.fromX() + placed.toZ() - placed.fromZ()) + 8 * HILL_RING + 4];
        int count = 0;
        for (int x = placed.fromX() - HILL_RING; x <= placed.toX() + HILL_RING; x++) {
            count = ringGround(ground, keep, ring, count, x, placed.fromZ() - HILL_RING);
            count = ringGround(ground, keep, ring, count, x, placed.toZ() + HILL_RING);
        }
        for (int z = placed.fromZ() - HILL_RING + 1; z <= placed.toZ() + HILL_RING - 1; z++) {
            count = ringGround(ground, keep, ring, count, placed.fromX() - HILL_RING, z);
            count = ringGround(ground, keep, ring, count, placed.toX() + HILL_RING, z);
        }
        if (count == 0) { return Integer.MIN_VALUE; }
        Arrays.sort(ring, 0, count);
        return ring[count / 2];
    }

    private static int ringGround(CityGround ground, int[] keep, int[] ring, int count, int x, int z) {
        for (int at = 0; at + 3 < keep.length; at += 4) {
            if (x >= keep[at] && x <= keep[at + 2] && z >= keep[at + 1] && z <= keep[at + 3]) { return count; }
        }
        ring[count] = ground.surface(x, z);
        return count + 1;
    }

    static List<CityPlan> around(CityGround ground, CityPlan plan, CityPlan.Plot placed) { return plansOver(ground, plan, placed.fromX() - KEEP_REACH, placed.toX() + KEEP_REACH, placed.fromZ() - KEEP_REACH, placed.toZ() + KEEP_REACH); }

    static List<CityPlan> plansOver(CityGround ground, CityPlan plan, int minX, int maxX, int minZ, int maxZ) {
        List<CityPlan> found = new ArrayList<>();
        for (int districtX = CityPlan.districtOf(minX, true); districtX <= CityPlan.districtOf(maxX, true); districtX++) {
            for (int districtZ = CityPlan.districtOf(minZ, false); districtZ <= CityPlan.districtOf(maxZ, false); districtZ++) {
                CityPlan other = CityPlan.windowOf(districtX, true) == plan.windowX() && CityPlan.windowOf(districtZ, false) == plan.windowZ() ? plan : CityPlan.of(ground, districtX, districtZ);
                if (other != null && !found.contains(other)) { found.add(other); }
            }
        }
        if (!found.contains(plan)) { found.add(plan); }
        return found;
    }

    static int[] keepAround(List<CityPlan> plans, CityPlan.Plot placed, List<ContentCityStructure.Well> wells) {
        IntList boxes = new IntArrayList();
        for (CityPlan plan : plans) { keepFrom(plan, placed, boxes); }
        for (ContentCityStructure.Well well : wells) {
            int reach = CityPlan.plazaReach();
            BoundingBox box = well.box();
            if (near(placed, box.minX() - reach, box.minZ() - reach, box.maxX() + reach, box.maxZ() + reach)) { boxes.addAll(IntList.of(box.minX() - reach, box.minZ() - reach, box.maxX() + reach, box.maxZ() + reach)); }
        }
        for (int value : roadsAround(plans, placed)) { boxes.add(value); }
        return boxes.toIntArray();
    }

    private static void keepFrom(CityPlan plan, CityPlan.Plot placed, IntList boxes) {
        for (CityPlan.Plot other : plan.plots()) {
            if (other.fromX() == placed.fromX() && other.fromZ() == placed.fromZ() && other.def() == placed.def()) { continue; }
            if (near(placed, other.fromX(), other.fromZ(), other.toX(), other.toZ())) { boxes.addAll(IntList.of(other.fromX(), other.fromZ(), other.toX(), other.toZ())); }
        }
        for (CityPlan.Rail rail : plan.rails()) {
            if (rail.subway()) { continue; }
            int from = rail.from();
            int to = rail.to();
            int[] box = rail.alongX() ? new int[] {from, rail.at(), to, rail.last()} : new int[] {rail.at(), from, rail.last(), to};
            if (near(placed, box[0], box[1], box[2], box[3])) { for (int value : box) { boxes.add(value); } }
        }
    }

    static int[] roadsAround(List<CityPlan> plans, CityPlan.Plot placed) {
        IntList boxes = new IntArrayList();
        for (CityPlan plan : plans) {
            for (List<CityPlan.Line> lines : List.of(plan.alongX(), plan.alongZ())) {
                for (CityPlan.Line line : lines) {
                    int[] box = line.alongX() ? new int[] {line.from(), line.at(), line.to(), line.last()} : new int[] {line.at(), line.from(), line.last(), line.to()};
                    if (near(placed, box[0], box[1], box[2], box[3])) { for (int value : box) { boxes.add(value); } }
                }
            }
            for (CityPlan.Court court : CityPlan.plannedCourts(plan)) {
                if (court.room() <= 0) { continue; }
                CityPlan.Line way = court.line();
                int[] box = way.alongX() ? new int[] {way.from(), way.at(), way.to(), way.last()} : new int[] {way.at(), way.from(), way.last(), way.to()};
                if (near(placed, box[0], box[1], box[2], box[3])) { for (int value : box) { boxes.add(value); } }
            }
        }
        return boxes.toIntArray();
    }

    private static boolean near(CityPlan.Plot placed, int fromX, int fromZ, int toX, int toZ) { return toX >= placed.fromX() - KEEP_REACH && fromX <= placed.toX() + KEEP_REACH && toZ >= placed.fromZ() - KEEP_REACH && fromZ <= placed.toZ() + KEEP_REACH; }
}
