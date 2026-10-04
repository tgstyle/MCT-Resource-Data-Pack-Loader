package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class) public interface IParticle {
    @Accessor void setMotionX(double motionX);

    @Accessor void setMotionZ(double motionZ);
}
