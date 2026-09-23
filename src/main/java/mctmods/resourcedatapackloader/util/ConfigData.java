package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfigData {
    private final ModConfigSpec.BooleanValue lootInjections;
    private final ModConfigSpec.BooleanValue playerLoot;
    private final ModConfigSpec.BooleanValue registryRemaps;
    private final ModConfigSpec.BooleanValue anvils;
    private final ModConfigSpec.BooleanValue blockDrops;
    private final ModConfigSpec.BooleanValue functions;

    ConfigData(ModConfigSpec.Builder builder) {
        builder.comment("Loot, functions and registry names").push("data");
        lootInjections = builder.comment("Apply loot_injections/*.json files, which add pools to loot tables that already exist instead of replacing the whole table [Default=true]").define("lootInjections", true);
        playerLoot = builder.comment("Apply player_loot/*.json files, which roll a loot table when a player dies and drop what it makes, on top of or instead of the inventory [Default=true]").define("playerLoot", true);
        registryRemaps = builder.comment("Apply registry_remap files, which rename a registry entry so worlds saved before the rename keep their blocks and items instead of losing them [Default=true]").define("registryRemaps", true);
        anvils = builder.comment("Apply anvils/*.json files, which let an anvil put named enchantments on an item for a level cost, earn an advancement as it is taken, and hold an item back from use until then [Default=true]").define("anvils", true);
        blockDrops = builder.comment("Apply block_drops/*.json files, which add to or replace what a block drops whenever it breaks, experience included, for blocks a pack does not own [Default=true]").define("blockDrops", true);
        functions = builder.comment("Load .mcfunction files from packs, so they work in every world [Default=true]").define("functions", true);
        builder.pop();
    }

    public boolean lootInjectionsOff() { return !lootInjections.get(); }

    public boolean playerLootOff() { return !playerLoot.get(); }

    public boolean registryRemapsOff() { return !registryRemaps.get(); }

    public boolean anvilsOff() { return !anvils.get(); }

    public boolean blockDropsOff() { return !blockDrops.get(); }

    public boolean functionsOff() { return !functions.get(); }
}
