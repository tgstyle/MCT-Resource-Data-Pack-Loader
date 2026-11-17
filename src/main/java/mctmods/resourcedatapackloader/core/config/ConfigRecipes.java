package mctmods.resourcedatapackloader.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public final class ConfigRecipes {
    private final ForgeConfigSpec.BooleanValue furnace;
    private final ForgeConfigSpec.BooleanValue swaps;
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

    ConfigRecipes(ForgeConfigSpec.Builder builder) {
        builder.comment("Recipe files, removals and blocking").push("recipes");
        furnace = builder.comment("True applies furnace/*.json files, which add and remove furnace smelting recipes; false applies none [Default=true]").define("furnace", true);
        swaps = builder.comment("True applies recipe_swaps/*.json files, which replace an item with another in the ingredients and results of crafting and furnace recipes; false applies none [Default=true]").define("swaps", true);
        removals = builder.comment("True applies recipe_removals/*.json files, which delete crafting recipes by name, namespace or output; false applies none [Default=true]").define("removals", true);
        skipMissingItems = builder.comment("True skips recipes that use an item which is not registered, instead of letting them fail; false lets them fail. The count is logged once [Default=true]").define("skipMissingItems", true);
        blockRecipes = builder.comment("True removes every crafting recipe, keeping only the mods in recipeWhitelist; false removes none. Include your pack's namespace to keep its own recipes [Default=false]").define("blockRecipes", false);
        recipeWhitelist = builder.comment("Mod ids whose crafting recipes survive while blockRecipes is true. Include your pack's namespace to keep its own recipes").defineListAllowEmpty("recipeWhitelist", List.of("minecraft"), ConfigCore.IS_TEXT);
        blockedRecipeMods = builder.comment("Mod ids whose crafting recipes are removed outright, whoever they belong to and whatever the whitelist says").defineListAllowEmpty("blockedRecipeMods", List.of(), ConfigCore.IS_TEXT);
        recipeMatch = builder.comment("What the mod id is read from when blocking crafting recipes. 'recipe' uses the recipe's own name, 'output' uses the item it makes, 'both' blocks if either matches and spares if either is whitelisted [Default=recipe]").define("recipeMatch", "recipe");
        blockFurnaceRecipes = builder.comment("True removes every furnace recipe, keeping only the mods in furnaceWhitelist; false removes none. The mod is read from the item produced [Default=false]").define("blockFurnaceRecipes", false);
        furnaceWhitelist = builder.comment("Mod ids whose furnace recipes survive while blockFurnaceRecipes is true. Include your pack's namespace to keep its own recipes").defineListAllowEmpty("furnaceWhitelist", List.of("minecraft"), ConfigCore.IS_TEXT);
        blockedFurnaceMods = builder.comment("Mod ids whose furnace recipes are removed outright, whatever the whitelist says").defineListAllowEmpty("blockedFurnaceMods", List.of(), ConfigCore.IS_TEXT);
        logBlockedRecipes = builder.comment("True logs a per mod count of what was blocked, so you can see what to whitelist; false logs none [Default=true]").define("logBlockedRecipes", true);
        builder.pop();
    }

    public boolean furnace() { return ConfigCore.flag(furnace); }

    public boolean swaps() { return ConfigCore.flag(swaps); }

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
