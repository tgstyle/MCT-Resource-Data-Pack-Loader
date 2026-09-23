package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import javax.annotation.Nullable;

@Mixin(LevelRenderer.class) public abstract class MixinLevelRenderer {
    @Shadow @Nullable private ClientLevel level;

    @Redirect(method = "renderClouds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getCloudHeight()F"))
    private float rdpl$cloudHeightAsked(DimensionSpecialEffects effects) {
        float own = effects.getCloudHeight();
        if (level == null || Float.isNaN(own)) { return own; }
        Integer asked = ContentDimensions.cloudHeight(level.dimension().location().toString());
        return asked != null ? asked : own;
    }
}
