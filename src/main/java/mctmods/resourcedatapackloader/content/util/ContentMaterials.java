package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registries;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentMaterials {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, MaterialDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentMaterials() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.registersToClients()) { return; }
        Json.eachFile(PackManager.MATERIALS, "material file", ContentMaterials::read);
        if (!DEFS.isEmpty()) { Summary.info("materials", "Loaded " + DEFS.size() + " material definition(s)"); }
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Material file {} is empty, ignoring it", key);
            return;
        }
        int[] reduction = {2, 5, 6, 2};
        if (json.has("reduction")) {
            JsonArray array = JsonUtils.getJsonArray(json, "reduction");
            if (array.size() != 4) { ContentLog.LOGGER.error("Reduction in {} needs four numbers, boots leggings chestplate helmet, using the defaults", key); }
            else {
                for (int i = 0; i < 4; i++) { reduction[i] = array.get(i).getAsInt(); }
            }
        }
        String name = key.getNamespace() + "_" + key.getPath().replace('/', '_');
        DEFS.put(key.toString(), new MaterialDef(key, name,
                JsonUtils.getInt(json, "harvestLevel", 1),
                JsonUtils.getInt(json, "durability", 250),
                JsonUtils.getFloat(json, "efficiency", 6.0F),
                JsonUtils.getFloat(json, "damage", 2.0F),
                JsonUtils.getInt(json, "enchantability", 14),
                reduction,
                JsonUtils.getFloat(json, "toughness", 0.0F),
                JsonUtils.getString(json, "equipSound", "item.armor.equip_iron"),
                JsonUtils.getString(json, "armorTexture", key.toString()),
                JsonUtils.getString(json, "repairItem", ""),
                Json.strings(json, "requires")));
    }

    public static void register() {
        load();
        for (MaterialDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires, def.registryName)) { continue; }
            Item.ToolMaterial tool = EnumHelper.addToolMaterial(def.name, def.harvestLevel, def.durability, def.efficiency, def.damage, def.enchantability);
            ItemArmor.ArmorMaterial armor = EnumHelper.addArmorMaterial(def.name, def.armorTexture, def.durability / 10, def.reduction, def.enchantability, sound(def.equipSound), def.toughness);
            def.resolve(tool, armor, net.minecraft.item.ItemStack.EMPTY);
        }
    }

    public static void resolveRepairItems() {
        for (MaterialDef def : DEFS.values()) {
            if (def.repairItem.isEmpty() || def.getTool() == null) { continue; }
            def.resolve(def.getTool(), def.getArmor(), ContentStacks.parse(def.registryName, def.repairItem, 1));
            def.getTool().setRepairItem(def.getRepair());
        }
    }

    @Nullable public static MaterialDef find(String name, Object context) {
        if (name == null || name.isEmpty()) { return null; }
        MaterialDef def = DEFS.get(name);
        if (def != null) { return def; }
        ContentLog.LOGGER.error("Unknown material '{}' in {}, the item is skipped. Known materials are {}", name, context, DEFS.keySet());
        return null;
    }

    @Nullable private static SoundEvent sound(String name) {
        ResourceLocation location = new ResourceLocation(name);
        return Registries.find(ForgeRegistries.SOUND_EVENTS, location);
    }
}
