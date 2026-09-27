package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentWorldShape;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

    @Inject(method = "create(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/level/levelgen/WorldDimensions;", at = @At("HEAD"), cancellable = true) private void rdpl$packPreset(HolderLookup.Provider registries, CallbackInfoReturnable<WorldDimensions> cir) {
        Identifier wanted = ContentWorldShape.serverPreset(levelType);
        if (wanted == null) { return; }
        Holder<WorldPreset> holder = registries.lookupOrThrow(Registries.WORLD_PRESET).get(ResourceKey.create(Registries.WORLD_PRESET, wanted)).orElse(null);
        if (holder == null) { return; }
        cir.setReturnValue(holder.value().createWorldDimensions());
    }
}
