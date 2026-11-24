package mctmods.resourcedatapackloader.common.worldgen.biome;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentSeasonTint;
import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnEntryDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.BiomeDef;
import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContentBiome extends Biome {
    private static final String TREES = "trees";
    private static final String FLOWERS = "flowers";
    private static final String GRASS = "grass";
    private static final String DEADBUSH = "deadbush";
    private static final String MUSHROOMS = "mushrooms";
    private static final String BIGMUSHROOMS = "bigmushrooms";
    private static final String REEDS = "reeds";
    private static final String CACTI = "cacti";
    private static final String SAND = "sand";
    private static final String GRAVEL = "gravel";
    private static final String CLAY = "clay";
    private static final String WATERLILY = "waterlily";
    private static final String FALLS = "falls";
    private static final String EXTRATREECHANCE = "extratreechance";
    private static final String PUMPKINS = "pumpkins";
    private static final String DESERTWELLS = "desertwells";
    private static final String ICE = "ice";
    private static final String FOSSILS = "fossils";
    private static final String ROCKS = "rocks";
    private static final Map<DecorateBiomeEvent.Decorate.EventType, String> SUPPRESSED = suppressed();
    @Nullable private final IBlockState stoneState;
    private final BiomeDef def;

    public static ContentBiome create(BiomeDef def) { return new ContentBiome(def, properties(def)); }

    protected ContentBiome(BiomeDef def, BiomeProperties properties) {
        super(properties);
        this.def = def;
        setRegistryName(def.registryName);
        IBlockState top = block(def.topBlock);
        if (top != null) { this.topBlock = top; }
        IBlockState filler = block(def.fillerBlock);
        if (filler != null) { this.fillerBlock = filler; }
        this.stoneState = def.stoneBlock.isEmpty() ? null : block(def.stoneBlock);
        if (!def.keepDefaultSpawns) { clearSpawns(); }
        decorate(this.decorator, def.decoration);
    }

    private static BiomeProperties properties(BiomeDef def) {
        BiomeProperties properties = new BiomeProperties(def.name);
        properties.setTemperature(def.temperature);
        properties.setRainfall(def.rainfall);
        properties.setBaseHeight(def.baseHeight);
        properties.setHeightVariation(def.heightVariation);
        properties.setWaterColor(def.waterColor);
        if (def.snow) { properties.setSnowEnabled(); }
        if (!def.rain) { properties.setRainDisabled(); }
        if (!def.baseBiome.isEmpty()) { properties.setBaseBiome(def.baseBiome); }
        return properties;
    }

    public BiomeDef getDef() { return def; }

    public void resolveSpawns() { for (SpawnEntryDef entry : def.spawns) { addSpawn(entry); } }

    private void clearSpawns() {
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
    }

    private void addSpawn(SpawnEntryDef entry) {
        Class<? extends EntityLiving> type = SpawnEntryDef.living("Biome", def.registryName, entry.entity);
        if (type == null) { return; }
        List<SpawnListEntry> list = listFor(entry.creatureType);
        if (list == null) {
            ContentLog.LOGGER.error("Spawn entry in biome {} has creature type '{}', which is not one of monster, creature, ambient or water", def.registryName, entry.creatureType);
            return;
        }
        list.add(new SpawnListEntry(type, entry.weight, entry.min, entry.max));
    }

    @Nullable private List<SpawnListEntry> listFor(String creatureType) {
        EnumCreatureType type = SpawnEntryDef.creatureType(creatureType);
        if (type == null) { return null; }
        switch (type) {
            case MONSTER: return spawnableMonsterList;
            case CREATURE: return spawnableCreatureList;
            case WATER_CREATURE: return spawnableWaterCreatureList;
            case AMBIENT: return spawnableCaveCreatureList;
            default: return null;
        }
    }

    @Nullable private IBlockState block(String name) { return ContentStates.parse(name, def.registryName); }

    public void applyDecoration(BiomeDecorator target) { decorate(target, def.decoration); }

    public boolean suppresses(DecorateBiomeEvent.Decorate.EventType type) {
        String key = SUPPRESSED.get(type);
        if (key == null) { return false; }
        Integer value = def.decoration.get(key);
        return value != null && value <= 0;
    }

    @Nullable public IBlockState getStoneState() { return stoneState; }

    private void decorate(BiomeDecorator decorator, Map<String, Integer> values) {
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            if (!apply(decorator, entry.getKey(), entry.getValue())) { ContentLog.LOGGER.error("Biome {} sets decoration '{}', which is not a known setting", def.registryName, entry.getKey()); }
        }
    }

    private static boolean apply(BiomeDecorator decorator, String key, int value) {
        switch (key.toLowerCase(Locale.ROOT)) {
            case TREES: decorator.treesPerChunk = value; return true;
            case FLOWERS: decorator.flowersPerChunk = value; return true;
            case GRASS: decorator.grassPerChunk = value; return true;
            case DEADBUSH: decorator.deadBushPerChunk = value; return true;
            case MUSHROOMS: decorator.mushroomsPerChunk = value; return true;
            case BIGMUSHROOMS: decorator.bigMushroomsPerChunk = value; return true;
            case REEDS: decorator.reedsPerChunk = value; return true;
            case CACTI: decorator.cactiPerChunk = value; return true;
            case SAND: decorator.sandPatchesPerChunk = value; return true;
            case GRAVEL: decorator.gravelPatchesPerChunk = value; return true;
            case CLAY: decorator.clayPerChunk = value; return true;
            case WATERLILY: decorator.waterlilyPerChunk = value; return true;
            case FALLS: decorator.generateFalls = value > 0; return true;
            case EXTRATREECHANCE: decorator.extraTreeChance = MathHelper.clamp(value, 0, 100) / 100.0F; return true;
            case PUMPKINS: case DESERTWELLS: case ICE: case FOSSILS: case ROCKS: return true;
            default: return false;
        }
    }

    @Override public float getSpawningChance() { return def.spawnChance; }

    public float monsterRate(boolean sky, boolean day) {
        if (sky) { return day ? def.surfaceDayMonsterRate : def.surfaceNightMonsterRate; }
        return day ? def.undergroundDayMonsterRate : def.undergroundNightMonsterRate;
    }

    @SideOnly(Side.CLIENT) public int snowColor() { return def.snowColor; }

    @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return ContentSeasonTint.grass(this, def.grassColor == ContentTypes.NO_COLOR ? original : def.grassColor); }

    private static Map<DecorateBiomeEvent.Decorate.EventType, String> suppressed() {
        Map<DecorateBiomeEvent.Decorate.EventType, String> map = new EnumMap<>(DecorateBiomeEvent.Decorate.EventType.class);
        map.put(DecorateBiomeEvent.Decorate.EventType.TREE, TREES);
        map.put(DecorateBiomeEvent.Decorate.EventType.FLOWERS, FLOWERS);
        map.put(DecorateBiomeEvent.Decorate.EventType.GRASS, GRASS);
        map.put(DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH, DEADBUSH);
        map.put(DecorateBiomeEvent.Decorate.EventType.SHROOM, MUSHROOMS);
        map.put(DecorateBiomeEvent.Decorate.EventType.BIG_SHROOM, BIGMUSHROOMS);
        map.put(DecorateBiomeEvent.Decorate.EventType.REED, REEDS);
        map.put(DecorateBiomeEvent.Decorate.EventType.CACTUS, CACTI);
        map.put(DecorateBiomeEvent.Decorate.EventType.SAND, SAND);
        map.put(DecorateBiomeEvent.Decorate.EventType.SAND_PASS2, GRAVEL);
        map.put(DecorateBiomeEvent.Decorate.EventType.CLAY, CLAY);
        map.put(DecorateBiomeEvent.Decorate.EventType.LILYPAD, WATERLILY);
        map.put(DecorateBiomeEvent.Decorate.EventType.LAKE_WATER, FALLS);
        map.put(DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA, FALLS);
        map.put(DecorateBiomeEvent.Decorate.EventType.PUMPKIN, PUMPKINS);
        map.put(DecorateBiomeEvent.Decorate.EventType.DESERT_WELL, DESERTWELLS);
        map.put(DecorateBiomeEvent.Decorate.EventType.ICE, ICE);
        map.put(DecorateBiomeEvent.Decorate.EventType.FOSSIL, FOSSILS);
        map.put(DecorateBiomeEvent.Decorate.EventType.ROCK, ROCKS);
        return map;
    }

    @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) { return ContentSeasonTint.foliage(this, def.foliageColor == ContentTypes.NO_COLOR ? original : def.foliageColor); }
}
