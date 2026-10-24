package mctmods.resourcedatapackloader.util;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigData {
    private final ForgeConfigSpec.BooleanValue lootInjections;
    private final ForgeConfigSpec.BooleanValue playerLoot;
    private final ForgeConfigSpec.BooleanValue registryRemaps;
    private final ForgeConfigSpec.BooleanValue anvils;
    private final ForgeConfigSpec.BooleanValue enchanting;
    private final ForgeConfigSpec.BooleanValue blockDrops;
    private final ForgeConfigSpec.BooleanValue functions;

    ConfigData(ForgeConfigSpec.Builder builder) {
        builder.comment("Loot, functions and registry names").push("data");
        lootInjections = builder.comment("Apply loot_injections/*.json files, which add pools to loot tables that already exist instead of replacing the whole table [Default=true]").define("lootInjections", true);
        playerLoot = builder.comment("Apply player_loot/*.json files, which roll a loot table when a player dies and drop what it makes, on top of or instead of the inventory [Default=true]").define("playerLoot", true);
        registryRemaps = builder.comment("Apply registry_remap files, which rename a registry entry so worlds saved before the rename keep their blocks and items instead of losing them [Default=true]").define("registryRemaps", true);
        anvils = builder.comment("Apply anvils/*.json files, which let an anvil put named enchantments on an item for a level cost, earn an advancement as it is taken, and hold an item back from use until then [Default=true]").define("anvils", true);
        enchanting = builder.comment("Apply enchanting/*.json files, which change what the enchanting table offers, asks and takes: per enchantment, per item and for the whole table [Default=true]").define("enchanting", true);
        blockDrops = builder.comment("Apply block_drops/*.json files, which add to or replace what a block drops whenever it breaks, experience included, for blocks a pack does not own [Default=true]").define("blockDrops", true);
        functions = builder.comment("Load .mcfunction files from packs, so they work in every world [Default=true]").define("functions", true);
        builder.pop();
    }

    public boolean lootInjectionsOff() { return !ConfigCore.flag(lootInjections); }

    public boolean playerLootOff() { return !ConfigCore.flag(playerLoot); }

    public boolean registryRemapsOff() { return !ConfigCore.flag(registryRemaps); }

    public boolean anvilsOff() { return !ConfigCore.flag(anvils); }

    public boolean enchantingOff() { return !ConfigCore.flag(enchanting); }

    public boolean blockDropsOff() { return !ConfigCore.flag(blockDrops); }

    public boolean functionsOff() { return !ConfigCore.flag(functions); }
}
