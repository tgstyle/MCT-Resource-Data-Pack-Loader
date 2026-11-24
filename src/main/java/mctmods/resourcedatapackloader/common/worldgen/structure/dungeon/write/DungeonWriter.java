package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;
import mctmods.resourcedatapackloader.common.respawn.RespawnRecorder;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.worldgen.structure.RespawnBrokenProcessor;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.DungeonSites;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlanner;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Chest;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Spawner;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentMapFill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public final class DungeonWriter {
    private static final int FLAGS = BlockFlags.UPDATE_CLIENTS_KNOWN_SHAPE;
    private static final Set<String> BAD_NBT = new HashSet<>();
    private static final WeakHashMap<DungeonPlan, DungeonShapes.Palette> PALETTES = new WeakHashMap<>();

    private static final ThreadLocal<Boolean> BROKEN = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Set<Long>> WRITTEN = ThreadLocal.withInitial(HashSet::new);

    private DungeonWriter() {}

    public static void rewrite(WorldGenLevel level, DungeonSites.Site site, BoundingBox window, int floor, int ceiling, boolean broken) {
        BROKEN.set(broken);
        try { build(level, site, window, floor, ceiling); }
        finally { BROKEN.set(false); WRITTEN.get().clear(); }
    }

    public static void write(WorldGenLevel level, DungeonSites.Site site, BoundingBox window, int floor, int ceiling) {
        DungeonPlan plan = build(level, site, window, floor, ceiling);
        if (site.def.respawn() == null) { return; }
        BoundingBox area = new BoundingBox(window.minX(), Math.max(window.minY(), plan.minY), window.minZ(), window.maxX(), Math.min(window.maxY(), plan.maxY), window.maxZ());
        RespawnInstance fresh = new RespawnInstance(RespawnInstance.DUNGEON, site.def.key().toString(), "", new BlockPos(window.minX(), window.minY(), window.minZ()), Rotation.NONE, Mirror.NONE, 100, new BlockPos(site.originX, site.surfaceY, site.originZ).asLong(), area);
        RespawnRecorder.record(level.getLevel(), site.def.respawn(), fresh, window, level);
    }

    static boolean set(WorldGenLevel level, BlockPos at, BlockState state) {
        if (BROKEN.get()) {
            BlockState there = level.getBlockState(at);
            if (there == state || !there.canBeReplaced() && !WRITTEN.get().contains(at.asLong())) { return false; }
            WRITTEN.get().add(at.asLong());
        }
        level.setBlock(at, state, FLAGS);
        return true;
    }

    static boolean broken() { return BROKEN.get(); }

    static List<StructureProcessor> gated(@Nullable StructureProcessor first) {
        List<StructureProcessor> chain = new ArrayList<>();
        if (first != null) { chain.add(first); }
        if (BROKEN.get()) { chain.add(new RespawnBrokenProcessor()); }
        return chain;
    }

    private static DungeonPlan build(WorldGenLevel level, DungeonSites.Site site, BoundingBox window, int floor, int ceiling) {
        DungeonPlan plan = DungeonPlanner.get(ContentDungeons.world(level), site.def, site);
        DungeonShapes.Palette palette = paletteOf(plan);
        if (ContentLog.LOGGER.debugEnabled() && inside(window, plan.site.originX(), plan.site.originZ())) { log(level, plan); }
        int low = Math.max(floor, plan.minY);
        int high = Math.min(ceiling, plan.maxY);
        FillRules rules = new FillRules(site.def.key(), List.of(), null);
        DungeonTemplates.write(level, plan, site, window, low, high);
        for (int chunkX = window.minX() >> 4; chunkX <= window.maxX() >> 4; chunkX++) {
            for (int chunkZ = window.minZ() >> 4; chunkZ <= window.maxZ() >> 4; chunkZ++) {
                DungeonPlan.Bucket bucket = plan.bucket(chunkX, chunkZ);
                if (bucket.isEmpty()) { continue; }
                BoundingBox columns = new BoundingBox(Math.max(window.minX(), chunkX << 4), low, Math.max(window.minZ(), chunkZ << 4), Math.min(window.maxX(), (chunkX << 4) + 15), high, Math.min(window.maxZ(), (chunkZ << 4) + 15));
                shape(level, plan, bucket, palette, columns);
                DungeonShafts.write(level, plan, bucket, palette, columns);
                DungeonEntrance.write(level, plan, bucket, palette, columns);
                DungeonSupport.write(level, plan, bucket, palette, columns, site.def.supportDepth(), window.minY());
                DungeonDecor.write(level, plan, site, bucket, columns);
                DungeonDecay.write(level, plan, site.def, bucket, palette, columns, window);
                for (Spawner spawner : bucket.spawners) {
                    if (inside(window, spawner.x(), spawner.z()) && spawner.y() >= floor && spawner.y() <= ceiling) { spawner(level, rules, spawner); }
                }
                for (Chest chest : bucket.chests) {
                    if (inside(window, chest.x(), chest.z()) && chest.y() >= floor && chest.y() <= ceiling) { chest(level, plan, rules, chest); }
                }
            }
        }
        return plan;
    }

    private static synchronized DungeonShapes.Palette paletteOf(DungeonPlan plan) { return PALETTES.computeIfAbsent(plan, DungeonShapes.Palette::new); }

    private static boolean inside(BoundingBox window, int x, int z) { return x >= window.minX() && x <= window.maxX() && z >= window.minZ() && z <= window.maxZ(); }

    private static void shape(WorldGenLevel level, DungeonPlan plan, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, BoundingBox columns) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = columns.minX(); x <= columns.maxX(); x++) {
            for (int z = columns.minZ(); z <= columns.maxZ(); z++) {
                for (int y = columns.minY(); y <= columns.maxY(); y++) {
                    BlockState state = DungeonShapes.at(plan, bucket, x, y, z, palette);
                    if (state != null) { set(level, pos.set(x, y, z), state); }
                }
            }
        }
    }

    private static void spawner(WorldGenLevel level, FillRules rules, Spawner spawner) {
        BlockPos at = new BlockPos(spawner.x(), spawner.y(), spawner.z());
        BlockState state = Blocks.SPAWNER.defaultBlockState();
        if (!set(level, at, state)) { return; }
        CompoundTag tag = spawner(rules, spawner.seed(), new StructureTemplate.StructureBlockInfo(at, state, null), spawner.mobId()).nbt();
        if (tag == null) { return; }
        load(level, at, tag);
    }

    static StructureTemplate.StructureBlockInfo spawner(FillRules rules, long seed, StructureTemplate.StructureBlockInfo info, String mob) {
        int brace = mob.indexOf('{');
        StructureTemplate.StructureBlockInfo filled = ContentMapFill.fill(rules, seed, info, brace < 0 ? mob : mob.substring(0, brace), null);
        if (filled != info && brace >= 0 && filled.nbt() != null) { merge(filled.nbt(), mob.substring(brace), mob); }
        return filled;
    }

    private static void merge(CompoundTag tag, String written, String whole) {
        try {
            CompoundTag extra = TagParser.parseCompoundFully(written);
            CompoundTag data = tag.getCompoundOrEmpty("SpawnData");
            CompoundTag entity = data.getCompoundOrEmpty("entity");
            String named = entity.getStringOr("id", "");
            entity.merge(extra);
            entity.putString("id", named);
            data.put("entity", entity);
            tag.put("SpawnData", data);
        }
        catch (CommandSyntaxException bad) { ContentLog.LOGGER.errorOnce(BAD_NBT, whole, "Dungeon spawner nbt '{}' could not be parsed, so its mob spawns without it: {}", whole, bad.getMessage()); }
    }

    private static void chest(WorldGenLevel level, DungeonPlan plan, FillRules rules, Chest chest) {
        BlockPos at = new BlockPos(chest.x(), chest.y(), chest.z());
        BlockState state = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing(plan, chest));
        if (!set(level, at, state)) { return; }
        CompoundTag tag = ContentMapFill.fill(rules, chest.seed(), new StructureTemplate.StructureBlockInfo(at, state, null), null, Identifier.parse(chest.lootTable())).nbt();
        if (tag != null) { load(level, at, tag); }
    }

    private static Direction facing(DungeonPlan plan, Chest chest) {
        for (Level level : plan.levels) {
            for (Room room : level.rooms) {
                if (chest.y() < room.y0 || chest.y() > room.y1 || !room.contains(chest.x(), chest.z())) { continue; }
                if (chest.x() == room.x0) { return Direction.EAST; }
                if (chest.x() == room.x1) { return Direction.WEST; }
                if (chest.z() == room.z0) { return Direction.SOUTH; }
                if (chest.z() == room.z1) { return Direction.NORTH; }
                return Direction.NORTH;
            }
        }
        return Direction.NORTH;
    }

    static void load(WorldGenLevel level, BlockPos at, CompoundTag tag) {
        BlockEntity entity = level.getBlockEntity(at);
        if (entity == null) { return; }
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        entity.saveWithoutMetadata(output);
        CompoundTag data = output.buildResult();
        data.merge(tag);
        entity.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data));
        entity.setChanged();
    }

    private static void log(WorldGenLevel level, DungeonPlan plan) {
        String dim = level.getLevel().dimension().identifier().toString();
        String origin = plan.site.originX() + "," + plan.site.surfaceY() + "," + plan.site.originZ();
        BlockPos stand = DungeonEntrance.standingCell(plan);
        ContentLog.LOGGER.debug("dungeon site={} dim={} size={},{},{} levels={} entrance={},{},{}", origin, dim, plan.maxX - plan.minX + 1, plan.maxY - plan.minY + 1, plan.maxZ - plan.minZ + 1, plan.levels.size(), stand.getX(), stand.getY(), stand.getZ());
        for (Level lvl : plan.levels) {
            for (Room room : lvl.rooms) {
                String kind = room.boss ? "boss" : room.index == lvl.exitRoom ? "exit" : "room";
                ContentLog.LOGGER.debug("dungeon room site={} level={} kind={} box={},{},{},{},{},{}", origin, lvl.index, kind, room.x0, room.y0, room.z0, room.x1, room.y1, room.z1);
            }
        }
    }
}
