package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.fireworks.BurstParticleOption;

import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.world.item.ItemStack;

public final class BurstParticle extends BreakingItemParticle {
    private static final float SIZE = 2.0F;
    private static final int LIFE = 30;
    private static final float GRAVITY = 0.05F;
    private static final float FRICTION = 0.92F;
    private static final float DAMP = 0.1F;

    private BurstParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, ItemStack stack) {
        super(level, x, y, z, stack);
        this.xd = this.xd * DAMP + xd;
        this.yd = this.yd * DAMP + yd;
        this.zd = this.zd * DAMP + zd;
        this.quadSize *= SIZE;
        this.lifetime = LIFE;
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

    public static final class Provider implements ParticleProvider<BurstParticleOption> {
        @Override
        public Particle createParticle(@Nonnull BurstParticleOption option, @Nonnull ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new BurstParticle(level, x, y, z, xd, yd, zd, option.getItem());
        }
    }
}
