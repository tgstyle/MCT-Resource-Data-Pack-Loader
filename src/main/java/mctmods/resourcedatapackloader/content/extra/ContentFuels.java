package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import java.util.ArrayList;
import java.util.List;

public final class ContentFuels {
    private static final Gson GSON = new GsonBuilder().create();
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static boolean loaded;

    private ContentFuels() {}

    public static boolean load() {
        if (loaded) { return !ENTRIES.isEmpty(); }
        loaded = true;
        if (!Config.content.fuels) { return false; }

        PackManager.get().forEach(PackManager.FUELS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { read(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in fuel file {}, ignoring it", key, ex); }
        });

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
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
        private final String oreDict;
        private final int burnTime;

        private Entry(ItemStack stack, String oreDict, int burnTime) {
            this.stack = stack;
            this.oreDict = oreDict;
            this.burnTime = burnTime;
        }

        private boolean matches(ItemStack fuel) {
            if (!oreDict.isEmpty()) {
                for (int id : OreDictionary.getOreIDs(fuel)) {
                    if (oreDict.equals(OreDictionary.getOreName(id))) { return true; }
                }
                return false;
            }

            if (stack.getItem() != fuel.getItem()) { return false; }
            return stack.getMetadata() == OreDictionary.WILDCARD_VALUE || stack.getMetadata() == fuel.getMetadata();
        }
    }
}
