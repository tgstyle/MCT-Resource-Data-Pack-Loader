package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.rubic.worldgen.generator.DeepGeneration;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class CaveRegionDef {
    public static final int NO_WATER = Integer.MIN_VALUE;
    public final ResourceLocation key;
    public final int weight;
    public final int minHeight;
    public final int maxHeight;
    public final List<Integer> dimensions;
    public final String floorCover;
    public final float floorChance;
    public final String ceilingCover;
    public final float ceilingChance;
    public final List<String> coverReplace;
    public final int waterLevel;
    public final List<SpawnEntryDef> spawns;
    public final boolean keepDefaultSpawns;
    public final List<PickDef> structures;
    public final float structureChance;
    public final String structureLoot;
    public final String biome;
    public final String skyStone;
    public final float skyIslands;
    public final float skyThickness;
    private boolean resolved;
    private boolean skyResolved;
    @Nullable private IBlockState skyState;
    @Nullable private IBlockState floorState;
    @Nullable private IBlockState ceilingState;
    @Nullable private Set<Block> replaceBlocks;

    @Nullable private Map<EnumCreatureType, List<Biome.SpawnListEntry>> spawnLists;

    public CaveRegionDef(ResourceLocation key, int weight, int minHeight, int maxHeight, List<Integer> dimensions,
                         String floorCover, float floorChance, String ceilingCover, float ceilingChance,
                         List<String> coverReplace, int waterLevel, List<SpawnEntryDef> spawns, boolean keepDefaultSpawns,
                         List<PickDef> structures, float structureChance, String structureLoot, String biome,
                         String skyStone, float skyIslands, float skyThickness) {
        this.key = key;
        this.weight = weight;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.dimensions = dimensions;
        this.floorCover = floorCover;
        this.floorChance = floorChance;
        this.ceilingCover = ceilingCover;
        this.ceilingChance = ceilingChance;
        this.coverReplace = coverReplace;
        this.waterLevel = waterLevel;
        this.spawns = spawns;
        this.keepDefaultSpawns = keepDefaultSpawns;
        this.structures = structures;
        this.structureChance = structureChance;
        this.structureLoot = structureLoot;
        this.biome = biome;
        this.skyStone = skyStone;
        this.skyIslands = skyIslands;
        this.skyThickness = skyThickness;
    }

    public boolean shapesSky() { return !skyStone.isEmpty() || !Float.isNaN(skyIslands) || !Float.isNaN(skyThickness) || hasBiome(); }

    @Nullable public IBlockState skyState() {
        if (!skyResolved) {
            skyResolved = true;
            skyState = DeepGeneration.parseState(skyStone, key + " skyStone");
        }
        return skyState;
    }

    public boolean hasWater() { return waterLevel != NO_WATER; }

    public boolean hasBiome() { return !biome.isEmpty(); }

    public boolean hasSpawns() { return !spawns.isEmpty(); }

    public boolean hasStructures() { return !structures.isEmpty(); }

    public List<Biome.SpawnListEntry> spawnsFor(EnumCreatureType type) {
        if (spawnLists == null) {
            Map<EnumCreatureType, List<Biome.SpawnListEntry>> made = new EnumMap<>(EnumCreatureType.class);
            for (SpawnEntryDef entry : spawns) {
                EnumCreatureType wanted = SpawnEntryDef.creatureType(entry.creatureType);
                if (wanted == null) {
                    ContentLog.LOGGER.error("Spawn entry in cave region {} has creature type '{}', which is not one of monster, creature, ambient or water", key, entry.creatureType);
                    continue;
                }
                Class<? extends EntityLiving> living = SpawnEntryDef.living("Cave region", key, entry.entity);
                if (living == null) { continue; }
                made.computeIfAbsent(wanted, type1 -> new ArrayList<>()).add(new Biome.SpawnListEntry(living, entry.weight, entry.min, entry.max));
            }
            spawnLists = made;
        }
        return spawnLists.getOrDefault(type, Collections.emptyList());
    }

    public boolean hasCovers() {
        resolve();
        return floorState != null || ceilingState != null;
    }

    public boolean inDimension(int dimension) { return dimensions.isEmpty() || dimensions.contains(dimension); }

    @Nullable public IBlockState floorState() {
        resolve();
        return floorState;
    }

    @Nullable public IBlockState ceilingState() {
        resolve();
        return ceilingState;
    }

    public boolean rejectsCover(IBlockState state) {
        resolve();
        if (replaceBlocks != null) { return !replaceBlocks.contains(state.getBlock()); }
        return state.getMaterial() != Material.ROCK;
    }

    private void resolve() {
        if (resolved) { return; }
        resolved = true;
        floorState = DeepGeneration.parseState(floorCover, key + " floorCover");
        ceilingState = DeepGeneration.parseState(ceilingCover, key + " ceilingCover");
        if (coverReplace.isEmpty()) { return; }
        Set<Block> found = new HashSet<>();
        for (String name : coverReplace) {
            int at = name.indexOf('@');
            Block block = Block.getBlockFromName(at >= 0 ? name.substring(0, at) : name);
            if (block != null) { found.add(block); }
        }
        replaceBlocks = found;
    }
}
