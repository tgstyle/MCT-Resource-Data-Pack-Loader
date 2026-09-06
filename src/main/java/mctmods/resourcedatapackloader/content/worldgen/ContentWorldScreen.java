package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.client.event.ScreenEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentWorldScreen {
    private static final Set<String> WARNED = new HashSet<>();
    private static final Set<WorldCreationUiState> HOOKED = Collections.newSetFromMap(new WeakHashMap<>());

    private ContentWorldScreen() {}

    public static void onScreenInit(ScreenEvent.Init.Pre event) {
        if (!(event.getScreen() instanceof CreateWorldScreen screen)) { return; }
        WorldCreationUiState state = screen.getUiState();
        String named = ContentTerrain.worldName();
        String seed = ContentTerrain.worldSeed();
        String mode = ContentTerrain.worldGameMode();
        if (named.isEmpty() && seed.isEmpty() && mode.isEmpty()) { return; }
        String fresh = I18n.get("selectWorld.newWorld");
        ContentLog.LOGGER.debug("The screen for making a world opened. A pack asks for the name '{}', the seed '{}' and the game mode '{}'. The box says '{}' and the game calls a new world '{}', so the name {} be filled in",
                named, seed, mode, state.getName(), fresh, state.getName().equals(fresh) ? "will" : "will not");
        if (!named.isEmpty() && state.getName().equals(fresh)) { state.setName(named); }
        WorldCreationUiState.SelectedGameMode asked = selected(mode);
        hold(state, seed, asked);
        if ((seed.isEmpty() && asked == null) || !HOOKED.add(state)) { return; }
        state.addListener(changed -> hold(changed, seed, asked));
    }

    private static void hold(WorldCreationUiState state, String seed, @Nullable WorldCreationUiState.SelectedGameMode asked) {
        if (state.isDebug()) { return; }
        if (!seed.isEmpty() && !seed.equals(state.getSeed())) { state.setSeed(seed); }
        if (asked != null && state.getGameMode() != asked) { state.setGameMode(asked); }
    }

    @Nullable private static WorldCreationUiState.SelectedGameMode selected(String mode) {
        String lowered = mode.trim().toLowerCase(Locale.ROOT);
        switch (lowered) {
            case "": return null;
            case "survival": return WorldCreationUiState.SelectedGameMode.SURVIVAL;
            case ContentTerrain.HARDCORE: return WorldCreationUiState.SelectedGameMode.HARDCORE;
            case "creative": return WorldCreationUiState.SelectedGameMode.CREATIVE;
            case "adventure", "spectator": {
                if (WARNED.add(lowered)) { ContentLog.LOGGER.info("A pack asks for the game mode '{}', which the world screen does not offer and this line does not apply at creation yet, so the mode is left as chosen", lowered); }
                return null;
            }
            default: {
                if (WARNED.add(lowered)) { ContentLog.LOGGER.error("A pack asks for the game mode '{}', which is not one of survival, hardcore, creative, adventure or spectator, so the mode is left as chosen", mode); }
                return null;
            }
        }
    }
}
