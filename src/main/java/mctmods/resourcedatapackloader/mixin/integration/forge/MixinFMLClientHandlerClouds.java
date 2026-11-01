package mctmods.resourcedatapackloader.mixin.integration.forge;

import mctmods.resourcedatapackloader.client.renderer.sky.ContentCloudRenderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import javax.annotation.Nullable;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FMLClientHandler.class, remap = false) public abstract class MixinFMLClientHandlerClouds {
    @WrapOperation(method = "renderClouds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getCloudRenderer()Lnet/minecraftforge/client/IRenderHandler;")) @Nullable private IRenderHandler rdpl$ownClouds(WorldProvider provider, Operation<IRenderHandler> original) { return ContentCloudRenderer.rendering() ? null : original.call(provider); }
}
