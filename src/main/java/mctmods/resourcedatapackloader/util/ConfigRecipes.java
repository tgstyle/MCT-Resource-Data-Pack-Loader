package mctmods.resourcedatapackloader.util;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;

public final class ConfigRecipes {
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

    ConfigRecipes(ForgeConfigSpec.Builder builder) {
        builder.comment("Recipe files, removals and blocking").push("recipes");
        furnace = builder.comment("Apply furnace/*.json files, which add and remove furnace smelting recipes [Default=true]").define("furnace", true);
        removals = builder.comment("Apply recipe_removals/*.json files, which delete crafting recipes by name, namespace or output [Default=true]").define("removals", true);
        skipMissingItems = builder.comment("Skip recipes that use an item which is not registered, instead of letting them fail. The count is logged once [Default=true]").define("skipMissingItems", true);
        blockRecipes = builder.comment("Remove every crafting recipe, keeping only the mods in recipeWhitelist. Include your pack's namespace to keep its own recipes [Default=false]").define("blockRecipes", false);
        recipeWhitelist = builder.comment("Mod ids whose crafting recipes survive while blockRecipes is on. Include your pack's namespace to keep its own recipes").defineListAllowEmpty("recipeWhitelist", List.of("minecraft"), each -> each instanceof String);
        blockedRecipeMods = builder.comment("Mod ids whose crafting recipes are removed outright, whoever they belong to and whatever the whitelist says").defineListAllowEmpty("blockedRecipeMods", List.of(), each -> each instanceof String);
        recipeMatch = builder.comment("What the mod id is read from when blocking crafting recipes. 'recipe' uses the recipe's own name, 'output' uses the item it makes, 'both' blocks if either matches and spares if either is whitelisted [Default=recipe]").define("recipeMatch", "recipe");
        blockFurnaceRecipes = builder.comment("Remove every furnace recipe, keeping only the mods in furnaceWhitelist. The mod is read from the item produced [Default=false]").define("blockFurnaceRecipes", false);
        furnaceWhitelist = builder.comment("Mod ids whose furnace recipes survive while blockFurnaceRecipes is on. Include your pack's namespace to keep its own recipes").defineListAllowEmpty("furnaceWhitelist", List.of("minecraft"), each -> each instanceof String);
        blockedFurnaceMods = builder.comment("Mod ids whose furnace recipes are removed outright, whatever the whitelist says").defineListAllowEmpty("blockedFurnaceMods", List.of(), each -> each instanceof String);
        logBlockedRecipes = builder.comment("Log a per mod count of what was blocked, so you can see what to whitelist [Default=true]").define("logBlockedRecipes", true);
        builder.pop();
    }

    public boolean furnace() { return furnace.get(); }

    public boolean removals() { return removals.get(); }

    public boolean skipMissingItems() { return skipMissingItems.get(); }

    public boolean blockRecipes() { return blockRecipes.get(); }

    public List<String> recipeWhitelist() { return Config.loaded() ? List.copyOf(recipeWhitelist.get()) : List.of("minecraft"); }

    public List<String> blockedRecipeMods() { return Config.loaded() ? List.copyOf(blockedRecipeMods.get()) : List.of(); }

    public String recipeMatch() { return recipeMatch.get(); }

    public boolean blockFurnaceRecipes() { return blockFurnaceRecipes.get(); }

    public List<String> furnaceWhitelist() { return Config.loaded() ? List.copyOf(furnaceWhitelist.get()) : List.of("minecraft"); }

    public List<String> blockedFurnaceMods() { return Config.loaded() ? List.copyOf(blockedFurnaceMods.get()) : List.of(); }

    public boolean logBlockedRecipes() { return logBlockedRecipes.get(); }
}
