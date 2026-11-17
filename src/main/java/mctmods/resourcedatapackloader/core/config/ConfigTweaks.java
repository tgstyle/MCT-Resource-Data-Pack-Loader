package mctmods.resourcedatapackloader.core.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfigTweaks {
    private final ModConfigSpec.BooleanValue promptLeafDecay;
    private final ModConfigSpec.BooleanValue lenientPaths;
    private final ModConfigSpec.BooleanValue unbreakableSpawners;
    private final ModConfigSpec.BooleanValue experimentalWarning;
    private final ModConfigSpec.BooleanValue privacy;
    private final ModConfigSpec.BooleanValue chatReporting;
    private final ModConfigSpec.BooleanValue darkSplash;
    private final ModConfigSpec.BooleanValue hideRecipeBookButton;

    ConfigTweaks(ModConfigSpec.Builder builder) {
        builder.comment("Small changes to how vanilla behaves").push("tweaks");
        promptLeafDecay = builder.comment("True makes leaves that lose their tree decay within a second instead of waiting on random ticks. False leaves them waiting on random ticks [Default=true]").define("promptLeafDecay", true);
        lenientPaths = builder.comment("True lets paths be made under a block and stay there when one is placed above. False stops a path staying there when a block is placed above it [Default=true]").define("lenientPaths", true);
        unbreakableSpawners = builder.comment("True makes mob spawners unbreakable: they cannot be mined or blown up. Creative mode still removes them. False leaves them breakable. Requires a restart [Default=false]").define("unbreakableSpawners", false);
        experimentalWarning = builder.comment("True shows the game's experimental settings warning when a world is made or opened. False answers it as if you had clicked proceed [Default=false]").define("experimentalWarning", false);
        privacy = builder.comment("True turns off the game's telemetry: no telemetry event is sent or logged. False leaves telemetry as vanilla has it. Chat reporting is its own option, chatReporting. Takes effect on the next world or server joined [Default=true]").define("privacy", true);
        chatReporting = builder.comment("Warning! Disabling chat reporting is claimed by Mojang to violate Minecraft usage guidelines, do so at your own risk. True keeps the game's chat signing and reporting as vanilla has them. False turns them off: the client signs no chat message, the server keeps no chat session and does not require one, so no message anybody sends can be reported. The warning toast about a server that does not enforce secure chat is hidden by chunks.hiddenToasts, not here. Takes effect on the next world or server joined [Default=true]").define("chatReporting", true);
        darkSplash = builder.comment("True draws the loading screen dark with the pack loader's logo in place of the game's: the logo is swapped as the screen is made, and the game's own Monochrome Logo option is turned on when it is still off, which takes effect at the next start. False leaves the option as it is [Default=true]").define("darkSplash", true);
        hideRecipeBookButton = builder.comment("True removes the Recipe Book button from the inventory and crafting table screens, and keeps the recipe book panel closed there. False leaves both alone [Default=false]").define("hideRecipeBookButton", false);
        builder.pop();
    }

    public boolean promptLeafDecay() { return Config.loaded() ? promptLeafDecay.get() : ConfigCore.flag("tweaks.promptLeafDecay", true); }

    public boolean lenientPaths() { return Config.loaded() ? lenientPaths.get() : ConfigCore.flag("tweaks.lenientPaths", true); }

    public boolean unbreakableSpawners() { return Config.loaded() ? unbreakableSpawners.get() : ConfigCore.flag("tweaks.unbreakableSpawners", false); }

    public boolean experimentalWarning() { return Config.loaded() ? experimentalWarning.get() : ConfigCore.flag("tweaks.experimentalWarning", false); }

    public boolean privacy() { return Config.loaded() ? privacy.get() : ConfigCore.flag("tweaks.privacy", true); }

    public boolean chatReporting() { return Config.loaded() ? chatReporting.get() : ConfigCore.flag("tweaks.chatReporting", true); }

    public boolean darkSplashOff() { return Config.loaded() ? !darkSplash.get() : !ConfigCore.flag("tweaks.darkSplash", true); }

    public boolean hideRecipeBookButton() { return Config.loaded() ? hideRecipeBookButton.get() : ConfigCore.flag("tweaks.hideRecipeBookButton", false); }
}
