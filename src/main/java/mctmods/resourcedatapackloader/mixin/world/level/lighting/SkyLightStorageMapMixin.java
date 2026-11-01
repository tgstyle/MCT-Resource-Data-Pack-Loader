package mctmods.resourcedatapackloader.mixin.world.level.lighting;

import mctmods.resourcedatapackloader.common.util.world.ILightShardSource;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.chunk.DataLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.level.lighting.SkyLightSectionStorage$SkyDataLayerStorageMap") public abstract class SkyLightStorageMapMixin {
    @Redirect(method = "copy()Lnet/minecraft/world/level/lighting/SkyLightSectionStorage$SkyDataLayerStorageMap;", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;clone()Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;", remap = false)) private Long2ObjectOpenHashMap<DataLayer> rdpl$sharded(Long2ObjectOpenHashMap<DataLayer> live) { return ((ILightShardSource) this).rdpl$issue(); }
}
