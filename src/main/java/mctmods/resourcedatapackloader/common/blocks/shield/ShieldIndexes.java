package mctmods.resourcedatapackloader.common.blocks.shield;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SpawnShieldDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public final class ShieldIndexes {
    private static final Map<Block, BlockShield> TABLE = new HashMap<>();
    private static final Predicate<BlockState> LISTED = state -> TABLE.containsKey(state.getBlock());
    private static int maxSpawnRadius;
    private static int maxWeatherRadius;
    private static boolean active;

    private ShieldIndexes() {}

    public static void resolve() {
        TABLE.clear();
        maxSpawnRadius = 0;
        maxWeatherRadius = 0;
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            BlockDef def = entry.def();
            if (def.blocksSpawns() == null && def.blocksWeather() == null) { continue; }
            int spawnRadius = 0;
            SpawnShieldDef spawns = def.blocksSpawns();
            Set<ResourceLocation> entities = new HashSet<>();
            List<TagKey<EntityType<?>>> tags = new ArrayList<>();
            if (spawns != null) {
                for (ResourceLocation id : spawns.entities()) {
                    if (BuiltInRegistries.ENTITY_TYPE.containsKey(id)) { entities.add(id); }
                    else { ContentLog.LOGGER.error("Block {} blocksSpawns lists the unknown entity '{}', so it is skipped", entry.id(), id); }
                }
                for (ResourceLocation id : spawns.entityTags()) { tags.add(TagKey.create(Registries.ENTITY_TYPE, id)); }
                if (spawns.entities().isEmpty() && spawns.entityTags().isEmpty() || !entities.isEmpty() || !tags.isEmpty()) { spawnRadius = spawns.radius(); }
                else { ContentLog.LOGGER.error("Block {} blocksSpawns lists no known entity, so it blocks no spawns", entry.id()); }
            }
            int weatherRadius = def.blocksWeather() == null ? 0 : def.blocksWeather().radius();
            if (spawnRadius == 0 && weatherRadius == 0) { continue; }
            TABLE.put(entry.block(), new BlockShield(spawnRadius, spawns == null ? SpawnShieldDef.Mobs.ALL : spawns.mobs(), entities, tags, weatherRadius));
            maxSpawnRadius = Math.max(maxSpawnRadius, spawnRadius);
            maxWeatherRadius = Math.max(maxWeatherRadius, weatherRadius);
        }
        active = !TABLE.isEmpty();
    }

    private static int reach(int blocks) { return (blocks + 15) >> 4; }

    private static ShieldIndex of(Level level) {
        IShieldHolder holder = (IShieldHolder) level;
        ShieldIndex index = holder.rdpl$shields();
        if (index == null) {
            index = new ShieldIndex();
            holder.rdpl$setShields(index);
        }
        return index;
    }

    @Nullable private static ShieldIndex existing(LevelReader level) { return level instanceof IShieldHolder holder ? holder.rdpl$shields() : null; }

    public static void scan(Level level, LevelChunk chunk) {
        if (!active) { return; }
        ShieldIndex index = of(level);
        ChunkPos at = chunk.getPos();
        long key = at.toLong();
        index.drop(key);
        LevelChunkSection[] sections = chunk.getSections();
        for (int i = 0; i < sections.length; i++) { addSection(index, key, sections[i], at.getMinBlockX(), chunk.getSectionYFromSectionIndex(i) << 4, at.getMinBlockZ()); }
    }

    private static long column(int x, int z) { return (x & 0x3FFFFFFL) << 38 | (z & 0x3FFFFFFL); }

    private static void addSection(ShieldIndex index, long key, LevelChunkSection section, int baseX, int baseY, int baseZ) {
        if (section.hasOnlyAir() || !section.getStates().maybeHas(LISTED)) { return; }
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    BlockShield shield = TABLE.get(section.getBlockState(x, y, z).getBlock());
                    if (shield != null) { index.add(key, column(baseX + x, baseZ + z), baseY + y, shield); }
                }
            }
        }
    }

    public static void changed(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!active) { return; }
        BlockShield was = TABLE.get(oldState.getBlock());
        BlockShield now = TABLE.get(newState.getBlock());
        if (was == now) { return; }
        ShieldIndex index = of(level);
        long key = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        long column = column(pos.getX(), pos.getZ());
        if (was != null) { index.remove(key, column, pos.getY()); }
        if (now != null) { index.add(key, column, pos.getY(), now); }
    }

    public static void drop(Level level, ChunkPos at) {
        if (!active) { return; }
        ShieldIndex index = existing(level);
        if (index != null) { index.drop(at.toLong()); }
    }

    public static boolean spawnDenied(LevelReader level, Entity entity, int x, int y, int z) {
        if (!active || maxSpawnRadius == 0) { return false; }
        ShieldIndex index = existing(level);
        return index != null && index.spawnDenied(entity, x, y, z, reach(maxSpawnRadius));
    }

    public static boolean weatherShut(LevelReader level, int x, int z) {
        if (!active || maxWeatherRadius == 0) { return false; }
        ShieldIndex index = existing(level);
        return index != null && index.weatherShut(x, z, reach(maxWeatherRadius));
    }

    public static void near(LevelReader level, int x, int z, int range, ShieldIndex.Near scratch) {
        ShieldIndex index = active && maxWeatherRadius > 0 ? existing(level) : null;
        if (index == null) {
            scratch.clear();
            return;
        }
        index.near(x, z, range, maxWeatherRadius, reach(range + maxWeatherRadius), scratch);
    }
}
