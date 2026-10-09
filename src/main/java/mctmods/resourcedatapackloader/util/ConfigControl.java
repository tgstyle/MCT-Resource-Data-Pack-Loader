package mctmods.resourcedatapackloader.util;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigControl {
    private static final String DEFAULT = "default";
    private final ForgeConfigSpec.ConfigValue<String> terrain;
    private final ForgeConfigSpec.ConfigValue<String> replacements;
    private final ForgeConfigSpec.ConfigValue<String> entities;
    private final ForgeConfigSpec.ConfigValue<String> chunks;
    private final ForgeConfigSpec.ConfigValue<String> bedrock;
    private final ForgeConfigSpec.ConfigValue<String> voidWorld;
    private final ForgeConfigSpec.ConfigValue<String> ores;
    private final ForgeConfigSpec.ConfigValue<String> generators;
    private final ForgeConfigSpec.ConfigValue<String> biomes;
    private final ForgeConfigSpec.ConfigValue<String> spawning;
    private final ForgeConfigSpec.ConfigValue<String> structures;
    private final ForgeConfigSpec.ConfigValue<String> villages;
    private final ForgeConfigSpec.ConfigValue<String> commands;
    private final ForgeConfigSpec.ConfigValue<String> recipes;
    private final ForgeConfigSpec.ConfigValue<String> blastPlaster;
    private final ForgeConfigSpec.ConfigValue<String> server;

    ConfigControl(ForgeConfigSpec.Builder builder) {
        builder.comment("Who decides each group of settings: default lets the active world template override the config, global uses the config alone, off turns the group off").push("control");
        terrain = builder.comment("The world's name and seed at creation, generatorOptions, the cave regions, the cloud height and the seams between worlds [default|global|off]").define("terrain", DEFAULT);
        chunks = builder.comment("The spawn chunk radius, pregeneration, retrogen and the reset, the welcome lines and the says card [default|global|off]").define("chunks", DEFAULT);
        bedrock = builder.comment("Flat bedrock and its dimension and biome lists [default|global|off]").define("bedrock", DEFAULT);
        voidWorld = builder.comment("Void world generation and its platform [default|global|off]").define("voidWorld", DEFAULT);
        ores = builder.comment("Blocking ore generation by mod and by ore type [default|global|off]").define("ores", DEFAULT);
        generators = builder.comment("Blocking other mods' world generation by mod, by type and by dimension [default|global|off]").define("generators", DEFAULT);
        biomes = builder.comment("Blocking biomes by mod and by name, and what replaces them [default|global|off]").define("biomes", DEFAULT);
        spawning = builder.comment("Mob spawn caps, hostile spawn rates and the light cap [default|global|off]").define("spawning", DEFAULT);
        replacements = builder.comment("Block replacement in chunks that already exist [default|global|off]").define("replacements", DEFAULT);
        entities = builder.comment("The slower pace of entities far from every player [default|global|off]").define("entities", DEFAULT);
        structures = builder.comment("Vanilla structures switched off, their spacing, separation, spawn distance, biomes, spawns, pins and terrain adaptation [default|global|off]").define("structures", DEFAULT);
        villages = builder.comment("The city and village streets a pack lays: their shape, dress, bridges, tunnels, rails, plots and plaza [default|global|off]").define("villages", DEFAULT);
        commands = builder.comment("Who may run the mod's own commands: the goto permission levels [default|global|off]").define("commands", DEFAULT);
        recipes = builder.comment("Recipe and furnace blocking and their whitelists [default|global|off]").define("recipes", DEFAULT);
        blastPlaster = builder.comment("Blast Plaster explosion handling driven from packs, with per dimension settings [default|global|off]").define("blastPlaster", DEFAULT);
        server = builder.comment("The server.properties lines a pack may set, written into server.properties on a dedicated server: the game mode, the difficulty, pvp, flight, spawn protection, the Nether, command blocks, the idle timeout, the motd, the world size, structures, mob spawning, the view and simulation distances, and commands on a world opened to LAN [default|global|off]").define("server", DEFAULT);
        builder.pop();
    }

    public String terrain() { return ConfigCore.text(terrain); }

    public String chunks() { return ConfigCore.text(chunks); }

    public String bedrock() { return ConfigCore.text(bedrock); }

    public String voidWorld() { return ConfigCore.text(voidWorld); }

    public String ores() { return ConfigCore.text(ores); }

    public String generators() { return ConfigCore.text(generators); }

    public String biomes() { return ConfigCore.text(biomes); }

    public String spawning() { return ConfigCore.text(spawning); }

    public String structures() { return ConfigCore.text(structures); }

    public String villages() { return ConfigCore.text(villages); }

    public String commands() { return ConfigCore.text(commands); }

    public String recipes() { return ConfigCore.text(recipes); }

    public String blastPlaster() { return ConfigCore.text(blastPlaster); }

    public String replacements() { return ConfigCore.text(replacements); }

    public String entities() { return ConfigCore.text(entities); }

    public String server() { return ConfigCore.text(server); }
}
