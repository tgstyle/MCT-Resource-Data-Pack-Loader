package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfigContent {
    private final ModConfigSpec.BooleanValue load;
    private final ModConfigSpec.BooleanValue vanillaClients;
    private final ModConfigSpec.BooleanValue sounds;
    private final ModConfigSpec.BooleanValue fuels;
    private final ModConfigSpec.BooleanValue potions;
    private final ModConfigSpec.BooleanValue brewing;
    private final ModConfigSpec.BooleanValue villagers;
    private final ModConfigSpec.BooleanValue entities;
    private final ModConfigSpec.BooleanValue biomes;
    private final ModConfigSpec.BooleanValue dimensions;
    private final ModConfigSpec.BooleanValue villages;
    private final ModConfigSpec.BooleanValue overrides;
    private final ModConfigSpec.BooleanValue hardness;
    private final ModConfigSpec.BooleanValue shovelPaths;
    private final ModConfigSpec.ConfigValue<String> shovelPathBecomes;
    private final ModConfigSpec.ConfigValue<String> shovelPathReverts;
    private final ModConfigSpec.BooleanValue hoeTilling;
    private final ModConfigSpec.ConfigValue<String> hoeTillsInto;
    private final ModConfigSpec.IntValue caneMaxHeight;
    private final ModConfigSpec.IntValue cactusMaxHeight;

    ConfigContent(ModConfigSpec.Builder builder) {
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

    public boolean loadOff() { return Config.loaded() ? !load.get() : !ConfigCore.flag("content.load", true); }

    public boolean vanillaClients() { return Config.loaded() ? vanillaClients.get() : ConfigCore.flag("content.vanillaClients", false); }

    public boolean sounds() { return Config.loaded() ? sounds.get() : ConfigCore.flag("content.sounds", true); }

    public boolean fuels() { return Config.loaded() ? fuels.get() : ConfigCore.flag("content.fuels", true); }

    public boolean potions() { return Config.loaded() ? potions.get() : ConfigCore.flag("content.potions", true); }

    public boolean brewing() { return Config.loaded() ? brewing.get() : ConfigCore.flag("content.brewing", true); }

    public boolean villagers() { return Config.loaded() ? villagers.get() : ConfigCore.flag("content.villagers", true); }

    public boolean entities() { return Config.loaded() ? entities.get() : ConfigCore.flag("content.entities", true); }

    public boolean biomes() { return Config.loaded() ? biomes.get() : ConfigCore.flag("content.biomes", true); }

    public boolean dimensions() { return Config.loaded() ? dimensions.get() : ConfigCore.flag("content.dimensions", true); }

    public boolean villages() { return Config.loaded() ? villages.get() : ConfigCore.flag("content.villages", true); }

    public boolean overrides() { return Config.loaded() ? overrides.get() : ConfigCore.flag("content.overrides", true); }

    public boolean hardness() { return Config.loaded() ? hardness.get() : ConfigCore.flag("content.hardness", true); }

    public boolean shovelPaths() { return Config.loaded() ? shovelPaths.get() : ConfigCore.flag("content.shovelPaths", true); }

    public String shovelPathBecomes() { return Config.loaded() ? shovelPathBecomes.get() : ConfigCore.text("content.shovelPathBecomes", ""); }

    public String shovelPathReverts() { return Config.loaded() ? shovelPathReverts.get() : ConfigCore.text("content.shovelPathReverts", ""); }

    public boolean hoeTilling() { return Config.loaded() ? hoeTilling.get() : ConfigCore.flag("content.hoeTilling", true); }

    public String hoeTillsInto() { return Config.loaded() ? hoeTillsInto.get() : ConfigCore.text("content.hoeTillsInto", ""); }

    public int caneMaxHeight() { return Config.loaded() ? caneMaxHeight.get() : 3; }

    public int cactusMaxHeight() { return Config.loaded() ? cactusMaxHeight.get() : 3; }
}
