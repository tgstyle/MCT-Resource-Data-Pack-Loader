package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.LightShardSource;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.chunk.DataLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.level.lighting.SkyLightSectionStorage$SkyDataLayerStorageMap") public abstract class MixinSkyLightStorageMap {
    @Redirect(method = "copy()Lnet/minecraft/world/level/lighting/SkyLightSectionStorage$SkyDataLayerStorageMap;", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;clone()Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;", remap = false))
    private Long2ObjectOpenHashMap<DataLayer> rdpl$sharded(Long2ObjectOpenHashMap<DataLayer> live) { return ((LightShardSource) this).rdpl$issue(); }
}
