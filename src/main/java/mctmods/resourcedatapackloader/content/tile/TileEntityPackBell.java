package mctmods.resourcedatapackloader.content.tile;

import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.block.ContentBlockBell;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityPackBell extends TileEntity implements ITickable {
    private static final int SHAKE_TICKS = 50;
    private static final int RESONATE_AFTER = 5;
    private static final int RESONATE_TICKS = 40;
    private static final int SEARCH_EVERY = 60;
    private static final int SEARCH_RADIUS = 48;
    private static final int HEAR_RADIUS = 32;
    private static final int RESONATE_RADIUS = 32;
    private static final int GLOW_RADIUS = 48;
    private static final int PARTICLE_COLOR = 16700985;
    private static final String POWERED = "RdplPowered";
    private long lastSearch;
    private int ticks;
    private boolean shaking;
    private EnumFacing clicked = EnumFacing.NORTH;
    @Nullable private List<EntityLivingBase> nearby;
    private boolean resonating;
    private int resonateTicks;
    private boolean powered;

    public int ticks() { return ticks; }

    public boolean shaking() { return shaking; }

    public EnumFacing clicked() { return clicked; }

    public boolean powered() { return powered; }

    public void power(boolean now) {
        if (powered == now) { return; }
        powered = now;
        markDirty();
    }

    public void hit(EnumFacing side) {
        clicked = side;
        if (shaking) { ticks = 0; }
        else { shaking = true; }
        world.addBlockEvent(pos, getBlockType(), ContentBlockBell.RING_EVENT, side.getIndex());
    }

    @Override public boolean receiveClientEvent(int id, int type) {
        if (id != ContentBlockBell.RING_EVENT) { return super.receiveClientEvent(id, type); }
        listen();
        resonateTicks = 0;
        clicked = EnumFacing.byIndex(type);
        ticks = 0;
        shaking = true;
        return true;
    }

    private void listen() {
        if (world.isRemote) { return; }
        long now = world.getTotalWorldTime();
        if (now > lastSearch + SEARCH_EVERY || nearby == null) {
            lastSearch = now;
            nearby = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(SEARCH_RADIUS));
        }
        for (EntityLivingBase living : nearby) {
            if (living instanceof EntityVillager && within(living, HEAR_RADIUS)) { ContentRaids.hear((EntityVillager) living, now); }
        }
    }

    private boolean within(EntityLivingBase living, int radius) {
        return living.isEntityAlive() && pos.distanceSqToCenter(living.posX, living.posY, living.posZ) < radius * radius;
    }

    private boolean raiderWithin(EntityLivingBase living, int radius) { return within(living, radius) && ContentRaids.answersBell(living); }

    @Override public void update() {
        if (shaking) { ticks++; }
        if (ticks >= SHAKE_TICKS) {
            shaking = false;
            ticks = 0;
        }
        if (world.isRemote || nearby == null) { return; }
        if (ticks >= RESONATE_AFTER && resonateTicks == 0 && raidersNear(nearby)) {
            resonating = true;
            Block block = getBlockType();
            if (block instanceof ContentBlockBell) { ((ContentBlockBell) block).resonate(world, pos); }
        }
        if (!resonating) { return; }
        if (resonateTicks < RESONATE_TICKS) { resonateTicks++; }
        else {
            reveal((WorldServer) world, nearby);
            resonating = false;
        }
    }

    private boolean raidersNear(List<EntityLivingBase> around) {
        for (EntityLivingBase living : around) {
            if (raiderWithin(living, RESONATE_RADIUS)) { return true; }
        }
        return false;
    }

    private void reveal(WorldServer server, List<EntityLivingBase> around) {
        int heard = 0;
        for (EntityLivingBase living : around) {
            if (pos.distanceSqToCenter(living.posX, living.posY, living.posZ) < GLOW_RADIUS * GLOW_RADIUS) { heard++; }
        }
        int per = MathHelper.clamp((heard - 21) / -2, 3, 15);
        int color = PARTICLE_COLOR;
        for (EntityLivingBase living : around) {
            if (!raiderWithin(living, GLOW_RADIUS)) { continue; }
            ContentRaids.glow(living);
            double dx = living.posX - pos.getX();
            double dz = living.posZ - pos.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            double x = pos.getX() + 0.5D + dx / distance;
            double z = pos.getZ() + 0.5D + dz / distance;
            for (int i = 0; i < per; i++) {
                color += 5;
                server.spawnParticle(EnumParticleTypes.SPELL_MOB, x, pos.getY() + 0.5D, z, 0, (color >> 16 & 255) / 255.0D, (color >> 8 & 255) / 255.0D, (color & 255) / 255.0D, 1.0D);
            }
        }
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        super.readFromNBT(tag);
        powered = tag.getBoolean(POWERED);
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(POWERED, powered);
        return tag;
    }

    @Override @Nonnull public AxisAlignedBB getRenderBoundingBox() { return new AxisAlignedBB(pos); }

    @Override public boolean shouldRefresh(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState was, @Nonnull IBlockState is) { return was.getBlock() != is.getBlock(); }
}
