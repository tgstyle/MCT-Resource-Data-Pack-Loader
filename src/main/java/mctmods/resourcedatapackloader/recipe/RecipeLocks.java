package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
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
            if (lock.selector.matches(id, result) && !Advancements.has(player, lock.advancement)) { return true; }
        }
        return false;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        String advancement = Json.getString(json, "advancement", "").trim();
        if (advancement.isEmpty()) {
            ContentLog.LOGGER.error("Recipe lock file {} names no advancement, ignoring it", key);
            return;
        }
        RecipeSelector selector = new RecipeSelector();
        selector.read(key, json);
        if (selector.isEmpty()) {
            ContentLog.LOGGER.error("Recipe lock file {} selects no recipes, ignoring it", key);
            return;
        }
        LOCKS.add(new Lock(selector, advancement));
    }

    private static final class Lock {
        final RecipeSelector selector;
        final String advancement;

        Lock(RecipeSelector selector, String advancement) {
            this.selector = selector;
            this.advancement = advancement;
        }
    }
}
