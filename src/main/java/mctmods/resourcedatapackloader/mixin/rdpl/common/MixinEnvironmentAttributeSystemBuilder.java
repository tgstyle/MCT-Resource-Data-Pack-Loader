package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironmentLayers;
import mctmods.resourcedatapackloader.content.worldgen.ContentRuleSwitch;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnvironmentAttributeSystem.Builder.class) public abstract class MixinEnvironmentAttributeSystemBuilder {
    @Inject(method = "addDefaultLayers", at = @At("RETURN")) private void rdpl$dimensionAmbience(Level level, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
        EnvironmentAttributeSystem.Builder builder = (EnvironmentAttributeSystem.Builder) (Object) this;
        ContentDimensionAmbience.layers(builder, level);
        ContentEnvironmentLayers.afterTimelines(builder, level);
    }

    @WrapMethod(method = "addTimelineLayer") private EnvironmentAttributeSystem.Builder rdpl$dayClock(Holder<Timeline> timeline, ClockManager clockManager, Operation<EnvironmentAttributeSystem.Builder> original) { return original.call(timeline, ContentRuleSwitch.dayClock(clockManager, timeline)); }
}
