package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.OrderDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class WorkOrder {
    private static final int QUEUE = 64;
    private static final int REST = 200;
    private static final int STOCK_ROW = 9;
    public final int id;
    public final OrderDef def;
    public final BlockPos chest;
    @Nullable public final BlockPos sign;
    public final String team;
    @Nullable public final UUID owner;
    public final boolean stock;
    int left;
    int spawned;
    long idleSince;
    boolean wanting = true;
    int shown = -1;
    final Map<UUID, Long> leases = new HashMap<>();
    private final Deque<BlockPos> queue = new ArrayDeque<>();
    private int cursor;
    private long restUntil;

    WorkOrder(int id, OrderDef def, BlockPos chest, @Nullable BlockPos sign, String team, @Nullable UUID owner, boolean stock, int left) {
        this.id = id;
        this.def = def;
        this.chest = chest;
        this.sign = sign;
        this.team = team;
        this.owner = owner;
        this.stock = stock;
        this.left = left;
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Id", id);
        tag.putString("Def", def.name());
        tag.store("Chest", BlockPos.CODEC, chest);
        tag.storeNullable("Sign", BlockPos.CODEC, sign);
        tag.putString("Team", team);
        tag.storeNullable("Owner", UUIDUtil.CODEC, owner);
        tag.putBoolean("Stock", stock);
        tag.putInt("Left", left);
        tag.putInt("Spawned", spawned);
        return tag;
    }

    @Nullable static WorkOrder load(CompoundTag tag) {
        OrderDef def = ContentOrders.def(tag.getStringOr("Def", ""));
        BlockPos chest = tag.read("Chest", BlockPos.CODEC).orElse(null);
        if (def == null || chest == null) { return null; }
        BlockPos sign = tag.read("Sign", BlockPos.CODEC).orElse(null);
        UUID owner = tag.read("Owner", UUIDUtil.CODEC).orElse(null);
        WorkOrder order = new WorkOrder(tag.getIntOr("Id", 0), def, chest, sign, tag.getStringOr("Team", ""), owner, tag.getBooleanOr("Stock", false), tag.getIntOr("Left", 0));
        order.spawned = tag.getIntOr("Spawned", 0);
        return order;
    }

    public boolean standing() { return stock || def.standing() > 0; }

    public int shownCount(ResourceHandler<ItemResource> chestItems) {
        if (stock) {
            int missing = 0;
            for (ItemStack want : stockWants(chestItems)) { missing += want.getCount(); }
            return missing;
        }
        if (def.standing() > 0) { return Math.max(0, def.standing() - OrderItems.count(def, chestItems)); }
        return left;
    }

    List<ItemStack> stockWants(ResourceHandler<ItemResource> chestItems) {
        List<ItemStack> wants = new ArrayList<>();
        for (int slot = 0; slot < Math.min(STOCK_ROW, chestItems.size()); slot++) {
            ItemStack held = ItemUtil.getStack(chestItems, slot);
            if (held.isEmpty() || held.getCount() >= held.getMaxStackSize()) { continue; }
            wants.add(held.copyWithCount(held.getMaxStackSize() - held.getCount()));
        }
        return wants;
    }

    public void delivered(int count) {
        if (!standing() && count > 0) { left = Math.max(0, left - count); }
    }

    public boolean done() { return !standing() && left <= 0; }

    public boolean wanting() { return wanting && !done(); }

    private boolean beyond(BlockPos pos, int radius) { return Math.abs(pos.getX() - chest.getX()) > radius || Math.abs(pos.getZ() - chest.getZ()) > radius || Math.abs(pos.getY() - chest.getY()) > radius; }

    @Nullable public BlockPos take(Mob mob) {
        int radius = def.reach(OrderItems.tier(mob.getMainHandItem()));
        Iterator<BlockPos> walk = queue.iterator();
        while (walk.hasNext()) {
            BlockPos pos = walk.next();
            if (beyond(pos, radius)) { continue; }
            walk.remove();
            if (still(mob.level(), pos)) { return pos; }
        }
        return null;
    }

    public void giveBack(BlockPos pos) {
        if (queue.size() < QUEUE) { queue.addLast(pos); }
    }

    private boolean still(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) { return false; }
        if (def.job() != OrderDef.Job.HAUL) { return OrderItems.wanted(def, level, pos, level.getBlockState(pos)); }
        return OrderItems.handler(level, pos) != null;
    }

    int fill(ServerLevel level, int budget) {
        if (!wanting() || queue.size() >= QUEUE || level.getGameTime() < restUntil) { return 0; }
        if (def.job() == OrderDef.Job.HAUL) {
            fillContainers(level);
            restUntil = level.getGameTime() + REST / 2;
            return budget / 4;
        }
        int radius = def.widest();
        int side = radius * 2 + 1;
        int low = Math.max(level.getMinY(), chest.getY() - radius);
        int high = Math.min(level.getMaxY(), chest.getY() + radius);
        int tall = high - low + 1;
        int volume = side * side * tall;
        int used = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        while (used < budget && queue.size() < QUEUE) {
            if (cursor >= volume) {
                cursor = 0;
                restUntil = level.getGameTime() + REST;
                break;
            }
            int index = cursor++;
            used++;
            at.set(chest.getX() - radius + index % side, high - index / (side * side), chest.getZ() - radius + index / side % side);
            if (!level.isLoaded(at)) { continue; }
            if (OrderItems.wanted(def, level, at, level.getBlockState(at)) && !queue.contains(at)) { queue.addLast(at.immutable()); }
        }
        return used;
    }

    private void fillContainers(ServerLevel level) {
        ResourceHandler<ItemResource> own = OrderItems.handler(level, chest);
        if (own == null) { return; }
        List<ItemStack> wants = stock ? stockWants(own) : null;
        if (wants != null && wants.isEmpty()) { return; }
        int radius = def.widest();
        for (int cx = chest.getX() - radius >> 4; cx <= chest.getX() + radius >> 4; cx++) {
            for (int cz = chest.getZ() - radius >> 4; cz <= chest.getZ() + radius >> 4; cz++) {
                if (!level.hasChunk(cx, cz)) { continue; }
                for (BlockEntity tile : level.getChunk(cx, cz).getBlockEntities().values()) {
                    BlockPos pos = tile.getBlockPos();
                    if (beyond(pos, radius) || ContentOrders.isOrderChest(level, pos) || queue.contains(pos)) { continue; }
                    ResourceHandler<ItemResource> items = OrderItems.handler(level, pos);
                    if (items != null && holds(items, wants)) { queue.addLast(pos); }
                }
            }
        }
    }

    private boolean holds(ResourceHandler<ItemResource> items, @Nullable List<ItemStack> wants) {
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = ItemUtil.getStack(items, slot);
            if (stack.isEmpty()) { continue; }
            if (wants == null && OrderItems.matches(def, stack)) { return true; }
            if (wants == null) { continue; }
            for (ItemStack want : wants) {
                if (OrderItems.same(want, stack)) { return true; }
            }
        }
        return false;
    }

    int working(long now) {
        leases.values().removeIf(until -> until < now);
        return leases.size();
    }
}
