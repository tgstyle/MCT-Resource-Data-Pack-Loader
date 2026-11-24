package mctmods.resourcedatapackloader.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nonnull;

public final class PullingHand extends AbstractPackProjectile {
    private static final double SPEED = 1.5D;
    private static final double PULL = 0.15D;
    private static final double LIFT = 0.35D;
    private static final double CAP = 2.0D;
    private float power;
    private int life;
    private int age;

    public PullingHand(EntityType<? extends PullingHand> type, Level level) { super(type, level); }

    public PullingHand(Level level, LivingEntity thrower, ItemStack stack, float power) {
        this(ContentEntities.pullingHand(), level);
        thrownBy(thrower, stack);
        this.power = power;
        life = (int) Math.ceil(thrower.getAttributeValue(Attributes.FOLLOW_RANGE) / SPEED) + 5;
    }

    public void aim(Vec3 at) {
        setDeltaMovement(at.subtract(position()).normalize().scale(SPEED));
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
        Player hit = nearestHit(Player.class, from, to, player -> !player.isSpectator() && player.isAlive());
        if (hit != null) {
            pull(hit);
            discard();
            return;
        }
        if (blocked) {
            discard();
            return;
        }
        setPos(to.x, to.y, to.z);
        face();
    }

    private void pull(Player player) {
        if (owner == null) { return; }
        Vec3 pull = owner.position().subtract(player.position()).scale(PULL * power).add(0.0D, LIFT, 0.0D);
        if (pull.length() > CAP) { pull = pull.normalize().scale(CAP); }
        player.push(pull.x, pull.y, pull.z);
        player.hurtMarked = true;
        player.resetFallDistance();
    }

    @Override public boolean shouldBeSaved() { return false; }

    @Override protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {}

    @Override protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {}
}
