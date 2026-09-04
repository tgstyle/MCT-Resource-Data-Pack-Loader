package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public final class Config {
    public static final ModConfigSpec SPEC;
    public static final Packs packs;
    public static final Recipes recipes;
    public static final Data data;
    public static final Worldgen worldgen;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        packs = new Packs(builder);
        recipes = new Recipes(builder);
        data = new Data(builder);
        worldgen = new Worldgen(builder);
        SPEC = builder.build();
    }

    private Config() {}

    public static boolean loaded() { return SPEC.isLoaded(); }

    public static final class Packs {
        private final ModConfigSpec.ConfigValue<String> rootDirectory;
        private final ModConfigSpec.BooleanValue overrideResourcePacks;
        private final ModConfigSpec.BooleanValue warnOnCaseMismatch;
        private final ModConfigSpec.BooleanValue logContents;
        private final ModConfigSpec.BooleanValue traceUnresolvedVariables;

        private Packs(ModConfigSpec.Builder builder) {
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

    public static final class Recipes {
        private final ModConfigSpec.BooleanValue furnace;
        private final ModConfigSpec.BooleanValue removals;
        private final ModConfigSpec.BooleanValue skipMissingItems;
        private final ModConfigSpec.BooleanValue blockRecipes;
        private final ModConfigSpec.ConfigValue<List<? extends String>> recipeWhitelist;
        private final ModConfigSpec.ConfigValue<List<? extends String>> blockedRecipeMods;
        private final ModConfigSpec.ConfigValue<String> recipeMatch;
        private final ModConfigSpec.BooleanValue blockFurnaceRecipes;
        private final ModConfigSpec.ConfigValue<List<? extends String>> furnaceWhitelist;
        private final ModConfigSpec.ConfigValue<List<? extends String>> blockedFurnaceMods;
        private final ModConfigSpec.BooleanValue logBlockedRecipes;

        private Recipes(ModConfigSpec.Builder builder) {
            builder.comment("Recipe files, removals and blocking").push("recipes");
            furnace = builder.comment("Apply furnace/*.json files, which add and remove furnace smelting recipes [Default=true]").define("furnace", true);
            removals = builder.comment("Apply recipe_removals/*.json files, which delete recipes by name, namespace or output [Default=true]").define("removals", true);
            skipMissingItems = builder.comment("Skip recipes that use an item which is not registered, instead of letting them fail. The count is logged once [Default=true]").define("skipMissingItems", true);
            blockRecipes = builder.comment("Remove every crafting recipe, keeping only the mods in recipeWhitelist. Include your pack's namespace to keep its own recipes [Default=false]").define("blockRecipes", false);
            recipeWhitelist = builder.comment("Mod ids whose crafting recipes survive while blockRecipes is on. Include your pack's namespace to keep its own recipes").defineList("recipeWhitelist", List.of("minecraft"), () -> "", each -> each instanceof String);
            blockedRecipeMods = builder.comment("Mod ids whose crafting recipes are removed outright, whoever they belong to and whatever the whitelist says").defineList("blockedRecipeMods", List.of(), () -> "", each -> each instanceof String);
            recipeMatch = builder.comment("What the mod id is read from when blocking crafting recipes. 'recipe' uses the recipe's own name, 'output' uses the item it makes, 'both' blocks if either matches and spares if either is whitelisted [Default=recipe]").define("recipeMatch", "recipe");
            blockFurnaceRecipes = builder.comment("Remove every furnace, blast furnace, smoker and campfire recipe, keeping only the mods in furnaceWhitelist. The mod is read from the item produced [Default=false]").define("blockFurnaceRecipes", false);
            furnaceWhitelist = builder.comment("Mod ids whose furnace recipes survive while blockFurnaceRecipes is on. Include your pack's namespace to keep its own recipes").defineList("furnaceWhitelist", List.of("minecraft"), () -> "", each -> each instanceof String);
            blockedFurnaceMods = builder.comment("Mod ids whose furnace recipes are removed outright, whatever the whitelist says").defineList("blockedFurnaceMods", List.of(), () -> "", each -> each instanceof String);
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
        private final ModConfigSpec.BooleanValue lootInjections;
        private final ModConfigSpec.BooleanValue playerLoot;
        private final ModConfigSpec.BooleanValue registryRemaps;

        private Data(ModConfigSpec.Builder builder) {
            builder.comment("Loot and registry names").push("data");
            lootInjections = builder.comment("Apply loot_injections/*.json files, which add pools to loot tables that already exist instead of replacing the whole table [Default=true]").define("lootInjections", true);
            playerLoot = builder.comment("Apply player_loot/*.json files, which roll a loot table when a player dies and drop what it makes, on top of or instead of the inventory [Default=true]").define("playerLoot", true);
            registryRemaps = builder.comment("Apply registry_remap files, which rename a registry entry so worlds saved before the rename keep their blocks and items instead of losing them [Default=true]").define("registryRemaps", true);
            builder.pop();
        }

        public boolean lootInjections() { return lootInjections.get(); }

        public boolean playerLoot() { return playerLoot.get(); }

        public boolean registryRemaps() { return registryRemaps.get(); }
    }

    public static final class Worldgen {
        private final ModConfigSpec.BooleanValue worldgenDebug;

        private Worldgen(ModConfigSpec.Builder builder) {
            builder.comment("What generates in the world, and what is stopped from generating").push("worldgen");
            worldgenDebug = builder.comment("Write the debug lines other messages refer to into logs/rdpl.log, such as which pack served a file and what each command did. Very verbose [Default=false]").define("worldgenDebug", false);
            builder.pop();
        }

        public boolean worldgenDebug() { return loaded() ? worldgenDebug.get() : ConfigCore.flag("worldgen.worldgenDebug", false); }
    }
}
