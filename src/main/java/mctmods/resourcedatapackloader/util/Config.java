package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public final class Config {
    public static final ForgeConfigSpec SPEC;
    public static final Packs packs;
    public static final Content content;
    public static final Entities entities;
    public static final Recipes recipes;
    public static final Data data;
    public static final Worldgen worldgen;
    public static final Tweaks tweaks;
    public static final Chunks chunks;
    public static final Control control;
    public static final String WELCOME = "Welcome to your World!";
    public static final String PREGEN_RUNNING = "World pregeneration running, %d%% done";
    public static final String PREGEN_FINISHED = "World pregeneration finished";
    public static final String PREGEN_STOPPED = "World pregeneration stopped";
    public static final String PREGEN_SPECTATING = "Spectating until the world is ready";

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        packs = new Packs(builder);
        content = new Content(builder);
        entities = new Entities(builder);
        recipes = new Recipes(builder);
        data = new Data(builder);
        worldgen = new Worldgen(builder);
        tweaks = new Tweaks(builder);
        chunks = new Chunks(builder);
        control = new Control(builder);
        SPEC = builder.build();
    }

    private Config() {}

    public static boolean loaded() { return SPEC.isLoaded(); }

    public static boolean contentOff() { return !content.load() || content.vanillaClients(); }

    public static final class Packs {
        private final ForgeConfigSpec.ConfigValue<String> rootDirectory;
        private final ForgeConfigSpec.BooleanValue overrideResourcePacks;
        private final ForgeConfigSpec.BooleanValue warnOnCaseMismatch;
        private final ForgeConfigSpec.BooleanValue logContents;
        private final ForgeConfigSpec.BooleanValue traceUnresolvedVariables;

        private Packs(ForgeConfigSpec.Builder builder) {
            builder.comment("How pack folders are found and served").push("packs");
            rootDirectory = builder.comment("Folder packs are loaded from, relative to the .minecraft directory. An absolute path also works. Requires a restart [Default=rdploader]").worldRestart().define("rootDirectory", PackManager.ROOT_DIRECTORY);
            overrideResourcePacks = builder.comment("Insert the asset pack above the player's selected resource packs and the world's own data packs. A pack named RDPLO... always overrides, RDPLN... never does [Default=true]").define("overrideResourcePacks", true);
            warnOnCaseMismatch = builder.comment("Warn when a file only matches because the filesystem is case-insensitive. Such packs break on Linux [Default=true]").define("warnOnCaseMismatch", true);
            logContents = builder.comment("Log every pack found and how many files it provides [Default=false]").define("logContents", false);
            traceUnresolvedVariables = builder.comment("Log a stack trace the first time a file with a '#' in its name is requested, naming whatever asked for it [Default=false]").define("traceUnresolvedVariables", false);
            builder.pop();
        }

        public String rootDirectory() { return loaded() ? rootDirectory.get() : ConfigCore.text("packs.rootDirectory", PackManager.ROOT_DIRECTORY); }

        public boolean overrideResourcePacks() { return loaded() ? overrideResourcePacks.get() : ConfigCore.flag("packs.overrideResourcePacks", true); }

        public boolean warnOnCaseMismatch() { return loaded() ? warnOnCaseMismatch.get() : ConfigCore.flag("packs.warnOnCaseMismatch", true); }

        public boolean logContents() { return loaded() ? logContents.get() : ConfigCore.flag("packs.logContents", false); }

        public boolean traceUnresolvedVariables() { return loaded() ? traceUnresolvedVariables.get() : ConfigCore.flag("packs.traceUnresolvedVariables", false); }
    }

    public static final class Content {
        private final ForgeConfigSpec.BooleanValue load;
        private final ForgeConfigSpec.BooleanValue vanillaClients;
        private final ForgeConfigSpec.BooleanValue sounds;
        private final ForgeConfigSpec.BooleanValue fuels;
        private final ForgeConfigSpec.BooleanValue potions;
        private final ForgeConfigSpec.BooleanValue brewing;
        private final ForgeConfigSpec.BooleanValue villagers;
        private final ForgeConfigSpec.BooleanValue entities;
        private final ForgeConfigSpec.BooleanValue overrides;
        private final ForgeConfigSpec.BooleanValue hardness;
        private final ForgeConfigSpec.BooleanValue shovelPaths;
        private final ForgeConfigSpec.ConfigValue<String> shovelPathBecomes;
        private final ForgeConfigSpec.ConfigValue<String> shovelPathReverts;
        private final ForgeConfigSpec.BooleanValue hoeTilling;
        private final ForgeConfigSpec.ConfigValue<String> hoeTillsInto;
        private final ForgeConfigSpec.IntValue caneMaxHeight;
        private final ForgeConfigSpec.IntValue cactusMaxHeight;

        private Content(ForgeConfigSpec.Builder builder) {
            builder.comment("Blocks, items, fluids and everything else packs define").push("content");
            load = builder.comment("Register the blocks, items, fluids, materials and creative tabs that packs define. Requires a restart [Default=true]").worldRestart().define("load", true);
            vanillaClients = builder.comment("Serve plain vanilla clients: nothing from any pack is registered, no blocks, items, fluids or creative tabs, so a client without the mod can join. Everything that lives on the server alone still applies. Requires a restart [Default=false]").worldRestart().define("vanillaClients", false);
            sounds = builder.comment("Register the sound events named by sounds/*.json, so packs can ship their own audio [Default=true]").define("sounds", true);
            fuels = builder.comment("Apply fuels/*.json files, which give items a furnace burn time [Default=true]").define("fuels", true);
            potions = builder.comment("Register the potion effects and potion types described by potions/*.json and potion_types/*.json in packs. Requires a restart [Default=true]").worldRestart().define("potions", true);
            brewing = builder.comment("Apply brewing/*.json files, which add brewing stand recipes [Default=true]").define("brewing", true);
            villagers = builder.comment("Register the villager professions described by villagers/*.json and apply the trades in trades/*.json. Requires a restart [Default=true]").worldRestart().define("villagers", true);
            entities = builder.comment("Register the entity variants described by entities/*.json in packs. Requires a restart [Default=true]").worldRestart().define("entities", true);
            overrides = builder.comment("Apply overrides/<namespace>/<name>.json files, which change properties of blocks, items and potion types that already exist, vanilla or modded [Default=true]").define("overrides", true);
            hardness = builder.comment("Apply hardness/*.json files, which give a group of blocks a mining time and blast resistance multiplier, rolled per block position [Default=true]").define("hardness", true);
            shovelPaths = builder.comment("Let a shovel turn blocks marked behavesAs path into a path, and revert a path while sneaking [Default=true]").define("shovelPaths", true);
            shovelPathBecomes = builder.comment("What a shovel turns those blocks into. Empty uses the dirt path").define("shovelPathBecomes", "");
            shovelPathReverts = builder.comment("What sneaking with a shovel turns a path back into. Empty uses dirt").define("shovelPathReverts", "");
            hoeTilling = builder.comment("Let a hoe till blocks marked behavesAs till [Default=true]").define("hoeTilling", true);
            hoeTillsInto = builder.comment("What a hoe turns those blocks into. Empty uses farmland").define("hoeTillsInto", "");
            caneMaxHeight = builder.comment("How tall vanilla sugar cane grows. Vanilla is 3. Pack defined cane blocks use their own growth section and ignore this [Default=3]").defineInRange("caneMaxHeight", 3, 1, 255);
            cactusMaxHeight = builder.comment("The same for vanilla cactus [Default=3]").defineInRange("cactusMaxHeight", 3, 1, 255);
            builder.pop();
        }

        public boolean load() { return loaded() ? load.get() : ConfigCore.flag("content.load", true); }

        public boolean vanillaClients() { return loaded() ? vanillaClients.get() : ConfigCore.flag("content.vanillaClients", false); }

        public boolean sounds() { return loaded() ? sounds.get() : ConfigCore.flag("content.sounds", true); }

        public boolean fuels() { return loaded() ? fuels.get() : ConfigCore.flag("content.fuels", true); }

        public boolean potions() { return loaded() ? potions.get() : ConfigCore.flag("content.potions", true); }

        public boolean brewing() { return loaded() ? brewing.get() : ConfigCore.flag("content.brewing", true); }

        public boolean villagers() { return loaded() ? villagers.get() : ConfigCore.flag("content.villagers", true); }

        public boolean entities() { return loaded() ? entities.get() : ConfigCore.flag("content.entities", true); }

        public boolean overrides() { return loaded() ? overrides.get() : ConfigCore.flag("content.overrides", true); }

        public boolean hardness() { return loaded() ? hardness.get() : ConfigCore.flag("content.hardness", true); }

        public boolean shovelPaths() { return loaded() ? shovelPaths.get() : ConfigCore.flag("content.shovelPaths", true); }

        public String shovelPathBecomes() { return loaded() ? shovelPathBecomes.get() : ConfigCore.text("content.shovelPathBecomes", ""); }

        public String shovelPathReverts() { return loaded() ? shovelPathReverts.get() : ConfigCore.text("content.shovelPathReverts", ""); }

        public boolean hoeTilling() { return loaded() ? hoeTilling.get() : ConfigCore.flag("content.hoeTilling", true); }

        public String hoeTillsInto() { return loaded() ? hoeTillsInto.get() : ConfigCore.text("content.hoeTillsInto", ""); }

        public int caneMaxHeight() { return loaded() ? caneMaxHeight.get() : 3; }

        public int cactusMaxHeight() { return loaded() ? cactusMaxHeight.get() : 3; }
    }

    public static final class Recipes {
        private final ForgeConfigSpec.BooleanValue furnace;
        private final ForgeConfigSpec.BooleanValue removals;
        private final ForgeConfigSpec.BooleanValue skipMissingItems;
        private final ForgeConfigSpec.BooleanValue blockRecipes;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> recipeWhitelist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockedRecipeMods;
        private final ForgeConfigSpec.ConfigValue<String> recipeMatch;
        private final ForgeConfigSpec.BooleanValue blockFurnaceRecipes;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> furnaceWhitelist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockedFurnaceMods;
        private final ForgeConfigSpec.BooleanValue logBlockedRecipes;

        private Recipes(ForgeConfigSpec.Builder builder) {
            builder.comment("Recipe files, removals and blocking").push("recipes");
            furnace = builder.comment("Apply furnace/*.json files, which add and remove furnace smelting recipes [Default=true]").define("furnace", true);
            removals = builder.comment("Apply recipe_removals/*.json files, which delete recipes by name, namespace or output [Default=true]").define("removals", true);
            skipMissingItems = builder.comment("Skip recipes that use an item which is not registered, instead of letting them fail. The count is logged once [Default=true]").define("skipMissingItems", true);
            blockRecipes = builder.comment("Remove every crafting recipe, keeping only the mods in recipeWhitelist. Include your pack's namespace to keep its own recipes [Default=false]").define("blockRecipes", false);
            recipeWhitelist = builder.comment("Mod ids whose crafting recipes survive while blockRecipes is on. Include your pack's namespace to keep its own recipes").defineList("recipeWhitelist", List.of("minecraft"), each -> each instanceof String);
            blockedRecipeMods = builder.comment("Mod ids whose crafting recipes are removed outright, whoever they belong to and whatever the whitelist says").defineList("blockedRecipeMods", List.of(), each -> each instanceof String);
            recipeMatch = builder.comment("What the mod id is read from when blocking crafting recipes. 'recipe' uses the recipe's own name, 'output' uses the item it makes, 'both' blocks if either matches and spares if either is whitelisted [Default=recipe]").define("recipeMatch", "recipe");
            blockFurnaceRecipes = builder.comment("Remove every furnace, blast furnace, smoker and campfire recipe, keeping only the mods in furnaceWhitelist. The mod is read from the item produced [Default=false]").define("blockFurnaceRecipes", false);
            furnaceWhitelist = builder.comment("Mod ids whose furnace recipes survive while blockFurnaceRecipes is on. Include your pack's namespace to keep its own recipes").defineList("furnaceWhitelist", List.of("minecraft"), each -> each instanceof String);
            blockedFurnaceMods = builder.comment("Mod ids whose furnace recipes are removed outright, whatever the whitelist says").defineList("blockedFurnaceMods", List.of(), each -> each instanceof String);
            logBlockedRecipes = builder.comment("Log a per mod count of what was blocked, so you can see what to whitelist [Default=true]").define("logBlockedRecipes", true);
            builder.pop();
        }

        public boolean furnace() { return furnace.get(); }

        public boolean removals() { return removals.get(); }

        public boolean skipMissingItems() { return skipMissingItems.get(); }

        public boolean blockRecipes() { return blockRecipes.get(); }

        public List<? extends String> recipeWhitelist() { return recipeWhitelist.get(); }

        public List<? extends String> blockedRecipeMods() { return blockedRecipeMods.get(); }

        public String recipeMatch() { return recipeMatch.get(); }

        public boolean blockFurnaceRecipes() { return blockFurnaceRecipes.get(); }

        public List<? extends String> furnaceWhitelist() { return furnaceWhitelist.get(); }

        public List<? extends String> blockedFurnaceMods() { return blockedFurnaceMods.get(); }

        public boolean logBlockedRecipes() { return logBlockedRecipes.get(); }
    }

    public static final class Data {
        private final ForgeConfigSpec.BooleanValue lootInjections;
        private final ForgeConfigSpec.BooleanValue playerLoot;
        private final ForgeConfigSpec.BooleanValue registryRemaps;

        private Data(ForgeConfigSpec.Builder builder) {
            builder.comment("Loot and registry names").push("data");
            lootInjections = builder.comment("Apply loot_injections/*.json files, which add pools to loot tables that already exist instead of replacing the whole table [Default=true]").define("lootInjections", true);
            playerLoot = builder.comment("Apply player_loot/*.json files, which roll a loot table when a player dies and drop what it makes, on top of or instead of the inventory [Default=true]").define("playerLoot", true);
            registryRemaps = builder.comment("Apply registry_remap files, which rename a registry entry so worlds saved before the rename keep their blocks and items instead of losing them [Default=true]").define("registryRemaps", true);
            builder.pop();
        }

        public boolean lootInjectionsOff() { return !lootInjections.get(); }

        public boolean playerLootOff() { return !playerLoot.get(); }

        public boolean registryRemapsOff() { return !registryRemaps.get(); }
    }

    public static final class Tweaks {
        private final ForgeConfigSpec.BooleanValue promptLeafDecay;
        private final ForgeConfigSpec.BooleanValue lenientPaths;
        private final ForgeConfigSpec.BooleanValue unbreakableSpawners;
        private final ForgeConfigSpec.BooleanValue experimentalWarning;

        private Tweaks(ForgeConfigSpec.Builder builder) {
            builder.comment("Small changes to how vanilla behaves").push("tweaks");
            promptLeafDecay = builder.comment("Leaves that lose their tree decay within a second instead of waiting on random ticks [Default=true]").define("promptLeafDecay", true);
            lenientPaths = builder.comment("Paths and tilled ground can be made under a block and stay there when one is placed above [Default=true]").define("lenientPaths", true);
            unbreakableSpawners = builder.comment("Mob spawners cannot be mined or blown up. Creative mode still removes them. Requires a restart [Default=false]").define("unbreakableSpawners", false);
            experimentalWarning = builder.comment("Show the game's experimental settings warning when a world is made or opened. Off answers it as if you had clicked proceed [Default=false]").define("experimentalWarning", false);
            builder.pop();
        }

        public boolean promptLeafDecay() { return loaded() ? promptLeafDecay.get() : ConfigCore.flag("tweaks.promptLeafDecay", true); }

        public boolean lenientPaths() { return loaded() ? lenientPaths.get() : ConfigCore.flag("tweaks.lenientPaths", true); }

        public boolean unbreakableSpawners() { return loaded() ? unbreakableSpawners.get() : ConfigCore.flag("tweaks.unbreakableSpawners", false); }

        public boolean experimentalWarning() { return loaded() ? experimentalWarning.get() : ConfigCore.flag("tweaks.experimentalWarning", false); }
    }

    public static final class Worldgen {
        private final ForgeConfigSpec.BooleanValue worldgenDebug;
        private final ForgeConfigSpec.ConfigValue<String> worldTemplate;
        private final ForgeConfigSpec.ConfigValue<String> worldSeed;
        private final ForgeConfigSpec.ConfigValue<String> worldName;
        private final ForgeConfigSpec.ConfigValue<String> worldGameMode;
        private final ForgeConfigSpec.ConfigValue<String> worldType;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldTypeExceptions;
        private final ForgeConfigSpec.BooleanValue tellWorldType;
        private final ForgeConfigSpec.ConfigValue<String> generatorOptions;
        private final ForgeConfigSpec.IntValue worldMinHeight;
        private final ForgeConfigSpec.IntValue worldMaxHeight;
        private final ForgeConfigSpec.ConfigValue<String> deepStone;
        private final ForgeConfigSpec.ConfigValue<String> noiseCaves;
        private final ForgeConfigSpec.ConfigValue<String> worldSpawn;
        private final ForgeConfigSpec.IntValue worldBorder;
        private final ForgeConfigSpec.IntValue worldBorderLimit;
        private final ForgeConfigSpec.IntValue worldTime;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldDifficulty;
        private final ForgeConfigSpec.BooleanValue flatBedrock;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> flatBedrockDimensions;
        private final ForgeConfigSpec.BooleanValue flatBedrockDimensionsAreBlacklist;
        private final ForgeConfigSpec.IntValue bedrockLayers;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> flatBedrockBiomes;
        private final ForgeConfigSpec.BooleanValue flatBedrockBiomesAreBlacklist;
        private final ForgeConfigSpec.BooleanValue flatBedrockRoof;
        private final ForgeConfigSpec.BooleanValue voidWorld;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> voidWorldDimensions;
        private final ForgeConfigSpec.BooleanValue voidWorldDimensionsAreBlacklist;
        private final ForgeConfigSpec.ConfigValue<String> voidPlatformBlock;
        private final ForgeConfigSpec.IntValue voidPlatformHeight;
        private final ForgeConfigSpec.IntValue voidPlatformSize;
        private final ForgeConfigSpec.BooleanValue retrogen;
        private final ForgeConfigSpec.BooleanValue adoptExistingChunks;
        private final ForgeConfigSpec.ConfigValue<String> retrogenKey;
        private final ForgeConfigSpec.IntValue retrogenChunksPerTick;
        private final ForgeConfigSpec.BooleanValue blockOres;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> oreWhitelist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> prospectItems;
        private final ForgeConfigSpec.BooleanValue prospectItemsAreBlacklist;
        private final ForgeConfigSpec.BooleanValue prospectDrops;
        private final ForgeConfigSpec.IntValue prospectSlow;
        private final ForgeConfigSpec.IntValue prospectWear;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> oreTypes;
        private final ForgeConfigSpec.BooleanValue oreTypesAreBlacklist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockOreDimensions;
        private final ForgeConfigSpec.BooleanValue blockOreDimensionsAreBlacklist;
        private final ForgeConfigSpec.BooleanValue blockBiomes;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeWhitelist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeNames;
        private final ForgeConfigSpec.BooleanValue biomeNamesAreBlacklist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockBiomeDimensions;
        private final ForgeConfigSpec.BooleanValue blockBiomeDimensionsAreBlacklist;
        private final ForgeConfigSpec.DoubleValue surfaceDayMonsterRate;
        private final ForgeConfigSpec.DoubleValue surfaceNightMonsterRate;
        private final ForgeConfigSpec.DoubleValue undergroundDayMonsterRate;
        private final ForgeConfigSpec.DoubleValue undergroundNightMonsterRate;
        private final ForgeConfigSpec.IntValue monsterCap;
        private final ForgeConfigSpec.IntValue creatureCap;
        private final ForgeConfigSpec.IntValue ambientCap;
        private final ForgeConfigSpec.IntValue waterCreatureCap;
        private final ForgeConfigSpec.IntValue monsterSpawnLight;
        private final ForgeConfigSpec.IntValue caveRegionPlainWeight;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureSpacing;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureSeparation;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureMost;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureSpawners;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureMinDistanceFromSpawn;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureBiomes;
        private final ForgeConfigSpec.BooleanValue structureBiomesAreBlacklist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureSpawns;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureAt;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureAdaptation;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldGravity;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldFallDamage;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldJumpStrength;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldTerminalVelocity;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> cloudHeight;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldBelow;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> worldAbove;
        private final ForgeConfigSpec.BooleanValue worldSeamEntities;
        private final ForgeConfigSpec.BooleanValue worldSeamBedrock;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> threatItems;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> threatLevels;
        private final ForgeConfigSpec.IntValue threatMost;
        private final ForgeConfigSpec.DoubleValue threatSpawnRate;
        private final ForgeConfigSpec.DoubleValue threatNotice;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> threatSays;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockReplacements;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockReplacementDimensions;
        private final ForgeConfigSpec.BooleanValue blockReplacementDimensionsAreBlacklist;
        private final ForgeConfigSpec.IntValue blockReplacementMinHeight;
        private final ForgeConfigSpec.IntValue blockReplacementMaxHeight;
        private final ForgeConfigSpec.ConfigValue<String> blockReplacementKey;
        private final ForgeConfigSpec.BooleanValue logBlockReplacements;

        private Worldgen(ForgeConfigSpec.Builder builder) {
            builder.comment("What generates in the world, and what is stopped from generating").push("worldgen");
            worldgenDebug = builder.comment("Write the debug lines other messages refer to into logs/rdpl.log, such as which pack served a file and what each command did. Very verbose [Default=false]").define("worldgenDebug", false);
            worldTemplate = builder.comment("Which world template's settings apply. A pack adds one in worldtemplates/*.json and you name it here as namespace:name. 'auto' picks the template from the highest priority pack. Empty uses none [Default=auto]").define("worldTemplate", "auto");
            worldSeed = builder.comment("The seed every new world is made with, whatever was typed when it was made, written the same way it would be typed. Empty leaves the choice alone [Default=empty]").define("worldSeed", "");
            worldGameMode = builder.comment("Which way every new world is started, one of survival, hardcore, creative, adventure or spectator. Hardcore is survival where death ends the world, save wide, the same as the choice on the world screen. Empty leaves it as whoever made the world chose [Default=empty]").define("worldGameMode", "");
            worldName = builder.comment("What a new world is called when the screen for making one opens. Empty leaves it as the game names it [Default=empty]").define("worldName", "");
            worldType = builder.comment("The world type the shaped world is built on, one of default, largebiomes or amplified. The shape below (heights, deep stone, sea level, bedrock, void) is generated as a world preset of its own, listed under World Type on the world screen and chosen there whatever was picked. Empty builds on default [Default=empty]").define("worldType", "");
            worldTypeExceptions = builder.comment("World types a player picks that the generated preset leaves alone, such as flat or debug_all_block_states. Empty means every choice is replaced [Default=[flat, debug_all_block_states]]").defineList("worldTypeExceptions", List.of("flat", "debug_all_block_states"), each -> each instanceof String);
            tellWorldType = builder.comment("Tell a player in chat, as they join a world made with the generated preset, which template shaped it. A pack cannot set this [Default=true]").define("tellWorldType", true);
            generatorOptions = builder.comment("The overworld's terrain settings as a JSON object, the keys the 1.12.2 customized world type wrote. Read here: seaLevel and useLavaOceans. Only applied to a world as it is created. Empty leaves the terrain as the world type makes it [Default=empty]").define("generatorOptions", "");
            worldMinHeight = builder.comment("The lowest block of the overworld, a multiple of 16 down to -2032. The game's own bottom is -64; lower makes a deep world under the vanilla terrain, solid stone until the worldgen layer carves it or noiseCaves carries the game's caves down. Only applied through the generated preset [Default=-64]").defineInRange("worldMinHeight", -64, -2032, 2016);
            worldMaxHeight = builder.comment("The block above the overworld's top, a multiple of 16 up to 2032, at most 4064 above worldMinHeight. The game's own top is 320; higher leaves open sky above the vanilla terrain [Default=320]").defineInRange("worldMaxHeight", 320, -2016, 2032);
            deepStone = builder.comment("The block the world below the vanilla terrain is made of when worldMinHeight goes under -64, such as a pack's own deepslate. It blends into deepslate across the eight layers under -64 the way deepslate blends into stone. Empty keeps stone [Default=empty]").define("deepStone", "");
            noiseCaves = builder.comment("Where the game's caves, tunnels, noodles and aquifers carry on when worldMinHeight goes under -64: off keeps the world under the vanilla terrain solid deep stone for the worldgen layer to carve, deep carries them down to the floor with the lava lakes moved to its bottom ten layers, world means the same on this version because the vanilla terrain has them already [Default=off]").define("noiseCaves", "off");
            worldSpawn = builder.comment("Where every new world spawns, written as x,z or x,y,z. Without a y the ground at that spot is used. Only applied to a world as it is created. Empty leaves the choice to the game [Default=empty]").define("worldSpawn", "");
            worldBorder = builder.comment("How far across, in blocks, the world border stands in every new world. Only applied to a world as it is created. 0 leaves the border where the game puts it [Default=0]").defineInRange("worldBorder", 0, 0, 60000000);
            worldBorderLimit = builder.comment("The widest border a pack is allowed to ask for through worldBorder. A pack asking for more is refused and the border is left where the game puts it. A pack cannot set this [Default=60000000]").defineInRange("worldBorderLimit", 60000000, 1, 60000000);
            worldTime = builder.comment("Lock the overworld's time of day, in ticks, the same figure /time set takes, so 18000 is midnight. The clock stops and never moves. -1 leaves time running [Default=-1]").defineInRange("worldTime", -1, -1, 23999);
            worldDifficulty = builder.comment("Lock the difficulty, one of peaceful, easy, normal or hard, for the whole world. Difficulty is save wide on this version, so an entry written as dimension=difficulty is read for the overworld alone. Empty leaves it as chosen [Default=empty]").defineList("worldDifficulty", List.of(), each -> each instanceof String);
            flatBedrock = builder.comment("Replace the jagged bedrock at the bottom of the world with flat layers, through the generated preset, so it shapes new worlds made with it [Default=false]").define("flatBedrock", false);
            flatBedrockDimensions = builder.comment("Dimensions to flatten bedrock in, by id such as minecraft:the_nether. Leave empty for every dimension [Default=[minecraft:overworld]]").defineList("flatBedrockDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
            flatBedrockDimensionsAreBlacklist = builder.comment("On, flattening skips these dimensions. Off, it applies only to them [Default=false]").define("flatBedrockDimensionsAreBlacklist", false);
            bedrockLayers = builder.comment("How many layers of bedrock to leave at the bottom [Default=1]").defineInRange("bedrockLayers", 1, 1, 5);
            flatBedrockBiomes = builder.comment("Biomes to flatten bedrock in, by id such as minecraft:birch_forest. Empty means every biome; elsewhere the bedrock stays as the game makes it").defineList("flatBedrockBiomes", List.of(), each -> each instanceof String);
            flatBedrockBiomesAreBlacklist = builder.comment("On, flattening skips these biomes. Off, it applies only to them [Default=false]").define("flatBedrockBiomesAreBlacklist", false);
            flatBedrockRoof = builder.comment("Flatten the bedrock ceiling too, where a dimension has one, such as the Nether roof [Default=false]").define("flatBedrockRoof", false);
            voidWorld = builder.comment("Generate the listed dimensions as empty space with a platform at the spawn point and nothing living, through the generated preset [Default=false]").define("voidWorld", false);
            voidWorldDimensions = builder.comment("Which dimensions are made void, by id. Empty means the overworld alone [Default=[minecraft:overworld]]").defineList("voidWorldDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
            voidWorldDimensionsAreBlacklist = builder.comment("Treat voidWorldDimensions as the dimensions to leave alone instead [Default=false]").define("voidWorldDimensionsAreBlacklist", false);
            voidPlatformBlock = builder.comment("The block the void world platform is made of [Default=minecraft:stone]").define("voidPlatformBlock", "minecraft:stone");
            voidPlatformHeight = builder.comment("The y the void world platform sits at [Default=64]").defineInRange("voidPlatformHeight", 64, -2032, 2031);
            voidPlatformSize = builder.comment("How wide the void world platform is, in blocks. Rounded down to an odd number so it centers on the spawn point [Default=9]").defineInRange("voidPlatformSize", 9, 1, 255);
            retrogen = builder.comment("Catch existing chunks up on worldgen entries with \"retrogen\": true. Off, chunks that already exist are left alone. Chunks are marked as they generate either way, so turning this on later only touches chunks older than the pack [Default=false]").define("retrogen", false);
            adoptExistingChunks = builder.comment("Treat chunks that already exist as if this pack generated them, marking them instead of leaving them for retrogen. Turn this on when replacing a mod that already generated the same ore, so retrogen never doubles it. Worldgen entries added later still retrogen into them [Default=false]").define("adoptExistingChunks", false);
            retrogenKey = builder.comment("Change this to make every chunk eligible for retrogen again, for every worldgen entry. New veins are added on top of what is already there [Default=0000]").define("retrogenKey", "0000");
            retrogenChunksPerTick = builder.comment("How many already generated chunks to catch up per tick. Higher is faster but stutters more [Default=2]").defineInRange("retrogenChunksPerTick", 2, 1, 64);
            blockOres = builder.comment("Stop every mod, and Minecraft itself, from generating ores. Only the mods in oreWhitelist still generate. An ore is a placed feature with ore in its id, which is Minecraft's and most mods' [Default=false]").define("blockOres", false);
            oreWhitelist = builder.comment("Mod ids allowed to generate ores while blockOres is on. Ores a pack defines belong to that pack's namespace [Default=[minecraft]]").defineList("oreWhitelist", List.of("minecraft"), each -> each instanceof String);
            prospectItems = builder.comment("Items that prospect for vein shaped worldgen entries when a sneaking player breaks a block with one, as item=entry|entry[,radius in chunks] or item=*[,radius], e.g. minecraft:compass=iron_vein|coal_seam or mypack:rod=*,12. The reading names the ore and a compass direction [Default=[]]").defineList("prospectItems", List.of(), each -> each instanceof String);
            prospectItemsAreBlacklist = builder.comment("On, the entries named after an item in prospectItems are the ones it does NOT read, and every other vein shaped entry is [Default=false]").define("prospectItemsAreBlacklist", false);
            prospectDrops = builder.comment("Whether a block broken in prospecting mode drops anything. Off, the sample is destroyed: no drops, no experience [Default=false]").define("prospectDrops", false);
            prospectSlow = builder.comment("How many times slower a block breaks in prospecting mode [Default=2]").defineInRange("prospectSlow", 2, 1, 100);
            prospectWear = builder.comment("How much durability a prospecting break costs the item, at least 2 [Default=2]").defineInRange("prospectWear", 2, 2, 1000);
            oreTypes = builder.comment("Ore types this applies to, whoever generates them and whatever the whitelist says. Known types: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM for any other ore [Default=[]]").defineList("oreTypes", List.of(), each -> each instanceof String);
            oreTypesAreBlacklist = builder.comment("On, oreTypes are blocked. Off, only oreTypes generate [Default=true]").define("oreTypesAreBlacklist", true);
            blockOreDimensions = builder.comment("Dimensions ore blocking applies to, by id such as minecraft:the_nether; read as the overworld, nether and end biome tags. Empty means every dimension [Default=[]]").defineList("blockOreDimensions", List.of(), each -> each instanceof String);
            blockOreDimensionsAreBlacklist = builder.comment("Treat blockOreDimensions as the dimensions to leave alone instead [Default=false]").define("blockOreDimensionsAreBlacklist", false);
            blockBiomes = builder.comment("Stop every biome from generating except the mods in biomeWhitelist. Blocked biomes become the void biome, or what the world template's roles and fallback name. Blocking every biome makes the overworld a void world [Default=false]").define("blockBiomes", false);
            biomeWhitelist = builder.comment("Mod ids whose biomes still generate while blockBiomes is on. A pack biome uses the pack's namespace [Default=[minecraft]]").defineList("biomeWhitelist", List.of("minecraft"), each -> each instanceof String);
            biomeNames = builder.comment("Biomes this applies to, whoever owns them and whatever the whitelist says, by id such as minecraft:birch_forest [Default=[]]").defineList("biomeNames", List.of(), each -> each instanceof String);
            biomeNamesAreBlacklist = builder.comment("On, biomeNames are blocked. Off, only biomeNames generate [Default=true]").define("biomeNamesAreBlacklist", true);
            blockBiomeDimensions = builder.comment("Dimensions biome blocking applies to, by id. Empty means every dimension whose biomes are placed by climate, the overworld and the nether [Default=[minecraft:overworld]]").defineList("blockBiomeDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
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
            caveRegionPlainWeight = builder.comment("The weight of plain, region-less underground against the cave regions' own weights. Higher leaves more of the underground without any region [Default=4]").defineInRange("caveRegionPlainWeight", 4, 0, 1000);
            structureSpacing = builder.comment("How far apart vanilla structures are seeded, in chunks, as structure=chunks entries: the 1.12.2 names temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities and villages, or any structure set id such as pillager_outposts. For mineshafts the number is one chunk in that many; for strongholds it is the ring distance [Default=[]]").defineList("structureSpacing", List.of(), each -> each instanceof String);
            structureSeparation = builder.comment("The closest two of a structure may be, in chunks, as structure=chunks entries; for strongholds it is the ring spread [Default=[]]").defineList("structureSeparation", List.of(), each -> each instanceof String);
            structureMost = builder.comment("The most of a structure a dimension may hold, as structure=count entries such as villages=100: once that many have been founded no chunk founds another, chunks pinned with structureAt aside. 0 or an absent entry sets no ceiling [Default=[]]").defineList("structureMost", List.of(), each -> each instanceof String);
            structureSpawners = builder.comment("What the mob spawner inside a vanilla structure spawns, as structure=namespace:entity entries, comma separated for a random pick. Only dungeons, mineshafts, netherbridges and strongholds build one; spawners other mods place are left alone [Default=[]]").defineList("structureSpawners", List.of(), each -> each instanceof String);
            structureMinDistanceFromSpawn = builder.comment("How far from the world spawn a structure starts, in blocks, as structure=blocks entries. Measured from the pack's worldSpawn when one is set, else from the world origin, since placement is decided before any spawn exists [Default=[]]").defineList("structureMinDistanceFromSpawn", List.of(), each -> each instanceof String);
            structureBiomes = builder.comment("Where a structure may generate, as structure=biome,biome entries naming biome ids or biome types such as SANDY [Default=[]]").defineList("structureBiomes", List.of(), each -> each instanceof String);
            structureBiomesAreBlacklist = builder.comment("On, structureBiomes names the biomes to keep a structure out of. Off, only those biomes get it [Default=false]").define("structureBiomesAreBlacklist", false);
            structureSpawns = builder.comment("The mobs a structure spawns whatever the biome says, as structure=namespace:entity:weight:least:most entries, comma separated; an empty list after the = spawns nothing [Default=[]]").defineList("structureSpawns", List.of(), each -> each instanceof String);
            structureAt = builder.comment("Structures pinned to exact spots, as structure=x,z entries in block coordinates, one per wanted instance. A pinned structure generates in that chunk and nowhere else [Default=[]]").defineList("structureAt", List.of(), each -> each instanceof String);
            structureAdaptation = builder.comment("How the terrain adapts to a structure, as structure=mode entries with the modes none, bury, beard_thin, beard_box and encapsulate [Default=[]]").defineList("structureAdaptation", List.of(), each -> each instanceof String);
            worldGravity = builder.comment("Scale gravity, as a multiplier of vanilla where 1.0 is unchanged and 0.17 is moon-like, for players and mobs. A bare value covers every dimension, and an entry written as dimension=value covers that dimension alone and wins over the bare one. Empty leaves gravity alone [Default=[]]").defineList("worldGravity", List.of(), each -> each instanceof String);
            worldFallDamage = builder.comment("Scale fall damage the same way, 0.5 halving it and 2.0 doubling it [Default=[]]").defineList("worldFallDamage", List.of(), each -> each instanceof String);
            worldJumpStrength = builder.comment("Scale jump strength the same way, 1.5 jumping half again as high [Default=[]]").defineList("worldJumpStrength", List.of(), each -> each instanceof String);
            worldTerminalVelocity = builder.comment("Scale the fastest a mob or player falls the same way, 0.5 falling at half vanilla's top speed [Default=[]]").defineList("worldTerminalVelocity", List.of(), each -> each instanceof String);
            cloudHeight = builder.comment("The y clouds are drawn at, as dimension=y entries. A bare number covers every dimension. Empty keeps the game's own cloud height, 192 in the overworld [Default=[]]").defineList("cloudHeight", List.of(), each -> each instanceof String);
            worldBelow = builder.comment("Stack another dimension under this one: falling out of the bottom of the world carries you into the named dimension, arriving under its ceiling at the same x and z, still falling. Entries are written as dimension=target, such as minecraft:overworld=minecraft:the_nether to hang the nether under the overworld; a bare id covers every dimension. Digging through needs the floor's bedrock left out, which worldSeamBedrock decides. Empty means the floor stays the floor [Default=[]]").defineList("worldBelow", List.of(), each -> each instanceof String);
            worldAbove = builder.comment("The same for the ceiling: rising past the top of the world carries you into the named dimension, arriving above its floor. Written the same way as worldBelow [Default=[]]").defineList("worldAbove", List.of(), each -> each instanceof String);
            worldSeamEntities = builder.comment("Whether dropped items, mobs and other entities ride the world seams too, or only players. Riders and mounts cross one at a time [Default=true]").define("worldSeamEntities", true);
            worldSeamBedrock = builder.comment("Keep the bedrock at a seam boundary anyway. Off, a dimension whose floor or ceiling carries a worldBelow or worldAbove seam generates no bedrock there, so the way through can be dug. Already generated chunks keep whatever they have [Default=false]").define("worldSeamBedrock", false);
            threatItems = builder.comment("Items that raise a player's threat level, as item=level,count entries with an optional ,each or ,batch at the end, e.g. minecraft:diamond_sword=5,1 or minecraft:diamond=1,16,batch. Each, the default, adds the level for every one held, counting no more than count of them; batch adds the level once for every count held. A count above the item's stack size is cut to the stack size. Every loaded entity holding items is a carrier: a player's main inventory, armor and off hand, a dropped stack, anything with an item inventory such as a chest mule or a chest minecart, and the held items and armor of other mobs. Empty turns the threat level off [Default=[]]").defineList("threatItems", List.of(), each -> each instanceof String);
            threatLevels = builder.comment("Rising scores that open each threat band, e.g. 5, 15, 40 for three bands. A player below the first is in band 0. Empty turns the threat level off [Default=[]]").defineList("threatLevels", List.of(), each -> each instanceof String);
            threatMost = builder.comment("The highest score a carrier can reach, -1 for no cap [Default=-1]").defineInRange("threatMost", -1, -1, Integer.MAX_VALUE);
            threatSpawnRate = builder.comment("Multiplied into the hostile spawn rate near carriers in the top band, scaled down through the lower bands. 1.0 changes nothing, 2.0 doubles spawns at the top [Default=1.0]").defineInRange("threatSpawnRate", 1.0D, 0.0D, 100.0D);
            threatNotice = builder.comment("How many blocks farther hostile mobs notice a carrier in the top band, scaled down through the lower bands. 0 changes nothing [Default=0.0]").defineInRange("threatNotice", 0.0D, 0.0D, 256.0D);
            threatSays = builder.comment("Lines said to a player entering a band, as band=message entries [Default=[]]").defineList("threatSays", List.of(), each -> each instanceof String);
            blockReplacements = builder.comment("Blocks swapped out of chunks as they load, written as block=block with an optional state on either side, such as minecraft:andesite=minecraft:stone or minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Every chunk is done once, new ones included [Default=[]]").defineList("blockReplacements", List.of(), each -> each instanceof String);
            blockReplacementDimensions = builder.comment("Dimensions block replacement applies to, by id. Empty means every dimension [Default=[]]").defineList("blockReplacementDimensions", List.of(), each -> each instanceof String);
            blockReplacementDimensionsAreBlacklist = builder.comment("On, block replacement skips these dimensions. Off, it applies only to them [Default=false]").define("blockReplacementDimensionsAreBlacklist", false);
            blockReplacementMinHeight = builder.comment("Lowest y block replacement looks at [Default=-64]").defineInRange("blockReplacementMinHeight", -64, -2032, 2031);
            blockReplacementMaxHeight = builder.comment("Highest y block replacement looks at [Default=319]").defineInRange("blockReplacementMaxHeight", 319, -2032, 2031);
            blockReplacementKey = builder.comment("Change this to make every chunk go through block replacement again [Default=0000]").define("blockReplacementKey", "0000");
            logBlockReplacements = builder.comment("Log the first time each replacement is made, and a total when a world catches up [Default=true]").define("logBlockReplacements", true);
            builder.pop();
        }

        public boolean worldgenDebug() { return loaded() ? worldgenDebug.get() : ConfigCore.flag("worldgen.worldgenDebug", false); }

        public String worldTemplate() { return loaded() ? worldTemplate.get() : ConfigCore.text("worldgen.worldTemplate", "auto"); }

        public String worldSeed() { return loaded() ? worldSeed.get() : ConfigCore.text("worldgen.worldSeed", ""); }

        public String worldName() { return loaded() ? worldName.get() : ConfigCore.text("worldgen.worldName", ""); }

        public String worldGameMode() { return loaded() ? worldGameMode.get() : ConfigCore.text("worldgen.worldGameMode", ""); }

        public String worldType() { return loaded() ? worldType.get() : ConfigCore.text("worldgen.worldType", ""); }

        public List<String> worldTypeExceptions() { return loaded() ? List.copyOf(worldTypeExceptions.get()) : List.of("flat", "debug_all_block_states"); }

        public boolean tellWorldType() { return loaded() ? tellWorldType.get() : ConfigCore.flag("worldgen.tellWorldType", true); }

        public String generatorOptions() { return loaded() ? generatorOptions.get() : ConfigCore.text("worldgen.generatorOptions", ""); }

        public int worldMinHeight() { return loaded() ? worldMinHeight.get() : -64; }

        public int worldMaxHeight() { return loaded() ? worldMaxHeight.get() : 320; }

        public String deepStone() { return loaded() ? deepStone.get() : ConfigCore.text("worldgen.deepStone", ""); }

        public String noiseCaves() { return loaded() ? noiseCaves.get() : ConfigCore.text("worldgen.noiseCaves", "off"); }

        public String worldSpawn() { return loaded() ? worldSpawn.get() : ConfigCore.text("worldgen.worldSpawn", ""); }

        public int worldBorder() { return loaded() ? worldBorder.get() : 0; }

        public int worldBorderLimit() { return loaded() ? worldBorderLimit.get() : 60000000; }

        public int worldTime() { return loaded() ? worldTime.get() : -1; }

        public List<String> worldDifficulty() { return loaded() ? List.copyOf(worldDifficulty.get()) : List.of(); }

        public boolean flatBedrock() { return loaded() ? flatBedrock.get() : ConfigCore.flag("worldgen.flatBedrock", false); }

        public List<String> flatBedrockDimensions() { return loaded() ? List.copyOf(flatBedrockDimensions.get()) : List.of("minecraft:overworld"); }

        public boolean flatBedrockDimensionsAreBlacklist() { return loaded() && flatBedrockDimensionsAreBlacklist.get(); }

        public int bedrockLayers() { return loaded() ? bedrockLayers.get() : 1; }

        public List<String> flatBedrockBiomes() { return loaded() ? List.copyOf(flatBedrockBiomes.get()) : List.of(); }

        public boolean flatBedrockBiomesAreBlacklist() { return loaded() && flatBedrockBiomesAreBlacklist.get(); }

        public boolean flatBedrockRoof() { return loaded() ? flatBedrockRoof.get() : ConfigCore.flag("worldgen.flatBedrockRoof", false); }

        public boolean voidWorld() { return loaded() ? voidWorld.get() : ConfigCore.flag("worldgen.voidWorld", false); }

        public List<String> voidWorldDimensions() { return loaded() ? List.copyOf(voidWorldDimensions.get()) : List.of("minecraft:overworld"); }

        public boolean voidWorldDimensionsAreBlacklist() { return loaded() && voidWorldDimensionsAreBlacklist.get(); }

        public String voidPlatformBlock() { return loaded() ? voidPlatformBlock.get() : ConfigCore.text("worldgen.voidPlatformBlock", "minecraft:stone"); }

        public int voidPlatformHeight() { return loaded() ? voidPlatformHeight.get() : 64; }

        public int voidPlatformSize() { return loaded() ? voidPlatformSize.get() : 9; }

        public boolean retrogen() { return loaded() ? retrogen.get() : ConfigCore.flag("worldgen.retrogen", false); }

        public boolean adoptExistingChunks() { return loaded() ? adoptExistingChunks.get() : ConfigCore.flag("worldgen.adoptExistingChunks", false); }

        public String retrogenKey() { return loaded() ? retrogenKey.get() : ConfigCore.text("worldgen.retrogenKey", "0000"); }

        public int retrogenChunksPerTick() { return loaded() ? retrogenChunksPerTick.get() : 2; }

        public boolean blockOres() { return loaded() ? blockOres.get() : ConfigCore.flag("worldgen.blockOres", false); }

        public List<String> oreWhitelist() { return loaded() ? List.copyOf(oreWhitelist.get()) : List.of("minecraft"); }
        public List<String> prospectItems() { return loaded() ? List.copyOf(prospectItems.get()) : List.of(); }

        public boolean prospectItemsAreBlacklist() { return loaded() && prospectItemsAreBlacklist.get(); }

        public boolean prospectDrops() { return loaded() && prospectDrops.get(); }

        public int prospectSlow() { return loaded() ? prospectSlow.get() : 2; }

        public int prospectWear() { return loaded() ? prospectWear.get() : 2; }

        public List<String> oreTypes() { return loaded() ? List.copyOf(oreTypes.get()) : List.of(); }

        public boolean oreTypesAreBlacklist() { return !loaded() || oreTypesAreBlacklist.get(); }

        public List<String> blockOreDimensions() { return loaded() ? List.copyOf(blockOreDimensions.get()) : List.of(); }

        public boolean blockOreDimensionsAreBlacklist() { return loaded() && blockOreDimensionsAreBlacklist.get(); }

        public boolean blockBiomes() { return loaded() ? blockBiomes.get() : ConfigCore.flag("worldgen.blockBiomes", false); }

        public List<String> biomeWhitelist() { return loaded() ? List.copyOf(biomeWhitelist.get()) : List.of("minecraft"); }

        public List<String> biomeNames() { return loaded() ? List.copyOf(biomeNames.get()) : List.of(); }

        public boolean biomeNamesAreBlacklist() { return !loaded() || biomeNamesAreBlacklist.get(); }

        public List<String> blockBiomeDimensions() { return loaded() ? List.copyOf(blockBiomeDimensions.get()) : List.of("minecraft:overworld"); }

        public boolean blockBiomeDimensionsAreBlacklist() { return loaded() && blockBiomeDimensionsAreBlacklist.get(); }

        public float surfaceDayMonsterRate() { return loaded() ? surfaceDayMonsterRate.get().floatValue() : 1.0F; }

        public float surfaceNightMonsterRate() { return loaded() ? surfaceNightMonsterRate.get().floatValue() : 1.0F; }

        public float undergroundDayMonsterRate() { return loaded() ? undergroundDayMonsterRate.get().floatValue() : 1.0F; }

        public float undergroundNightMonsterRate() { return loaded() ? undergroundNightMonsterRate.get().floatValue() : 1.0F; }

        public int monsterCap() { return loaded() ? monsterCap.get() : -1; }

        public int creatureCap() { return loaded() ? creatureCap.get() : -1; }

        public int ambientCap() { return loaded() ? ambientCap.get() : -1; }

        public int waterCreatureCap() { return loaded() ? waterCreatureCap.get() : -1; }

        public int monsterSpawnLight() { return loaded() ? monsterSpawnLight.get() : -1; }

        public int caveRegionPlainWeight() { return loaded() ? caveRegionPlainWeight.get() : 4; }

        public List<String> structureSpacing() { return loaded() ? List.copyOf(structureSpacing.get()) : List.of(); }

        public List<String> structureSeparation() { return loaded() ? List.copyOf(structureSeparation.get()) : List.of(); }
        public List<String> structureMost() { return loaded() ? List.copyOf(structureMost.get()) : List.of(); }
        public List<String> structureSpawners() { return loaded() ? List.copyOf(structureSpawners.get()) : List.of(); }

        public List<String> structureMinDistanceFromSpawn() { return loaded() ? List.copyOf(structureMinDistanceFromSpawn.get()) : List.of(); }

        public List<String> structureBiomes() { return loaded() ? List.copyOf(structureBiomes.get()) : List.of(); }

        public boolean structureBiomesAreBlacklist() { return loaded() && structureBiomesAreBlacklist.get(); }

        public List<String> structureSpawns() { return loaded() ? List.copyOf(structureSpawns.get()) : List.of(); }

        public List<String> structureAt() { return loaded() ? List.copyOf(structureAt.get()) : List.of(); }

        public List<String> structureAdaptation() { return loaded() ? List.copyOf(structureAdaptation.get()) : List.of(); }
        public List<String> worldGravity() { return loaded() ? List.copyOf(worldGravity.get()) : List.of(); }
        public List<String> worldFallDamage() { return loaded() ? List.copyOf(worldFallDamage.get()) : List.of(); }
        public List<String> worldJumpStrength() { return loaded() ? List.copyOf(worldJumpStrength.get()) : List.of(); }
        public List<String> worldTerminalVelocity() { return loaded() ? List.copyOf(worldTerminalVelocity.get()) : List.of(); }
        public List<String> cloudHeight() { return loaded() ? List.copyOf(cloudHeight.get()) : List.of(); }
        public List<String> worldBelow() { return loaded() ? List.copyOf(worldBelow.get()) : List.of(); }
        public List<String> worldAbove() { return loaded() ? List.copyOf(worldAbove.get()) : List.of(); }
        public boolean worldSeamEntities() { return !loaded() || worldSeamEntities.get(); }
        public boolean worldSeamBedrock() { return loaded() && worldSeamBedrock.get(); }
        public List<String> threatItems() { return loaded() ? List.copyOf(threatItems.get()) : List.of(); }
        public List<String> threatLevels() { return loaded() ? List.copyOf(threatLevels.get()) : List.of(); }
        public int threatMost() { return loaded() ? threatMost.get() : -1; }
        public float threatSpawnRate() { return loaded() ? threatSpawnRate.get().floatValue() : 1.0F; }
        public float threatNotice() { return loaded() ? threatNotice.get().floatValue() : 0.0F; }
        public List<String> threatSays() { return loaded() ? List.copyOf(threatSays.get()) : List.of(); }
        public List<String> blockReplacements() { return loaded() ? List.copyOf(blockReplacements.get()) : List.of(); }
        public List<String> blockReplacementDimensions() { return loaded() ? List.copyOf(blockReplacementDimensions.get()) : List.of(); }
        public boolean blockReplacementDimensionsAreBlacklist() { return loaded() && blockReplacementDimensionsAreBlacklist.get(); }
        public int blockReplacementMinHeight() { return loaded() ? blockReplacementMinHeight.get() : -64; }
        public int blockReplacementMaxHeight() { return loaded() ? blockReplacementMaxHeight.get() : 319; }
        public String blockReplacementKey() { return loaded() ? blockReplacementKey.get() : "0000"; }
        public boolean logBlockReplacements() { return !loaded() || logBlockReplacements.get(); }
    }

    public static final class Chunks {
        private final ForgeConfigSpec.BooleanValue saysCard;
        private final ForgeConfigSpec.ConfigValue<String> saysIcon;
        private final ForgeConfigSpec.ConfigValue<String> saysColor;
        private final ForgeConfigSpec.ConfigValue<String> saysImage;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> welcomeSays;
        private final ForgeConfigSpec.IntValue pregenOnNewWorld;
        private final ForgeConfigSpec.BooleanValue pregenToBorder;
        private final ForgeConfigSpec.IntValue pregenBorderLimit;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> pregenDimensions;
        private final ForgeConfigSpec.BooleanValue pregenAllDimensions;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> pregenDimensionsWhenEntered;
        private final ForgeConfigSpec.BooleanValue pregenResume;
        private final ForgeConfigSpec.IntValue pregenChunksInFlight;
        private final ForgeConfigSpec.ConfigValue<String> pregenRunningSays;
        private final ForgeConfigSpec.ConfigValue<String> pregenFinishedSays;
        private final ForgeConfigSpec.ConfigValue<String> pregenStoppedSays;
        private final ForgeConfigSpec.ConfigValue<String> pregenSpectatingSays;
        private final ForgeConfigSpec.ConfigValue<String> pregenLogo;
        private final ForgeConfigSpec.IntValue spawnChunkRadius;

        private Chunks(ForgeConfigSpec.Builder builder) {
            builder.comment("What this mod says to players and how it shows it, and the land made before anybody plays").push("chunks");
            saysCard = builder.comment("Show the lines this mod says, the welcome and later the land-making progress and the threat lines, as a card in the lower right corner instead of in chat. The card slides in, stays eight seconds and fades, and shows over an open screen too [Default=false]").define("saysCard", false);
            saysIcon = builder.comment("An item drawn on the card, e.g. minecraft:compass. Empty draws none [Default=]").define("saysIcon", "");
            saysColor = builder.comment("The card's background color as hex, e.g. 1E2630. Empty uses a dark slate [Default=]").define("saysColor", "");
            saysImage = builder.comment("A PNG from the pack's client assets stretched over the card as its background, e.g. rubyworld:textures/gui/card.png, drawn over the color. Empty draws none [Default=]").define("saysImage", "");
            welcomeSays = builder.comment("Welcome lines, shown in green on every login. A bare entry is the line for everywhere; a dimension=message entry overrides it for that dimension and also greets every arrival there, e.g. minecraft:the_nether=Welcome to the Nether!. An empty message after the = mutes that dimension; an empty list shows nothing. Left at this default it speaks each player's language [Default=[Welcome to your World!]]").defineList("welcomeSays", List.of(WELCOME), each -> each instanceof String);
            pregenOnNewWorld = builder.comment("How far around the spawn, in chunks, a world has its land made before anybody plays it. The game makes 12 chunks around the spawn on its own, so 12 is the floor and 0 means nothing beyond that. Raise it to reach further than the game does [Default=0]").defineInRange("pregenOnNewWorld", 0, 0, 8192);
            pregenToBorder = builder.comment("Whether a new world has its land made out to its world border instead of a set number of chunks, centered on the border rather than the spawn. A world whose border was never moved in has no border to reach and is passed over [Default=false]").define("pregenToBorder", false);
            pregenBorderLimit = builder.comment("The furthest a border may reach, in chunks either way, before making land out to it is refused. This is here to stop a mistake running for weeks, not to be turned up, and a pack cannot set it. A square of 8192 holds 268 million chunks [Default=8192]").defineInRange("pregenBorderLimit", 8192, 1, 1875000);
            pregenDimensions = builder.comment("Which dimensions a new world has its land made in, by id, in the order given, one after another [Default=[minecraft:overworld]]").defineList("pregenDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
            pregenAllDimensions = builder.comment("Make the land of every dimension the server holds, modded ones included, the overworld first and the rest in id order, instead of only those in pregenDimensions. Ones named in pregenDimensionsWhenEntered are still left for their first visitor [Default=false]").define("pregenAllDimensions", false);
            pregenDimensionsWhenEntered = builder.comment("Dimensions whose land is made not up front but the first time anybody sets foot in them, to the same reach, holding everybody the same way until it is done. One named here and in pregenDimensions is simply made up front [Default=[]]").defineList("pregenDimensionsWhenEntered", List.of(), each -> each instanceof String);
            pregenResume = builder.comment("Whether a run that was stopped or cut short picks up where it left off next time the world is loaded, rather than starting again [Default=false]").define("pregenResume", false);
            pregenChunksInFlight = builder.comment("How many chunks a land-making run asks the game for at once. More keeps the generation threads busier and the server less responsive to whoever is held watching [Default=32]").defineInRange("pregenChunksInFlight", 32, 1, 512);
            pregenRunningSays = builder.comment("The progress message players see while the world generates, where %d is the percentage and a second %s the dimension. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + PREGEN_RUNNING + "]").define("pregenRunningSays", PREGEN_RUNNING);
            pregenFinishedSays = builder.comment("The message players see when generation finishes. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + PREGEN_FINISHED + "]").define("pregenFinishedSays", PREGEN_FINISHED);
            pregenStoppedSays = builder.comment("The message players see when generation is stopped early. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + PREGEN_STOPPED + "]").define("pregenStoppedSays", PREGEN_STOPPED);
            pregenSpectatingSays = builder.comment("The mid-screen message players see while held in spectator during world generation. Empty shows nothing. Left at this default it speaks each player's language [Default=" + PREGEN_SPECTATING + "]").define("pregenSpectatingSays", PREGEN_SPECTATING);
            pregenLogo = builder.comment("Where the logo stands when pregeneration finishes: left, center or right, above the mid-screen text. It is always shown; an unknown word is read as center [Default=center]").define("pregenLogo", "center");
            spawnChunkRadius = builder.comment("How far from the spawn point, in chunks, chunks are held loaded whether or not a player is there. -1 leaves the game's own value. On 1.20.1 the game has no such setting and this does nothing; on 1.21.1 it sets the spawnChunkRadius game rule when a world starts [Default=-1]").defineInRange("spawnChunkRadius", -1, -1, 32);
            builder.pop();
        }

        public int pregenOnNewWorld() { return loaded() ? pregenOnNewWorld.get() : 0; }

        public boolean pregenToBorder() { return loaded() && pregenToBorder.get(); }

        public int pregenBorderLimit() { return loaded() ? pregenBorderLimit.get() : 8192; }

        public List<String> pregenDimensions() { return loaded() ? List.copyOf(pregenDimensions.get()) : List.of("minecraft:overworld"); }

        public boolean pregenAllDimensions() { return loaded() && pregenAllDimensions.get(); }

        public List<String> pregenDimensionsWhenEntered() { return loaded() ? List.copyOf(pregenDimensionsWhenEntered.get()) : List.of(); }

        public boolean pregenResume() { return loaded() && pregenResume.get(); }

        public int pregenChunksInFlight() { return loaded() ? pregenChunksInFlight.get() : 32; }

        public String pregenRunningSays() { return loaded() ? pregenRunningSays.get() : PREGEN_RUNNING; }

        public String pregenFinishedSays() { return loaded() ? pregenFinishedSays.get() : PREGEN_FINISHED; }

        public String pregenStoppedSays() { return loaded() ? pregenStoppedSays.get() : PREGEN_STOPPED; }

        public String pregenSpectatingSays() { return loaded() ? pregenSpectatingSays.get() : PREGEN_SPECTATING; }

        public String pregenLogo() { return loaded() ? pregenLogo.get() : "center"; }

        public int spawnChunkRadius() { return loaded() ? spawnChunkRadius.get() : -1; }

        public boolean saysCard() { return loaded() ? saysCard.get() : ConfigCore.flag("chunks.saysCard", false); }

        public String saysIcon() { return loaded() ? saysIcon.get() : ConfigCore.text("chunks.saysIcon", ""); }

        public String saysColor() { return loaded() ? saysColor.get() : ConfigCore.text("chunks.saysColor", ""); }

        public String saysImage() { return loaded() ? saysImage.get() : ConfigCore.text("chunks.saysImage", ""); }

        public List<String> welcomeSays() { return loaded() ? List.copyOf(welcomeSays.get()) : List.of(WELCOME); }
    }

    public static final class Entities {
        private final ForgeConfigSpec.BooleanValue slowDistantEntities;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> slowedKinds;
        private final ForgeConfigSpec.IntValue slowDistance;
        private final ForgeConfigSpec.IntValue slowRate;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> neverSlowed;
        private final ForgeConfigSpec.IntValue slowRecheck;

        private Entities(ForgeConfigSpec.Builder builder) {
            builder.comment("How entities far from every player are ticked").push("entities");
            slowDistantEntities = builder.comment("Tick entities far from every player less often. Nothing is ever left unticked, only ticked at a slower pace [Default=true]").define("slowDistantEntities", true);
            slowedKinds = builder.comment("Which kinds are given fewer ticks: items, experience, projectiles. Anything that thinks for itself is always given a slower pace instead, without being named here, and machines are never slowed [Default=[items, experience]]").defineList("slowedKinds", List.of("items", "experience"), each -> each instanceof String);
            slowDistance = builder.comment("How far from the nearest player, in blocks, before a chunk is slowed. The game stops telling a player about most entities beyond 64, so nothing below that [Default=192]").defineInRange("slowDistance", 192, 64, 4096);
            slowRate = builder.comment("One tick in this many is given to a slowed chunk. 1 is no slowing at all, 20 is once a second [Default=4]").defineInRange("slowRate", 4, 1, 20);
            neverSlowed = builder.comment("Entities left alone however far away they are, as namespace:name [Default=[]]").defineList("neverSlowed", List.of(), each -> each instanceof String);
            slowRecheck = builder.comment("How often, in ticks, the distance to the nearest player is worked out again. Every player counts for themselves, so someone alone far away still has their own quiet space around them [Default=20]").defineInRange("slowRecheck", 20, 1, 100);
            builder.pop();
        }

        public boolean slowDistantEntities() { return !loaded() || slowDistantEntities.get(); }

        public List<String> slowedKinds() { return loaded() ? List.copyOf(slowedKinds.get()) : List.of("items", "experience"); }

        public int slowDistance() { return loaded() ? slowDistance.get() : 192; }

        public int slowRate() { return loaded() ? slowRate.get() : 4; }

        public List<String> neverSlowed() { return loaded() ? List.copyOf(neverSlowed.get()) : List.of(); }

        public int slowRecheck() { return loaded() ? slowRecheck.get() : 20; }
    }

    public static final class Control {
        private final ForgeConfigSpec.ConfigValue<String> terrain;
        private final ForgeConfigSpec.ConfigValue<String> replacements;
        private final ForgeConfigSpec.ConfigValue<String> entities;
        private final ForgeConfigSpec.ConfigValue<String> chunks;
        private final ForgeConfigSpec.ConfigValue<String> bedrock;
        private final ForgeConfigSpec.ConfigValue<String> voidWorld;
        private final ForgeConfigSpec.ConfigValue<String> ores;
        private final ForgeConfigSpec.ConfigValue<String> biomes;
        private final ForgeConfigSpec.ConfigValue<String> spawning;
        private final ForgeConfigSpec.ConfigValue<String> structures;

        private Control(ForgeConfigSpec.Builder builder) {
            builder.comment("Who decides each group of settings: default lets the active world template override the config, global uses the config alone, off turns the group off").push("control");
            terrain = builder.comment("The world's name, seed and game mode at creation, and the rest of the terrain group as it is ported [default|global|off]").define("terrain", "default");
            chunks = builder.comment("The welcome lines and the says card, and the rest of the chunks group as it is ported [default|global|off]").define("chunks", "default");
            bedrock = builder.comment("Flat bedrock and its dimension and biome lists [default|global|off]").define("bedrock", "default");
            voidWorld = builder.comment("Void world generation and its platform [default|global|off]").define("voidWorld", "default");
            ores = builder.comment("Blocking ore generation by mod and by ore type [default|global|off]").define("ores", "default");
            biomes = builder.comment("Blocking biomes by mod and by name, and what replaces them [default|global|off]").define("biomes", "default");
            spawning = builder.comment("Mob spawn caps, hostile spawn rates and the light cap [default|global|off]").define("spawning", "default");
            replacements = builder.comment("Block replacement in chunks that already exist [default|global|off]").define("replacements", "default");
            entities = builder.comment("The slower pace of entities far from every player [default|global|off]").define("entities", "default");
            structures = builder.comment("Vanilla structures switched off, their spacing, separation, spawn distance, biomes, spawns, pins and terrain adaptation [default|global|off]").define("structures", "default");
            builder.pop();
        }

        public String terrain() { return loaded() ? terrain.get() : ConfigCore.text("control.terrain", "default"); }

        public String chunks() { return loaded() ? chunks.get() : ConfigCore.text("control.chunks", "default"); }

        public String bedrock() { return loaded() ? bedrock.get() : ConfigCore.text("control.bedrock", "default"); }

        public String voidWorld() { return loaded() ? voidWorld.get() : ConfigCore.text("control.voidWorld", "default"); }

        public String ores() { return loaded() ? ores.get() : ConfigCore.text("control.ores", "default"); }

        public String biomes() { return loaded() ? biomes.get() : ConfigCore.text("control.biomes", "default"); }

        public String spawning() { return loaded() ? spawning.get() : ConfigCore.text("control.spawning", "default"); }

        public String structures() { return loaded() ? structures.get() : ConfigCore.text("control.structures", "default"); }

        public String replacements() { return loaded() ? replacements.get() : ConfigCore.text("control.replacements", "default"); }

        public String entities() { return loaded() ? entities.get() : ConfigCore.text("control.entities", "default"); }
    }
}
