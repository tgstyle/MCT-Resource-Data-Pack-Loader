package mctmods.resourcedatapackloader.command;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
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
        map.put("textures", minecraft -> List.of(minecraft.getTextureManager(), minecraft.getPaintingTextures(), minecraft.getMobEffectTextures(), minecraft.getMapDecorationTextures(), minecraft.getGuiSprites(), minecraft.getModelManager(), minecraft.getItemRenderer(), minecraft.getBlockRenderer(), minecraft.particleEngine));
        map.put("models", minecraft -> List.of(minecraft.getModelManager(), minecraft.getItemRenderer(), minecraft.getBlockRenderer()));
        map.put("languages", minecraft -> List.of(minecraft.getLanguageManager()));
        map.put("sounds", minecraft -> List.of(minecraft.getSoundManager()));
        map.put("shaders", minecraft -> List.of(minecraft.gameRenderer.createReloadListener()));
        return Collections.unmodifiableMap(map);
    }

    public static Set<String> names() { return GROUPS.keySet(); }

    public static CompletableFuture<Void> reload(Minecraft minecraft, String group) {
        List<PreparableReloadListener> listeners = GROUPS.get(group).apply(minecraft);
        return SimpleReloadInstance.create(minecraft.getResourceManager(), listeners, Util.backgroundExecutor(), minecraft, CompletableFuture.completedFuture(Unit.INSTANCE), false)
                .done()
                .thenRunAsync(minecraft.levelRenderer::allChanged, minecraft);
    }
}
