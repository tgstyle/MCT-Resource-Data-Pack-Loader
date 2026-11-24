package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonThemeDef;
import mctmods.resourcedatapackloader.common.util.world.Box;
import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;
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
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentMapFill;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.math.MathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockChest;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.Template;

public final class DungeonDecor {
    private static final Set<String> MISSING = new HashSet<>();
    private static final String WALL_LINE = "wallLine";
    private static final String FLOOR_PATCH = "floorPatch";
    private static final String SCATTER = "scatter";
    private static final String PILLARS_ROW = "pillarsRow";
    private static final String BANNER_LINE = "bannerLine";
    private static final String TORCH_LINE = "torchLine";
    private static final String FILL = "fill";
    private static final String HEIGHT = "height";
    private static final String GAP = "gap";
    private static final String ON_CEILING = "ceiling";
    private static final String ON_WALL = "wall";
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

    public static void write(World world, DungeonPlan plan, DungeonSites.Site site, DungeonPlan.Bucket bucket, StructureBoundingBox box) {
        FillRules rules = new FillRules(site.def.key, Collections.emptyList(), null);
        for (Room room : bucket.rooms) {
            if (room.templateId != null || !box.intersectsWith(room.x0, room.z0, room.x1, room.z1) || room.y1 < box.minY || room.floorY > box.maxY) { continue; }
            Level level = levelOf(plan, room);
            DungeonThemeDef theme = level == null ? null : ContentDungeonThemes.byName(level.themeId);
            DungeonPopulation.Draw draw = theme == null ? null : DungeonPopulation.draw(plan, site.def, level, room);
            if (draw == null || draw.decor().isEmpty()) { continue; }
            Scene scene = new Scene(world, plan, level, room, box, rules, draw, theme);
            for (String name : draw.decor()) {
                DungeonThemeDef.Decorator decorator = theme.decorators.get(name);
                if (decorator == null) { ContentLog.LOGGER.errorOnce(MISSING, theme.key + "/" + name, "Dungeon room kind '{}' lists decor '{}', which theme {} does not define, so it is left out", room.kind, name, theme.key); }
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

    private static long key(int x, int y, int z) { return new BlockPos(x, y, z).toLong(); }

    private static IBlockState oriented(IBlockState state, @Nullable EnumFacing away) {
        if (away == null) { return state; }
        IBlockState result = state;
        for (IProperty<?> property : state.getPropertyKeys()) {
            if (property instanceof PropertyDirection && property.getAllowedValues().contains(away)) { result = result.withProperty((PropertyDirection) property, away); }
            else if (property instanceof PropertyBool && property.getName().equals(away.getOpposite().getName())) { result = result.withProperty((PropertyBool) property, true); }
        }
        return result;
    }

    private interface Visit {
        void at(int x, int z, int index, EnumFacing away);
    }

    private static final class Scene {
        private final World world;
        private final Level level;
        private final Room room;
        private final StructureBoundingBox box;
        private final FillRules rules;
        private final DungeonPopulation.Draw draw;
        private final DungeonThemeDef theme;
        private final Set<Long> taken = new HashSet<>();
        private final List<int[]> doors = new ArrayList<>();
        private DungeonThemeDef.Decorator decorator;
        private List<ContentStates.Spec> specs;
        private long seed;

        private Scene(World world, DungeonPlan plan, Level level, Room room, StructureBoundingBox box, FillRules rules, DungeonPopulation.Draw draw, DungeonThemeDef theme) {
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
                if (room.contains(spawner.x, spawner.z) && spawner.y >= room.floorY && spawner.y <= room.y1) { taken.add(key(spawner.x, spawner.y, spawner.z)); }
            }
            for (Chest chest : plan.chests) {
                if (room.contains(chest.x, chest.z) && chest.y >= room.floorY && chest.y <= room.y1) { taken.add(key(chest.x, chest.y, chest.z)); }
            }
        }

        private void apply(String name, DungeonThemeDef.Decorator held) {
            List<ContentStates.Spec> found = new ArrayList<>();
            for (String block : held.blocks) {
                ContentStates.Spec spec = ContentStates.spec(block, theme.key);
                if (spec != null) { found.add(spec); }
            }
            if (found.isEmpty()) { return; }
            decorator = held;
            specs = found;
            seed = MathUtil.mix(room.seed, name.hashCode(), 0, 0);
            switch (held.type) {
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

        private int number(String key, int otherwise) { return decorator.numbers.getOrDefault(key, otherwise); }

        private void wallLine() {
            int height = Math.max(1, number(HEIGHT, DEFAULT_HEIGHT));
            int span = WALL_RUN + Math.max(0, number(GAP, DEFAULT_GAP));
            walls((x, z, index, away) -> {
                if (index % span >= WALL_RUN) { return; }
                for (int row = 0; row < height; row++) {
                    put(x, room.floorY + row, z, away);
                }
            });
        }

        private void floorPatch() {
            int percent = number("percent", DEFAULT_PERCENT);
            floors((x, z, index, away) -> {
                if (roll(x, room.floorY, z) < percent) { put(x, room.floorY, z, null); }
            });
        }

        private void scatter() {
            int per100 = number("per100", DEFAULT_PER100);
            String on = decorator.on == null ? "floor" : decorator.on;
            if (ON_WALL.equals(on)) {
                walls((x, z, index, away) -> {
                    for (int y = room.floorY; y <= room.y1; y++) {
                        if (roll(x, y, z) < per100) { put(x, y, z, away); }
                    }
                });
            }
            else if (ON_CEILING.equals(on)) {
                floors((x, z, index, away) -> {
                    if (roll(x, room.y1, z) < per100) { put(x, room.y1, z, EnumFacing.DOWN); }
                });
            }
            else {
                floors((x, z, index, away) -> {
                    if (roll(x, room.floorY, z) < per100) { put(x, room.floorY, z, null); }
                });
            }
        }

        private void pillarsRow() {
            int height = Math.max(1, number(HEIGHT, DEFAULT_PILLAR_HEIGHT));
            int step = Math.max(0, number(GAP, DEFAULT_PILLAR_GAP)) + 1;
            boolean alongX = room.x1 - room.x0 >= room.z1 - room.z0;
            int line = alongX ? room.z0 + room.z1 >> 1 : room.x0 + room.x1 >> 1;
            int length = alongX ? room.x1 - room.x0 : room.z1 - room.z0;
            floors((x, z, index, away) -> {
                if ((alongX ? z : x) != line) { return; }
                int along = alongX ? x - room.x0 : z - room.z0;
                if (along < 1 || along > length - 1 || (along - 1) % step != 0) { return; }
                for (int row = 0; row < height; row++) {
                    put(x, room.floorY + row, z, null);
                }
            });
        }

        private void hung() {
            int spacing = Math.max(1, number(GAP, DEFAULT_SPACING));
            int y = room.floorY + number(HEIGHT, EYE_ROW);
            walls((x, z, index, away) -> {
                if (index % spacing == 0) { put(x, y, z, away); }
            });
        }

        private void fill() {
            if (decorator.loot == null) { return; }
            int per100 = number("per100", DEFAULT_PER100);
            walls((x, z, index, away) -> {
                if (roll(x, room.floorY, z) < per100 && !crowded(x, z)) { container(x, z, away); }
            });
        }

        private void container(int x, int z, EnumFacing away) {
            if (shut(x, room.floorY, z)) { return; }
            long lootSeed = MathUtil.mix(seed, x, room.floorY, z);
            String table = draw.loot(decorator.loot, lootSeed);
            if (table == null) { return; }
            BlockPos at = new BlockPos(x, room.floorY, z);
            ContentStates.Spec spec = pick(x, room.floorY, z);
            IBlockState state = oriented(spec.state, away);
            NBTTagCompound tag;
            if (state.getBlock() instanceof BlockChest) { tag = ContentMapFill.fill(rules, lootSeed, at, new Template.BlockInfo(at, state, spec.tag), null, new ResourceLocation(table)).tileentityData; }
            else {
                tag = spec.tag == null ? new NBTTagCompound() : spec.tag.copy();
                tag.setString("LootTable", table);
                tag.setLong("LootTableSeed", lootSeed);
            }
            ContentStates.place(world, at, state, tag);
            taken.add(at.toLong());
        }

        private boolean crowded(int x, int z) {
            for (EnumFacing side : EnumFacing.HORIZONTALS) {
                if (taken.contains(key(x + side.getXOffset(), room.floorY, z + side.getZOffset()))) { return true; }
            }
            return false;
        }

        private int roll(int x, int y, int z) { return MathUtil.percent(MathUtil.mix(seed ^ ROLL_SALT, x, y, z)); }

        private ContentStates.Spec pick(int x, int y, int z) { return specs.get((int) Math.floorMod(MathUtil.mix(seed, x, y, z), specs.size())); }

        private boolean shut(int x, int y, int z) {
            if (y < Math.max(box.minY, room.floorY) || y > Math.min(box.maxY, room.y1) || x < box.minX || x > box.maxX || z < box.minZ || z > box.maxZ) { return true; }
            return taken.contains(key(x, y, z)) || !world.isAirBlock(new BlockPos(x, y, z));
        }

        private void put(int x, int y, int z, @Nullable EnumFacing away) {
            if (shut(x, y, z)) { return; }
            ContentStates.Spec spec = pick(x, y, z);
            ContentStates.place(world, new BlockPos(x, y, z), oriented(spec.state, away), spec.tag);
            taken.add(key(x, y, z));
        }

        private boolean usable(int x, int z) {
            if (!DungeonShapes.isFloorCell(room, room.features, x, z)) { return false; }
            for (int[] door : doors) {
                if (Math.abs(x - door[0]) <= door[2] && Math.abs(z - door[1]) <= door[2]) { return false; }
            }
            Shaft shaft = level.shaft;
            return shaft == null || !shaft.covers(x, z, 1);
        }

        private void floors(Visit visit) {
            StructureBoundingBox clip = Box.clip(box, room.x0, box.minY, room.z0, room.x1, box.maxY, room.z1, 0);
            for (int x = clip.minX; x <= clip.maxX; x++) {
                for (int z = clip.minZ; z <= clip.maxZ; z++) {
                    if (usable(x, z)) { visit.at(x, z, 0, room.away(x, z)); }
                }
            }
        }

        private void walls(Visit visit) {
            StructureBoundingBox clip = Box.clip(box, room.x0, box.minY, room.z0, room.x1, box.maxY, room.z1, 0);
            for (int x = clip.minX; x <= clip.maxX; x++) {
                for (int z = clip.minZ; z <= clip.maxZ; z++) {
                    if ((x == room.x0 || x == room.x1 || z == room.z0 || z == room.z1) && usable(x, z)) { visit.at(x, z, ring(x, z), room.away(x, z)); }
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
    }
}
