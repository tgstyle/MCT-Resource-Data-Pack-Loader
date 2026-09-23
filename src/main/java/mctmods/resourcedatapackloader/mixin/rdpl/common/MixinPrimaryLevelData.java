package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimaryLevelData.class) public abstract class MixinPrimaryLevelData {
    @Shadow private LevelSettings settings;
    @Shadow @Final @Mutable private WorldOptions worldOptions;

    @Inject(method = "<init>(Lnet/minecraft/world/level/LevelSettings;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/storage/PrimaryLevelData$SpecialWorldProperty;Lcom/mojang/serialization/Lifecycle;)V", at = @At("RETURN"))
    private void rdpl$packWorld(CallbackInfo ci) {
        if (PrimaryLevelData.class.cast(this).isDebugWorld()) { return; }
        settings = ContentTerrain.newWorld(settings);
        worldOptions = ContentTerrain.newWorld(worldOptions);
    }
}
