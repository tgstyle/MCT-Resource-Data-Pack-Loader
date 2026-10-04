package mctmods.resourcedatapackloader.content.entity.ai;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.def.OrderDef;
import mctmods.resourcedatapackloader.content.entity.OrderBoard;
import mctmods.resourcedatapackloader.content.entity.OrderItems;
import mctmods.resourcedatapackloader.content.entity.WorkOrder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.IItemHandler;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class EntityAIWorkOrder extends EntityAIBase {
    private static final int LOOK = 20;
    private static final int SWING = 6;
    private static final int GIVE_UP = 300;
    private static final int HOPS = 3;
    private static final double REACH_SQ = 16.0D;
    private final EntityCreature mob;
    @Nullable private WorkOrder order;
    @Nullable private BlockPos target;
    private Stage stage = Stage.WORK;
    private String kind = "";
    private int total;
    private int left;
    private int walked;
    private boolean finished;

    public EntityAIWorkOrder(EntityCreature mob) {
        this.mob = mob;
        setMutexBits(3);
    }

    @Override public boolean shouldExecute() {
        if (mob.world.isRemote || (mob.ticksExisted + mob.getEntityId()) % LOOK != 0) { return false; }
        order = OrderBoard.claim(mob);
        return order != null;
    }

    @Override public boolean shouldContinueExecuting() { return order != null && !finished && OrderBoard.holds(mob, order); }

    @Override public void startExecuting() {
        finished = false;
        target = null;
        total = 0;
        walked = 0;
        mob.setDropChance(EntityEquipmentSlot.MAINHAND, 2.0F);
        mob.setDropChance(EntityEquipmentSlot.OFFHAND, 2.0F);
        stage = order != null && !order.def.deliversToSelf && OrderItems.carriesAny(mob) ? Stage.DELIVER : Stage.WORK;
    }

    @Override public void resetTask() {
        if (target != null && total > 0) { mob.world.sendBlockBreakProgress(mob.getEntityId(), target, -1); }
        if (target != null && order != null && stage == Stage.WORK) { order.giveBack(target); }
        OrderBoard.release(mob, order);
        order = null;
        target = null;
        total = 0;
        mob.getNavigator().clearPath();
    }

    @Override public void updateTask() {
        WorkOrder held = order;
        if (held == null) { return; }
        if (mob.world.getTotalWorldTime() % LOOK == 0) { OrderBoard.renew(mob, held); }
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
                if (!def.deliversToSelf && OrderItems.carriesAny(mob)) { stage = Stage.DELIVER; }
                else { finished = true; }
                return;
            }
        }
        BlockPos pos = target;
        if (far(pos)) {
            walk(pos);
            return;
        }
        if (def.job == OrderDef.Job.HAUL) {
            haul(held, pos);
            return;
        }
        IBlockState state = mob.world.getBlockState(pos);
        if (total == 0 && !begin(held, pos, state)) { return; }
        mob.getLookHelper().setLookPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 30.0F, 30.0F);
        if ((total - left) % SWING == 0) { mob.swingArm(EnumHand.MAIN_HAND); }
        left--;
        mob.world.sendBlockBreakProgress(mob.getEntityId(), pos, (int) (9.0F * (total - left) / total));
        if (left > 0) { return; }
        mob.world.sendBlockBreakProgress(mob.getEntityId(), pos, -1);
        harvest(held, pos, state);
    }

    private boolean begin(WorkOrder held, BlockPos pos, IBlockState state) {
        if (!OrderItems.wanted(held.def, mob.world, pos, state)) {
            target = null;
            return false;
        }
        if (!OrderItems.harvestable(mob, pos, state)) {
            held.giveBack(pos);
            target = null;
            if (!held.def.tool.isEmpty() && !OrderItems.isKind(mob.getHeldItemMainhand(), held.def.tool)) {
                kind = held.def.tool;
                stage = Stage.FETCH;
                walked = 0;
            }
            else { stop(); }
            return false;
        }
        total = OrderItems.digTicks(mob, pos, state, held.def.speed);
        left = total;
        mob.getNavigator().clearPath();
        return true;
    }

    private void harvest(WorkOrder held, BlockPos pos, IBlockState state) {
        OrderDef def = held.def;
        List<ItemStack> drops = ContentHardness.harvest(mob, pos, OrderItems.fortune(mob));
        if (def.job == OrderDef.Job.FARM) { OrderItems.replant(mob.world, pos, state, drops); }
        int counted = 0;
        boolean full = false;
        for (ItemStack drop : drops) {
            if (drop.isEmpty()) { continue; }
            boolean counts = OrderItems.matches(def, drop);
            int count = drop.getCount();
            int spilled = OrderItems.spilled(mob, drop);
            if (spilled > 0) { full = true; }
            if (counts) { counted += count - spilled; }
        }
        target = null;
        total = 0;
        wear(def);
        if (def.deliversToSelf) {
            OrderBoard.counted(mob, held, counted);
            if (full) { finished = true; }
        }
        else if (full) { stage = Stage.DELIVER; }
    }

    private void wear(OrderDef def) {
        ItemStack tool = mob.getHeldItemMainhand();
        if (tool.isEmpty() || !tool.isItemStackDamageable()) { return; }
        String was = def.tool.isEmpty() ? first(tool) : def.tool;
        tool.damageItem(1, mob);
        if (!mob.getHeldItemMainhand().isEmpty()) { return; }
        mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
        kind = was;
        stage = Stage.FETCH;
        walked = 0;
    }

    private static String first(ItemStack tool) {
        Set<String> kinds = tool.getItem().getToolClasses(tool);
        return kinds.isEmpty() ? "" : kinds.iterator().next();
    }

    private void haul(WorkOrder held, BlockPos pos) {
        IItemHandler from = OrderItems.handler(mob.world, pos);
        IItemHandler own = OrderItems.handler(mob.world, held.chest);
        target = null;
        if (from == null || own == null) { return; }
        int moved = held.stock ? OrderItems.move(from, OrderItems.carried(mob), stack -> true, OrderBoard.stockWants(held, own), Integer.MAX_VALUE, mob)
                : OrderItems.move(from, OrderItems.carried(mob), stack -> OrderItems.matches(held.def, stack), null, held.shownCount(own), mob);
        if (held.def.deliversToSelf) { OrderBoard.counted(mob, held, moved); }
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
        IItemHandler chest = OrderItems.handler(mob.world, held.chest);
        if (chest == null) {
            finished = true;
            return;
        }
        IItemHandler carried = OrderItems.carried(mob);
        int counted = OrderItems.move(carried, chest, stack -> held.stock || OrderItems.matches(held.def, stack), null, Integer.MAX_VALUE, mob);
        OrderItems.move(carried, chest, stack -> true, null, Integer.MAX_VALUE, mob);
        OrderBoard.counted(mob, held, counted);
    }

    private void fetch(WorkOrder held) {
        if (far(held.chest)) {
            walk(held.chest);
            return;
        }
        if (!held.def.deliversToSelf) { unload(held); }
        IItemHandler chest = OrderItems.handler(mob.world, held.chest);
        if (chest == null) {
            finished = true;
            return;
        }
        for (int slot = 0; slot < chest.getSlots(); slot++) {
            if (!OrderItems.isKind(chest.getStackInSlot(slot), kind)) { continue; }
            ItemStack tool = chest.extractItem(slot, 1, false);
            if (tool.isEmpty()) { continue; }
            ItemStack old = mob.getHeldItemMainhand();
            if (!old.isEmpty()) { OrderItems.spilled(mob, old); }
            mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, tool);
            stage = Stage.WORK;
            walked = 0;
            return;
        }
        stop();
    }

    private boolean far(BlockPos pos) { return mob.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > REACH_SQ; }

    private void walk(BlockPos pos) {
        if (++walked > GIVE_UP) {
            if (stage == Stage.WORK) { target = null; }
            else { finished = true; }
            walked = 0;
            return;
        }
        if (walked % 20 != 1) { return; }
        BlockPos to = approach(pos);
        if (mob.getNavigator().tryMoveToXYZ(to.getX() + 0.5D, to.getY(), to.getZ() + 0.5D, 1.0D)) { return; }
        Vec3d aim = new Vec3d(to.getX() + 0.5D, to.getY(), to.getZ() + 0.5D);
        double floor = Math.min(mob.posY, to.getY()) - 1.0D;
        for (int tries = 0; tries < HOPS; tries++) {
            Vec3d toward = RandomPositionGenerator.findRandomTargetBlockTowards(mob, 10, 7, aim);
            if (toward != null && toward.y >= floor && mob.getNavigator().tryMoveToXYZ(toward.x, toward.y, toward.z, 1.0D)) { return; }
        }
    }

    private BlockPos approach(BlockPos pos) {
        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
            BlockPos beside = pos.offset(side);
            if (mob.world.isAirBlock(beside)) { return beside; }
        }
        return mob.world.isAirBlock(pos.up()) ? pos.up() : pos;
    }

    private void stop() {
        finished = true;
        OrderBoard.pause(mob);
    }

    private enum Stage { WORK, DELIVER, FETCH }
}
