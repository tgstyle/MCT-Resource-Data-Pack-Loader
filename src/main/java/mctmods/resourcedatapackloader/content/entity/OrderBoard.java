package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.def.OrderDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class OrderBoard extends SavedData {
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

    public static OrderBoard of(ServerLevel level) { return level.getDataStorage().computeIfAbsent(OrderBoard::read, OrderBoard::new, NAME); }

    private static OrderBoard of(Mob mob) { return of((ServerLevel) mob.level()); }

    private static OrderBoard read(CompoundTag compound) {
        OrderBoard board = new OrderBoard();
        board.nextId = Math.max(1, compound.getInt(NEXT));
        ListTag list = compound.getList(ORDERS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            WorkOrder order = WorkOrder.load(list.getCompound(i));
            if (order != null) { board.orders.add(order); }
        }
        return board;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag compound) {
        ListTag list = new ListTag();
        for (WorkOrder order : orders) { list.add(order.save()); }
        compound.put(ORDERS, list);
        compound.putInt(NEXT, nextId);
        return compound;
    }

    public WorkOrder open(ServerLevel level, OrderDef def, BlockPos chest, @Nullable BlockPos sign, Player by, boolean stock) {
        for (WorkOrder order : orders) {
            if (order.def == def && order.chest.equals(chest) && order.stock == stock) { return order; }
        }
        PlayerTeam side = level.getScoreboard().getPlayersTeam(by.getScoreboardName());
        WorkOrder order = new WorkOrder(nextId++, def, chest.immutable(), sign == null ? null : sign.immutable(), side == null ? "" : side.getName(), by.getUUID(), stock, def.limit());
        order.idleSince = level.getGameTime();
        orders.add(order);
        setDirty();
        ContentLog.LOGGER.info("{} opened the {} order {} at the chest at {}, {}, {}{}", by.getName().getString(), def.job().name().toLowerCase(Locale.ROOT), def.name(), chest.getX(), chest.getY(), chest.getZ(), side == null ? "" : " for team " + side.getName());
        refresh(level, order, level.getGameTime());
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
        setDirty();
        ContentLog.LOGGER.info("The {} order {} at {}, {}, {} closes: {}", order.def.job().name().toLowerCase(Locale.ROOT), order.def.name(), order.chest.getX(), order.chest.getY(), order.chest.getZ(), why);
    }

    public void tick(ServerLevel level) {
        if (orders.isEmpty()) { return; }
        int budget = BUDGET;
        int size = orders.size();
        for (int i = 0; i < size && budget > 0; i++) { budget -= orders.get((turn + i) % size).fill(level, budget); }
        turn = (turn + 1) % size;
        long now = level.getGameTime();
        if (now % CHECK != 0) { return; }
        for (WorkOrder order : new ArrayList<>(orders)) { refresh(level, order, now); }
    }

    private void refresh(ServerLevel level, WorkOrder order, long now) {
        if (!level.isLoaded(order.chest)) { return; }
        IItemHandler items = OrderItems.handler(level, order.chest);
        if (items == null) {
            close(order, "its chest is gone");
            return;
        }
        if (order.sign != null && level.isLoaded(order.sign) && !(level.getBlockEntity(order.sign) instanceof SignBlockEntity)) {
            close(order, "its sign is gone");
            return;
        }
        int count = order.shownCount(items);
        order.wanting = count > 0;
        if (count != order.shown) {
            order.shown = count;
            show(level, order, count);
        }
        if (order.done()) {
            close(order, "it is filled");
            return;
        }
        if (order.working(now) > 0) { order.idleSince = now; }
        else if (order.wanting() && now - order.idleSince >= SPAWN_WAIT && now % SPAWN_EVERY == 0) { spawn(level, order); }
    }

    public static void counted(Mob mob, WorkOrder order, int count) {
        if (count <= 0) { return; }
        OrderBoard board = of(mob);
        order.delivered(count);
        board.setDirty();
        board.refresh((ServerLevel) mob.level(), order, mob.level().getGameTime());
    }

    public static List<ItemStack> stockWants(WorkOrder order, IItemHandler chest) { return order.stockWants(chest); }

    public static void pause(Mob mob) { mob.getPersistentData().putLong(PAUSED, mob.level().getGameTime() + PAUSE); }

    @Nullable public static WorkOrder claim(Mob mob) {
        long now = mob.level().getGameTime();
        if (mob.getPersistentData().getLong(PAUSED) > now) { return null; }
        OrderBoard board = of(mob);
        WorkOrder found = board.resume(mob, now);
        if (found == null) { found = board.best(mob, now); }
        if (found != null) { board.lease(mob, found, now); }
        return found;
    }

    public static void renew(Mob mob, WorkOrder order) { of(mob).lease(mob, order, mob.level().getGameTime()); }

    public static boolean holds(Mob mob, WorkOrder order) {
        Long until = order.leases.get(mob.getUUID());
        return of(mob).orders.contains(order) && until != null && until >= mob.level().getGameTime();
    }

    private static void show(ServerLevel level, WorkOrder order, int count) {
        if (order.sign == null || !level.isLoaded(order.sign)) { return; }
        if (!(level.getBlockEntity(order.sign) instanceof SignBlockEntity sign)) { return; }
        sign.updateText(text -> text.setMessage(1, Component.literal(String.valueOf(count))), true);
    }

    @Nullable WorkOrder best(Mob mob, long now) {
        WorkOrder best = null;
        double bestDistance = 0.0D;
        for (WorkOrder order : orders) {
            if (!order.wanting() || order.working(now) >= order.def.workers() || !ContentOrders.serves(mob, order)) { continue; }
            double distance = mob.distanceToSqr(order.chest.getX(), order.chest.getY(), order.chest.getZ());
            int seek = order.def.widest() + SEEK;
            if (distance > (double) seek * seek) { continue; }
            if (best == null || order.def.priority() > best.def.priority() || order.def.priority() == best.def.priority() && distance < bestDistance) {
                best = order;
                bestDistance = distance;
            }
        }
        return best;
    }

    @Nullable WorkOrder resume(Mob mob, long now) {
        CompoundTag kept = mob.getPersistentData();
        if (!kept.contains(ORDER)) { return null; }
        WorkOrder order = byId(kept.getInt(ORDER));
        boolean held = order != null && order.leases.containsKey(mob.getUUID());
        if (order != null && order.wanting() && ContentOrders.serves(mob, order) && (held || order.working(now) < order.def.workers())) { return order; }
        release(mob, order);
        return null;
    }

    void lease(Mob mob, WorkOrder order, long now) {
        order.leases.put(mob.getUUID(), now + LEASE_TICKS);
        order.idleSince = now;
        CompoundTag kept = mob.getPersistentData();
        kept.putInt(ORDER, order.id);
        kept.putLong(LEASE, now + LEASE_TICKS);
        if (order.team.isEmpty() && order.owner != null && ContentOrders.boss(mob) == null) { ContentOrders.bind(mob, order.owner); }
    }

    public static void release(Mob mob, @Nullable WorkOrder order) {
        if (order != null) { order.leases.remove(mob.getUUID()); }
        if (order != null && mob.getPersistentData().getInt(ORDER) != order.id) { return; }
        mob.getPersistentData().remove(ORDER);
        mob.getPersistentData().remove(LEASE);
    }

    private void spawn(ServerLevel level, WorkOrder order) {
        if (order.spawned >= ContentOrders.spawnCap()) { return; }
        BlockPos at = standing(level, order.chest);
        if (at == null) { return; }
        for (String taker : order.def.takers()) {
            if (ContentOrders.grouped(taker)) { continue; }
            EntityType<?> type = EntityType.byString(taker).orElse(null);
            Entity made = type == null ? null : type.create(level);
            if (!(made instanceof Mob living)) { continue; }
            living.moveTo(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D, level.getRandom().nextFloat() * 360.0F, 0.0F);
            ForgeEventFactory.onFinalizeSpawn(living, level, level.getCurrentDifficultyAt(at), MobSpawnType.EVENT, null, null);
            living.setPersistenceRequired();
            PlayerTeam team = order.team.isEmpty() ? null : level.getScoreboard().getPlayerTeam(order.team);
            if (team != null) { level.getScoreboard().addPlayerToTeam(living.getScoreboardName(), team); }
            else if (order.owner != null) { ContentOrders.bind(living, order.owner); }
            level.addFreshEntity(living);
            order.spawned++;
            setDirty();
            ContentLog.LOGGER.info("No worker came for the order {} at {}, {}, {}, so a {} is spawned at its chest ({} of {})", order.def.name(), order.chest.getX(), order.chest.getY(), order.chest.getZ(), taker, order.spawned, ContentOrders.spawnCap());
            return;
        }
    }

    @Nullable private static BlockPos standing(ServerLevel level, BlockPos chest) {
        if (level.isEmptyBlock(chest.above()) && level.isEmptyBlock(chest.above(2))) { return chest.above(); }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos pos = chest.relative(side);
            if (level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) { return pos; }
        }
        return null;
    }
}
