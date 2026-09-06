package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
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
        WorldCreationUiState.WorldTypeEntry preset = presetEntry(state);
        if (named.isEmpty() && seed.isEmpty() && mode.isEmpty() && preset == null) { return; }
        String fresh = I18n.get("selectWorld.newWorld");
        ContentLog.LOGGER.debug("The screen for making a world opened. A pack asks for the name '{}', the seed '{}' and the game mode '{}'. The box says '{}' and the game calls a new world '{}', so the name {} be filled in",
                named, seed, mode, state.getName(), fresh, state.getName().equals(fresh) ? "will" : "will not");
        if (!named.isEmpty() && state.getName().equals(fresh)) { state.setName(named); }
        WorldCreationUiState.SelectedGameMode asked = selected(mode);
        hold(state, seed, asked, preset);
        if ((seed.isEmpty() && asked == null && preset == null) || !HOOKED.add(state)) { return; }
        state.addListener(changed -> hold(changed, seed, asked, preset));
    }

    private static void hold(WorldCreationUiState state, String seed, @Nullable WorldCreationUiState.SelectedGameMode asked, @Nullable WorldCreationUiState.WorldTypeEntry preset) {
        if (state.isDebug()) { return; }
        if (!seed.isEmpty() && !seed.equals(state.getSeed())) { state.setSeed(seed); }
        if (asked != null && state.getGameMode() != asked) { state.setGameMode(asked); }
        if (preset != null && state.getWorldType() != preset && !kept(state.getWorldType())) { state.setWorldType(preset); }
    }

    @Nullable private static WorldCreationUiState.WorldTypeEntry presetEntry(WorldCreationUiState state) {
        ResourceLocation wanted = ContentWorldShape.presetId();
        if (wanted == null) { return null; }
        for (WorldCreationUiState.WorldTypeEntry entry : state.getNormalPresetList()) {
            if (entry.preset() != null && entry.preset().unwrapKey().map(key -> key.location().equals(wanted)).orElse(false)) { return entry; }
        }
        if (WARNED.add(wanted.toString())) { ContentLog.LOGGER.error("The generated world preset {} is not in the world screen's list, so the world type is left as chosen", wanted); }
        return null;
    }

    private static boolean kept(WorldCreationUiState.WorldTypeEntry current) {
        ResourceLocation chosen = current.preset() == null ? null : current.preset().unwrapKey().map(ResourceKey::location).orElse(null);
        if (chosen == null) { return true; }
        for (String exception : ContentTerrain.worldTypeExceptions()) {
            String named = exception.trim();
            if (named.equalsIgnoreCase(chosen.toString()) || named.equalsIgnoreCase(chosen.getPath())) { return true; }
        }
        return false;
    }

    @Nullable private static WorldCreationUiState.SelectedGameMode selected(String mode) {
        String lowered = mode.trim().toLowerCase(Locale.ROOT);
        return switch (lowered) {
            case "" -> null;
            case "survival" -> WorldCreationUiState.SelectedGameMode.SURVIVAL;
            case ContentTerrain.HARDCORE -> WorldCreationUiState.SelectedGameMode.HARDCORE;
            case "creative" -> WorldCreationUiState.SelectedGameMode.CREATIVE;
            case "adventure", "spectator" -> {
                if (WARNED.add(lowered)) {
                    ContentLog.LOGGER.info("A pack asks for the game mode '{}', which the world screen does not offer and this line does not apply at creation yet, so the mode is left as chosen", lowered);
                }
                yield null;
            }
            default -> {
                if (WARNED.add(lowered)) {
                    ContentLog.LOGGER.error("A pack asks for the game mode '{}', which is not one of survival, hardcore, creative, adventure or spectator, so the mode is left as chosen", mode);
                }
                yield null;
            }
        };
    }
}
