package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RegistryLoadTask.class) public interface IRegistryLoadTask {
    @Invoker("registryKey") ResourceKey<? extends Registry<?>> rdpl$registryKey();
}
