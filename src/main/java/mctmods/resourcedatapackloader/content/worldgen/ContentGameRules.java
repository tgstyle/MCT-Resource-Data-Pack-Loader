package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

public final class ContentGameRules {
    private static final Map<Integer, GameRules> BY_DIMENSION = new HashMap<>();

    private ContentGameRules() {}

    public static void load() {
        BY_DIMENSION.clear();
        Map<Integer, Map<String, String>> wanted = new LinkedHashMap<>();

        PackManager.get().forEach(PackManager.GAMERULES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            for (Map.Entry<Integer, Map<String, String>> entry : ContentParser.gameRuleFile(key, contents).entrySet()) {
                wanted.computeIfAbsent(entry.getKey(), id -> new LinkedHashMap<>()).putAll(entry.getValue());
            }
        });

        for (DimensionDef def : ContentDimensions.all().values()) {
            if (def.gameRules.isEmpty()) { continue; }

            wanted.computeIfAbsent(def.id, id -> new LinkedHashMap<>()).putAll(def.gameRules);
        }

        for (Map.Entry<Integer, Map<String, String>> entry : wanted.entrySet()) {
            GameRules rules = new GameRules();
            for (Map.Entry<String, String> rule : entry.getValue().entrySet()) {
                if (!rules.hasRule(rule.getKey())) {
                    ContentLog.LOGGER.error("Dimension {} sets game rule '{}', which does not exist, ignoring it", entry.getKey(), rule.getKey());
                    continue;
                }
                rules.setOrCreateGameRule(rule.getKey(), rule.getValue());
            }
            BY_DIMENSION.put(entry.getKey(), rules);
        }
        if (!BY_DIMENSION.isEmpty()) { Summary.info("gamerules", "Applying separate game rules in dimension(s) " + BY_DIMENSION.keySet()); }
    }

    @Nullable public static GameRules forWorld(World world) {
        if (BY_DIMENSION.isEmpty() || world == null || world.provider == null) { return null; }

        return BY_DIMENSION.get(world.provider.getDimension());
    }
}
