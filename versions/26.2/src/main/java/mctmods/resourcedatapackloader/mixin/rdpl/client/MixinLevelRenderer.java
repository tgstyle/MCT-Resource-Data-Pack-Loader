package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import javax.annotation.Nullable;

@Mixin(LevelExtractor.class) public abstract class MixinLevelRenderer {
    @Shadow @Nullable private ClientLevel level;

    @WrapOperation(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeProbe;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;F)Ljava/lang/Object;"))
    private Object rdpl$cloudHeightAsked(EnvironmentAttributeProbe probe, EnvironmentAttribute<?> attribute, float partialTicks, Operation<Object> original) {
        Object own = original.call(probe, attribute, partialTicks);
        if (attribute != EnvironmentAttributes.CLOUD_HEIGHT || level == null || !(own instanceof Float height) || Float.isNaN(height)) { return own; }
        Integer asked = ContentDimensions.cloudHeight(level.dimension().identifier().toString());
        return asked != null ? Float.valueOf(asked) : own;
    }
}
