package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public final class RecipeLocks {
    private static final List<Lock> LOCKS = new ArrayList<>();

    private RecipeLocks() {}

    public static void reload() {
        LOCKS.clear();
        Json.eachObject(PackManager.RECIPE_LOCKS, "recipe lock file", RecipeLocks::read);
        if (!LOCKS.isEmpty()) { Summary.info("recipes.locks", "Loaded " + LOCKS.size() + " recipe lock(s)"); }
    }

    public static boolean barred(EntityPlayer player, ResourceLocation id, ItemStack result) {
        for (Lock lock : LOCKS) {
            if (lock.selector.matches(id, result) && lock.bars(player)) { return true; }
        }
        return false;
    }

    private static void read(ResourceLocation key, JsonObject json) {
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
        RecipeSelector selector = new RecipeSelector();
        selector.read(key, json);
        if (selector.isEmpty()) {
            ContentLog.LOGGER.error("Recipe lock file {} selects no recipes, ignoring it", key);
            return;
        }
        LOCKS.add(new Lock(selector, advancement, standing));
    }

    private static final class Lock {
        final RecipeSelector selector;
        final String advancement;
        final Standing standing;

        Lock(RecipeSelector selector, String advancement, Standing standing) {
            this.selector = selector;
            this.advancement = advancement;
            this.standing = standing;
        }

        boolean bars(EntityPlayer player) { return !advancement.isEmpty() && !Advancements.has(player, advancement) || !standing.met(player); }
    }
}
