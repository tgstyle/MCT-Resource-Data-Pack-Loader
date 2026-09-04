package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Config {
    public static final ForgeConfigSpec SPEC;
    public static final Packs packs;
    public static final Worldgen worldgen;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        packs = new Packs(builder);
        worldgen = new Worldgen(builder);
        SPEC = builder.build();
    }

    private Config() {}

    public static boolean loaded() { return SPEC.isLoaded(); }

    public static final class Packs {
        private final ForgeConfigSpec.ConfigValue<String> rootDirectory;
        private final ForgeConfigSpec.BooleanValue overrideResourcePacks;
        private final ForgeConfigSpec.BooleanValue warnOnCaseMismatch;
        private final ForgeConfigSpec.BooleanValue logContents;
        private final ForgeConfigSpec.BooleanValue traceUnresolvedVariables;

        private Packs(ForgeConfigSpec.Builder builder) {
            builder.comment("How pack folders are found and served").push("packs");
            rootDirectory = builder.comment("Folder packs are loaded from, relative to the .minecraft directory. An absolute path also works. Requires a restart [Default=rdploader]").worldRestart().define("rootDirectory", PackManager.ROOT_DIRECTORY);
            overrideResourcePacks = builder.comment("Insert the asset pack above the player's selected resource packs and the world's own data packs. A pack named RDPLO... always overrides, RDPLN... never does [Default=true]").define("overrideResourcePacks", true);
            warnOnCaseMismatch = builder.comment("Warn when a file only matches because the filesystem is case-insensitive. Such packs break on Linux [Default=true]").define("warnOnCaseMismatch", true);
            logContents = builder.comment("Log every pack found and how many files it provides [Default=false]").define("logContents", false);
            traceUnresolvedVariables = builder.comment("Log a stack trace the first time a file with a '#' in its name is requested, naming whatever asked for it [Default=false]").define("traceUnresolvedVariables", false);
            builder.pop();
        }

        public String rootDirectory() { return loaded() ? rootDirectory.get() : ConfigCore.text("packs.rootDirectory", PackManager.ROOT_DIRECTORY); }

        public boolean overrideResourcePacks() { return loaded() ? overrideResourcePacks.get() : ConfigCore.flag("packs.overrideResourcePacks", true); }

        public boolean warnOnCaseMismatch() { return loaded() ? warnOnCaseMismatch.get() : ConfigCore.flag("packs.warnOnCaseMismatch", true); }

        public boolean logContents() { return loaded() ? logContents.get() : ConfigCore.flag("packs.logContents", false); }

        public boolean traceUnresolvedVariables() { return loaded() ? traceUnresolvedVariables.get() : ConfigCore.flag("packs.traceUnresolvedVariables", false); }
    }

    public static final class Worldgen {
        private final ForgeConfigSpec.BooleanValue worldgenDebug;

        private Worldgen(ForgeConfigSpec.Builder builder) {
            builder.comment("What generates in the world, and what is stopped from generating").push("worldgen");
            worldgenDebug = builder.comment("Write the debug lines other messages refer to into logs/rdpl.log, such as which pack served a file and what each command did. Very verbose [Default=false]").define("worldgenDebug", false);
            builder.pop();
        }

        public boolean worldgenDebug() { return loaded() ? worldgenDebug.get() : ConfigCore.flag("worldgen.worldgenDebug", false); }
    }
}
