package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.client.util.ContentReach;
import mctmods.resourcedatapackloader.client.util.RainSplash;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensionRules;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.dimension.ContentWorldProvider;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RainDef;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class) public class MixinEntityRenderer {
    @Shadow public int rendererUpdateCount;
    @Shadow @Final private Minecraft mc;
    @Inject(method = "createWorldIcon", at = @At("HEAD"), cancellable = true) private void rdpl$iconAfterTheFog(CallbackInfo ci) {
        if (HoldView.showing()) { ci.cancel(); }
    }

    @Inject(method = "getMouseOver", at = @At("RETURN")) private void rdpl$weaponReach(float partialTicks, CallbackInfo ci) { ContentReach.pick(partialTicks); }

    @Redirect(method = "updateFogColor(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getFogColor(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;", remap = false)) private Vec3d rdpl$fluidFog(Block block, World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) { return ContentFogSampler.fluidFog(state, block.getFogColor(world, pos, state, entity, originalColor, partialTicks)); }

    @Unique private boolean rdpl$aboveCeiling() {
        Entity view = Minecraft.getMinecraft().getRenderViewEntity();
        return view != null && ContentWeather.above(view.world, MathHelper.floor(view.posY));
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherRender(float partialTicks, CallbackInfo ci) {
        if (rdpl$aboveCeiling()) { ci.cancel(); }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherParticles(CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        RainDef rain = ContentDimensions.rain(mc.world);
        if (rdpl$aboveCeiling() || rain != null && rain.splashless()) {
            ci.cancel();
            return;
        }
        RainDef splash = mc.world.provider instanceof ContentWorldProvider ? ((ContentWorldProvider) mc.world.provider).rainSplash() : rain;
        if (splash == null) { return; }
        RainSplash.tick(mc, splash, rendererUpdateCount);
        ci.cancel();
    }

    @WrapOperation(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;doesXZShowFog(II)Z")) private boolean rdpl$existingShowsFog(WorldProvider provider, int x, int z, Operation<Boolean> original) { return ContentDimensionRules.existingShowsFog(mc.world, original.call(provider, x, z)); }

    @WrapOperation(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;calcSunriseSunsetColors(FF)[F")) private float[] rdpl$existingSunrise(WorldProvider provider, float celestialAngle, float partialTicks, Operation<float[]> original) { return ContentDimensionRules.existingSunrise(mc.world, original.call(provider, celestialAngle, partialTicks)); }

    @ModifyExpressionValue(method = "updateFogColor", at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColor1:F", opcode = Opcodes.GETFIELD), @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColor2:F", opcode = Opcodes.GETFIELD)}) private float rdpl$existingFogLight(float own) { return ContentDimensionRules.existingFogLight(mc.world, own); }

    @WrapOperation(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getLightmapColors(FFFF[F)V", remap = false)) private void rdpl$existingLightmap(WorldProvider provider, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors, Operation<Void> original) {
        original.call(provider, partialTicks, sunBrightness, skyLight, blockLight, colors);
        ContentDimensionRules.existingLightmap(mc.world, partialTicks, sunBrightness, skyLight, blockLight, colors);
    }
}
