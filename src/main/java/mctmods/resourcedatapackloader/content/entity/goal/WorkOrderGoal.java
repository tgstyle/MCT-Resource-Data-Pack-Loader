package mctmods.resourcedatapackloader.content.entity.goal;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.def.OrderDef;
import mctmods.resourcedatapackloader.content.entity.OrderBoard;
import mctmods.resourcedatapackloader.content.entity.OrderItems;
import mctmods.resourcedatapackloader.content.entity.WorkOrder;
import mctmods.resourcedatapackloader.util.Walking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

public final class WorkOrderGoal extends Goal {
    private static final int LOOK = 20;
    private static final int SWING = 6;
    private static final int GIVE_UP = 300;
    private static final int HOPS = 3;
    private static final double REACH_SQ = 16.0D;
    private final PathfinderMob mob;
    @Nullable private WorkOrder order;
    @Nullable private BlockPos target;
    private Stage stage = Stage.WORK;
    private String kind = "";
    private int total;
    private int left;
    private int walked;
    private int nextLook;
    private boolean finished;

    public WorkOrderGoal(PathfinderMob mob) {
        this.mob = mob;
        this.nextLook = mob.getId() % LOOK;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean requiresUpdateEveryTick() { return true; }

    @Override public boolean canUse() {
        if (!(mob.level() instanceof ServerLevel) || mob.tickCount < nextLook) { return false; }
        nextLook = mob.tickCount + LOOK;
        order = OrderBoard.claim(mob);
        return order != null;
    }

    @Override public boolean canContinueToUse() { return order != null && !finished && OrderBoard.holds(mob, order); }

    @Override public void start() {
        finished = false;
        target = null;
        total = 0;
        walked = 0;
        mob.setDropChance(EquipmentSlot.MAINHAND, 2.0F);
        mob.setDropChance(EquipmentSlot.OFFHAND, 2.0F);
        stage = order != null && !order.def.deliversToSelf() && OrderItems.carriesAny(mob) ? Stage.DELIVER : Stage.WORK;
    }

    @Override public void stop() {
        if (target != null && total > 0) { mob.level().destroyBlockProgress(mob.getId(), target, -1); }
        if (target != null && order != null && stage == Stage.WORK) { order.giveBack(target); }
        OrderBoard.release(mob, order);
        order = null;
        target = null;
        total = 0;
        mob.getNavigation().stop();
    }

    @Override public void tick() {
        WorkOrder held = order;
        if (held == null) { return; }
        if (mob.level().getGameTime() % LOOK == 0) { OrderBoard.renew(mob, held); }
        if (stage == Stage.DELIVER) { deliver(held); }
        else if (stage == Stage.FETCH) { fetch(held); }
        else { work(held); }
    }

    private void work(WorkOrder held) {
        OrderDef def = held.def;
        if (target == null) {
            target = held.wanting() ? held.take(mob) : null;
            walked = 0;
            total = 0;
            if (target == null) {
                if (!def.deliversToSelf() && OrderItems.carriesAny(mob)) { stage = Stage.DELIVER; }
                else { finished = true; }
                return;
            }
        }
        BlockPos pos = target;
        if (far(pos)) {
            walk(pos);
            return;
        }
        if (def.job() == OrderDef.Job.HAUL) {
            haul(held, pos);
            return;
        }
        BlockState state = mob.level().getBlockState(pos);
        if (total == 0 && !begin(held, pos, state)) { return; }
        mob.getLookControl().setLookAt(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 30.0F, 30.0F);
        if ((total - left) % SWING == 0) { LineCompat.swing(mob); }
        left--;
        mob.level().destroyBlockProgress(mob.getId(), pos, (int) (9.0F * (total - left) / total));
        if (left > 0) { return; }
        mob.level().destroyBlockProgress(mob.getId(), pos, -1);
        harvest(held, pos, state);
    }

    private boolean begin(WorkOrder held, BlockPos pos, BlockState state) {
        if (!OrderItems.wanted(held.def, mob.level(), pos, state)) {
            target = null;
            return false;
        }
        if (!OrderItems.harvestable(mob, state)) {
            held.giveBack(pos);
            target = null;
            if (!held.def.tool().isEmpty() && !OrderItems.isKind(mob.getMainHandItem(), held.def.tool())) {
                kind = held.def.tool();
                stage = Stage.FETCH;
                walked = 0;
            }
            else { giveUp(); }
            return false;
        }
        total = OrderItems.digTicks(mob, pos, state, held.def.speed());
        left = total;
        mob.getNavigation().stop();
        return true;
    }

    private void harvest(WorkOrder held, BlockPos pos, BlockState state) {
        OrderDef def = held.def;
        List<ItemStack> drops = ContentHardness.harvest(mob, pos, mob.getMainHandItem());
        if (def.job() == OrderDef.Job.FARM) { OrderItems.replant(mob.level(), pos, state, drops); }
        int counted = 0;
        boolean full = false;
        for (ItemStack drop : drops) {
            if (drop.isEmpty()) { continue; }
            boolean counts = OrderItems.counts(def, drop);
            int count = drop.getCount();
            int spilled = OrderItems.spilled(mob, drop);
            if (spilled > 0) { full = true; }
            if (counts) { counted += count - spilled; }
        }
        target = null;
        total = 0;
        wear(def);
        if (def.deliversToSelf()) {
            OrderBoard.counted(mob, held, counted);
            if (full) { finished = true; }
        }
        else if (full) { stage = Stage.DELIVER; }
    }

    private void wear(OrderDef def) {
        ItemStack tool = mob.getMainHandItem();
        if (tool.isEmpty() || !tool.isDamageableItem()) { return; }
        String was = def.tool().isEmpty() ? OrderItems.kind(tool) : def.tool();
        tool.hurtAndBreak(1, mob, EquipmentSlot.MAINHAND);
        if (!mob.getMainHandItem().isEmpty()) { return; }
        mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        kind = was;
        stage = Stage.FETCH;
        walked = 0;
    }

    private void haul(WorkOrder held, BlockPos pos) {
        ResourceHandler<ItemResource> from = OrderItems.handler(mob.level(), pos);
        ResourceHandler<ItemResource> own = OrderItems.handler(mob.level(), held.chest);
        target = null;
        if (from == null || own == null) { return; }
        int moved = held.stock ? OrderItems.move(from, OrderItems.carried(mob), _ -> true, OrderBoard.stockWants(held, own), Integer.MAX_VALUE)
                : OrderItems.move(from, OrderItems.carried(mob), stack -> OrderItems.matches(held.def, stack), null, held.shownCount(own));
        if (held.def.deliversToSelf()) { OrderBoard.counted(mob, held, moved); }
        else if (OrderItems.carriesAny(mob)) { stage = Stage.DELIVER; }
    }

    private void deliver(WorkOrder held) {
        if (far(held.chest)) {
            walk(held.chest);
            return;
        }
        unload(held);
        if (OrderItems.carriesAny(mob)) { finished = true; }
        stage = Stage.WORK;
        walked = 0;
    }

    private void unload(WorkOrder held) {
        ResourceHandler<ItemResource> chest = OrderItems.handler(mob.level(), held.chest);
        if (chest == null) {
            finished = true;
            return;
        }
        ResourceHandler<ItemResource> carried = OrderItems.carried(mob);
        int counted = OrderItems.move(carried, chest, stack -> held.stock || OrderItems.counts(held.def, stack), null, Integer.MAX_VALUE);
        OrderItems.move(carried, chest, _ -> true, null, Integer.MAX_VALUE);
        OrderBoard.counted(mob, held, counted);
    }

    private void fetch(WorkOrder held) {
        if (far(held.chest)) {
            walk(held.chest);
            return;
        }
        if (!held.def.deliversToSelf()) { unload(held); }
        ResourceHandler<ItemResource> chest = OrderItems.handler(mob.level(), held.chest);
        if (chest == null) {
            finished = true;
            return;
        }
        for (int slot = 0; slot < chest.size(); slot++) {
            if (!OrderItems.isKind(ItemUtil.getStack(chest, slot), kind)) { continue; }
            ItemStack tool = OrderItems.takeOne(chest, slot);
            if (tool.isEmpty()) { continue; }
            ItemStack old = mob.getMainHandItem();
            if (!old.isEmpty()) { OrderItems.spilled(mob, old); }
            mob.setItemSlot(EquipmentSlot.MAINHAND, tool);
            stage = Stage.WORK;
            walked = 0;
            return;
        }
        giveUp();
    }

    private boolean far(BlockPos pos) { return mob.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > REACH_SQ; }

    private void walk(BlockPos pos) {
        if (++walked > GIVE_UP) {
            if (stage == Stage.WORK) { target = null; }
            else { finished = true; }
            walked = 0;
            return;
        }
        if (walked % 20 != 1) { return; }
        BlockPos to = approach(pos);
        double pace = Walking.pace(mob);
        if (mob.getNavigation().moveTo(to.getX() + 0.5D, to.getY(), to.getZ() + 0.5D, pace)) { return; }
        Vec3 aim = Vec3.atBottomCenterOf(to);
        double floor = Math.min(mob.getY(), to.getY()) - 1.0D;
        for (int tries = 0; tries < HOPS; tries++) {
            Vec3 toward = DefaultRandomPos.getPosTowards(mob, 10, 7, aim, Math.PI / 2.0D);
            if (toward != null && toward.y >= floor && mob.getNavigation().moveTo(toward.x, toward.y, toward.z, pace)) { return; }
        }
    }

    private BlockPos approach(BlockPos pos) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos beside = pos.relative(side);
            if (mob.level().isEmptyBlock(beside)) { return beside; }
        }
        return mob.level().isEmptyBlock(pos.above()) ? pos.above() : pos;
    }

    private void giveUp() {
        finished = true;
        OrderBoard.pause(mob);
    }

    private enum Stage { WORK, DELIVER, FETCH }
}
