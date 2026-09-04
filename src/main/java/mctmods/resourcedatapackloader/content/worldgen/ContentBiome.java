package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.SpawnEntryDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.MathHelper;
import java.util.List;
import java.util.Locale;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;

public class ContentBiome extends Biome {
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

    public void resolveSpawns() {
        for (SpawnEntryDef entry : def.spawns) { addSpawn(entry); }
    }

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
            if (!apply(decorator, entry.getKey(), entry.getValue())) {
                ContentLog.LOGGER.error("Biome {} sets decoration '{}', which is not a known setting", def.registryName, entry.getKey());
            }
        }
    }

    private static boolean apply(BiomeDecorator decorator, String key, int value) {
        switch (key.toLowerCase(Locale.ROOT)) {
            case "trees": decorator.treesPerChunk = value; return true;
            case "flowers": decorator.flowersPerChunk = value; return true;
            case "grass": decorator.grassPerChunk = value; return true;
            case "deadbush": decorator.deadBushPerChunk = value; return true;
            case "mushrooms": decorator.mushroomsPerChunk = value; return true;
            case "bigmushrooms": decorator.bigMushroomsPerChunk = value; return true;
            case "reeds": decorator.reedsPerChunk = value; return true;
            case "cacti": decorator.cactiPerChunk = value; return true;
            case "sand": decorator.sandPatchesPerChunk = value; return true;
            case "gravel": decorator.gravelPatchesPerChunk = value; return true;
            case "clay": decorator.clayPerChunk = value; return true;
            case "waterlily": decorator.waterlilyPerChunk = value; return true;
            case "falls": decorator.generateFalls = value > 0; return true;
            case "extratreechance": decorator.extraTreeChance = MathHelper.clamp(value, 0, 100) / 100.0F; return true;
            case "pumpkins": case "desertwells": case "ice": case "fossils": case "rocks": return true;
            default: return false;
        }
    }

    @Override public float getSpawningChance() { return def.spawnChance; }

    public float monsterRate(boolean sky, boolean day) {
        if (sky) { return day ? def.surfaceDayMonsterRate : def.surfaceNightMonsterRate; }
        return day ? def.undergroundDayMonsterRate : def.undergroundNightMonsterRate;
    }

    @Override @SideOnly(Side.CLIENT) public int getModdedBiomeGrassColor(int original) { return def.grassColor == ContentTypes.NO_COLOR ? original : def.grassColor; }

    private static Map<DecorateBiomeEvent.Decorate.EventType, String> suppressed() {
        Map<DecorateBiomeEvent.Decorate.EventType, String> map = new EnumMap<>(DecorateBiomeEvent.Decorate.EventType.class);
        map.put(DecorateBiomeEvent.Decorate.EventType.TREE, "trees");
        map.put(DecorateBiomeEvent.Decorate.EventType.FLOWERS, "flowers");
        map.put(DecorateBiomeEvent.Decorate.EventType.GRASS, "grass");
        map.put(DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH, "deadbush");
        map.put(DecorateBiomeEvent.Decorate.EventType.SHROOM, "mushrooms");
        map.put(DecorateBiomeEvent.Decorate.EventType.BIG_SHROOM, "bigmushrooms");
        map.put(DecorateBiomeEvent.Decorate.EventType.REED, "reeds");
        map.put(DecorateBiomeEvent.Decorate.EventType.CACTUS, "cacti");
        map.put(DecorateBiomeEvent.Decorate.EventType.SAND, "sand");
        map.put(DecorateBiomeEvent.Decorate.EventType.SAND_PASS2, "gravel");
        map.put(DecorateBiomeEvent.Decorate.EventType.CLAY, "clay");
        map.put(DecorateBiomeEvent.Decorate.EventType.LILYPAD, "waterlily");
        map.put(DecorateBiomeEvent.Decorate.EventType.LAKE_WATER, "falls");
        map.put(DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA, "falls");
        map.put(DecorateBiomeEvent.Decorate.EventType.PUMPKIN, "pumpkins");
        map.put(DecorateBiomeEvent.Decorate.EventType.DESERT_WELL, "desertwells");
        map.put(DecorateBiomeEvent.Decorate.EventType.ICE, "ice");
        map.put(DecorateBiomeEvent.Decorate.EventType.FOSSIL, "fossils");
        map.put(DecorateBiomeEvent.Decorate.EventType.ROCK, "rocks");
        return map;
    }

    @Override @SideOnly(Side.CLIENT) public int getModdedBiomeFoliageColor(int original) {
        return def.foliageColor == ContentTypes.NO_COLOR ? original : def.foliageColor;
    }
}
