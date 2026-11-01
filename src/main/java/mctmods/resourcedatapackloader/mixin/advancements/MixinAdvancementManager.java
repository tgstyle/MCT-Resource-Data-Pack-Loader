package mctmods.resourcedatapackloader.mixin.advancements;

import mctmods.resourcedatapackloader.common.advancements.AdvancementOverrides;
import mctmods.resourcedatapackloader.common.advancements.RecipeTolerance;

import java.util.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementManager.class) public abstract class MixinAdvancementManager {
    @Inject(method = "loadBuiltInAdvancements", at = @At("HEAD")) private void rdpl$injectPackAdvancements(Map<ResourceLocation, Advancement.Builder> map, CallbackInfo ci) { AdvancementOverrides.apply(map); }

    @Inject(method = "reload", at = @At("TAIL")) private void rdpl$reportMissingRecipes(CallbackInfo ci) { RecipeTolerance.flush(); }
}
