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
    public static Entities entities = new Entities();
    public static Worldgen worldgen = new Worldgen();
    @net.minecraftforge.common.config.Config.Comment("Working around other mods")
    public static Compat compat = new Compat();
    @net.minecraftforge.common.config.Config.Comment("What the game shows while a world loads")
    public static Client client = new Client();
    @net.minecraftforge.common.config.Config.Comment("Small changes to how vanilla behaves")
    public static Tweaks tweaks = new Tweaks();

    @net.minecraftforge.common.config.Config.Comment("Chunks held loaded around a world's spawn point")
    public static Chunks chunks = new Chunks();

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
        @net.minecraftforge.common.config.Config.Comment("Ticking entities less often far from every player [default|global|off]")
        public String entities = "default";
        @net.minecraftforge.common.config.Config.Comment("How many chunks are held loaded around a world's spawn point [default|global|off]")
        public String chunks = "default";
        @net.minecraftforge.common.config.Config.Comment("Blast Plaster explosion handling driven from packs, with per dimension settings [default|global|off]")
        public String blastPlaster = "default";
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
        @net.minecraftforge.common.config.Config.Comment("Serve plain vanilla clients: nothing from any pack is registered — no blocks, items, fluids, materials, sounds, potions, villagers or their trades, biomes or dimensions — so a client without the mod can join. Everything that lives on the server alone still applies. See Server-side packs in HOWTO.md. Requires a restart [Default=false]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean vanillaClients = false;
        @net.minecraftforge.common.config.Config.Comment("Register the blocks and items described by blocks/*.json and items/*.json in packs. Turning this off leaves worlds containing them with missing blocks. Requires a restart [Default=true]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean load = true;
        @net.minecraftforge.common.config.Config.Comment("Apply hardness/*.json files, which give a group of blocks a mining time and blast resistance multiplier, rolled per block position [Default=true]")
        public boolean hardness = true;
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

    public static class Entities {
        @net.minecraftforge.common.config.Config.Comment("Tick entities far from every player less often. Nothing is ever left unticked, only ticked at a slower pace [Default=true]")
        public boolean slowDistantEntities = true;
        @net.minecraftforge.common.config.Config.Comment("Which kinds are given fewer ticks: items, experience, projectiles. Anything that thinks for itself is always given a slower pace instead, without being named here, and machines are never slowed [Default={items, experience}]")
        public String[] slowedKinds = { "items", "experience" };
        @net.minecraftforge.common.config.Config.Comment("How far from the nearest player, in blocks, before a chunk is slowed. The game stops telling a player about most entities beyond 64, so nothing below that [Default=192]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 64, max = 4096)
        public int slowDistance = 192;
        @net.minecraftforge.common.config.Config.Comment("One tick in this many is given to a slowed chunk. 1 is no slowing at all, 20 is once a second [Default=4]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 20)
        public int slowRate = 4;
        @net.minecraftforge.common.config.Config.Comment("Entities left alone however far away they are, as namespace:name [Default={}]")
        public String[] neverSlowed = {};
        @net.minecraftforge.common.config.Config.Comment("How often, in ticks, the distance to the nearest player is worked out again. Every player counts for themselves, so someone alone far away still has their own quiet space around them [Default=20]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 100)
        public int slowRecheck = 20;
    }

    public static class Chunks {
        @net.minecraftforge.common.config.Config.Comment("How far from the spawn point, in blocks, chunks are held loaded whether or not a player is there. 128 is what the game does. 0 holds none, so the spawn area unloads like anywhere else. A dimension only ever holds spawn chunks if it was registered to, so this changes nothing for one that was not [Default=128]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 1024)
        public int spawnChunkRadius = 128;
        @net.minecraftforge.common.config.Config.Comment("A radius per dimension. Write dimension=blocks, one per line, as in 7=0. Overrides spawnChunkRadius for the dimensions listed [Default={}]")
        public String[] spawnChunkRadii = {};
        @net.minecraftforge.common.config.Config.Comment("How many chunks may be waiting to be written before the game stops resting between writes. It rests a hundredth of a second after each one, which holds it to about a hundred a second however fast the disk is, so anything that makes land faster than that piles up in memory. 0 leaves it resting always, as the game does [Default=100]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 10000)
        public int hurryWritesAbove = 100;
        @net.minecraftforge.common.config.Config.Comment("How many chunks a bulk generation run keeps loaded behind itself. Holding a chunk means its neighbours are there when the game comes to decorate and light them, so the work is done once instead of the chunk being fetched back and written again. Higher holds more memory. Below a whole region of 1024 the seams between regions miss their turn and are left for the relight to dress [Default=2048]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 64, max = 16384)
        public int pregenKeepLoaded = 2048;
        @net.minecraftforge.common.config.Config.Comment("How many chunks may be waiting to be written before a bulk generation run rests until the writing catches up. 0 never rests [Default=2000]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 100000)
        public int pregenPauseAbove = 2000;
        @net.minecraftforge.common.config.Config.Comment("How many milliseconds of each round a bulk generation run may take. Players are held spectating while it runs, so nobody is in the world to feel it overrun; higher generates faster and only the write queue, governed by pregenPauseAbove, pushes back. 50 keeps to a single round for the rare setup where something else must stay responsive [Default=200]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 1000)
        public int pregenMillisPerRound = 200;
        @net.minecraftforge.common.config.Config.Comment("How far around the spawn, in chunks, a brand new world has its land made before anybody plays it. The game makes 12 chunks around the spawn on its own when a world is created, so any figure below that is raised to 12, letting the run adopt and light that ground in one organized pass instead of leaving it to trickle. 0 makes none [Default=0]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 8192)
        public int pregenOnNewWorld = 0;
        @net.minecraftforge.common.config.Config.Comment("Whether a new world has its land made out to its world border instead of a set number of chunks, centred on the border rather than the spawn. A world whose border was never moved in has no border to reach and is passed over [Default=false]")
        public boolean pregenToBorder = false;
        @net.minecraftforge.common.config.Config.Comment({
                "The furthest a border may reach, in chunks either way, before making land out to it is refused.",
                "This is here to stop a mistake running for weeks, not to be turned up. A pack cannot set it.",
                "A square of this reach holds 268 million chunks. At two hundred a second that is a fortnight",
                "of running, and several terabytes on disk. If a border is being refused, it is nearly always",
                "the border that is wrong, not this number. Raise it only having worked out how long the run",
                "will take and where it will be kept [Default=8192]"})
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 1875000)
        public int pregenBorderLimit = 8192;
        @net.minecraftforge.common.config.Config.Comment("Which dimensions a new world has its land made in, in the order given, one after another [Default=0]")
        public int[] pregenDimensions = {0};
        @net.minecraftforge.common.config.Config.Comment("Make the land of every dimension anything registers, modded ones included, the overworld first and the rest in rising order, instead of only those in pregenDimensions. Ones named in pregenDimensionsWhenEntered are still left for their first visitor [Default=false]")
        public boolean pregenAllDimensions = false;
        @net.minecraftforge.common.config.Config.Comment("Dimensions whose land is made not up front but the first time anybody sets foot in them, to the same reach, holding everybody the same way until it is done. One named here and in pregenDimensions is simply made up front [Default=]")
        public int[] pregenDimensionsWhenEntered = {};
        @net.minecraftforge.common.config.Config.Comment("Whether a run that was stopped or cut short picks up where it left off next time the world is loaded, rather than starting again [Default=false]")
        public boolean pregenResume = false;
        @net.minecraftforge.common.config.Config.Comment("The progress message players see while the world generates, where %d is the percentage. Empty tells them nothing. Left at this default it speaks each player's language [Default=World pregeneration running, %d%% done]")
        public String pregenRunningSays = "World pregeneration running, %d%% done";
        @net.minecraftforge.common.config.Config.Comment("The progress message players see during the relight pass, where %d is the percentage. Empty tells them nothing. Left at this default it speaks each player's language [Default=World relighting, %d%% done]")
        public String pregenRelightSays = "World relighting, %d%% done";
        @net.minecraftforge.common.config.Config.Comment("The message players see when generation finishes. Empty tells them nothing. Left at this default it speaks each player's language [Default=World pregeneration finished]")
        public String pregenFinishedSays = "World pregeneration finished";
        @net.minecraftforge.common.config.Config.Comment("The message players see when generation is stopped early. Empty tells them nothing. Left at this default it speaks each player's language [Default=World pregeneration stopped]")
        public String pregenStoppedSays = "World pregeneration stopped";
        @net.minecraftforge.common.config.Config.Comment("The mid-screen message players see while held in spectator during world generation. Empty shows nothing. Left at this default it speaks each player's language [Default=Spectating until the world is ready]")
        public String pregenSpectatingSays = "Spectating until the world is ready";
        @net.minecraftforge.common.config.Config.Comment("Welcome lines, shown in green on every login and after land-making. A bare entry is the line for everywhere; a dimension=message entry overrides it for that dimension and also greets every arrival there, e.g. -1=Welcome to the Nether!. An empty message after the = mutes that dimension; an empty list shows nothing. Left at this default it speaks each player's language [Default=[Welcome to your World!]]")
        public String[] welcomeSays = {"Welcome to your World!"};
    }

    public static class Worldgen {
        @net.minecraftforge.common.config.Config.Comment("Generate the ore veins described by worldgen/*.json in new chunks. Existing chunks are not changed [Default=true]")
        public boolean load = true;
        @net.minecraftforge.common.config.Config.Comment("Disable all pregen/generation optimizations (why would you do this?) [Default=false]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        @SuppressWarnings("unused") public boolean disableOptimizations = false;
        @net.minecraftforge.common.config.Config.Comment("Sink the ground up under village pieces the way modern versions do, so they sit seated in the terrain instead of floating on stilts over every dip. Changes the terrain, so a world made with it on differs from one made without [Default=false]")
        public boolean terrainAdaptation = false;
        @net.minecraftforge.common.config.Config.Comment("Which structures the terrain adapts to and how, as structure=mode entries. Structures are villages, strongholds, mineshafts, monuments and mansions; modes are none, bury, beard_thin, beard_box and encapsulate, the same five modern versions use. Villages are beard_thin unless overridden, everything else is none unless named [Default=[]]")
        public String[] structureAdaptation = {};
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
        @net.minecraftforge.common.config.Config.Comment("The world type every new world is made with, whatever was chosen when it was made, such as default, largebiomes, amplified, customized, biomesop or realistic. Empty leaves the choice alone [Default=empty]")
        public String worldType = "";
        @net.minecraftforge.common.config.Config.Comment("Tell a player in chat, as they join a world a pack chose the type of, that the pack chose it. A pack cannot set this [Default=true]")
        public boolean tellWorldType = true;
        @net.minecraftforge.common.config.Config.Comment("World types a player picks that worldType leaves alone, such as flat or customized. Empty means every choice is replaced [Default={flat, debug_all_block_states}]")
        public String[] worldTypeExceptions = { "flat", "debug_all_block_states" };
        @net.minecraftforge.common.config.Config.Comment("The seed every new world is made with, whatever was typed when it was made, written the same way it would be typed. A number is used as it is, anything else is turned into one the way the game does. Empty leaves the choice alone [Default=empty]")
        public String worldSeed = "";
        @net.minecraftforge.common.config.Config.Comment("Which way every new world is started, one of survival, creative, adventure or spectator. Empty leaves it as whoever made the world chose [Default=]")
        public String worldGameMode = "";
        @net.minecraftforge.common.config.Config.Comment("What a new world is called when the screen for making one opens. Empty leaves it as the game names it [Default=]")
        public String worldName = "";
        @net.minecraftforge.common.config.Config.Comment("Where every new world spawns, written as x,z or x,y,z. Without a y the game's usual ground level for the world type is used. Only applied to a world as it is created, so worlds that already exist are left alone. Empty leaves the choice to the game [Default=empty]")
        public String worldSpawn = "";
        @net.minecraftforge.common.config.Config.Comment("How far across, in blocks, the world border stands in every new world. Only applied to a world as it is created, so worlds that already exist are left alone. 0 leaves the border where the game puts it [Default=0]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 60000000)
        public int worldBorder = 0;
        @net.minecraftforge.common.config.Config.Comment("The widest border a pack is allowed to ask for through worldBorder. A pack asking for more is refused and the border is left where the game puts it. A pack cannot set this [Default=60000000]")
        @net.minecraftforge.common.config.Config.RangeInt(min = 1, max = 60000000)
        public int worldBorderLimit = 60000000;
        @net.minecraftforge.common.config.Config.Comment("Lock the overworld's time of day, in ticks, the same figure /time set takes, so 18000 is midnight. The clock stops and never moves. -1 leaves time running [Default=-1]")
        @net.minecraftforge.common.config.Config.RangeInt(min = -1, max = 23999)
        public int worldTime = -1;
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
        @net.minecraftforge.common.config.Config.Comment("Pin structures to exact places as structure=x,z entries, one per line, e.g. villages=1000,-500. A pinned structure generates only in the chunks named, each at its usual ground rules, and its spacing, separation and flat-ground checks stand aside [Default=[]]")
        public String[] structureAt = {};
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
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The block village roads are paved with when terrainAdaptation lays them. Empty keeps the vanilla biome road block [Default=empty]")
        public String villagePathBlock = "";
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The block placed under the road surface. Empty keeps vanilla gravel [Default=empty]")
        public String villagePathSupportBlock = "";
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The block the road crosses water with. Empty keeps vanilla planks [Default=empty]")
        public String villagePathBridgeBlock = "";
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. Extra blocks of road width on each side beyond the vanilla 3, when terrainAdaptation lays the roads. Widens the road pieces themselves, so houses stand back from wide streets. Only shapes villages seeded after the change; where space is too tight a segment falls back to vanilla width [Default=0]")
        public int villagePathExtraWidth = 0;
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The block of the center line down the middle of village roads, when terrainAdaptation lays them. Empty draws no center line [Default=empty]")
        public String villagePathCenterBlock = "";
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. Dashes the center line: N blocks of line, then one of road, anchored to world coordinates so segments continue each other. 0 keeps the line solid [Default=0]")
        public int villagePathCenterDash = 0;
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The block of the edge lines between road and sidewalk. Empty draws no edge lines [Default=empty]")
        public String villagePathLineBlock = "";
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The block sidewalks are laid with, level with the road, outside the edge lines. Empty lays no sidewalks [Default=empty]")
        public String villagePathSidewalkBlock = "";
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. How many blocks wide each sidewalk is, when villagePathSidewalkBlock is set [Default=2]")
        public int villagePathSidewalkWidth = 2;
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The narrowest road allowed. A segment that cannot fit its full dress falls back to a bare 3-wide alley; below this width it does not happen at all, and the village lays out around it. 0 never refuses [Default=0]")
        public int villagePathMinimumWidth = 0;
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. Path intersect designs to paint at junctions, by registry key from pathintersects/ in a pack. One entry paints every junction the same; several pick per junction, weighted by each design's weight. Empty paints nothing [Default=empty]")
        public String[] villagePathIntersects = {};
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. Roads hold each grade for at least this many blocks before stepping, anchored to world coordinates so segments agree across pieces. 0 lets roads step every block as vanilla slopes do [Default=6]")
        public int villagePathFlatRun = 6;
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The block bridge sidewalks are decked with where a road crosses water. Empty keeps the normal sidewalk block on bridges [Default=empty]")
        public String villagePathBridgeSidewalkBlock = "";
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. The block bridge barriers are built from, stacked along both edges of the deck over water. Empty builds no barriers [Default=empty]")
        public String villagePathBridgeBarrierBlock = "";
        @net.minecraftforge.common.config.Config.Comment("EXPERIMENTAL, a work in progress. Use at your own risk of corrupting villages. How many blocks tall the bridge barriers stand [Default=1]")
        public int villagePathBridgeBarrierHeight = 1;
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
        @net.minecraftforge.common.config.Config.Comment("Dimensions ore blocking applies to. Empty means every dimension [Default=]")
        public int[] blockOreDimensions = {};
        @net.minecraftforge.common.config.Config.Comment("Treat blockOreDimensions as the dimensions to leave alone instead [Default=false]")
        public boolean blockOreDimensionsAreBlacklist = false;
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
        @net.minecraftforge.common.config.Config.Comment("Write per-chunk retrogen lines, cascading worldgen traces, a snapshot of how the server is keeping up every few seconds, and the debug lists other messages refer to. Very verbose, about two lines per chunk loaded [Default=false]")
        public boolean worldgenDebug = false;
    }

    public static class Tweaks {
        @net.minecraftforge.common.config.Config.Comment("Leaves that lose their tree decay within a second instead of waiting on random ticks. Ignored when Universal Tweaks is installed, which does this itself [Default=true]")
        public boolean promptLeafDecay = true;
        @net.minecraftforge.common.config.Config.Comment("Grass paths can be made under a block and stay there when one is placed above. Ignored when Universal Tweaks is installed, which does this itself [Default=true]")
        public boolean lenientPaths = true;
        @net.minecraftforge.common.config.Config.Comment("Mob spawners cannot be mined or blown up. Creative mode still removes them. Requires a restart [Default=false]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public boolean unbreakableSpawners = false;
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
