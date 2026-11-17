package mctmods.resourcedatapackloader.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigTweaks {
    private final ForgeConfigSpec.BooleanValue promptLeafDecay;
    private final ForgeConfigSpec.BooleanValue lenientPaths;
    private final ForgeConfigSpec.BooleanValue unbreakableSpawners;
    private final ForgeConfigSpec.BooleanValue experimentalWarning;
    private final ForgeConfigSpec.BooleanValue privacy;
    private final ForgeConfigSpec.BooleanValue chatReporting;
    private final ForgeConfigSpec.BooleanValue darkSplash;
    private final ForgeConfigSpec.BooleanValue hideRecipeBookButton;

    ConfigTweaks(ForgeConfigSpec.Builder builder) {
        builder.comment("Small changes to how vanilla behaves").push("tweaks");
        promptLeafDecay = builder.comment("True, leaves that lose their tree decay within a second instead of waiting on random ticks; false, they wait on random ticks [Default=true]").define("promptLeafDecay", true);
        lenientPaths = builder.comment("True, paths can be made under a block and stay there when one is placed above; false, they do not stay there [Default=true]").define("lenientPaths", true);
        unbreakableSpawners = builder.comment("True, mob spawners cannot be mined or blown up; false, they can be. Creative mode still removes them. Requires a restart [Default=false]").define("unbreakableSpawners", false);
        experimentalWarning = builder.comment("True shows the game's experimental settings warning when a world is made or opened; false answers it as if you had clicked proceed [Default=false]").define("experimentalWarning", false);
        privacy = builder.comment("True turns off the game's telemetry: no telemetry event is sent or logged. False leaves telemetry as vanilla has it. Chat reporting is its own option, chatReporting. Takes effect on the next world or server joined [Default=true]").define("privacy", true);
        chatReporting = builder.comment("Warning! Disabling chat reporting is claimed by Mojang to violate Minecraft usage guidelines, do so at your own risk. True keeps the game's chat signing and reporting as vanilla has them. False turns them off: the client signs no chat message, the server keeps no chat session and does not require one, so no message anybody sends can be reported. The warning toast about a server that does not enforce secure chat is hidden by chunks.hiddenToasts, not here. Takes effect on the next world or server joined [Default=true]").define("chatReporting", true);
        darkSplash = builder.comment("True draws the loading screen dark with the pack loader's logo in place of the game's: the logo is swapped as the screen is made, and the game's own Monochrome Logo option is turned on when it is still off, which takes effect at the next start. False leaves the loading screen as the game draws it, and the option as it is [Default=true]").define("darkSplash", true);
        hideRecipeBookButton = builder.comment("True removes the Recipe Book button from the inventory and crafting table screens, and keeps the recipe book panel closed there; false leaves both as they are [Default=false]").define("hideRecipeBookButton", false);
        builder.pop();
    }

    public boolean promptLeafDecay() { return Config.loaded() ? promptLeafDecay.get() : ConfigCore.flag("tweaks.promptLeafDecay", true); }

    public boolean lenientPaths() { return Config.loaded() ? lenientPaths.get() : ConfigCore.flag("tweaks.lenientPaths", true); }

    public boolean unbreakableSpawners() { return Config.loaded() ? unbreakableSpawners.get() : ConfigCore.flag("tweaks.unbreakableSpawners", false); }

    public boolean experimentalWarning() { return Config.loaded() ? experimentalWarning.get() : ConfigCore.flag("tweaks.experimentalWarning", false); }

    public boolean privacy() { return Config.loaded() ? privacy.get() : ConfigCore.flag("tweaks.privacy", true); }

    public boolean chatReporting() { return Config.loaded() ? chatReporting.get() : ConfigCore.flag("tweaks.chatReporting", true); }

    public boolean hideRecipeBookButton() { return Config.loaded() ? hideRecipeBookButton.get() : ConfigCore.flag("tweaks.hideRecipeBookButton", false); }

    public boolean darkSplashOff() { return Config.loaded() ? !darkSplash.get() : !ConfigCore.flag("tweaks.darkSplash", true); }
}
