package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;
import mctmods.resourcedatapackloader.common.respawn.RespawnRecorder;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.world.TemplateFilters;
import mctmods.resourcedatapackloader.common.util.world.TileData;
import mctmods.resourcedatapackloader.common.util.worldgen.FillRules;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.DungeonSites;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Level;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan.Room;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlanner;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Chest;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords.Spawner;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentMapFill;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.Template;

public final class DungeonWriter {
    private static final Set<String> BAD_NBT = new HashSet<>();
    private static final WeakHashMap<DungeonPlan, DungeonShapes.Palette> PALETTES = new WeakHashMap<>();

    private static final ThreadLocal<Boolean> BROKEN = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Set<Long>> WRITTEN = ThreadLocal.withInitial(HashSet::new);

    private DungeonWriter() {}

    public static void rewrite(World world, DungeonSites.Site site, StructureBoundingBox window, int floor, int ceiling, boolean broken) {
        BROKEN.set(broken);
        try { build(world, site, window, floor, ceiling); }
        finally { BROKEN.set(false); WRITTEN.get().clear(); }
    }

    public static void write(World world, DungeonSites.Site site, StructureBoundingBox window, int floor, int ceiling) {
        DungeonPlan plan = build(world, site, window, floor, ceiling);
        if (site.def.respawn == null) { return; }
        StructureBoundingBox area = new StructureBoundingBox(window.minX, Math.max(window.minY, plan.minY), window.minZ, window.maxX, Math.min(window.maxY, plan.maxY), window.maxZ);
        RespawnInstance fresh = new RespawnInstance(RespawnInstance.DUNGEON, site.def.key.toString(), "", new BlockPos(window.minX, window.minY, window.minZ), Rotation.NONE, Mirror.NONE, RespawnInstance.WHOLE, new BlockPos(site.originX, site.surfaceY, site.originZ).toLong(), area);
        RespawnRecorder.record(world, site.def.respawn, fresh, window);
    }

    static boolean set(World world, BlockPos at, IBlockState state) {
        if (BROKEN.get()) {
            IBlockState there = world.getBlockState(at);
            if (there == state || !there.getBlock().isReplaceable(world, at) && !WRITTEN.get().contains(at.toLong())) { return false; }
            WRITTEN.get().add(at.toLong());
        }
        world.setBlockState(at, state, BlockFlags.UPDATE_CLIENTS);
        return true;
    }

    static boolean broken() { return BROKEN.get(); }

    @Nullable static ITemplateProcessor gate() { return BROKEN.get() ? TemplateFilters.broken() : null; }

    private static DungeonPlan build(World world, DungeonSites.Site site, StructureBoundingBox window, int floor, int ceiling) {
        DungeonPlan plan = DungeonPlanner.get(world, site.def, site);
        DungeonShapes.Palette palette = paletteOf(plan);
        FillRules rules = new FillRules(site.def.key, Collections.emptyList(), null);
        if (ContentLog.LOGGER.debugEnabled() && inside(window, plan.site.originX, plan.site.originZ)) { log(world, plan); }
        int low = Math.max(floor, plan.minY);
        int high = Math.min(ceiling, plan.maxY);
        DungeonTemplates.write(world, plan, site, window, low, high);
        for (int chunkX = Coords.blockToCube(window.minX); chunkX <= Coords.blockToCube(window.maxX); chunkX++) {
            for (int chunkZ = Coords.blockToCube(window.minZ); chunkZ <= Coords.blockToCube(window.maxZ); chunkZ++) {
                DungeonPlan.Bucket bucket = plan.bucket(chunkX, chunkZ);
                if (bucket.isEmpty()) { continue; }
                StructureBoundingBox columns = new StructureBoundingBox(Math.max(window.minX, Coords.cubeToMinBlock(chunkX)), low, Math.max(window.minZ, Coords.cubeToMinBlock(chunkZ)), Math.min(window.maxX, Coords.cubeToMaxBlock(chunkX)), high, Math.min(window.maxZ, Coords.cubeToMaxBlock(chunkZ)));
                shape(world, plan, bucket, palette, columns);
                DungeonShafts.write(world, plan, bucket, palette, columns);
                DungeonEntrance.write(world, plan, bucket, palette, columns);
                DungeonSupport.write(world, plan, bucket, palette, columns, site.def.supportDepth, window.minY);
                DungeonDecor.write(world, plan, site, bucket, columns);
                DungeonDecay.write(world, plan, site.def, bucket, palette, columns, window);
                for (Spawner spawner : bucket.spawners) {
                    if (inside(window, spawner.x, spawner.z) && spawner.y >= floor && spawner.y <= ceiling) { spawner(world, rules, spawner); }
                }
                for (Chest chest : bucket.chests) {
                    if (inside(window, chest.x, chest.z) && chest.y >= floor && chest.y <= ceiling) { chest(world, plan, rules, chest); }
                }
            }
        }
        return plan;
    }

    private static synchronized DungeonShapes.Palette paletteOf(DungeonPlan plan) { return PALETTES.computeIfAbsent(plan, DungeonShapes.Palette::new); }

    private static boolean inside(StructureBoundingBox window, int x, int z) { return x >= window.minX && x <= window.maxX && z >= window.minZ && z <= window.maxZ; }

    private static void shape(World world, DungeonPlan plan, DungeonPlan.Bucket bucket, DungeonShapes.Palette palette, StructureBoundingBox columns) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = columns.minX; x <= columns.maxX; x++) {
            for (int z = columns.minZ; z <= columns.maxZ; z++) {
                for (int y = columns.minY; y <= columns.maxY; y++) {
                    IBlockState state = DungeonShapes.at(plan, bucket, x, y, z, palette);
                    if (state != null) { set(world, pos.setPos(x, y, z), state); }
                }
            }
        }
    }

    private static void spawner(World world, FillRules rules, Spawner spawner) {
        BlockPos at = new BlockPos(spawner.x, spawner.y, spawner.z);
        IBlockState state = Blocks.MOB_SPAWNER.getDefaultState();
        if (!set(world, at, state)) { return; }
        NBTTagCompound tag = spawner(rules, spawner.seed, at, new Template.BlockInfo(at, state, null), spawner.mobId).tileentityData;
        if (tag == null) { return; }
        load(world, at, tag);
    }

    static Template.BlockInfo spawner(FillRules rules, long seed, BlockPos at, Template.BlockInfo info, String mob) {
        int brace = mob.indexOf('{');
        Template.BlockInfo filled = ContentMapFill.fill(rules, seed, at, info, brace < 0 ? mob : mob.substring(0, brace), null);
        if (filled != info && brace >= 0 && filled.tileentityData != null) { merge(filled.tileentityData, mob.substring(brace), mob); }
        return filled;
    }

    static void merge(NBTTagCompound tag, String written, String whole) {
        NBTTagCompound extra = ContentStates.parseTag(written, BAD_NBT, whole, "Dungeon spawner nbt '{}' could not be parsed, so its mob spawns without it: {}", whole);
        if (extra == null) { return; }
        NBTTagCompound entity = tag.getCompoundTag(ContentMapFill.SPAWN_DATA);
        String named = entity.getString(ContentMapFill.ID);
        entity.merge(extra);
        entity.setString(ContentMapFill.ID, named);
        tag.setTag(ContentMapFill.SPAWN_DATA, entity);
    }

    private static void chest(World world, DungeonPlan plan, FillRules rules, Chest chest) {
        BlockPos at = new BlockPos(chest.x, chest.y, chest.z);
        IBlockState state = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing(plan, chest));
        if (!set(world, at, state)) { return; }
        Template.BlockInfo filled = ContentMapFill.fill(rules, chest.seed, at, new Template.BlockInfo(at, state, null), null, new ResourceLocation(chest.lootTable));
        if (filled.tileentityData != null) { load(world, at, filled.tileentityData); }
    }

    private static EnumFacing facing(DungeonPlan plan, Chest chest) {
        for (Level level : plan.levels) {
            for (Room room : level.rooms) {
                if (chest.y < room.y0 || chest.y > room.y1 || !room.contains(chest.x, chest.z)) { continue; }
                return room.away(chest.x, chest.z);
            }
        }
        return EnumFacing.NORTH;
    }

    private static void load(World world, BlockPos at, NBTTagCompound tag) {
        TileEntity entity = world.getTileEntity(at);
        if (entity == null) { return; }
        entity.readFromNBT(TileData.at(tag, at));
        entity.markDirty();
    }

    private static void log(World world, DungeonPlan plan) {
        int dim = world.provider.getDimension();
        String origin = plan.site.originX + "," + plan.site.surfaceY + "," + plan.site.originZ;
        BlockPos stand = DungeonEntrance.standingCell(plan);
        ContentLog.LOGGER.debug("dungeon site={} dim={} size={},{},{} levels={} entrance={},{},{}", origin, dim, plan.maxX - plan.minX + 1, plan.maxY - plan.minY + 1, plan.maxZ - plan.minZ + 1, plan.levels.size(), stand.getX(), stand.getY(), stand.getZ());
        for (Level level : plan.levels) {
            for (Room room : level.rooms) {
                String kind = room.boss ? "boss" : room.index == level.exitRoom ? "exit" : "room";
                ContentLog.LOGGER.debug("dungeon room site={} level={} kind={} box={},{},{},{},{},{}", origin, level.index, kind, room.x0, room.y0, room.z0, room.x1, room.y1, room.z1);
            }
        }
    }
}
