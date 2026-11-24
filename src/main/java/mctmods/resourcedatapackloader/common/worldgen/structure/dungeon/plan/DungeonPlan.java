package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Chest;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Entrance;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Feature;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Spawner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;

public final class DungeonPlan {
    private static final Bucket EMPTY = new Bucket();
    public final Site site;
    public final String themeId;
    public final List<Level> levels;
    public final Entrance entrance;
    public final int bossRoom;
    public final List<Spawner> spawners;
    public final List<Chest> chests;
    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;
    private final int[] rolls;
    private final Map<Long, Bucket> buckets;

    private DungeonPlan(Builder b) {
        this.site = b.site;
        this.themeId = b.themeId;
        this.rolls = b.rolls.clone();
        this.entrance = b.entrance;
        this.bossRoom = b.bossRoom;
        List<Level> frozen = new ArrayList<>();
        for (Level.Builder lb : b.levels) {
            frozen.add(lb.build());
        }
        this.levels = Collections.unmodifiableList(frozen);
        this.spawners = List.copyOf(b.spawners);
        this.chests = List.copyOf(b.chests);
        int[] box = {site.originX(), site.surfaceY(), site.originZ(), site.originX(), site.surfaceY(), site.originZ()};
        this.buckets = new TreeMap<>();
        for (Level level : levels) {
            for (Room room : level.rooms) {
                grow(box, room.x0 - 1, room.y0 - 1, room.z0 - 1, room.x1 + 1, room.y1 + 1, room.z1 + 1);
                spread(room.x0 - 1, room.z0 - 1, room.x1 + 1, room.z1 + 1, room, null, null, null, null);
                for (Feature f : room.features) {
                    grow(box, f.x0(), f.y0(), f.z0(), f.x1(), f.y1(), f.z1());
                    spread(f.x0(), f.z0(), f.x1(), f.z1(), null, null, null, f, null);
                }
            }
            for (Link link : level.links) {
                for (Segment s : link.segments) {
                    int sx0 = Math.min(s.x0(), s.x1()) - link.width;
                    int sz0 = Math.min(s.z0(), s.z1()) - link.width;
                    int sx1 = Math.max(s.x0(), s.x1()) + link.width;
                    int sz1 = Math.max(s.z0(), s.z1()) + link.width;
                    grow(box, sx0, Math.min(s.floorYStart(), s.floorYEnd()), sz0, sx1, Math.max(s.floorYStart(), s.floorYEnd()) + link.height, sz1);
                    spread(sx0, sz0, sx1, sz1, null, link, null, null, null);
                }
            }
            if (level.shaft != null) {
                Shaft s = level.shaft;
                grow(box, s.x() - 1, s.bottomY(), s.z() - 1, s.x() + 3, s.topY() + 4, s.z() + 3);
                spread(s.x() - 1, s.z() - 1, s.x() + 3, s.z() + 3, null, null, s, null, null);
            }
        }
        for (Entrance e : entrances()) {
            grow(box, e.x() - PlanRecords.ENTRANCE_REACH, site.surfaceY() - PlanRecords.ENTRANCE_DOWN, e.z() - PlanRecords.ENTRANCE_REACH, e.x() + PlanRecords.ENTRANCE_REACH, site.surfaceY() + PlanRecords.ENTRANCE_UP, e.z() + PlanRecords.ENTRANCE_REACH);
            spread(e.x() - PlanRecords.ENTRANCE_REACH, e.z() - PlanRecords.ENTRANCE_REACH, e.x() + PlanRecords.ENTRANCE_REACH, e.z() + PlanRecords.ENTRANCE_REACH, null, null, null, null, e);
        }
        for (Spawner s : spawners) {
            grow(box, s.x(), s.y(), s.z(), s.x(), s.y(), s.z());
            bucketFor(s.x() >> 4, s.z() >> 4).spawnerList.add(s);
        }
        for (Chest c : chests) {
            grow(box, c.x(), c.y(), c.z(), c.x(), c.y(), c.z());
            bucketFor(c.x() >> 4, c.z() >> 4).chestList.add(c);
        }
        this.minX = box[0];
        this.minY = box[1];
        this.minZ = box[2];
        this.maxX = box[3];
        this.maxY = box[4];
        this.maxZ = box[5];
    }

    private static void grow(int[] box, int x0, int y0, int z0, int x1, int y1, int z1) {
        box[0] = Math.min(box[0], x0);
        box[1] = Math.min(box[1], y0);
        box[2] = Math.min(box[2], z0);
        box[3] = Math.max(box[3], x1);
        box[4] = Math.max(box[4], y1);
        box[5] = Math.max(box[5], z1);
    }

    public List<Entrance> entrances() {
        List<Entrance> all = new ArrayList<>();
        if (entrance == null) { return all; }
        all.add(entrance);
        all.addAll(entrance.extras());
        return all;
    }

    private Bucket bucketFor(int chunkX, int chunkZ) { return buckets.computeIfAbsent(ChunkPos.pack(chunkX, chunkZ), _ -> new Bucket()); }

    private void spread(int x0, int z0, int x1, int z1, @Nullable Room room, @Nullable Link link, @Nullable Shaft shaft, @Nullable Feature feature, @Nullable Entrance entrance) {
        for (int cx = x0 >> 4; cx <= x1 >> 4; cx++) {
            for (int cz = z0 >> 4; cz <= z1 >> 4; cz++) {
                Bucket b = bucketFor(cx, cz);
                addOnce(b.roomList, room);
                addOnce(b.linkList, link);
                addOnce(b.shaftList, shaft);
                addOnce(b.featureList, feature);
                addOnce(b.entranceList, entrance);
            }
        }
    }

    private static <T> void addOnce(List<T> list, @Nullable T item) {
        if (item != null && !list.contains(item)) {
            list.add(item);
        }
    }

    public Bucket bucket(int chunkX, int chunkZ) {
        Bucket b = buckets.get(ChunkPos.pack(chunkX, chunkZ));
        return b == null ? EMPTY : b;
    }

    public int roll(int index) { return rolls[index]; }

    public int roll(String name) { return rolls[DungeonDef.rollIndex(name)]; }

    public int roomCount() {
        int n = 0;
        for (Level level : levels) {
            n += level.rooms.size();
        }
        return n;
    }

    public static Builder builder(Site site, String themeId) { return new Builder(site, themeId); }

    public static final class Builder {
        private final Site site;
        private final String themeId;
        private final int[] rolls = new int[DungeonDef.ROLLS.size()];
        private final List<Level.Builder> levels = new ArrayList<>();
        private final List<Spawner> spawners = new ArrayList<>();
        private final List<Chest> chests = new ArrayList<>();
        private Entrance entrance;
        private int bossRoom = -1;

        private Builder(Site site, String themeId) {
            this.site = site;
            this.themeId = themeId;
        }

        public Builder roll(int index, int degree) {
            rolls[index] = degree;
            return this;
        }

        public Level.Builder level(int floorY, int height, int slotSide, int gridX, int gridZ, int anchorX, int anchorZ, String levelThemeId) {
            Level.Builder lb = new Level.Builder(levels.size(), floorY, height, slotSide, gridX, gridZ, anchorX, anchorZ, levelThemeId);
            levels.add(lb);
            return lb;
        }

        public Level.Builder level(int index) { return levels.get(index); }

        public Builder entrance(Entrance entrance) {
            this.entrance = entrance;
            return this;
        }

        public void bossRoom(int bossRoom) { this.bossRoom = bossRoom; }

        public Builder spawner(Spawner spawner) {
            spawners.add(spawner);
            return this;
        }

        public Builder chest(Chest chest) {
            chests.add(chest);
            return this;
        }

        public DungeonPlan build() { return new DungeonPlan(this); }
    }

    public record Site(int originX, int originZ, long siteSeed, String sizeId, int surfaceY) {}

    public static final class Bucket {
        private final List<Room> roomList = new ArrayList<>();
        private final List<Link> linkList = new ArrayList<>();
        private final List<Shaft> shaftList = new ArrayList<>();
        private final List<Feature> featureList = new ArrayList<>();
        private final List<Entrance> entranceList = new ArrayList<>();
        private final List<Spawner> spawnerList = new ArrayList<>();
        private final List<Chest> chestList = new ArrayList<>();
        public final List<Room> rooms = Collections.unmodifiableList(roomList);
        public final List<Link> links = Collections.unmodifiableList(linkList);
        public final List<Shaft> shafts = Collections.unmodifiableList(shaftList);
        public final List<Feature> features = Collections.unmodifiableList(featureList);
        public final List<Entrance> entrances = Collections.unmodifiableList(entranceList);
        public final List<Spawner> spawners = Collections.unmodifiableList(spawnerList);
        public final List<Chest> chests = Collections.unmodifiableList(chestList);

        public boolean isEmpty() { return roomList.isEmpty() && linkList.isEmpty() && shaftList.isEmpty() && featureList.isEmpty() && entranceList.isEmpty() && spawnerList.isEmpty() && chestList.isEmpty(); }
    }

    public static final class Level {
        public final int index;
        public final int floorY;
        public final int height;
        public final int slotSide;
        public final int gridX;
        public final int gridZ;
        public final int anchorX;
        public final int anchorZ;
        public final String themeId;
        public final List<Room> rooms;
        public final List<Link> links;
        @Nullable
        public final Shaft shaft;
        public final int exitRoom;
        private final int[] rolls;

        private Level(Builder b) {
            this.rolls = b.rolls.clone();
            this.index = b.index;
            this.floorY = b.floorY;
            this.height = b.height;
            this.slotSide = b.slotSide;
            this.gridX = b.gridX;
            this.gridZ = b.gridZ;
            this.anchorX = b.anchorX;
            this.anchorZ = b.anchorZ;
            this.themeId = b.themeId;
            List<Room> frozen = new ArrayList<>();
            for (int i = 0; i < b.rooms.size(); i++) {
                Room room = b.rooms.get(i);
                List<Feature> extra = b.features.get(i);
                frozen.add(extra == null ? room : room.withFeatures(extra));
            }
            this.rooms = Collections.unmodifiableList(frozen);
            this.links = List.copyOf(b.links);
            this.shaft = b.shaft;
            this.exitRoom = b.exitRoom;
        }

        public int roll(int index) { return rolls[index]; }

        public int roll(String name) { return rolls[DungeonDef.rollIndex(name)]; }

        public static final class Builder {
            private final int[] rolls = new int[DungeonDef.ROLLS.size()];
            private final int index;
            private final int floorY;
            private final int height;
            private final int slotSide;
            private final int gridX;
            private final int gridZ;
            private final int anchorX;
            private final int anchorZ;
            private final String themeId;
            private final List<Room> rooms = new ArrayList<>();
            private final Map<Integer, List<Feature>> features = new TreeMap<>();
            private final List<Link> links = new ArrayList<>();
            private Shaft shaft;
            private int exitRoom = -1;

            private Builder(int index, int floorY, int height, int slotSide, int gridX, int gridZ, int anchorX, int anchorZ, String themeId) {
                this.index = index;
                this.floorY = floorY;
                this.height = height;
                this.slotSide = slotSide;
                this.gridX = gridX;
                this.gridZ = gridZ;
                this.anchorX = anchorX;
                this.anchorZ = anchorZ;
                this.themeId = themeId;
            }

            public void addRoom(Room room) { rooms.add(room); }

            void replaceRoom(int roomIndex, Room room) { rooms.set(roomIndex, room); }

            int index() { return index; }

            int[] rolls() { return rolls.clone(); }

            int slotSide() { return slotSide; }

            int gridX() { return gridX; }

            int gridZ() { return gridZ; }

            int anchorX() { return anchorX; }

            int anchorZ() { return anchorZ; }

            String themeId() { return themeId; }

            @Nullable Shaft shaft() { return shaft; }

            List<Link> links() { return links; }

            List<Feature> featuresOf(int roomIndex) {
                List<Feature> all = new ArrayList<>(rooms.get(roomIndex).features);
                all.addAll(features.getOrDefault(roomIndex, Collections.emptyList()));
                return all;
            }

            public Room room(int roomIndex) { return rooms.get(roomIndex); }

            public int roomCount() { return rooms.size(); }

            public void addFeature(int roomIndex, Feature feature) { features.computeIfAbsent(roomIndex, _ -> new ArrayList<>()).add(feature); }

            public void addLink(Link link) { links.add(link); }

            public void shaft(@Nullable Shaft shaft) { this.shaft = shaft; }

            public Builder rolls(int[] resolved) {
                System.arraycopy(resolved, 0, rolls, 0, rolls.length);
                return this;
            }

            public void exitRoom(int exitRoom) { this.exitRoom = exitRoom; }

            private Level build() { return new Level(this); }
        }
    }

    public static final class Room {
        public final int index;
        public final int gx;
        public final int gz;
        public final int kx;
        public final int kz;
        public final int x0;
        public final int y0;
        public final int z0;
        public final int x1;
        public final int y1;
        public final int z1;
        public final int floorY;
        public final int height;
        public final String kind;
        public final String shape;
        @Nullable
        public final String templateId;
        public final int turn;
        public final boolean node;
        public final boolean entrance;
        public final boolean start;
        public final boolean boss;
        public final long seed;
        public final List<Feature> features;

        public Room(int index, int gx, int gz, int kx, int kz, int[] box, int floorY, int height, String kind, String shape, @Nullable String templateId, int turn, boolean node, boolean entrance, boolean start, boolean boss, long seed, List<Feature> features) {
            this.index = index;
            this.gx = gx;
            this.gz = gz;
            this.kx = kx;
            this.kz = kz;
            this.x0 = box[0];
            this.y0 = box[1];
            this.z0 = box[2];
            this.x1 = box[3];
            this.y1 = box[4];
            this.z1 = box[5];
            this.floorY = floorY;
            this.height = height;
            this.kind = kind;
            this.shape = shape;
            this.templateId = templateId;
            this.turn = turn;
            this.node = node;
            this.entrance = entrance;
            this.start = start;
            this.boss = boss;
            this.seed = seed;
            this.features = List.copyOf(features);
        }

        public Room withFeatures(List<Feature> added) {
            List<Feature> all = new ArrayList<>(features);
            all.addAll(added);
            return new Room(index, gx, gz, kx, kz, new int[] {x0, y0, z0, x1, y1, z1}, floorY, height, kind, shape, templateId, turn, node, entrance, start, boss, seed, all);
        }

        Room withFlags(boolean node, boolean boss) {
            return new Room(index, gx, gz, kx, kz, new int[] {x0, y0, z0, x1, y1, z1}, floorY, height, kind, shape, templateId, turn, node, entrance, start, boss, seed, features);
        }

        public boolean contains(int x, int z) { return x >= x0 && x <= x1 && z >= z0 && z <= z1; }
    }

    public record Segment(int x0, int z0, int x1, int z1, int floorYStart, int floorYEnd) {
        public int floorAt(int x, int z) {
            int along = Math.abs(x - x0) + Math.abs(z - z0);
            int span = Math.abs(floorYEnd - floorYStart);
            return floorYStart + Integer.signum(floorYEnd - floorYStart) * Math.min(along, span);
        }

        public int floorClamped(int x, int z) {
            return floorAt(Math.clamp(x, Math.min(x0, x1), Math.max(x0, x1)), Math.clamp(z, Math.min(z0, z1), Math.max(z0, z1)));
        }
    }

    public static final class Link {
        public final int roomA;
        public final int roomB;
        public final int doorAX;
        public final int doorAZ;
        public final int doorBX;
        public final int doorBZ;
        public final int jogAX;
        public final int jogAZ;
        public final int jogBX;
        public final int jogBZ;
        public final int jogAxis;
        public final int jogLine;
        public final int width;
        public final int height;
        public final int floorYA;
        public final int floorYB;
        public final List<Segment> segments;

        public Link(int roomA, int roomB, int[] doorA, int[] doorB, int[] jog, int jogAxis, int jogLine, int width, int height, int floorYA, int floorYB, List<Segment> segments) {
            this.roomA = roomA;
            this.roomB = roomB;
            this.doorAX = doorA[0];
            this.doorAZ = doorA[1];
            this.doorBX = doorB[0];
            this.doorBZ = doorB[1];
            this.jogAX = jog[0];
            this.jogAZ = jog[1];
            this.jogBX = jog[2];
            this.jogBZ = jog[3];
            this.jogAxis = jogAxis;
            this.jogLine = jogLine;
            this.width = width;
            this.height = height;
            this.floorYA = floorYA;
            this.floorYB = floorYB;
            this.segments = List.copyOf(segments);
        }

        public static int low(int width) { return -((width - 1) / 2); }

        public static int high(int width) { return width / 2; }

        public int[] footprint(Segment s) {
            int lo = low(width);
            int hi = high(width);
            int minX = Math.min(s.x0(), s.x1());
            int maxX = Math.max(s.x0(), s.x1());
            int minZ = Math.min(s.z0(), s.z1());
            int maxZ = Math.max(s.z0(), s.z1());
            return new int[] {minX != maxX ? minX : minX + lo, minZ != maxZ ? minZ : minZ + lo, minX != maxX ? maxX : maxX + hi, minZ != maxZ ? maxZ : maxZ + hi};
        }
    }

}
