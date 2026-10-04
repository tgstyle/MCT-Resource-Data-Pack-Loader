package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentDimensionAmbience;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Optional;

@Mixin(BiomeAmbientSoundsHandler.class) public abstract class MixinBiomeAmbientSoundsHandler {
    @Shadow @Final private LocalPlayer player;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getAmbientLoop()Ljava/util/Optional;"))
    private Optional<Holder<SoundEvent>> rdpl$dimensionLoop(Biome biome) { return ContentDimensionAmbience.loop(biome, player.level()); }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getAmbientAdditions()Ljava/util/Optional;"))
    private Optional<AmbientAdditionsSettings> rdpl$dimensionAdditions(Biome biome) { return ContentDimensionAmbience.additions(biome, player.level()); }
}
