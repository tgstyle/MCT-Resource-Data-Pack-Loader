package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelDataAndDimensions.WorldDataAndGenSettings.class) public abstract class MixinWorldDataAndGenSettings {
    @Shadow @Final private WorldData data;
    @Shadow @Final @Mutable private WorldGenSettings genSettings;

    @Inject(method = "<init>(Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/world/level/levelgen/WorldGenSettings;)V", at = @At("RETURN"))
    private void rdpl$packWorld(CallbackInfo ci) {
        if (!(data instanceof PrimaryLevelData primary) || primary.isDebugWorld() || primary.isInitialized()) { return; }
        genSettings = new WorldGenSettings(ContentTerrain.newWorld(genSettings.options()), genSettings.dimensions());
    }
}
