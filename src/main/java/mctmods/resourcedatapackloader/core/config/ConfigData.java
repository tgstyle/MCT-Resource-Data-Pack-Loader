package mctmods.resourcedatapackloader.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfigData {
    private final ModConfigSpec.BooleanValue lootInjections;
    private final ModConfigSpec.BooleanValue lootRemovals;
    private final ModConfigSpec.BooleanValue playerLoot;
    private final ModConfigSpec.BooleanValue soulbound;
    private final ModConfigSpec.BooleanValue mobDrops;
    private final ModConfigSpec.BooleanValue registryRemaps;
    private final ModConfigSpec.BooleanValue anvils;
    private final ModConfigSpec.BooleanValue affixes;
    private final ModConfigSpec.BooleanValue enchanting;
    private final ModConfigSpec.BooleanValue blockDrops;
    private final ModConfigSpec.BooleanValue functions;

    ConfigData(ModConfigSpec.Builder builder) {
        builder.comment("Loot, functions and registry names").push("data");
        lootInjections = builder.comment("True applies loot_injections/*.json files, which add pools to loot tables that already exist instead of replacing the whole table; false applies none [Default=true]").define("lootInjections", true);
        lootRemovals = builder.comment("True applies loot_removals/*.json files, which delete named entries from loot tables that already exist, or empty a table whole; false applies none [Default=true]").define("lootRemovals", true);
        playerLoot = builder.comment("True applies player_loot/*.json files, which roll a loot table when a player dies and drop what it makes, on top of or instead of the inventory; false applies none [Default=true]").define("playerLoot", true);
        soulbound = builder.comment("True applies soulbound/*.json files, which keep marked items (an enchantment, a list of items or an NBT flag) with the player through death, and show a tooltip line on them; false applies none [Default=true]").define("soulbound", true);
        mobDrops = builder.comment("True applies mob_drops/*.json files, which strip items from what named mobs drop and add stacks to it, by killer item and damage type; false applies none [Default=true]").define("mobDrops", true);
        registryRemaps = builder.comment("True applies registry_remap files, which rename a registry entry so worlds saved before the rename keep their blocks and items instead of losing them; false applies none [Default=true]").define("registryRemaps", true);
        anvils = builder.comment("True applies anvils/*.json files, which let an anvil put named enchantments on an item for a level cost, earn an advancement as it is taken, and hold an item back from use until then; false applies none [Default=true]").define("anvils", true);
        affixes = builder.comment("True applies affixes/*.json and gems/*.json files, which give an item random attribute bonuses and sockets the first time a player holds it, and let an anvil put a gem in a socket; false applies none [Default=true]").define("affixes", true);
        enchanting = builder.comment("True applies enchanting/*.json files, which change what the enchanting table offers, asks and takes: per enchantment, per item and for the whole table; false applies none [Default=true]").define("enchanting", true);
        blockDrops = builder.comment("True applies block_drops/*.json files, which add to or replace what a block drops whenever it breaks, experience included, for blocks a pack does not own; false applies none [Default=true]").define("blockDrops", true);
        functions = builder.comment("True loads .mcfunction files from packs, so they work in every world; false loads none [Default=true]").define("functions", true);
        builder.pop();
    }

    public boolean lootInjectionsOff() { return !ConfigCore.flag(lootInjections); }

    public boolean lootRemovalsOff() { return !ConfigCore.flag(lootRemovals); }

    public boolean playerLootOff() { return !ConfigCore.flag(playerLoot); }

    public boolean soulboundOff() { return !ConfigCore.flag(soulbound); }

    public boolean mobDropsOff() { return !ConfigCore.flag(mobDrops); }

    public boolean registryRemapsOff() { return !ConfigCore.flag(registryRemaps); }

    public boolean anvilsOff() { return !ConfigCore.flag(anvils); }

    public boolean affixesOff() { return !ConfigCore.flag(affixes); }

    public boolean enchantingOff() { return !ConfigCore.flag(enchanting); }

    public boolean blockDropsOff() { return !ConfigCore.flag(blockDrops); }

    public boolean functionsOff() { return !ConfigCore.flag(functions); }
}
