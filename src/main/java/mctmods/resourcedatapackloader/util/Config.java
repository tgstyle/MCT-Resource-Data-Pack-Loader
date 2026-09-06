package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public final class Config {
    public static final ForgeConfigSpec SPEC;
    public static final Packs packs;
    public static final Content content;
    public static final Recipes recipes;
    public static final Data data;
    public static final Worldgen worldgen;
    public static final Tweaks tweaks;
    public static final Chunks chunks;
    public static final Control control;
    public static final String WELCOME = "Welcome to your World!";

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        packs = new Packs(builder);
        content = new Content(builder);
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
        private final ForgeConfigSpec.BooleanValue lenientPaths;
        private final ForgeConfigSpec.BooleanValue experimentalWarning;

        private Tweaks(ForgeConfigSpec.Builder builder) {
            builder.comment("Small changes to how vanilla behaves").push("tweaks");
            lenientPaths = builder.comment("Paths and tilled ground can be made under a block and stay there when one is placed above [Default=true]").define("lenientPaths", true);
            experimentalWarning = builder.comment("Show the game's experimental settings warning when a world is made or opened. Off answers it as if you had clicked proceed [Default=false]").define("experimentalWarning", false);
            builder.pop();
        }

        public boolean lenientPaths() { return loaded() ? lenientPaths.get() : ConfigCore.flag("tweaks.lenientPaths", true); }

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
            worldMinHeight = builder.comment("The lowest block of the overworld, a multiple of 16 down to -2032. The game's own bottom is -64; lower makes a deep world under the vanilla terrain, solid stone until the worldgen layer carves it. Only applied through the generated preset [Default=-64]").defineInRange("worldMinHeight", -64, -2032, 2016);
            worldMaxHeight = builder.comment("The block above the overworld's top, a multiple of 16 up to 2032, at most 4064 above worldMinHeight. The game's own top is 320; higher leaves open sky above the vanilla terrain [Default=320]").defineInRange("worldMaxHeight", 320, -2016, 2032);
            deepStone = builder.comment("The block the world below the vanilla terrain is made of when worldMinHeight goes under -64, such as a pack's own deepslate. It blends into deepslate across the eight layers under -64 the way deepslate blends into stone. Empty keeps stone [Default=empty]").define("deepStone", "");
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
    }

    public static final class Chunks {
        private final ForgeConfigSpec.BooleanValue saysCard;
        private final ForgeConfigSpec.ConfigValue<String> saysIcon;
        private final ForgeConfigSpec.ConfigValue<String> saysColor;
        private final ForgeConfigSpec.ConfigValue<String> saysImage;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> welcomeSays;

        private Chunks(ForgeConfigSpec.Builder builder) {
            builder.comment("What this mod says to players and how it shows it. The chunk loading and land-making keys of the 1.12.2 line arrive with the worldgen layer").push("chunks");
            saysCard = builder.comment("Show the lines this mod says, the welcome and later the land-making progress and the threat lines, as a card in the lower right corner instead of in chat. The card slides in, stays eight seconds and fades, and shows over an open screen too [Default=false]").define("saysCard", false);
            saysIcon = builder.comment("An item drawn on the card, e.g. minecraft:compass. Empty draws none [Default=]").define("saysIcon", "");
            saysColor = builder.comment("The card's background color as hex, e.g. 1E2630. Empty uses a dark slate [Default=]").define("saysColor", "");
            saysImage = builder.comment("A PNG from the pack's client assets stretched over the card as its background, e.g. rubyworld:textures/gui/card.png, drawn over the color. Empty draws none [Default=]").define("saysImage", "");
            welcomeSays = builder.comment("Welcome lines, shown in green on every login. A bare entry is the line for everywhere; a dimension=message entry overrides it for that dimension and also greets every arrival there, e.g. minecraft:the_nether=Welcome to the Nether!. An empty message after the = mutes that dimension; an empty list shows nothing. Left at this default it speaks each player's language [Default=[Welcome to your World!]]").defineList("welcomeSays", List.of(WELCOME), each -> each instanceof String);
            builder.pop();
        }

        public boolean saysCard() { return loaded() ? saysCard.get() : ConfigCore.flag("chunks.saysCard", false); }

        public String saysIcon() { return loaded() ? saysIcon.get() : ConfigCore.text("chunks.saysIcon", ""); }

        public String saysColor() { return loaded() ? saysColor.get() : ConfigCore.text("chunks.saysColor", ""); }

        public String saysImage() { return loaded() ? saysImage.get() : ConfigCore.text("chunks.saysImage", ""); }

        public List<String> welcomeSays() { return loaded() ? List.copyOf(welcomeSays.get()) : List.of(WELCOME); }
    }

    public static final class Control {
        private final ForgeConfigSpec.ConfigValue<String> terrain;
        private final ForgeConfigSpec.ConfigValue<String> chunks;
        private final ForgeConfigSpec.ConfigValue<String> bedrock;
        private final ForgeConfigSpec.ConfigValue<String> voidWorld;

        private Control(ForgeConfigSpec.Builder builder) {
            builder.comment("Who decides each group of settings: default lets the active world template override the config, global uses the config alone, off turns the group off").push("control");
            terrain = builder.comment("The world's name, seed and game mode at creation, and the rest of the terrain group as it is ported [default|global|off]").define("terrain", "default");
            chunks = builder.comment("The welcome lines and the says card, and the rest of the chunks group as it is ported [default|global|off]").define("chunks", "default");
            bedrock = builder.comment("Flat bedrock and its dimension and biome lists [default|global|off]").define("bedrock", "default");
            voidWorld = builder.comment("Void world generation and its platform [default|global|off]").define("voidWorld", "default");
            builder.pop();
        }

        public String terrain() { return loaded() ? terrain.get() : ConfigCore.text("control.terrain", "default"); }

        public String chunks() { return loaded() ? chunks.get() : ConfigCore.text("control.chunks", "default"); }

        public String bedrock() { return loaded() ? bedrock.get() : ConfigCore.text("control.bedrock", "default"); }

        public String voidWorld() { return loaded() ? voidWorld.get() : ConfigCore.text("control.voidWorld", "default"); }
    }
}
