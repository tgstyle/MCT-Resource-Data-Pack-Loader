package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.compat.ClientCompat;
import mctmods.resourcedatapackloader.compat.LineClientCompat;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.neoforged.neoforge.client.entity.animation.json.AnimationLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class ReloadGroups {
    private static final Map<String, Function<Minecraft, List<PreparableReloadListener>>> GROUPS = groups();

    private ReloadGroups() {}

    private static Map<String, Function<Minecraft, List<PreparableReloadListener>>> groups() {
        Map<String, Function<Minecraft, List<PreparableReloadListener>>> map = new LinkedHashMap<>();
        map.put("textures", minecraft -> {
            List<PreparableReloadListener> listeners = new ArrayList<>(List.of(minecraft.getTextureManager(), minecraft.getAtlasManager()));
            listeners.addAll(LineClientCompat.paletteListeners());
            listeners.addAll(baked(minecraft));
            return listeners;
        });
        map.put("models", minecraft -> {
            List<PreparableReloadListener> listeners = new ArrayList<>(List.of(minecraft.getAtlasManager()));
            listeners.addAll(baked(minecraft));
            return listeners;
        });
        map.put("languages", minecraft -> List.of(minecraft.getLanguageManager()));
        map.put("sounds", minecraft -> List.of(minecraft.getSoundManager()));
        map.put("shaders", minecraft -> {
            List<PreparableReloadListener> listeners = new ArrayList<>(List.of(minecraft.getShaderManager()));
            listeners.addAll(LineClientCompat.postEffectListeners());
            return listeners;
        });
        return Collections.unmodifiableMap(map);
    }

    private static List<PreparableReloadListener> baked(Minecraft minecraft) {
        return List.of(AnimationLoader.INSTANCE, minecraft.getModelManager(), minecraft.particleEngine.resourceManager, ClientCompat.levelListener(minecraft));
    }

    public static Set<String> names() { return GROUPS.keySet(); }

    public static CompletableFuture<Void> reload(Minecraft minecraft, String group) {
        List<PreparableReloadListener> listeners = GROUPS.get(group).apply(minecraft);
        return SimpleReloadInstance.create(minecraft.getResourceManager(), listeners, Util.backgroundExecutor(), minecraft, CompletableFuture.completedFuture(Unit.INSTANCE), false)
                .done()
                .thenRunAsync(() -> ClientCompat.allChanged(minecraft), minecraft);
    }
}
