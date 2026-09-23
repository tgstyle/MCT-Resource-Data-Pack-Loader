package mctmods.resourcedatapackloader.util;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ConfigTweaks {
    private final ModConfigSpec.BooleanValue promptLeafDecay;
    private final ModConfigSpec.BooleanValue lenientPaths;
    private final ModConfigSpec.BooleanValue unbreakableSpawners;
    private final ModConfigSpec.BooleanValue experimentalWarning;
    private final ModConfigSpec.BooleanValue privacy;
    private final ModConfigSpec.BooleanValue darkSplash;

    ConfigTweaks(ModConfigSpec.Builder builder) {
        builder.comment("Small changes to how vanilla behaves").push("tweaks");
        promptLeafDecay = builder.comment("Leaves that lose their tree decay within a second instead of waiting on random ticks [Default=true]").define("promptLeafDecay", true);
        lenientPaths = builder.comment("Paths can be made under a block and stay there when one is placed above [Default=true]").define("lenientPaths", true);
        unbreakableSpawners = builder.comment("Mob spawners cannot be mined or blown up. Creative mode still removes them. Requires a restart [Default=false]").define("unbreakableSpawners", false);
        experimentalWarning = builder.comment("Show the game's experimental settings warning when a world is made or opened. Off answers it as if you had clicked proceed [Default=false]").define("experimentalWarning", false);
        privacy = builder.comment("Turn off the game's telemetry and chat reporting: no telemetry event is sent or logged, the client signs no chat message, the server keeps no chat session and does not require one, so no message anybody sends can be reported. A pack cannot set this. Takes effect on the next world or server joined [Default=true]").define("privacy", true);
        darkSplash = builder.comment("Draw the loading screen dark with the pack loader's logo in place of the game's: the logo is swapped as the screen is made, and the game's own Monochrome Logo option is turned on when it is still off, which takes effect at the next start. Off leaves the option as it is [Default=true]").define("darkSplash", true);
        builder.pop();
    }

    public boolean promptLeafDecay() { return Config.loaded() ? promptLeafDecay.get() : ConfigCore.flag("tweaks.promptLeafDecay", true); }

    public boolean lenientPaths() { return Config.loaded() ? lenientPaths.get() : ConfigCore.flag("tweaks.lenientPaths", true); }

    public boolean unbreakableSpawners() { return Config.loaded() ? unbreakableSpawners.get() : ConfigCore.flag("tweaks.unbreakableSpawners", false); }

    public boolean experimentalWarning() { return Config.loaded() ? experimentalWarning.get() : ConfigCore.flag("tweaks.experimentalWarning", false); }

    public boolean privacy() { return Config.loaded() ? privacy.get() : ConfigCore.flag("tweaks.privacy", true); }

    public boolean darkSplashOff() { return Config.loaded() ? !darkSplash.get() : !ConfigCore.flag("tweaks.darkSplash", true); }
}
