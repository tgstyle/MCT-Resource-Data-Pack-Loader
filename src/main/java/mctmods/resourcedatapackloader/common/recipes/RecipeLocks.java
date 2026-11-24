package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.entity.Advancements;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class RecipeLocks {
    private static final List<String> LOCK_KEYS = List.of("advancement", "team", "objective", "scoreAtLeast", "dietAtLeast", "dietCount", "names", "namespaces", "outputs", "regex", "inputs", "types");
    private static final List<Lock> LOCKS = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private RecipeLocks() {}

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        LOCKS.clear();
        Json.eachObject(PackManager.RECIPE_LOCKS, "recipe lock file", RecipeLocks::read);
        if (!LOCKS.isEmpty()) { Summary.info("recipes.locks", "Loaded " + LOCKS.size() + " recipe lock(s)"); }
    }

    public static boolean barred(ServerPlayer player, Identifier id, ItemStack result) {
        for (Lock lock : LOCKS) {
            if ((lock.selector.matchesName(id) || lock.selector.matchesOutput(result)) && lock.bars(player)) { return true; }
        }
        return false;
    }

    private static void read(Identifier key, JsonObject json) {
        Json.unknownKeys(json, "the top level", LOCK_KEYS);
        String advancement = Json.getString(json, "advancement", "").trim();
        Standing standing = Standing.parse(json);
        if (standing.halfScore()) {
            ContentLog.LOGGER.error("Recipe lock file {} sets only one of objective and scoreAtLeast, so it leaves the score condition out", key);
            standing = standing.withoutScore();
        }
        if (advancement.isEmpty() && !standing.any()) {
            ContentLog.LOGGER.error("Recipe lock file {} names no advancement or standing, ignoring it", key);
            return;
        }
        RecipeSelector selector = RecipeSelector.scopeOf("Recipe lock", key, json);
        if (selector != null) { LOCKS.add(new Lock(selector, advancement, standing)); }
    }

    private record Lock(RecipeSelector selector, String advancement, Standing standing) {
        boolean bars(ServerPlayer player) { return !advancement.isEmpty() && !Advancements.has(player, advancement) || !standing.met(player); }
    }
}
