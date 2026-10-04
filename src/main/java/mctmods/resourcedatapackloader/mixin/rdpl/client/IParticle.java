package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class) public interface IParticle {
    @Accessor("xd") void setXd(double xd);

    @Accessor("zd") void setZd(double zd);
}
