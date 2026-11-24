package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.cards.CardIds;
import mctmods.resourcedatapackloader.common.loot.PlayerLoot;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.util.ItemHandlers;
import mctmods.resourcedatapackloader.common.util.Nbts;
import mctmods.resourcedatapackloader.common.util.PlayerLogout;
import mctmods.resourcedatapackloader.common.util.entity.Players;
import mctmods.resourcedatapackloader.common.util.entity.Walking;
import mctmods.resourcedatapackloader.common.util.entity.Worn;
import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

public final class GateLoot {
    public static final GateLoot NONE = new GateLoot(false, Collections.emptyList(), true, "");
    private static final String DROP_ON_LEAVE = "dropOnLeave";
    private static final String MARK = "rdplLoot";
    private static final String BLOCK_ENTITY = "BlockEntityTag";
    private static final int PICKUP_DELAY = 40;
    private static final Map<UUID, BlockPos> POSITIONS = PlayerLogout.track(new HashMap<>());
    private static final Map<UUID, List<ItemStack>> OPEN = PlayerLogout.track(new HashMap<>());
    private static final Map<UUID, List<ItemStack>> OWN = PlayerLogout.track(new HashMap<>());
    private static final List<String> LOOT_KEYS = Collections.unmodifiableList(Arrays.asList("keep", "enderChest", "message"));

    private final boolean enabled;
    private final List<ItemStack> keep;
    private final boolean enderChest;
    private final String message;

    private GateLoot(boolean enabled, List<ItemStack> keep, boolean enderChest, String message) {
        this.enabled = enabled;
        this.keep = keep;
        this.enderChest = enderChest;
        this.message = message;
    }

    public static GateLoot parse(JsonObject json, ResourceLocation key, int dimension) {
        if (!json.has(DROP_ON_LEAVE)) { return NONE; }
        JsonElement element = json.get(DROP_ON_LEAVE);
        if (!element.isJsonObject() && !Json.getBoolean(json, DROP_ON_LEAVE, false)) { return NONE; }
        if (GateDef.dimensionMissing(dimension, key, "dropOnLeave", "nothing is marked")) { return NONE; }
        JsonObject options = element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        Json.unknownKeys(options, "dropOnLeave", LOOT_KEYS);
        List<ItemStack> keep = new ArrayList<>();
        for (String named : Json.strings(options, "keep")) {
            String[] parts = named.split(":");
            Item item = parts.length < 2 ? null : Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
            if (item == null) { ContentLog.LOGGER.warn("Gate {} lists keep item {}, which is not a known item, so it is ignored", key, named); }
            else { keep.add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE)); }
        }
        return new GateLoot(true, keep, Json.getBoolean(options, "enderChest", true), Json.getString(options, "message", "Loot from %dim% stays behind"));
    }

    public boolean active() { return enabled; }

    public static boolean stays(EntityPlayer player, ItemStack stack) {
        GateDef def = ContentGates.lootDimension(player.dimension);
        return def != null && (marked(stack, def.getKey()) || holds(stack, def.getKey()));
    }

    private boolean keeps(ItemStack stack) {
        for (ItemStack wanted : keep) {
            if (Stacks.matches(wanted, stack)) { return true; }
        }
        return false;
    }

    static void track(MinecraftServer server) {
        for (EntityPlayerMP player : Players.online(server)) {
            if (ContentGates.lootDimension(player.dimension) == null) {
                OWN.remove(player.getUniqueID());
                continue;
            }
            POSITIONS.put(player.getUniqueID(), player.getPosition());
            if (player.openContainer == player.inventoryContainer) { snapshot(player); }
        }
    }

    private static List<ItemStack> copies(EntityPlayer player) {
        List<ItemStack> copies = new ArrayList<>();
        for (ItemStack stack : Stacks.held(player)) { copies.add(stack.copy()); }
        return copies;
    }

    private static void snapshot(EntityPlayer player) { OWN.put(player.getUniqueID(), copies(player)); }

    @SubscribeEvent public static void onPickup(EntityItemPickupEvent event) {
        GateDef def = ContentGates.lootDimension(event.getEntityPlayer().dimension);
        if (def != null) { mark(def, event.getItem().getItem()); }
    }

    @SubscribeEvent public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        GateDef def = ContentGates.lootDimension(event.player.dimension);
        if (def == null || event.player.world.isRemote) { return; }
        mark(def, event.crafting);
        EntityPlayer player = event.player;
        List<ItemStack> before = OWN.get(player.getUniqueID());
        if (before == null || player.openContainer != player.inventoryContainer) { return; }
        markTaken(player, def, before);
        snapshot(player);
    }

    @SubscribeEvent public static void onSmelted(PlayerEvent.ItemSmeltedEvent event) {
        GateDef def = ContentGates.lootDimension(event.player.dimension);
        if (def != null && !event.player.world.isRemote) { mark(def, event.smelting); }
    }

    @SubscribeEvent public static void onOpen(PlayerContainerEvent.Open event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote || ContentGates.lootDimension(player.dimension) == null) { return; }
        OPEN.put(player.getUniqueID(), copies(player));
    }

    @SubscribeEvent public static void onClose(PlayerContainerEvent.Close event) {
        EntityPlayer player = event.getEntityPlayer();
        List<ItemStack> before = OPEN.remove(player.getUniqueID());
        GateDef def = ContentGates.lootDimension(player.dimension);
        if (before == null || def == null) { return; }
        markTaken(player, def, before);
        snapshot(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onTravel(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        GateDef def = ContentGates.lootDimension(entity.dimension);
        if (def == null) { return; }
        EntityPlayerMP player = Players.serverPlayer(entity);
        if (player != null) { drop(player, def, entity.world, entity.getPosition(), def.loot.enderChest); }
        else if (carriesMarked(entity, def.getKey())) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onArrive(PlayerEvent.PlayerChangedDimensionEvent event) {
        GateDef def = ContentGates.lootDimension(event.fromDim);
        BlockPos last = POSITIONS.get(event.player.getUniqueID());
        if (ContentGates.lootDimension(event.toDim) != null) { snapshot(event.player); }
        World from = DimensionManager.getWorld(event.fromDim);
        EntityPlayerMP player = Players.serverPlayer(event.player);
        if (def != null && last != null && from != null && player != null) { drop(player, def, from, last, def.loot.enderChest); }
    }

    @SubscribeEvent public static void onDeath(LivingDeathEvent event) {
        EntityPlayerMP player = Players.serverPlayer(event.getEntity());
        if (player == null) { return; }
        GateDef def = ContentGates.lootDimension(player.dimension);
        if (def != null && PlayerLoot.keepInventory(player.world)) { drop(player, def, player.world, player.getPosition(), false); }
    }

    private static void mark(GateDef def, ItemStack stack) {
        if (stack.isEmpty() || def.loot.keeps(stack)) { return; }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setString(MARK, def.getKey());
    }

    private static boolean marked(ItemStack stack, String key) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && key.equals(tag.getString(MARK));
    }

    private static boolean holds(ItemStack stack, String key) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(BLOCK_ENTITY, Constants.NBT.TAG_COMPOUND)) {
            NBTTagList items = tag.getCompoundTag(BLOCK_ENTITY).getTagList("Items", Constants.NBT.TAG_COMPOUND);
            for (int index = 0; index < items.tagCount(); index++) {
                if (key.equals(items.getCompoundTagAt(index).getCompoundTag(Nbts.STACK_TAG).getString(MARK))) { return true; }
            }
        }
        IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (handler == null) { return false; }
        return ItemHandlers.any(handler, held -> marked(held, key));
    }

    private static boolean carriesMarked(Entity entity, String key) {
        if (entity instanceof EntityItem) { return marked(((EntityItem) entity).getItem(), key); }
        if (!(entity instanceof IInventory)) { return false; }
        for (ItemStack stack : contents((IInventory) entity)) {
            if (marked(stack, key) || holds(stack, key)) { return true; }
        }
        return false;
    }

    private static List<ItemStack> contents(IInventory inventory) {
        List<ItemStack> all = new ArrayList<>();
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) { all.add(inventory.getStackInSlot(slot)); }
        return all;
    }

    private static List<ItemStack> carried(EntityPlayer player, boolean enderChest) {
        List<ItemStack> all = Stacks.held(player);
        if (enderChest) { all.addAll(contents(player.getInventoryEnderChest())); }
        Entity vehicle = player.getRidingEntity();
        if (vehicle instanceof IInventory) { all.addAll(contents((IInventory) vehicle)); }
        all.addAll(Worn.baubles(player));
        return all;
    }

    private static int total(List<ItemStack> stacks, ItemStack kind) {
        int total = 0;
        for (ItemStack stack : stacks) {
            if (ItemHandlerHelper.canItemStacksStack(kind, stack)) { total += stack.getCount(); }
        }
        return total;
    }

    private static void markTaken(EntityPlayer player, GateDef def, List<ItemStack> before) {
        List<ItemStack> now = Stacks.held(player);
        List<ItemStack> done = new ArrayList<>();
        List<ItemStack> split = new ArrayList<>();
        for (ItemStack kind : now) {
            if (marked(kind, def.getKey()) || def.loot.keeps(kind) || total(done, kind) > 0) { continue; }
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

    private static void drop(EntityPlayerMP player, GateDef def, World world, BlockPos at, boolean enderChest) {
        String key = def.getKey();
        int count = 0;
        for (ItemStack stack : carried(player, enderChest)) {
            if (stack.isEmpty() || !marked(stack, key) && !holds(stack, key)) { continue; }
            ItemStack dropped = stack.copy();
            NBTTagCompound tag = dropped.getTagCompound();
            if (tag != null && marked(dropped, key)) {
                tag.removeTag(MARK);
                if (tag.isEmpty()) { dropped.setTagCompound(null); }
            }
            stack.setCount(0);
            count += dropped.getCount();
            Vec3d feet = Walking.feet(at);
            Stacks.dropAt(world, feet.x, feet.y, feet.z, dropped, PICKUP_DELAY);
        }
        if (count == 0) { return; }
        ContentLog.LOGGER.info("Gate {} dropped {} marked item(s) of {} on leaving dimension {}", key, count, player.getName(), def.dimension);
        Says.bar(player, CardIds.GATE_LOOT_LEFT, TextFormatting.YELLOW, Says.fill(def.loot.message, "%dim%", def.name, "%count%", Integer.toString(count)));
    }
}
