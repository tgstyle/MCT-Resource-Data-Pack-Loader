package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write;

import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;
import mctmods.resourcedatapackloader.common.respawn.RespawnRecorder;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.util.world.TemplateFilters;
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
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
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
    private static final int FLAGS = BlockFlags.UPDATE_CLIENTS;
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
        RespawnInstance fresh = new RespawnInstance(RespawnInstance.DUNGEON, site.def.key.toString(), "", new BlockPos(window.minX, window.minY, window.minZ), Rotation.NONE, Mirror.NONE, 100, new BlockPos(site.originX, site.surfaceY, site.originZ).toLong(), area);
        RespawnRecorder.record(world, site.def.respawn, fresh, window);
    }

    static boolean set(World world, BlockPos at, IBlockState state) {
        if (BROKEN.get()) {
            IBlockState there = world.getBlockState(at);
            if (there == state || !there.getBlock().isReplaceable(world, at) && !WRITTEN.get().contains(at.toLong())) { return false; }
            WRITTEN.get().add(at.toLong());
        }
        world.setBlockState(at, state, FLAGS);
        return true;
    }

    static boolean broken() { return BROKEN.get(); }

    static ITemplateProcessor gated(ITemplateProcessor first) { return TemplateFilters.chain(first, BROKEN.get() ? TemplateFilters.broken() : null); }

    private static DungeonPlan build(World world, DungeonSites.Site site, StructureBoundingBox window, int floor, int ceiling) {
        DungeonPlan plan = DungeonPlanner.get(world, site.def, site);
        DungeonShapes.Palette palette = paletteOf(plan);
        FillRules rules = new FillRules(site.def.key, Collections.emptyList(), null);
        if (ContentLog.LOGGER.debugEnabled() && inside(window, plan.site.originX, plan.site.originZ)) { log(world, plan); }
        int low = Math.max(floor, plan.minY);
        int high = Math.min(ceiling, plan.maxY);
        DungeonTemplates.write(world, plan, site, window, low, high);
        for (int chunkX = window.minX >> 4; chunkX <= window.maxX >> 4; chunkX++) {
            for (int chunkZ = window.minZ >> 4; chunkZ <= window.maxZ >> 4; chunkZ++) {
                DungeonPlan.Bucket bucket = plan.bucket(chunkX, chunkZ);
                if (bucket.isEmpty()) { continue; }
                StructureBoundingBox columns = new StructureBoundingBox(Math.max(window.minX, chunkX << 4), low, Math.max(window.minZ, chunkZ << 4), Math.min(window.maxX, (chunkX << 4) + 15), high, Math.min(window.maxZ, (chunkZ << 4) + 15));
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
        int brace = spawner.mobId.indexOf('{');
        String id = brace < 0 ? spawner.mobId : spawner.mobId.substring(0, brace);
        IBlockState state = Blocks.MOB_SPAWNER.getDefaultState();
        if (!set(world, at, state)) { return; }
        Template.BlockInfo filled = ContentMapFill.fill(rules, spawner.seed, at, new Template.BlockInfo(at, state, null), id, null);
        NBTTagCompound tag = filled.tileentityData;
        if (tag == null) { return; }
        if (brace >= 0) { merge(tag, spawner.mobId.substring(brace), spawner.mobId); }
        load(world, at, tag);
    }

    static void merge(NBTTagCompound tag, String written, String whole) {
        try {
            NBTTagCompound extra = JsonToNBT.getTagFromJson(written);
            NBTTagCompound entity = tag.getCompoundTag("SpawnData");
            String named = entity.getString("id");
            entity.merge(extra);
            entity.setString("id", named);
            tag.setTag("SpawnData", entity);
        }
        catch (NBTException bad) { ContentLog.LOGGER.errorOnce(BAD_NBT, whole, "Dungeon spawner nbt '{}' could not be parsed, so its mob spawns without it: {}", whole, bad.getMessage()); }
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
                if (chest.x == room.x0) { return EnumFacing.EAST; }
                if (chest.x == room.x1) { return EnumFacing.WEST; }
                if (chest.z == room.z0) { return EnumFacing.SOUTH; }
                if (chest.z == room.z1) { return EnumFacing.NORTH; }
                return EnumFacing.NORTH;
            }
        }
        return EnumFacing.NORTH;
    }

    private static void load(World world, BlockPos at, NBTTagCompound tag) {
        TileEntity entity = world.getTileEntity(at);
        if (entity == null) { return; }
        tag.setInteger("x", at.getX());
        tag.setInteger("y", at.getY());
        tag.setInteger("z", at.getZ());
        entity.readFromNBT(tag);
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
