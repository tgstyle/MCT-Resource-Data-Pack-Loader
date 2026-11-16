package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonSizeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RoomKindDef;
import mctmods.resourcedatapackloader.common.util.worldgen.SeedStreams;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.DungeonWorld;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Turns;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class DungeonSlots {
    public static final String BOSS_KIND = "boss";
    private static final double SLOT_SURPLUS = 1.4;
    private static final int SHAFT_REACH = 2;
    private static final int MIN_START_SIDE = 5;
    static final int MAX_SUNK = 2;
    private static final int HEIGHT_TRIM = 2;
    private static final int MIN_FIELD_PERCENT = 50;
    private static final int FREE = -1;
    private static final int PLAIN = -2;
    private static final int[] STEP_X = {1, 0, -1, 0};
    private static final int[] STEP_Z = {0, 1, 0, -1};

    private final DungeonDef def;
    private final List<RoomKindDef> kinds;
    private final DungeonSizeDef size;
    private final int levelIndex;
    private final boolean bottom;
    private final int vertical;
    private final int anchorX;
    private final int anchorZ;
    private final int siteX;
    private final int siteZ;
    private final int[] fieldBox;
    private final long levelSeed;
    private final Tally tally;
    private final TemplateSizes sizes;
    private final int m;
    private final RoomKindDef hall;
    private final Map<String, Integer> levelCounts = new TreeMap<>();
    private final List<Spec> specs = new ArrayList<>();
    private final List<Spec> forced = new ArrayList<>();
    private final List<Block> blocks = new ArrayList<>();
    private Spec bossSpec;
    private int floorY;
    private int slabHeight;
    private int rooms;
    private int q;
    private int ox;
    private int oz;
    private int firstCol;
    private int firstRow;
    private int cols;
    private int rows;
    private int[] owner;

    private DungeonSlots(DungeonDef def, List<RoomKindDef> kinds, DungeonSizeDef size, int levelIndex, int levels, int vertical, int anchorX, int anchorZ, int siteX, int siteZ, int[] fieldBox, long levelSeed, Tally tally, TemplateSizes sizes) {
        this.def = def;
        this.kinds = kinds;
        this.size = size;
        this.levelIndex = levelIndex;
        this.bottom = levelIndex == levels - 1;
        this.vertical = vertical;
        this.anchorX = anchorX;
        this.anchorZ = anchorZ;
        this.siteX = siteX;
        this.siteZ = siteZ;
        this.fieldBox = fieldBox;
        this.levelSeed = levelSeed;
        this.tally = tally;
        this.sizes = sizes;
        this.m = (def.corridor().width() + 1) / 2 + 2;
        this.hall = hallOf(kinds);
    }

    public static Level.Builder layout(DungeonPlan.Builder plan, DungeonDef def, List<RoomKindDef> kinds, DungeonSizeDef size, int levelIndex, int levels, int floorY, int slabHeight, String themeId, int vertical, int anchorX, int anchorZ, int siteX, int siteZ, int[] fieldBox, long levelSeed, Tally tally, TemplateSizes sizes) {
        DungeonSlots slots = new DungeonSlots(def, kinds, size, levelIndex, levels, vertical, anchorX, anchorZ, siteX, siteZ, fieldBox, levelSeed, tally, sizes);
        slots.floorY = floorY;
        slots.slabHeight = slabHeight;
        slots.rollRooms();
        slots.chooseGrid();
        Level.Builder level = plan.level(floorY, slabHeight, slots.q, slots.ox, slots.oz, anchorX, anchorZ, themeId);
        slots.reserve();
        slots.grow();
        slots.join();
        slots.emit(level);
        return level;
    }

    private static RoomKindDef hallOf(List<RoomKindDef> kinds) {
        for (RoomKindDef kind : kinds) {
            if (RoomKindDef.HALL.equals(kind.kind()) && !kind.templated()) { return kind; }
        }
        return RoomKindDef.hall();
    }

    private void rollRooms() {
        Random random = SeedStreams.random(levelSeed, "slots", 0);
        int low = size.rooms()[0];
        int high = Math.max(low, size.rooms()[1]);
        rooms = low + random.nextInt(high - low + 1);
        int total = 0;
        for (RoomKindDef kind : kinds) {
            if (kind.allowsLevel(levelIndex)) { total += kind.weight(); }
        }
        Random counts = SeedStreams.random(levelSeed, "count", 0);
        for (RoomKindDef kind : kinds) {
            if (!kind.templated() || !kind.allowsLevel(levelIndex) || total == 0) { continue; }
            double expected = rooms * (double) kind.weight() / total;
            int count = (int) expected;
            if (counts.nextDouble() < expected - count) { count++; }
            if (kind.maxPerLevel() != RoomKindDef.NO_CAP) { count = Math.min(count, kind.maxPerLevel()); }
            if (kind.maxPerDungeon() != RoomKindDef.NO_CAP) { count = Math.min(count, kind.maxPerDungeon() - tally.count(kind.kind())); }
            for (int i = 0; i < count; i++) {
                tally.add(kind.kind());
                specs.add(templateSpec(kind, specs.size()));
            }
        }
        if (bottom && def.boss() != null) { bossSpec = bossSpec(def.boss()); }
    }

    private Spec templateSpec(RoomKindDef kind, int index) {
        Random random = SeedStreams.random(levelSeed, "template", index);
        String id = PickDef.pick(kind.templates(), random, null);
        int turn = Turns.turn(kind.turns(), random).ordinal();
        return measured(new Spec(kind, id, turn, false), id);
    }

    private Spec bossSpec(DungeonDef.Boss boss) {
        Random random = SeedStreams.random(levelSeed, "template", -1);
        String id = PickDef.pick(boss.templates(), random, null);
        if (id == null) {
            int[] span = boss.size() != null ? boss.size() : new int[] {def.roomSize()[1], def.roomSize()[1]};
            Spec spec = new Spec(hall, null, 0, true);
            spec.sx = span[0];
            spec.sz = span[1];
            spec.ok = true;
            return spec;
        }
        return measured(new Spec(hall, id, 0, true), id);
    }

    private Spec measured(Spec spec, @Nullable String id) {
        int[] span = id == null ? null : sizes.size(id);
        if (span == null) {
            ContentLog.LOGGER.onceWarn("Dungeon {} names template '{}', which could not be loaded, so its room is a plain one", def.key(), id);
            return spec;
        }
        boolean turned = spec.turn % 2 == 1;
        spec.sx = turned ? span[2] : span[0];
        spec.sy = span[1];
        spec.sz = turned ? span[0] : span[2];
        spec.ok = true;
        return spec;
    }

    private int blockSlots(int side) {
        int total = 0;
        for (Spec spec : specs) {
            if (spec.ok) { total += spec.blockX(side, m) * spec.blockZ(side, m); }
        }
        if (bossSpec != null && bossSpec.ok) { total += bossSpec.blockX(side, m) * bossSpec.blockZ(side, m); }
        return total;
    }

    private void chooseGrid() {
        int low = def.roomSize()[0] + 2 * m;
        int high = Math.max(low, def.roomSize()[1] + 2 * m);
        for (int side = high; side >= low; side--) {
            gridFor(side);
            if (cols * rows >= (int) Math.ceil(rooms * SLOT_SURPLUS) + blockSlots(side)) { return; }
        }
        gridFor(low);
        int fits = cols * rows;
        if (fits < rooms) {
            tally.tell("lowered", "Dungeon {} level {} fits {} rooms, not the {} rolled", def.key(), levelIndex, fits, rooms);
            rooms = Math.max(1, fits);
        }
    }

    private void gridFor(int side) {
        q = side;
        ox = anchorX - side / 2;
        oz = anchorZ - side / 2;
        int fieldSize = size.footprint() * Math.max(MIN_FIELD_PERCENT, 100 - def.shrink() * levelIndex) / 100;
        int x0 = Math.min(Math.max(siteX - fieldSize / 2, fieldBox[0]), ox);
        int z0 = Math.min(Math.max(siteZ - fieldSize / 2, fieldBox[1]), oz);
        int x1 = Math.max(Math.min(siteX - fieldSize / 2 + fieldSize - 1, fieldBox[2]), ox + side - 1);
        int z1 = Math.max(Math.min(siteZ - fieldSize / 2 + fieldSize - 1, fieldBox[3]), oz + side - 1);
        firstCol = Math.min(0, Math.floorDiv(x0 - ox + side - 1, side));
        firstRow = Math.min(0, Math.floorDiv(z0 - oz + side - 1, side));
        cols = Math.max(0, Math.floorDiv(x1 + 1 - ox, side) - 1) - firstCol + 1;
        rows = Math.max(0, Math.floorDiv(z1 + 1 - oz, side) - 1) - firstRow + 1;
    }

    private int cell(int col, int row) { return row * cols + col; }

    private int startCell() { return cell(-firstCol, -firstRow); }

    private boolean taken(int idx) { return owner[idx] == PLAIN || owner[idx] >= 0 && blocks.get(owner[idx]).taken; }

    private boolean solid(int idx) { return taken(idx) && (owner[idx] < 0 || !blocks.get(owner[idx]).spec.boss); }

    private void reserve() {
        owner = new int[cols * rows];
        Arrays.fill(owner, FREE);
        owner[startCell()] = PLAIN;
        Random random = SeedStreams.random(levelSeed, "reserve", 0);
        for (Spec spec : specs) {
            if (!spec.ok) {
                forced.add(spec);
                continue;
            }
            int kx = spec.blockX(q, m);
            int kz = spec.blockZ(q, m);
            List<int[]> spots = freeSpots(kx, kz);
            if (spots.isEmpty()) {
                fallback(spec);
                continue;
            }
            int[] at = spots.get(random.nextInt(spots.size()));
            claim(spec, at[0], at[1], kx, kz);
        }
        if (bossSpec == null) { return; }
        int kx = bossSpec.blockX(q, m);
        int kz = bossSpec.blockZ(q, m);
        int[] at = bossSpot(kx, kz, random);
        if (!bossSpec.ok || at == null) {
            fallback(bossSpec);
            return;
        }
        claim(bossSpec, at[0], at[1], kx, kz);
    }

    private void fallback(Spec spec) {
        tally.tell("fallback:" + spec.templateId + ":" + spec.boss, "Dungeon {} level {} found no free block for {}, so its room is a plain one", def.key(), levelIndex, spec.templateId);
        forced.add(spec);
    }

    private void claim(Spec spec, int col, int row, int kx, int kz) {
        Block block = new Block(spec, col, row, kx, kz);
        blocks.add(block);
        for (int r = row; r < row + kz; r++) {
            for (int c = col; c < col + kx; c++) { owner[cell(c, r)] = blocks.size() - 1; }
        }
    }

    private List<int[]> freeSpots(int kx, int kz) {
        List<int[]> spots = new ArrayList<>();
        for (int row = 0; row + kz <= rows; row++) {
            for (int col = 0; col + kx <= cols; col++) {
                if (blockFree(col, row, kx, kz)) { spots.add(new int[] {col, row}); }
            }
        }
        return spots;
    }

    private boolean blockFree(int col, int row, int kx, int kz) {
        for (int r = row; r < row + kz; r++) {
            for (int c = col; c < col + kx; c++) {
                if (owner[cell(c, r)] != FREE) { return false; }
            }
        }
        return true;
    }

    @Nullable private int[] bossSpot(int kx, int kz, Random random) {
        List<int[]> spots = freeSpots(kx, kz);
        if (spots.isEmpty()) { return null; }
        int startCol = -firstCol;
        int startRow = -firstRow;
        boolean right = startCol < cols / 2;
        boolean down = startRow < rows / 2;
        List<int[]> edge = new ArrayList<>();
        for (int[] spot : spots) {
            boolean xEdge = right ? spot[0] + kx == cols : spot[0] == 0;
            boolean zEdge = down ? spot[1] + kz == rows : spot[1] == 0;
            if (xEdge || zEdge) { edge.add(spot); }
        }
        List<int[]> pool = edge.isEmpty() ? spots : edge;
        int best = -1;
        List<int[]> ties = new ArrayList<>();
        for (int[] spot : pool) {
            int away = Math.abs(spot[0] + kx / 2 - startCol) + Math.abs(spot[1] + kz / 2 - startRow);
            if (away > best) {
                best = away;
                ties.clear();
            }
            if (away == best) { ties.add(spot); }
        }
        return ties.get(random.nextInt(ties.size()));
    }

    private void grow() {
        Random random = SeedStreams.random(levelSeed, "growth", 0);
        int count = 1;
        while (count < rooms) {
            List<Integer> frontier = new ArrayList<>();
            for (int idx = 0; idx < owner.length; idx++) {
                if (!taken(idx) && touchesTaken(idx)) { frontier.add(idx); }
            }
            if (frontier.isEmpty()) { break; }
            take(frontier.get(random.nextInt(frontier.size())));
            count++;
        }
    }

    private boolean touchesTaken(int idx) {
        int col = idx % cols;
        int row = idx / cols;
        for (int s = 0; s < STEP_X.length; s++) {
            int c = col + STEP_X[s];
            int r = row + STEP_Z[s];
            if (c >= 0 && c < cols && r >= 0 && r < rows && solid(cell(c, r))) { return true; }
        }
        return false;
    }

    private void take(int idx) {
        if (owner[idx] == FREE) { owner[idx] = PLAIN; }
        else if (owner[idx] >= 0) { blocks.get(owner[idx]).taken = true; }
    }

    private void join() {
        Random random = SeedStreams.random(levelSeed, "join", 0);
        for (int b = 0; b < blocks.size(); b++) {
            Block block = blocks.get(b);
            if (block.taken) { continue; }
            if (blockTouchesTaken(block)) {
                block.taken = true;
                continue;
            }
            if (!drawPath(block, b, random.nextInt(STEP_X.length))) {
                fallback(block.spec);
                for (int r = block.row; r < block.row + block.kz; r++) {
                    for (int c = block.col; c < block.col + block.kx; c++) { owner[cell(c, r)] = FREE; }
                }
            }
        }
    }

    private boolean blockTouchesTaken(Block block) {
        for (int r = block.row; r < block.row + block.kz; r++) {
            for (int c = block.col; c < block.col + block.kx; c++) {
                if (touchesTaken(cell(c, r))) { return true; }
            }
        }
        return false;
    }

    private boolean drawPath(Block block, int b, int shift) {
        int[] parent = new int[owner.length];
        boolean[] seen = new boolean[owner.length];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int idx = 0; idx < owner.length; idx++) {
            if (solid(idx)) {
                seen[idx] = true;
                parent[idx] = -1;
                queue.add(idx);
            }
        }
        while (!queue.isEmpty()) {
            int idx = queue.poll();
            int col = idx % cols;
            int row = idx / cols;
            for (int k = 0; k < STEP_X.length; k++) {
                int s = (k + shift) % STEP_X.length;
                int c = col + STEP_X[s];
                int r = row + STEP_Z[s];
                if (c < 0 || c >= cols || r < 0 || r >= rows) { continue; }
                int next = cell(c, r);
                if (seen[next] || owner[next] != FREE) { continue; }
                seen[next] = true;
                parent[next] = idx;
                if (adjacentTo(c, r, b)) {
                    for (int walk = next; walk >= 0 && !taken(walk); walk = parent[walk]) { owner[walk] = PLAIN; }
                    block.taken = true;
                    return true;
                }
                queue.add(next);
            }
        }
        return false;
    }

    private boolean adjacentTo(int col, int row, int b) {
        for (int s = 0; s < STEP_X.length; s++) {
            int c = col + STEP_X[s];
            int r = row + STEP_Z[s];
            if (c >= 0 && c < cols && r >= 0 && r < rows && owner[cell(c, r)] == b) { return true; }
        }
        return false;
    }

    private void emit(Level.Builder level) {
        List<Integer> plain = new ArrayList<>();
        for (int idx = 0; idx < owner.length; idx++) {
            if (owner[idx] == PLAIN && idx != startCell()) { plain.add(idx); }
        }
        Map<Integer, Spec> forcedAt = new TreeMap<>();
        Random random = SeedStreams.random(levelSeed, "forced", 0);
        for (Spec spec : forced) {
            if (plain.isEmpty()) {
                tally.tell("nowhere:" + spec.templateId, "Dungeon {} level {} has no plain slot for the room of {}", def.key(), levelIndex, spec.templateId);
                continue;
            }
            forcedAt.put(plain.remove(random.nextInt(plain.size())), spec);
        }
        level.addRoom(plainRoom(0, 0, 0, null, true));
        for (int idx = 0; idx < owner.length; idx++) {
            if (idx == startCell()) { continue; }
            int gx = firstCol + idx % cols;
            int gz = firstRow + idx / cols;
            if (owner[idx] == PLAIN) {
                level.addRoom(plainRoom(level.roomCount(), gx, gz, forcedAt.get(idx), false));
            }
            else if (owner[idx] >= 0) {
                Block block = blocks.get(owner[idx]);
                if (block.taken && block.col == idx % cols && block.row == idx / cols) { level.addRoom(blockRoom(level.roomCount(), block)); }
            }
        }
    }

    private RoomKindDef rollKind(Random random) {
        List<RoomKindDef> open = new ArrayList<>();
        int total = 0;
        for (RoomKindDef kind : kinds) {
            if (kind.templated() || !kind.allowsLevel(levelIndex) || capped(kind)) { continue; }
            open.add(kind);
            total += kind.weight();
        }
        int roll = total == 0 ? 0 : random.nextInt(total);
        for (RoomKindDef kind : open) {
            roll -= kind.weight();
            if (roll < 0) {
                levelCounts.merge(kind.kind(), 1, Integer::sum);
                tally.add(kind.kind());
                return kind;
            }
        }
        return hall;
    }

    private boolean capped(RoomKindDef kind) {
        boolean level = kind.maxPerLevel() != RoomKindDef.NO_CAP && levelCounts.getOrDefault(kind.kind(), 0) >= kind.maxPerLevel();
        return level || kind.maxPerDungeon() != RoomKindDef.NO_CAP && tally.count(kind.kind()) >= kind.maxPerDungeon();
    }

    private Room plainRoom(int index, int gx, int gz, @Nullable Spec spec, boolean start) {
        RoomKindDef kind = spec != null ? spec.kind : rollKind(SeedStreams.random(levelSeed, "kind", index));
        boolean boss = spec != null && spec.boss;
        int side = q - 2 * m;
        int high = Math.max(1, Math.min(def.roomSize()[1], side));
        int lowX = start ? Math.max(MIN_START_SIDE, def.roomSize()[0]) : def.roomSize()[0];
        int lowZ = lowX;
        if (kind.minSize() != null) {
            if (kind.minSize()[0] > high || kind.minSize()[1] > high) { kind = hall; }
            else {
                lowX = Math.max(lowX, kind.minSize()[0]);
                lowZ = Math.max(lowZ, kind.minSize()[1]);
            }
        }
        Random sizeRandom = SeedStreams.random(levelSeed, "size", index);
        int w = Math.min(lowX, high) + sizeRandom.nextInt(high - Math.min(lowX, high) + 1);
        int d = Math.min(lowZ, high) + sizeRandom.nextInt(high - Math.min(lowZ, high) + 1);
        Random offset = SeedStreams.random(levelSeed, "offset", index);
        int slotX = ox + gx * q;
        int slotZ = oz + gz * q;
        int x0 = start ? forcedOffset(slotX, anchorX, w, offset) : slotX + m + offset.nextInt(side - w + 1);
        int z0 = start ? forcedOffset(slotZ, anchorZ, d, offset) : slotZ + m + offset.nextInt(side - d + 1);
        Random vertical = SeedStreams.random(levelSeed, "vertical", index);
        int sunk = vertical.nextInt(100) < DungeonDef.Degree.CHANCES[this.vertical] ? 1 + vertical.nextInt(MAX_SUNK) : 0;
        int height = rollHeight(vertical, kind.height());
        return room(index, gx, gz, 1, 1, new int[] {x0, z0, w, d}, floorY - sunk, height, kind.kind(), kind, null, 0, start, boss);
    }

    private int forcedOffset(int slot, int anchor, int length, Random random) {
        int low = Math.max(slot + m, anchor + SHAFT_REACH - length + 1);
        int high = Math.min(slot + q - m - length, anchor - SHAFT_REACH);
        return high < low ? low : low + random.nextInt(high - low + 1);
    }

    private Room blockRoom(int index, Block block) {
        Spec spec = block.spec;
        int gx = firstCol + block.col;
        int gz = firstRow + block.row;
        int x0 = ox + gx * q + (block.kx * q - spec.sx) / 2;
        int z0 = oz + gz * q + (block.kz * q - spec.sz) / 2;
        int height = spec.sy;
        if (spec.templateId == null) { height = rollHeight(SeedStreams.random(levelSeed, "vertical", index), def.boss() != null ? def.boss().height() : null); }
        int floor = spec.boss ? floorY : floorY + spec.kind.yOffset();
        String kindName = spec.boss ? BOSS_KIND : spec.kind.kind();
        return room(index, gx, gz, block.kx, block.kz, new int[] {x0, z0, spec.sx, spec.sz}, floor, height, kindName, spec.kind, spec.templateId, spec.turn, false, spec.boss);
    }

    private Room room(int index, int gx, int gz, int kx, int kz, int[] span, int floor, int height, String kindName, RoomKindDef kind, @Nullable String templateId, int turn, boolean start, boolean boss) {
        Random shapes = SeedStreams.random(levelSeed, "shape", index);
        String shape = PickDef.pick(kind.shapes(), shapes, "box");
        int[] box = {span[0], floor, span[1], span[0] + span[2] - 1, floor + height - 1, span[1] + span[3] - 1};
        return new Room(index, gx, gz, kx, kz, box, floor, height, kindName, shape, templateId, turn, kind.node(), start && levelIndex == 0, start, boss, SeedStreams.seed(levelSeed, "room", index), Collections.emptyList());
    }

    private int rollHeight(Random random, @Nullable int[] range) {
        int[] held = range != null ? range : new int[] {def.levelHeight()[0] - HEIGHT_TRIM, def.levelHeight()[1] - HEIGHT_TRIM};
        int low = Math.max(1, held[0]);
        int high = Math.max(low, held[1]);
        int spread = (high - low) * DungeonDef.Degree.CHANCES[vertical] / 100;
        return Math.max(1, Math.min(low + random.nextInt(spread + 1), slabHeight - HEIGHT_TRIM));
    }

    private static final class Spec {
        final RoomKindDef kind;
        @Nullable final String templateId;
        final int turn;
        final boolean boss;
        boolean ok;
        int sx;
        int sy;
        int sz;

        Spec(RoomKindDef kind, @Nullable String templateId, int turn, boolean boss) {
            this.kind = kind;
            this.templateId = templateId;
            this.turn = turn;
            this.boss = boss;
        }

        int blockX(int side, int margin) { return (sx + 2 * margin + side - 1) / side; }

        int blockZ(int side, int margin) { return (sz + 2 * margin + side - 1) / side; }
    }

    private static final class Block {
        final Spec spec;
        final int col;
        final int row;
        final int kx;
        final int kz;
        boolean taken;

        Block(Spec spec, int col, int row, int kx, int kz) {
            this.spec = spec;
            this.col = col;
            this.row = row;
            this.kx = kx;
            this.kz = kz;
        }
    }

    public static final class Tally {
        private final Map<String, Integer> perDungeon = new TreeMap<>();
        private final Set<String> told = new HashSet<>();

        int count(String kind) { return perDungeon.getOrDefault(kind, 0); }

        void add(String kind) { perDungeon.merge(kind, 1, Integer::sum); }

        void tell(String key, String message, Object... args) {
            if (told.add(key)) { ContentLog.LOGGER.debug(message, args); }
        }
    }

    public static final class TemplateSizes {
        private final DungeonWorld world;
        private final Map<String, int[]> cache = new HashMap<>();

        public TemplateSizes(DungeonWorld world) { this.world = world; }

        @Nullable public int[] size(String id) {
            if (!cache.containsKey(id)) {
                ResourceLocation named = ResourceLocation.tryParse(id);
                StructureTemplate template = named == null ? null : world.templates().get(named).orElse(null);
                cache.put(id, template == null ? null : new int[] {template.getSize().getX(), template.getSize().getY(), template.getSize().getZ()});
            }
            return cache.get(id);
        }
    }
}
