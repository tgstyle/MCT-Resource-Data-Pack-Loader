package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.mixin.AccessorFurnaceRecipes;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Names;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class FurnaceBlocking {
    private static final Blocked BLOCKED = new Blocked();
    private static final ThreadLocal<Boolean> TRUSTED = new ThreadLocal<>();
    private static final Set<String> SOURCES = new HashSet<>();
    private static final Set<ItemStack> TRUSTED_OUTPUTS = Collections.newSetFromMap(new IdentityHashMap<>());
    @Nullable private static Set<String> whitelist;
    @Nullable private static Set<String> blocked;

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

        String owner = owner(result);
        if (owner == null) { return false; }
        if (blockedMods().contains(owner)) { return count(owner); }
        if (ContentControl.flag(ContentControl.RECIPES, "blockFurnaceRecipes", Config.recipes.blockFurnaceRecipes) && !allowedMods().contains(owner)) { return count(owner); }

        return false;
    }

    private static boolean count(String owner) {
        BLOCKED.count(owner);
        return true;
    }

    private static Set<String> allowedMods() {
        if (whitelist == null) { whitelist = Names.lower(ContentControl.list(ContentControl.RECIPES, "furnaceWhitelist", Config.recipes.furnaceWhitelist)); }
        return whitelist;
    }

    private static Set<String> blockedMods() {
        if (blocked == null) { blocked = Names.lower(ContentControl.list(ContentControl.RECIPES, "blockedFurnaceMods", Config.recipes.blockedFurnaceMods)); }
        return blocked;
    }

    public static boolean disabled() {
        if (ContentControl.off(ContentControl.RECIPES)) { return true; }

        return !ContentControl.flag(ContentControl.RECIPES, "blockFurnaceRecipes", Config.recipes.blockFurnaceRecipes) && ContentControl.list(ContentControl.RECIPES, "blockedFurnaceMods", Config.recipes.blockedFurnaceMods).length == 0;
    }

    public static void apply() {
        if (disabled()) { return; }

        Map<ItemStack, ItemStack> smelting = net.minecraft.item.crafting.FurnaceRecipes.instance().getSmeltingList();
        Map<ItemStack, Float> experience = ((AccessorFurnaceRecipes) net.minecraft.item.crafting.FurnaceRecipes.instance()).rdpl$getExperienceList();
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

    @Nullable private static String owner(ItemStack result) {
        if (result.isEmpty()) { return null; }

        ResourceLocation name = result.getItem().getRegistryName();
        return name == null ? null : name.getNamespace().toLowerCase(Locale.ROOT);
    }

}
