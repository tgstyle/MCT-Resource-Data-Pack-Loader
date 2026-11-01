package mctmods.resourcedatapackloader.common.fuels;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public final class ContentFuels {
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentFuels() {}

    public static boolean load() {
        if (!GENERATION.stale()) { return !ENTRIES.isEmpty(); }
        ENTRIES.clear();
        if (!Config.content.fuels) { return false; }
        Json.eachFile(PackManager.FUELS, "fuel file", ContentFuels::read);
        if (!ENTRIES.isEmpty()) { Summary.info("fuels", "Loaded " + ENTRIES.size() + " fuel entry/entries"); }
        return !ENTRIES.isEmpty();
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "fuel file");
        if (json == null) { return; }
        for (JsonElement element : Json.getJsonArray(json, "fuels")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A fuel in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            int burnTime = Json.getInt(entry, "burnTime", 0);
            if (burnTime <= 0) {
                ContentLog.LOGGER.error("A fuel in {} has no positive burnTime, skipping it", key);
                continue;
            }
            String oreDict = Json.getString(entry, "oreDict", "");
            if (!oreDict.isEmpty()) {
                ENTRIES.add(new Entry(ItemStack.EMPTY, oreDict, burnTime));
                continue;
            }
            ItemStack stack = ContentStacks.parse(key, Json.getString(entry, "item", ""), 1);
            if (stack.isEmpty()) { continue; }
            ENTRIES.add(new Entry(stack, "", burnTime));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();
        if (fuel.isEmpty()) { return; }
        for (Entry entry : ENTRIES) {
            if (entry.matches(fuel)) {
                event.setBurnTime(entry.burnTime);
                return;
            }
        }
    }

    private static final class Entry {
        private final ItemStack stack;
        @Nullable private final NonNullList<ItemStack> ores;
        private final int burnTime;

        private Entry(ItemStack stack, String oreDict, int burnTime) {
            this.stack = stack;
            this.ores = oreDict.isEmpty() ? null : OreDictionary.getOres(oreDict);
            this.burnTime = burnTime;
        }

        private boolean matches(ItemStack fuel) {
            if (ores != null) { return OreDictionary.containsMatch(false, ores, fuel); }
            return Stacks.matches(stack, fuel);
        }
    }
}
