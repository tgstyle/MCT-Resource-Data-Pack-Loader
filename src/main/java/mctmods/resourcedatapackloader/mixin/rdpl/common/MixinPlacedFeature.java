package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentFlatSource;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentPopulateControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlacedFeature.class) public abstract class MixinPlacedFeature {
    @Inject(method = "placeWithBiomeCheck", at = @At("HEAD"), cancellable = true)
    private void rdpl$populate(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ContentFlatSource.undecorated(generator) || ContentVoidWorld.voidRefuses(level, PlacedFeature.class.cast(this)) || ContentPopulateControl.refuses(generator, PlacedFeature.class.cast(this)) || ContentOreControl.refuses(generator, PlacedFeature.class.cast(this)) || ContentGeneratorControl.refuses(generator, PlacedFeature.class.cast(this))) { cir.setReturnValue(false); }
    }
}
