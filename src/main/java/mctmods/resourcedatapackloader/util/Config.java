package mctmods.resourcedatapackloader.util;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ConfigPacks packs;
    public static final ConfigContent content;
    public static final ConfigEntities entities;
    public static final ConfigRecipes recipes;
    public static final ConfigData data;
    public static final ConfigWorldgen worldgen;
    public static final ConfigTweaks tweaks;
    public static final ConfigChunks chunks;
    public static final ConfigCommands commands;
    public static final ConfigControl control;
    public static final String WELCOME = "Welcome to your World!";
    public static final String PREGEN_RUNNING = "World pregeneration running, %d%% done";
    public static final String PREGEN_FINISHED = "World pregeneration finished";
    public static final String PREGEN_STOPPED = "World pregeneration stopped";
    public static final String PREGEN_SPECTATING = "Spectating until the world is ready";

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        packs = new ConfigPacks(builder);
        content = new ConfigContent(builder);
        entities = new ConfigEntities(builder);
        recipes = new ConfigRecipes(builder);
        data = new ConfigData(builder);
        worldgen = new ConfigWorldgen(builder);
        tweaks = new ConfigTweaks(builder);
        chunks = new ConfigChunks(builder);
        commands = new ConfigCommands(builder);
        control = new ConfigControl(builder);
        SPEC = builder.build();
    }

    private Config() {}

    public static boolean loaded() { return SPEC.isLoaded(); }

    public static boolean contentOff() { return content.loadOff() || content.vanillaClients(); }

    public static boolean definitionsOff() { return content.loadOff(); }
}
