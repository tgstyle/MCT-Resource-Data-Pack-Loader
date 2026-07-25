package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.advancement.AdvancementOverrides;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AdvancementManager.class)
public abstract class MixinAdvancementManager {

    @Inject(method = "loadBuiltInAdvancements", at = @At("HEAD"))
    private void rdpl$injectPackAdvancements(Map<ResourceLocation, Advancement.Builder> map, CallbackInfo ci) { AdvancementOverrides.apply(map); }
}
