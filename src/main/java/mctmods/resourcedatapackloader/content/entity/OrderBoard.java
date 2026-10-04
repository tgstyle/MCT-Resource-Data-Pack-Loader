package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.OrderDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.SavedData;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class OrderBoard extends WorldSavedData {
    private static final String NAME = "rdpl_orders";
    private static final String ORDERS = "Orders";
    private static final String NEXT = "NextId";
    static final String ORDER = "rdplOrder";
    static final String LEASE = "rdplOrderLease";
    private static final String PAUSED = "rdplOrderPaused";
    private static final int PAUSE = 1200;
    private static final int BUDGET = 2048;
    private static final int LEASE_TICKS = 200;
    private static final int CHECK = 20;
    private static final int SPAWN_EVERY = 100;
    private static final int SPAWN_WAIT = 200;
    private static final int SEEK = 32;
    private final List<WorkOrder> orders = new ArrayList<>();
    private int nextId = 1;
    private int turn;

    public OrderBoard(String name) { super(name); }

    public static OrderBoard of(World world) { return SavedData.get(world.getPerWorldStorage(), OrderBoard.class, NAME, OrderBoard::new); }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        orders.clear();
        nextId = Math.max(1, compound.getInteger(NEXT));
        NBTTagList list = compound.getTagList(ORDERS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            WorkOrder order = WorkOrder.load(list.getCompoundTagAt(i));
            if (order != null) { orders.add(order); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (WorkOrder order : orders) { list.appendTag(order.save()); }
        compound.setTag(ORDERS, list);
        compound.setInteger(NEXT, nextId);
        return compound;
    }

    public WorkOrder open(World world, OrderDef def, BlockPos chest, @Nullable BlockPos sign, EntityPlayer by, boolean stock) {
        for (WorkOrder order : orders) {
            if (order.def == def && order.chest.equals(chest) && order.stock == stock) { return order; }
        }
        ScorePlayerTeam side = world.getScoreboard().getPlayersTeam(by.getName());
        WorkOrder order = new WorkOrder(nextId++, def, chest.toImmutable(), sign == null ? null : sign.toImmutable(), side == null ? "" : side.getName(), by.getUniqueID(), stock, def.limit);
        order.idleSince = world.getTotalWorldTime();
        orders.add(order);
        markDirty();
        ContentLog.LOGGER.info("{} opened the {} order {} at the chest at {}, {}, {}{}", by.getName(), def.job.name().toLowerCase(Locale.ROOT), def.name, chest.getX(), chest.getY(), chest.getZ(), side == null ? "" : " for team " + side.getName());
        refresh(world, order, world.getTotalWorldTime());
        return order;
    }

    @Nullable WorkOrder byId(int id) {
        for (WorkOrder order : orders) {
            if (order.id == id) { return order; }
        }
        return null;
    }

    boolean hasChest(BlockPos pos) {
        for (WorkOrder order : orders) {
            if (order.chest.equals(pos)) { return true; }
        }
        return false;
    }

    void cancelAt(BlockPos pos) {
        for (WorkOrder order : new ArrayList<>(orders)) {
            if (pos.equals(order.sign) || pos.equals(order.chest)) { close(order, "its sign or chest is gone"); }
        }
    }

    private void close(WorkOrder order, String why) {
        orders.remove(order);
        markDirty();
        ContentLog.LOGGER.info("The {} order {} at {}, {}, {} closes: {}", order.def.job.name().toLowerCase(Locale.ROOT), order.def.name, order.chest.getX(), order.chest.getY(), order.chest.getZ(), why);
    }

    public void tick(World world) {
        if (orders.isEmpty()) { return; }
        int budget = BUDGET;
        int size = orders.size();
        for (int i = 0; i < size && budget > 0; i++) { budget -= orders.get((turn + i) % size).fill(world, budget); }
        turn = (turn + 1) % size;
        long now = world.getTotalWorldTime();
        if (now % CHECK != 0) { return; }
        for (WorkOrder order : new ArrayList<>(orders)) { refresh(world, order, now); }
    }

    private void refresh(World world, WorkOrder order, long now) {
        if (!world.isBlockLoaded(order.chest)) { return; }
        IItemHandler items = OrderItems.handler(world, order.chest);
        if (items == null) {
            close(order, "its chest is gone");
            return;
        }
        if (order.sign != null && world.isBlockLoaded(order.sign) && !(world.getTileEntity(order.sign) instanceof TileEntitySign)) {
            close(order, "its sign is gone");
            return;
        }
        int count = order.shownCount(items);
        order.wanting = count > 0;
        if (count != order.shown) {
            order.shown = count;
            show(world, order, count);
        }
        if (order.done()) {
            close(order, "it is filled");
            return;
        }
        if (order.working(now) > 0) { order.idleSince = now; }
        else if (order.wanting() && now - order.idleSince >= SPAWN_WAIT && now % SPAWN_EVERY == 0) { spawn(world, order); }
    }

    public static void counted(EntityLiving mob, WorkOrder order, int count) {
        if (count <= 0) { return; }
        OrderBoard board = of(mob.world);
        order.delivered(count);
        board.markDirty();
        board.refresh(mob.world, order, mob.world.getTotalWorldTime());
    }

    public static List<ItemStack> stockWants(WorkOrder order, IItemHandler chest) { return order.stockWants(chest); }

    public static void pause(EntityLiving mob) { mob.getEntityData().setLong(PAUSED, mob.world.getTotalWorldTime() + PAUSE); }

    @Nullable public static WorkOrder claim(EntityLiving mob) {
        long now = mob.world.getTotalWorldTime();
        if (mob.getEntityData().getLong(PAUSED) > now) { return null; }
        OrderBoard board = of(mob.world);
        WorkOrder found = board.resume(mob, now);
        if (found == null) { found = board.best(mob, now); }
        if (found != null) { board.lease(mob, found, now); }
        return found;
    }

    public static void renew(EntityLiving mob, WorkOrder order) { of(mob.world).lease(mob, order, mob.world.getTotalWorldTime()); }

    public static boolean holds(EntityLiving mob, WorkOrder order) {
        Long until = order.leases.get(mob.getUniqueID());
        return of(mob.world).orders.contains(order) && until != null && until >= mob.world.getTotalWorldTime();
    }

    private static void show(World world, WorkOrder order, int count) {
        if (order.sign == null || !world.isBlockLoaded(order.sign)) { return; }
        TileEntity tile = world.getTileEntity(order.sign);
        if (!(tile instanceof TileEntitySign)) { return; }
        ((TileEntitySign) tile).signText[1] = new TextComponentString(String.valueOf(count));
        tile.markDirty();
        IBlockState state = world.getBlockState(order.sign);
        world.notifyBlockUpdate(order.sign, state, state, 3);
    }

    @Nullable WorkOrder best(EntityLiving mob, long now) {
        WorkOrder best = null;
        double bestDistance = 0.0D;
        for (WorkOrder order : orders) {
            if (!order.wanting() || order.working(now) >= order.def.workers || !ContentOrders.serves(mob, order)) { continue; }
            double distance = mob.getDistanceSq(order.chest);
            int seek = order.def.widest() + SEEK;
            if (distance > (double) seek * seek) { continue; }
            if (best == null || order.def.priority > best.def.priority || order.def.priority == best.def.priority && distance < bestDistance) {
                best = order;
                bestDistance = distance;
            }
        }
        return best;
    }

    @Nullable WorkOrder resume(EntityLiving mob, long now) {
        NBTTagCompound kept = mob.getEntityData();
        if (!kept.hasKey(ORDER)) { return null; }
        WorkOrder order = byId(kept.getInteger(ORDER));
        boolean held = order != null && order.leases.containsKey(mob.getUniqueID());
        if (order != null && order.wanting() && ContentOrders.serves(mob, order) && (held || order.working(now) < order.def.workers)) { return order; }
        release(mob, order);
        return null;
    }

    void lease(EntityLiving mob, WorkOrder order, long now) {
        order.leases.put(mob.getUniqueID(), now + LEASE_TICKS);
        order.idleSince = now;
        NBTTagCompound kept = mob.getEntityData();
        kept.setInteger(ORDER, order.id);
        kept.setLong(LEASE, now + LEASE_TICKS);
        if (order.team.isEmpty() && order.owner != null && ContentOrders.boss(mob) == null) { ContentOrders.bind(mob, order.owner); }
    }

    public static void release(EntityLiving mob, @Nullable WorkOrder order) {
        if (order != null) { order.leases.remove(mob.getUniqueID()); }
        if (order != null && mob.getEntityData().getInteger(ORDER) != order.id) { return; }
        mob.getEntityData().removeTag(ORDER);
        mob.getEntityData().removeTag(LEASE);
    }

    private void spawn(World world, WorkOrder order) {
        if (order.spawned >= ContentOrders.spawnCap()) { return; }
        BlockPos at = standing(world, order.chest);
        if (at == null) { return; }
        for (String taker : order.def.takers) {
            if (ContentOrders.grouped(taker)) { continue; }
            Entity made = EntityList.createEntityByIDFromName(new ResourceLocation(taker), world);
            if (!(made instanceof EntityLiving)) { continue; }
            EntityLiving living = (EntityLiving) made;
            living.setLocationAndAngles(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
            living.onInitialSpawn(world.getDifficultyForLocation(at), null);
            living.enablePersistence();
            if (!order.team.isEmpty() && world.getScoreboard().getTeamNames().contains(order.team)) { world.getScoreboard().addPlayerToTeam(living.getCachedUniqueIdString(), order.team); }
            else if (order.owner != null) { ContentOrders.bind(living, order.owner); }
            world.spawnEntity(living);
            order.spawned++;
            markDirty();
            ContentLog.LOGGER.info("No worker came for the order {} at {}, {}, {}, so a {} is spawned at its chest ({} of {})", order.def.name, order.chest.getX(), order.chest.getY(), order.chest.getZ(), taker, order.spawned, ContentOrders.spawnCap());
            return;
        }
    }

    @Nullable private static BlockPos standing(World world, BlockPos chest) {
        if (world.isAirBlock(chest.up()) && world.isAirBlock(chest.up(2))) { return chest.up(); }
        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
            BlockPos pos = chest.offset(side);
            if (world.isAirBlock(pos) && world.isAirBlock(pos.up()) && world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP)) { return pos; }
        }
        return null;
    }
}
