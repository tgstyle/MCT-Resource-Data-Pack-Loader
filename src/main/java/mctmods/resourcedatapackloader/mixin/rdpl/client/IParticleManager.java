package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(ParticleManager.class) public interface IParticleManager { @Accessor Map<Integer, IParticleFactory> getParticleTypes(); }
