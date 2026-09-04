package mctmods.resourcedatapackloader.content.extra;

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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentFuels {
    private static final String FUELS = "fuels";
    private static final String ORE_DICT = "oreDict";
    private static final String TAG = "tag";
    private static final Gson GSON = new GsonBuilder().create();
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentFuels() {}

    public static boolean load() {
        if (!GENERATION.stale()) { return !ENTRIES.isEmpty(); }
        ENTRIES.clear();
        if (!Config.content.fuels()) { return false; }
        Json.eachFile(PackManager.FUELS, "fuel file", ContentFuels::read);
        if (!ENTRIES.isEmpty()) { Summary.info("fuels", "Loaded " + ENTRIES.size() + " fuel entry/entries"); }
        return !ENTRIES.isEmpty();
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Fuel file {} is empty, ignoring it", key);
            return;
        }
        for (JsonElement element : GsonHelper.getAsJsonArray(json, FUELS)) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A fuel in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            int burnTime = GsonHelper.getAsInt(entry, "burnTime", 0);
            if (burnTime <= 0) {
                ContentLog.LOGGER.error("A fuel in {} has no positive burnTime, skipping it", key);
                continue;
            }
            if (entry.has(ORE_DICT)) { ContentLog.LOGGER.warn("A fuel in {} uses '{}', which this line does not read. Name an item tag under '{}' instead, such as c:ingots/copper or forge:ingots/copper", key, ORE_DICT, TAG); }
            String tag = GsonHelper.getAsString(entry, TAG, "").trim();
            if (!tag.isEmpty()) {
                ResourceLocation name = ResourceLocation.tryParse(tag);
                if (name == null) {
                    ContentLog.LOGGER.error("A fuel in {} names tag '{}', which is not a valid tag id, skipping it", key, tag);
                    continue;
                }
                ENTRIES.add(new Entry(null, TagKey.create(Registries.ITEM, name), burnTime));
                continue;
            }
            Item item = ContentStacks.find(key, GsonHelper.getAsString(entry, "item", ""));
            if (item == null) { continue; }
            ENTRIES.add(new Entry(item, null, burnTime));
        }
    }

    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (!load()) { return; }
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
        @Nullable private final Item item;
        @Nullable private final TagKey<Item> tag;
        private final int burnTime;

        private Entry(@Nullable Item item, @Nullable TagKey<Item> tag, int burnTime) {
            this.item = item;
            this.tag = tag;
            this.burnTime = burnTime;
        }

        private boolean matches(ItemStack fuel) { return tag != null ? fuel.is(tag) : fuel.is(item); }
    }
}
