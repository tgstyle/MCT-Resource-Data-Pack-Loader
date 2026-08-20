package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Render.class) public abstract class MixinRender {
    @Shadow public abstract void bindTexture(ResourceLocation location);

    @Inject(method = "bindEntityTexture", at = @At("HEAD"), cancellable = true) private void rdpl$variantTexture(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ResourceLocation wanted = ContentEntities.texture(entity);
        if (wanted == null) { return; }
        bindTexture(wanted);
        cir.setReturnValue(Boolean.TRUE);
    }
}
