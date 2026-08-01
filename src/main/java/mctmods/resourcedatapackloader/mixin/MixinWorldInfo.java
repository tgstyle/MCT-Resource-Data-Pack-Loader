package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldInfo.class)
public abstract class MixinWorldInfo {
    @Shadow private String generatorOptions;
    @Shadow private WorldType terrainType;

    @Inject(method = "<init>(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V", at = @At("TAIL"))
    private void rdpl$shapeTerrain(WorldSettings settings, String name, CallbackInfo ci) {
        if (terrainType != WorldType.DEFAULT || (generatorOptions != null && !generatorOptions.isEmpty())) { return; }

        String options = ContentTerrain.options();
        if (options.isEmpty()) { return; }

        generatorOptions = options;
        ContentLog.LOGGER.info("Shaping the overworld of '{}' with {}", name, options);
    }
}
