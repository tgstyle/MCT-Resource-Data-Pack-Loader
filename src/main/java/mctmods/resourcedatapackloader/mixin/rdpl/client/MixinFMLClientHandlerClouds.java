package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentCloudRenderer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import javax.annotation.Nullable;

@Mixin(value = FMLClientHandler.class, remap = false) public abstract class MixinFMLClientHandlerClouds {
    @WrapOperation(method = "renderClouds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;getCloudRenderer()Lnet/minecraftforge/client/IRenderHandler;")) @Nullable private IRenderHandler rdpl$ownClouds(WorldProvider provider, Operation<IRenderHandler> original) { return ContentCloudRenderer.rendering() ? null : original.call(provider); }
}
