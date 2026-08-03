package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class MixinGuiCreateWorld {
    @Shadow private String gameMode;
    @Shadow private String worldSeed;
    @Shadow private String worldName;
    @Shadow private boolean hardCoreMode;
    @Shadow private boolean allowCheats;
    @Shadow private boolean allowCheatsWasSetByUser;

    @Inject(method = "initGui", at = @At("HEAD"))
    private void rdpl$showPackChoices(CallbackInfo ci) {
        String named = ContentTerrain.worldName();
        String seed = ContentTerrain.worldSeed();
        String fresh = I18n.format("selectWorld.newWorld");
        ContentLog.LOGGER.debug("The screen for making a world opened. A pack asks for the name '{}', the seed '{}' and the game mode '{}'. The box says '{}' and the game calls a new world '{}', so the name {} be filled in",
                named, seed, ContentTerrain.worldGameMode(), worldName, fresh, worldName.equals(fresh) ? "will" : "will not");
        if (!named.isEmpty() && worldName.equals(fresh)) { worldName = named; }
        if (!seed.isEmpty() && worldSeed.isEmpty()) { worldSeed = seed; }

        GameType asked = ContentTerrain.gameModeFrom(ContentTerrain.worldGameMode());
        if (asked != GameType.SURVIVAL && asked != GameType.CREATIVE) { return; }

        gameMode = asked.getName();
        hardCoreMode = false;
        if (allowCheatsWasSetByUser) { return; }

        allowCheats = asked == GameType.CREATIVE;
    }
}
