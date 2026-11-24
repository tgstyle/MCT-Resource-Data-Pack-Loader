package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.util.loot.StructureLoot;
import mctmods.resourcedatapackloader.common.util.world.TemplateBlocks;
import mctmods.resourcedatapackloader.common.util.world.TemplateFilters;
import mctmods.resourcedatapackloader.common.util.world.TileData;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.DungeonSites;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write.DungeonWriter;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.BlockRotationProcessor;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

public final class RespawnReset {
    private static final Set<String> MISSING = new HashSet<>();
    private static final int FLAGS = 2;
    private static final int MOB_REACH = 2;

    private RespawnReset() {}

    public static void reset(WorldServer world, RespawnInstance instance, RespawnDef def) {
        if (def.rebuilds()) { rebuild(world, instance, def.rebuild); }
        if (def.loot != RespawnDef.Loot.NONE) { restock(world, instance, def.loot); }
        if (def.spawners) { rearm(world, instance); }
        if (def.mobs && (instance.kind == RespawnInstance.IMPRINT || instance.kind == RespawnInstance.CAVE)) { repopulate(world, instance, def.mobCap); }
    }

    private static void rebuild(WorldServer world, RespawnInstance instance, RespawnDef.Rebuild mode) {
        ITemplateProcessor filter = mode == RespawnDef.Rebuild.BROKEN ? TemplateFilters.broken() : null;
        StructureBoundingBox area = instance.area();
        if (instance.kind == RespawnInstance.DUNGEON) {
            DungeonSites.Site site = siteOf(world, instance);
            if (site != null) { DungeonWriter.rewrite(world, site, area, area.minY, area.maxY, mode == RespawnDef.Rebuild.BROKEN); }
            return;
        }
        if (instance.kind == RespawnInstance.MAP || instance.tpl.isEmpty()) {
            StructureMapDef map = mapOf(instance);
            if (map != null) { ContentStructureMaps.cells(world, map, instance.seed, instance.rotation, instance.pos.getX(), instance.pos.getY(), instance.pos.getZ(), area, filter); }
            return;
        }
        Template template = template(world, instance);
        if (template == null) { return; }
        PlacementSettings settings = new PlacementSettings().setRotation(instance.rotation).setMirror(instance.mirror).setIntegrity(instance.integrity / 100.0F).setBoundingBox(area).setIgnoreEntities(true);
        if (instance.kind == RespawnInstance.PLOT) { settings.setSeed(instance.seed); }
        else { settings.setRandom(new Random(instance.seed)); }
        template.addBlocksToWorld(world, instance.pos, TemplateFilters.chain(new BlockRotationProcessor(instance.pos, settings), filter), settings, FLAGS);
    }

    @Nullable private static DungeonSites.Site siteOf(WorldServer world, RespawnInstance instance) {
        BlockPos origin = BlockPos.fromLong(instance.seed);
        for (DungeonSites.Site site : DungeonSites.sitesNear(world, origin, 0)) {
            if (site.def.key.toString().equals(instance.def) && site.originX == origin.getX() && site.originZ == origin.getZ()) { return site; }
        }
        return null;
    }

    @Nullable private static StructureMapDef mapOf(RespawnInstance instance) {
        if (instance.kind == RespawnInstance.MAP) { return ContentStructureMaps.byName(instance.def); }
        VillageDef village = ContentVillages.byName(instance.def);
        return village == null ? null : ContentStructureMaps.byName(village.structure);
    }

    @Nullable private static Template template(WorldServer world, RespawnInstance instance) {
        Template template = world.getStructureTemplateManager().get(world.getMinecraftServer(), new ResourceLocation(instance.tpl));
        if (template == null) { ContentLog.LOGGER.errorOnce(MISSING, instance.tpl, "Respawning structure {} needs template {}, which could not be loaded, so it is not rebuilt", instance.id, instance.tpl); }
        return template;
    }

    private static void restock(WorldServer world, RespawnInstance instance, RespawnDef.Loot mode) {
        Random random = new Random();
        for (Map.Entry<Long, String> row : instance.loot.entrySet()) {
            TileEntity tile = world.getTileEntity(BlockPos.fromLong(row.getKey()));
            if (!(tile instanceof TileEntityLockableLoot)) { continue; }
            TileEntityLockableLoot chest = (TileEntityLockableLoot) tile;
            if (mode == RespawnDef.Loot.EMPTIED && !chest.isEmpty()) { continue; }
            if (mode == RespawnDef.Loot.ALL) { chest.clear(); }
            StructureLoot.stock(chest, row.getValue(), random);
            chest.markDirty();
        }
    }

    private static void rearm(WorldServer world, RespawnInstance instance) {
        for (Map.Entry<Long, NBTTagCompound> row : instance.spawn.entrySet()) {
            BlockPos at = BlockPos.fromLong(row.getKey());
            TileEntity tile = world.getTileEntity(at);
            if (!(tile instanceof TileEntityMobSpawner)) { continue; }
            tile.readFromNBT(TileData.at(row.getValue(), at));
            tile.markDirty();
            IBlockState state = world.getBlockState(at);
            world.notifyBlockUpdate(at, state, state, 3);
        }
    }

    private static void repopulate(WorldServer world, RespawnInstance instance, int mobCap) {
        if (instance.tpl.isEmpty()) { return; }
        Template template = template(world, instance);
        if (template == null) { return; }
        List<NBTTagCompound> entities = TemplateBlocks.entities(template);
        if (entities.isEmpty()) { return; }
        Set<ResourceLocation> kinds = new HashSet<>();
        for (NBTTagCompound entity : entities) { kinds.add(new ResourceLocation(entity.getString("id"))); }
        int cap = mobCap > 0 ? mobCap : entities.size();
        AxisAlignedBB around = instance.bounds().grow(MOB_REACH);
        Set<UUID> before = new HashSet<>();
        int count = 0;
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, around)) {
            before.add(entity.getUniqueID());
            if (counts(entity, kinds)) { count++; }
        }
        if (count >= cap) { return; }
        PlacementSettings settings = new PlacementSettings().setRotation(instance.rotation).setMirror(instance.mirror).setBoundingBox(instance.area()).setIgnoreEntities(false);
        template.addBlocksToWorld(world, instance.pos, (placed, at, info) -> null, settings, FLAGS);
        List<Entity> fresh = new ArrayList<>();
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, around)) {
            if (!before.contains(entity.getUniqueID()) && counts(entity, kinds)) { fresh.add(entity); }
        }
        for (int i = cap - count; i < fresh.size(); i++) { fresh.get(i).setDead(); }
    }

    private static boolean counts(Entity entity, Set<ResourceLocation> kinds) { return entity instanceof EntityLivingBase && kinds.contains(EntityList.getKey(entity)); }
}
