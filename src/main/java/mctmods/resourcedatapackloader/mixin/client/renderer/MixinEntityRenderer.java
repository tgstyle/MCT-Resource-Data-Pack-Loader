package mctmods.resourcedatapackloader.mixin.client.renderer;

import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.client.util.ContentDimensionRulesClient;
import mctmods.resourcedatapackloader.client.util.ContentReach;
import mctmods.resourcedatapackloader.client.util.RainSplash;
import mctmods.resourcedatapackloader.client.util.TrueDarkness;
import mctmods.resourcedatapackloader.common.blocks.shield.ShieldIndex;
import mctmods.resourcedatapackloader.common.blocks.shield.ShieldIndexes;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensionRules;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.dimension.ContentWorldProvider;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.DarknessDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RainDef;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import javax.annotation.Nullable;
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
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class) public class MixinEntityRenderer {
    @Shadow public int rendererUpdateCount;
    @Shadow @Final private Minecraft mc;
    @Shadow @Final private int[] lightmapColors;
    @Unique @Nullable private DarknessDef rdpl$darkness;
    @Unique private final ShieldIndex.Near rdpl$shields = new ShieldIndex.Near();
    @Unique private int rdpl$viewY;
    @Inject(method = "createWorldIcon", at = @At("HEAD"), cancellable = true) private void rdpl$iconAfterTheFog(CallbackInfo ci) {
        if (HoldView.showing()) { ci.cancel(); }
    }

    @Inject(method = "getMouseOver", at = @At("RETURN")) private void rdpl$weaponReach(float partialTicks, CallbackInfo ci) { ContentReach.pick(partialTicks); }

    @Redirect(method = "updateFogColor(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getFogColor(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;", remap = false)) private Vec3d rdpl$fluidFog(Block block, World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) { return ContentFogSampler.fluidFog(state, block.getFogColor(world, pos, state, entity, originalColor, partialTicks)); }

    @Unique private boolean rdpl$aboveCeiling() {
        Entity view = Minecraft.getMinecraft().getRenderViewEntity();
        return view != null && ContentWeather.above(view.world, MathHelper.floor(view.posY));
    }

    @Unique private void rdpl$fillShields(int range) {
        Entity view = mc.getRenderViewEntity();
        if (view == null || !ShieldIndexes.active()) { return; }
        rdpl$viewY = MathHelper.floor(view.posY);
        ShieldIndexes.near(mc.world, MathHelper.floor(view.posX), MathHelper.floor(view.posZ), range, rdpl$shields);
    }

    @Unique private BlockPos rdpl$shutHeight(BlockPos pos) { return new BlockPos(pos.getX(), rdpl$viewY + 11, pos.getZ()); }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherRender(float partialTicks, CallbackInfo ci) {
        if (rdpl$aboveCeiling()) {
            ci.cancel();
            return;
        }
        rdpl$fillShields(mc.gameSettings.fancyGraphics ? 10 : 5);
    }

    @WrapOperation(method = "renderRainSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getPrecipitationHeight(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;")) private BlockPos rdpl$shutRain(World world, BlockPos pos, Operation<BlockPos> original) {
        return rdpl$shields.shut(pos.getX(), pos.getZ()) ? rdpl$shutHeight(pos) : original.call(world, pos);
    }

    @WrapOperation(method = "addRainParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getPrecipitationHeight(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;", ordinal = 0)) private BlockPos rdpl$shutSplash(World world, BlockPos pos, Operation<BlockPos> original) {
        return rdpl$shields.shut(pos.getX(), pos.getZ()) ? rdpl$shutHeight(pos) : original.call(world, pos);
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true) private void rdpl$skipWeatherParticles(CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        rdpl$fillShields(10);
        RainDef rain = ContentDimensions.rain(mc.world);
        if (rdpl$aboveCeiling() || rain != null && rain.splashless()) {
            ci.cancel();
            return;
        }
        RainDef splash = mc.world.provider instanceof ContentWorldProvider ? ((ContentWorldProvider) mc.world.provider).rainSplash() : rain;
        if (splash == null) { return; }
        RainSplash.tick(mc, splash, rendererUpdateCount, rdpl$shields);
        ci.cancel();
    }

    @WrapOperation(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;doesXZShowFog(II)Z")) private boolean rdpl$existingShowsFog(WorldProvider provider, int x, int z, Operation<Boolean> original) { return ContentDimensionRules.existingShowsFog(mc.world, original.call(provider, x, z)); }

    @WrapOperation(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;calcSunriseSunsetColors(FF)[F")) private float[] rdpl$existingSunrise(WorldProvider provider, float celestialAngle, float partialTicks, Operation<float[]> original) { return ContentDimensionRules.existingSunrise(mc.world, original.call(provider, celestialAngle, partialTicks)); }

    @ModifyExpressionValue(method = "updateFogColor", at = {@At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColor1:F", opcode = Opcodes.GETFIELD), @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColor2:F", opcode = Opcodes.GETFIELD)}) private float rdpl$existingFogLight(float own) { return ContentDimensionRules.existingFogLight(mc.world, own); }

    @ModifyExpressionValue(method = "updateFogColor(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;getNightVisionBrightness(Lnet/minecraft/entity/EntityLivingBase;F)F")) private float rdpl$tintedVisionFog(float own) { return ContentDimensionRulesClient.tintsVision(mc.world) ? 0.0F : own; }

    @WrapOperation(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getLightmapColors(FFFF[F)V", remap = false)) private void rdpl$existingLightmap(WorldProvider provider, float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors, Operation<Void> original) {
        original.call(provider, partialTicks, sunBrightness, skyLight, blockLight, colors);
        ContentDimensionRules.existingLightmap(mc.world, partialTicks, sunBrightness, skyLight, blockLight, colors);
        TrueDarkness.nightVision(rdpl$darkness, colors);
    }

    @ModifyExpressionValue(method = "updateLightmap(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getSunBrightness(F)F")) private float rdpl$darkSun(float own) {
        rdpl$darkness = TrueDarkness.of(mc.world);
        return TrueDarkness.sun(rdpl$darkness, mc.world, own);
    }

    @ModifyConstant(method = "updateLightmap(F)V", constant = {@Constant(floatValue = 0.95F), @Constant(floatValue = 0.96F)}) private float rdpl$darkScale(float own) { return TrueDarkness.scale(rdpl$darkness, own); }

    @ModifyConstant(method = "updateLightmap(F)V", constant = {@Constant(floatValue = 0.05F), @Constant(floatValue = 0.03F)}) private float rdpl$darkFloor(float own) { return TrueDarkness.floor(rdpl$darkness, own); }

    @ModifyExpressionValue(method = "updateLightmap(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getLightBrightnessTable()[F")) private float[] rdpl$darkTable(float[] own) { return TrueDarkness.table(rdpl$darkness, own); }

    @ModifyExpressionValue(method = "updateLightmap(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/DimensionType;getId()I")) private int rdpl$darkEnd(int own) { return TrueDarkness.endLight(rdpl$darkness, own); }

    @ModifyExpressionValue(method = "updateLightmap(F)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;gammaSetting:F", opcode = Opcodes.GETFIELD)) private float rdpl$lockedGamma(float own) { return TrueDarkness.gamma(rdpl$darkness, own); }

    @Inject(method = "updateLightmap(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V")) private void rdpl$grayNight(float partialTicks, CallbackInfo ci) { TrueDarkness.gray(rdpl$darkness, lightmapColors); }
}
