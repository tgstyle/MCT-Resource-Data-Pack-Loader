package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeonThemes;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.DungeonSites;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Link;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPopulation;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Chest;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Shaft;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Spawner;
import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentMapFill;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class DungeonDecor {
    private static final Set<String> MISSING = new HashSet<>();
    private static final String WALL_LINE = "wallLine";
    private static final String FLOOR_PATCH = "floorPatch";
    private static final String SCATTER = "scatter";
    private static final String PILLARS_ROW = "pillarsRow";
    private static final String BANNER_LINE = "bannerLine";
    private static final String TORCH_LINE = "torchLine";
    private static final String FILL = "fill";
    private static final String ON_CEILING = "ceiling";
    private static final String ON_WALL = "wall";
    private static final int SHAFT_SIDE = 3;
    private static final int WALL_RUN = 4;
    private static final int DEFAULT_HEIGHT = 1;
    private static final int DEFAULT_GAP = 1;
    private static final int DEFAULT_SPACING = 4;
    private static final int DEFAULT_PILLAR_HEIGHT = 3;
    private static final int DEFAULT_PILLAR_GAP = 2;
    private static final int DEFAULT_PERCENT = 10;
    private static final int DEFAULT_PER100 = 3;
    private static final int EYE_ROW = 2;
    private static final long ROLL_SALT = 0x5DECL;

    private DungeonDecor() {}

    public static void write(WorldGenLevel world, DungeonPlan plan, DungeonSites.Site site, DungeonPlan.Bucket bucket, BoundingBox box) {
        for (Room room : bucket.rooms) {
            if (room.templateId != null || !box.intersects(room.x0, room.z0, room.x1, room.z1) || room.y1 < box.minY() || room.floorY > box.maxY()) { continue; }
            Level level = levelOf(plan, room);
            DungeonThemeDef theme = level == null ? null : ContentDungeonThemes.byName(level.themeId);
            DungeonPopulation.Draw draw = theme == null ? null : DungeonPopulation.draw(plan, site.def, level, room);
            if (draw == null || draw.decor().isEmpty()) { continue; }
            Scene scene = new Scene(world, plan, level, room, box, new FillRules(site.def.key(), List.of(), null), draw, theme);
            for (String name : draw.decor()) {
                DungeonThemeDef.Decorator decorator = theme.decorators().get(name);
                if (decorator == null) { ContentLog.LOGGER.errorOnce(MISSING, theme.key() + "/" + name, "Dungeon room kind '{}' lists decor '{}', which theme {} does not define, so it is left out", room.kind, name, theme.key()); }
                else { scene.apply(name, decorator); }
            }
        }
    }

    @Nullable private static Level levelOf(DungeonPlan plan, Room room) {
        for (Level level : plan.levels) {
            if (level.rooms.contains(room)) { return level; }
        }
        return null;
    }

    private static long key(int x, int y, int z) { return new BlockPos(x, y, z).asLong(); }

    private static <T extends Comparable<T>> BlockState facing(BlockState state, Property<T> property, Direction away) { return property.getValue(away.getName()).map(value -> state.setValue(property, value)).orElse(state); }

    private static BlockState oriented(BlockState state, @Nullable Direction away) {
        if (away == null) { return state; }
        BlockState result = state;
        for (Property<?> property : state.getProperties()) {
            if (property instanceof EnumProperty<?> side && side.getValueClass() == Direction.class) { result = facing(result, side, away); }
            else if (property instanceof BooleanProperty flag && flag.getName().equals(away.getOpposite().getName())) { result = result.setValue(flag, true); }
        }
        return result;
    }

    private interface Visit {
        void at(int x, int z, int index, Direction away);
    }

    private static final class Scene {
        private final WorldGenLevel world;
        private final Level level;
        private final Room room;
        private final BoundingBox box;
        private final FillRules rules;
        private final DungeonPopulation.Draw draw;
        private final DungeonThemeDef theme;
        private final Set<Long> taken = new HashSet<>();
        private final List<int[]> doors = new ArrayList<>();
        private DungeonThemeDef.Decorator decorator;
        private List<ContentStates.Spec> specs;
        private long seed;

        private Scene(WorldGenLevel world, DungeonPlan plan, Level level, Room room, BoundingBox box, FillRules rules, DungeonPopulation.Draw draw, DungeonThemeDef theme) {
            this.world = world;
            this.level = level;
            this.room = room;
            this.box = box;
            this.rules = rules;
            this.draw = draw;
            this.theme = theme;
            for (Link link : level.links) {
                if (link.roomA == room.index) { doors.add(new int[] {link.doorAX, link.doorAZ, 1 + link.width / 2}); }
                if (link.roomB == room.index) { doors.add(new int[] {link.doorBX, link.doorBZ, 1 + link.width / 2}); }
            }
            for (Spawner spawner : plan.spawners) {
                if (room.contains(spawner.x(), spawner.z()) && spawner.y() >= room.floorY && spawner.y() <= room.y1) { taken.add(key(spawner.x(), spawner.y(), spawner.z())); }
            }
            for (Chest chest : plan.chests) {
                if (room.contains(chest.x(), chest.z()) && chest.y() >= room.floorY && chest.y() <= room.y1) { taken.add(key(chest.x(), chest.y(), chest.z())); }
            }
        }

        private void apply(String name, DungeonThemeDef.Decorator held) {
            List<ContentStates.Spec> found = new ArrayList<>();
            for (String block : held.blocks()) {
                ContentStates.Spec spec = ContentStates.spec(block, theme.key());
                if (spec != null) { found.add(spec); }
            }
            if (found.isEmpty()) { return; }
            decorator = held;
            specs = found;
            seed = Hashes.mix(room.seed, name.hashCode(), 0, 0);
            switch (held.type()) {
                case WALL_LINE:
                    wallLine();
                    break;
                case FLOOR_PATCH:
                    floorPatch();
                    break;
                case SCATTER:
                    scatter();
                    break;
                case PILLARS_ROW:
                    pillarsRow();
                    break;
                case BANNER_LINE:
                case TORCH_LINE:
                    hung();
                    break;
                case FILL:
                    fill();
                    break;
                default:
                    break;
            }
        }

        private int number(String key, int otherwise) { return decorator.numbers().getOrDefault(key, otherwise); }

        private void wallLine() {
            int height = Math.max(1, number("height", DEFAULT_HEIGHT));
            int span = WALL_RUN + Math.max(0, number("gap", DEFAULT_GAP));
            walls((x, z, index, away) -> {
                if (index % span >= WALL_RUN) { return; }
                for (int row = 0; row < height; row++) {
                    put(x, room.floorY + row, z, away);
                }
            });
        }

        private void floorPatch() {
            int percent = number("percent", DEFAULT_PERCENT);
            floors((x, z, _, _) -> {
                if (roll(x, room.floorY, z) < percent) { put(x, room.floorY, z, null); }
            });
        }

        private void scatter() {
            int per100 = number("per100", DEFAULT_PER100);
            String on = decorator.on() == null ? "floor" : decorator.on();
            if (ON_WALL.equals(on)) {
                walls((x, z, _, away) -> {
                    for (int y = room.floorY; y <= room.y1; y++) {
                        if (roll(x, y, z) < per100) { put(x, y, z, away); }
                    }
                });
            }
            else if (ON_CEILING.equals(on)) {
                floors((x, z, _, _) -> {
                    if (roll(x, room.y1, z) < per100) { put(x, room.y1, z, Direction.DOWN); }
                });
            }
            else {
                floors((x, z, _, _) -> {
                    if (roll(x, room.floorY, z) < per100) { put(x, room.floorY, z, null); }
                });
            }
        }

        private void pillarsRow() {
            int height = Math.max(1, number("height", DEFAULT_PILLAR_HEIGHT));
            int step = Math.max(0, number("gap", DEFAULT_PILLAR_GAP)) + 1;
            boolean alongX = room.x1 - room.x0 >= room.z1 - room.z0;
            int line = alongX ? room.z0 + room.z1 >> 1 : room.x0 + room.x1 >> 1;
            int length = alongX ? room.x1 - room.x0 : room.z1 - room.z0;
            floors((x, z, _, _) -> {
                if ((alongX ? z : x) != line) { return; }
                int along = alongX ? x - room.x0 : z - room.z0;
                if (along < 1 || along > length - 1 || (along - 1) % step != 0) { return; }
                for (int row = 0; row < height; row++) {
                    put(x, room.floorY + row, z, null);
                }
            });
        }

        private void hung() {
            int spacing = Math.max(1, number("gap", DEFAULT_SPACING));
            int y = room.floorY + number("height", EYE_ROW);
            walls((x, z, index, away) -> {
                if (index % spacing == 0) { put(x, y, z, away); }
            });
        }

        private void fill() {
            if (decorator.loot() == null) { return; }
            int per100 = number("per100", DEFAULT_PER100);
            walls((x, z, _, away) -> {
                if (roll(x, room.floorY, z) < per100 && !crowded(x, z)) { container(x, z, away); }
            });
        }

        private void container(int x, int z, Direction away) {
            if (shut(x, room.floorY, z)) { return; }
            long lootSeed = Hashes.mix(seed, x, room.floorY, z);
            String table = draw.loot(decorator.loot(), lootSeed);
            if (table == null) { return; }
            BlockPos at = new BlockPos(x, room.floorY, z);
            ContentStates.Spec spec = pick(x, room.floorY, z);
            BlockState state = oriented(spec.state(), away);
            CompoundTag tag;
            if (state.getBlock() instanceof ChestBlock) { tag = ContentMapFill.fill(rules, lootSeed, new StructureTemplate.StructureBlockInfo(at, state, spec.tag()), null, Identifier.parse(table)).nbt(); }
            else {
                tag = spec.tag() == null ? new CompoundTag() : spec.tag().copy();
                tag.putString("LootTable", table);
                tag.putLong("LootTableSeed", lootSeed);
            }
            place(at, state, tag);
            taken.add(at.asLong());
        }

        private boolean crowded(int x, int z) {
            for (Direction side : Direction.Plane.HORIZONTAL) {
                if (taken.contains(key(x + side.getStepX(), room.floorY, z + side.getStepZ()))) { return true; }
            }
            return false;
        }

        private int roll(int x, int y, int z) { return (int) Math.floorMod(Hashes.mix(seed ^ ROLL_SALT, x, y, z), 100L); }

        private ContentStates.Spec pick(int x, int y, int z) { return specs.get(Math.floorMod(Hashes.mix(seed, x, y, z), specs.size())); }

        private boolean shut(int x, int y, int z) {
            if (y < Math.max(box.minY(), room.floorY) || y > Math.min(box.maxY(), room.y1) || x < box.minX() || x > box.maxX() || z < box.minZ() || z > box.maxZ()) { return true; }
            return taken.contains(key(x, y, z)) || !world.isEmptyBlock(new BlockPos(x, y, z));
        }

        private void put(int x, int y, int z, @Nullable Direction away) {
            if (shut(x, y, z)) { return; }
            ContentStates.Spec spec = pick(x, y, z);
            place(new BlockPos(x, y, z), oriented(spec.state(), away), spec.tag());
            taken.add(key(x, y, z));
        }

        private void place(BlockPos at, BlockState state, @Nullable CompoundTag tag) {
            if (!DungeonWriter.set(world, at, state) || tag == null) { return; }
            DungeonWriter.load(world, at, tag);
        }

        private boolean usable(int x, int z) {
            if (!DungeonShapes.isFloorCell(room, room.features, x, z)) { return false; }
            for (int[] door : doors) {
                if (Math.abs(x - door[0]) <= door[2] && Math.abs(z - door[1]) <= door[2]) { return false; }
            }
            Shaft shaft = level.shaft;
            return shaft == null || x < shaft.x() - 1 || x > shaft.x() + SHAFT_SIDE || z < shaft.z() - 1 || z > shaft.z() + SHAFT_SIDE;
        }

        private void floors(Visit visit) {
            for (int x = Math.max(room.x0, box.minX()); x <= Math.min(room.x1, box.maxX()); x++) {
                for (int z = Math.max(room.z0, box.minZ()); z <= Math.min(room.z1, box.maxZ()); z++) {
                    if (usable(x, z)) { visit.at(x, z, 0, awayFrom(x, z)); }
                }
            }
        }

        private void walls(Visit visit) {
            for (int x = Math.max(room.x0, box.minX()); x <= Math.min(room.x1, box.maxX()); x++) {
                for (int z = Math.max(room.z0, box.minZ()); z <= Math.min(room.z1, box.maxZ()); z++) {
                    if ((x == room.x0 || x == room.x1 || z == room.z0 || z == room.z1) && usable(x, z)) { visit.at(x, z, ring(x, z), awayFrom(x, z)); }
                }
            }
        }

        private int ring(int x, int z) {
            int width = room.x1 - room.x0 + 1;
            int depth = room.z1 - room.z0 + 1;
            if (z == room.z0) { return x - room.x0; }
            if (x == room.x1) { return width + z - room.z0; }
            if (z == room.z1) { return width + depth + room.x1 - x; }
            return 2 * width + depth + room.z1 - z;
        }

        private Direction awayFrom(int x, int z) {
            if (x == room.x0) { return Direction.EAST; }
            if (x == room.x1) { return Direction.WEST; }
            if (z == room.z0) { return Direction.SOUTH; }
            return Direction.NORTH;
        }
    }
}
