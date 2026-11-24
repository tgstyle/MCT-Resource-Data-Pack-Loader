package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.cards.CardIds;
import mctmods.resourcedatapackloader.common.loot.PlayerLoot;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.items.ItemHandlerHelper;

public final class GateLoot {
    public static final GateLoot NONE = new GateLoot(false, Collections.emptySet(), true, "");
    private static final String DROP_ON_LEAVE = "dropOnLeave";
    private static final String MARK = "rdplLoot";
    private static final String BLOCK_ENTITY = "BlockEntityTag";
    private static final int PICKUP_DELAY = 40;
    private static final Map<UUID, BlockPos> POSITIONS = PlayerLogout.track(new HashMap<>());
    private static final Map<UUID, List<ItemStack>> OPEN = PlayerLogout.track(new HashMap<>());
    private static final Map<UUID, List<ItemStack>> OWN = PlayerLogout.track(new HashMap<>());
    private static final List<String> LOOT_KEYS = List.of("keep", "enderChest", "message");

    private final boolean enabled;
    private final Set<Item> keep;
    private final boolean enderChest;
    private final String message;

    private GateLoot(boolean enabled, Set<Item> keep, boolean enderChest, String message) {
        this.enabled = enabled;
        this.keep = keep;
        this.enderChest = enderChest;
        this.message = message;
    }

    public static GateLoot parse(JsonObject json, ResourceLocation key, @Nullable ResourceLocation dimension) {
        if (!json.has(DROP_ON_LEAVE)) { return NONE; }
        JsonElement element = json.get(DROP_ON_LEAVE);
        if (!element.isJsonObject() && !Json.getBoolean(json, DROP_ON_LEAVE, false)) { return NONE; }
        if (GateDef.dimensionMissing(dimension, key, DROP_ON_LEAVE, "nothing is marked")) { return NONE; }
        JsonObject options = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        Json.unknownKeys(options, DROP_ON_LEAVE, LOOT_KEYS);
        Set<Item> keep = new HashSet<>();
        for (String named : Json.strings(options, "keep")) {
            Item item = named.indexOf(':') < 0 ? null : ContentStacks.item(ResourceLocation.tryParse(named));
            if (item == null) { ContentLog.LOGGER.warn("Gate {} lists keep item {}, which is not a known item, so it is ignored", key, named); }
            else { keep.add(item); }
        }
        return new GateLoot(true, keep, Json.getBoolean(options, "enderChest", true), Json.getString(options, "message", "Loot from %dim% stays behind"));
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(GateEvents::onTick);
        MinecraftForge.EVENT_BUS.addListener(GateLoot::onPickup);
        MinecraftForge.EVENT_BUS.addListener(GateLoot::onCrafted);
        MinecraftForge.EVENT_BUS.addListener(GateLoot::onSmelted);
        MinecraftForge.EVENT_BUS.addListener(GateLoot::onOpen);
        MinecraftForge.EVENT_BUS.addListener(GateLoot::onClose);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, GateLoot::onTravel);
        MinecraftForge.EVENT_BUS.addListener(GateLoot::onArrive);
        MinecraftForge.EVENT_BUS.addListener(GateLoot::onDeath);
    }

    public boolean active() { return enabled; }

    static boolean stays(Player player, ItemStack stack) {
        GateDef def = ContentGates.lootDimension(player.level().dimension().location());
        return def != null && (marked(stack, def.id()) || holds(stack, def.id()));
    }

    private boolean keeps(ItemStack stack) { return keep.contains(stack.getItem()); }

    static void track(MinecraftServer server) {
        if (!ContentGates.looting()) { return; }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (ContentGates.lootDimension(player.level().dimension().location()) == null) {
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

    public static void onPickup(EntityItemPickupEvent event) {
        GateDef def = ContentGates.lootDimension(event.getEntity().level().dimension().location());
        if (def != null) { mark(def, event.getItem().getItem()); }
    }

    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        GateDef def = ContentGates.lootDimension(event.getEntity().level().dimension().location());
        if (def == null || event.getEntity().level().isClientSide()) { return; }
        mark(def, event.getCrafting());
        Player player = event.getEntity();
        List<ItemStack> before = OWN.get(player.getUUID());
        if (before == null || player.containerMenu != player.inventoryMenu) { return; }
        markTaken(player, def, before);
        snapshot(player);
    }

    public static void onSmelted(PlayerEvent.ItemSmeltedEvent event) {
        GateDef def = ContentGates.lootDimension(event.getEntity().level().dimension().location());
        if (def != null && !event.getEntity().level().isClientSide()) { mark(def, event.getSmelting()); }
    }

    public static void onOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || ContentGates.lootDimension(player.level().dimension().location()) == null) { return; }
        OPEN.put(player.getUUID(), copies(player));
    }

    public static void onClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) { return; }
        List<ItemStack> before = OPEN.remove(player.getUUID());
        GateDef def = ContentGates.lootDimension(player.level().dimension().location());
        if (before == null || def == null) { return; }
        markTaken(player, def, before);
        snapshot(player);
    }

    public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        GateDef def = ContentGates.lootDimension(entity.level().dimension().location());
        if (def == null) { return; }
        if (entity instanceof ServerPlayer player) { drop(player, def, player.serverLevel(), player.blockPosition(), def.loot().enderChest); }
        else if (carriesMarked(entity, def.id())) { event.setCanceled(true); }
    }

    public static void onArrive(PlayerEvent.PlayerChangedDimensionEvent event) {
        GateDef def = ContentGates.lootDimension(event.getFrom().location());
        BlockPos last = POSITIONS.get(event.getEntity().getUUID());
        if (ContentGates.lootDimension(event.getTo().location()) != null) { snapshot(event.getEntity()); }
        if (def == null || last == null || !(event.getEntity() instanceof ServerPlayer player)) { return; }
        ServerLevel from = player.server.getLevel(event.getFrom());
        if (from != null) { drop(player, def, from, last, def.loot().enderChest); }
    }

    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        GateDef def = ContentGates.lootDimension(player.level().dimension().location());
        if (def != null && PlayerLoot.keepInventory(player.level())) { drop(player, def, player.serverLevel(), player.blockPosition(), false); }
    }

    private static void mark(GateDef def, ItemStack stack) {
        if (stack.isEmpty() || def.loot().keeps(stack)) { return; }
        stack.getOrCreateTag().putString(MARK, def.id());
    }

    private static boolean marked(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        return tag != null && key.equals(tag.getString(MARK));
    }

    private static boolean holds(ItemStack stack, String key) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(BLOCK_ENTITY, Tag.TAG_COMPOUND)) {
            ListTag items = tag.getCompound(BLOCK_ENTITY).getList("Items", Tag.TAG_COMPOUND);
            for (int index = 0; index < items.size(); index++) {
                if (key.equals(items.getCompound(index).getCompound("tag").getString(MARK))) { return true; }
            }
        }
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> ItemHandlers.any(handler, held -> marked(held, key))).orElse(false);
    }

    private static boolean carriesMarked(Entity entity, String key) {
        if (entity instanceof ItemEntity item) { return marked(item.getItem(), key); }
        if (!(entity instanceof Container inventory)) { return false; }
        for (ItemStack stack : contents(inventory)) {
            if (marked(stack, key) || holds(stack, key)) { return true; }
        }
        return false;
    }

    private static List<ItemStack> contents(Container inventory) {
        List<ItemStack> all = new ArrayList<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) { all.add(inventory.getItem(slot)); }
        return all;
    }

    private static List<ItemStack> carried(Player player, boolean enderChest) {
        List<ItemStack> all = Stacks.held(player);
        if (enderChest) { all.addAll(contents(player.getEnderChestInventory())); }
        if (player.getVehicle() instanceof Container vehicle) { all.addAll(contents(vehicle)); }
        if (!ContentCurios.missing()) { all.addAll(ContentCurios.worn(player, stack -> !stack.isEmpty())); }
        return all;
    }

    private static int total(List<ItemStack> stacks, ItemStack kind) {
        int total = 0;
        for (ItemStack stack : stacks) {
            if (ItemHandlerHelper.canItemStacksStack(kind, stack)) { total += stack.getCount(); }
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
                if (!ItemHandlerHelper.canItemStacksStack(kind, stack)) { continue; }
                int take = Math.min(owed, stack.getCount());
                owed -= take;
                if (take == stack.getCount()) {
                    mark(def, stack);
                    continue;
                }
                ItemStack part = stack.copy();
                part.setCount(take);
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
            if (marked(dropped, key)) { dropped.removeTagKey(MARK); }
            stack.setCount(0);
            count += dropped.getCount();
            Vec3 feet = Vec3.atBottomCenterOf(at);
            Stacks.dropAt(level, feet.x, feet.y, feet.z, dropped, PICKUP_DELAY);
        }
        if (count == 0) { return; }
        ContentLog.LOGGER.info("Gate {} dropped {} marked item(s) of {} on leaving dimension {}", key, count, player.getName().getString(), def.dimension());
        Says.bar(player, CardIds.GATE_LOOT_LEFT, ChatFormatting.YELLOW, Says.fill(def.loot().message, "%dim%", def.name(), "%count%", Integer.toString(count)));
    }
}
