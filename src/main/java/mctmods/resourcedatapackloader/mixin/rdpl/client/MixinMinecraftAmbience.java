package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.client.ContentDimensionAmbience;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;

@Mixin(Minecraft.class) public abstract class MixinMinecraftAmbience {
    @Shadow @Nullable public LocalPlayer player;

    @Inject(method = "getSituationalMusic", at = @At("RETURN"), cancellable = true)
    private void rdpl$dimensionMusic(CallbackInfoReturnable<Music> cir) {
        if (player == null || cir.getReturnValue() == Musics.CREDITS) { return; }
        Music music = ContentDimensionAmbience.music(player.level());
        if (music != null) { cir.setReturnValue(music); }
    }
}
