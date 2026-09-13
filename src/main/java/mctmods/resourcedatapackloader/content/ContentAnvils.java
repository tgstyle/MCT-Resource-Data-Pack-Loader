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
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentAnvils {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ItemStack, AnvilDef> OFFERED = Collections.synchronizedMap(new WeakHashMap<>());
    private static final int TOLD_EVERY = 40;
    private static final List<AnvilDef> DEFS = new ArrayList<>();
    private static final Map<AnvilDef, ItemStack> ITEMS = new IdentityHashMap<>();
    private static final Map<AnvilDef, ItemStack> WITH = new IdentityHashMap<>();
    private static final Map<AnvilDef, ItemStack> RESULTS = new IdentityHashMap<>();
    private static final Set<String> UNKNOWN = new HashSet<>();
    private static final Map<String, Long> TOLD = new HashMap<>();

    private ContentAnvils() {}

    public static boolean load() {
        DEFS.clear();
        ITEMS.clear();
        WITH.clear();
        RESULTS.clear();
        UNKNOWN.clear();
        if (Config.data.anvilsOff()) { return false; }
        Json.eachFile(PackManager.ANVILS, "anvil file", (key, contents) -> {
            AnvilDef def = read(key, contents);
            if (def == null) { return; }
            ItemStack stack = ContentStacks.parse(key, def.item(), 1);
            ItemStack with = ContentStacks.parse(key, def.with(), 1);
            if (stack.isEmpty() || with.isEmpty()) { return; }
            if (!def.result().isEmpty()) {
                ItemStack result = ContentStacks.parse(key, def.result(), def.resultCount());
                if (result.isEmpty()) { return; }
                RESULTS.put(def, result);
            }
            DEFS.add(def);
            ITEMS.put(def, stack);
            WITH.put(def, with);
        });
        if (!DEFS.isEmpty()) { Summary.info("anvils", "Loaded " + DEFS.size() + " piece(s) of anvil work"); }
        return !DEFS.isEmpty();
    }

    @Nullable private static AnvilDef read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Anvil work {} is empty, ignoring it", key);
            return null;
        }
        String item = itemOf(json, "item");
        if (item.isEmpty()) {
            ContentLog.LOGGER.error("Anvil work {} names no item, ignoring it", key);
            return null;
        }
        String with = itemOf(json, "with");
        if (with.isEmpty()) {
            ContentLog.LOGGER.error("Anvil work {} names nothing for the right slot under with, and an anvil only answers to a pair, ignoring it", key);
            return null;
        }
        Map<String, Integer> enchantments = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "enchantments", new JsonObject()).entrySet()) {
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                ContentLog.LOGGER.error("Anvil work {} gives the enchantment {} something that is not a level, skipping it", key, entry.getKey());
                continue;
            }
            enchantments.put(entry.getKey(), Math.max(1, entry.getValue().getAsInt()));
        }
        return new AnvilDef(key, item, countOf(json, "item"), with, countOf(json, "with"), itemOf(json, "result"), countOf(json, "result"), Math.max(1, GsonHelper.getAsInt(json, "levels", 1)),
                enchantments, GsonHelper.getAsString(json, "grants", "").trim(), GsonHelper.getAsBoolean(json, "locks", false));
    }

    private static String itemOf(JsonObject json, String name) {
        if (json.has(name) && json.get(name).isJsonObject()) { return GsonHelper.getAsString(GsonHelper.getAsJsonObject(json, name), "item", "").trim(); }
        return GsonHelper.getAsString(json, name, "").trim();
    }

    private static int countOf(JsonObject json, String name) {
        if (!json.has(name) || !json.get(name).isJsonObject()) { return 1; }
        return Math.max(1, GsonHelper.getAsInt(GsonHelper.getAsJsonObject(json, name), "count", 1));
    }

    @Nullable private static AnvilDef holding(ItemStack held) {
        if (held.isEmpty()) { return null; }
        for (AnvilDef def : DEFS) {
            if (ItemStack.isSameItem(held, ITEMS.get(def))) { return def; }
        }
        return null;
    }

    public static void onAnvil(AnvilUpdateEvent event) {
        if (DEFS.isEmpty() || event.getRight().isEmpty()) { return; }
        AnvilDef def = holding(event.getLeft());
        if (def == null) { return; }
        if (!ItemStack.isSameItem(event.getRight(), WITH.get(def)) || event.getRight().getCount() < def.withCount() || event.getLeft().getCount() < def.itemCount()) { return; }
        ItemStack output = event.getLeft().copy();
        output.setCount(def.itemCount());
        ItemStack result = RESULTS.get(def);
        if (result != null) {
            output = result.copy();
            output.applyComponents(event.getLeft().getComponentsPatch());
        }
        ItemEnchantments.Mutable held = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(output));
        boolean rises = false;
        for (Map.Entry<String, Integer> entry : def.enchantments().entrySet()) {
            Holder<Enchantment> enchantment = enchantment(event.getPlayer(), def, entry.getKey());
            if (enchantment == null || held.getLevel(enchantment) >= entry.getValue()) { continue; }
            held.set(enchantment, entry.getValue());
            rises = true;
        }
        if (!rises && result == null && (!def.enchantments().isEmpty() || def.grants().isEmpty())) { return; }
        if (rises) { EnchantmentHelper.setEnchantments(output, held.toImmutable()); }
        OFFERED.put(output, def);
        event.setOutput(output);
        event.setCost(def.levels());
        event.setMaterialCost(def.withCount());
    }

    @Nullable private static Holder<Enchantment> enchantment(@Nullable Player player, AnvilDef def, String name) {
        ResourceLocation id = ResourceLocation.tryParse(name);
        Holder<Enchantment> found = id == null || player == null ? null : player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(ResourceKey.create(Registries.ENCHANTMENT, id)).orElse(null);
        if (found == null && player != null && UNKNOWN.add(def.key() + " " + name)) { ContentLog.LOGGER.error("Anvil work {} names the enchantment {}, which nothing registers, skipping it", def.key(), name); }
        return found;
    }

    public static void onTaken(AnvilRepairEvent event) {
        AnvilDef def = OFFERED.get(event.getOutput());
        if (def == null || def.grants().isEmpty() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (!Advancements.grant(player, def.grants())) { ContentLog.LOGGER.error("Anvil work {} grants the advancement {}, which no pack provides, so nothing is earned", def.key(), def.grants()); }
    }

    public static ItemStack leftAfterWork(AnvilMenu menu, ItemStack taken) {
        AnvilDef def = OFFERED.get(taken);
        if (def == null) { return ItemStack.EMPTY; }
        ItemStack left = menu.getSlot(0).getItem();
        if (left.getCount() <= def.itemCount()) { return ItemStack.EMPTY; }
        ItemStack kept = left.copy();
        kept.shrink(def.itemCount());
        return kept;
    }

    public static void onHeld(LivingEquipmentChangeEvent event) {
        if (DEFS.isEmpty() || event.getSlot() != EquipmentSlot.MAINHAND || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        AnvilDef def = holding(event.getTo());
        if (def == null || !def.locks() || def.grants().isEmpty() || Advancements.has(player, def.grants())) { return; }
        tell(player, def);
    }

    public static void onAttack(AttackEntityEvent event) {
        if (locked(event.getEntity(), event.getEntity().getMainHandItem())) { event.setCanceled(true); }
    }

    public static void onUse(PlayerInteractEvent.RightClickItem event) {
        if (locked(event.getEntity(), event.getItemStack())) { event.setCanceled(true); }
    }

    public static void onUseOn(PlayerInteractEvent.RightClickBlock event) {
        if (locked(event.getEntity(), event.getItemStack())) { event.setUseItem(TriState.FALSE); }
    }

    public static void onDig(PlayerInteractEvent.LeftClickBlock event) {
        if (locked(event.getEntity(), event.getItemStack())) { event.setCanceled(true); }
    }

    private static boolean locked(Player player, ItemStack held) {
        if (DEFS.isEmpty()) { return false; }
        AnvilDef def = holding(held);
        if (def == null || !def.locks() || def.grants().isEmpty() || Advancements.has(player, def.grants())) { return false; }
        if (player instanceof ServerPlayer server) { tell(server, def); }
        return true;
    }

    private static void tell(ServerPlayer player, AnvilDef def) {
        long now = player.serverLevel().getGameTime();
        String name = player.getGameProfile().getName();
        Long last = TOLD.get(name);
        if (last != null && now - last < TOLD_EVERY) { return; }
        TOLD.put(name, now);
        Says.tell(player, "That waits on " + Advancements.title(player.server, def.grants()), ChatFormatting.RED);
    }
}
