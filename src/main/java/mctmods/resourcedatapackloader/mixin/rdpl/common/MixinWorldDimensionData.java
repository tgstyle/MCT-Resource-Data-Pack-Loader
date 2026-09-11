package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentWorldShape;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.server.dedicated.DedicatedServerProperties$WorldDimensionData") public abstract class MixinWorldDimensionData {
    @Shadow @Final private String levelType;

    @Inject(method = "create", at = @At("HEAD"), cancellable = true) private void rdpl$packPreset(RegistryAccess access, CallbackInfoReturnable<WorldDimensions> cir) {
        ResourceLocation wanted = ContentWorldShape.serverPreset(levelType);
        if (wanted == null) { return; }
        Registry<WorldPreset> registry = access.registryOrThrow(Registries.WORLD_PRESET);
        Holder<WorldPreset> holder = registry.getHolder(ResourceKey.create(Registries.WORLD_PRESET, wanted)).orElse(null);
        if (holder == null) { return; }
        cir.setReturnValue(holder.value().createWorldDimensions());
    }
}
