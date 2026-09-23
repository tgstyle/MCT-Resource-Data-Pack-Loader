package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FossilFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FossilFeature.class) public abstract class MixinFossilFeature {
    @Shadow private static int countEmptyCorners(WorldGenLevel level, BoundingBox boundingBox) { throw new AssertionError(); }

    @Redirect(method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/FossilFeature;countEmptyCorners(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;)I"))
    private int rdpl$spareBores(WorldGenLevel level, BoundingBox boundingBox) { return ContentCityClaim.fossilIntoBore(level, boundingBox) ? Integer.MAX_VALUE : countEmptyCorners(level, boundingBox); }
}
