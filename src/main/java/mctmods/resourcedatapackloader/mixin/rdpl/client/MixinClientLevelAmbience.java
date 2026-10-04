package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentDimensionAmbience;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(ClientLevel.class) public abstract class MixinClientLevelAmbience {
    @Redirect(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getAmbientParticle()Ljava/util/Optional;"))
    private Optional<AmbientParticleSettings> rdpl$dimensionParticle(Biome biome) { return ContentDimensionAmbience.particle(biome, (ClientLevel) (Object) this); }

    @Inject(method = "doAnimateTick", at = @At("TAIL"))
    private void rdpl$dimensionParticles(int posX, int posY, int posZ, int range, RandomSource random, Block block, BlockPos.MutableBlockPos blockPos, CallbackInfo ci) { ContentDimensionAmbience.particles((ClientLevel) (Object) this, blockPos); }
}
