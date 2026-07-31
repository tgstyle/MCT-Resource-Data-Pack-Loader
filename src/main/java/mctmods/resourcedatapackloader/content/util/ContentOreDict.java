package mctmods.resourcedatapackloader.content.util;

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
import net.minecraftforge.oredict.OreDictionary;
import java.util.Map;

public final class ContentOreDict {
    private static final Gson GSON = new GsonBuilder().create();
    private static boolean applied;

    private ContentOreDict() {}

    public static void apply() {
        if (applied) { return; }
        applied = true;
        if (!Config.content.oreDictionary) { return; }

        int[] count = new int[1];
        PackManager.get().forEach(PackManager.OREDICT, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { read(key, contents, count); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in ore dictionary file {}, ignoring it", key, ex); }
        });

        if (count[0] > 0) { Summary.info("oredict_extra", "Added " + count[0] + " extra ore dictionary entry/entries"); }
    }

    private static void read(ResourceLocation key, String contents, int[] count) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Ore dictionary file {} is empty, ignoring it", key);
            return;
        }

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String name = entry.getKey();
            if (name.startsWith("_")) { continue; }

            if (!entry.getValue().isJsonArray()) {
                ContentLog.LOGGER.error("Ore dictionary name '{}' in {} is not an array, skipping it", name, key);
                continue;
            }

            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                ItemStack stack = ContentStacks.parse(key, element.getAsString(), 1);
                if (stack.isEmpty()) { continue; }
                OreDictionary.registerOre(name, stack);
                count[0]++;
            }
        }
    }
}
