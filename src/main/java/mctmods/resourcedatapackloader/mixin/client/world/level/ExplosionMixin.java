package mctmods.resourcedatapackloader.mixin.client.world.level;

import mctmods.resourcedatapackloader.client.util.BlastHush;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class) public abstract class ExplosionMixin {
    @Redirect(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V")) private void rdpl$packBlastSound(Level level, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay) { if (BlastHush.audible(x, y, z)) { level.playLocalSound(x, y, z, sound, category, volume, pitch, distanceDelay); } }
}
