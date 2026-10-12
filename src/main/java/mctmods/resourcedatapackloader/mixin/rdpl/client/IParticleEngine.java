package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleEngine.class) public interface IParticleEngine {
    @Accessor("resourceManager") ParticleResources rdpl$getResourceManager();
}
