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

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        packs = new Packs(builder);
        content = new Content(builder);
        recipes = new Recipes(builder);
        data = new Data(builder);
        worldgen = new Worldgen(builder);
        tweaks = new Tweaks(builder);
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
        private final ForgeConfigSpec.BooleanValue overrides;
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
            overrides = builder.comment("Apply overrides/<namespace>/<name>.json files, which change properties of blocks, items and potion types that already exist, vanilla or modded [Default=true]").define("overrides", true);
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

        public boolean overrides() { return loaded() ? overrides.get() : ConfigCore.flag("content.overrides", true); }

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

        private Tweaks(ForgeConfigSpec.Builder builder) {
            builder.comment("Small changes to how vanilla behaves").push("tweaks");
            lenientPaths = builder.comment("Paths and tilled ground can be made under a block and stay there when one is placed above [Default=true]").define("lenientPaths", true);
            builder.pop();
        }

        public boolean lenientPaths() { return loaded() ? lenientPaths.get() : ConfigCore.flag("tweaks.lenientPaths", true); }
    }

    public static final class Worldgen {
        private final ForgeConfigSpec.BooleanValue worldgenDebug;

        private Worldgen(ForgeConfigSpec.Builder builder) {
            builder.comment("What generates in the world, and what is stopped from generating").push("worldgen");
            worldgenDebug = builder.comment("Write the debug lines other messages refer to into logs/rdpl.log, such as which pack served a file and what each command did. Very verbose [Default=false]").define("worldgenDebug", false);
            builder.pop();
        }

        public boolean worldgenDebug() { return loaded() ? worldgenDebug.get() : ConfigCore.flag("worldgen.worldgenDebug", false); }
    }
}
