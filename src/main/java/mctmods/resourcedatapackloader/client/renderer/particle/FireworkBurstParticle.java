package mctmods.resourcedatapackloader.client.renderer.particle;

import mctmods.resourcedatapackloader.common.fireworks.FireworkParticleOption;

import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;

public final class FireworkBurstParticle extends BreakingItemParticle {
    private static final float SIZE_SCALE = 2.0F;
    private static final int LIFETIME = 30;
    private static final float GRAVITY = 0.05F;
    private static final float FRICTION = 0.92F;

    private FireworkBurstParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, FireworkParticleOption option) {
        super(level, x, y, z, vx, vy, vz, option.getItem());
        this.quadSize *= SIZE_SCALE;
        this.lifetime = LIFETIME;
        this.gravity = GRAVITY;
        this.friction = FRICTION;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU0();
    }

    @Override
    protected float getU1() {
        return this.sprite.getU1();
    }

    @Override
    protected float getV0() {
        return this.sprite.getV0();
    }

    @Override
    protected float getV1() {
        return this.sprite.getV1();
    }

    public static final class Provider implements ParticleProvider<FireworkParticleOption> {
        @Override
        public Particle createParticle(@Nonnull FireworkParticleOption option, @Nonnull ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            return new FireworkBurstParticle(level, x, y, z, vx, vy, vz, option);
        }
    }
}
