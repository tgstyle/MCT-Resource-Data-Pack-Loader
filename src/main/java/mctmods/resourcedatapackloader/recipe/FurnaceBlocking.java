package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IFurnaceRecipes;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Stacks;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.item.ItemStack;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class FurnaceBlocking {
    private static final Blocked BLOCKED = new Blocked();
    private static final ThreadLocal<Boolean> TRUSTED = new ThreadLocal<>();
    private static final Set<String> SOURCES = new HashSet<>();
    private static final Set<ItemStack> TRUSTED_OUTPUTS = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final TemplateMemo<Set<String>> WHITELIST = new TemplateMemo<>();
    private static final TemplateMemo<Set<String>> BLOCKED_MODS = new TemplateMemo<>();

    private FurnaceBlocking() {}

    public static void beginTrusted(String source) {
        if (SOURCES.add(source)) { ContentLog.LOGGER.info("Furnace recipes added by {} are trusted and will not be blocked", source); }
        TRUSTED.set(Boolean.TRUE);
    }

    public static void endTrusted() { TRUSTED.remove(); }

    public static boolean rejects(ItemStack result) {
        if (disabled()) { return false; }
        if (Boolean.TRUE.equals(TRUSTED.get())) {
            TRUSTED_OUTPUTS.add(result);
            return false;
        }
        String owner = Stacks.namespace(result);
        if (owner == null) { return false; }
        if (blockedMods().contains(owner)) { return count(owner); }
        if (ContentControl.flag(ContentControl.RECIPES, "blockFurnaceRecipes", Config.recipes.blockFurnaceRecipes) && !allowedMods().contains(owner)) { return count(owner); }
        return false;
    }

    private static boolean count(String owner) {
        BLOCKED.count(owner);
        return true;
    }

    private static Set<String> allowedMods() { return WHITELIST.get(() -> Settings.lower(ContentControl.list(ContentControl.RECIPES, "furnaceWhitelist", Config.recipes.furnaceWhitelist))); }

    private static Set<String> blockedMods() { return BLOCKED_MODS.get(() -> Settings.lower(ContentControl.list(ContentControl.RECIPES, "blockedFurnaceMods", Config.recipes.blockedFurnaceMods))); }

    public static boolean disabled() {
        if (ContentControl.off(ContentControl.RECIPES)) { return true; }
        return !ContentControl.flag(ContentControl.RECIPES, "blockFurnaceRecipes", Config.recipes.blockFurnaceRecipes) && ContentControl.list(ContentControl.RECIPES, "blockedFurnaceMods", Config.recipes.blockedFurnaceMods).length == 0;
    }

    public static void apply() {
        if (disabled()) { return; }
        Map<ItemStack, ItemStack> smelting = net.minecraft.item.crafting.FurnaceRecipes.instance().getSmeltingList();
        Map<ItemStack, Float> experience = ((IFurnaceRecipes) net.minecraft.item.crafting.FurnaceRecipes.instance()).rdpl$getExperienceList();
        Iterator<Map.Entry<ItemStack, ItemStack>> iterator = smelting.entrySet().iterator();
        int removed = 0;
        while (iterator.hasNext()) {
            Map.Entry<ItemStack, ItemStack> entry = iterator.next();
            if (TRUSTED_OUTPUTS.contains(entry.getValue())) { continue; }
            if (!rejects(entry.getValue())) { continue; }
            experience.remove(entry.getValue());
            iterator.remove();
            removed++;
        }
        int total = BLOCKED.total();
        if (total == 0) { return; }
        Summary.info("furnace.blocked", "Blocked " + total + " furnace recipe(s), " + removed + " of them already registered before the block list could be read");
        if (ContentControl.flag(ContentControl.RECIPES, "logBlockedRecipes", Config.recipes.logBlockedRecipes)) { BLOCKED.report("furnace recipe(s)"); }
    }
}
