package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import micdoodle8.mods.galacticraft.core.TransformerHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TransformerHooks.class, remap = false) public class MixinTransformerHooksGravity {
    @Inject(method = "getGravityForEntity", at = @At("RETURN"), cancellable = true)
    private static void rdpl$worldGravity(Entity entity, CallbackInfoReturnable<Double> cir) {
        double held = cir.getReturnValueD();
        double scaled = ContentPhysics.gravity(entity.world, held);
        if (scaled != held) { cir.setReturnValue(scaled); }
    }

    @Inject(method = "getItemGravity", at = @At("RETURN"), cancellable = true)
    private static void rdpl$itemGravity(EntityItem e, CallbackInfoReturnable<Double> cir) {
        double held = cir.getReturnValueD();
        double scaled = ContentPhysics.gravity(e.world, held);
        if (scaled != held) { cir.setReturnValue(scaled); }
    }

    @Inject(method = "getArrowGravity", at = @At("RETURN"), cancellable = true)
    private static void rdpl$arrowGravity(EntityArrow e, CallbackInfoReturnable<Float> cir) {
        float held = cir.getReturnValueF();
        float scaled = (float) ContentPhysics.gravity(e.world, held);
        if (scaled != held) { cir.setReturnValue(scaled); }
    }
}
