package mctmods.resourcedatapackloader.common.entities;

import mctmods.resourcedatapackloader.core.util.LineCompat;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class PullingHand extends AbstractPackProjectile {
    private static final double PULL = 0.15D;
    private static final double LIFT = 0.35D;
    private static final double MAX_PULL = 2.0D;
    private static final int EXTRA_LIFE = 5;
    private float power;
    private int life;
    private int age;

    public PullingHand(EntityType<? extends PullingHand> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public PullingHand(Level level, LivingEntity thrower, ItemStack stack, float power, double range) {
        this(ContentEntities.pullingHand(), level);
        thrownBy(thrower, stack);
        this.power = power;
        life = (int) Math.ceil(range / 1.5D) + EXTRA_LIFE;
    }

    public void aim(double x, double y, double z, float velocity) {
        setDeltaMovement(new Vec3(x, y, z).normalize().scale(velocity));
        face();
        yRotO = getYRot();
        xRotO = getXRot();
    }

    @Override public void tick() {
        super.tick();
        if (stepRemote()) { return; }
        if (++age > life || owner == null || !owner.isAlive() || owner.level() != level()) {
            discard();
            return;
        }
        Vec3 from = position();
        Vec3 to = from.add(getDeltaMovement());
        BlockHitResult block = trace(from, to);
        boolean blocked = block.getType() != HitResult.Type.MISS;
        if (blocked) { to = block.getLocation(); }
        Player hit = nearestHit(Player.class, from, to, player -> !player.isSpectator());
        if (hit != null) { pull(hit); }
        if (hit != null || blocked) {
            discard();
            return;
        }
        setPos(to);
        face();
    }

    private void pull(Player player) {
        if (owner == null) { return; }
        Vec3 push = owner.position().subtract(player.position()).scale(PULL * power).add(0.0D, LIFT, 0.0D);
        if (push.length() > MAX_PULL) { push = push.normalize().scale(MAX_PULL); }
        player.push(push);
        LineCompat.markVelocity(player);
        player.resetFallDistance();
    }

    @Override public boolean isPickable() { return false; }

    @Override public boolean shouldBeSaved() { return false; }

    @Override protected void readAdditionalSaveData(@Nonnull ValueInput tag) {}

    @Override protected void addAdditionalSaveData(@Nonnull ValueOutput tag) {}
}
