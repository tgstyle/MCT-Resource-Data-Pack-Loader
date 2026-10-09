package mctmods.resourcedatapackloader.util;

import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public abstract class ConfigWorldgenWorld {
    private final ModConfigSpec.BooleanValue load;
    private final ModConfigSpec.BooleanValue worldgenDebug;
    private final ModConfigSpec.ConfigValue<String> worldTemplate;
    private final ModConfigSpec.ConfigValue<String> worldSeed;
    private final ModConfigSpec.ConfigValue<String> worldName;
    private final ModConfigSpec.ConfigValue<String> worldGameMode;
    private final ModConfigSpec.BooleanValue worldLanCommands;
    private final ModConfigSpec.ConfigValue<String> worldForceGameMode;
    private final ModConfigSpec.ConfigValue<String> worldPvp;
    private final ModConfigSpec.ConfigValue<String> worldFlight;
    private final ModConfigSpec.IntValue worldSpawnProtection;
    private final ModConfigSpec.ConfigValue<String> worldNether;
    private final ModConfigSpec.ConfigValue<String> worldCommandBlocks;
    private final ModConfigSpec.IntValue worldIdleTimeout;
    private final ModConfigSpec.ConfigValue<String> worldMotd;
    private final ModConfigSpec.IntValue worldMaxSize;
    private final ModConfigSpec.ConfigValue<String> worldStructures;
    private final ModConfigSpec.ConfigValue<String> worldSpawnMonsters;
    private final ModConfigSpec.ConfigValue<String> worldSpawnAnimals;
    private final ModConfigSpec.ConfigValue<String> worldSpawnNpcs;
    private final ModConfigSpec.IntValue worldViewDistance;
    private final ModConfigSpec.IntValue worldSimulationDistance;
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
        worldLanCommands = builder.comment("Let a player opening a single player world to LAN turn commands on for everyone who joins. Off, the Allow Commands button on the Open to LAN screen is grayed out and stays off, and the world is opened to LAN without commands however it is asked for [Default=true]").define("worldLanCommands", true);
        worldForceGameMode = builder.comment("Whether a player who joins is put back in the server's game mode every time, the force-gamemode line of server.properties: true or false. A dedicated server writes it into server.properties. Empty leaves it as the server has it [Default=empty]").define("worldForceGameMode", "");
        worldPvp = builder.comment("Whether players can hurt each other, the pvp line of server.properties: true or false. A dedicated server writes it into server.properties, and a single player world takes it too. Empty leaves it as the server has it [Default=empty]").define("worldPvp", "");
        worldFlight = builder.comment("Whether a player flying in survival is left alone instead of kicked, the allow-flight line of server.properties: true or false. A dedicated server writes it into server.properties, and a single player world takes it too. Empty leaves it as the server has it [Default=empty]").define("worldFlight", "");
        worldSpawnProtection = builder.comment("How many blocks around the spawn point only operators may build in, the spawn-protection line of server.properties, 0 for none. Only a dedicated server protects its spawn. -1 leaves it as the server has it [Default=-1]").defineInRange("worldSpawnProtection", -1, -1, MinecraftServer.ABSOLUTE_MAX_WORLD_SIZE);
        worldNether = builder.comment("Whether the Nether can be entered, the allow-nether line of server.properties: true or false. A dedicated server writes it into server.properties, and a single player world takes it too. Empty leaves it as the server has it [Default=empty]").define("worldNether", "");
        worldCommandBlocks = builder.comment("Whether command blocks run, the enable-command-block line of server.properties: true or false. A dedicated server writes it into server.properties, and false turns them off in a single player world too. Empty leaves it as the server has it [Default=empty]").define("worldCommandBlocks", "");
        worldIdleTimeout = builder.comment("How many minutes a player may stand idle before being kicked, the player-idle-timeout line of server.properties, 0 for never. A dedicated server writes it into server.properties, and a single player world opened to LAN takes it too. -1 leaves it as the server has it [Default=-1]").defineInRange("worldIdleTimeout", -1, -1, 1000000);
        worldMotd = builder.comment("The line shown under the server's name in the server list, the motd line of server.properties. A dedicated server writes it into server.properties, and a single player world opened to LAN shows it too. Empty leaves it as the server has it [Default=empty]").define("worldMotd", "");
        worldMaxSize = builder.comment("The farthest out, in blocks from the middle, a world border may ever reach, the max-world-size line of server.properties. -1 leaves it as the server has it [Default=-1]").defineInRange("worldMaxSize", -1, -1, MinecraftServer.ABSOLUTE_MAX_WORLD_SIZE);
        worldStructures = builder.comment("Whether a new world generates structures, the generate-structures line of server.properties and the Generate Structures button on the world screen: true or false. Only applied to a world as it is created. Empty leaves it as chosen [Default=empty]").define("worldStructures", "");
        worldSpawnMonsters = builder.comment("Whether hostile mobs spawn, the spawn-monsters line of server.properties: true or false. A dedicated server writes it into server.properties, and false stops them in a single player world too. Empty leaves it as the server has it [Default=empty]").define("worldSpawnMonsters", "");
        worldSpawnAnimals = builder.comment("Whether animals spawn, the spawn-animals line of server.properties: true or false. A dedicated server writes it into server.properties, and a single player world takes it too. Empty leaves it as the server has it [Default=empty]").define("worldSpawnAnimals", "");
        worldSpawnNpcs = builder.comment("Whether villagers spawn, the spawn-npcs line of server.properties: true or false. A dedicated server writes it into server.properties, and a single player world takes it too. Empty leaves it as the server has it [Default=empty]").define("worldSpawnNpcs", "");
        worldViewDistance = builder.comment("How many chunks out a dedicated server sends the world to each player, the view-distance line of server.properties. A single player world follows the render distance instead. -1 leaves it as the server has it [Default=-1]").defineInRange("worldViewDistance", -1, -1, 32);
        worldSimulationDistance = builder.comment("How many chunks out a dedicated server keeps the world ticking around each player, the simulation-distance line of server.properties. A single player world follows its own setting instead. -1 leaves it as the server has it [Default=-1]").defineInRange("worldSimulationDistance", -1, -1, 32);
        worldName = builder.comment("What a new world is called when the screen for making one opens. Empty leaves it as the game names it [Default=empty]").define("worldName", "");
        worldType = builder.comment("The world type the shaped world is built on, one of default, largebiomes, amplified or flat, with the 1.12.2 names customized and default_1_1 read as default; flat is a superflat overworld built from the generatorOptions layers, with the pack's cities on it. The shape below (heights, deep stone, sea level, bedrock, void) is generated as a world preset of its own, listed under World Type on the world screen and chosen there whatever was picked. Empty builds on default [Default=empty]").define("worldType", "");
        worldTypeExceptions = builder.comment("World types a player picks that the generated preset leaves alone, such as flat or debug_all_block_states. Empty means every choice is replaced [Default=[flat, debug_all_block_states]]").defineListAllowEmpty("worldTypeExceptions", List.of("flat", "debug_all_block_states"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
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
        worldDifficulty = builder.comment("Lock the difficulty, one of peaceful, easy, normal or hard. A bare difficulty covers every dimension, and an entry written as dimension=difficulty, such as minecraft:the_nether=hard, covers that dimension alone and wins over the bare one. The world's own setting is left as it was and comes back when the entry is removed. Empty leaves it as chosen [Default=empty]").defineListAllowEmpty("worldDifficulty", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        flatBedrock = builder.comment("Replace the jagged bedrock at the bottom of the world with flat layers. Only affects new chunks unless flatBedrockRetrogen is on. Cannot be undone [Default=false]").define("flatBedrock", false);
        flatBedrockDimensions = builder.comment("Dimensions to flatten bedrock in, by id such as minecraft:the_nether. Leave empty for every dimension [Default=[minecraft:overworld]]").defineListAllowEmpty("flatBedrockDimensions", List.of("minecraft:overworld"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        flatBedrockDimensionsAreBlacklist = builder.comment("On, flattening skips these dimensions. Off, it applies only to them [Default=false]").define("flatBedrockDimensionsAreBlacklist", false);
        bedrockLayers = builder.comment("How many layers of bedrock to leave at the bottom [Default=1]").defineInRange("bedrockLayers", 1, 1, 5);
        flatBedrockBiomes = builder.comment("Biomes to flatten bedrock in, by id such as minecraft:birch_forest. Empty means every biome; elsewhere the bedrock stays as the game makes it").defineListAllowEmpty("flatBedrockBiomes", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        flatBedrockBiomesAreBlacklist = builder.comment("On, flattening skips these biomes. Off, it applies only to them [Default=false]").define("flatBedrockBiomesAreBlacklist", false);
        flatBedrockRoof = builder.comment("Flatten the bedrock ceiling too, where a dimension has one, such as the Nether roof [Default=false]").define("flatBedrockRoof", false);
        flatBedrockFiller = builder.comment("What replaces the bedrock that is removed. Empty picks the block the dimension is made of: stone, netherrack, end stone [Default=]").define("flatBedrockFiller", "");
        flatBedrockFillers = builder.comment("A filler per dimension, written dimension=block, such as minecraft:the_nether=minecraft:netherrack. Overrides flatBedrockFiller for the dimensions listed [Default=[minecraft:the_nether=minecraft:netherrack, minecraft:the_end=minecraft:end_stone]]").defineListAllowEmpty("flatBedrockFillers", List.of("minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        flatBedrockBiomeTypes = builder.comment("Biome types to flatten bedrock in, alongside flatBedrockBiomes, by biome tag such as minecraft:is_ocean or by 1.12.2 type name such as OCEAN").defineListAllowEmpty("flatBedrockBiomeTypes", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        flatBedrockRetrogen = builder.comment("Flatten the bedrock in chunks that already exist, not only new ones, while retrogen is on. Each chunk is done once and remembers it [Default=false]").define("flatBedrockRetrogen", false);
        flatBedrockRetrogenKey = builder.comment("Change this to make every chunk eligible for bedrock flattening again [Default=0000]").define("flatBedrockRetrogenKey", "0000");
        voidWorld = builder.comment("Generate the listed dimensions as empty space with a platform at the spawn point and nothing living, through the generated preset [Default=false]").define("voidWorld", false);
        voidWorldDimensions = builder.comment("Which dimensions are made void, by id. Empty means none, or every dimension when voidWorldDimensionsAreBlacklist is on [Default=[minecraft:overworld]]").defineListAllowEmpty("voidWorldDimensions", List.of("minecraft:overworld"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
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
        oreWhitelist = builder.comment("Mod ids allowed to generate ores while blockOres is on. Ores a pack defines belong to that pack's namespace [Default=[minecraft]]").defineListAllowEmpty("oreWhitelist", List.of("minecraft"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        prospectItems = builder.comment("Items that prospect for vein shaped worldgen entries when a sneaking player breaks a block with one, as item=entry|entry[,radius in chunks] or item=*[,radius], e.g. minecraft:compass=iron_vein|coal_seam or mypack:rod=*,12. The reading names the ore and a compass direction [Default=[]]").defineListAllowEmpty("prospectItems", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        prospectItemsAreBlacklist = builder.comment("On, the entries named after an item in prospectItems are the ones it does NOT read, and every other vein shaped entry is [Default=false]").define("prospectItemsAreBlacklist", false);
        prospectDrops = builder.comment("Whether a block broken in prospecting mode drops anything. Off, the sample is destroyed: no drops, no experience [Default=false]").define("prospectDrops", false);
        prospectSlow = builder.comment("How many times slower a block breaks in prospecting mode [Default=2]").defineInRange("prospectSlow", 2, 1, 100);
        prospectWear = builder.comment("How much durability a prospecting break costs the item, at least 2 [Default=2]").defineInRange("prospectWear", 2, 2, 1000);
        oreTypes = builder.comment("Ore types this applies to, whoever generates them and whatever the whitelist says. Known types: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM for any other ore [Default=[]]").defineListAllowEmpty("oreTypes", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        oreTypesAreBlacklist = builder.comment("On, oreTypes are blocked. Off, only oreTypes generate [Default=true]").define("oreTypesAreBlacklist", true);
        blockOreDimensions = builder.comment("Dimensions ore blocking applies to, by id such as minecraft:the_nether or a pack's own. Empty means every dimension [Default=[]]").defineListAllowEmpty("blockOreDimensions", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockOreDimensionsAreBlacklist = builder.comment("Treat blockOreDimensions as the dimensions to leave alone instead [Default=false]").define("blockOreDimensionsAreBlacklist", false);
        blockBiomes = builder.comment("Stop every biome from generating except the mods in biomeWhitelist. Blocked biomes become the void biome, or what the world template's roles and fallback name. Blocking every biome makes the overworld a void world [Default=false]").define("blockBiomes", false);
        biomeWhitelist = builder.comment("Mod ids whose biomes still generate while blockBiomes is on. A pack biome uses the pack's namespace [Default=[minecraft]]").defineListAllowEmpty("biomeWhitelist", List.of("minecraft"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        biomeNames = builder.comment("Biomes this applies to, whoever owns them and whatever the whitelist says, by id such as minecraft:birch_forest [Default=[]]").defineListAllowEmpty("biomeNames", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        biomeNamesAreBlacklist = builder.comment("On, biomeNames are blocked. Off, only biomeNames generate [Default=true]").define("biomeNamesAreBlacklist", true);
        blockBiomeDimensions = builder.comment("Dimensions biome blocking applies to, by id. Empty means every dimension whose biomes are placed by climate, the overworld and the nether [Default=[minecraft:overworld]]").defineListAllowEmpty("blockBiomeDimensions", List.of("minecraft:overworld"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
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
        structureSpacing = builder.comment("How far apart vanilla structures are seeded, in chunks, as structure=chunks entries: the 1.12.2 names temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities and villages, or any structure set id such as pillager_outposts. For mineshafts the number is one chunk in that many; for strongholds it is the ring distance [Default=[]]").defineListAllowEmpty("structureSpacing", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureSeparation = builder.comment("The closest two of a structure may be, in chunks, as structure=chunks entries; for strongholds it is the ring spread [Default=[]]").defineListAllowEmpty("structureSeparation", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureMost = builder.comment("The most of a structure a dimension may hold, as structure=count entries such as villages=100: once that many have been founded no chunk founds another, chunks pinned with structureAt aside. 0 or an absent entry sets no ceiling [Default=[]]").defineListAllowEmpty("structureMost", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureSpawners = builder.comment("What the mob spawner inside a vanilla structure spawns, as structure=namespace:entity entries, comma separated for a random pick. Only dungeons, mineshafts, netherbridges and strongholds build one; spawners other mods place are left alone [Default=[]]").defineListAllowEmpty("structureSpawners", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureMinDistanceFromSpawn = builder.comment("How far from the world spawn a structure starts, in blocks, as structure=blocks entries. Measured from the world's spawn point; while a new world is still choosing one, from the pack's worldSpawn when one is set, else from the world origin [Default=[]]").defineListAllowEmpty("structureMinDistanceFromSpawn", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureBiomes = builder.comment("Where a structure may generate, as structure=biome,biome entries naming biome ids or biome types such as SANDY [Default=[]]").defineListAllowEmpty("structureBiomes", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureBiomesAreBlacklist = builder.comment("Direction of the biome lists, written as structure=true or structure=false, one per line. True takes the listed biomes away, false makes them the only ones [Default=false]").defineListAllowEmpty("structureBiomesAreBlacklist", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureSpawns = builder.comment("The mobs a structure spawns whatever the biome says, as structure=namespace:entity:weight:least:most entries, comma separated; an empty list after the = spawns nothing [Default=[]]").defineListAllowEmpty("structureSpawns", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureAt = builder.comment("Structures pinned to exact spots, as structure=x,z entries in block coordinates, one per wanted instance. A pinned structure generates in that chunk and nowhere else. city=x,z seats the center district of the city whose region holds that spot [Default=[]]").defineListAllowEmpty("structureAt", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        structureAdaptation = builder.comment("How the terrain adapts to a structure, as structure=mode entries with the modes none, bury, beard_thin, beard_box and encapsulate [Default=[]]").defineListAllowEmpty("structureAdaptation", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
    }

    public boolean loadOff() { return !ConfigCore.flag(load); }

    public boolean worldgenDebug() { return ConfigCore.flag(worldgenDebug); }

    public String worldTemplate() { return ConfigCore.text(worldTemplate); }

    public String worldSeed() { return ConfigCore.text(worldSeed); }

    public String worldName() { return ConfigCore.text(worldName); }

    public String worldGameMode() { return ConfigCore.text(worldGameMode); }

    public boolean worldLanCommands() { return ConfigCore.flag(worldLanCommands); }

    public String worldForceGameMode() { return ConfigCore.text(worldForceGameMode); }

    public String worldPvp() { return ConfigCore.text(worldPvp); }

    public String worldFlight() { return ConfigCore.text(worldFlight); }

    public int worldSpawnProtection() { return ConfigCore.number(worldSpawnProtection); }

    public String worldNether() { return ConfigCore.text(worldNether); }

    public String worldCommandBlocks() { return ConfigCore.text(worldCommandBlocks); }

    public int worldIdleTimeout() { return ConfigCore.number(worldIdleTimeout); }

    public String worldMotd() { return ConfigCore.text(worldMotd); }

    public int worldMaxSize() { return ConfigCore.number(worldMaxSize); }

    public String worldStructures() { return ConfigCore.text(worldStructures); }

    public String worldSpawnMonsters() { return ConfigCore.text(worldSpawnMonsters); }

    public String worldSpawnAnimals() { return ConfigCore.text(worldSpawnAnimals); }

    public String worldSpawnNpcs() { return ConfigCore.text(worldSpawnNpcs); }

    public int worldViewDistance() { return ConfigCore.number(worldViewDistance); }

    public int worldSimulationDistance() { return ConfigCore.number(worldSimulationDistance); }

    public String worldType() { return ConfigCore.text(worldType); }

    public List<String> worldTypeExceptions() { return ConfigCore.strings(worldTypeExceptions); }

    public boolean tellWorldType() { return ConfigCore.flag(tellWorldType); }

    public String generatorOptions() { return ConfigCore.text(generatorOptions); }

    public int worldMinHeight() { return ConfigCore.number(worldMinHeight); }

    public int worldMaxHeight() { return ConfigCore.number(worldMaxHeight); }

    public String deepStone() { return ConfigCore.text(deepStone); }

    public String noiseCaves() { return ConfigCore.text(noiseCaves); }

    public String worldSpawn() { return ConfigCore.text(worldSpawn); }

    public int worldBorder() { return ConfigCore.number(worldBorder); }

    public int worldBorderLimit() { return ConfigCore.number(worldBorderLimit); }

    public int worldTime() { return ConfigCore.number(worldTime); }

    public List<String> worldDifficulty() { return ConfigCore.strings(worldDifficulty); }

    public boolean flatBedrock() { return ConfigCore.flag(flatBedrock); }

    public List<String> flatBedrockDimensions() { return ConfigCore.strings(flatBedrockDimensions); }

    public boolean flatBedrockDimensionsAreBlacklist() { return ConfigCore.flag(flatBedrockDimensionsAreBlacklist); }

    public int bedrockLayers() { return ConfigCore.number(bedrockLayers); }

    public List<String> flatBedrockBiomes() { return ConfigCore.strings(flatBedrockBiomes); }

    public boolean flatBedrockBiomesAreBlacklist() { return ConfigCore.flag(flatBedrockBiomesAreBlacklist); }

    public boolean flatBedrockRoof() { return ConfigCore.flag(flatBedrockRoof); }

    public String flatBedrockFiller() { return ConfigCore.text(flatBedrockFiller); }

    public List<String> flatBedrockFillers() { return ConfigCore.strings(flatBedrockFillers); }

    public List<String> flatBedrockBiomeTypes() { return ConfigCore.strings(flatBedrockBiomeTypes); }

    public boolean flatBedrockRetrogen() { return ConfigCore.flag(flatBedrockRetrogen); }

    public String flatBedrockRetrogenKey() { return ConfigCore.text(flatBedrockRetrogenKey); }

    public boolean voidWorld() { return ConfigCore.flag(voidWorld); }

    public List<String> voidWorldDimensions() { return ConfigCore.strings(voidWorldDimensions); }

    public boolean voidWorldDimensionsAreBlacklist() { return ConfigCore.flag(voidWorldDimensionsAreBlacklist); }

    public boolean dragonFight() { return ConfigCore.flag(dragonFight); }

    public String voidPlatformBlock() { return ConfigCore.text(voidPlatformBlock); }

    public int voidPlatformHeight() { return ConfigCore.number(voidPlatformHeight); }

    public int voidPlatformSize() { return ConfigCore.number(voidPlatformSize); }

    public boolean retrogen() { return ConfigCore.flag(retrogen); }

    public boolean adoptExistingChunks() { return ConfigCore.flag(adoptExistingChunks); }

    public String retrogenKey() { return ConfigCore.text(retrogenKey); }

    public int retrogenChunksPerTick() { return ConfigCore.number(retrogenChunksPerTick); }

    public boolean blockOres() { return ConfigCore.flag(blockOres); }

    public boolean logBlockedOres() { return ConfigCore.flag(logBlockedOres); }

    public boolean logBlockedBiomes() { return ConfigCore.flag(logBlockedBiomes); }

    public List<String> oreWhitelist() { return ConfigCore.strings(oreWhitelist); }

    public List<String> prospectItems() { return ConfigCore.strings(prospectItems); }

    public boolean prospectItemsAreBlacklist() { return ConfigCore.flag(prospectItemsAreBlacklist); }

    public boolean prospectDrops() { return ConfigCore.flag(prospectDrops); }

    public int prospectSlow() { return ConfigCore.number(prospectSlow); }

    public int prospectWear() { return ConfigCore.number(prospectWear); }

    public List<String> oreTypes() { return ConfigCore.strings(oreTypes); }

    public boolean oreTypesAreBlacklist() { return ConfigCore.flag(oreTypesAreBlacklist); }

    public List<String> blockOreDimensions() { return ConfigCore.strings(blockOreDimensions); }

    public boolean blockOreDimensionsAreBlacklist() { return ConfigCore.flag(blockOreDimensionsAreBlacklist); }

    public boolean blockBiomes() { return ConfigCore.flag(blockBiomes); }

    public List<String> biomeWhitelist() { return ConfigCore.strings(biomeWhitelist); }

    public List<String> biomeNames() { return ConfigCore.strings(biomeNames); }

    public boolean biomeNamesAreBlacklist() { return ConfigCore.flag(biomeNamesAreBlacklist); }

    public List<String> blockBiomeDimensions() { return ConfigCore.strings(blockBiomeDimensions); }

    public boolean blockBiomeDimensionsAreBlacklist() { return ConfigCore.flag(blockBiomeDimensionsAreBlacklist); }

    public float surfaceDayMonsterRate() { return ConfigCore.decimal(surfaceDayMonsterRate); }

    public float surfaceNightMonsterRate() { return ConfigCore.decimal(surfaceNightMonsterRate); }

    public float undergroundDayMonsterRate() { return ConfigCore.decimal(undergroundDayMonsterRate); }

    public float undergroundNightMonsterRate() { return ConfigCore.decimal(undergroundNightMonsterRate); }

    public int monsterCap() { return ConfigCore.number(monsterCap); }

    public int creatureCap() { return ConfigCore.number(creatureCap); }

    public int ambientCap() { return ConfigCore.number(ambientCap); }

    public int waterCreatureCap() { return ConfigCore.number(waterCreatureCap); }

    public int monsterSpawnLight() { return ConfigCore.number(monsterSpawnLight); }

    public int caveRegionPlainWeight() { return ConfigCore.number(caveRegionPlainWeight); }

    public int caveRegionCells() { return ConfigCore.number(caveRegionCells); }

    public int caveRegionCellsY() { return ConfigCore.number(caveRegionCellsY); }

    public List<String> structureSpacing() { return ConfigCore.strings(structureSpacing); }

    public List<String> structureSeparation() { return ConfigCore.strings(structureSeparation); }

    public List<String> structureMost() { return ConfigCore.strings(structureMost); }

    public List<String> structureSpawners() { return ConfigCore.strings(structureSpawners); }

    public List<String> structureMinDistanceFromSpawn() { return ConfigCore.strings(structureMinDistanceFromSpawn); }

    public List<String> structureBiomes() { return ConfigCore.strings(structureBiomes); }

    public List<String> structureBiomesAreBlacklist() { return ConfigCore.strings(structureBiomesAreBlacklist); }

    public List<String> structureSpawns() { return ConfigCore.strings(structureSpawns); }

    public List<String> structureAt() { return ConfigCore.strings(structureAt); }

    public List<String> structureAdaptation() { return ConfigCore.strings(structureAdaptation); }
}
