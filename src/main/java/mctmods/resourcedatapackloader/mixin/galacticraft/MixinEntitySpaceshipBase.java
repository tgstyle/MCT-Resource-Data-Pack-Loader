package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.util.compat.GcWorldProvider;

import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntitySpaceshipBase.class, remap = false) public class MixinEntitySpaceshipBase {
    @Inject(method = "getSpaceshipGui", at = @At("HEAD"), cancellable = true)
    private void rdpl$packRocketGui(CallbackInfoReturnable<ResourceLocation> cir) {
        WorldProvider provider = ((Entity) (Object) this).world.provider;
        if (!(provider instanceof GcWorldProvider)) { return; }
        ResourceLocation gui = ((GcWorldProvider) provider).rocketGui();
        if (gui != null) { cir.setReturnValue(gui); }
    }
}
