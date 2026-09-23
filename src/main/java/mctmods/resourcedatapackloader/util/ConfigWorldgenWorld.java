package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public abstract class ConfigWorldgenWorld {
    private final ModConfigSpec.BooleanValue load;
    private final ModConfigSpec.BooleanValue worldgenDebug;
    private final ModConfigSpec.ConfigValue<String> worldTemplate;
    private final ModConfigSpec.ConfigValue<String> worldSeed;
    private final ModConfigSpec.ConfigValue<String> worldName;
    private final ModConfigSpec.ConfigValue<String> worldGameMode;
    private final ModConfigSpec.ConfigValue<String> worldType;
    private final ModConfigSpec.ConfigValue<List<? extends String>> worldTypeExceptions;
    private final ModConfigSpec.BooleanValue tellWorldType;
    private final ModConfigSpec.ConfigValue<String> generatorOptions;
    private final ModConfigSpec.IntValue worldMinHeight;
    private final ModConfigSpec.IntValue worldMaxHeight;
    private final ModConfigSpec.ConfigValue<String> deepStone;
    private final ModConfigSpec.ConfigValue<String> noiseCaves;
    private final ModConfigSpec.ConfigValue<String> worldSpawn;
    private final ModConfigSpec.IntValue worldBorder;
    private final ModConfigSpec.IntValue worldBorderLimit;
    private final ModConfigSpec.IntValue worldTime;
    private final ModConfigSpec.ConfigValue<List<? extends String>> worldDifficulty;
    private final ModConfigSpec.BooleanValue flatBedrock;
    private final ModConfigSpec.ConfigValue<List<? extends String>> flatBedrockDimensions;
    private final ModConfigSpec.BooleanValue flatBedrockDimensionsAreBlacklist;
    private final ModConfigSpec.IntValue bedrockLayers;
    private final ModConfigSpec.ConfigValue<List<? extends String>> flatBedrockBiomes;
    private final ModConfigSpec.BooleanValue flatBedrockBiomesAreBlacklist;
    private final ModConfigSpec.BooleanValue flatBedrockRoof;
    private final ModConfigSpec.ConfigValue<String> flatBedrockFiller;
    private final ModConfigSpec.ConfigValue<List<? extends String>> flatBedrockFillers;
    private final ModConfigSpec.ConfigValue<List<? extends String>> flatBedrockBiomeTypes;
    private final ModConfigSpec.BooleanValue flatBedrockRetrogen;
    private final ModConfigSpec.ConfigValue<String> flatBedrockRetrogenKey;
    private final ModConfigSpec.BooleanValue voidWorld;
    private final ModConfigSpec.ConfigValue<List<? extends String>> voidWorldDimensions;
    private final ModConfigSpec.BooleanValue voidWorldDimensionsAreBlacklist;
    private final ModConfigSpec.BooleanValue dragonFight;
    private final ModConfigSpec.ConfigValue<String> voidPlatformBlock;
    private final ModConfigSpec.IntValue voidPlatformHeight;
    private final ModConfigSpec.IntValue voidPlatformSize;
    private final ModConfigSpec.BooleanValue retrogen;
    private final ModConfigSpec.BooleanValue adoptExistingChunks;
    private final ModConfigSpec.ConfigValue<String> retrogenKey;
    private final ModConfigSpec.IntValue retrogenChunksPerTick;
    private final ModConfigSpec.BooleanValue blockOres;
    private final ModConfigSpec.BooleanValue logBlockedOres;
    private final ModConfigSpec.BooleanValue logBlockedBiomes;
    private final ModConfigSpec.ConfigValue<List<? extends String>> oreWhitelist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> prospectItems;
    private final ModConfigSpec.BooleanValue prospectItemsAreBlacklist;
    private final ModConfigSpec.BooleanValue prospectDrops;
    private final ModConfigSpec.IntValue prospectSlow;
    private final ModConfigSpec.IntValue prospectWear;
    private final ModConfigSpec.ConfigValue<List<? extends String>> oreTypes;
    private final ModConfigSpec.BooleanValue oreTypesAreBlacklist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> blockOreDimensions;
    private final ModConfigSpec.BooleanValue blockOreDimensionsAreBlacklist;
    private final ModConfigSpec.BooleanValue blockBiomes;
    private final ModConfigSpec.ConfigValue<List<? extends String>> biomeWhitelist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> biomeNames;
    private final ModConfigSpec.BooleanValue biomeNamesAreBlacklist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> blockBiomeDimensions;
    private final ModConfigSpec.BooleanValue blockBiomeDimensionsAreBlacklist;
    private final ModConfigSpec.DoubleValue surfaceDayMonsterRate;
    private final ModConfigSpec.DoubleValue surfaceNightMonsterRate;
    private final ModConfigSpec.DoubleValue undergroundDayMonsterRate;
    private final ModConfigSpec.DoubleValue undergroundNightMonsterRate;
    private final ModConfigSpec.IntValue monsterCap;
    private final ModConfigSpec.IntValue creatureCap;
    private final ModConfigSpec.IntValue ambientCap;
    private final ModConfigSpec.IntValue waterCreatureCap;
    private final ModConfigSpec.IntValue monsterSpawnLight;
    private final ModConfigSpec.IntValue caveRegionPlainWeight;
    private final ModConfigSpec.IntValue caveRegionCells;
    private final ModConfigSpec.IntValue caveRegionCellsY;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureSpacing;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureSeparation;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureMost;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureSpawners;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureMinDistanceFromSpawn;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureBiomes;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureBiomesAreBlacklist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureSpawns;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureAt;
    private final ModConfigSpec.ConfigValue<List<? extends String>> structureAdaptation;

    ConfigWorldgenWorld(ModConfigSpec.Builder builder) {
        builder.comment("What generates in the world, and what is stopped from generating").push("worldgen");
        load = builder.comment("Generate what the worldgen/*.json entries describe in new chunks. Chunks that already exist are not changed [Default=true]").define("load", true);
        worldgenDebug = builder.comment("Write the debug lines other messages refer to into logs/rdpl.log, such as which pack served a file and what each command did. Very verbose [Default=false]").define("worldgenDebug", false);
        worldTemplate = builder.comment("Which world template's settings apply. A pack adds one in worldtemplates/*.json and you name it here as namespace:name. 'auto' picks the template from the highest priority pack. Empty uses none [Default=auto]").define("worldTemplate", "auto");
        worldSeed = builder.comment("The seed every new world is made with, whatever was typed when it was made, written the same way it would be typed. Empty leaves the choice alone [Default=empty]").define("worldSeed", "");
        worldGameMode = builder.comment("Which way every new world is started, one of survival, hardcore, creative, adventure or spectator. Hardcore is survival where death ends the world, save wide, the same as the choice on the world screen. Empty leaves it as whoever made the world chose. A dedicated server sets every world to its server.properties mode at each start, so there the pack's mode is written into server.properties (gamemode and hardcore) before the world loads [Default=empty]").define("worldGameMode", "");
        worldName = builder.comment("What a new world is called when the screen for making one opens. Empty leaves it as the game names it [Default=empty]").define("worldName", "");
        worldType = builder.comment("The world type the shaped world is built on, one of default, largebiomes, amplified or flat, with the 1.12.2 names customized and default_1_1 read as default; flat is a superflat overworld built from the generatorOptions layers, with the pack's cities on it. The shape below (heights, deep stone, sea level, bedrock, void) is generated as a world preset of its own, listed under World Type on the world screen and chosen there whatever was picked. Empty builds on default [Default=empty]").define("worldType", "");
        worldTypeExceptions = builder.comment("World types a player picks that the generated preset leaves alone, such as flat or debug_all_block_states. Empty means every choice is replaced [Default=[flat, debug_all_block_states]]").defineListAllowEmpty("worldTypeExceptions", List.of("flat", "debug_all_block_states"), () -> "", each -> each instanceof String);
        tellWorldType = builder.comment("Tell a player in chat, as they join a world made with the generated preset, which template shaped it. A pack cannot set this [Default=true]").define("tellWorldType", true);
        generatorOptions = builder.comment("The overworld's terrain settings as a JSON object, the keys the 1.12.2 customized world type wrote. Read here: seaLevel, useLavaOceans, fixedBiome, and useCaves, useRavines, useDungeons, useLavaLakes, useStrongholds, useVillages, useMineShafts, useTemples, useMonuments and useMansions set to false. With worldType flat it is the layers instead, bottom up, as the 1.12.2 superflat text 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village or a list of layers; the number after the layers is the biome, and village, biome_1, mineshaft, stronghold, oceanmonument, lake, lava_lake and decoration after it turn those on. Only applied to a world as it is created. Empty leaves the terrain as the world type makes it [Default=empty]").define("generatorOptions", "");
        worldMinHeight = builder.comment("The lowest block of the overworld, a multiple of 16 down to -2032. The game's own bottom is -64; lower makes a deep world under the vanilla terrain, solid stone until the worldgen layer carves it or noiseCaves carries the game's caves down. Only applied through the generated preset [Default=-64]").defineInRange("worldMinHeight", -64, -2032, 2016);
        worldMaxHeight = builder.comment("The block above the overworld's top, a multiple of 16 up to 2032, at most 4064 above worldMinHeight. The game's own top is 320; higher leaves open sky above the vanilla terrain [Default=320]").defineInRange("worldMaxHeight", 320, -2016, 2032);
        deepStone = builder.comment("The block the world below the vanilla terrain is made of when worldMinHeight goes under -64, such as a pack's own deepslate. It blends into deepslate across the eight layers under -64 the way deepslate blends into stone. Empty keeps stone [Default=empty]").define("deepStone", "");
        noiseCaves = builder.comment("Where the game's caves, tunnels, noodles and aquifers carry on when worldMinHeight goes under -64: off keeps the world under the vanilla terrain solid deep stone for the worldgen layer to carve, deep carries them down to the floor with the lava lakes moved to its bottom ten layers, world means the same on this version because the vanilla terrain has them already [Default=off]").define("noiseCaves", "off");
        worldSpawn = builder.comment("Where every new world spawns, written as x,z or x,y,z. Without a y the ground at that spot is used. Only applied to a world as it is created. Empty leaves the choice to the game [Default=empty]").define("worldSpawn", "");
        worldBorder = builder.comment("How far across, in blocks, the world border stands in every new world. Only applied to a world as it is created. 0 leaves the border where the game puts it [Default=0]").defineInRange("worldBorder", 0, 0, 60000000);
        worldBorderLimit = builder.comment("The widest border a pack is allowed to ask for through worldBorder. A pack asking for more is refused and the border is left where the game puts it. A pack cannot set this [Default=60000000]").defineInRange("worldBorderLimit", 60000000, 1, 60000000);
        worldTime = builder.comment("Lock the overworld's time of day, in ticks, the same figure /time set takes, so 18000 is midnight. The clock stops and never moves. -1 leaves time running [Default=-1]").defineInRange("worldTime", -1, -1, 23999);
        worldDifficulty = builder.comment("Lock the difficulty, one of peaceful, easy, normal or hard. A bare difficulty covers every dimension, and an entry written as dimension=difficulty, such as minecraft:the_nether=hard, covers that dimension alone and wins over the bare one. The world's own setting is left as it was and comes back when the entry is removed. Empty leaves it as chosen [Default=empty]").defineListAllowEmpty("worldDifficulty", List.of(), () -> "", each -> each instanceof String);
        flatBedrock = builder.comment("Replace the jagged bedrock at the bottom of the world with flat layers. Only affects new chunks unless flatBedrockRetrogen is on. Cannot be undone [Default=false]").define("flatBedrock", false);
        flatBedrockDimensions = builder.comment("Dimensions to flatten bedrock in, by id such as minecraft:the_nether. Leave empty for every dimension [Default=[minecraft:overworld]]").defineListAllowEmpty("flatBedrockDimensions", List.of("minecraft:overworld"), () -> "", each -> each instanceof String);
        flatBedrockDimensionsAreBlacklist = builder.comment("On, flattening skips these dimensions. Off, it applies only to them [Default=false]").define("flatBedrockDimensionsAreBlacklist", false);
        bedrockLayers = builder.comment("How many layers of bedrock to leave at the bottom [Default=1]").defineInRange("bedrockLayers", 1, 1, 5);
        flatBedrockBiomes = builder.comment("Biomes to flatten bedrock in, by id such as minecraft:birch_forest. Empty means every biome; elsewhere the bedrock stays as the game makes it").defineListAllowEmpty("flatBedrockBiomes", List.of(), () -> "", each -> each instanceof String);
        flatBedrockBiomesAreBlacklist = builder.comment("On, flattening skips these biomes. Off, it applies only to them [Default=false]").define("flatBedrockBiomesAreBlacklist", false);
        flatBedrockRoof = builder.comment("Flatten the bedrock ceiling too, where a dimension has one, such as the Nether roof [Default=false]").define("flatBedrockRoof", false);
        flatBedrockFiller = builder.comment("What replaces the bedrock that is removed. Empty picks the block the dimension is made of: stone, netherrack, end stone [Default=]").define("flatBedrockFiller", "");
        flatBedrockFillers = builder.comment("A filler per dimension, written dimension=block, such as minecraft:the_nether=minecraft:netherrack. Overrides flatBedrockFiller for the dimensions listed [Default=[minecraft:the_nether=minecraft:netherrack, minecraft:the_end=minecraft:end_stone]]").defineListAllowEmpty("flatBedrockFillers", List.of("minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"), () -> "", each -> each instanceof String);
        flatBedrockBiomeTypes = builder.comment("Biome types to flatten bedrock in, alongside flatBedrockBiomes, by biome tag such as minecraft:is_ocean or by 1.12.2 type name such as OCEAN").defineListAllowEmpty("flatBedrockBiomeTypes", List.of(), () -> "", each -> each instanceof String);
        flatBedrockRetrogen = builder.comment("Flatten the bedrock in chunks that already exist, not only new ones, while retrogen is on. Each chunk is done once and remembers it [Default=false]").define("flatBedrockRetrogen", false);
        flatBedrockRetrogenKey = builder.comment("Change this to make every chunk eligible for bedrock flattening again [Default=0000]").define("flatBedrockRetrogenKey", "0000");
        voidWorld = builder.comment("Generate the listed dimensions as empty space with a platform at the spawn point and nothing living, through the generated preset [Default=false]").define("voidWorld", false);
        voidWorldDimensions = builder.comment("Which dimensions are made void, by id. Empty means none, or every dimension when voidWorldDimensionsAreBlacklist is on [Default=[minecraft:overworld]]").defineListAllowEmpty("voidWorldDimensions", List.of("minecraft:overworld"), () -> "", each -> each instanceof String);
        voidWorldDimensionsAreBlacklist = builder.comment("Treat voidWorldDimensions as the dimensions to leave alone instead [Default=false]").define("voidWorldDimensionsAreBlacklist", false);
        dragonFight = builder.comment("Let the ender dragon fight happen: the dragon itself, its bar, the crystals and the fountain it stands on. A void end leaves it out unless a pack asks for it [Default=true]").define("dragonFight", true);
        voidPlatformBlock = builder.comment("The block the void world platform is made of [Default=minecraft:stone]").define("voidPlatformBlock", "minecraft:stone");
        voidPlatformHeight = builder.comment("The y the void world platform sits at [Default=64]").defineInRange("voidPlatformHeight", 64, -2032, 2031);
        voidPlatformSize = builder.comment("How wide the void world platform is, in blocks. Rounded down to an odd number so it centers on the spawn point [Default=9]").defineInRange("voidPlatformSize", 9, 1, Integer.MAX_VALUE);
        retrogen = builder.comment("Catch existing chunks up on worldgen entries with \"retrogen\": true. Off, chunks that already exist are left alone. Chunks are marked as they generate either way, so turning this on later only touches chunks older than the pack [Default=false]").define("retrogen", false);
        adoptExistingChunks = builder.comment("Treat chunks that already exist as if this pack generated them, marking them instead of leaving them for retrogen. Turn this on when replacing a mod that already generated the same ore, so retrogen never doubles it. Worldgen entries added later still retrogen into them [Default=false]").define("adoptExistingChunks", false);
        retrogenKey = builder.comment("Change this to make every chunk eligible for retrogen again, for every worldgen entry. New veins are added on top of what is already there [Default=0000]").define("retrogenKey", "0000");
        retrogenChunksPerTick = builder.comment("How many already generated chunks to catch up per tick. Higher is faster but stutters more [Default=2]").defineInRange("retrogenChunksPerTick", 2, 1, Integer.MAX_VALUE);
        blockOres = builder.comment("Stop every mod, and Minecraft itself, from generating ores. Only the mods in oreWhitelist still generate. An ore is a placed feature with ore in its id, which is Minecraft's and most mods' [Default=false]").define("blockOres", false);
        logBlockedOres = builder.comment("Log the first time each mod and ore type is blocked, so you can see what to whitelist [Default=true]").define("logBlockedOres", true);
        logBlockedBiomes = builder.comment("Log a per mod count of which biomes were blocked, so you can see what to whitelist [Default=true]").define("logBlockedBiomes", true);
        oreWhitelist = builder.comment("Mod ids allowed to generate ores while blockOres is on. Ores a pack defines belong to that pack's namespace [Default=[minecraft]]").defineListAllowEmpty("oreWhitelist", List.of("minecraft"), () -> "", each -> each instanceof String);
        prospectItems = builder.comment("Items that prospect for vein shaped worldgen entries when a sneaking player breaks a block with one, as item=entry|entry[,radius in chunks] or item=*[,radius], e.g. minecraft:compass=iron_vein|coal_seam or mypack:rod=*,12. The reading names the ore and a compass direction [Default=[]]").defineListAllowEmpty("prospectItems", List.of(), () -> "", each -> each instanceof String);
        prospectItemsAreBlacklist = builder.comment("On, the entries named after an item in prospectItems are the ones it does NOT read, and every other vein shaped entry is [Default=false]").define("prospectItemsAreBlacklist", false);
        prospectDrops = builder.comment("Whether a block broken in prospecting mode drops anything. Off, the sample is destroyed: no drops, no experience [Default=false]").define("prospectDrops", false);
        prospectSlow = builder.comment("How many times slower a block breaks in prospecting mode [Default=2]").defineInRange("prospectSlow", 2, 1, 100);
        prospectWear = builder.comment("How much durability a prospecting break costs the item, at least 2 [Default=2]").defineInRange("prospectWear", 2, 2, 1000);
        oreTypes = builder.comment("Ore types this applies to, whoever generates them and whatever the whitelist says. Known types: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM for any other ore [Default=[]]").defineListAllowEmpty("oreTypes", List.of(), () -> "", each -> each instanceof String);
        oreTypesAreBlacklist = builder.comment("On, oreTypes are blocked. Off, only oreTypes generate [Default=true]").define("oreTypesAreBlacklist", true);
        blockOreDimensions = builder.comment("Dimensions ore blocking applies to, by id such as minecraft:the_nether or a pack's own. Empty means every dimension [Default=[]]").defineListAllowEmpty("blockOreDimensions", List.of(), () -> "", each -> each instanceof String);
        blockOreDimensionsAreBlacklist = builder.comment("Treat blockOreDimensions as the dimensions to leave alone instead [Default=false]").define("blockOreDimensionsAreBlacklist", false);
        blockBiomes = builder.comment("Stop every biome from generating except the mods in biomeWhitelist. Blocked biomes become the void biome, or what the world template's roles and fallback name. Blocking every biome makes the overworld a void world [Default=false]").define("blockBiomes", false);
        biomeWhitelist = builder.comment("Mod ids whose biomes still generate while blockBiomes is on. A pack biome uses the pack's namespace [Default=[minecraft]]").defineListAllowEmpty("biomeWhitelist", List.of("minecraft"), () -> "", each -> each instanceof String);
        biomeNames = builder.comment("Biomes this applies to, whoever owns them and whatever the whitelist says, by id such as minecraft:birch_forest [Default=[]]").defineListAllowEmpty("biomeNames", List.of(), () -> "", each -> each instanceof String);
        biomeNamesAreBlacklist = builder.comment("On, biomeNames are blocked. Off, only biomeNames generate [Default=true]").define("biomeNamesAreBlacklist", true);
        blockBiomeDimensions = builder.comment("Dimensions biome blocking applies to, by id. Empty means every dimension whose biomes are placed by climate, the overworld and the nether [Default=[minecraft:overworld]]").defineListAllowEmpty("blockBiomeDimensions", List.of("minecraft:overworld"), () -> "", each -> each instanceof String);
        blockBiomeDimensionsAreBlacklist = builder.comment("On, biome blocking skips these dimensions. Off, it applies only to them [Default=false]").define("blockBiomeDimensionsAreBlacklist", false);
        surfaceDayMonsterRate = builder.comment("How often hostile mobs spawn on the surface during the day, where the sky can be seen. 0 stops them, 1 is vanilla, above 1 forces spawns vanilla would refuse [Default=1.0]").defineInRange("surfaceDayMonsterRate", 1.0D, 0.0D, 4.0D);
        surfaceNightMonsterRate = builder.comment("The same for the surface at night [Default=1.0]").defineInRange("surfaceNightMonsterRate", 1.0D, 0.0D, 4.0D);
        undergroundDayMonsterRate = builder.comment("The same for underground during the day, where the sky cannot be seen [Default=1.0]").defineInRange("undergroundDayMonsterRate", 1.0D, 0.0D, 4.0D);
        undergroundNightMonsterRate = builder.comment("The same for underground at night [Default=1.0]").defineInRange("undergroundNightMonsterRate", 1.0D, 0.0D, 4.0D);
        monsterCap = builder.comment("How many hostile mobs may be loaded at once across the world, before the count is scaled by how many chunks are loaded. Vanilla is 70. -1 leaves it alone [Default=-1]").defineInRange("monsterCap", -1, -1, 1000);
        creatureCap = builder.comment("The same cap for passive animals. Vanilla is 10. -1 leaves it alone [Default=-1]").defineInRange("creatureCap", -1, -1, 1000);
        ambientCap = builder.comment("The same cap for ambient mobs such as bats. Vanilla is 15. -1 leaves it alone [Default=-1]").defineInRange("ambientCap", -1, -1, 1000);
        waterCreatureCap = builder.comment("The same cap for water mobs such as squid. Vanilla is 5. -1 leaves it alone [Default=-1]").defineInRange("waterCreatureCap", -1, -1, 1000);
        monsterSpawnLight = builder.comment("The brightest block light a hostile mob may still spawn in, on top of the vanilla checks. -1 keeps the vanilla rule alone. Spawners are not affected [Default=-1]").defineInRange("monsterSpawnLight", -1, -1, 15);
        caveRegionPlainWeight = builder.comment("The weight of plain, region-less underground against the cave regions' own weights. Higher leaves more of the underground without any region [Default=4]").defineInRange("caveRegionPlainWeight", 4, 0, Integer.MAX_VALUE);
        caveRegionCells = builder.comment("How wide a cave region cell is in blocks. Cave regions from packs are painted over the underground in cells about this size [Default=128]").defineInRange("caveRegionCells", 128, 16, Integer.MAX_VALUE);
        caveRegionCellsY = builder.comment("How tall a cave region cell is in blocks [Default=64]").defineInRange("caveRegionCellsY", 64, 16, Integer.MAX_VALUE);
        structureSpacing = builder.comment("How far apart vanilla structures are seeded, in chunks, as structure=chunks entries: the 1.12.2 names temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities and villages, or any structure set id such as pillager_outposts. For mineshafts the number is one chunk in that many; for strongholds it is the ring distance [Default=[]]").defineListAllowEmpty("structureSpacing", List.of(), () -> "", each -> each instanceof String);
        structureSeparation = builder.comment("The closest two of a structure may be, in chunks, as structure=chunks entries; for strongholds it is the ring spread [Default=[]]").defineListAllowEmpty("structureSeparation", List.of(), () -> "", each -> each instanceof String);
        structureMost = builder.comment("The most of a structure a dimension may hold, as structure=count entries such as villages=100: once that many have been founded no chunk founds another, chunks pinned with structureAt aside. 0 or an absent entry sets no ceiling [Default=[]]").defineListAllowEmpty("structureMost", List.of(), () -> "", each -> each instanceof String);
        structureSpawners = builder.comment("What the mob spawner inside a vanilla structure spawns, as structure=namespace:entity entries, comma separated for a random pick. Only dungeons, mineshafts, netherbridges and strongholds build one; spawners other mods place are left alone [Default=[]]").defineListAllowEmpty("structureSpawners", List.of(), () -> "", each -> each instanceof String);
        structureMinDistanceFromSpawn = builder.comment("How far from the world spawn a structure starts, in blocks, as structure=blocks entries. Measured from the world's spawn point; while a new world is still choosing one, from the pack's worldSpawn when one is set, else from the world origin [Default=[]]").defineListAllowEmpty("structureMinDistanceFromSpawn", List.of(), () -> "", each -> each instanceof String);
        structureBiomes = builder.comment("Where a structure may generate, as structure=biome,biome entries naming biome ids or biome types such as SANDY [Default=[]]").defineListAllowEmpty("structureBiomes", List.of(), () -> "", each -> each instanceof String);
        structureBiomesAreBlacklist = builder.comment("Direction of the biome lists, written as structure=true or structure=false, one per line. True takes the listed biomes away, false makes them the only ones [Default=false]").defineListAllowEmpty("structureBiomesAreBlacklist", List.of(), () -> "", each -> each instanceof String);
        structureSpawns = builder.comment("The mobs a structure spawns whatever the biome says, as structure=namespace:entity:weight:least:most entries, comma separated; an empty list after the = spawns nothing [Default=[]]").defineListAllowEmpty("structureSpawns", List.of(), () -> "", each -> each instanceof String);
        structureAt = builder.comment("Structures pinned to exact spots, as structure=x,z entries in block coordinates, one per wanted instance. A pinned structure generates in that chunk and nowhere else. city=x,z seats the center district of the city whose region holds that spot [Default=[]]").defineListAllowEmpty("structureAt", List.of(), () -> "", each -> each instanceof String);
        structureAdaptation = builder.comment("How the terrain adapts to a structure, as structure=mode entries with the modes none, bury, beard_thin, beard_box and encapsulate [Default=[]]").defineListAllowEmpty("structureAdaptation", List.of(), () -> "", each -> each instanceof String);
    }

    public boolean loadOff() { return Config.loaded() ? !load.get() : !ConfigCore.flag("worldgen.load", true); }

    public boolean worldgenDebug() { return Config.loaded() ? worldgenDebug.get() : ConfigCore.flag("worldgen.worldgenDebug", false); }

    public String worldTemplate() { return Config.loaded() ? worldTemplate.get() : ConfigCore.text("worldgen.worldTemplate", "auto"); }

    public String worldSeed() { return Config.loaded() ? worldSeed.get() : ConfigCore.text("worldgen.worldSeed", ""); }

    public String worldName() { return Config.loaded() ? worldName.get() : ConfigCore.text("worldgen.worldName", ""); }

    public String worldGameMode() { return Config.loaded() ? worldGameMode.get() : ConfigCore.text("worldgen.worldGameMode", ""); }

    public String worldType() { return Config.loaded() ? worldType.get() : ConfigCore.text("worldgen.worldType", ""); }

    public List<String> worldTypeExceptions() { return Config.loaded() ? List.copyOf(worldTypeExceptions.get()) : List.of("flat", "debug_all_block_states"); }

    public boolean tellWorldType() { return Config.loaded() ? tellWorldType.get() : ConfigCore.flag("worldgen.tellWorldType", true); }

    public String generatorOptions() { return Config.loaded() ? generatorOptions.get() : ConfigCore.text("worldgen.generatorOptions", ""); }

    public int worldMinHeight() { return Config.loaded() ? worldMinHeight.get() : ConfigCore.number("worldgen.worldMinHeight", -64); }

    public int worldMaxHeight() { return Config.loaded() ? worldMaxHeight.get() : ConfigCore.number("worldgen.worldMaxHeight", 320); }

    public String deepStone() { return Config.loaded() ? deepStone.get() : ConfigCore.text("worldgen.deepStone", ""); }

    public String noiseCaves() { return Config.loaded() ? noiseCaves.get() : ConfigCore.text("worldgen.noiseCaves", "off"); }

    public String worldSpawn() { return Config.loaded() ? worldSpawn.get() : ConfigCore.text("worldgen.worldSpawn", ""); }

    public int worldBorder() { return Config.loaded() ? worldBorder.get() : 0; }

    public int worldBorderLimit() { return Config.loaded() ? worldBorderLimit.get() : 60000000; }

    public int worldTime() { return Config.loaded() ? worldTime.get() : -1; }

    public List<String> worldDifficulty() { return Config.loaded() ? List.copyOf(worldDifficulty.get()) : List.of(); }

    public boolean flatBedrock() { return Config.loaded() ? flatBedrock.get() : ConfigCore.flag("worldgen.flatBedrock", false); }

    public List<String> flatBedrockDimensions() { return Config.loaded() ? List.copyOf(flatBedrockDimensions.get()) : ConfigCore.strings("worldgen.flatBedrockDimensions", List.of("minecraft:overworld")); }

    public boolean flatBedrockDimensionsAreBlacklist() { return Config.loaded() ? flatBedrockDimensionsAreBlacklist.get() : ConfigCore.flag("worldgen.flatBedrockDimensionsAreBlacklist", false); }

    public int bedrockLayers() { return Config.loaded() ? bedrockLayers.get() : ConfigCore.number("worldgen.bedrockLayers", 1); }

    public List<String> flatBedrockBiomes() { return Config.loaded() ? List.copyOf(flatBedrockBiomes.get()) : ConfigCore.strings("worldgen.flatBedrockBiomes", List.of()); }

    public boolean flatBedrockBiomesAreBlacklist() { return Config.loaded() ? flatBedrockBiomesAreBlacklist.get() : ConfigCore.flag("worldgen.flatBedrockBiomesAreBlacklist", false); }

    public boolean flatBedrockRoof() { return Config.loaded() ? flatBedrockRoof.get() : ConfigCore.flag("worldgen.flatBedrockRoof", false); }

    public String flatBedrockFiller() { return Config.loaded() ? flatBedrockFiller.get() : ConfigCore.text("worldgen.flatBedrockFiller", ""); }

    public List<String> flatBedrockFillers() { return Config.loaded() ? List.copyOf(flatBedrockFillers.get()) : ConfigCore.strings("worldgen.flatBedrockFillers", List.of("minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone")); }

    public List<String> flatBedrockBiomeTypes() { return Config.loaded() ? List.copyOf(flatBedrockBiomeTypes.get()) : ConfigCore.strings("worldgen.flatBedrockBiomeTypes", List.of()); }

    public boolean flatBedrockRetrogen() { return Config.loaded() ? flatBedrockRetrogen.get() : ConfigCore.flag("worldgen.flatBedrockRetrogen", false); }

    public String flatBedrockRetrogenKey() { return Config.loaded() ? flatBedrockRetrogenKey.get() : ConfigCore.text("worldgen.flatBedrockRetrogenKey", "0000"); }

    public boolean voidWorld() { return Config.loaded() ? voidWorld.get() : ConfigCore.flag("worldgen.voidWorld", false); }

    public List<String> voidWorldDimensions() { return Config.loaded() ? List.copyOf(voidWorldDimensions.get()) : ConfigCore.strings("worldgen.voidWorldDimensions", List.of("minecraft:overworld")); }

    public boolean voidWorldDimensionsAreBlacklist() { return Config.loaded() ? voidWorldDimensionsAreBlacklist.get() : ConfigCore.flag("worldgen.voidWorldDimensionsAreBlacklist", false); }

    public boolean dragonFight() { return Config.loaded() ? dragonFight.get() : ConfigCore.flag("worldgen.dragonFight", true); }

    public String voidPlatformBlock() { return Config.loaded() ? voidPlatformBlock.get() : ConfigCore.text("worldgen.voidPlatformBlock", "minecraft:stone"); }

    public int voidPlatformHeight() { return Config.loaded() ? voidPlatformHeight.get() : 64; }

    public int voidPlatformSize() { return Config.loaded() ? voidPlatformSize.get() : 9; }

    public boolean retrogen() { return Config.loaded() ? retrogen.get() : ConfigCore.flag("worldgen.retrogen", false); }

    public boolean adoptExistingChunks() { return Config.loaded() ? adoptExistingChunks.get() : ConfigCore.flag("worldgen.adoptExistingChunks", false); }

    public String retrogenKey() { return Config.loaded() ? retrogenKey.get() : ConfigCore.text("worldgen.retrogenKey", "0000"); }

    public int retrogenChunksPerTick() { return Config.loaded() ? retrogenChunksPerTick.get() : 2; }

    public boolean blockOres() { return Config.loaded() ? blockOres.get() : ConfigCore.flag("worldgen.blockOres", false); }

    public boolean logBlockedOres() { return !Config.loaded() || logBlockedOres.get(); }

    public boolean logBlockedBiomes() { return Config.loaded() ? logBlockedBiomes.get() : ConfigCore.flag("worldgen.logBlockedBiomes", true); }

    public List<String> oreWhitelist() { return Config.loaded() ? List.copyOf(oreWhitelist.get()) : ConfigCore.strings("worldgen.oreWhitelist", List.of("minecraft")); }

    public List<String> prospectItems() { return Config.loaded() ? List.copyOf(prospectItems.get()) : List.of(); }

    public boolean prospectItemsAreBlacklist() { return Config.loaded() && prospectItemsAreBlacklist.get(); }

    public boolean prospectDrops() { return Config.loaded() && prospectDrops.get(); }

    public int prospectSlow() { return Config.loaded() ? prospectSlow.get() : 2; }

    public int prospectWear() { return Config.loaded() ? prospectWear.get() : 2; }

    public List<String> oreTypes() { return Config.loaded() ? List.copyOf(oreTypes.get()) : ConfigCore.strings("worldgen.oreTypes", List.of()); }

    public boolean oreTypesAreBlacklist() { return Config.loaded() ? oreTypesAreBlacklist.get() : ConfigCore.flag("worldgen.oreTypesAreBlacklist", true); }

    public List<String> blockOreDimensions() { return Config.loaded() ? List.copyOf(blockOreDimensions.get()) : ConfigCore.strings("worldgen.blockOreDimensions", List.of()); }

    public boolean blockOreDimensionsAreBlacklist() { return Config.loaded() ? blockOreDimensionsAreBlacklist.get() : ConfigCore.flag("worldgen.blockOreDimensionsAreBlacklist", false); }

    public boolean blockBiomes() { return Config.loaded() ? blockBiomes.get() : ConfigCore.flag("worldgen.blockBiomes", false); }

    public List<String> biomeWhitelist() { return Config.loaded() ? List.copyOf(biomeWhitelist.get()) : ConfigCore.strings("worldgen.biomeWhitelist", List.of("minecraft")); }

    public List<String> biomeNames() { return Config.loaded() ? List.copyOf(biomeNames.get()) : ConfigCore.strings("worldgen.biomeNames", List.of()); }

    public boolean biomeNamesAreBlacklist() { return Config.loaded() ? biomeNamesAreBlacklist.get() : ConfigCore.flag("worldgen.biomeNamesAreBlacklist", true); }

    public List<String> blockBiomeDimensions() { return Config.loaded() ? List.copyOf(blockBiomeDimensions.get()) : ConfigCore.strings("worldgen.blockBiomeDimensions", List.of("minecraft:overworld")); }

    public boolean blockBiomeDimensionsAreBlacklist() { return Config.loaded() ? blockBiomeDimensionsAreBlacklist.get() : ConfigCore.flag("worldgen.blockBiomeDimensionsAreBlacklist", false); }

    public float surfaceDayMonsterRate() { return Config.loaded() ? surfaceDayMonsterRate.get().floatValue() : 1.0F; }

    public float surfaceNightMonsterRate() { return Config.loaded() ? surfaceNightMonsterRate.get().floatValue() : 1.0F; }

    public float undergroundDayMonsterRate() { return Config.loaded() ? undergroundDayMonsterRate.get().floatValue() : 1.0F; }

    public float undergroundNightMonsterRate() { return Config.loaded() ? undergroundNightMonsterRate.get().floatValue() : 1.0F; }

    public int monsterCap() { return Config.loaded() ? monsterCap.get() : -1; }

    public int creatureCap() { return Config.loaded() ? creatureCap.get() : -1; }

    public int ambientCap() { return Config.loaded() ? ambientCap.get() : -1; }

    public int waterCreatureCap() { return Config.loaded() ? waterCreatureCap.get() : -1; }

    public int monsterSpawnLight() { return Config.loaded() ? monsterSpawnLight.get() : -1; }

    public int caveRegionPlainWeight() { return Config.loaded() ? caveRegionPlainWeight.get() : ConfigCore.number("worldgen.caveRegionPlainWeight", 4); }

    public int caveRegionCells() { return Config.loaded() ? caveRegionCells.get() : 128; }

    public int caveRegionCellsY() { return Config.loaded() ? caveRegionCellsY.get() : 64; }

    public List<String> structureSpacing() { return Config.loaded() ? List.copyOf(structureSpacing.get()) : ConfigCore.strings("worldgen.structureSpacing", List.of()); }

    public List<String> structureSeparation() { return Config.loaded() ? List.copyOf(structureSeparation.get()) : ConfigCore.strings("worldgen.structureSeparation", List.of()); }

    public List<String> structureMost() { return Config.loaded() ? List.copyOf(structureMost.get()) : ConfigCore.strings("worldgen.structureMost", List.of()); }

    public List<String> structureSpawners() { return Config.loaded() ? List.copyOf(structureSpawners.get()) : ConfigCore.strings("worldgen.structureSpawners", List.of()); }

    public List<String> structureMinDistanceFromSpawn() { return Config.loaded() ? List.copyOf(structureMinDistanceFromSpawn.get()) : ConfigCore.strings("worldgen.structureMinDistanceFromSpawn", List.of()); }

    public List<String> structureBiomes() { return Config.loaded() ? List.copyOf(structureBiomes.get()) : ConfigCore.strings("worldgen.structureBiomes", List.of()); }

    public List<String> structureBiomesAreBlacklist() { return Config.loaded() ? List.copyOf(structureBiomesAreBlacklist.get()) : ConfigCore.strings("worldgen.structureBiomesAreBlacklist", List.of()); }

    public List<String> structureSpawns() { return Config.loaded() ? List.copyOf(structureSpawns.get()) : ConfigCore.strings("worldgen.structureSpawns", List.of()); }

    public List<String> structureAt() { return Config.loaded() ? List.copyOf(structureAt.get()) : ConfigCore.strings("worldgen.structureAt", List.of()); }

    public List<String> structureAdaptation() { return Config.loaded() ? List.copyOf(structureAdaptation.get()) : ConfigCore.strings("worldgen.structureAdaptation", List.of()); }
}
