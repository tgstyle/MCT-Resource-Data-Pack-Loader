package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class) public abstract class MixinMinecraftDimensionOverlay {
    @Shadow public WorldClient world;

    @WrapOperation(method = "getAmbientMusicType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getMusicType()Lnet/minecraft/client/audio/MusicTicker$MusicType;", remap = false)) private MusicTicker.MusicType rdpl$existingMusic(WorldProvider provider, Operation<MusicTicker.MusicType> original) { return ContentDimensionRules.existingMusic(world, original.call(provider)); }
}
