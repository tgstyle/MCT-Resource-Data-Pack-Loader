package mctmods.resourcedatapackloader.mixin.client.multiplayer;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionEffects;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class) public abstract class ClientLevelMixin {
    @Mutable @Shadow @Final private DimensionSpecialEffects effects;

    @Unique private DimensionDef rdpl$def() { return ContentDimensions.def((ClientLevel) (Object) this); }

    @Inject(method = "<init>", at = @At("RETURN")) private void rdpl$existingEffects(CallbackInfo ci) { effects = ContentDimensionEffects.existing(effects, ContentDimensions.base((ClientLevel) (Object) this)); }

    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;")) private Vec3 rdpl$skyColor(Vec3 vec, CubicSampler.Vec3Fetcher fetcher) {
        DimensionDef def = rdpl$def();
        return def != null && def.skyColor() >= 0 ? Vec3.fromRGB24(def.skyColor()) : CubicSampler.gaussianSampleVec3(vec, fetcher);
    }

    @Inject(method = "getCloudColor", at = @At("HEAD"), cancellable = true) private void rdpl$cloudColor(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        int layer = effects instanceof ContentDimensionEffects content ? content.layerColor() : SkyLookDef.UNSET;
        DimensionDef def = rdpl$def();
        if (layer >= 0) { cir.setReturnValue(Vec3.fromRGB24(layer)); }
        else if (def != null && def.cloudColor() >= 0) { cir.setReturnValue(Vec3.fromRGB24(def.cloudColor())); }
    }

    @Inject(method = "getStarBrightness", at = @At("HEAD"), cancellable = true) private void rdpl$starBrightness(float partialTick, CallbackInfoReturnable<Float> cir) {
        DimensionDef def = rdpl$def();
        if (def != null && def.starBrightness() >= 0.0F) { cir.setReturnValue(def.starBrightness()); }
    }

    @Redirect(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getAmbientParticle()Ljava/util/Optional;")) private Optional<AmbientParticleSettings> rdpl$dimensionParticle(Biome biome) { return ContentDimensionAmbience.particle(biome, (ClientLevel) (Object) this); }

    @Inject(method = "doAnimateTick", at = @At("TAIL")) private void rdpl$dimensionParticles(int posX, int posY, int posZ, int range, RandomSource random, Block block, BlockPos.MutableBlockPos blockPos, CallbackInfo ci) { ContentDimensionAmbience.particles((ClientLevel) (Object) this, blockPos); }
}
