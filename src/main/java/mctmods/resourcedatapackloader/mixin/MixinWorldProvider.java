package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldProvider.class)
public abstract class MixinWorldProvider {
    @Inject(method = "setWorld", at = @At("RETURN"))
    private void rdpl$rememberDimension(World worldIn, CallbackInfo ci) { ContentBiomeControl.remember(worldIn); }
}
