package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionRules;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CloudRenderer.class, remap = false) public abstract class MixinCloudRendererDimensionOverlay {
    @WrapOperation(method = "checkSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;isSurfaceWorld()Z", remap = true)) private boolean rdpl$existingSurface(WorldProvider provider, Operation<Boolean> original) { return ContentDimensionRules.existingSurface(provider, original.call(provider)); }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getCloudHeight()F", remap = true)) private float rdpl$existingCloudHeight(WorldProvider provider, Operation<Float> original) { return ContentDimensionRules.existingCloudHeight(Minecraft.getMinecraft().world, original.call(provider)); }
}
