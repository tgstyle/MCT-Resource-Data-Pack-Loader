package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.cards.CardIds;
import mctmods.resourcedatapackloader.common.loot.PlayerLoot;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.util.ItemHandlers;
import mctmods.resourcedatapackloader.common.util.PlayerLogout;
import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class GateLoot {
    public static final GateLoot NONE = new GateLoot(false, Set.of(), true, "");
    private static final String DROP_ON_LEAVE = "dropOnLeave";
    private static final String MARK = "rdplLoot";
    private static final int PICKUP_DELAY = 40;
    private static final int TIMED_EVERY = 20;
    private static final Map<UUID, BlockPos> POSITIONS = PlayerLogout.track(new HashMap<>());
    private static final Map<UUID, List<ItemStack>> OPEN = PlayerLogout.track(new HashMap<>());
    private static final Map<UUID, List<ItemStack>> OWN = PlayerLogout.track(new HashMap<>());
    private static final List<String> LOOT_KEYS = List.of("keep", "enderChest", "message");
    private static int ticks;

    private final boolean enabled;
    private final Set<Identifier> keep;
    private final boolean enderChest;
    private final String message;

    private GateLoot(boolean enabled, Set<Identifier> keep, boolean enderChest, String message) {
        this.enabled = enabled;
        this.keep = keep;
        this.enderChest = enderChest;
        this.message = message;
    }

    public static GateLoot parse(JsonObject json, Identifier key, @Nullable Identifier dimension) {
        if (!json.has(DROP_ON_LEAVE)) { return NONE; }
        JsonElement element = json.get(DROP_ON_LEAVE);
        if (!element.isJsonObject() && !Json.getBoolean(json, DROP_ON_LEAVE, false)) { return NONE; }
        if (GateDef.dimensionMissing(dimension, key, DROP_ON_LEAVE, "nothing is marked")) { return NONE; }
        JsonObject options = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        Json.unknownKeys(options, DROP_ON_LEAVE, LOOT_KEYS);
        Set<Identifier> keep = new HashSet<>();
        for (String named : Json.strings(options, "keep")) {
            Identifier item = named.contains(":") ? Identifier.tryParse(named) : null;
            if (item == null || !BuiltInRegistries.ITEM.containsKey(item)) { ContentLog.LOGGER.warn("Gate {} lists keep item {}, which is not a known item, so it is ignored", key, named); }
            else { keep.add(item); }
        }
        return new GateLoot(true, keep, Json.getBoolean(options, "enderChest", true), Json.getString(options, "message", "Loot from %dim% stays behind"));
    }

    public boolean active() { return enabled; }

    static boolean stays(Player player, ItemStack stack) {
        GateDef def = lootDef(player);
        return def != null && (marked(stack, def.id()) || holds(stack, def.id()));
    }

    private boolean keeps(ItemStack stack) { return keep.contains(BuiltInRegistries.ITEM.getKey(stack.getItem())); }

    @SubscribeEvent public static void onTick(ServerTickEvent.Post event) {
        if (!ContentGates.timed() || ++ticks % TIMED_EVERY != 0) { return; }
        MinecraftServer server = event.getServer();
        GateStay.tick(server, TIMED_EVERY);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (ContentGates.lootDimension(player.level().dimension().identifier()) == null) {
                OWN.remove(player.getUUID());
                continue;
            }
            POSITIONS.put(player.getUUID(), player.blockPosition());
            if (player.containerMenu == player.inventoryMenu) { snapshot(player); }
        }
    }

    private static List<ItemStack> copies(Player player) {
        List<ItemStack> copies = new ArrayList<>();
        for (ItemStack stack : Stacks.held(player)) { copies.add(stack.copy()); }
        return copies;
    }

    private static void snapshot(Player player) { OWN.put(player.getUUID(), copies(player)); }

    @SubscribeEvent public static void onPickup(ItemEntityPickupEvent.Pre event) {
        GateDef def = lootDef(event.getPlayer());
        if (def != null) { mark(def, event.getItemEntity().getItem()); }
    }

    @SubscribeEvent public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        GateDef def = lootDef(event.getEntity());
        if (def == null || event.getEntity().level().isClientSide()) { return; }
        mark(def, event.getCrafting());
        Player player = event.getEntity();
        List<ItemStack> before = OWN.get(player.getUUID());
        if (before == null || player.containerMenu != player.inventoryMenu) { return; }
        markTaken(player, def, before);
        snapshot(player);
    }

    @SubscribeEvent public static void onSmelted(PlayerEvent.ItemSmeltedEvent event) {
        GateDef def = lootDef(event.getEntity());
        if (def != null && !event.getEntity().level().isClientSide()) { mark(def, event.getSmelting()); }
    }

    @SubscribeEvent public static void onOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || lootDef(player) == null) { return; }
        OPEN.put(player.getUUID(), copies(player));
    }

    @SubscribeEvent public static void onClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        List<ItemStack> before = OPEN.remove(player.getUUID());
        GateDef def = lootDef(player);
        if (before == null || def == null) { return; }
        markTaken(player, def, before);
        snapshot(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (event.getDimension().equals(entity.level().dimension())) { return; }
        GateDef def = ContentGates.lootDimension(entity.level().dimension().identifier());
        if (def == null) { return; }
        if (entity instanceof ServerPlayer player) { drop(player, def, player.level(), player.blockPosition(), def.loot().enderChest); }
        else if (carriesMarked(entity, def.id())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onArrive(PlayerEvent.PlayerChangedDimensionEvent event) {
        GateDef def = ContentGates.lootDimension(event.getFrom().identifier());
        BlockPos last = POSITIONS.get(event.getEntity().getUUID());
        if (ContentGates.lootDimension(event.getTo().identifier()) != null) { snapshot(event.getEntity()); }
        if (def == null || last == null || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        ServerLevel from = player.level().getServer().getLevel(event.getFrom());
        if (from != null) { drop(player, def, from, last, def.loot().enderChest); }
    }

    @SubscribeEvent public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        GateDef def = lootDef(player);
        if (def != null && PlayerLoot.keepInventory(player.level())) { drop(player, def, player.level(), player.blockPosition(), false); }
    }

    @Nullable private static GateDef lootDef(Player player) { return ContentGates.lootDimension(player.level().dimension().identifier()); }

    private static void mark(GateDef def, ItemStack stack) {
        if (stack.isEmpty() || def.loot().keeps(stack)) { return; }
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(MARK, def.id()));
    }

    private static boolean marked(ItemStack stack, String key) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(MARK) && key.equals(data.copyTag().getStringOr(MARK, ""));
    }

    private static boolean holds(ItemStack stack, String key) {
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents != null && contents.nonEmptyItemCopyStream().anyMatch(inner -> marked(inner, key))) { return true; }
        BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null && bundle.items().stream().anyMatch(inner -> marked(inner.create(), key))) { return true; }
        if (stack.isEmpty()) { return false; }
        ResourceHandler<ItemResource> handler = ItemAccess.forStack(stack.copy()).getCapability(Capabilities.Item.ITEM);
        if (handler == null) { return false; }
        return ItemHandlers.any(handler, held -> marked(held, key));
    }

    private static boolean carriesMarked(Entity entity, String key) {
        if (entity instanceof ItemEntity item) { return marked(item.getItem(), key); }
        if (!(entity instanceof Container inventory)) { return false; }
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (marked(stack, key) || holds(stack, key)) { return true; }
        }
        return false;
    }

    private static int total(List<ItemStack> stacks, ItemStack kind) {
        int total = 0;
        for (ItemStack stack : stacks) {
            if (ItemStack.isSameItemSameComponents(kind, stack)) { total += stack.getCount(); }
        }
        return total;
    }

    private static void markTaken(Player player, GateDef def, List<ItemStack> before) {
        List<ItemStack> now = Stacks.held(player);
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
        for (ItemStack part : split) {
            if (!player.getInventory().add(part)) { player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), part)); }
        }
    }

    private static void drop(ServerPlayer player, GateDef def, ServerLevel level, BlockPos at, boolean enderChest) {
        String key = def.id();
        List<Container> sources = new ArrayList<>();
        sources.add(player.getInventory());
        if (enderChest) { sources.add(player.getEnderChestInventory()); }
        if (player.getVehicle() instanceof Container vehicle) { sources.add(vehicle); }
        int count = 0;
        for (Container source : sources) {
            for (int slot = 0; slot < source.getContainerSize(); slot++) {
                ItemStack stack = source.getItem(slot);
                if (stack.isEmpty() || !marked(stack, key) && !holds(stack, key)) { continue; }
                count += spill(stack.copy(), key, level, at);
                source.setItem(slot, ItemStack.EMPTY);
            }
        }
        for (ItemStack stack : ContentCurios.worn(player, worn -> marked(worn, key) || holds(worn, key))) {
            count += spill(stack.copy(), key, level, at);
            stack.setCount(0);
        }
        if (count == 0) { return; }
        ContentLog.LOGGER.info("Gate {} dropped {} marked item(s) of {} on leaving dimension {}", key, count, player.getName().getString(), def.dimension());
        Says.bar(player, CardIds.GATE_LOOT_LEFT, ChatFormatting.YELLOW, Says.fill(def.loot().message, "%dim%", def.name(), "%count%", Integer.toString(count)));
    }

    private static int spill(ItemStack dropped, String key, ServerLevel level, BlockPos at) {
        if (marked(dropped, key)) { CustomData.update(DataComponents.CUSTOM_DATA, dropped, tag -> tag.remove(MARK)); }
        int count = dropped.getCount();
        Vec3 feet = Vec3.atBottomCenterOf(at);
        Stacks.dropAt(level, feet.x, feet.y, feet.z, dropped, PICKUP_DELAY);
        return count;
    }
}
