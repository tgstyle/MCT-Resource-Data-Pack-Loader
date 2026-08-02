package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.pack.PackManager;

@net.minecraftforge.common.config.Config(modid = MCTMixin.MIXIN_ID, name = "mct_resourcedatapackloader_mixin")
public class Config {
    @net.minecraftforge.common.config.Config.Comment("Who decides each group of settings. 'default' lets the pack decide and falls back to the values here, 'global' makes the values here win and ignores the pack, 'off' disables the group entirely and no pack can turn it back on")
    public static Control control = new Control();
    @net.minecraftforge.common.config.Config.Comment("How pack folders are found and served")
    public static Packs packs = new Packs();
    @net.minecraftforge.common.config.Config.Comment("Blocks, items, fluids and the rest of what a pack can define")
    public static Content content = new Content();
    @net.minecraftforge.common.config.Config.Comment("Crafting, smelting and the files that change them")
    public static Recipes recipes = new Recipes();
    @net.minecraftforge.common.config.Config.Comment("Loot, advancements, functions and registry names")
    public static Data data = new Data();
    @net.minecraftforge.common.config.Config.Comment("What generates in the world, and what is stopped from generating")
    public static Worldgen worldgen = new Worldgen();
    @net.minecraftforge.common.config.Config.Comment("Working around other mods")
    public static Compat compat = new Compat();
    @net.minecraftforge.common.config.Config.Comment("What the game shows while a world loads")
    public static Client client = new Client();
    @net.minecraftforge.common.config.Config.Comment("Small changes to how vanilla behaves")
    public static Tweaks tweaks = new Tweaks();

    public static class Control {
        @net.minecraftforge.common.config.Config.Comment("Ore blocking and the ore whitelists [default|global|off]")
        public String ores = "default";
        @net.minecraftforge.common.config.Config.Comment("Biome blocking, the biome whitelists and the world template [default|global|off]")
        public String biomes = "default";
        @net.minecraftforge.common.config.Config.Comment("Blocking other mods' own world generators [default|global|off]")
        public String generators = "default";
        @net.minecraftforge.common.config.Config.Comment("Caves, ravines, villages and the rest of the structure switches, where structures are placed, and which vanilla village pieces are used [default|global|off]")
        public String structures = "default";
        @net.minecraftforge.common.config.Config.Comment("Mob caps and the hostile spawn rates [default|global|off]")
        public String spawning = "default";
        @net.minecraftforge.common.config.Config.Comment("Flat bedrock and its biome and dimension lists [default|global|off]")
        public String bedrock = "default";
        @net.minecraftforge.common.config.Config.Comment("Void world generation and its platform [default|global|off]")
        public String voidWorld = "default";
        @net.minecraftforge.common.config.Config.Comment("Recipe and furnace blocking and their whitelists [default|global|off]")
        public String recipes = "default";
        @net.minecraftforge.common.config.Config.Comment("The overworld's terrain shape, set through generatorOptions [default|global|off]")
        public String terrain = "default";
        @net.minecraftforge.common.config.Config.Comment("Replacing blocks in chunks that already exist [default|global|off]")
        public String replacements = "default";
        @net.minecraftforge.common.config.Config.Comment("Village plots from packs [default|global|off]")
        public String villages = "default";
    }

    public static class Packs {
        @net.minecraftforge.common.config.Config.Comment("Folder packs are loaded from, relative to the .minecraft directory. An absolute path also works. Requires a restart [Default=rdploader]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public String rootDirectory = PackManager.ROOT_DIRECTORY;
        @net.minecraftforge.common.config.Config.Comment("Insert the asset pack above the player's selected resource packs. A pack named RDPLO... always overrides, RDPLN... never does [Default=true]")
        public boolean overrideResourcePacks = true;
        @net.minecraftforge.common.config.Config.Comment("Warn when a file only matches because the filesystem is case-insensitive. Such packs break on Linux [Default=true]")
        public boolean warnOnCaseMismatch = true;
        @net.minecraftforge.common.config.Config.Comment("Log every pack found and how many files of each type it provides [Default=false]")
        public boolean logContents = false;
        @net.minecraftforge.common.config.Config.Comment("Log a stack trace the first time a file with a '#' in its name is requested, naming whatever asked for it [Default=false]")
        public boolean traceUnresolvedVariables = false;
    }

    public static class Content {
        @net.minecraftforge.common.config.Config.Comment("Register the blocks and items described by blocks/*.json and items/*.json in packs. Turning this off leaves worlds containing them with missing blocks. Requires a restart [Default=true]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean load = true;
        @net.minecraftforge.common.config.Config.Comment("Register the sound events named by sounds/*.json, so packs can ship their own audio [Default=true]")
        public boolean sounds = true;
        @net.minecraftforge.common.config.Config.Comment("Apply fuels/*.json files, which give items a furnace burn time [Default=true]")
        public boolean fuels = true;
        @net.minecraftforge.common.config.Config.Comment("Apply oredict/*.json files, which add ore dictionary names to items that already exist [Default=true]")
        public boolean oreDictionary = true;
        @net.minecraftforge.common.config.Config.Comment("Register the potion effects and potion types described by potions/*.json and potion_types/*.json in packs. Requires a restart [Default=true]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean potions = true;
        @net.minecraftforge.common.config.Config.Comment("Apply brewing/*.json files, which add brewing stand recipes [Default=true]")
        public boolean brewing = true;
        @net.minecraftforge.common.config.Config.Comment("Register the villager professions described by villagers/*.json and apply the trades in trades/*.json. Requires a restart [Default=true]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean villagers = true;
        @net.minecraftforge.common.config.Config.Comment("Register the dimensions described by dimensions/*.json in packs. Turning this off leaves worlds that contain them unable to load those dimensions. Requires a restart [Default=true]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean dimensions = true;
        @net.minecraftforge.common.config.Config.Comment("How tall vanilla sugar cane grows. Vanilla is 3. Pack defined cane blocks use their own growth section and ignore this. Skipped when Universal Tweaks is installed [Default=3]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 255)
        public int caneMaxHeight = 3;
        @net.minecraftforge.common.config.Config.Comment("The same for vanilla cactus [Default=3]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 255)
        public int cactusMaxHeight = 3;
        @net.minecraftforge.common.config.Config.Comment("Let a shovel turn blocks marked behavesAs path into a path, and revert a path while sneaking [Default=true]")
        public boolean shovelPaths = true;
        @net.minecraftforge.common.config.Config.Comment("What a shovel turns those blocks into. Empty uses grass path")
        public String shovelPathBecomes = "";
        @net.minecraftforge.common.config.Config.Comment("What sneaking with a shovel turns a path back into. Empty uses dirt")
        public String shovelPathReverts = "";
        @net.minecraftforge.common.config.Config.Comment("Let a hoe till blocks marked behavesAs till [Default=true]")
        public boolean hoeTilling = true;
        @net.minecraftforge.common.config.Config.Comment("What a hoe turns those blocks into. Empty uses farmland")
        public String hoeTillsInto = "";
        @net.minecraftforge.common.config.Config.Comment("Register the biomes described by biomes/*.json in packs and place them into world generation. Requires a restart [Default=true]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean biomes = true;
        @net.minecraftforge.common.config.Config.Comment("Register the village plots described by villages/*.json in packs so villages can build them. Requires a restart [Default=true]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean villages = true;
        @net.minecraftforge.common.config.Config.Comment("Register the entity variants described by entities/*.json in packs. Requires a restart [Default=true]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean entities = true;
    }

    public static class Recipes {
        @net.minecraftforge.common.config.Config.Comment("Apply furnace/*.json files, which add and remove furnace smelting recipes [Default=true]")
        public boolean furnace = true;
        @net.minecraftforge.common.config.Config.Comment("Apply recipe_removals/*.json files, which delete crafting recipes by name, namespace or output [Default=true]")
        public boolean removals = true;
        @net.minecraftforge.common.config.Config.Comment("Stop this mod from replacing or adding recipes. Only needed if another coremod conflicts over recipe loading [Default=false]")
        public boolean disableOverrides = false;
        @net.minecraftforge.common.config.Config.Comment("Skip recipes that use an item which is not registered, instead of letting them fail. The count is logged once [Default=true]")
        public boolean skipMissingItems = true;
        @net.minecraftforge.common.config.Config.Comment("Let advancements load when they refer to a recipe that no longer exists. The advancement works but never unlocks that recipe [Default=true]")
        public boolean tolerateMissingInAdvancements = true;
        @net.minecraftforge.common.config.Config.Comment("Remove every crafting recipe, keeping only the mods in recipeWhitelist. Include your pack's namespace to keep its own recipes [Default=false]")
        public boolean blockRecipes = false;
        @net.minecraftforge.common.config.Config.Comment("Mod ids whose crafting recipes survive while blockRecipes is on. Include your pack's namespace to keep its own recipes")
        public String[] recipeWhitelist = { "minecraft" };
        @net.minecraftforge.common.config.Config.Comment("Mod ids whose crafting recipes are removed outright, whoever they belong to and whatever the whitelist says. One per line")
        public String[] blockedRecipeMods = {};
        @net.minecraftforge.common.config.Config.Comment("What the mod id is read from when blocking crafting recipes. 'recipe' uses the recipe's own name, 'output' uses the item it makes, 'both' blocks if either matches and spares if either is whitelisted [Default=recipe]")
        public String recipeMatch = "recipe";
        @net.minecraftforge.common.config.Config.Comment("Remove every furnace recipe, keeping only the mods in furnaceWhitelist. The mod is read from the item produced [Default=false]")
        public boolean blockFurnaceRecipes = false;
        @net.minecraftforge.common.config.Config.Comment("Mod ids whose furnace recipes survive while blockFurnaceRecipes is on. Include your pack's namespace to keep its own recipes. CraftTweaker and GroovyScript additions always survive")
        public String[] furnaceWhitelist = { "minecraft" };
        @net.minecraftforge.common.config.Config.Comment("Mod ids whose furnace recipes are removed outright, whatever the whitelist says. One per line")
        public String[] blockedFurnaceMods = {};
        @net.minecraftforge.common.config.Config.Comment("Log a per mod count of what was blocked, so you can see what to whitelist [Default=true]")
        public boolean logBlockedRecipes = true;
    }

    public static class Data {
        @net.minecraftforge.common.config.Config.Comment("Apply loot_injections/*.json files, which add pools to loot tables that already exist instead of replacing the whole table [Default=true]")
        public boolean lootInjections = true;
        @net.minecraftforge.common.config.Config.Comment("Load .mcfunction files from packs, so they work in every world. A function saved in the world still wins [Default=true]")
        public boolean functions = true;
        @net.minecraftforge.common.config.Config.Comment("Apply registry_remap files, which rename a registry entry so worlds saved before the rename keep their blocks and items instead of losing them [Default=true]")
        public boolean registryRemaps = true;
    }

    public static class Worldgen {
        @net.minecraftforge.common.config.Config.Comment("Generate the ore veins described by worldgen/*.json in new chunks. Existing chunks are not changed [Default=true]")
        public boolean load = true;
        @net.minecraftforge.common.config.Config.Comment("Catch existing chunks up on worldgen entries with \"retrogen\": true. Off, chunks that already exist are left alone. Chunks are marked as they generate either way, so turning this on later only touches chunks older than the pack [Default=false]")
        public boolean retrogen = false;
        @net.minecraftforge.common.config.Config.Comment("Treat chunks that already exist as if this pack generated them, marking them instead of leaving them for retrogen. Turn this on when replacing a mod that already generated the same ore, such as CoFH World, so retrogen never doubles it. Worldgen entries added later still retrogen into them [Default=false]")
        public boolean adoptExistingChunks = false;
        @net.minecraftforge.common.config.Config.Comment("Change this to make every chunk eligible for retrogen again, for every worldgen entry. New veins are added on top of what is already there [Default=0000]")
        public String retrogenKey = "0000";
        @net.minecraftforge.common.config.Config.Comment("How many already generated chunks to catch up per tick. Higher is faster but stutters more [Default=2]")
        public int retrogenChunksPerTick = 2;
        @net.minecraftforge.common.config.Config.Comment("How often hostile mobs spawn underground during the day, where the sky cannot be seen. 0 stops them, 1 is vanilla, above 1 forces spawns vanilla would refuse [Default=1.0]")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 4.0D)
        public float undergroundDayMonsterRate = 1.0F;
        @net.minecraftforge.common.config.Config.Comment("The same for underground at night [Default=1.0]")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 4.0D)
        public float undergroundNightMonsterRate = 1.0F;
        @net.minecraftforge.common.config.Config.Comment("The same for the surface during the day, where mobs normally burn anyway [Default=1.0]")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 4.0D)
        public float surfaceDayMonsterRate = 1.0F;
        @net.minecraftforge.common.config.Config.Comment("The same for the surface at night [Default=1.0]")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0.0D, max = 4.0D)
        public float surfaceNightMonsterRate = 1.0F;
        @net.minecraftforge.common.config.Config.Comment("How many hostile mobs may be loaded at once across the world, before the count is scaled by how many chunks are loaded. Vanilla is 70. Lower means quieter nights, higher means more pressure. -1 leaves it alone [Default=-1]")
        @net.minecraftforge.common.config.Config.RangeInt(min = -1, max = 1000)
        public int monsterCap = -1;
        @net.minecraftforge.common.config.Config.Comment("The same cap for passive animals. Vanilla is 10. -1 leaves it alone [Default=-1]")
        @net.minecraftforge.common.config.Config.RangeInt(min = -1, max = 1000)
        public int creatureCap = -1;
        @net.minecraftforge.common.config.Config.Comment("The same cap for ambient mobs such as bats. Vanilla is 15. -1 leaves it alone [Default=-1]")
        @net.minecraftforge.common.config.Config.RangeInt(min = -1, max = 1000)
        public int ambientCap = -1;
        @net.minecraftforge.common.config.Config.Comment("The same cap for water mobs such as squid. Vanilla is 5. -1 leaves it alone [Default=-1]")
        @net.minecraftforge.common.config.Config.RangeInt(min = -1, max = 1000)
        public int waterCreatureCap = -1;
        @net.minecraftforge.common.config.Config.Comment("The overworld's terrain shape, in the same format the customized world type writes. Sets sea level, lava oceans and the terrain noise. Only applied to a world as it is created, so worlds that already exist are left alone [Default=empty]")
        public String generatorOptions = "";
        @net.minecraftforge.common.config.Config.Comment("Which world types the terrain settings are given to, by name, such as default, customized, biomesop or realistic. Empty means every world type [Default={}]")
        public String[] terrainWorldTypes = {};
        @net.minecraftforge.common.config.Config.Comment("Treat terrainWorldTypes as the world types to leave alone instead [Default=false]")
        public boolean terrainWorldTypesAreBlacklist = false;
        @net.minecraftforge.common.config.Config.Comment("Generate mod CoFH World files through this mod when CoFH World is missing. Translating them into a pack is the supported way [Default=false]")
        public boolean readCofhWorldFiles = false;
        @net.minecraftforge.common.config.Config.Comment("Stop every other mod from generating anything through its own world generators, which is how mods add things Forge's ore and decoration events never see, such as Tinkers' slime islands. Only the mods in generatorWhitelist still generate. This mod's own pack generation is never blocked [Default=false]")
        public boolean blockWorldGenerators = false;
        @net.minecraftforge.common.config.Config.Comment("Mod ids allowed to run their own world generators while blockWorldGenerators is on")
        public String[] generatorWhitelist = { "minecraft" };
        @net.minecraftforge.common.config.Config.Comment("Mod ids, or parts of a generator class name such as slimeisland, blocked outright whatever the whitelist says. One per line")
        public String[] blockedGenerators = {};
        @net.minecraftforge.common.config.Config.Comment("How far apart a structure is seeded, written as structure=chunks, one per line, such as temples=24. For mineshafts the number is one chunk in that many")
        public String[] structureSpacing = {};
        @net.minecraftforge.common.config.Config.Comment("The closest two of a structure may be, written as structure=chunks, one per line. Monuments, mansions, end cities and strongholds use it")
        public String[] structureSeparation = {};
        @net.minecraftforge.common.config.Config.Comment("How far from world spawn, in blocks, before a structure starts generating, written as structure=blocks, one per line")
        public String[] structureMinDistanceFromSpawn = {};
        @net.minecraftforge.common.config.Config.Comment("Biomes a structure generates in, written as structure=biome,biome, one per line. Names are registry names, biome names or dictionary types such as SANDY")
        public String[] structureBiomes = {};
        @net.minecraftforge.common.config.Config.Comment("Mobs a structure spawns whatever the biome says, written as structure=namespace:entity:weight:least:most, comma separated, one structure per line. Temples, monuments and nether fortresses have such a list. An empty list after the equals stops that structure spawning anything")
        public String[] structureSpawns = {};
        @net.minecraftforge.common.config.Config.Comment("What the mob spawner inside a vanilla structure spawns, written as structure=namespace:entity, comma separated for a random pick, one structure per line. Only dungeons, mineshafts, netherbridges and strongholds have one. Spawners other mods place are left alone")
        public String[] structureSpawners = {};
        @net.minecraftforge.common.config.Config.Comment("Direction of the biome lists, written as structure=true or structure=false, one per line. True takes the listed biomes away, false makes them the only ones [Default=false]")
        public String[] structureBiomesAreBlacklist = {};
        @net.minecraftforge.common.config.Config.Comment("Vanilla village pieces named here, one per line. The names are house1, house2, house3, house4garden, church, woodhut, hall, field1 and field2")
        public String[] villagePieces = {};
        @net.minecraftforge.common.config.Config.Comment("On, the pieces in villagePieces are blocked. Off, only those pieces generate [Default=true]")
        public boolean villagePiecesAreBlacklist = true;
        @net.minecraftforge.common.config.Config.Comment("World generator types blocked outright, whatever the whitelist says. One of ores, structures, flora, lakes, terrain or unknown, one per line. Types are worked out from the generator class name, so use generatorTypeMap for the ones that guess wrong")
        public String[] generatorTypes = {};
        @net.minecraftforge.common.config.Config.Comment("On, the types in generatorTypes are blocked. Off, only those types generate and everything else is blocked [Default=true]")
        public boolean generatorTypesAreBlacklist = true;
        @net.minecraftforge.common.config.Config.Comment("Types for generators the class name does not describe, written as pattern=type, where pattern is a mod id or part of a generator class name. One per line, checked before the built in patterns")
        public String[] generatorTypeMap = {};
        @net.minecraftforge.common.config.Config.Comment("Blocks swapped out of chunks that already exist, written as block=block, meta optional on either side, such as bigreactors:oreyellorite=minecraft:stone. One per line. Chunks are done once each as they load")
        public String[] blockReplacements = {};
        @net.minecraftforge.common.config.Config.Comment("Dimensions block replacement applies to. Empty means every dimension")
        public int[] blockReplacementDimensions = {};
        @net.minecraftforge.common.config.Config.Comment("On, block replacement skips these dimensions. Off, it applies only to them [Default=false]")
        public boolean blockReplacementDimensionsAreBlacklist = false;
        @net.minecraftforge.common.config.Config.Comment("Lowest y block replacement looks at [Default=0]")
        public int blockReplacementMinHeight = 0;
        @net.minecraftforge.common.config.Config.Comment("Highest y block replacement looks at [Default=255]")
        public int blockReplacementMaxHeight = 255;
        @net.minecraftforge.common.config.Config.Comment("Change this to make every chunk go through block replacement again [Default=0000]")
        public String blockReplacementKey = "0000";
        @net.minecraftforge.common.config.Config.Comment("Log the first time each replacement is made, and a total when a world catches up [Default=true]")
        public boolean logBlockReplacements = true;
        @net.minecraftforge.common.config.Config.Comment("Dimensions world generator blocking applies to. Empty means every dimension [Default=0, the overworld]")
        public int[] blockGeneratorDimensions = { 0 };
        @net.minecraftforge.common.config.Config.Comment("On, generator blocking skips these dimensions. Off, it applies only to them [Default=false]")
        public boolean blockGeneratorDimensionsAreBlacklist = false;
        @net.minecraftforge.common.config.Config.Comment("Log the first time each mod and generator is blocked, so you can see what to whitelist [Default=true]")
        public boolean logBlockedGenerators = true;
        @net.minecraftforge.common.config.Config.Comment("Stop every mod, and Minecraft itself, from generating ores. Only the mods in oreWhitelist still generate. Reaches only ore generation that fires Forge's ore generation event [Default=false]")
        public boolean blockOres = false;
        @net.minecraftforge.common.config.Config.Comment("Mod ids allowed to generate ores while blockOres is on. Ores a pack defines belong to that pack's namespace")
        public String[] oreWhitelist = { "minecraft" };
        @net.minecraftforge.common.config.Config.Comment("Ore types this applies to, whoever generates them and whatever the whitelist says. One per line. Known types: COAL, IRON, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, SILVERFISH, CUSTOM")
        public String[] oreTypes = {};
        @net.minecraftforge.common.config.Config.Comment("On, oreTypes are blocked. Off, only oreTypes generate [Default=true]")
        public boolean oreTypesAreBlacklist = true;
        @net.minecraftforge.common.config.Config.Comment("Log the first time each mod and ore type is blocked, so you can see what to whitelist [Default=true]")
        public boolean logBlockedOres = true;
        @net.minecraftforge.common.config.Config.Comment("Stop every biome from generating except the mods in biomeWhitelist. Blocked biomes become the void biome. Blocking every biome makes the overworld a void world [Default=false]")
        public boolean blockBiomes = false;
        @net.minecraftforge.common.config.Config.Comment("Mod ids whose biomes still generate while blockBiomes is on. A pack biome uses the pack's namespace")
        public String[] biomeWhitelist = { "minecraft" };
        @net.minecraftforge.common.config.Config.Comment("Biomes this applies to, whoever owns them and whatever the whitelist says. Friendly name such as Birch Forest, or registry name such as minecraft:birch_forest. One per line")
        public String[] biomeNames = {};
        @net.minecraftforge.common.config.Config.Comment("On, biomeNames are blocked. Off, only biomeNames generate [Default=true]")
        public boolean biomeNamesAreBlacklist = true;
        @net.minecraftforge.common.config.Config.Comment("Dimensions biome blocking applies to. Empty means every dimension [Default=0, the overworld]")
        public int[] blockBiomeDimensions = { 0 };
        @net.minecraftforge.common.config.Config.Comment("On, biome blocking skips these dimensions. Off, it applies only to them [Default=false]")
        public boolean blockBiomeDimensionsAreBlacklist = false;
        @net.minecraftforge.common.config.Config.Comment("Log a per mod count of which biomes were blocked, so you can see what to whitelist [Default=true]")
        public boolean logBlockedBiomes = true;
        @net.minecraftforge.common.config.Config.Comment("Which world template fills the biomes that blocking removes, such as oceans, rivers and beaches that the biome lists never reach. Built in: void, vanilla, ocean, plains, desert. A pack can add its own in worldtemplates/*.json and you name it here as namespace:name. 'auto' picks the pack template from the highest priority pack. Empty leaves blocked biomes as the void and ignores any pack settings section [Default=auto]")
        public String worldTemplate = "auto";
        @net.minecraftforge.common.config.Config.Comment("Generate the overworld as empty space with a platform at the spawn point, and stop mobs, animals and structures appearing. Only affects new chunks [Default=false]")
        public boolean voidWorld = false;
        @net.minecraftforge.common.config.Config.Comment("Which dimensions are made void. Empty means the overworld alone [Default={0}]")
        public int[] voidWorldDimensions = { 0 };
        @net.minecraftforge.common.config.Config.Comment("Treat voidWorldDimensions as the dimensions to leave alone instead [Default=false]")
        public boolean voidWorldDimensionsAreBlacklist = false;
        @net.minecraftforge.common.config.Config.Comment("Let the ender dragon fight happen: the dragon itself, its bar, the crystals and the fountain it stands on. A void end leaves it out unless a pack asks for it [Default=true]")
        public boolean dragonFight = true;
        @net.minecraftforge.common.config.Config.Comment("The block the void world platform is made of [Default=minecraft:stone]")
        public String voidPlatformBlock = "minecraft:stone";
        @net.minecraftforge.common.config.Config.Comment("How high above the bottom of the world the void world platform sits [Default=64]")
        public int voidPlatformHeight = 64;
        @net.minecraftforge.common.config.Config.Comment("How wide the void world platform is, in blocks. Rounded down to an odd number so it centers on the spawn point [Default=9]")
        public int voidPlatformSize = 9;
        @net.minecraftforge.common.config.Config.Comment("Replace the jagged bedrock at the bottom of the world with flat layers. Only affects new chunks unless flatBedrockRetrogen is on. Cannot be undone [Default=false]")
        public boolean flatBedrock = false;
        @net.minecraftforge.common.config.Config.Comment("Dimensions to flatten bedrock in. Leave empty for every dimension [Default=0, the overworld]")
        public int[] flatBedrockDimensions = {0};
        @net.minecraftforge.common.config.Config.Comment("On, flattening skips these dimensions. Off, it applies only to them [Default=false]")
        public boolean flatBedrockDimensionsAreBlacklist = false;
        @net.minecraftforge.common.config.Config.Comment("How many layers of bedrock to leave at the bottom [Default=1]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 5)
        public int bedrockLayers = 1;
        @net.minecraftforge.common.config.Config.Comment("Biomes to flatten bedrock in. Friendly name such as Birch Forest, or registry name such as minecraft:birch_forest. Empty means every biome. One per line")
        public String[] flatBedrockBiomes = {};
        @net.minecraftforge.common.config.Config.Comment("Biome dictionary types to flatten bedrock in, alongside flatBedrockBiomes. Known types include OCEAN, RIVER, MOUNTAIN, SWAMP, NETHER, END. One per line")
        public String[] flatBedrockBiomeTypes = {};
        @net.minecraftforge.common.config.Config.Comment("On, flattening skips these biomes. Off, it applies only to them [Default=false]")
        public boolean flatBedrockBiomesAreBlacklist = false;
        @net.minecraftforge.common.config.Config.Comment("Change this to make every chunk eligible for bedrock flattening again [Default=0000]")
        public String flatBedrockRetrogenKey = "0000";
        @net.minecraftforge.common.config.Config.Comment("Flatten the bedrock in chunks that already exist, not only new ones. Each chunk is done once and remembers it [Default=false]")
        public boolean flatBedrockRetrogen = false;
        @net.minecraftforge.common.config.Config.Comment("Flatten the bedrock ceiling too, where a dimension has one, such as the Nether roof [Default=false]")
        public boolean flatBedrockRoof = false;
        @net.minecraftforge.common.config.Config.Comment("A filler per dimension. Write dimension=block, one per line, as in -1=minecraft:netherrack. Overrides flatBedrockFiller for the dimensions listed")
        public String[] flatBedrockFillers = { "-1=minecraft:netherrack", "1=minecraft:end_stone" };
        @net.minecraftforge.common.config.Config.Comment("What replaces the bedrock that is removed. Empty picks per dimension: stone, netherrack, end stone")
        public String flatBedrockFiller = "";
        @net.minecraftforge.common.config.Config.Comment("Write per-chunk retrogen lines and cascading worldgen traces to logs/rdpl.log, and the debug lists other messages refer to. Very verbose, about two lines per chunk loaded [Default=false]")
        public boolean worldgenDebug = false;
    }

    public static class Tweaks {
        @net.minecraftforge.common.config.Config.Comment("Leaves that lose their tree decay within a second instead of waiting on random ticks. Ignored when Universal Tweaks is installed, which does this itself [Default=false]")
        public boolean promptLeafDecay = false;
        @net.minecraftforge.common.config.Config.Comment("Grass paths can be made under a block and stay there when one is placed above. Ignored when Universal Tweaks is installed, which does this itself [Default=false]")
        public boolean lenientPaths = false;
    }

    public static class Client {
        @net.minecraftforge.common.config.Config.Comment("Add the spawn area percentage to the world loading screen, so a slow first load shows progress instead of a motionless 'Building terrain' [Default=true]")
        public boolean loadingScreenPercent = true;
    }

    public static class Compat {
        @net.minecraftforge.common.config.Config.Comment("Silence the 'Could not load material model' and 'Could not load multimodel' errors Tinkers' Construct and Construct's Armory log for every tool, part and armor piece. Does not change which model is used. Requires a restart [Default=false]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean fixTinkersModelErrors = false;
    }
}
