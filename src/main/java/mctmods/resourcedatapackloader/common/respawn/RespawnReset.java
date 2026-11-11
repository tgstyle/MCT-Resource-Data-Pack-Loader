package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.util.loot.StructureLoot;
import mctmods.resourcedatapackloader.common.util.world.TemplateFilters;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentCityBlocks;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentMapCells;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

public final class RespawnReset {
    private static final Set<String> MISSING = ConcurrentHashMap.newKeySet();
    private static final int FLAGS = Block.UPDATE_CLIENTS;
    private static final int FULL = 100;
    private static final int MOB_REACH = 2;

    private RespawnReset() {}

    public static void reset(ServerLevel level, RespawnInstance instance, RespawnDef def) {
        if (def.rebuilds()) { rebuild(level, instance, def.rebuild()); }
        if (def.loot() != RespawnDef.Loot.NONE) { restock(level, instance, def.loot()); }
        if (def.spawners()) { rearm(level, instance); }
        if (def.mobs() && (instance.kind == RespawnInstance.IMPRINT || instance.kind == RespawnInstance.CAVE)) { repopulate(level, instance, def.mobCap()); }
    }

    private static boolean unloaded(ServerLevel level, RespawnInstance instance) {
        BoundingBox area = instance.area();
        for (int chunkX = area.minX() >> 4; chunkX <= area.maxX() >> 4; chunkX++) {
            for (int chunkZ = area.minZ() >> 4; chunkZ <= area.maxZ() >> 4; chunkZ++) {
                if (!level.hasChunk(chunkX, chunkZ)) { return true; }
            }
        }
        return false;
    }

    private static void rebuild(ServerLevel level, RespawnInstance instance, RespawnDef.Rebuild mode) {
        if (instance.kind == RespawnInstance.MAP || instance.tpl.isEmpty()) {
            rebuildMap(level, instance, mode);
            return;
        }
        if (unloaded(level, instance)) { return; }
        StructureTemplate template = template(level, instance);
        if (template == null) { return; }
        StructurePlaceSettings settings = settings(instance).setIgnoreEntities(true);
        if (instance.kind == RespawnInstance.PLOT) {
            VillageDef village = ContentVillages.byKey(instance.def);
            boolean ruled = village != null && ContentVillages.vanillaHouse(village);
            if (ruled) { settings.addProcessor(JigsawReplacementProcessor.INSTANCE); }
            settings.addProcessor(new ContentCityBlocks(instance.integrity, ruled));
        }
        else if (instance.integrity < FULL) { settings.addProcessor(new BlockRotProcessor(instance.integrity / (float) FULL)); }
        if (mode == RespawnDef.Rebuild.BROKEN) { settings.addProcessor(TemplateFilters.broken()); }
        template.placeInWorld(level, instance.pos, instance.pos, settings, RandomSource.create(instance.seed), FLAGS);
    }

    private static void rebuildMap(ServerLevel level, RespawnInstance instance, RespawnDef.Rebuild mode) {
        StructureMapDef map = mapOf(instance);
        if (map == null) { return; }
        BoundingBox area = instance.area();
        ContentMapCells.Reach reach = (cornerX, cornerZ) -> cornerX + map.cell() - 1 >= area.minX() && cornerX <= area.maxX() && cornerZ + map.cell() - 1 >= area.minZ() && cornerZ <= area.maxZ();
        ContentMapCells.each(map, level.getStructureManager(), instance.seed, instance.rotation, instance.pos.getX(), instance.pos.getY(), instance.pos.getZ(), reach, cell -> {
            StructurePlaceSettings settings = ContentMapCells.settings(map, instance.rotation, cell.seed()).setBoundingBox(area).setIgnoreEntities(true);
            if (mode == RespawnDef.Rebuild.BROKEN) { settings.addProcessor(TemplateFilters.broken()); }
            cell.template().placeInWorld(level, cell.at(), cell.at(), settings, RandomSource.create(cell.seed()), FLAGS);
        });
    }

    @Nullable private static StructureMapDef mapOf(RespawnInstance instance) {
        if (instance.kind == RespawnInstance.MAP) {
            ResourceLocation key = ResourceLocation.tryParse(instance.def);
            return key == null ? null : ContentStructureMaps.def(key);
        }
        VillageDef village = ContentVillages.byKey(instance.def);
        ResourceLocation key = village == null ? null : ResourceLocation.tryParse(village.structure());
        return key == null ? null : ContentStructureMaps.def(key);
    }

    private static StructurePlaceSettings settings(RespawnInstance instance) { return new StructurePlaceSettings().setRotation(instance.rotation).setMirror(instance.mirror).setBoundingBox(instance.area()).setRandom(RandomSource.create(instance.seed)); }

    @Nullable private static StructureTemplate template(ServerLevel level, RespawnInstance instance) {
        ResourceLocation named = ResourceLocation.tryParse(instance.tpl);
        Optional<StructureTemplate> found = named == null ? Optional.empty() : level.getStructureManager().get(named);
        if (found.isEmpty()) { ContentLog.LOGGER.errorOnce(MISSING, instance.tpl, "Respawning structure {} needs template {}, which could not be loaded, so it is not rebuilt", instance.id, instance.tpl); }
        return found.orElse(null);
    }

    private static void restock(ServerLevel level, RespawnInstance instance, RespawnDef.Loot mode) {
        RandomSource random = RandomSource.create();
        for (Map.Entry<Long, String> row : instance.loot.entrySet()) {
            BlockPos at = BlockPos.of(row.getKey());
            ResourceLocation table = ResourceLocation.tryParse(row.getValue());
            if (table == null || !level.hasChunk(at.getX() >> 4, at.getZ() >> 4) || !(level.getBlockEntity(at) instanceof RandomizableContainerBlockEntity chest)) { continue; }
            if (mode == RespawnDef.Loot.EMPTIED && chest.getLootTable() == null && !chest.isEmpty()) { continue; }
            if (mode == RespawnDef.Loot.ALL) { chest.clearContent(); }
            StructureLoot.stock(chest, table, random.nextLong());
            chest.setChanged();
        }
    }

    private static void rearm(ServerLevel level, RespawnInstance instance) {
        for (Map.Entry<Long, CompoundTag> row : instance.spawn.entrySet()) {
            BlockPos at = BlockPos.of(row.getKey());
            if (!level.hasChunk(at.getX() >> 4, at.getZ() >> 4)) { continue; }
            BlockEntity tile = level.getBlockEntity(at);
            if (!(tile instanceof SpawnerBlockEntity)) { continue; }
            CompoundTag saved = row.getValue().copy();
            saved.putInt("x", at.getX());
            saved.putInt("y", at.getY());
            saved.putInt("z", at.getZ());
            tile.loadWithComponents(saved, level.registryAccess());
            tile.setChanged();
            BlockState state = level.getBlockState(at);
            level.sendBlockUpdated(at, state, state, Block.UPDATE_ALL);
        }
    }

    private static void repopulate(ServerLevel level, RespawnInstance instance, int mobCap) {
        if (instance.tpl.isEmpty() || unloaded(level, instance)) { return; }
        StructureTemplate template = template(level, instance);
        if (template == null) { return; }
        ListTag entities = template.save(new CompoundTag()).getList("entities", Tag.TAG_COMPOUND);
        if (entities.isEmpty()) { return; }
        Set<ResourceLocation> kinds = new HashSet<>();
        for (int i = 0; i < entities.size(); i++) {
            ResourceLocation kind = ResourceLocation.tryParse(entities.getCompound(i).getCompound("nbt").getString("id"));
            if (kind != null) { kinds.add(kind); }
        }
        int cap = mobCap > 0 ? mobCap : entities.size();
        AABB around = instance.bounds().inflate(MOB_REACH);
        Set<UUID> before = new HashSet<>();
        int count = 0;
        for (Entity entity : level.getEntitiesOfClass(Entity.class, around)) {
            before.add(entity.getUUID());
            if (counts(entity, kinds)) { count++; }
        }
        if (count >= cap) { return; }
        StructurePlaceSettings settings = settings(instance).setIgnoreEntities(false).addProcessor(TemplateFilters.nothing());
        template.placeInWorld(level, instance.pos, instance.pos, settings, RandomSource.create(instance.seed), FLAGS);
        List<Entity> fresh = new ArrayList<>();
        for (Entity entity : level.getEntitiesOfClass(Entity.class, around)) {
            if (!before.contains(entity.getUUID()) && counts(entity, kinds)) { fresh.add(entity); }
        }
        for (int i = cap - count; i < fresh.size(); i++) { fresh.get(i).discard(); }
    }

    private static boolean counts(Entity entity, Set<ResourceLocation> kinds) { return entity instanceof LivingEntity && kinds.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())); }
}
