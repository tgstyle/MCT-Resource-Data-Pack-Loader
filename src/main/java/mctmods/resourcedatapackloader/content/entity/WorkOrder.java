package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.OrderDef;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
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

    NBTTagCompound save() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Id", id);
        tag.setString("Def", def.name);
        tag.setTag("Chest", NBTUtil.createPosTag(chest));
        if (sign != null) { tag.setTag("Sign", NBTUtil.createPosTag(sign)); }
        tag.setString("Team", team);
        if (owner != null) { tag.setUniqueId("Owner", owner); }
        tag.setBoolean("Stock", stock);
        tag.setInteger("Left", left);
        tag.setInteger("Spawned", spawned);
        return tag;
    }

    @Nullable static WorkOrder load(NBTTagCompound tag) {
        OrderDef def = ContentOrders.def(tag.getString("Def"));
        if (def == null) { return null; }
        BlockPos sign = tag.hasKey("Sign") ? NBTUtil.getPosFromTag(tag.getCompoundTag("Sign")) : null;
        UUID owner = tag.hasUniqueId("Owner") ? tag.getUniqueId("Owner") : null;
        WorkOrder order = new WorkOrder(tag.getInteger("Id"), def, NBTUtil.getPosFromTag(tag.getCompoundTag("Chest")), sign, tag.getString("Team"), owner, tag.getBoolean("Stock"), tag.getInteger("Left"));
        order.spawned = tag.getInteger("Spawned");
        return order;
    }

    public boolean standing() { return stock || def.standing > 0; }

    public int shownCount(IItemHandler chestItems) {
        if (stock) {
            int missing = 0;
            for (ItemStack want : stockWants(chestItems)) { missing += want.getCount(); }
            return missing;
        }
        if (def.standing > 0) { return Math.max(0, def.standing - OrderItems.count(def, chestItems)); }
        return left;
    }

    List<ItemStack> stockWants(IItemHandler chestItems) {
        List<ItemStack> wants = new ArrayList<>();
        for (int slot = 0; slot < Math.min(STOCK_ROW, chestItems.getSlots()); slot++) {
            ItemStack held = chestItems.getStackInSlot(slot);
            if (held.isEmpty() || held.getCount() >= held.getMaxStackSize()) { continue; }
            wants.add(ItemHandlerHelper.copyStackWithSize(held, held.getMaxStackSize() - held.getCount()));
        }
        return wants;
    }

    public void delivered(int count) {
        if (!standing() && count > 0) { left = Math.max(0, left - count); }
    }

    public boolean done() { return !standing() && left <= 0; }

    public boolean wanting() { return wanting && !done(); }

    private boolean beyond(BlockPos pos, int radius) { return Math.abs(pos.getX() - chest.getX()) > radius || Math.abs(pos.getZ() - chest.getZ()) > radius || Math.abs(pos.getY() - chest.getY()) > radius; }

    @Nullable public BlockPos take(EntityLiving mob) {
        int radius = def.reach(OrderItems.tier(mob.getHeldItemMainhand()));
        Iterator<BlockPos> walk = queue.iterator();
        while (walk.hasNext()) {
            BlockPos pos = walk.next();
            if (beyond(pos, radius)) { continue; }
            walk.remove();
            if (still(mob.world, pos)) { return pos; }
        }
        return null;
    }

    public void giveBack(BlockPos pos) {
        if (queue.size() < QUEUE) { queue.addLast(pos); }
    }

    private boolean still(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) { return false; }
        if (def.job != OrderDef.Job.HAUL) { return OrderItems.wanted(def, world, pos, world.getBlockState(pos)); }
        return OrderItems.handler(world, pos) != null;
    }

    int fill(World world, int budget) {
        if (!wanting() || queue.size() >= QUEUE || world.getTotalWorldTime() < restUntil) { return 0; }
        if (def.job == OrderDef.Job.HAUL) {
            fillContainers(world);
            restUntil = world.getTotalWorldTime() + REST / 2;
            return budget / 4;
        }
        int radius = def.widest();
        int side = radius * 2 + 1;
        int low = Math.max(0, chest.getY() - radius);
        int high = Math.min(world.getHeight() - 1, chest.getY() + radius);
        int tall = high - low + 1;
        int volume = side * side * tall;
        int used = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        while (used < budget && queue.size() < QUEUE) {
            if (cursor >= volume) {
                cursor = 0;
                restUntil = world.getTotalWorldTime() + REST;
                break;
            }
            int index = cursor++;
            used++;
            at.setPos(chest.getX() - radius + index % side, high - index / (side * side), chest.getZ() - radius + index / side % side);
            if (!world.isBlockLoaded(at)) { continue; }
            if (OrderItems.wanted(def, world, at, world.getBlockState(at)) && !queue.contains(at)) { queue.addLast(at.toImmutable()); }
        }
        return used;
    }

    private void fillContainers(World world) {
        IItemHandler own = OrderItems.handler(world, chest);
        if (own == null) { return; }
        List<ItemStack> wants = stock ? stockWants(own) : null;
        if (wants != null && wants.isEmpty()) { return; }
        int radius = def.widest();
        for (int cx = chest.getX() - radius >> 4; cx <= chest.getX() + radius >> 4; cx++) {
            for (int cz = chest.getZ() - radius >> 4; cz <= chest.getZ() + radius >> 4; cz++) {
                if (!world.isBlockLoaded(new ChunkPos(cx, cz).getBlock(8, 0, 8))) { continue; }
                Chunk chunk = world.getChunk(cx, cz);
                for (TileEntity tile : chunk.getTileEntityMap().values()) {
                    BlockPos pos = tile.getPos();
                    if (beyond(pos, radius) || ContentOrders.isOrderChest(world, pos) || queue.contains(pos)) { continue; }
                    IItemHandler items = OrderItems.handler(world, pos);
                    if (items != null && holds(items, wants)) { queue.addLast(pos); }
                }
            }
        }
    }

    private boolean holds(IItemHandler items, @Nullable List<ItemStack> wants) {
        for (int slot = 0; slot < items.getSlots(); slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (stack.isEmpty()) { continue; }
            if (wants == null && OrderItems.matches(def, stack)) { return true; }
            if (wants == null) { continue; }
            for (ItemStack want : wants) {
                if (ItemHandlerHelper.canItemStacksStack(want, stack)) { return true; }
            }
        }
        return false;
    }

    int working(long now) {
        leases.values().removeIf(until -> until < now);
        return leases.size();
    }
}
