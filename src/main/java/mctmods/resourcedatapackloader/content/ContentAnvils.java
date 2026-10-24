package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.content.def.AnvilDef;
import mctmods.resourcedatapackloader.content.entity.ContentMobExperience;
import mctmods.resourcedatapackloader.content.util.ItemUseGate;
import mctmods.resourcedatapackloader.content.util.TellThrottle;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.VanillaClients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentAnvils {
    private static final Map<ItemStack, AnvilDef> OFFERED = Collections.synchronizedMap(new WeakHashMap<>());
    private static final String TOO_EXPENSIVE = "tooExpensive";
    private static final int TOLD_EVERY = 40;
    private static final float BREAK_CHANCE = 0.12F;
    private static final List<AnvilDef> DEFS = new ArrayList<>();
    private static final int MOST_LEVEL = 255;
    private static final Map<AnvilDef, List<ItemStack>> ITEMS = new IdentityHashMap<>();
    private static final Map<AnvilDef, List<ItemStack>> WITH = new IdentityHashMap<>();
    private static final Map<AnvilDef, ItemStack> RESULTS = new IdentityHashMap<>();
    private static final Map<AnvilDef, Map<Enchantment, Integer>> ENCHANTMENTS = new IdentityHashMap<>();
    private static final TellThrottle TOLD = new TellThrottle(TOLD_EVERY);

    private ContentAnvils() {}

    public static void load() {
        DEFS.clear();
        ITEMS.clear();
        WITH.clear();
        RESULTS.clear();
        ENCHANTMENTS.clear();
        AnvilCosts.resetAnvil();
        AnvilRules.reset();
        if (Config.data.anvilsOff()) { return; }
        Json.eachFile(PackManager.ANVILS, "anvil file", (key, contents) -> {
            JsonObject json = Json.object(key, contents, "anvil work");
            if (json == null || !ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
            if (AnvilRules.names(json)) {
                AnvilRules.read(key, json);
                return;
            }
            if (settings(key, json) && !json.has("item")) { return; }
            AnvilDef def = read(key, json);
            if (def == null) { return; }
            List<ItemStack> stack = stacks(key, def.item());
            List<ItemStack> with = stacks(key, def.with());
            if (stack.isEmpty() || with.isEmpty()) { return; }
            if (!def.result().isEmpty()) {
                ItemStack result = ContentStacks.parse(key, def.result(), def.resultCount());
                if (result.isEmpty()) { return; }
                RESULTS.put(def, result);
            }
            Map<Enchantment, Integer> found = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : def.enchantments().entrySet()) {
                ResourceLocation id = ContentParser.location(entry.getKey());
                Enchantment enchantment = id == null ? null : ForgeRegistries.ENCHANTMENTS.getValue(id);
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
        if (AnvilRules.count() > 0) { Summary.info("anvils", "Loaded " + AnvilRules.count() + " anvil rule(s)"); }
        if (AnvilCosts.servedCap() != AnvilCosts.VANILLA_CAP) { Summary.info("anvils", "Anvil work costing " + AnvilCosts.servedCap() + " level(s) or more is too expensive"); }
    }

    private static boolean settings(ResourceLocation key, JsonObject json) {
        if (!json.has(TOO_EXPENSIVE)) { return false; }
        int cap = Json.getInt(json, TOO_EXPENSIVE, AnvilCosts.VANILLA_CAP);
        if (cap < 1 || cap > AnvilCosts.HIGHEST) { ContentLog.LOGGER.error("Anvil file {} sets {} to {}, outside 1 to {}, ignoring it", key, TOO_EXPENSIVE, cap, AnvilCosts.HIGHEST); }
        else {
            AnvilCosts.serveCap(cap);
            VanillaClients.clientDrawn("Anvil file " + key, TOO_EXPENSIVE);
        }
        return true;
    }

    @Nullable private static AnvilDef read(ResourceLocation key, JsonObject json) {
        List<String> item = itemsOf(json, "item");
        if (item.isEmpty()) {
            ContentLog.LOGGER.error("Anvil work {} names no item, ignoring it", key);
            return null;
        }
        List<String> with = itemsOf(json, "with");
        if (with.isEmpty()) {
            ContentLog.LOGGER.error("Anvil work {} names nothing for the right slot under with, and an anvil only answers to a pair, ignoring it", key);
            return null;
        }
        Map<String, Integer> enchantments = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, "enchantments", new JsonObject()).entrySet()) {
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                ContentLog.LOGGER.error("Anvil work {} gives the enchantment {} something that is not a level, skipping it", key, entry.getKey());
                continue;
            }
            enchantments.put(Settings.lower(entry.getKey()),Mth.clamp(Json.asInt(entry.getValue(), entry.getKey(), 1), 1, MOST_LEVEL));
        }
        return new AnvilDef(key, item, countOf(json, "item"), with, countOf(json, "with"), resultOf(json), countOf(json, "result"), Math.max(1, Json.getInt(json, "levels", 1)),
                enchantments, Json.getString(json, "grants", "").trim(), Json.getBoolean(json, "locks", false));
    }

    private static String resultOf(JsonObject json) {
        if (GsonHelper.isObjectNode(json, "result")) { return Json.getString(Json.getJsonObject(json, "result"), "item", "").trim(); }
        return Json.getString(json, "result", "").trim();
    }

    private static List<String> itemsOf(JsonObject json, String name) {
        JsonObject holder = GsonHelper.isObjectNode(json, name) ? Json.getJsonObject(json, name) : json;
        String field = holder == json ? name : "item";
        if (GsonHelper.isArrayNode(holder, field)) {
            List<String> names = new ArrayList<>();
            for (String each : Json.strings(holder, field)) {
                if (!each.trim().isEmpty()) { names.add(each.trim()); }
            }
            return names;
        }
        String single = Json.getString(holder, field, "").trim();
        return single.isEmpty() ? List.of() : List.of(single);
    }

    private static List<ItemStack> stacks(ResourceLocation key, List<String> names) {
        List<ItemStack> found = new ArrayList<>();
        for (String name : names) {
            ItemStack stack = ContentStacks.parse(key, name, 1);
            if (stack.isEmpty()) { return List.of(); }
            found.add(stack);
        }
        return found;
    }

    private static boolean any(ItemStack held, List<ItemStack> wanted) {
        for (ItemStack each : wanted) {
            if (ItemStack.isSameItem(held, each)) { return true; }
        }
        return false;
    }

    public static boolean anvil(BlockState state) { return state.is(Blocks.ANVIL) || state.is(Blocks.CHIPPED_ANVIL) || state.is(Blocks.DAMAGED_ANVIL); }

    private static int countOf(JsonObject json, String name) {
        if (!GsonHelper.isObjectNode(json, name)) { return 1; }
        return Math.max(1, Json.getInt(Json.getJsonObject(json, name), "count", 1));
    }

    @Nullable private static AnvilDef holding(ItemStack held) {
        if (held.isEmpty()) { return null; }
        for (AnvilDef def : DEFS) {
            if (any(held, ITEMS.get(def))) { return def; }
        }
        return null;
    }

    private static boolean pairs(AnvilDef def, ItemStack left, ItemStack right) { return any(left, ITEMS.get(def)) && left.getCount() >= def.itemCount() && any(right, WITH.get(def)) && right.getCount() >= def.withCount(); }

    private static ItemStack worked(AnvilDef def, ItemStack left) {
        ItemStack output = left.copy();
        output.setCount(def.itemCount());
        ItemStack result = RESULTS.get(def);
        if (result != null) {
            output = result.copy();
            CompoundTag carried = left.getTag();
            if (carried != null) {
                CompoundTag kept = carried.copy();
                kept.remove(ItemStack.TAG_DAMAGE);
                output.setTag(kept);
                if (result.getDamageValue() > 0) { output.setDamageValue(result.getDamageValue()); }
            }
        }
        Map<Enchantment, Integer> held = EnchantmentHelper.getEnchantments(output);
        boolean rises = false;
        for (Map.Entry<Enchantment, Integer> entry : ENCHANTMENTS.get(def).entrySet()) {
            Integer already = held.get(entry.getKey());
            if (already != null && already >= entry.getValue()) { continue; }
            held.put(entry.getKey(), entry.getValue());
            rises = true;
        }
        if (!rises && result == null) { return ItemStack.EMPTY; }
        if (rises) { EnchantmentHelper.setEnchantments(held, output); }
        return output;
    }

    public static void onAnvil(AnvilUpdateEvent event) {
        if (offerWork(event)) { return; }
        if (ContentAffixes.socket(event)) { return; }
        AnvilRules.apply(event);
    }

    private static boolean offerWork(AnvilUpdateEvent event) {
        if (DEFS.isEmpty() || event.getRight().isEmpty()) { return false; }
        AnvilDef def = holding(event.getLeft());
        if (def == null || !pairs(def, event.getLeft(), event.getRight())) { return false; }
        ItemStack output = worked(def, event.getLeft());
        if (output.isEmpty()) {
            if (!ENCHANTMENTS.get(def).isEmpty() || def.grants().isEmpty()) { return false; }
            output = event.getLeft().copy();
            output.setCount(def.itemCount());
        }
        OFFERED.put(output, def);
        event.setOutput(output);
        event.setCost(def.levels());
        event.setMaterialCost(def.withCount());
        return true;
    }

    @Nullable public static AnvilDef affordable(Mob mob, int level) {
        ItemStack left = mob.getMainHandItem();
        ItemStack right = mob.getOffhandItem();
        if (DEFS.isEmpty() || left.isEmpty() || right.isEmpty()) { return null; }
        for (AnvilDef def : DEFS) {
            if (def.levels() <= level && pairs(def, left, right) && !worked(def, left).isEmpty()) { return def; }
        }
        return null;
    }

    public static void gather(Mob mob) {
        if (DEFS.isEmpty()) { return; }
        for (ItemEntity dropped : mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(1.0D, 0.5D, 1.0D))) {
            if (!dropped.isAlive() || dropped.hasPickUpDelay()) { continue; }
            ItemStack stack = dropped.getItem();
            if (ignores(stack)) { continue; }
            ItemStack right = mob.getOffhandItem();
            int room = right.isEmpty() ? stack.getMaxStackSize() : ItemHandlerHelper.canItemStacksStack(right, stack) ? right.getMaxStackSize() - right.getCount() : 0;
            int taken = Math.min(room, stack.getCount());
            if (taken <= 0) { continue; }
            mob.take(dropped, taken);
            if (right.isEmpty()) { mob.setItemSlot(EquipmentSlot.OFFHAND, stack.copyWithCount(taken)); }
            else { right.grow(taken); }
            stack.shrink(taken);
            if (stack.isEmpty()) { dropped.discard(); }
            else { dropped.setItem(stack); }
        }
    }

    private static boolean ignores(ItemStack stack) {
        for (AnvilDef def : DEFS) {
            if (any(stack, WITH.get(def))) { return false; }
        }
        return true;
    }

    public static void work(Mob mob, AnvilDef def, BlockPos anvil) {
        ItemStack left = mob.getMainHandItem();
        ItemStack right = mob.getOffhandItem();
        Level level = mob.level();
        BlockState state = level.getBlockState(anvil);
        if (!anvil(state) || def.levels() > ContentMobExperience.level(mob) || !pairs(def, left, right)) { return; }
        ItemStack output = worked(def, left);
        if (output.isEmpty()) { return; }
        ContentMobExperience.addLevels(mob, -def.levels());
        ItemStack kept = left.getCount() > def.itemCount() ? left.copyWithCount(left.getCount() - def.itemCount()) : ItemStack.EMPTY;
        mob.setItemSlot(EquipmentSlot.MAINHAND, output);
        if (!kept.isEmpty()) { mob.spawnAtLocation(kept, 0.5F); }
        right.shrink(def.withCount());
        if (right.isEmpty()) { mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY); }
        if (mob.getRandom().nextFloat() < BREAK_CHANCE) {
            BlockState worn = AnvilBlock.damage(state);
            if (worn == null) {
                level.removeBlock(anvil, false);
                level.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, anvil, 0);
            }
            else {
                level.setBlock(anvil, worn, Block.UPDATE_CLIENTS);
                level.levelEvent(LevelEvent.SOUND_ANVIL_USED, anvil, 0);
            }
        }
        else { level.levelEvent(LevelEvent.SOUND_ANVIL_USED, anvil, 0); }
        ContentLog.LOGGER.info("{} worked {} at the anvil at {}, {}, {}: spent {} level(s) and {} {}, and holds {}", mob.getName().getString(), def.key(), anvil.getX(), anvil.getY(), anvil.getZ(), def.levels(), def.withCount(), String.join(" or ", def.with()), output);
        if (!def.grants().isEmpty()) { ContentLog.LOGGER.debug("Anvil work {} also grants {}, which only a player can earn, so {} earns nothing more", def.key(), def.grants(), mob.getName().getString()); }
    }

    public static void onTaken(AnvilRepairEvent event) {
        AnvilDef def = OFFERED.get(event.getOutput());
        if (def == null || def.grants().isEmpty() || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        if (!Advancements.grant(player, def.grants())) { ContentLog.LOGGER.error("Anvil work {} grants the advancement {}, which no pack provides, so nothing is earned", def.key(), def.grants()); }
    }

    public static ItemStack leftAfterWork(AnvilMenu menu, ItemStack taken) {
        AnvilDef def = OFFERED.get(taken);
        return def == null ? ItemStack.EMPTY : rest(menu.getSlot(0).getItem(), def.itemCount());
    }

    public static ItemStack rightAfterWork(AnvilMenu menu, ItemStack taken) {
        AnvilDef def = OFFERED.get(taken);
        return def == null ? ItemStack.EMPTY : rest(menu.getSlot(1).getItem(), def.withCount());
    }

    private static ItemStack rest(ItemStack held, int spent) {
        if (held.getCount() <= spent) { return ItemStack.EMPTY; }
        ItemStack kept = held.copy();
        kept.shrink(spent);
        return kept;
    }

    public static void onHeld(LivingEquipmentChangeEvent event) {
        if (DEFS.isEmpty() || event.getSlot() != EquipmentSlot.MAINHAND || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        locked(player, event.getTo());
    }

    public static void onAttack(AttackEntityEvent event) { ItemUseGate.cancel(event, locked(event.getEntity(), event.getEntity().getMainHandItem())); }

    public static void onUse(PlayerInteractEvent.RightClickItem event) { ItemUseGate.cancel(event, locked(event.getEntity(), event.getItemStack())); }

    public static void onUseOn(PlayerInteractEvent.RightClickBlock event) { ItemUseGate.denyUse(event, locked(event.getEntity(), event.getItemStack())); }

    public static void onDig(PlayerInteractEvent.LeftClickBlock event) { ItemUseGate.cancel(event, locked(event.getEntity(), event.getItemStack())); }

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
        if (!TOLD.ready(name, now)) { return; }
        TOLD.mark(name, now);
        Says.tell(player, CardIds.ANVIL_WAITS, ChatFormatting.RED, "That waits on " + Advancements.title(player.server, def.grants()));
    }
}
