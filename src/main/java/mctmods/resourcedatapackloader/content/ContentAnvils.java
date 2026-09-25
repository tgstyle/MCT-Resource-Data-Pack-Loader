package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.AnvilDef;
import mctmods.resourcedatapackloader.content.entity.ContentMobExperience;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class ContentAnvils {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<ItemStack, AnvilDef> OFFERED = Collections.synchronizedMap(new WeakHashMap<>());
    private static final int TOLD_EVERY = 40;
    private static final float BREAK_CHANCE = 0.12F;
    private static final List<AnvilDef> DEFS = new ArrayList<>();
    private static final int MOST_LEVEL = 255;
    private static final Map<AnvilDef, List<ItemStack>> ITEMS = new IdentityHashMap<>();
    private static final Map<AnvilDef, List<ItemStack>> WITH = new IdentityHashMap<>();
    private static final Map<AnvilDef, ItemStack> RESULTS = new IdentityHashMap<>();
    private static final Set<String> UNKNOWN = new HashSet<>();
    private static final Map<String, Long> TOLD = new HashMap<>();

    private ContentAnvils() {}

    public static void load() {
        DEFS.clear();
        ITEMS.clear();
        WITH.clear();
        RESULTS.clear();
        UNKNOWN.clear();
        if (Config.data.anvilsOff()) { return; }
        Json.eachFile(PackManager.ANVILS, "anvil file", (key, contents) -> {
            AnvilDef def = read(key, contents);
            if (def == null) { return; }
            List<ItemStack> stack = stacks(key, def.item());
            List<ItemStack> with = stacks(key, def.with());
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
    }

    @Nullable private static AnvilDef read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Anvil work {} is empty, ignoring it", key);
            return null;
        }
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
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "enchantments", new JsonObject()).entrySet()) {
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                ContentLog.LOGGER.error("Anvil work {} gives the enchantment {} something that is not a level, skipping it", key, entry.getKey());
                continue;
            }
            enchantments.put(entry.getKey().trim().toLowerCase(Locale.ROOT), Mth.clamp(entry.getValue().getAsInt(), 1, MOST_LEVEL));
        }
        return new AnvilDef(key, item, countOf(json, "item"), with, countOf(json, "with"), resultOf(json), countOf(json, "result"), Math.max(1, GsonHelper.getAsInt(json, "levels", 1)),
                enchantments, GsonHelper.getAsString(json, "grants", "").trim(), GsonHelper.getAsBoolean(json, "locks", false));
    }

    private static String resultOf(JsonObject json) {
        if (json.has("result") && json.get("result").isJsonObject()) { return GsonHelper.getAsString(GsonHelper.getAsJsonObject(json, "result"), "item", "").trim(); }
        return GsonHelper.getAsString(json, "result", "").trim();
    }

    private static List<String> itemsOf(JsonObject json, String name) {
        JsonObject holder = json.has(name) && json.get(name).isJsonObject() ? GsonHelper.getAsJsonObject(json, name) : json;
        String field = holder == json ? name : "item";
        if (holder.has(field) && holder.get(field).isJsonArray()) {
            List<String> names = new ArrayList<>();
            for (String each : Json.strings(holder, field)) {
                if (!each.trim().isEmpty()) { names.add(each.trim()); }
            }
            return names;
        }
        String single = GsonHelper.getAsString(holder, field, "").trim();
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
        if (!json.has(name) || !json.get(name).isJsonObject()) { return 1; }
        return Math.max(1, GsonHelper.getAsInt(GsonHelper.getAsJsonObject(json, name), "count", 1));
    }

    @Nullable private static AnvilDef holding(ItemStack held) {
        if (held.isEmpty()) { return null; }
        for (AnvilDef def : DEFS) {
            if (any(held, ITEMS.get(def))) { return def; }
        }
        return null;
    }

    private static boolean pairs(AnvilDef def, ItemStack left, ItemStack right) {
        return any(left, ITEMS.get(def)) && left.getCount() >= def.itemCount() && any(right, WITH.get(def)) && right.getCount() >= def.withCount();
    }

    private static ItemStack worked(Level level, AnvilDef def, ItemStack left) {
        ItemStack output = left.copy();
        output.setCount(def.itemCount());
        ItemStack result = RESULTS.get(def);
        if (result != null) {
            output = result.copy();
            if (!left.getComponentsPatch().isEmpty()) {
                output = new ItemStack(result.getItemHolder(), result.getCount(), left.getComponentsPatch());
                if (result.has(DataComponents.DAMAGE)) { output.set(DataComponents.DAMAGE, result.getDamageValue()); }
                else { output.remove(DataComponents.DAMAGE); }
            }
        }
        ItemEnchantments.Mutable held = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(output));
        boolean rises = false;
        for (Map.Entry<String, Integer> entry : def.enchantments().entrySet()) {
            Holder<Enchantment> enchantment = enchantment(level, def, entry.getKey());
            if (enchantment == null || held.getLevel(enchantment) >= entry.getValue()) { continue; }
            held.set(enchantment, entry.getValue());
            rises = true;
        }
        if (!rises && result == null) { return ItemStack.EMPTY; }
        if (rises) { EnchantmentHelper.setEnchantments(output, held.toImmutable()); }
        return output;
    }

    public static void onAnvil(AnvilUpdateEvent event) {
        if (DEFS.isEmpty() || event.getRight().isEmpty()) { return; }
        AnvilDef def = holding(event.getLeft());
        if (def == null || !pairs(def, event.getLeft(), event.getRight())) { return; }
        ItemStack output = worked(event.getPlayer().level(), def, event.getLeft());
        if (output.isEmpty()) {
            if (resolvesAny(event.getPlayer().level(), def) || def.grants().isEmpty()) { return; }
            output = event.getLeft().copy();
            output.setCount(def.itemCount());
        }
        OFFERED.put(output, def);
        event.setOutput(output);
        event.setCost(def.levels());
        event.setMaterialCost(def.withCount());
    }

    private static boolean resolvesAny(Level level, AnvilDef def) {
        for (String name : def.enchantments().keySet()) {
            if (enchantment(level, def, name) != null) { return true; }
        }
        return false;
    }

    public static void onServerStarted(ServerStartedEvent event) {
        for (AnvilDef def : DEFS) {
            for (String name : def.enchantments().keySet()) { enchantment(event.getServer().overworld(), def, name); }
        }
    }

    @Nullable private static Holder<Enchantment> enchantment(Level level, AnvilDef def, String name) {
        ResourceLocation id = ContentParser.location(name);
        Holder<Enchantment> found = id == null ? null : level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(ResourceKey.create(Registries.ENCHANTMENT, id)).orElse(null);
        if (found == null && UNKNOWN.add(def.key() + " " + name)) { ContentLog.LOGGER.error("Anvil work {} names the enchantment {}, which nothing registers, skipping it", def.key(), name); }
        return found;
    }

    @Nullable public static AnvilDef affordable(Mob mob, int level) {
        ItemStack left = mob.getMainHandItem();
        ItemStack right = mob.getOffhandItem();
        if (DEFS.isEmpty() || left.isEmpty() || right.isEmpty()) { return null; }
        for (AnvilDef def : DEFS) {
            if (def.levels() <= level && pairs(def, left, right) && !worked(mob.level(), def, left).isEmpty()) { return def; }
        }
        return null;
    }

    public static void gather(Mob mob) {
        if (DEFS.isEmpty()) { return; }
        for (ItemEntity dropped : mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(1.0D, 0.5D, 1.0D))) {
            if (!dropped.isAlive() || dropped.hasPickUpDelay()) { continue; }
            ItemStack stack = dropped.getItem();
            if (!paysFor(stack)) { continue; }
            ItemStack right = mob.getOffhandItem();
            int room = right.isEmpty() ? stack.getMaxStackSize() : ItemStack.isSameItemSameComponents(right, stack) ? right.getMaxStackSize() - right.getCount() : 0;
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

    private static boolean paysFor(ItemStack stack) {
        for (AnvilDef def : DEFS) {
            if (any(stack, WITH.get(def))) { return true; }
        }
        return false;
    }

    public static void work(Mob mob, AnvilDef def, BlockPos anvil) {
        ItemStack left = mob.getMainHandItem();
        ItemStack right = mob.getOffhandItem();
        Level level = mob.level();
        BlockState state = level.getBlockState(anvil);
        if (!anvil(state) || def.levels() > ContentMobExperience.level(mob) || !pairs(def, left, right)) { return; }
        ItemStack output = worked(level, def, left);
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
                level.setBlock(anvil, worn, 2);
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
        Says.tell(player, mctmods.resourcedatapackloader.content.card.CardIds.ANVIL_WAITS, "That waits on " + Advancements.title(player.server, def.grants()), ChatFormatting.RED);
    }
}
