package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;

public final class BiomeDef {
    public static final int AUTO_ID = -1;
    public final ResourceLocation registryName;
    public final String name;
    public final int id;
    public final float temperature;
    public final float rainfall;
    public final float baseHeight;
    public final float heightVariation;
    public final boolean snow;
    public final boolean rain;
    public final int waterColor;
    public final String baseBiome;
    public final String topBlock;
    public final String fillerBlock;
    public final String stoneBlock;
    public final List<String> types;
    public final String climate;
    public final int weight;
    public final boolean spawnBiome;
    public final boolean villageBiome;
    public final boolean villageSpawn;
    public final boolean strongholdBiome;
    public final Map<String, Integer> decoration;
    public final float spawnChance;
    public final float surfaceDayMonsterRate;
    public final float surfaceNightMonsterRate;
    public final float undergroundDayMonsterRate;
    public final float undergroundNightMonsterRate;
    public final int grassColor;
    public final int foliageColor;
    public final boolean keepDefaultSpawns;
    public final List<SpawnEntryDef> spawns;
    public final List<String> requires;

    public BiomeDef(ResourceLocation registryName, String name, int id, float temperature, float rainfall, float baseHeight, float heightVariation, boolean snow, boolean rain, int waterColor, String baseBiome, String topBlock, String fillerBlock, String stoneBlock, List<String> types, String climate, int weight, boolean spawnBiome, boolean villageBiome, boolean villageSpawn, boolean strongholdBiome, Map<String, Integer> decoration, float spawnChance, float surfaceDayMonsterRate, float surfaceNightMonsterRate, float undergroundDayMonsterRate, float undergroundNightMonsterRate, int grassColor, int foliageColor, boolean keepDefaultSpawns, List<SpawnEntryDef> spawns, List<String> requires) {
        this.registryName = registryName;
        this.name = name;
        this.id = id;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.baseHeight = baseHeight;
        this.heightVariation = heightVariation;
        this.snow = snow;
        this.rain = rain;
        this.waterColor = waterColor;
        this.baseBiome = baseBiome;
        this.topBlock = topBlock;
        this.fillerBlock = fillerBlock;
        this.stoneBlock = stoneBlock;
        this.types = types;
        this.climate = climate;
        this.weight = weight;
        this.spawnBiome = spawnBiome;
        this.villageBiome = villageBiome;
        this.villageSpawn = villageSpawn;
        this.strongholdBiome = strongholdBiome;
        this.decoration = decoration;
        this.spawnChance = spawnChance;
        this.surfaceDayMonsterRate = surfaceDayMonsterRate;
        this.surfaceNightMonsterRate = surfaceNightMonsterRate;
        this.undergroundDayMonsterRate = undergroundDayMonsterRate;
        this.undergroundNightMonsterRate = undergroundNightMonsterRate;
        this.grassColor = grassColor;
        this.foliageColor = foliageColor;
        this.keepDefaultSpawns = keepDefaultSpawns;
        this.spawns = spawns;
        this.requires = requires;
    }

    public boolean isAutoId() { return id == AUTO_ID; }
}
