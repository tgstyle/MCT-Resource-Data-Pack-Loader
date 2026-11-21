package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.cards.CardIds;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class GateLoot {
    public static final GateLoot NONE = new GateLoot(false, List.of(), true, "");
    private static final String MARK = "rdplLoot";
    private static final int PICKUP_DELAY = 40;
    private static final Map<UUID, BlockPos> POSITIONS = new HashMap<>();
    private static final Map<UUID, List<ItemStack>> OPEN = new HashMap<>();
    private static final Map<UUID, List<ItemStack>> OWN = new HashMap<>();
    private static final List<String> LOOT_KEYS = List.of("keep", "enderChest", "message");

    private final boolean enabled;
    private final List<Item> keep;
    private final boolean enderChest;
    private final String message;

    private GateLoot(boolean enabled, List<Item> keep, boolean enderChest, String message) {
        this.enabled = enabled;
        this.keep = keep;
        this.enderChest = enderChest;
        this.message = message;
    }

    public static GateLoot parse(JsonObject json, ResourceLocation key, @Nullable ResourceLocation dimension) {
        if (!json.has("dropOnLeave")) { return NONE; }
        JsonElement element = json.get("dropOnLeave");
        if (!element.isJsonObject() && !Json.getBoolean(json, "dropOnLeave", false)) { return NONE; }
        if (dimension == null) {
            ContentLog.LOGGER.error("Gate {} sets dropOnLeave without dimension, so nothing is marked", key);
            return NONE;
        }
        JsonObject options = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        Json.unknownKeys(options, "dropOnLeave", LOOT_KEYS);
        List<Item> keep = new ArrayList<>();
        for (String named : Json.strings(options, "keep")) {
            ResourceLocation id = named.indexOf(':') < 0 ? null : ResourceLocation.tryParse(named);
            Item item = id == null ? null : BuiltInRegistries.ITEM.getOptional(id).orElse(null);
            if (item == null) { ContentLog.LOGGER.warn("Gate {} lists keep item {}, which is not a known item, so it is ignored", key, named); }
            else { keep.add(item); }
        }
        return new GateLoot(true, List.copyOf(keep), Json.getBoolean(options, "enderChest", true), Json.getString(options, "message", "Loot from %dim% stays behind"));
    }

    public static void register(IEventBus bus) {
        bus.addListener(GateEvents::onTick);
        bus.addListener(GateLoot::onLogout);
        bus.addListener(GateLoot::onPickup);
        bus.addListener(GateLoot::onCrafted);
        bus.addListener(GateLoot::onSmelted);
        bus.addListener(GateLoot::onOpen);
        bus.addListener(GateLoot::onClose);
        bus.addListener(EventPriority.LOWEST, GateLoot::onTravel);
        bus.addListener(GateLoot::onArrive);
        bus.addListener(GateLoot::onDeath);
    }

    public boolean active() { return enabled; }

    static boolean stays(Player player, ItemStack stack) {
        GateDef def = ContentGates.lootDimension(player.level().dimension().location());
        return def != null && (marked(stack, def.id()) || holds(stack, def.id()));
    }

    private boolean keeps(ItemStack stack) {
        for (Item wanted : keep) {
            if (stack.is(wanted)) { return true; }
        }
        return false;
    }

    static void track(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (lootOf(player) == null) {
                OWN.remove(player.getUUID());
                continue;
            }
            POSITIONS.put(player.getUUID(), player.blockPosition());
            if (player.containerMenu == player.inventoryMenu) { snapshot(player); }
        }
    }

    private static void snapshot(Player player) {
        List<ItemStack> own = new ArrayList<>();
        for (ItemStack stack : held(player)) { own.add(stack.copy()); }
        OWN.put(player.getUUID(), own);
    }

    @Nullable private static GateDef lootOf(Entity entity) { return ContentGates.lootDimension(entity.level().dimension().location()); }

    private static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        POSITIONS.remove(event.getEntity().getUUID());
        OPEN.remove(event.getEntity().getUUID());
        OWN.remove(event.getEntity().getUUID());
    }

    private static void onPickup(ItemEntityPickupEvent.Pre event) {
        GateDef def = lootOf(event.getPlayer());
        if (def != null) { mark(def, event.getItemEntity().getItem()); }
    }

    private static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        GateDef def = lootOf(event.getEntity());
        if (def == null || event.getEntity().level().isClientSide) { return; }
        mark(def, event.getCrafting());
        Player player = event.getEntity();
        List<ItemStack> before = OWN.get(player.getUUID());
        if (before == null || player.containerMenu != player.inventoryMenu || !(player instanceof ServerPlayer server)) { return; }
        markTaken(server, def, before);
        snapshot(player);
    }

    private static void onSmelted(PlayerEvent.ItemSmeltedEvent event) {
        GateDef def = lootOf(event.getEntity());
        if (def != null && !event.getEntity().level().isClientSide) { mark(def, event.getSmelting()); }
    }

    private static void onOpen(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || lootOf(player) == null) { return; }
        List<ItemStack> before = new ArrayList<>();
        for (ItemStack stack : held(player)) { before.add(stack.copy()); }
        OPEN.put(player.getUUID(), before);
    }

    private static void onClose(PlayerContainerEvent.Close event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        List<ItemStack> before = OPEN.remove(player.getUUID());
        GateDef def = lootOf(player);
        if (before == null || def == null) { return; }
        markTaken(player, def, before);
        snapshot(player);
    }

    private static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        GateDef def = lootOf(entity);
        if (def == null) { return; }
        if (entity instanceof ServerPlayer player) { drop(player, def, player.serverLevel(), player.blockPosition(), def.loot().enderChest); }
        else if (carriesMarked(entity, def.id())) { event.setCanceled(true); }
    }

    private static void onArrive(PlayerEvent.PlayerChangedDimensionEvent event) {
        GateDef def = ContentGates.lootDimension(event.getFrom().location());
        BlockPos last = POSITIONS.get(event.getEntity().getUUID());
        if (ContentGates.lootDimension(event.getTo().location()) != null) { snapshot(event.getEntity()); }
        if (def == null || last == null || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        ServerLevel from = player.server.getLevel(event.getFrom());
        if (from != null) { drop(player, def, from, last, def.loot().enderChest); }
    }

    private static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        GateDef def = lootOf(player);
        if (def != null && player.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) { drop(player, def, player.serverLevel(), player.blockPosition(), false); }
    }

    private static void mark(GateDef def, ItemStack stack) {
        if (stack.isEmpty() || def.loot().keeps(stack)) { return; }
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(MARK, def.id()));
    }

    private static boolean marked(ItemStack stack, String key) {
        return !stack.isEmpty() && key.equals(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString(MARK));
    }

    private static boolean holds(ItemStack stack, String key) {
        for (ItemStack inside : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()) {
            if (marked(inside, key)) { return true; }
        }
        BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) {
            for (ItemStack inside : bundle.items()) {
                if (marked(inside, key)) { return true; }
            }
        }
        IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler == null) { return false; }
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (marked(handler.getStackInSlot(slot), key)) { return true; }
        }
        return false;
    }

    private static boolean carriesMarked(Entity entity, String key) {
        if (entity instanceof ItemEntity item) { return marked(item.getItem(), key); }
        if (!(entity instanceof Container container)) { return false; }
        for (ItemStack stack : contents(container)) {
            if (marked(stack, key) || holds(stack, key)) { return true; }
        }
        return false;
    }

    private static List<ItemStack> contents(Container container) {
        List<ItemStack> all = new ArrayList<>();
        for (int slot = 0; slot < container.getContainerSize(); slot++) { all.add(container.getItem(slot)); }
        return all;
    }

    private static List<ItemStack> held(Player player) {
        List<ItemStack> all = new ArrayList<>();
        for (List<ItemStack> part : List.of(player.getInventory().items, player.getInventory().armor, player.getInventory().offhand)) {
            for (ItemStack stack : part) {
                if (!stack.isEmpty()) { all.add(stack); }
            }
        }
        return all;
    }

    private static List<ItemStack> carried(Player player, boolean enderChest) {
        List<ItemStack> all = held(player);
        if (enderChest) { all.addAll(contents(player.getEnderChestInventory())); }
        if (player.getVehicle() instanceof Container vehicle) { all.addAll(contents(vehicle)); }
        all.addAll(ContentCurios.worn(player, stack -> !stack.isEmpty()));
        return all;
    }

    private static int total(List<ItemStack> stacks, ItemStack kind) {
        int total = 0;
        for (ItemStack stack : stacks) {
            if (ItemStack.isSameItemSameComponents(kind, stack)) { total += stack.getCount(); }
        }
        return total;
    }

    private static void markTaken(ServerPlayer player, GateDef def, List<ItemStack> before) {
        List<ItemStack> now = held(player);
        List<ItemStack> done = new ArrayList<>();
        List<ItemStack> split = new ArrayList<>();
        for (ItemStack kind : now) {
            if (marked(kind, def.id()) || def.loot().keeps(kind) || total(done, kind) > 0) { continue; }
            done.add(kind);
            int owed = total(now, kind) - total(before, kind);
            for (ItemStack stack : now) {
                if (owed <= 0) { break; }
                if (!ItemStack.isSameItemSameComponents(kind, stack)) { continue; }
                int take = Math.min(owed, stack.getCount());
                owed -= take;
                if (take == stack.getCount()) {
                    mark(def, stack);
                    continue;
                }
                ItemStack part = stack.copyWithCount(take);
                stack.shrink(take);
                mark(def, part);
                split.add(part);
            }
        }
        for (ItemStack part : split) { ItemHandlerHelper.giveItemToPlayer(player, part); }
    }

    private static void drop(ServerPlayer player, GateDef def, ServerLevel level, BlockPos at, boolean enderChest) {
        String key = def.id();
        int count = 0;
        for (ItemStack stack : carried(player, enderChest)) {
            if (stack.isEmpty() || !marked(stack, key) && !holds(stack, key)) { continue; }
            ItemStack dropped = stack.copy();
            if (marked(dropped, key)) { CustomData.update(DataComponents.CUSTOM_DATA, dropped, tag -> tag.remove(MARK)); }
            stack.setCount(0);
            count += dropped.getCount();
            ItemEntity item = new ItemEntity(level, at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D, dropped);
            item.setPickUpDelay(PICKUP_DELAY);
            level.addFreshEntity(item);
        }
        if (count == 0) { return; }
        ContentLog.LOGGER.info("Gate {} dropped {} marked item(s) of {} on leaving dimension {}", key, count, player.getName().getString(), def.dimension());
        Says.bar(player, CardIds.GATE_LOOT_LEFT, ChatFormatting.YELLOW, def.loot().message.replace("%dim%", def.name()).replace("%count%", Integer.toString(count)));
    }
}
