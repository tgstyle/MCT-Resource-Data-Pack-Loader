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
    private static final double MAX_PULL = 2.0D;
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

    public void shoot(Vec3 motion) {
        setDeltaMovement(motion);
        face();
        yRotO = getYRot();
        xRotO = getXRot();
    }

    @Override public void tick() {
        super.tick();
        if (stepRemote()) { return; }
        Vec3 motion = getDeltaMovement();
        if (++age > life || owner == null || !owner.isAlive() || owner.level() != level()) {
            discard();
            return;
        }
        Vec3 from = position();
        Vec3 to = from.add(motion);
        BlockHitResult block = trace(from, to);
        boolean blocked = block.getType() != HitResult.Type.MISS;
        if (blocked) { to = block.getLocation(); }
        Player hit = nearestHit(Player.class, from, to, player -> !player.isSpectator());
        if (hit != null) { pull(hit); }
        if (hit != null || blocked) {
            discard();
            return;
        }
        setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        face();
    }

    private void pull(Player player) {
        if (owner == null) { return; }
        Vec3 pull = owner.position().subtract(player.position()).scale(PULL * power).add(0.0D, LIFT, 0.0D);
        if (pull.length() > MAX_PULL) { pull = pull.normalize().scale(MAX_PULL); }
        player.push(pull);
        player.hurtMarked = true;
        player.resetFallDistance();
    }

    @Override public boolean shouldBeSaved() { return false; }

    @Override protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {}

    @Override protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {}
}
