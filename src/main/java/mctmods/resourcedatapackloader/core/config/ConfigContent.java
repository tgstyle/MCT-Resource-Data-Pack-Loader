package mctmods.resourcedatapackloader.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfigContent {
    private final ModConfigSpec.BooleanValue load;
    private final ModConfigSpec.BooleanValue vanillaClients;
    private final ModConfigSpec.BooleanValue sounds;
    private final ModConfigSpec.BooleanValue fuels;
    private final ModConfigSpec.BooleanValue tooltips;
    private final ModConfigSpec.BooleanValue potions;
    private final ModConfigSpec.BooleanValue enchantments;
    private final ModConfigSpec.BooleanValue brewing;
    private final ModConfigSpec.BooleanValue villagers;
    private final ModConfigSpec.BooleanValue entities;
    private final ModConfigSpec.BooleanValue biomes;
    private final ModConfigSpec.BooleanValue dimensions;
    private final ModConfigSpec.BooleanValue villages;
    private final ModConfigSpec.BooleanValue overrides;
    private final ModConfigSpec.BooleanValue disabled;
    private final ModConfigSpec.BooleanValue itemLocks;
    private final ModConfigSpec.BooleanValue weights;
    private final ModConfigSpec.BooleanValue hardness;
    private final ModConfigSpec.BooleanValue shovelPaths;
    private final ModConfigSpec.ConfigValue<String> shovelPathBecomes;
    private final ModConfigSpec.ConfigValue<String> shovelPathReverts;
    private final ModConfigSpec.BooleanValue hoeTilling;
    private final ModConfigSpec.ConfigValue<String> hoeTillsInto;
    private final ModConfigSpec.IntValue caneMaxHeight;
    private final ModConfigSpec.IntValue cactusMaxHeight;
    private final ModConfigSpec.BooleanValue fluidRules;
    private final ModConfigSpec.IntValue fluidReactionsPerChunk;
    private final ModConfigSpec.IntValue fluidReactionsPerDimension;
    private final ModConfigSpec.IntValue fluidWatched;

    ConfigContent(ModConfigSpec.Builder builder) {
        builder.comment("Blocks, items, fluids and everything else packs define").push("content");
        load = builder.comment("True registers the blocks, items, fluids, materials and creative tabs that packs define. False registers none of them. Requires a restart [Default=true]").worldRestart().define("load", true);
        vanillaClients = builder.comment("True serves plain vanilla clients: nothing from any pack is registered, no blocks, items, fluids or creative tabs, so a client without the mod can join. Everything that lives on the server alone still applies. False registers pack content for clients as usual. Requires a restart [Default=false]").worldRestart().define("vanillaClients", false);
        sounds = builder.comment("True registers the sound events named by sounds/*.json, so packs can ship their own audio. False registers no sound events [Default=true]").define("sounds", true);
        fuels = builder.comment("True applies fuels/*.json files, which give items a furnace burn time. False ignores them [Default=true]").define("fuels", true);
        tooltips = builder.comment("True applies tooltips/*.json files, which add tooltip lines to items and blocks and a description under an enchantment's name. False ignores them [Default=true]").define("tooltips", true);
        potions = builder.comment("True registers the potion effects and potion types described by potions/*.json and potion_types/*.json in packs. False registers none of them. Requires a restart [Default=true]").worldRestart().define("potions", true);
        enchantments = builder.comment("True generates the enchantments described by enchantments/*.json in packs as data pack enchantments. False generates none of them. Requires a restart [Default=true]").worldRestart().define("enchantments", true);
        brewing = builder.comment("True applies brewing/*.json files, which add brewing stand recipes. False ignores them [Default=true]").define("brewing", true);
        villagers = builder.comment("True registers the villager professions described by villagers/*.json and applies the trades in trades/*.json. False registers no professions and applies no trades. Requires a restart [Default=true]").worldRestart().define("villagers", true);
        entities = builder.comment("True registers the entity variants described by entities/*.json in packs. False registers none of them. Requires a restart [Default=true]").worldRestart().define("entities", true);
        biomes = builder.comment("True registers the biomes described by biomes/*.json in packs and places them into world generation. False registers none of them and places none. Requires a restart [Default=true]").worldRestart().define("biomes", true);
        dimensions = builder.comment("True registers the dimensions described by dimensions/*.json in packs. False registers none of them, and leaves worlds that contain them unable to load those dimensions. Requires a restart [Default=true]").worldRestart().define("dimensions", true);
        villages = builder.comment("True registers the village plots described by villages/*.json in packs so cities and villages can build them. False registers none, so they cannot build them. Requires a restart [Default=true]").worldRestart().define("villages", true);
        overrides = builder.comment("True applies overrides/<namespace>/<name>.json files, which change properties of blocks, items and potion types that already exist, vanilla or modded. False ignores them [Default=true]").define("overrides", true);
        disabled = builder.comment("True applies disabled/*.json files, which take blocks and items out of play: no creative tab, JEI entry, recipe, loot, trade, tag, placing, use or pick-up, and stacks of them are deleted. False ignores them [Default=true]").define("disabled", true);
        itemLocks = builder.comment("True applies item_locks/*.json files, which keep items out of the creative tabs, creative search and JEI for each player until that player has earned an advancement. False ignores them [Default=true]").define("itemLocks", true);
        weights = builder.comment("True applies weights/*.json files and the weight key of item, block and override files, which give items a carried weight. False ignores them [Default=true]").define("weights", true);
        hardness = builder.comment("True applies hardness/*.json files, which give a group of blocks a mining time and blast resistance multiplier, rolled per block position. False ignores them [Default=true]").define("hardness", true);
        shovelPaths = builder.comment("True lets a shovel turn blocks marked behavesAs path into a path, and revert a path while sneaking. False stops both [Default=true]").define("shovelPaths", true);
        shovelPathBecomes = builder.comment("What a shovel turns those blocks into. Empty uses the dirt path").define("shovelPathBecomes", "");
        shovelPathReverts = builder.comment("What sneaking with a shovel turns a path back into. Empty uses dirt").define("shovelPathReverts", "");
        hoeTilling = builder.comment("True lets a hoe till blocks marked behavesAs till. False stops it [Default=true]").define("hoeTilling", true);
        hoeTillsInto = builder.comment("What a hoe turns those blocks into. Empty uses farmland").define("hoeTillsInto", "");
        caneMaxHeight = builder.comment("How tall vanilla sugar cane grows. Vanilla is 3. Pack defined cane blocks use their own growth section and ignore this [Default=3]").defineInRange("caneMaxHeight", 3, 1, 255);
        cactusMaxHeight = builder.comment("The same for vanilla cactus [Default=3]").defineInRange("cactusMaxHeight", 3, 1, 255);
        fluidRules = builder.comment("True reads fluid_rules/*.json files, which attach reactions to water and lava. False ignores them [Default=true]").define("fluidRules", true);
        fluidReactionsPerChunk = builder.comment("Reaction points a fluid may spend per chunk per tick. A block change costs 1, a function 4, an explosion 16. A check over the budget is tried again on the next tick [Default=16]").defineInRange("fluidReactionsPerChunk", 16, 1, 100000);
        fluidReactionsPerDimension = builder.comment("Reaction points all fluids may spend per dimension per tick [Default=256]").defineInRange("fluidReactionsPerDimension", 256, 1, 1000000);
        fluidWatched = builder.comment("Most fluid contacts kept on watch per dimension, for reactions held back by the budget or set to recheck on an interval [Default=4096]").defineInRange("fluidWatched", 4096, 1, 1000000);
        builder.pop();
    }

    public boolean loadOff() { return Config.loaded() ? !load.get() : !ConfigCore.flag("content.load", true); }

    public boolean vanillaClients() { return Config.loaded() ? vanillaClients.get() : ConfigCore.flag("content.vanillaClients", false); }

    public boolean sounds() { return Config.loaded() ? sounds.get() : ConfigCore.flag("content.sounds", true); }

    public boolean fuels() { return Config.loaded() ? fuels.get() : ConfigCore.flag("content.fuels", true); }

    public boolean tooltips() { return Config.loaded() ? tooltips.get() : ConfigCore.flag("content.tooltips", true); }

    public boolean potions() { return Config.loaded() ? potions.get() : ConfigCore.flag("content.potions", true); }

    public boolean enchantments() { return Config.loaded() ? enchantments.get() : ConfigCore.flag("content.enchantments", true); }

    public boolean brewing() { return Config.loaded() ? brewing.get() : ConfigCore.flag("content.brewing", true); }

    public boolean villagers() { return Config.loaded() ? villagers.get() : ConfigCore.flag("content.villagers", true); }

    public boolean entities() { return Config.loaded() ? entities.get() : ConfigCore.flag("content.entities", true); }

    public boolean biomes() { return Config.loaded() ? biomes.get() : ConfigCore.flag("content.biomes", true); }

    public boolean dimensions() { return Config.loaded() ? dimensions.get() : ConfigCore.flag("content.dimensions", true); }

    public boolean villages() { return Config.loaded() ? villages.get() : ConfigCore.flag("content.villages", true); }

    public boolean overrides() { return Config.loaded() ? overrides.get() : ConfigCore.flag("content.overrides", true); }

    public boolean disabled() { return Config.loaded() ? disabled.get() : ConfigCore.flag("content.disabled", true); }

    public boolean itemLocks() { return Config.loaded() ? itemLocks.get() : ConfigCore.flag("content.itemLocks", true); }

    public boolean weights() { return Config.loaded() ? weights.get() : ConfigCore.flag("content.weights", true); }

    public boolean hardness() { return Config.loaded() ? hardness.get() : ConfigCore.flag("content.hardness", true); }

    public boolean shovelPaths() { return Config.loaded() ? shovelPaths.get() : ConfigCore.flag("content.shovelPaths", true); }

    public String shovelPathBecomes() { return Config.loaded() ? shovelPathBecomes.get() : ConfigCore.text("content.shovelPathBecomes", ""); }

    public String shovelPathReverts() { return Config.loaded() ? shovelPathReverts.get() : ConfigCore.text("content.shovelPathReverts", ""); }

    public boolean hoeTilling() { return Config.loaded() ? hoeTilling.get() : ConfigCore.flag("content.hoeTilling", true); }

    public String hoeTillsInto() { return Config.loaded() ? hoeTillsInto.get() : ConfigCore.text("content.hoeTillsInto", ""); }

    public int caneMaxHeight() { return Config.loaded() ? caneMaxHeight.get() : 3; }

    public int cactusMaxHeight() { return Config.loaded() ? cactusMaxHeight.get() : 3; }

    public boolean fluidRules() { return Config.loaded() ? fluidRules.get() : ConfigCore.flag("content.fluidRules", true); }

    public int fluidReactionsPerChunk() { return Config.loaded() ? fluidReactionsPerChunk.get() : ConfigCore.number("content.fluidReactionsPerChunk", 16); }

    public int fluidReactionsPerDimension() { return Config.loaded() ? fluidReactionsPerDimension.get() : ConfigCore.number("content.fluidReactionsPerDimension", 256); }

    public int fluidWatched() { return Config.loaded() ? fluidWatched.get() : ConfigCore.number("content.fluidWatched", 4096); }
}
