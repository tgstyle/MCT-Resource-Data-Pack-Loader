package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public final class ConfigRecipes {
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

    ConfigRecipes(ModConfigSpec.Builder builder) {
        builder.comment("Recipe files, removals and blocking").push("recipes");
        furnace = builder.comment("Apply furnace/*.json files, which add and remove furnace smelting recipes [Default=true]").define("furnace", true);
        removals = builder.comment("Apply recipe_removals/*.json files, which delete crafting recipes by name, namespace or output [Default=true]").define("removals", true);
        skipMissingItems = builder.comment("Skip recipes that use an item which is not registered, instead of letting them fail. The count is logged once [Default=true]").define("skipMissingItems", true);
        blockRecipes = builder.comment("Remove every crafting recipe, keeping only the mods in recipeWhitelist. Include your pack's namespace to keep its own recipes [Default=false]").define("blockRecipes", false);
        recipeWhitelist = builder.comment("Mod ids whose crafting recipes survive while blockRecipes is on. Include your pack's namespace to keep its own recipes").defineListAllowEmpty("recipeWhitelist", List.of("minecraft"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockedRecipeMods = builder.comment("Mod ids whose crafting recipes are removed outright, whoever they belong to and whatever the whitelist says").defineListAllowEmpty("blockedRecipeMods", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        recipeMatch = builder.comment("What the mod id is read from when blocking crafting recipes. 'recipe' uses the recipe's own name, 'output' uses the item it makes, 'both' blocks if either matches and spares if either is whitelisted [Default=recipe]").define("recipeMatch", "recipe");
        blockFurnaceRecipes = builder.comment("Remove every furnace recipe, keeping only the mods in furnaceWhitelist. The mod is read from the item produced [Default=false]").define("blockFurnaceRecipes", false);
        furnaceWhitelist = builder.comment("Mod ids whose furnace recipes survive while blockFurnaceRecipes is on. Include your pack's namespace to keep its own recipes").defineListAllowEmpty("furnaceWhitelist", List.of("minecraft"), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        blockedFurnaceMods = builder.comment("Mod ids whose furnace recipes are removed outright, whatever the whitelist says").defineListAllowEmpty("blockedFurnaceMods", List.of(), ConfigCore.BLANK, ConfigCore.IS_TEXT);
        logBlockedRecipes = builder.comment("Log a per mod count of what was blocked, so you can see what to whitelist [Default=true]").define("logBlockedRecipes", true);
        builder.pop();
    }

    public boolean furnace() { return ConfigCore.flag(furnace); }

    public boolean removals() { return ConfigCore.flag(removals); }

    public boolean skipMissingItems() { return ConfigCore.flag(skipMissingItems); }

    public boolean blockRecipes() { return ConfigCore.flag(blockRecipes); }

    public List<String> recipeWhitelist() { return ConfigCore.strings(recipeWhitelist); }

    public List<String> blockedRecipeMods() { return ConfigCore.strings(blockedRecipeMods); }

    public String recipeMatch() { return ConfigCore.text(recipeMatch); }

    public boolean blockFurnaceRecipes() { return ConfigCore.flag(blockFurnaceRecipes); }

    public List<String> furnaceWhitelist() { return ConfigCore.strings(furnaceWhitelist); }

    public List<String> blockedFurnaceMods() { return ConfigCore.strings(blockedFurnaceMods); }

    public boolean logBlockedRecipes() { return ConfigCore.flag(logBlockedRecipes); }
}
