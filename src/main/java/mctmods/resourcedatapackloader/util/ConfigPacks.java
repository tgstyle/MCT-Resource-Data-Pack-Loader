package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigPacks {
    private final ForgeConfigSpec.ConfigValue<String> rootDirectory;
    private final ForgeConfigSpec.BooleanValue overrideResourcePacks;
    private final ForgeConfigSpec.BooleanValue warnOnCaseMismatch;
    private final ForgeConfigSpec.BooleanValue logContents;
    private final ForgeConfigSpec.BooleanValue traceUnresolvedVariables;

    ConfigPacks(ForgeConfigSpec.Builder builder) {
        builder.comment("How pack folders are found and served").push("packs");
        rootDirectory = builder.comment("Folder packs are loaded from, relative to the .minecraft directory. An absolute path also works. Requires a restart [Default=rdploader]").worldRestart().define("rootDirectory", PackManager.ROOT_DIRECTORY);
        overrideResourcePacks = builder.comment("Insert the asset pack above the player's selected resource packs and the world's own data packs. A pack named RDPLO... always overrides, RDPLN... never does [Default=true]").define("overrideResourcePacks", true);
        warnOnCaseMismatch = builder.comment("Warn when a file only matches because the filesystem is case-insensitive. Such packs break on Linux [Default=true]").define("warnOnCaseMismatch", true);
        logContents = builder.comment("Log every pack found and how many files it provides [Default=false]").define("logContents", false);
        traceUnresolvedVariables = builder.comment("Log a stack trace the first time a file with a '#' in its name is requested, naming whatever asked for it [Default=false]").define("traceUnresolvedVariables", false);
        builder.pop();
    }

    public String rootDirectory() { return Config.loaded() ? rootDirectory.get() : ConfigCore.text("packs.rootDirectory", PackManager.ROOT_DIRECTORY); }

    public boolean overrideResourcePacks() { return Config.loaded() ? overrideResourcePacks.get() : ConfigCore.flag("packs.overrideResourcePacks", true); }

    public boolean warnOnCaseMismatch() { return Config.loaded() ? warnOnCaseMismatch.get() : ConfigCore.flag("packs.warnOnCaseMismatch", true); }

    public boolean logContents() { return Config.loaded() ? logContents.get() : ConfigCore.flag("packs.logContents", false); }

    public boolean traceUnresolvedVariables() { return Config.loaded() ? traceUnresolvedVariables.get() : ConfigCore.flag("packs.traceUnresolvedVariables", false); }
}
