package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.util.WeatherShield;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class) public abstract class LevelRendererWeatherMixin {
    @Unique private static boolean rdpl$aboveCeiling() {
        Entity view = Minecraft.getInstance().getCameraEntity();
        return view != null && ContentWeather.above(view.level(), view.getBlockY());
    }

    @Inject(method = "render(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherRender(CallbackInfo ci) { if (rdpl$aboveCeiling()) { ci.cancel(); } }

    @Inject(method = "extractRenderState", at = @At("HEAD")) private void rdpl$fillRainShields(Level level, int ticks, float partialTicks, Vec3 cameraPos, WeatherRenderState renderState, CallbackInfo ci) { WeatherShield.fill(level, cameraPos); }

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I")) private int rdpl$shutRain(Level level, Heightmap.Types type, int x, int z) { return WeatherShield.shut(x, z) ? WeatherShield.shutHeight() : level.getHeight(type, x, z); }

    @Redirect(method = "tickRainParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;", ordinal = 0)) private BlockPos rdpl$shutSplash(ClientLevel level, Heightmap.Types heightmapType, BlockPos pos) { return WeatherShield.shut(pos.getX(), pos.getZ()) ? new BlockPos(pos.getX(), WeatherShield.shutHeight(), pos.getZ()) : level.getHeightmapPos(heightmapType, pos); }

    @Inject(method = "tickRainParticles(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Camera;ILnet/minecraft/server/level/ParticleStatus;I)V", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherParticles(ClientLevel level, Camera camera, int ticks, ParticleStatus particleStatus, int weatherRadius, CallbackInfo ci) {
        if (rdpl$aboveCeiling()) {
            ci.cancel();
            return;
        }
        WeatherShield.fill(level, camera.position());
    }
}
