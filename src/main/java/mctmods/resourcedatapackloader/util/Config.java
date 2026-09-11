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
    public static final Commands commands;
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
        commands = new Commands(builder);
        control = new Control(builder);
        SPEC = builder.build();
    }

    private Config() {}

    public static boolean loaded() { return SPEC.isLoaded(); }

    public static boolean contentOff() { return !content.load() || content.vanillaClients(); }

    public static boolean definitionsOff() { return !content.load(); }

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
        private final ForgeConfigSpec.BooleanValue biomes;
        private final ForgeConfigSpec.BooleanValue dimensions;
        private final ForgeConfigSpec.BooleanValue villages;
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
            biomes = builder.comment("Register the biomes described by biomes/*.json in packs and place them into world generation. Requires a restart [Default=true]").worldRestart().define("biomes", true);
            dimensions = builder.comment("Register the dimensions described by dimensions/*.json in packs. Turning this off leaves worlds that contain them unable to load those dimensions. Requires a restart [Default=true]").worldRestart().define("dimensions", true);
            villages = builder.comment("Register the village plots described by villages/*.json in packs so cities and villages can build them. Requires a restart [Default=true]").worldRestart().define("villages", true);
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

        public boolean biomes() { return loaded() ? biomes.get() : ConfigCore.flag("content.biomes", true); }

        public boolean dimensions() { return loaded() ? dimensions.get() : ConfigCore.flag("content.dimensions", true); }

        public boolean villages() { return loaded() ? villages.get() : ConfigCore.flag("content.villages", true); }

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
            recipeWhitelist = builder.comment("Mod ids whose crafting recipes survive while blockRecipes is on. Include your pack's namespace to keep its own recipes").defineListAllowEmpty("recipeWhitelist", List.of("minecraft"), each -> each instanceof String);
            blockedRecipeMods = builder.comment("Mod ids whose crafting recipes are removed outright, whoever they belong to and whatever the whitelist says").defineListAllowEmpty("blockedRecipeMods", List.of(), each -> each instanceof String);
            recipeMatch = builder.comment("What the mod id is read from when blocking crafting recipes. 'recipe' uses the recipe's own name, 'output' uses the item it makes, 'both' blocks if either matches and spares if either is whitelisted [Default=recipe]").define("recipeMatch", "recipe");
            blockFurnaceRecipes = builder.comment("Remove every furnace, blast furnace, smoker and campfire recipe, keeping only the mods in furnaceWhitelist. The mod is read from the item produced [Default=false]").define("blockFurnaceRecipes", false);
            furnaceWhitelist = builder.comment("Mod ids whose furnace recipes survive while blockFurnaceRecipes is on. Include your pack's namespace to keep its own recipes").defineListAllowEmpty("furnaceWhitelist", List.of("minecraft"), each -> each instanceof String);
            blockedFurnaceMods = builder.comment("Mod ids whose furnace recipes are removed outright, whatever the whitelist says").defineListAllowEmpty("blockedFurnaceMods", List.of(), each -> each instanceof String);
            logBlockedRecipes = builder.comment("Log a per mod count of what was blocked, so you can see what to whitelist [Default=true]").define("logBlockedRecipes", true);
            builder.pop();
        }

        public boolean furnace() { return furnace.get(); }

        public boolean removals() { return removals.get(); }

        public boolean skipMissingItems() { return skipMissingItems.get(); }

        public boolean blockRecipes() { return blockRecipes.get(); }

        public List<String> recipeWhitelist() { return loaded() ? List.copyOf(recipeWhitelist.get()) : List.of("minecraft"); }

        public List<String> blockedRecipeMods() { return loaded() ? List.copyOf(blockedRecipeMods.get()) : List.of(); }

        public String recipeMatch() { return recipeMatch.get(); }

        public boolean blockFurnaceRecipes() { return blockFurnaceRecipes.get(); }

        public List<String> furnaceWhitelist() { return loaded() ? List.copyOf(furnaceWhitelist.get()) : List.of("minecraft"); }

        public List<String> blockedFurnaceMods() { return loaded() ? List.copyOf(blockedFurnaceMods.get()) : List.of(); }

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
        private final ForgeConfigSpec.BooleanValue privacy;
        private final ForgeConfigSpec.BooleanValue darkSplash;

        private Tweaks(ForgeConfigSpec.Builder builder) {
            builder.comment("Small changes to how vanilla behaves").push("tweaks");
            promptLeafDecay = builder.comment("Leaves that lose their tree decay within a second instead of waiting on random ticks [Default=true]").define("promptLeafDecay", true);
            lenientPaths = builder.comment("Paths and tilled ground can be made under a block and stay there when one is placed above [Default=true]").define("lenientPaths", true);
            unbreakableSpawners = builder.comment("Mob spawners cannot be mined or blown up. Creative mode still removes them. Requires a restart [Default=false]").define("unbreakableSpawners", false);
            experimentalWarning = builder.comment("Show the game's experimental settings warning when a world is made or opened. Off answers it as if you had clicked proceed [Default=false]").define("experimentalWarning", false);
            privacy = builder.comment("Turn off the game's telemetry and chat reporting: no telemetry event is sent or logged, the client signs no chat message, the server keeps no chat session and does not require one, so no message anybody sends can be reported. A pack cannot set this. Takes effect on the next world or server joined [Default=true]").define("privacy", true);
            darkSplash = builder.comment("Draw the loading screen dark with the pack loader's logo in place of the game's: the logo is swapped as the screen is made, and the game's own Monochrome Logo option is turned on when it is still off, which takes effect at the next start. Off leaves the option as it is [Default=true]").define("darkSplash", true);
            builder.pop();
        }

        public boolean promptLeafDecay() { return loaded() ? promptLeafDecay.get() : ConfigCore.flag("tweaks.promptLeafDecay", true); }

        public boolean lenientPaths() { return loaded() ? lenientPaths.get() : ConfigCore.flag("tweaks.lenientPaths", true); }

        public boolean unbreakableSpawners() { return loaded() ? unbreakableSpawners.get() : ConfigCore.flag("tweaks.unbreakableSpawners", false); }

        public boolean experimentalWarning() { return loaded() ? experimentalWarning.get() : ConfigCore.flag("tweaks.experimentalWarning", false); }

        public boolean privacy() { return loaded() ? privacy.get() : ConfigCore.flag("tweaks.privacy", true); }

        public boolean darkSplash() { return loaded() ? darkSplash.get() : ConfigCore.flag("tweaks.darkSplash", true); }
    }

    public static final class Worldgen {
        private final ForgeConfigSpec.BooleanValue load;
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
        private final ForgeConfigSpec.BooleanValue logBlockedOres;
        private final ForgeConfigSpec.BooleanValue logBlockedBiomes;
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
        private final ForgeConfigSpec.IntValue caveRegionCells;
        private final ForgeConfigSpec.IntValue caveRegionCellsY;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureSpacing;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureSeparation;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureMost;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureSpawners;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureMinDistanceFromSpawn;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureBiomes;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureBiomesAreBlacklist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureSpawns;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureAt;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> structureAdaptation;
        private final ForgeConfigSpec.BooleanValue terrainAdaptation;
        private final ForgeConfigSpec.ConfigValue<String> villagePathBlock;
        private final ForgeConfigSpec.IntValue villagePathExtraWidth;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villageBlockSizes;
        private final ForgeConfigSpec.IntValue villageCitySpacing;
        private final ForgeConfigSpec.ConfigValue<String> villagePathAlleyBlock;
        private final ForgeConfigSpec.IntValue villagePathAlleyChance;
        private final ForgeConfigSpec.IntValue villagePathMinimumWidth;
        private final ForgeConfigSpec.IntValue villagePathFlatRun;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePieces;
        private final ForgeConfigSpec.BooleanValue villagePiecesAreBlacklist;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villageBlocks;
        private final ForgeConfigSpec.IntValue villagePlotsLeast;
        private final ForgeConfigSpec.IntValue villagePlotsMost;
        private final ForgeConfigSpec.BooleanValue villagePlotsBackRow;
        private final ForgeConfigSpec.ConfigValue<String> villageLayout;
        private final ForgeConfigSpec.ConfigValue<String> villagePathCenterBlock;
        private final ForgeConfigSpec.IntValue villagePathCenterDash;
        private final ForgeConfigSpec.ConfigValue<String> villagePathLineBlock;
        private final ForgeConfigSpec.ConfigValue<String> villagePathSidewalkBlock;
        private final ForgeConfigSpec.IntValue villagePathSidewalkWidth;
        private final ForgeConfigSpec.ConfigValue<String> villagePathLampBlock;
        private final ForgeConfigSpec.IntValue villagePathLampHeight;
        private final ForgeConfigSpec.ConfigValue<String> villagePathLampTopBlock;
        private final ForgeConfigSpec.ConfigValue<String> villagePathLampSideBlock;
        private final ForgeConfigSpec.ConfigValue<String> villagePathLampStructure;
        private final ForgeConfigSpec.ConfigValue<String> villagePathSupportBlock;
        private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeBlock;
        private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeSidewalkBlock;
        private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeBarrierBlock;
        private final ForgeConfigSpec.IntValue villagePathBridgeBarrierHeight;
        private final ForgeConfigSpec.IntValue villagePathBridgeDrop;
        private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeFrameBlock;
        private final ForgeConfigSpec.ConfigValue<String> villagePathBridgeFrameTopBlock;
        private final ForgeConfigSpec.IntValue villagePathBridgeFrameHeight;
        private final ForgeConfigSpec.IntValue villagePathBridgeFrameRun;
        private final ForgeConfigSpec.IntValue villagePathBridgeFrameLeast;
        private final ForgeConfigSpec.ConfigValue<String> villagePathTunnelBlock;
        private final ForgeConfigSpec.IntValue villagePathTunnelDepth;
        private final ForgeConfigSpec.ConfigValue<String> villagePathTunnelLightBlock;
        private final ForgeConfigSpec.IntValue villagePathTunnelLightRun;
        private final ForgeConfigSpec.IntValue villageRailLines;
        private final ForgeConfigSpec.IntValue villageRailSpacing;
        private final ForgeConfigSpec.ConfigValue<String> villageRailDirection;
        private final ForgeConfigSpec.IntValue villageRailWidth;
        private final ForgeConfigSpec.ConfigValue<String> villageRailBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageRailTrackSeat;
        private final ForgeConfigSpec.ConfigValue<String> villageRailBedBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageRailTieBlock;
        private final ForgeConfigSpec.IntValue villageRailTieRun;
        private final ForgeConfigSpec.IntValue villageRailTracks;
        private final ForgeConfigSpec.IntValue villageRailTrackGap;
        private final ForgeConfigSpec.ConfigValue<String> villageRailShoulderBlock;
        private final ForgeConfigSpec.IntValue villageRailShoulderWidth;
        private final ForgeConfigSpec.ConfigValue<String> villageRailPowerBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageRailPowerBase;
        private final ForgeConfigSpec.IntValue villageRailPowerRun;
        private final ForgeConfigSpec.IntValue villageRailClimb;
        private final ForgeConfigSpec.IntValue villageRailTail;
        private final ForgeConfigSpec.ConfigValue<String> villageRailSupportBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageRailDeckBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageRailBarrierBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageRailBridgeFrameBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageRailBridgeFrameTopBlock;
        private final ForgeConfigSpec.IntValue villageRailBridgeFrameHeight;
        private final ForgeConfigSpec.IntValue villageRailBridgeFrameRun;
        private final ForgeConfigSpec.IntValue villageRailBridgeFrameLeast;
        private final ForgeConfigSpec.ConfigValue<String> villageRailTunnelBlock;
        private final ForgeConfigSpec.IntValue villageRailTunnelDepth;
        private final ForgeConfigSpec.ConfigValue<String> villageRailTunnelLightBlock;
        private final ForgeConfigSpec.IntValue villageRailTunnelLightRun;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villageWellStructure;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePathDeadEnds;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePathPiers;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePathPierCargo;
        private final ForgeConfigSpec.ConfigValue<String> villagePathPierLoot;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villagePathIntersects;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> villageDecor;
        private final ForgeConfigSpec.IntValue villageSubwayLines;
        private final ForgeConfigSpec.IntValue villageSubwayDepth;
        private final ForgeConfigSpec.IntValue villageSubwaySpacing;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayDirection;
        private final ForgeConfigSpec.IntValue villageSubwayWidth;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayTrackSeat;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayBedBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayTieBlock;
        private final ForgeConfigSpec.IntValue villageSubwayTieRun;
        private final ForgeConfigSpec.IntValue villageSubwayTracks;
        private final ForgeConfigSpec.IntValue villageSubwayTrackGap;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayShoulderBlock;
        private final ForgeConfigSpec.IntValue villageSubwayShoulderWidth;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayPowerBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayPowerBase;
        private final ForgeConfigSpec.IntValue villageSubwayPowerRun;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayTunnelBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayTunnelLightBlock;
        private final ForgeConfigSpec.IntValue villageSubwayTunnelLightRun;
        private final ForgeConfigSpec.IntValue villageSubwayClimb;
        private final ForgeConfigSpec.IntValue villageSubwayTail;
        private final ForgeConfigSpec.IntValue villageSubwayStationLength;
        private final ForgeConfigSpec.IntValue villageSubwayStationRun;
        private final ForgeConfigSpec.IntValue villageSubwayPlatformWidth;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayPlatformBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayStairBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayRailingBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayBenchBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayBenchEndBlock;
        private final ForgeConfigSpec.IntValue villageSubwayBenchLength;
        private final ForgeConfigSpec.IntValue villageSubwaySurfaces;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayStation;
        private final ForgeConfigSpec.ConfigValue<String> villageSubwayEntrance;
        private final ForgeConfigSpec.IntValue villageSubwayStationFoot;
        private final ForgeConfigSpec.IntValue villageSubwayStationRepeat;
        private final ForgeConfigSpec.ConfigValue<String> villageSewerBlock;
        private final ForgeConfigSpec.IntValue villageSewerDepth;
        private final ForgeConfigSpec.IntValue villageSewerHeight;
        private final ForgeConfigSpec.IntValue villageSewerWidth;
        private final ForgeConfigSpec.ConfigValue<String> villageSewerWaterBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSewerWalkBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSewerLightBlock;
        private final ForgeConfigSpec.IntValue villageSewerLightRun;
        private final ForgeConfigSpec.ConfigValue<String> villageSewerLadderBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSewerCoverBlock;
        private final ForgeConfigSpec.ConfigValue<String> villageSewerMossBlock;
        private final ForgeConfigSpec.IntValue villageSewerMossChance;
        private final ForgeConfigSpec.ConfigValue<String> villageSewerVineBlock;
        private final ForgeConfigSpec.IntValue villageSewerVineChance;
        private final ForgeConfigSpec.BooleanValue villageSewerWellEntrance;
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
            load = builder.comment("Generate what the worldgen/*.json entries describe in new chunks. Chunks that already exist are not changed [Default=true]").define("load", true);
            worldgenDebug = builder.comment("Write the debug lines other messages refer to into logs/rdpl.log, such as which pack served a file and what each command did. Very verbose [Default=false]").define("worldgenDebug", false);
            worldTemplate = builder.comment("Which world template's settings apply. A pack adds one in worldtemplates/*.json and you name it here as namespace:name. 'auto' picks the template from the highest priority pack. Empty uses none [Default=auto]").define("worldTemplate", "auto");
            worldSeed = builder.comment("The seed every new world is made with, whatever was typed when it was made, written the same way it would be typed. Empty leaves the choice alone [Default=empty]").define("worldSeed", "");
            worldGameMode = builder.comment("Which way every new world is started, one of survival, hardcore, creative, adventure or spectator. Hardcore is survival where death ends the world, save wide, the same as the choice on the world screen. Empty leaves it as whoever made the world chose [Default=empty]").define("worldGameMode", "");
            worldName = builder.comment("What a new world is called when the screen for making one opens. Empty leaves it as the game names it [Default=empty]").define("worldName", "");
            worldType = builder.comment("The world type the shaped world is built on, one of default, largebiomes, amplified or flat; flat is a superflat overworld built from the generatorOptions layers, with the pack's cities on it. The shape below (heights, deep stone, sea level, bedrock, void) is generated as a world preset of its own, listed under World Type on the world screen and chosen there whatever was picked. Empty builds on default [Default=empty]").define("worldType", "");
            worldTypeExceptions = builder.comment("World types a player picks that the generated preset leaves alone, such as flat or debug_all_block_states. Empty means every choice is replaced [Default=[flat, debug_all_block_states]]").defineListAllowEmpty("worldTypeExceptions", List.of("flat", "debug_all_block_states"), each -> each instanceof String);
            tellWorldType = builder.comment("Tell a player in chat, as they join a world made with the generated preset, which template shaped it. A pack cannot set this [Default=true]").define("tellWorldType", true);
            generatorOptions = builder.comment("The overworld's terrain settings as a JSON object, the keys the 1.12.2 customized world type wrote. Read here: seaLevel and useLavaOceans. With worldType flat it is the layers instead, bottom up, as the 1.12.2 superflat text 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village or a list of layers, and a village, mineshaft or stronghold named after the biome turns that vanilla structure on. Only applied to a world as it is created. Empty leaves the terrain as the world type makes it [Default=empty]").define("generatorOptions", "");
            worldMinHeight = builder.comment("The lowest block of the overworld, a multiple of 16 down to -2032. The game's own bottom is -64; lower makes a deep world under the vanilla terrain, solid stone until the worldgen layer carves it or noiseCaves carries the game's caves down. Only applied through the generated preset [Default=-64]").defineInRange("worldMinHeight", -64, -2032, 2016);
            worldMaxHeight = builder.comment("The block above the overworld's top, a multiple of 16 up to 2032, at most 4064 above worldMinHeight. The game's own top is 320; higher leaves open sky above the vanilla terrain [Default=320]").defineInRange("worldMaxHeight", 320, -2016, 2032);
            deepStone = builder.comment("The block the world below the vanilla terrain is made of when worldMinHeight goes under -64, such as a pack's own deepslate. It blends into deepslate across the eight layers under -64 the way deepslate blends into stone. Empty keeps stone [Default=empty]").define("deepStone", "");
            noiseCaves = builder.comment("Where the game's caves, tunnels, noodles and aquifers carry on when worldMinHeight goes under -64: off keeps the world under the vanilla terrain solid deep stone for the worldgen layer to carve, deep carries them down to the floor with the lava lakes moved to its bottom ten layers, world means the same on this version because the vanilla terrain has them already [Default=off]").define("noiseCaves", "off");
            worldSpawn = builder.comment("Where every new world spawns, written as x,z or x,y,z. Without a y the ground at that spot is used. Only applied to a world as it is created. Empty leaves the choice to the game [Default=empty]").define("worldSpawn", "");
            worldBorder = builder.comment("How far across, in blocks, the world border stands in every new world. Only applied to a world as it is created. 0 leaves the border where the game puts it [Default=0]").defineInRange("worldBorder", 0, 0, 60000000);
            worldBorderLimit = builder.comment("The widest border a pack is allowed to ask for through worldBorder. A pack asking for more is refused and the border is left where the game puts it. A pack cannot set this [Default=60000000]").defineInRange("worldBorderLimit", 60000000, 1, 60000000);
            worldTime = builder.comment("Lock the overworld's time of day, in ticks, the same figure /time set takes, so 18000 is midnight. The clock stops and never moves. -1 leaves time running [Default=-1]").defineInRange("worldTime", -1, -1, 23999);
            worldDifficulty = builder.comment("Lock the difficulty, one of peaceful, easy, normal or hard, for the whole world. Difficulty is save wide on this version, so an entry written as dimension=difficulty is read for the overworld alone. Empty leaves it as chosen [Default=empty]").defineListAllowEmpty("worldDifficulty", List.of(), each -> each instanceof String);
            flatBedrock = builder.comment("Replace the jagged bedrock at the bottom of the world with flat layers, through the generated preset, so it shapes new worlds made with it [Default=false]").define("flatBedrock", false);
            flatBedrockDimensions = builder.comment("Dimensions to flatten bedrock in, by id such as minecraft:the_nether. Leave empty for every dimension [Default=[minecraft:overworld]]").defineListAllowEmpty("flatBedrockDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
            flatBedrockDimensionsAreBlacklist = builder.comment("On, flattening skips these dimensions. Off, it applies only to them [Default=false]").define("flatBedrockDimensionsAreBlacklist", false);
            bedrockLayers = builder.comment("How many layers of bedrock to leave at the bottom [Default=1]").defineInRange("bedrockLayers", 1, 1, 5);
            flatBedrockBiomes = builder.comment("Biomes to flatten bedrock in, by id such as minecraft:birch_forest. Empty means every biome; elsewhere the bedrock stays as the game makes it").defineListAllowEmpty("flatBedrockBiomes", List.of(), each -> each instanceof String);
            flatBedrockBiomesAreBlacklist = builder.comment("On, flattening skips these biomes. Off, it applies only to them [Default=false]").define("flatBedrockBiomesAreBlacklist", false);
            flatBedrockRoof = builder.comment("Flatten the bedrock ceiling too, where a dimension has one, such as the Nether roof [Default=false]").define("flatBedrockRoof", false);
            voidWorld = builder.comment("Generate the listed dimensions as empty space with a platform at the spawn point and nothing living, through the generated preset [Default=false]").define("voidWorld", false);
            voidWorldDimensions = builder.comment("Which dimensions are made void, by id. Empty means the overworld alone [Default=[minecraft:overworld]]").defineListAllowEmpty("voidWorldDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
            voidWorldDimensionsAreBlacklist = builder.comment("Treat voidWorldDimensions as the dimensions to leave alone instead [Default=false]").define("voidWorldDimensionsAreBlacklist", false);
            voidPlatformBlock = builder.comment("The block the void world platform is made of [Default=minecraft:stone]").define("voidPlatformBlock", "minecraft:stone");
            voidPlatformHeight = builder.comment("The y the void world platform sits at [Default=64]").defineInRange("voidPlatformHeight", 64, -2032, 2031);
            voidPlatformSize = builder.comment("How wide the void world platform is, in blocks. Rounded down to an odd number so it centers on the spawn point [Default=9]").defineInRange("voidPlatformSize", 9, 1, 255);
            retrogen = builder.comment("Catch existing chunks up on worldgen entries with \"retrogen\": true. Off, chunks that already exist are left alone. Chunks are marked as they generate either way, so turning this on later only touches chunks older than the pack [Default=false]").define("retrogen", false);
            adoptExistingChunks = builder.comment("Treat chunks that already exist as if this pack generated them, marking them instead of leaving them for retrogen. Turn this on when replacing a mod that already generated the same ore, so retrogen never doubles it. Worldgen entries added later still retrogen into them [Default=false]").define("adoptExistingChunks", false);
            retrogenKey = builder.comment("Change this to make every chunk eligible for retrogen again, for every worldgen entry. New veins are added on top of what is already there [Default=0000]").define("retrogenKey", "0000");
            retrogenChunksPerTick = builder.comment("How many already generated chunks to catch up per tick. Higher is faster but stutters more [Default=2]").defineInRange("retrogenChunksPerTick", 2, 1, 64);
            blockOres = builder.comment("Stop every mod, and Minecraft itself, from generating ores. Only the mods in oreWhitelist still generate. An ore is a placed feature with ore in its id, which is Minecraft's and most mods' [Default=false]").define("blockOres", false);
            logBlockedOres = builder.comment("Log the first time each mod and ore type is blocked, so you can see what to whitelist [Default=true]").define("logBlockedOres", true);
            logBlockedBiomes = builder.comment("Log a per mod count of which biomes were blocked, so you can see what to whitelist [Default=true]").define("logBlockedBiomes", true);
            oreWhitelist = builder.comment("Mod ids allowed to generate ores while blockOres is on. Ores a pack defines belong to that pack's namespace [Default=[minecraft]]").defineListAllowEmpty("oreWhitelist", List.of("minecraft"), each -> each instanceof String);
            prospectItems = builder.comment("Items that prospect for vein shaped worldgen entries when a sneaking player breaks a block with one, as item=entry|entry[,radius in chunks] or item=*[,radius], e.g. minecraft:compass=iron_vein|coal_seam or mypack:rod=*,12. The reading names the ore and a compass direction [Default=[]]").defineListAllowEmpty("prospectItems", List.of(), each -> each instanceof String);
            prospectItemsAreBlacklist = builder.comment("On, the entries named after an item in prospectItems are the ones it does NOT read, and every other vein shaped entry is [Default=false]").define("prospectItemsAreBlacklist", false);
            prospectDrops = builder.comment("Whether a block broken in prospecting mode drops anything. Off, the sample is destroyed: no drops, no experience [Default=false]").define("prospectDrops", false);
            prospectSlow = builder.comment("How many times slower a block breaks in prospecting mode [Default=2]").defineInRange("prospectSlow", 2, 1, 100);
            prospectWear = builder.comment("How much durability a prospecting break costs the item, at least 2 [Default=2]").defineInRange("prospectWear", 2, 2, 1000);
            oreTypes = builder.comment("Ore types this applies to, whoever generates them and whatever the whitelist says. Known types: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM for any other ore [Default=[]]").defineListAllowEmpty("oreTypes", List.of(), each -> each instanceof String);
            oreTypesAreBlacklist = builder.comment("On, oreTypes are blocked. Off, only oreTypes generate [Default=true]").define("oreTypesAreBlacklist", true);
            blockOreDimensions = builder.comment("Dimensions ore blocking applies to, by id such as minecraft:the_nether; read as the overworld, nether and end biome tags. Empty means every dimension [Default=[]]").defineListAllowEmpty("blockOreDimensions", List.of(), each -> each instanceof String);
            blockOreDimensionsAreBlacklist = builder.comment("Treat blockOreDimensions as the dimensions to leave alone instead [Default=false]").define("blockOreDimensionsAreBlacklist", false);
            blockBiomes = builder.comment("Stop every biome from generating except the mods in biomeWhitelist. Blocked biomes become the void biome, or what the world template's roles and fallback name. Blocking every biome makes the overworld a void world [Default=false]").define("blockBiomes", false);
            biomeWhitelist = builder.comment("Mod ids whose biomes still generate while blockBiomes is on. A pack biome uses the pack's namespace [Default=[minecraft]]").defineListAllowEmpty("biomeWhitelist", List.of("minecraft"), each -> each instanceof String);
            biomeNames = builder.comment("Biomes this applies to, whoever owns them and whatever the whitelist says, by id such as minecraft:birch_forest [Default=[]]").defineListAllowEmpty("biomeNames", List.of(), each -> each instanceof String);
            biomeNamesAreBlacklist = builder.comment("On, biomeNames are blocked. Off, only biomeNames generate [Default=true]").define("biomeNamesAreBlacklist", true);
            blockBiomeDimensions = builder.comment("Dimensions biome blocking applies to, by id. Empty means every dimension whose biomes are placed by climate, the overworld and the nether [Default=[minecraft:overworld]]").defineListAllowEmpty("blockBiomeDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
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
            caveRegionCells = builder.comment("How wide a cave region cell is in blocks. Cave regions from packs are painted over the underground in cells about this size [Default=128]").defineInRange("caveRegionCells", 128, 16, 4096);
            caveRegionCellsY = builder.comment("How tall a cave region cell is in blocks [Default=64]").defineInRange("caveRegionCellsY", 64, 16, 4096);
            structureSpacing = builder.comment("How far apart vanilla structures are seeded, in chunks, as structure=chunks entries: the 1.12.2 names temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities and villages, or any structure set id such as pillager_outposts. For mineshafts the number is one chunk in that many; for strongholds it is the ring distance [Default=[]]").defineListAllowEmpty("structureSpacing", List.of(), each -> each instanceof String);
            structureSeparation = builder.comment("The closest two of a structure may be, in chunks, as structure=chunks entries; for strongholds it is the ring spread [Default=[]]").defineListAllowEmpty("structureSeparation", List.of(), each -> each instanceof String);
            structureMost = builder.comment("The most of a structure a dimension may hold, as structure=count entries such as villages=100: once that many have been founded no chunk founds another, chunks pinned with structureAt aside. 0 or an absent entry sets no ceiling [Default=[]]").defineListAllowEmpty("structureMost", List.of(), each -> each instanceof String);
            structureSpawners = builder.comment("What the mob spawner inside a vanilla structure spawns, as structure=namespace:entity entries, comma separated for a random pick. Only dungeons, mineshafts, netherbridges and strongholds build one; spawners other mods place are left alone [Default=[]]").defineListAllowEmpty("structureSpawners", List.of(), each -> each instanceof String);
            structureMinDistanceFromSpawn = builder.comment("How far from the world spawn a structure starts, in blocks, as structure=blocks entries. Measured from the pack's worldSpawn when one is set, else from the world origin, since placement is decided before any spawn exists [Default=[]]").defineListAllowEmpty("structureMinDistanceFromSpawn", List.of(), each -> each instanceof String);
            structureBiomes = builder.comment("Where a structure may generate, as structure=biome,biome entries naming biome ids or biome types such as SANDY [Default=[]]").defineListAllowEmpty("structureBiomes", List.of(), each -> each instanceof String);
            structureBiomesAreBlacklist = builder.comment("Direction of the biome lists, written as structure=true or structure=false, one per line. True takes the listed biomes away, false makes them the only ones [Default=false]").defineListAllowEmpty("structureBiomesAreBlacklist", List.of(), each -> each instanceof String);
            structureSpawns = builder.comment("The mobs a structure spawns whatever the biome says, as structure=namespace:entity:weight:least:most entries, comma separated; an empty list after the = spawns nothing [Default=[]]").defineListAllowEmpty("structureSpawns", List.of(), each -> each instanceof String);
            structureAt = builder.comment("Structures pinned to exact spots, as structure=x,z entries in block coordinates, one per wanted instance. A pinned structure generates in that chunk and nowhere else. city=x,z seats the center district of the city whose region holds that spot [Default=[]]").defineListAllowEmpty("structureAt", List.of(), each -> each instanceof String);
            structureAdaptation = builder.comment("How the terrain adapts to a structure, as structure=mode entries with the modes none, bury, beard_thin, beard_box and encapsulate [Default=[]]").defineListAllowEmpty("structureAdaptation", List.of(), each -> each instanceof String);
            terrainAdaptation = builder.comment("Lay RDPL's own city streets, seated into the terrain instead of standing on stilts over every dip, and read the villagePath and villageRail options with them. Changes the terrain, so a world made with it on differs from one made without. This line lays no streets yet, so turning it on only says so [Default=false]").define("terrainAdaptation", false);
            villagePathBlock = builder.comment("The block city roads are paved with when terrainAdaptation lays them. Empty paves them with dirt path [Default=empty]").define("villagePathBlock", "");
            villagePathExtraWidth = builder.comment("Extra blocks of road width on each side beyond the usual 3, when terrainAdaptation lays the roads. Widens the streets themselves, so the blocks between them stand back from wide roads [Default=0]").defineInRange("villagePathExtraWidth", 0, 0, 16);
            villageBlockSizes = builder.comment("How deep the blocks between a city's parallel streets are, one weighted entry per line written size=weight like 32=3, rolled once per district. Empty uses 32 [Default=[]]").defineListAllowEmpty("villageBlockSizes", List.of(), each -> each instanceof String);
            villageCitySpacing = builder.comment("How far apart city districts are seeded, in districts sized from the plots (twice the largest plot, plus a plaza and a street each side, rounded up to 16 blocks, at least 96): one district in every square of this many carries a city center, seated in the middle half of the square at a spot fixed by the world seed (or where structureAt city=x,z pins it), from which the city grows ring by ring to villagePlotsLeast plots but never past a quarter of this spacing in any direction, so neighboring cities stay apart. At 1 every district is one, a plaza with the well at its center and streets out of it that join the next district's. 0 seeds none [Default=16]").defineInRange("villageCitySpacing", 16, 0, 256);
            villagePathAlleyBlock = builder.comment("The block alleys are laid with. An alley is a street too narrow for lines and sidewalks. Empty lays alleys with the street block [Default=empty]").define("villagePathAlleyBlock", "");
            villagePathAlleyChance = builder.comment("The percent chance a street is laid as an alley rather than at its full width. 0 lays no alleys [Default=0]").defineInRange("villagePathAlleyChance", 0, 0, 100);
            villagePathMinimumWidth = builder.comment("The narrowest street allowed. A street that would be laid narrower than this is not laid at all, and the district lays out around the gap. 0 never refuses [Default=0]").defineInRange("villagePathMinimumWidth", 0, 0, 32);
            villagePathFlatRun = builder.comment("Streets hold each grade for at least this many blocks before stepping, anchored to world coordinates so segments agree across pieces. 0 or 1 lets a street step every block [Default=6]").defineInRange("villagePathFlatRun", 6, 0, 64);
            villagePieces = builder.comment("Village plot definitions named here, one per line, as the full id of a villages file such as mypack:smithy. On 1.12.2 these were the vanilla piece names, which this line does not have [Default=[]]").defineListAllowEmpty("villagePieces", List.of(), each -> each instanceof String);
            villagePiecesAreBlacklist = builder.comment("On, the plots in villagePieces are blocked. Off, only those plots are built [Default=true]").define("villagePiecesAreBlacklist", true);
            villageBlocks = builder.comment("Blocks village plots are built from, as original=replacement pairs, minecraft:cobblestone=mypack:ruby_brick. A pair may carry a chance out of 100, minecraft:cobblestone=minecraft:mossy_cobblestone,20, weighed where the block is laid. Streets are never ruled. Empty leaves every block as the plot's own structure has it [Default=[]]").defineListAllowEmpty("villageBlocks", List.of(), each -> each instanceof String);
            villagePlotsLeast = builder.comment("How many plots a city grows to: districts are added ring by ring around its center until they hold at least this many. 0 lays the center district alone [Default=0]").defineInRange("villagePlotsLeast", 0, 0, 512);
            villagePlotsMost = builder.comment("The most plots a city may hold: growth stops before the district that would pass it, and no district seats more than this. 0 sets no ceiling [Default=0]").defineInRange("villagePlotsMost", 0, 0, 512);
            villagePlotsBackRow = builder.comment("On, a second pass seats a plot directly behind every plot that fronts a street, turned to face it, with the same roll and the same room test, so the inside of a block between two streets is built rather than left bare. Off leaves plots on the street fronts only [Default=true]").define("villagePlotsBackRow", true);
            villageLayout = builder.comment("A city map laid out instead of planning the district, named like mypack:downtown and read from that pack's citymaps folder. Empty plans the district as usual [Default=empty]").define("villageLayout", "");
            villagePathCenterBlock = builder.comment("The block of the center line down the middle of a city street. Empty draws no center line [Default=empty]").define("villagePathCenterBlock", "");
            villagePathCenterDash = builder.comment("Dashes the center line: N blocks of line, then one of street, anchored to world coordinates so segments continue each other. 0 keeps the line solid [Default=0]").defineInRange("villagePathCenterDash", 0, 0, 64);
            villagePathLineBlock = builder.comment("The block of the edge lines between street and sidewalk. Empty draws no edge lines [Default=empty]").define("villagePathLineBlock", "");
            villagePathSidewalkBlock = builder.comment("The block sidewalks are laid with, level with the street, outside the edge lines. Empty lays no sidewalks [Default=empty]").define("villagePathSidewalkBlock", "");
            villagePathSidewalkWidth = builder.comment("How many blocks wide each sidewalk is, when villagePathSidewalkBlock is set. A street too narrow to carry its lines and sidewalks is laid bare instead [Default=2]").defineInRange("villagePathSidewalkWidth", 2, 0, 16);
            villagePathLampBlock = builder.comment("The block a lamp post along a street is built from, stacked villagePathLampHeight tall on the curb. Empty stands no lamp posts [Default=minecraft:oak_fence]").define("villagePathLampBlock", "minecraft:oak_fence");
            villagePathLampHeight = builder.comment("How many blocks tall the lamp post stands before its head [Default=3]").defineInRange("villagePathLampHeight", 3, 1, 32);
            villagePathLampTopBlock = builder.comment("The head that sits on top of a lamp post. Empty leaves the post bare [Default=minecraft:red_wool]").define("villagePathLampTopBlock", "minecraft:red_wool");
            villagePathLampSideBlock = builder.comment("The light hung on each side of a lamp post head. Empty hangs none [Default=minecraft:torch]").define("villagePathLampSideBlock", "minecraft:torch");
            villagePathLampStructure = builder.comment("A structure file placed as the lamp instead of stacking the lamp blocks, named like mypack:street_lamp. Its lowest layer sits on the curb. Empty stacks the lamp blocks [Default=empty]").define("villagePathLampStructure", "");
            villagePathSupportBlock = builder.comment("The block laid one layer under the street surface. Empty lays none [Default=empty]").define("villagePathSupportBlock", "");
            villagePathBridgeBlock = builder.comment("The block a street crosses water with. Empty decks a bridge with the street block [Default=empty]").define("villagePathBridgeBlock", "");
            villagePathBridgeSidewalkBlock = builder.comment("The block bridge sidewalks are decked with where a street crosses water. Empty keeps the normal sidewalk block on bridges [Default=empty]").define("villagePathBridgeSidewalkBlock", "");
            villagePathBridgeBarrierBlock = builder.comment("The block bridge barriers are built from, stacked along both edges of the deck. Empty builds no barriers [Default=empty]").define("villagePathBridgeBarrierBlock", "");
            villagePathBridgeBarrierHeight = builder.comment("How many blocks tall the bridge barriers stand [Default=1]").defineInRange("villagePathBridgeBarrierHeight", 1, 1, 16);
            villagePathBridgeDrop = builder.comment("How far a street's grade must stand clear of the ground before the drop under it is bridged rather than filled solid. 0 keeps streets out of the air, bridging water only [Default=0]").defineInRange("villagePathBridgeDrop", 0, 0, 64);
            villagePathBridgeFrameBlock = builder.comment("The block an overhead frame over a long bridge is built from: a post up each side of the deck and a beam across the top. Empty builds none [Default=empty]").define("villagePathBridgeFrameBlock", "");
            villagePathBridgeFrameTopBlock = builder.comment("The block the beam across the top of that frame is made of. Empty uses villagePathBridgeFrameBlock [Default=empty]").define("villagePathBridgeFrameTopBlock", "");
            villagePathBridgeFrameHeight = builder.comment("How many blocks of clear headroom the frame leaves over the deck. The beam lies one block above that [Default=4]").defineInRange("villagePathBridgeFrameHeight", 4, 1, 32);
            villagePathBridgeFrameRun = builder.comment("How many rows apart the frames stand when a bridge is long enough for several. They are spread symmetrically about the middle of the bridged run [Default=24]").defineInRange("villagePathBridgeFrameRun", 24, 1, 256);
            villagePathBridgeFrameLeast = builder.comment("The shortest bridged run that gets a frame at all, in rows. A shorter bridge is left plain [Default=24]").defineInRange("villagePathBridgeFrameLeast", 24, 1, 256);
            villagePathTunnelBlock = builder.comment("The block a street is lined with where it bores through a hill instead of cutting it open: the walls either side of the bore and the roof over it. Empty bores no tunnels and lets a street climb the hill [Default=empty]").define("villagePathTunnelBlock", "");
            villagePathTunnelDepth = builder.comment("How much ground must stand over the street surface before a stretch is bored as a tunnel rather than climbed. A rise that deep anywhere along it is held level and bored through. Needs villagePathTunnelBlock [Default=10]").defineInRange("villagePathTunnelDepth", 10, 1, 128);
            villagePathTunnelLightBlock = builder.comment("The block set into a tunnel roof down its center line as a light. Empty lights none [Default=empty]").define("villagePathTunnelLightBlock", "");
            villagePathTunnelLightRun = builder.comment("How many blocks apart the tunnel lights sit, anchored to world coordinates so pieces agree [Default=8]").defineInRange("villagePathTunnelLightRun", 8, 1, 64);
            villageRailLines = builder.comment("How many railway lines run through a city, laid before any street so the town grows around them: the first through the city's center district, the rest to either side at least villageRailSpacing apart, rounded up to whole districts so each keeps to the same edge of its blocks, each crossing every district of the city it reaches. At villageCitySpacing 1 every district is a city and carries its own. 0 lays none [Default=0]").defineInRange("villageRailLines", 0, 0, 16);
            villageRailSpacing = builder.comment("The fewest blocks of clear ground between one railway line's bed and the next of the same district. 1 lays them a block apart, which is how a pack builds a yard of parallel lines [Default=48]").defineInRange("villageRailSpacing", 48, 1, 256);
            villageRailDirection = builder.comment("Which way a district's railway lines run: ew for east to west, ns for north to south, any to roll it per district [Default=any]").define("villageRailDirection", "any");
            villageRailWidth = builder.comment("The least the railbed is, in blocks. 3 carries one track down the middle and 5 carries two; a bed asked for more tracks than this fits widens to hold them, and villageRailShoulderWidth is added outside it [Default=3]").defineInRange("villageRailWidth", 3, 3, 32);
            villageRailBlock = builder.comment("The track block laid on the bed. Empty lays vanilla rails, which minecarts ride [Default=empty]").define("villageRailBlock", "");
            villageRailTrackSeat = builder.comment("Where the track sits: auto seats a rail block on the bed and sets any other block into the bed surface, on always lays it on the bed, in always sets it flush into the bed [Default=auto]").define("villageRailTrackSeat", "auto");
            villageRailBedBlock = builder.comment("The bed the track lies on. Empty lays gravel [Default=empty]").define("villageRailBedBlock", "");
            villageRailTieBlock = builder.comment("The sleeper laid across the bed every villageRailTieRun rows. Empty lays oak planks [Default=empty]").define("villageRailTieBlock", "");
            villageRailTieRun = builder.comment("How many rows apart the sleepers lie [Default=2]").defineInRange("villageRailTieRun", 2, 1, 32);
            villageRailTracks = builder.comment("How many tracks the one bed carries, side by side, villageRailTrackGap apart. The bed widens to hold them all. 0 lays one track on a bed under five wide and two on a wider one [Default=0]").defineInRange("villageRailTracks", 0, 0, 8);
            villageRailTrackGap = builder.comment("How many blocks apart the tracks on a bed sit, center to center. 2, the least allowed, leaves one block of bed between them, which is what keeps them from curving into one another the way touching rails do [Default=2]").defineInRange("villageRailTrackGap", 2, 2, 16);
            villageRailShoulderBlock = builder.comment("The block dressing the outermost columns of the bed, a maintenance path beside the track, the railway counterpart of a street sidewalk. Empty lays none and leaves the bed its full width [Default=empty]").define("villageRailShoulderBlock", "");
            villageRailShoulderWidth = builder.comment("How many columns wide that shoulder is on each side, added outside villageRailWidth. Needs villageRailShoulderBlock [Default=1]").defineInRange("villageRailShoulderWidth", 1, 0, 8);
            villageRailPowerBlock = builder.comment("The powered track set into the line every villageRailPowerRun rows. Empty uses a vanilla powered rail; a block that is not a rail is simply laid there [Default=empty]").define("villageRailPowerBlock", "");
            villageRailPowerBase = builder.comment("What sits under a powered track to feed it. Empty uses a redstone block [Default=empty]").define("villageRailPowerBase", "");
            villageRailPowerRun = builder.comment("How many rows apart a powered rail over its base is set into the track, so minecarts keep going. 0 powers none [Default=0]").defineInRange("villageRailPowerRun", 0, 0, 256);
            villageRailClimb = builder.comment("How many rows a railway line runs level for each block it climbs or falls. 1 grades it as steep as a street [Default=8]").defineInRange("villageRailClimb", 8, 1, 64);
            villageRailTail = builder.comment("How far a railway line runs on past the district at either end. Held to 48, which is as far as a structure start reaches [Default=48]").defineInRange("villageRailTail", 48, 0, 48);
            villageRailSupportBlock = builder.comment("The post block under a trestle, where the line runs over water or a drop. Empty uses oak logs [Default=empty]").define("villageRailSupportBlock", "");
            villageRailDeckBlock = builder.comment("The deck a trestle carries the bed on. Empty uses oak planks [Default=empty]").define("villageRailDeckBlock", "");
            villageRailBarrierBlock = builder.comment("Barriers stood along both edges of a trestle deck. Empty stands none [Default=empty]").define("villageRailBarrierBlock", "");
            villageRailBridgeFrameBlock = builder.comment("The block an overhead frame over a long trestle is built from: a post up each side of the deck and a beam across the top. Empty builds none [Default=empty]").define("villageRailBridgeFrameBlock", "");
            villageRailBridgeFrameTopBlock = builder.comment("The block the beam across the top of that frame is made of. Empty uses villageRailBridgeFrameBlock [Default=empty]").define("villageRailBridgeFrameTopBlock", "");
            villageRailBridgeFrameHeight = builder.comment("How many blocks of clear headroom the frame leaves over the deck. The beam lies one block above that [Default=4]").defineInRange("villageRailBridgeFrameHeight", 4, 1, 32);
            villageRailBridgeFrameRun = builder.comment("How many rows apart the frames stand when a trestle is long enough for several. They are spread symmetrically about the middle of the trestle [Default=24]").defineInRange("villageRailBridgeFrameRun", 24, 1, 256);
            villageRailBridgeFrameLeast = builder.comment("The shortest trestle that gets a frame at all, in rows. A shorter trestle is left plain [Default=24]").defineInRange("villageRailBridgeFrameLeast", 24, 1, 256);
            villageRailTunnelBlock = builder.comment("The block a railway line is lined with where it bores through a hill instead of climbing it. Empty bores no tunnels [Default=empty]").define("villageRailTunnelBlock", "");
            villageRailTunnelDepth = builder.comment("How much ground must stand over the bed before a stretch is bored as a tunnel rather than climbed. Needs villageRailTunnelBlock [Default=6]").defineInRange("villageRailTunnelDepth", 6, 1, 128);
            villageRailTunnelLightBlock = builder.comment("The block set into a railway tunnel roof down its center line as a light. Empty lights none [Default=empty]").define("villageRailTunnelLightBlock", "");
            villageRailTunnelLightRun = builder.comment("How many blocks apart those tunnel lights sit, anchored to world coordinates so pieces agree [Default=8]").defineInRange("villageRailTunnelLightRun", 8, 1, 64);
            villageWellStructure = builder.comment("Structure files placed as the plaza centerpiece at the district's middle crossing, one weighted entry per line written name=weight like mypack:plaza_spire=3, rolled once per district. Its lowest layer sits on the plaza floor. Empty places none [Default=[]]").defineListAllowEmpty("villageWellStructure", List.of(), each -> each instanceof String);
            villagePathDeadEnds = builder.comment("How a street that dead ends is closed off, as structure names read from a pack, one per line, rolled per end. Empty closes each dead end with a cul-de-sac instead [Default=[]]").defineListAllowEmpty("villagePathDeadEnds", List.of(), each -> each instanceof String);
            villagePathPiers = builder.comment("Pier styles for a street that dead ends over water: the bridged tail becomes a pier instead of a bridge to nowhere. The styles are railed, pilings and boardwalk; several entries roll one per pier. Empty leaves such a tail a plain bridge [Default=[]]").defineListAllowEmpty("villagePathPiers", List.of(), each -> each instanceof String);
            villagePathPierCargo = builder.comment("Cargo stood along the inside of a pier's rails, as block=weight entries, block=weight,height to stack it, or empty=weight for the share left clear. Every other row rolls the list on each side. Empty leaves piers bare [Default=[]]").defineListAllowEmpty("villagePathPierCargo", List.of(), each -> each instanceof String);
            villagePathPierLoot = builder.comment("The loot table cargo blocks with an inventory are filled from, rolled the first time one is opened. A pack may replace the built-in table by shipping its own loot table at that name. Empty leaves them empty [Default=resourcedatapackloader:chests/pier_cargo]").define("villagePathPierLoot", "resourcedatapackloader:chests/pier_cargo");
            villagePathIntersects = builder.comment("Path intersect designs painted where streets cross, by registry key from a pack's pathintersects folder. One entry paints every crossing alike; several roll one per crossing, weighted by each design. Empty paints nothing [Default=[]]").defineListAllowEmpty("villagePathIntersects", List.of(), each -> each instanceof String);
            villageDecor = builder.comment("Decoration scattered along city streets, as name=weight pairs naming worldgen from a pack, mypack:street_flowers=2. The name empty is the share of spots left bare. Every third block of verge on each side of a street rolls the list. Empty scatters nothing [Default=[]]").defineListAllowEmpty("villageDecor", List.of(), each -> each instanceof String);
            villageSubwayLines = builder.comment("How many underground railway lines a city digs: the first under the center district's street, the rest to either side at least villageSubwaySpacing apart, rounded up to whole districts so each runs under a street. 0 digs none and rolls nothing, so the city is laid exactly as it would be without them [Default=0]").defineInRange("villageSubwayLines", 0, 0, 32);
            villageSubwayDepth = builder.comment("How far under the surface a subway's bed sits. The line is graded from the ground above it, so it follows the land at that depth rather than running level [Default=24]").defineInRange("villageSubwayDepth", 24, 6, 192);
            villageSubwaySpacing = builder.comment("How far apart a city's subway lines are kept from one another [Default=64]").defineInRange("villageSubwaySpacing", 64, 1, 512);
            villageSubwayDirection = builder.comment("Which way subway lines run: x, z, or any to roll per city [Default=any]").define("villageSubwayDirection", "any");
            villageSubwayWidth = builder.comment("How wide the bed is, before shoulders [Default=3]").defineInRange("villageSubwayWidth", 3, 3, 33);
            villageSubwayBlock = builder.comment("The track block. Empty lays vanilla rail [Default=empty]").define("villageSubwayBlock", "");
            villageSubwayTrackSeat = builder.comment("Whether the track sits on the bed, in it, or auto to let the block decide [Default=auto]").define("villageSubwayTrackSeat", "auto");
            villageSubwayBedBlock = builder.comment("The block the bed is made of. Empty uses gravel [Default=empty]").define("villageSubwayBedBlock", "");
            villageSubwayTieBlock = builder.comment("The block laid across the bed as sleepers. Empty uses planks [Default=empty]").define("villageSubwayTieBlock", "");
            villageSubwayTieRun = builder.comment("How many blocks apart the sleepers sit [Default=2]").defineInRange("villageSubwayTieRun", 2, 1, 64);
            villageSubwayTracks = builder.comment("How many parallel tracks the bed carries. 0 takes as many as the width allows [Default=0]").defineInRange("villageSubwayTracks", 0, 0, 16);
            villageSubwayTrackGap = builder.comment("How far apart parallel tracks sit [Default=2]").defineInRange("villageSubwayTrackGap", 2, 2, 16);
            villageSubwayShoulderBlock = builder.comment("The block either side of the bed. Empty leaves no shoulder [Default=empty]").define("villageSubwayShoulderBlock", "");
            villageSubwayShoulderWidth = builder.comment("How wide that shoulder is [Default=1]").defineInRange("villageSubwayShoulderWidth", 1, 0, 16);
            villageSubwayPowerBlock = builder.comment("The powered track block. Empty uses vanilla powered rail [Default=empty]").define("villageSubwayPowerBlock", "");
            villageSubwayPowerBase = builder.comment("The block set under a powered track to drive it. Empty uses a redstone block [Default=empty]").define("villageSubwayPowerBase", "");
            villageSubwayPowerRun = builder.comment("How many blocks apart the powered tracks sit. 0 lays none [Default=0]").defineInRange("villageSubwayPowerRun", 0, 0, 256);
            villageSubwayTunnelBlock = builder.comment("The block the bore is lined with: the walls either side and the roof over it. Empty digs no subway at all, since a subway is a bore [Default=empty]").define("villageSubwayTunnelBlock", "");
            villageSubwayTunnelLightBlock = builder.comment("The block set into the tunnel roof as a light. Empty lights none [Default=empty]").define("villageSubwayTunnelLightBlock", "");
            villageSubwayTunnelLightRun = builder.comment("How many blocks apart those lights sit, anchored to world coordinates so pieces agree [Default=8]").defineInRange("villageSubwayTunnelLightRun", 8, 1, 128);
            villageSubwayClimb = builder.comment("How many blocks a line runs before it may step one block up or down [Default=8]").defineInRange("villageSubwayClimb", 8, 1, 128);
            villageSubwayTail = builder.comment("How far past the city's own pieces a line runs before it stops [Default=48]").defineInRange("villageSubwayTail", 48, 0, 256);
            villageSubwayStationLength = builder.comment("How many blocks long a subway station chamber is. 0 builds no stations at all [Default=0]").defineInRange("villageSubwayStationLength", 0, 0, 128);
            villageSubwayStationRun = builder.comment("How many blocks apart further stations sit along a line, past the one nearest the plaza. 0 builds only the one at the plaza [Default=0]").defineInRange("villageSubwayStationRun", 0, 0, 1024);
            villageSubwayPlatformWidth = builder.comment("How far the chamber is opened out either side of the bed to make a platform [Default=3]").defineInRange("villageSubwayPlatformWidth", 3, 0, 16);
            villageSubwayPlatformBlock = builder.comment("The block the platform is floored with. Empty floors it with the tunnel lining [Default=empty]").define("villageSubwayPlatformBlock", "");
            villageSubwayStairBlock = builder.comment("The block the steps up to the road side are made of. Empty uses the tunnel lining [Default=empty]").define("villageSubwayStairBlock", "");
            villageSubwayRailingBlock = builder.comment("The block railed around the head of a station's stairs where they open on the street, so nobody walks into the well. Empty leaves the head unrailed [Default=minecraft:iron_bars]").define("villageSubwayRailingBlock", "minecraft:iron_bars");
            villageSubwayBenchBlock = builder.comment("The seat of the benches set on a station's platform and beside its stair head. A stairs block reads as a bench; any block works. Empty leaves the benches out [Default=minecraft:oak_stairs]").define("villageSubwayBenchBlock", "minecraft:oak_stairs");
            villageSubwayBenchEndBlock = builder.comment("The arms at each end of a station bench. Empty leaves the seat bare at both ends [Default=minecraft:oak_log]").define("villageSubwayBenchEndBlock", "minecraft:oak_log");
            villageSubwayBenchLength = builder.comment("How long a station bench is, arms included. 0 leaves the benches out [Default=5]").defineInRange("villageSubwayBenchLength", 5, 0, 32);
            villageSubwaySurfaces = builder.comment("The chance in a hundred that a subway line climbs to the surface at one end and carries on from there as an ordinary railway, tunnel behind it and open track ahead. The climb takes villageSubwayClimb rows per block, so a deep line spends a long run coming up. 0 keeps every subway buried for its whole length [Default=25]").defineInRange("villageSubwaySurfaces", 25, 0, 100);
            villageSubwayStation = builder.comment("A structure from a pack's structures folder used as the station itself, in place of the carved stairwell. Lift one out of a world built by hand with #scripts/rdpl-grab-template.py: its solid cells are laid and its air cells are carved, so the shape is the build and not a description of it. Empty carves the stairwell instead [Default=empty]").define("villageSubwayStation", "");
            villageSubwayEntrance = builder.comment("A structure from a pack's structures folder set at the head of a station's stairs, so the way in is marked on the street. Empty leaves the stairs coming up bare [Default=empty]").define("villageSubwayEntrance", "");
            villageSubwayStationFoot = builder.comment("How many layers at the foot of a station build are laid once, before the part that repeats. The floor and the doorway out to the platform live here [Default=4]").defineInRange("villageSubwayStationFoot", 4, 0, 64);
            villageSubwayStationRepeat = builder.comment("How many layers of a station build repeat, so one build serves any depth: the shaft grows by whole copies of this band and the corridor absorbs what is left over. It must be a whole turn of the stairs or the flights will not join. 0 never grows the build [Default=12]").defineInRange("villageSubwayStationRepeat", 12, 0, 64);
            villageSewerBlock = builder.comment("The block a sewer is lined with under a city's streets and alleys: its floor, walls and roof. Empty digs no sewers [Default=empty]").define("villageSewerBlock", "");
            villageSewerDepth = builder.comment("How far under a street's own surface the sewer floor sits. The sewer follows the street, so a climbing street carries a climbing sewer. Needs villageSewerBlock [Default=8]").defineInRange("villageSewerDepth", 8, 4, 128);
            villageSewerHeight = builder.comment("How many blocks of headroom stand over the sewer walkway [Default=3]").defineInRange("villageSewerHeight", 3, 2, 32);
            villageSewerWidth = builder.comment("How wide a sewer runs, counted across including its two walls. Even numbers are rounded up so the channel keeps the middle [Default=5]").defineInRange("villageSewerWidth", 5, 3, 33);
            villageSewerWaterBlock = builder.comment("The block filling the channel down the middle of a sewer. Empty leaves the channel dry [Default=minecraft:water]").define("villageSewerWaterBlock", "minecraft:water");
            villageSewerWalkBlock = builder.comment("The block the walkways either side of the channel are surfaced with. Empty walks on the lining block [Default=empty]").define("villageSewerWalkBlock", "");
            villageSewerLightBlock = builder.comment("The block set into a sewer roof over the channel as a light. Empty lights none [Default=empty]").define("villageSewerLightBlock", "");
            villageSewerLightRun = builder.comment("How many blocks apart the sewer lights sit, anchored to world coordinates so pieces agree [Default=8]").defineInRange("villageSewerLightRun", 8, 1, 128);
            villageSewerLadderBlock = builder.comment("The block a manhole shaft is climbed by, set down the shaft from the street to the sewer roof. Empty leaves the shaft open [Default=empty]").define("villageSewerLadderBlock", "");
            villageSewerCoverBlock = builder.comment("The block covering a manhole, set flush in an east-west street wherever a street or alley meets it, and on the plaza where that street crosses the sewer loop. Empty leaves the shaft mouth open [Default=empty]").define("villageSewerCoverBlock", "");
            villageSewerMossBlock = builder.comment("A second block mixed into the sewer lining here and there, mossy stone among plain for instance. Empty lines the sewer with one block throughout [Default=empty]").define("villageSewerMossBlock", "");
            villageSewerMossChance = builder.comment("The percentage of lining blocks that come out as villageSewerMossBlock. Rolled per block position from the world seed, so a repave lays the same pattern [Default=25]").defineInRange("villageSewerMossChance", 25, 0, 100);
            villageSewerVineBlock = builder.comment("A block hung on the inside of the sewer walls here and there, vines for instance. Empty hangs nothing [Default=empty]").define("villageSewerVineBlock", "");
            villageSewerVineChance = builder.comment("The percentage of wall-side cells that carry villageSewerVineBlock. Rolled per block position from the world seed, so a repave hangs the same pattern [Default=20]").defineInRange("villageSewerVineChance", 20, 0, 100);
            villageSewerWellEntrance = builder.comment("On, a city with sewers gets a loop of sewer under the plaza around the well, the sewers of the streets meeting there running through it, and a manhole on the plaza down onto the loop on each side where an east-west street crosses it, so the sewers are one connected system with an entrance at the town center. Off, the street sewers cross under the well and the plaza has no way down of its own [Default=true]").define("villageSewerWellEntrance", true);
            worldGravity = builder.comment("Scale gravity, as a multiplier of vanilla where 1.0 is unchanged and 0.17 is moon-like, for players and mobs. A bare value covers every dimension, and an entry written as dimension=value covers that dimension alone and wins over the bare one. Empty leaves gravity alone [Default=[]]").defineListAllowEmpty("worldGravity", List.of(), each -> each instanceof String);
            worldFallDamage = builder.comment("Scale fall damage the same way, 0.5 halving it and 2.0 doubling it [Default=[]]").defineListAllowEmpty("worldFallDamage", List.of(), each -> each instanceof String);
            worldJumpStrength = builder.comment("Scale jump strength the same way, 1.5 jumping half again as high [Default=[]]").defineListAllowEmpty("worldJumpStrength", List.of(), each -> each instanceof String);
            worldTerminalVelocity = builder.comment("Scale the fastest a mob or player falls the same way, 0.5 falling at half vanilla's top speed [Default=[]]").defineListAllowEmpty("worldTerminalVelocity", List.of(), each -> each instanceof String);
            cloudHeight = builder.comment("The y clouds are drawn at, as dimension=y entries. A bare number covers every dimension. Empty keeps the game's own cloud height, 192 in the overworld [Default=[]]").defineListAllowEmpty("cloudHeight", List.of(), each -> each instanceof String);
            worldBelow = builder.comment("Stack another dimension under this one: falling out of the bottom of the world carries you into the named dimension, arriving under its ceiling at the same x and z, still falling. Entries are written as dimension=target, such as minecraft:overworld=minecraft:the_nether to hang the nether under the overworld; a bare id covers every dimension. Digging through needs the floor's bedrock left out, which worldSeamBedrock decides. Empty means the floor stays the floor [Default=[]]").defineListAllowEmpty("worldBelow", List.of(), each -> each instanceof String);
            worldAbove = builder.comment("The same for the ceiling: rising past the top of the world carries you into the named dimension, arriving above its floor. Written the same way as worldBelow [Default=[]]").defineListAllowEmpty("worldAbove", List.of(), each -> each instanceof String);
            worldSeamEntities = builder.comment("Whether dropped items, mobs and other entities ride the world seams too, or only players. Riders and mounts cross one at a time [Default=true]").define("worldSeamEntities", true);
            worldSeamBedrock = builder.comment("Keep the bedrock at a seam boundary anyway. Off, a dimension whose floor or ceiling carries a worldBelow or worldAbove seam generates no bedrock there, so the way through can be dug. Already generated chunks keep whatever they have [Default=false]").define("worldSeamBedrock", false);
            threatItems = builder.comment("Items that raise a player's threat level, as item=level,count entries with an optional ,each or ,batch at the end, e.g. minecraft:diamond_sword=5,1 or minecraft:diamond=1,16,batch. Each, the default, adds the level for every one held, counting no more than count of them; batch adds the level once for every count held. A count above the item's stack size is cut to the stack size. Every loaded entity holding items is a carrier: a player's main inventory, armor and off hand, a dropped stack, anything with an item inventory such as a chest mule or a chest minecart, and the held items and armor of other mobs. Empty turns the threat level off [Default=[]]").defineListAllowEmpty("threatItems", List.of(), each -> each instanceof String);
            threatLevels = builder.comment("Rising scores that open each threat band, e.g. 5, 15, 40 for three bands. A player below the first is in band 0. Empty turns the threat level off [Default=[]]").defineListAllowEmpty("threatLevels", List.of(), each -> each instanceof String);
            threatMost = builder.comment("The highest score a carrier can reach, -1 for no cap [Default=-1]").defineInRange("threatMost", -1, -1, Integer.MAX_VALUE);
            threatSpawnRate = builder.comment("Multiplied into the hostile spawn rate near carriers in the top band, scaled down through the lower bands. 1.0 changes nothing, 2.0 doubles spawns at the top [Default=1.0]").defineInRange("threatSpawnRate", 1.0D, 0.0D, 100.0D);
            threatNotice = builder.comment("How many blocks farther hostile mobs notice a carrier in the top band, scaled down through the lower bands. 0 changes nothing [Default=0.0]").defineInRange("threatNotice", 0.0D, 0.0D, 256.0D);
            threatSays = builder.comment("Lines said to a player entering a band, as band=message entries [Default=[]]").defineListAllowEmpty("threatSays", List.of(), each -> each instanceof String);
            blockReplacements = builder.comment("Blocks swapped out of chunks as they load, written as block=block with an optional state on either side, such as minecraft:andesite=minecraft:stone or minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Every chunk is done once, new ones included [Default=[]]").defineListAllowEmpty("blockReplacements", List.of(), each -> each instanceof String);
            blockReplacementDimensions = builder.comment("Dimensions block replacement applies to, by id. Empty means every dimension [Default=[]]").defineListAllowEmpty("blockReplacementDimensions", List.of(), each -> each instanceof String);
            blockReplacementDimensionsAreBlacklist = builder.comment("On, block replacement skips these dimensions. Off, it applies only to them [Default=false]").define("blockReplacementDimensionsAreBlacklist", false);
            blockReplacementMinHeight = builder.comment("Lowest y block replacement looks at [Default=-64]").defineInRange("blockReplacementMinHeight", -64, -2032, 2031);
            blockReplacementMaxHeight = builder.comment("Highest y block replacement looks at [Default=319]").defineInRange("blockReplacementMaxHeight", 319, -2032, 2031);
            blockReplacementKey = builder.comment("Change this to make every chunk go through block replacement again [Default=0000]").define("blockReplacementKey", "0000");
            logBlockReplacements = builder.comment("Log the first time each replacement is made, and a total when a world catches up [Default=true]").define("logBlockReplacements", true);
            builder.pop();
        }

        public boolean load() { return loaded() ? load.get() : ConfigCore.flag("worldgen.load", true); }

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
        public boolean logBlockedOres() { return !loaded() || logBlockedOres.get(); }
        public boolean logBlockedBiomes() { return !loaded() || logBlockedBiomes.get(); }

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

        public int caveRegionCells() { return loaded() ? caveRegionCells.get() : 128; }

        public int caveRegionCellsY() { return loaded() ? caveRegionCellsY.get() : 64; }

        public List<String> structureSpacing() { return loaded() ? List.copyOf(structureSpacing.get()) : List.of(); }

        public List<String> structureSeparation() { return loaded() ? List.copyOf(structureSeparation.get()) : List.of(); }
        public List<String> structureMost() { return loaded() ? List.copyOf(structureMost.get()) : List.of(); }
        public List<String> structureSpawners() { return loaded() ? List.copyOf(structureSpawners.get()) : List.of(); }

        public List<String> structureMinDistanceFromSpawn() { return loaded() ? List.copyOf(structureMinDistanceFromSpawn.get()) : List.of(); }

        public List<String> structureBiomes() { return loaded() ? List.copyOf(structureBiomes.get()) : List.of(); }

        public List<String> structureBiomesAreBlacklist() { return loaded() ? List.copyOf(structureBiomesAreBlacklist.get()) : List.of(); }

        public List<String> structureSpawns() { return loaded() ? List.copyOf(structureSpawns.get()) : List.of(); }

        public List<String> structureAt() { return loaded() ? List.copyOf(structureAt.get()) : List.of(); }

        public List<String> structureAdaptation() { return loaded() ? List.copyOf(structureAdaptation.get()) : List.of(); }
        public boolean terrainAdaptation() { return loaded() && terrainAdaptation.get(); }
        public String villagePathBlock() { return loaded() ? villagePathBlock.get() : ""; }
        public int villagePathExtraWidth() { return loaded() ? villagePathExtraWidth.get() : 0; }
        public List<String> villageBlockSizes() { return loaded() ? List.copyOf(villageBlockSizes.get()) : List.of(); }
        public int villageCitySpacing() { return loaded() ? villageCitySpacing.get() : 16; }
        public String villagePathAlleyBlock() { return loaded() ? villagePathAlleyBlock.get() : ""; }
        public int villagePathAlleyChance() { return loaded() ? villagePathAlleyChance.get() : 0; }
        public int villagePathMinimumWidth() { return loaded() ? villagePathMinimumWidth.get() : 0; }
        public int villagePathFlatRun() { return loaded() ? villagePathFlatRun.get() : 6; }
        public List<String> villagePieces() { return loaded() ? List.copyOf(villagePieces.get()) : List.of(); }
        public boolean villagePiecesAreBlacklist() { return !loaded() || villagePiecesAreBlacklist.get(); }
        public List<String> villageBlocks() { return loaded() ? List.copyOf(villageBlocks.get()) : List.of(); }
        public int villagePlotsLeast() { return loaded() ? villagePlotsLeast.get() : 0; }
        public int villagePlotsMost() { return loaded() ? villagePlotsMost.get() : 0; }
        public boolean villagePlotsBackRow() { return !loaded() || villagePlotsBackRow.get(); }
        public String villageLayout() { return loaded() ? villageLayout.get() : ""; }
        public String villagePathCenterBlock() { return loaded() ? villagePathCenterBlock.get() : ""; }
        public int villagePathCenterDash() { return loaded() ? villagePathCenterDash.get() : 0; }
        public String villagePathLineBlock() { return loaded() ? villagePathLineBlock.get() : ""; }
        public String villagePathSidewalkBlock() { return loaded() ? villagePathSidewalkBlock.get() : ""; }
        public int villagePathSidewalkWidth() { return loaded() ? villagePathSidewalkWidth.get() : 2; }
        public String villagePathLampBlock() { return loaded() ? villagePathLampBlock.get() : "minecraft:oak_fence"; }
        public int villagePathLampHeight() { return loaded() ? villagePathLampHeight.get() : 3; }
        public String villagePathLampTopBlock() { return loaded() ? villagePathLampTopBlock.get() : "minecraft:red_wool"; }
        public String villagePathLampSideBlock() { return loaded() ? villagePathLampSideBlock.get() : "minecraft:torch"; }
        public String villagePathLampStructure() { return loaded() ? villagePathLampStructure.get() : ""; }
        public String villagePathSupportBlock() { return loaded() ? villagePathSupportBlock.get() : ""; }
        public String villagePathBridgeBlock() { return loaded() ? villagePathBridgeBlock.get() : ""; }
        public String villagePathBridgeSidewalkBlock() { return loaded() ? villagePathBridgeSidewalkBlock.get() : ""; }
        public String villagePathBridgeBarrierBlock() { return loaded() ? villagePathBridgeBarrierBlock.get() : ""; }
        public int villagePathBridgeBarrierHeight() { return loaded() ? villagePathBridgeBarrierHeight.get() : 1; }
        public int villagePathBridgeDrop() { return loaded() ? villagePathBridgeDrop.get() : 0; }
        public String villagePathBridgeFrameBlock() { return loaded() ? villagePathBridgeFrameBlock.get() : ""; }
        public String villagePathBridgeFrameTopBlock() { return loaded() ? villagePathBridgeFrameTopBlock.get() : ""; }
        public int villagePathBridgeFrameHeight() { return loaded() ? villagePathBridgeFrameHeight.get() : 4; }
        public int villagePathBridgeFrameRun() { return loaded() ? villagePathBridgeFrameRun.get() : 24; }
        public int villagePathBridgeFrameLeast() { return loaded() ? villagePathBridgeFrameLeast.get() : 24; }
        public String villagePathTunnelBlock() { return loaded() ? villagePathTunnelBlock.get() : ""; }
        public int villagePathTunnelDepth() { return loaded() ? villagePathTunnelDepth.get() : 10; }
        public String villagePathTunnelLightBlock() { return loaded() ? villagePathTunnelLightBlock.get() : ""; }
        public int villagePathTunnelLightRun() { return loaded() ? villagePathTunnelLightRun.get() : 8; }
        public int villageRailLines() { return loaded() ? villageRailLines.get() : 0; }
        public int villageRailSpacing() { return loaded() ? villageRailSpacing.get() : 48; }
        public String villageRailDirection() { return loaded() ? villageRailDirection.get() : "any"; }
        public int villageRailWidth() { return loaded() ? villageRailWidth.get() : 3; }
        public String villageRailBlock() { return loaded() ? villageRailBlock.get() : ""; }
        public String villageRailTrackSeat() { return loaded() ? villageRailTrackSeat.get() : "auto"; }
        public String villageRailBedBlock() { return loaded() ? villageRailBedBlock.get() : ""; }
        public String villageRailTieBlock() { return loaded() ? villageRailTieBlock.get() : ""; }
        public int villageRailTieRun() { return loaded() ? villageRailTieRun.get() : 2; }
        public int villageRailTracks() { return loaded() ? villageRailTracks.get() : 0; }
        public int villageRailTrackGap() { return loaded() ? villageRailTrackGap.get() : 2; }
        public String villageRailShoulderBlock() { return loaded() ? villageRailShoulderBlock.get() : ""; }
        public int villageRailShoulderWidth() { return loaded() ? villageRailShoulderWidth.get() : 1; }
        public String villageRailPowerBlock() { return loaded() ? villageRailPowerBlock.get() : ""; }
        public String villageRailPowerBase() { return loaded() ? villageRailPowerBase.get() : ""; }
        public int villageRailPowerRun() { return loaded() ? villageRailPowerRun.get() : 0; }
        public int villageRailClimb() { return loaded() ? villageRailClimb.get() : 8; }
        public int villageRailTail() { return loaded() ? villageRailTail.get() : 48; }
        public String villageRailSupportBlock() { return loaded() ? villageRailSupportBlock.get() : ""; }
        public String villageRailDeckBlock() { return loaded() ? villageRailDeckBlock.get() : ""; }
        public String villageRailBarrierBlock() { return loaded() ? villageRailBarrierBlock.get() : ""; }
        public String villageRailBridgeFrameBlock() { return loaded() ? villageRailBridgeFrameBlock.get() : ""; }
        public String villageRailBridgeFrameTopBlock() { return loaded() ? villageRailBridgeFrameTopBlock.get() : ""; }
        public int villageRailBridgeFrameHeight() { return loaded() ? villageRailBridgeFrameHeight.get() : 4; }
        public int villageRailBridgeFrameRun() { return loaded() ? villageRailBridgeFrameRun.get() : 24; }
        public int villageRailBridgeFrameLeast() { return loaded() ? villageRailBridgeFrameLeast.get() : 24; }
        public String villageRailTunnelBlock() { return loaded() ? villageRailTunnelBlock.get() : ""; }
        public int villageRailTunnelDepth() { return loaded() ? villageRailTunnelDepth.get() : 6; }
        public String villageRailTunnelLightBlock() { return loaded() ? villageRailTunnelLightBlock.get() : ""; }
        public int villageRailTunnelLightRun() { return loaded() ? villageRailTunnelLightRun.get() : 8; }
        public List<String> villageWellStructure() { return loaded() ? List.copyOf(villageWellStructure.get()) : List.of(); }
        public List<String> villagePathDeadEnds() { return loaded() ? List.copyOf(villagePathDeadEnds.get()) : List.of(); }
        public List<String> villagePathPiers() { return loaded() ? List.copyOf(villagePathPiers.get()) : List.of(); }
        public List<String> villagePathPierCargo() { return loaded() ? List.copyOf(villagePathPierCargo.get()) : List.of(); }
        public String villagePathPierLoot() { return loaded() ? villagePathPierLoot.get() : "resourcedatapackloader:chests/pier_cargo"; }
        public List<String> villagePathIntersects() { return loaded() ? List.copyOf(villagePathIntersects.get()) : List.of(); }
        public List<String> villageDecor() { return loaded() ? List.copyOf(villageDecor.get()) : List.of(); }

        public String villageSewerBlock() { return loaded() ? villageSewerBlock.get() : ""; }

        public int villageSubwayLines() { return loaded() ? villageSubwayLines.get() : 0; }

        public int villageSubwayDepth() { return loaded() ? villageSubwayDepth.get() : 24; }

        public int villageSubwaySpacing() { return loaded() ? villageSubwaySpacing.get() : 64; }

        public String villageSubwayDirection() { return loaded() ? villageSubwayDirection.get() : "any"; }

        public int villageSubwayWidth() { return loaded() ? villageSubwayWidth.get() : 3; }

        public String villageSubwayBlock() { return loaded() ? villageSubwayBlock.get() : ""; }

        public String villageSubwayTrackSeat() { return loaded() ? villageSubwayTrackSeat.get() : "auto"; }

        public String villageSubwayBedBlock() { return loaded() ? villageSubwayBedBlock.get() : ""; }

        public String villageSubwayTieBlock() { return loaded() ? villageSubwayTieBlock.get() : ""; }

        public int villageSubwayTieRun() { return loaded() ? villageSubwayTieRun.get() : 2; }

        public int villageSubwayTracks() { return loaded() ? villageSubwayTracks.get() : 0; }

        public int villageSubwayTrackGap() { return loaded() ? villageSubwayTrackGap.get() : 2; }

        public String villageSubwayShoulderBlock() { return loaded() ? villageSubwayShoulderBlock.get() : ""; }

        public int villageSubwayShoulderWidth() { return loaded() ? villageSubwayShoulderWidth.get() : 1; }

        public String villageSubwayPowerBlock() { return loaded() ? villageSubwayPowerBlock.get() : ""; }

        public String villageSubwayPowerBase() { return loaded() ? villageSubwayPowerBase.get() : ""; }

        public int villageSubwayPowerRun() { return loaded() ? villageSubwayPowerRun.get() : 0; }

        public String villageSubwayTunnelBlock() { return loaded() ? villageSubwayTunnelBlock.get() : ""; }

        public String villageSubwayTunnelLightBlock() { return loaded() ? villageSubwayTunnelLightBlock.get() : ""; }

        public int villageSubwayTunnelLightRun() { return loaded() ? villageSubwayTunnelLightRun.get() : 8; }

        public int villageSubwayClimb() { return loaded() ? villageSubwayClimb.get() : 8; }

        public int villageSubwayTail() { return loaded() ? villageSubwayTail.get() : 48; }

        public int villageSubwayStationLength() { return loaded() ? villageSubwayStationLength.get() : 0; }

        public int villageSubwayStationRun() { return loaded() ? villageSubwayStationRun.get() : 0; }

        public int villageSubwayPlatformWidth() { return loaded() ? villageSubwayPlatformWidth.get() : 3; }

        public String villageSubwayPlatformBlock() { return loaded() ? villageSubwayPlatformBlock.get() : ""; }

        public String villageSubwayStairBlock() { return loaded() ? villageSubwayStairBlock.get() : ""; }

        public String villageSubwayRailingBlock() { return loaded() ? villageSubwayRailingBlock.get() : "minecraft:iron_bars"; }

        public String villageSubwayBenchBlock() { return loaded() ? villageSubwayBenchBlock.get() : "minecraft:oak_stairs"; }

        public String villageSubwayBenchEndBlock() { return loaded() ? villageSubwayBenchEndBlock.get() : "minecraft:oak_log"; }

        public int villageSubwayBenchLength() { return loaded() ? villageSubwayBenchLength.get() : 5; }

        public int villageSubwaySurfaces() { return loaded() ? villageSubwaySurfaces.get() : 25; }

        public String villageSubwayStation() { return loaded() ? villageSubwayStation.get() : ""; }

        public String villageSubwayEntrance() { return loaded() ? villageSubwayEntrance.get() : ""; }

        public int villageSubwayStationFoot() { return loaded() ? villageSubwayStationFoot.get() : 4; }

        public int villageSubwayStationRepeat() { return loaded() ? villageSubwayStationRepeat.get() : 12; }

        public int villageSewerDepth() { return loaded() ? villageSewerDepth.get() : 8; }

        public int villageSewerHeight() { return loaded() ? villageSewerHeight.get() : 3; }

        public int villageSewerWidth() { return loaded() ? villageSewerWidth.get() : 5; }

        public String villageSewerWaterBlock() { return loaded() ? villageSewerWaterBlock.get() : "minecraft:water"; }

        public String villageSewerWalkBlock() { return loaded() ? villageSewerWalkBlock.get() : ""; }

        public String villageSewerLightBlock() { return loaded() ? villageSewerLightBlock.get() : ""; }

        public int villageSewerLightRun() { return loaded() ? villageSewerLightRun.get() : 8; }

        public String villageSewerLadderBlock() { return loaded() ? villageSewerLadderBlock.get() : ""; }

        public String villageSewerCoverBlock() { return loaded() ? villageSewerCoverBlock.get() : ""; }

        public String villageSewerMossBlock() { return loaded() ? villageSewerMossBlock.get() : ""; }

        public int villageSewerMossChance() { return loaded() ? villageSewerMossChance.get() : 25; }

        public String villageSewerVineBlock() { return loaded() ? villageSewerVineBlock.get() : ""; }

        public int villageSewerVineChance() { return loaded() ? villageSewerVineChance.get() : 20; }
        public boolean villageSewerWellEntrance() { return !loaded() || villageSewerWellEntrance.get(); }
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
        private final ForgeConfigSpec.BooleanValue pregenBackup;
        private final ForgeConfigSpec.ConfigValue<String> pregenBackupSays;
        private final ForgeConfigSpec.ConfigValue<String> resetSays;
        private final ForgeConfigSpec.ConfigValue<String> resetSendsTo;
        private final ForgeConfigSpec.ConfigValue<String> resetRuns;
        private final ForgeConfigSpec.BooleanValue resetClearsEntities;
        private final ForgeConfigSpec.BooleanValue resetClearsScores;
        private final ForgeConfigSpec.IntValue spawnChunkRadius;

        private Chunks(ForgeConfigSpec.Builder builder) {
            builder.comment("What this mod says to players and how it shows it, and the land made before anybody plays").push("chunks");
            saysCard = builder.comment("Show the lines this mod says, the welcome and later the land-making progress and the threat lines, as a card in the lower right corner instead of in chat. The card slides in, stays eight seconds and fades, and shows over an open screen too [Default=false]").define("saysCard", false);
            saysIcon = builder.comment("An item drawn on the card, e.g. minecraft:compass. Empty draws none [Default=]").define("saysIcon", "");
            saysColor = builder.comment("The card's background color as hex, e.g. 1E2630. Empty uses a dark slate [Default=]").define("saysColor", "");
            saysImage = builder.comment("A PNG from the pack's client assets stretched over the card as its background, e.g. rubyworld:textures/gui/card.png, drawn over the color. Empty draws none [Default=]").define("saysImage", "");
            welcomeSays = builder.comment("Welcome lines, shown in green on every login. A bare entry is the line for everywhere; a dimension=message entry overrides it for that dimension and also greets every arrival there, e.g. minecraft:the_nether=Welcome to the Nether!. An empty message after the = mutes that dimension; an empty list shows nothing. Left at this default it speaks each player's language [Default=[Welcome to your World!]]").defineListAllowEmpty("welcomeSays", List.of(WELCOME), each -> each instanceof String);
            pregenOnNewWorld = builder.comment("How far around the spawn, in chunks, a world has its land made before anybody plays it. The game makes 12 chunks around the spawn on its own, so 12 is the floor and 0 means nothing beyond that. Raise it to reach further than the game does [Default=0]").defineInRange("pregenOnNewWorld", 0, 0, 8192);
            pregenToBorder = builder.comment("Whether a new world has its land made out to its world border instead of a set number of chunks, centered on the border rather than the spawn. A world whose border was never moved in has no border to reach and is passed over [Default=false]").define("pregenToBorder", false);
            pregenBorderLimit = builder.comment("The furthest a border may reach, in chunks either way, before making land out to it is refused. This is here to stop a mistake running for weeks, not to be turned up, and a pack cannot set it. A square of 8192 holds 268 million chunks [Default=8192]").defineInRange("pregenBorderLimit", 8192, 1, 1875000);
            pregenDimensions = builder.comment("Which dimensions a new world has its land made in, by id, in the order given, one after another [Default=[minecraft:overworld]]").defineListAllowEmpty("pregenDimensions", List.of("minecraft:overworld"), each -> each instanceof String);
            pregenAllDimensions = builder.comment("Make the land of every dimension the server holds, modded ones included, the overworld first and the rest in id order, instead of only those in pregenDimensions. Ones named in pregenDimensionsWhenEntered are still left for their first visitor [Default=false]").define("pregenAllDimensions", false);
            pregenDimensionsWhenEntered = builder.comment("Dimensions whose land is made not up front but the first time anybody sets foot in them, to the same reach, holding everybody the same way until it is done. One named here and in pregenDimensions is simply made up front [Default=[]]").defineListAllowEmpty("pregenDimensionsWhenEntered", List.of(), each -> each instanceof String);
            pregenResume = builder.comment("Whether a run that was stopped or cut short picks up where it left off next time the world is loaded, rather than starting again [Default=false]").define("pregenResume", false);
            pregenChunksInFlight = builder.comment("How many chunks a land-making run asks the game for at once. More keeps the generation threads busier and the server less responsive to whoever is held watching [Default=32]").defineInRange("pregenChunksInFlight", 32, 1, 512);
            pregenRunningSays = builder.comment("The progress message players see while the world generates, where %d is the percentage and a second %s the dimension. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + PREGEN_RUNNING + "]").define("pregenRunningSays", PREGEN_RUNNING);
            pregenFinishedSays = builder.comment("The message players see when generation finishes. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + PREGEN_FINISHED + "]").define("pregenFinishedSays", PREGEN_FINISHED);
            pregenStoppedSays = builder.comment("The message players see when generation is stopped early. Empty tells them nothing. Left at this default it speaks each player's language [Default=" + PREGEN_STOPPED + "]").define("pregenStoppedSays", PREGEN_STOPPED);
            pregenSpectatingSays = builder.comment("The mid-screen message players see while held in spectator during world generation. Empty shows nothing. Left at this default it speaks each player's language [Default=" + PREGEN_SPECTATING + "]").define("pregenSpectatingSays", PREGEN_SPECTATING);
            pregenLogo = builder.comment("Where the logo stands when pregeneration finishes: left, center or right, above the mid-screen text. It is always shown; an unknown word is read as center [Default=center]").define("pregenLogo", "center");
            pregenBackup = builder.comment("Copy the world to a pristine backup once pregeneration finishes, while the players are still held. The copy is what a reset would restore [Default=false]").define("pregenBackup", false);
            pregenBackupSays = builder.comment("The mid-screen message players see while that backup is copied. Empty shows nothing [Default=Pack requested world backup]").define("pregenBackupSays", "Pack requested world backup");
            resetSays = builder.comment("The mid-screen line players are shown while /rdpl reset puts the map back. Empty resets quietly [Default=Pack requested map reset]").define("resetSays", "Pack requested map reset");
            resetSendsTo = builder.comment("Where players are put by a reset: spawn, a position as x,y,z, or dimension:x,y,z to send them into another world [Default=spawn]").define("resetSendsTo", "spawn");
            resetRuns = builder.comment("A function run after a reset has cleared the map, named namespace:path. Empty runs nothing [Default=empty]").define("resetRuns", "");
            resetClearsEntities = builder.comment("Remove every entity that is not a player when the map resets [Default=true]").define("resetClearsEntities", true);
            resetClearsScores = builder.comment("Set every objective the pack keeps back to nothing when the map resets, so a new match starts from zero. Teams themselves are kept [Default=true]").define("resetClearsScores", true);
            spawnChunkRadius = builder.comment("How far from the spawn point, in chunks, chunks are held loaded whether or not a player is there. On 1.20.1 it sets the spawn ticket the server holds from the moment a world starts, in place of the game's own 11; on 1.21.1 it sets the spawnChunkRadius game rule when a world starts. Either way the same number of chunks is held on both lines. The default 2 is what 1.21.1 itself uses, 25 chunks, and makes a new world here ready about five times sooner than the game's 11 does; -1 leaves each game its own, which is 441 chunks on 1.20.1 against 25 on 1.21.1 [Default=2]").defineInRange("spawnChunkRadius", 2, -1, 32);
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

        public boolean pregenBackup() { return loaded() && pregenBackup.get(); }

        public String pregenBackupSays() { return loaded() ? pregenBackupSays.get() : "Pack requested world backup"; }

        public String resetSays() { return loaded() ? resetSays.get() : "Pack requested map reset"; }

        public String resetSendsTo() { return loaded() ? resetSendsTo.get() : "spawn"; }

        public String resetRuns() { return loaded() ? resetRuns.get() : ""; }

        public boolean resetClearsEntities() { return !loaded() || resetClearsEntities.get(); }

        public boolean resetClearsScores() { return !loaded() || resetClearsScores.get(); }

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
            slowedKinds = builder.comment("Which kinds are given fewer ticks: items, experience, projectiles. Anything that thinks for itself is always given a slower pace instead, without being named here, and machines are never slowed [Default=[items, experience]]").defineListAllowEmpty("slowedKinds", List.of("items", "experience"), each -> each instanceof String);
            slowDistance = builder.comment("How far from the nearest player, in blocks, before a chunk is slowed. The game stops telling a player about most entities beyond 64, so nothing below that [Default=192]").defineInRange("slowDistance", 192, 64, 4096);
            slowRate = builder.comment("One tick in this many is given to a slowed chunk. 1 is no slowing at all, 20 is once a second [Default=4]").defineInRange("slowRate", 4, 1, 20);
            neverSlowed = builder.comment("Entities left alone however far away they are, as namespace:name [Default=[]]").defineListAllowEmpty("neverSlowed", List.of(), each -> each instanceof String);
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

    public static final class Commands {
        private final ForgeConfigSpec.IntValue gotoLevel;
        private final ForgeConfigSpec.IntValue gotoNextLevel;
        private final ForgeConfigSpec.IntValue gotoBackLevel;
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> gotoPlaceLevels;

        private Commands(ForgeConfigSpec.Builder builder) {
            builder.comment("Who may run which parts of the mod's own commands").push("commands");
            gotoLevel = builder.comment("The permission level needed for /rdplserver goto <name>, which carries the sender to the nearest one. 3 is an operator, the level every other part of the command sits at. 2 also lets a command block run it, so a pack can put the jump on a button or a pressure plate without handing anybody the rest of the command. 0 lets any player type it. The other parts of /rdplserver stay at 3 whatever this says [Default=3]").defineInRange("gotoLevel", 3, 0, 4);
            gotoNextLevel = builder.comment("The permission level for /rdplserver goto <name> next, which passes over the one it last carried the sender to and finds another. Same scale as gotoLevel [Default=3]").defineInRange("gotoNextLevel", 3, 0, 4);
            gotoBackLevel = builder.comment("The permission level for /rdplserver goto <name> back, which returns the sender to the one before. Same scale as gotoLevel [Default=3]").defineInRange("gotoBackLevel", 3, 0, 4);
            gotoPlaceLevels = builder.comment("Permission levels for single places, as name=level entries, one per line, overriding the three settings above for that place alone and in all three of its forms. The name is what you would type after goto, so a vanilla one such as Village or Mansion, or a name a pack registered for its own structures with locateAs. Same scale: 3 an operator, 2 also a command block, 0 anybody. A pack can then open the way to its own ruins while every vanilla structure stays shut, or the other way about. A name nothing has registered is ignored with a note in the log [Default=[]]").defineListAllowEmpty("gotoPlaceLevels", List.of(), each -> each instanceof String);
            builder.pop();
        }

        public int gotoLevel() { return loaded() ? gotoLevel.get() : 3; }

        public int gotoNextLevel() { return loaded() ? gotoNextLevel.get() : 3; }

        public int gotoBackLevel() { return loaded() ? gotoBackLevel.get() : 3; }

        public List<String> gotoPlaceLevels() { return loaded() ? List.copyOf(gotoPlaceLevels.get()) : List.of(); }
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
        private final ForgeConfigSpec.ConfigValue<String> villages;
        private final ForgeConfigSpec.ConfigValue<String> commands;
        private final ForgeConfigSpec.ConfigValue<String> recipes;
        private final ForgeConfigSpec.ConfigValue<String> blastPlaster;

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
            villages = builder.comment("The city and village streets a pack lays: their shape, dress, bridges, tunnels, rails, plots and plaza [default|global|off]").define("villages", "default");
            commands = builder.comment("Who may run the mod's own commands: the goto permission levels [default|global|off]").define("commands", "default");
            recipes = builder.comment("Recipe and furnace blocking and their whitelists [default|global|off]").define("recipes", "default");
            blastPlaster = builder.comment("Blast Plaster explosion handling driven from packs, with per dimension settings [default|global|off]").define("blastPlaster", "default");
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

        public String villages() { return loaded() ? villages.get() : ConfigCore.text("control.villages", "default"); }

        public String commands() { return loaded() ? commands.get() : ConfigCore.text("control.commands", "default"); }

        public String recipes() { return loaded() ? recipes.get() : ConfigCore.text("control.recipes", "default"); }

        public String blastPlaster() { return loaded() ? blastPlaster.get() : ConfigCore.text("control.blastPlaster", "default"); }

        public String replacements() { return loaded() ? replacements.get() : ConfigCore.text("control.replacements", "default"); }

        public String entities() { return loaded() ? entities.get() : ConfigCore.text("control.entities", "default"); }
    }
}
