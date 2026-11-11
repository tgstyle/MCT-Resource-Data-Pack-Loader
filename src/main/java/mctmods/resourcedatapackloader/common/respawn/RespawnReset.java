package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.util.loot.StructureLoot;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentImprint;
import mctmods.resourcedatapackloader.common.worldgen.structure.RespawnBrokenProcessor;
import mctmods.resourcedatapackloader.common.worldgen.structure.RespawnVoidProcessor;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentCityBlocks;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentMapCells;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentMapFillProcessor;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.AABB;

public final class RespawnReset {
    private static final Set<String> MISSING = new HashSet<>();
    private static final int MOB_REACH = 2;

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
        StructureTemplate template = template(level, instance);
        if (template == null) { return; }
        RandomSource random = RandomSource.create(instance.seed);
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(instance.rotation).setMirror(instance.mirror).setBoundingBox(instance.area()).setIgnoreEntities(true).setRandom(random);
        if (instance.kind == RespawnInstance.PLOT) {
            VillageDef village = ContentVillages.byKey(instance.def);
            boolean ruled = village != null && ContentVillages.vanillaHouse(village);
            if (ruled) { settings.addProcessor(JigsawReplacementProcessor.INSTANCE); }
            settings.addProcessor(new ContentCityBlocks(instance.integrity, ruled));
        }
        else if (instance.integrity < 100) { settings.addProcessor(new BlockRotProcessor(instance.integrity / 100.0F)); }
        if (mode == RespawnDef.Rebuild.BROKEN) { settings.addProcessor(new RespawnBrokenProcessor()); }
        template.placeInWorld(level, instance.pos, instance.pos, settings, random, Block.UPDATE_CLIENTS);
    }

    private static void rebuildMap(ServerLevel level, RespawnInstance instance, RespawnDef.Rebuild mode) {
        StructureMapDef map = mapOf(instance);
        if (map == null) { return; }
        BoundingBox area = instance.area();
        ContentMapCells.each(map, instance.seed, instance.rotation, instance.pos.getX(), instance.pos.getY(), instance.pos.getZ(), (cornerX, cornerZ) -> cornerX + map.cell() > area.minX() && cornerX <= area.maxX() && cornerZ + map.cell() > area.minZ() && cornerZ <= area.maxZ(), cell -> {
            Optional<StructureTemplate> piece = LineCompat.templates(level.getServer()).get(cell.template());
            if (piece.isEmpty()) {
                ContentStructureMaps.missing(map, cell.named());
                return false;
            }
            Vec3i span = piece.get().getSize(instance.rotation);
            BlockPos at = new BlockPos(cell.cornerX() + ContentImprint.backX(instance.rotation, span), cell.y(), cell.cornerZ() + ContentImprint.backZ(instance.rotation, span));
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(instance.rotation).setBoundingBox(area);
            if (map.fills()) { settings.addProcessor(new ContentMapFillProcessor(map, cell.seed())); }
            if (mode == RespawnDef.Rebuild.BROKEN) { settings.addProcessor(new RespawnBrokenProcessor()); }
            piece.get().placeInWorld(level, at, at, settings, RandomSource.create(cell.seed()), Block.UPDATE_CLIENTS);
            return true;
        });
    }

    @Nullable private static StructureMapDef mapOf(RespawnInstance instance) {
        if (instance.kind == RespawnInstance.MAP) {
            Identifier key = Identifier.tryParse(instance.def);
            return key == null ? null : ContentStructureMaps.def(key);
        }
        VillageDef village = ContentVillages.byKey(instance.def);
        Identifier key = village == null ? null : Identifier.tryParse(village.structure());
        return key == null ? null : ContentStructureMaps.def(key);
    }

    @Nullable private static StructureTemplate template(ServerLevel level, RespawnInstance instance) {
        Identifier named = Identifier.tryParse(instance.tpl);
        Optional<StructureTemplate> held = named == null ? Optional.empty() : LineCompat.templates(level.getServer()).get(named);
        if (held.isEmpty()) {
            ContentLog.LOGGER.errorOnce(MISSING, instance.tpl, "Respawning structure {} needs template {}, which could not be loaded, so it is not rebuilt", instance.id, instance.tpl);
            return null;
        }
        return held.get();
    }

    private static void restock(ServerLevel level, RespawnInstance instance, RespawnDef.Loot mode) {
        RandomSource random = RandomSource.create();
        for (Map.Entry<Long, String> row : instance.loot.entrySet()) {
            BlockEntity tile = level.getBlockEntity(BlockPos.of(row.getKey()));
            Identifier table = Identifier.tryParse(row.getValue());
            if (!(tile instanceof RandomizableContainer chest) || table == null) { continue; }
            if (mode == RespawnDef.Loot.EMPTIED && chest.getLootTable() == null && !chest.isEmpty()) { continue; }
            if (mode == RespawnDef.Loot.ALL) { chest.clearContent(); }
            StructureLoot.stock(tile, table, random.nextLong());
            tile.setChanged();
        }
    }

    private static void rearm(ServerLevel level, RespawnInstance instance) {
        for (Map.Entry<Long, CompoundTag> row : instance.spawn.entrySet()) {
            BlockPos at = BlockPos.of(row.getKey());
            if (!(level.getBlockEntity(at) instanceof SpawnerBlockEntity tile)) { continue; }
            CompoundTag saved = row.getValue().copy();
            saved.putInt("x", at.getX());
            saved.putInt("y", at.getY());
            saved.putInt("z", at.getZ());
            tile.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), saved));
            tile.setChanged();
            BlockState state = level.getBlockState(at);
            level.sendBlockUpdated(at, state, state, Block.UPDATE_ALL);
        }
    }

    private static void repopulate(ServerLevel level, RespawnInstance instance, int mobCap) {
        if (instance.tpl.isEmpty()) { return; }
        StructureTemplate template = template(level, instance);
        if (template == null) { return; }
        ListTag entities = template.save(new CompoundTag()).getListOrEmpty("entities");
        if (entities.isEmpty()) { return; }
        Set<Identifier> kinds = new HashSet<>();
        for (int i = 0; i < entities.size(); i++) {
            Identifier kind = Identifier.tryParse(entities.getCompoundOrEmpty(i).getCompoundOrEmpty("nbt").getStringOr("id", ""));
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
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(instance.rotation).setMirror(instance.mirror).setBoundingBox(instance.area()).setIgnoreEntities(false).addProcessor(new RespawnVoidProcessor());
        template.placeInWorld(level, instance.pos, instance.pos, settings, RandomSource.create(), Block.UPDATE_CLIENTS);
        List<Entity> fresh = new ArrayList<>();
        for (Entity entity : level.getEntitiesOfClass(Entity.class, around)) {
            if (!before.contains(entity.getUUID()) && counts(entity, kinds)) { fresh.add(entity); }
        }
        for (int i = cap - count; i < fresh.size(); i++) { fresh.get(i).discard(); }
    }

    private static boolean counts(Entity entity, Set<Identifier> kinds) { return entity instanceof LivingEntity && kinds.contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())); }
}
