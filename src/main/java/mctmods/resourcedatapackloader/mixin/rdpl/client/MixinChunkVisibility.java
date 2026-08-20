package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo @Mixin(targets = "net.optifine.render.ChunkVisibility") public class MixinChunkVisibility {
    @Dynamic @Inject(method = "getMaxChunkY", at = @At("HEAD"), cancellable = true, remap = false) private static void getMaxChunkYRubic(World world, Entity viewEntity, int renderDistanceChunks, CallbackInfoReturnable<Integer> cbi) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return; }
        cbi.setReturnValue(Integer.MAX_VALUE - 1);
    }
}
