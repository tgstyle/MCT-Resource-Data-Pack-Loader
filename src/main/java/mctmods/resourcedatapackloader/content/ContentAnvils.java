package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.AnvilDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentAnvils {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String TAG = "rdplAnvil";
    private static final int TOLD_EVERY = 40;
    private static final List<AnvilDef> DEFS = new ArrayList<>();
    private static final Map<AnvilDef, ItemStack> ITEMS = new IdentityHashMap<>();
    private static final Map<AnvilDef, ItemStack> WITH = new IdentityHashMap<>();
    private static final Map<AnvilDef, ItemStack> RESULTS = new IdentityHashMap<>();
    private static final Map<AnvilDef, Map<Enchantment, Integer>> ENCHANTMENTS = new IdentityHashMap<>();
    private static final Map<String, Long> TOLD = new HashMap<>();

    private ContentAnvils() {}

    public static boolean load() {
        DEFS.clear();
        ITEMS.clear();
        WITH.clear();
        RESULTS.clear();
        ENCHANTMENTS.clear();
        if (!Config.data.anvils) { return false; }
        Json.eachFile(PackManager.ANVILS, "anvil file", (key, contents) -> {
            AnvilDef def = read(key, contents);
            if (def == null) { return; }
            ItemStack stack = ContentStacks.parse(key, def.item, 1);
            ItemStack with = ContentStacks.parse(key, def.with, 1);
            if (stack.isEmpty() || with.isEmpty()) { return; }
            if (!def.result.isEmpty()) {
                ItemStack result = ContentStacks.parse(key, def.result, 1);
                if (result.isEmpty()) { return; }
                RESULTS.put(def, result);
            }
            Map<Enchantment, Integer> found = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : def.enchantments.entrySet()) {
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(entry.getKey()));
                if (enchantment == null) {
                    ContentLog.LOGGER.error("Anvil work {} names the enchantment {}, which nothing registers, skipping it", key, entry.getKey());
                    continue;
                }
                found.put(enchantment, entry.getValue());
            }
            DEFS.add(def);
            ITEMS.put(def, stack);
            WITH.put(def, with);
            ENCHANTMENTS.put(def, found);
        });
        if (!DEFS.isEmpty()) { Summary.info("anvils", "Loaded " + DEFS.size() + " piece(s) of anvil work"); }
        return !DEFS.isEmpty();
    }

    @Nullable private static AnvilDef read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Anvil work {} is empty, ignoring it", key);
            return null;
        }
        String item = JsonUtils.getString(json, "item", "").trim();
        if (item.isEmpty()) {
            ContentLog.LOGGER.error("Anvil work {} names no item, ignoring it", key);
            return null;
        }
        String with;
        int withCount = 1;
        if (json.has("with") && json.get("with").isJsonObject()) {
            JsonObject pair = JsonUtils.getJsonObject(json, "with");
            with = JsonUtils.getString(pair, "item", "").trim();
            withCount = Math.max(1, JsonUtils.getInt(pair, "count", 1));
        }
        else { with = JsonUtils.getString(json, "with", "").trim(); }
        if (with.isEmpty()) {
            ContentLog.LOGGER.error("Anvil work {} names nothing for the right slot under with, and an anvil only answers to a pair, ignoring it", key);
            return null;
        }
        Map<String, Integer> enchantments = new LinkedHashMap<>();
        if (json.has("enchantments")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "enchantments").entrySet()) {
                if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Anvil work {} gives the enchantment {} something that is not a level, skipping it", key, entry.getKey());
                    continue;
                }
                enchantments.put(entry.getKey(), Math.max(1, entry.getValue().getAsInt()));
            }
        }
        return new AnvilDef(key, item, with, withCount, JsonUtils.getString(json, "result", "").trim(), Math.max(1, JsonUtils.getInt(json, "levels", 1)), enchantments, JsonUtils.getString(json, "grants", "").trim(), JsonUtils.getBoolean(json, "locks", false));
    }

    @Nullable private static AnvilDef holding(ItemStack held) {
        if (held.isEmpty()) { return null; }
        for (AnvilDef def : DEFS) {
            ItemStack wanted = ITEMS.get(def);
            if (ContentStacks.matches(held, wanted.getItem(), wanted.getMetadata())) { return def; }
        }
        return null;
    }

    @Nullable private static AnvilDef named(String name) {
        for (AnvilDef def : DEFS) {
            if (def.registryName.toString().equals(name)) { return def; }
        }
        return null;
    }

    @SubscribeEvent public static void onAnvil(AnvilUpdateEvent event) {
        if (DEFS.isEmpty() || event.getRight().isEmpty()) { return; }
        AnvilDef def = holding(event.getLeft());
        if (def == null) { return; }
        ItemStack with = WITH.get(def);
        if (!ContentStacks.matches(event.getRight(), with.getItem(), with.getMetadata()) || event.getRight().getCount() < def.withCount) { return; }
        ItemStack output = event.getLeft().copy();
        ItemStack result = RESULTS.get(def);
        if (result != null) {
            output = result.copy();
            NBTTagCompound carried = event.getLeft().getTagCompound();
            if (carried != null) { output.setTagCompound(carried.copy()); }
        }
        Map<Enchantment, Integer> held = EnchantmentHelper.getEnchantments(output);
        boolean rises = false;
        for (Map.Entry<Enchantment, Integer> entry : ENCHANTMENTS.get(def).entrySet()) {
            Integer already = held.get(entry.getKey());
            if (already != null && already >= entry.getValue()) { continue; }
            held.put(entry.getKey(), entry.getValue());
            rises = true;
        }
        if (!rises && result == null && (!ENCHANTMENTS.get(def).isEmpty() || def.grants.isEmpty())) { return; }
        if (rises) { EnchantmentHelper.setEnchantments(held, output); }
        NBTTagCompound tag = output.getTagCompound();
        if (tag == null) { tag = new NBTTagCompound(); }
        tag.setString(TAG, def.registryName.toString());
        output.setTagCompound(tag);
        event.setOutput(output);
        event.setCost(def.levels);
        event.setMaterialCost(def.withCount);
    }

    @SubscribeEvent public static void onTaken(AnvilRepairEvent event) {
        ItemStack result = event.getItemResult();
        NBTTagCompound tag = result.getTagCompound();
        if (tag == null || !tag.hasKey(TAG, 8)) { return; }
        AnvilDef def = named(tag.getString(TAG));
        tag.removeTag(TAG);
        if (tag.isEmpty()) { result.setTagCompound(null); }
        if (def == null || def.grants.isEmpty() || !(event.getEntityPlayer() instanceof EntityPlayerMP)) { return; }
        grant((EntityPlayerMP) event.getEntityPlayer(), def);
    }

    private static void grant(EntityPlayerMP player, AnvilDef def) {
        MinecraftServer server = player.getServer();
        if (server == null) { return; }
        Advancement advancement = server.getAdvancementManager().getAdvancement(new ResourceLocation(def.grants));
        if (advancement == null) {
            ContentLog.LOGGER.error("Anvil work {} grants the advancement {}, which no pack provides, so nothing is earned", def.registryName, def.grants);
            return;
        }
        PlayerAdvancements progress = player.getAdvancements();
        for (String criterion : advancement.getCriteria().keySet()) { progress.grantCriterion(advancement, criterion); }
    }

    @SubscribeEvent public static void onHeld(LivingEquipmentChangeEvent event) {
        if (DEFS.isEmpty() || event.getSlot() != EntityEquipmentSlot.MAINHAND || !(event.getEntityLiving() instanceof EntityPlayerMP)) { return; }
        AnvilDef def = holding(event.getTo());
        if (def == null || !def.locks || def.grants.isEmpty() || Advancements.has((EntityPlayer) event.getEntityLiving(), def.grants)) { return; }
        tell((EntityPlayerMP) event.getEntityLiving(), def);
    }

    @SubscribeEvent public static void onAttack(AttackEntityEvent event) {
        if (locked(event.getEntityPlayer(), event.getEntityPlayer().getHeldItemMainhand())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onUse(PlayerInteractEvent.RightClickItem event) {
        if (locked(event.getEntityPlayer(), event.getItemStack())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onUseOn(PlayerInteractEvent.RightClickBlock event) {
        if (locked(event.getEntityPlayer(), event.getItemStack())) { event.setUseItem(Event.Result.DENY); }
    }

    @SubscribeEvent public static void onDig(PlayerInteractEvent.LeftClickBlock event) {
        if (locked(event.getEntityPlayer(), event.getItemStack())) { event.setCanceled(true); }
    }

    private static boolean locked(EntityPlayer player, ItemStack held) {
        if (DEFS.isEmpty()) { return false; }
        AnvilDef def = holding(held);
        if (def == null || !def.locks || def.grants.isEmpty() || Advancements.has(player, def.grants)) { return false; }
        if (player instanceof EntityPlayerMP) { tell((EntityPlayerMP) player, def); }
        return true;
    }

    private static void tell(EntityPlayerMP player, AnvilDef def) {
        long now = player.world.getTotalWorldTime();
        Long last = TOLD.get(player.getName());
        if (last != null && now - last < TOLD_EVERY) { return; }
        TOLD.put(player.getName(), now);
        Says.tell(player, "That waits on " + title(player, def.grants), TextFormatting.RED);
    }

    private static String title(EntityPlayerMP player, String name) {
        MinecraftServer server = player.getServer();
        Advancement advancement = server == null ? null : server.getAdvancementManager().getAdvancement(new ResourceLocation(name));
        return advancement == null || advancement.getDisplay() == null ? name : advancement.getDisplay().getTitle().getUnformattedText();
    }
}
