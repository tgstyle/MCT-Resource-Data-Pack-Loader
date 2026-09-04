package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.util.Stacks;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentFuels {
    private static final Gson GSON = new GsonBuilder().create();
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
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Fuel file {} is empty, ignoring it", key);
            return;
        }
        for (JsonElement element : JsonUtils.getJsonArray(json, "fuels")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A fuel in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            int burnTime = JsonUtils.getInt(entry, "burnTime", 0);
            if (burnTime <= 0) {
                ContentLog.LOGGER.error("A fuel in {} has no positive burnTime, skipping it", key);
                continue;
            }
            String oreDict = JsonUtils.getString(entry, "oreDict", "");
            if (!oreDict.isEmpty()) {
                ENTRIES.add(new Entry(ItemStack.EMPTY, oreDict, burnTime));
                continue;
            }
            ItemStack stack = ContentStacks.parse(key, JsonUtils.getString(entry, "item", ""), 1);
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
