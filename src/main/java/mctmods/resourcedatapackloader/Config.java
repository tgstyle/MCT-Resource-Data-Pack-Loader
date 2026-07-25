package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.pack.PackManager;

@net.minecraftforge.common.config.Config(modid = MCTMixin.MIXIN_ID, name = "mct_resourcedatapackloader_mixin")
public class Config {
    public static Settings settings = new Settings();

    public static class Settings {
        @net.minecraftforge.common.config.Config.Comment("Folder packs are loaded from, relative to the .minecraft directory. An absolute path also works. Requires a restart [Default=rdloader]")
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        public String rootDirectory = PackManager.ROOT_DIRECTORY;
        @net.minecraftforge.common.config.Config.Comment("Insert the asset pack above the player's selected resource packs, so loaded files cannot be overridden by them [Default=true]")
        public boolean overrideResourcePacks = true;
        @net.minecraftforge.common.config.Config.Comment("Warn when a file only matches because the filesystem is case-insensitive. Such packs break on Linux [Default=true]")
        public boolean warnOnCaseMismatch = true;
        @net.minecraftforge.common.config.Config.Comment("Log every pack found and how many files of each type it provides [Default=false]")
        public boolean logPackContents = false;
        @net.minecraftforge.common.config.Config.Comment("Log a stack trace the first time a file with a '#' in its name is requested, such as textures/#texture.png. These come from a model whose texture variable was never defined, and the trace names whatever asked for it [Default=false]")
        public boolean traceUnresolvedVariables = false;
        @net.minecraftforge.common.config.Config.Comment("Stop this mod from replacing or adding recipes. Only needed if another coremod conflicts over recipe loading [Default=false]")
        public boolean disableRecipeOverrides = false;
        @net.minecraftforge.common.config.Config.Comment("Let advancements load when they refer to a recipe that no longer exists, instead of failing. The advancement still works, it just never unlocks that recipe [Default=true]")
        public boolean tolerateMissingRecipes = true;
    }
}
