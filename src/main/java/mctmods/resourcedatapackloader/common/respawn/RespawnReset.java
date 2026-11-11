package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
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
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

public final class RespawnReset {
    private static final Set<String> MISSING = new HashSet<>();
    private static final int FLAGS = 2;
    private static final int MOB_REACH = 2;
    private static final int FULL = 100;

    private RespawnReset() {}

    public static void reset(ServerLevel level, RespawnInstance instance, RespawnDef def) {
        if (def.rebuilds()) { rebuild(level, instance, def.rebuild()); }
        if (def.loot() != RespawnDef.Loot.NONE) { restock(level, instance, def.loot()); }
        if (def.spawners()) { rearm(level, instance); }
        if (def.mobs() && (instance.kind == RespawnInstance.IMPRINT || instance.kind == RespawnInstance.CAVE)) { repopulate(level, instance, def.mobCap()); }
    }

    private static void rebuild(ServerLevel level, RespawnInstance instance, RespawnDef.Rebuild mode) {
        if (instance.kind == RespawnInstance.MAP || instance.tpl.isEmpty()) {
            rebuildMap(level, instance, mode);
            return;
        }
        StructureProcessor filter = mode == RespawnDef.Rebuild.BROKEN ? TemplateFilters.broken() : null;
        StructureTemplate template = template(level, instance);
        if (template == null) { return; }
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(instance.rotation).setMirror(instance.mirror).setBoundingBox(instance.area()).setIgnoreEntities(true).setRandom(RandomSource.create(instance.seed));
        if (instance.kind == RespawnInstance.PLOT) {
            VillageDef village = ContentVillages.byKey(instance.def);
            boolean ruled = village != null && ContentVillages.vanillaHouse(village);
            if (ruled) { settings.addProcessor(JigsawReplacementProcessor.INSTANCE); }
            settings.addProcessor(new ContentCityBlocks(instance.integrity, ruled));
        }
        else if (instance.integrity < FULL) { settings.addProcessor(new BlockRotProcessor(instance.integrity / (float) FULL)); }
        if (filter != null) { settings.addProcessor(filter); }
        template.placeInWorld(level, instance.pos, instance.pos, settings, RandomSource.create(instance.seed), FLAGS);
    }

    private static void rebuildMap(ServerLevel level, RespawnInstance instance, RespawnDef.Rebuild mode) {
        StructureMapDef map = mapOf(instance);
        if (map == null) { return; }
        ContentMapCells.rebuild(level, map, instance.seed, instance.rotation, instance.pos, instance.area(), mode == RespawnDef.Rebuild.BROKEN ? TemplateFilters.broken() : null);
    }

    @Nullable private static StructureMapDef mapOf(RespawnInstance instance) {
        String named = instance.def;
        if (instance.kind == RespawnInstance.PLOT) {
            VillageDef village = ContentVillages.byKey(instance.def);
            named = village == null ? "" : village.structure();
        }
        ResourceLocation key = ResourceLocation.tryParse(named);
        return key == null ? null : ContentStructureMaps.def(key);
    }

    @Nullable private static StructureTemplate template(ServerLevel level, RespawnInstance instance) {
        ResourceLocation named = ResourceLocation.tryParse(instance.tpl);
        Optional<StructureTemplate> found = named == null ? Optional.empty() : level.getStructureManager().get(named);
        if (found.isEmpty()) { ContentLog.LOGGER.errorOnce(MISSING, instance.tpl, "Respawning structure {} needs template {}, which could not be loaded, so it is not rebuilt", instance.id, instance.tpl); }
        return found.orElse(null);
    }

    private static void restock(ServerLevel level, RespawnInstance instance, RespawnDef.Loot mode) {
        RandomSource random = RandomSource.create();
        for (Map.Entry<Long, String> row : instance.loot.entrySet()) {
            ResourceLocation table = ResourceLocation.tryParse(row.getValue());
            if (table == null || !(level.getBlockEntity(BlockPos.of(row.getKey())) instanceof RandomizableContainerBlockEntity chest)) { continue; }
            if (mode == RespawnDef.Loot.EMPTIED && !empty(chest)) { continue; }
            if (mode == RespawnDef.Loot.ALL) { chest.clearContent(); }
            chest.setLootTable(table, random.nextLong());
            chest.setChanged();
        }
    }

    private static boolean empty(RandomizableContainerBlockEntity chest) {
        CompoundTag saved = chest.saveWithoutMetadata();
        return saved.contains("LootTable", Tag.TAG_STRING) || saved.getList("Items", Tag.TAG_COMPOUND).isEmpty();
    }

    private static void rearm(ServerLevel level, RespawnInstance instance) {
        for (Map.Entry<Long, CompoundTag> row : instance.spawn.entrySet()) {
            BlockPos at = BlockPos.of(row.getKey());
            BlockEntity found = level.getBlockEntity(at);
            if (!(found instanceof SpawnerBlockEntity)) { continue; }
            CompoundTag saved = row.getValue().copy();
            saved.putInt("x", at.getX());
            saved.putInt("y", at.getY());
            saved.putInt("z", at.getZ());
            found.load(saved);
            found.setChanged();
            BlockState state = level.getBlockState(at);
            level.sendBlockUpdated(at, state, state, 3);
        }
    }

    private static void repopulate(ServerLevel level, RespawnInstance instance, int mobCap) {
        if (instance.tpl.isEmpty()) { return; }
        StructureTemplate template = template(level, instance);
        if (template == null) { return; }
        ListTag entities = template.save(new CompoundTag()).getList("entities", Tag.TAG_COMPOUND);
        if (entities.isEmpty()) { return; }
        Set<ResourceLocation> kinds = new HashSet<>();
        for (int i = 0; i < entities.size(); i++) { kinds.add(ResourceLocation.tryParse(entities.getCompound(i).getCompound("nbt").getString("id"))); }
        int cap = mobCap > 0 ? mobCap : entities.size();
        AABB around = instance.bounds().inflate(MOB_REACH);
        Set<UUID> before = new HashSet<>();
        int count = 0;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, around)) {
            before.add(entity.getUUID());
            if (counts(entity, kinds)) { count++; }
        }
        if (count >= cap) { return; }
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(instance.rotation).setMirror(instance.mirror).setBoundingBox(instance.area()).setIgnoreEntities(false).addProcessor(TemplateFilters.none());
        template.placeInWorld(level, instance.pos, instance.pos, settings, RandomSource.create(), FLAGS);
        List<Entity> fresh = new ArrayList<>();
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, around)) {
            if (!before.contains(entity.getUUID()) && counts(entity, kinds)) { fresh.add(entity); }
        }
        for (int i = cap - count; i < fresh.size(); i++) { fresh.get(i).discard(); }
    }

    private static boolean counts(Entity entity, Set<ResourceLocation> kinds) { return kinds.contains(EntityType.getKey(entity.getType())); }
}
