package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfigControl {
    private final ModConfigSpec.ConfigValue<String> terrain;
    private final ModConfigSpec.ConfigValue<String> replacements;
    private final ModConfigSpec.ConfigValue<String> entities;
    private final ModConfigSpec.ConfigValue<String> chunks;
    private final ModConfigSpec.ConfigValue<String> bedrock;
    private final ModConfigSpec.ConfigValue<String> voidWorld;
    private final ModConfigSpec.ConfigValue<String> ores;
    private final ModConfigSpec.ConfigValue<String> generators;
    private final ModConfigSpec.ConfigValue<String> biomes;
    private final ModConfigSpec.ConfigValue<String> spawning;
    private final ModConfigSpec.ConfigValue<String> structures;
    private final ModConfigSpec.ConfigValue<String> villages;
    private final ModConfigSpec.ConfigValue<String> commands;
    private final ModConfigSpec.ConfigValue<String> recipes;
    private final ModConfigSpec.ConfigValue<String> blastPlaster;

    ConfigControl(ModConfigSpec.Builder builder) {
        builder.comment("Who decides each group of settings: default lets the active world template override the config, global uses the config alone, off turns the group off").push("control");
        terrain = builder.comment("The world's name, seed and game mode at creation, generatorOptions, the cave regions, the cloud height, the seams between worlds and the difficulty [default|global|off]").define("terrain", "default");
        chunks = builder.comment("The spawn chunk radius, pregeneration, retrogen and the reset, the welcome lines and the says card [default|global|off]").define("chunks", "default");
        bedrock = builder.comment("Flat bedrock and its dimension and biome lists [default|global|off]").define("bedrock", "default");
        voidWorld = builder.comment("Void world generation and its platform [default|global|off]").define("voidWorld", "default");
        ores = builder.comment("Blocking ore generation by mod and by ore type [default|global|off]").define("ores", "default");
        generators = builder.comment("Blocking other mods' world generation by mod, by type and by dimension [default|global|off]").define("generators", "default");
        biomes = builder.comment("Blocking biomes by mod and by name, and what replaces them [default|global|off]").define("biomes", "default");
        spawning = builder.comment("Mob spawn caps, hostile spawn rates and the light cap [default|global|off]").define("spawning", "default");
        replacements = builder.comment("Block replacement in chunks that already exist [default|global|off]").define("replacements", "default");
        entities = builder.comment("The slower pace of entities far from every player [default|global|off]").define("entities", "default");
        structures = builder.comment("Vanilla structures switched off, their spacing, separation, spawn distance, biomes, spawns, pins and terrain adaptation [default|global|off]").define("structures", "default");
        villages = builder.comment("The city and village streets a pack lays: their shape, dress, bridges, tunnels, rails, plots and plaza [default|global|off]").define("villages", "default");
        commands = builder.comment("Who may run the mod's own commands: the goto permission levels [default|global|off]").define("commands", "default");
        recipes = builder.comment("Recipe and furnace blocking and their whitelists [default|global|off]").define("recipes", "default");
        blastPlaster = builder.comment("Blast Plaster explosion handling driven from packs, with per dimension settings [default|global|off]").define("blastPlaster", "default");
        builder.pop();
    }

    public String terrain() { return Config.loaded() ? terrain.get() : ConfigCore.text("control.terrain", "default"); }

    public String chunks() { return Config.loaded() ? chunks.get() : ConfigCore.text("control.chunks", "default"); }

    public String bedrock() { return Config.loaded() ? bedrock.get() : ConfigCore.text("control.bedrock", "default"); }

    public String voidWorld() { return Config.loaded() ? voidWorld.get() : ConfigCore.text("control.voidWorld", "default"); }

    public String ores() { return Config.loaded() ? ores.get() : ConfigCore.text("control.ores", "default"); }

    public String generators() { return Config.loaded() ? generators.get() : ConfigCore.text("control.generators", "default"); }

    public String biomes() { return Config.loaded() ? biomes.get() : ConfigCore.text("control.biomes", "default"); }

    public String spawning() { return Config.loaded() ? spawning.get() : ConfigCore.text("control.spawning", "default"); }

    public String structures() { return Config.loaded() ? structures.get() : ConfigCore.text("control.structures", "default"); }

    public String villages() { return Config.loaded() ? villages.get() : ConfigCore.text("control.villages", "default"); }

    public String commands() { return Config.loaded() ? commands.get() : ConfigCore.text("control.commands", "default"); }

    public String recipes() { return Config.loaded() ? recipes.get() : ConfigCore.text("control.recipes", "default"); }

    public String blastPlaster() { return Config.loaded() ? blastPlaster.get() : ConfigCore.text("control.blastPlaster", "default"); }

    public String replacements() { return Config.loaded() ? replacements.get() : ConfigCore.text("control.replacements", "default"); }

    public String entities() { return Config.loaded() ? entities.get() : ConfigCore.text("control.entities", "default"); }
}
