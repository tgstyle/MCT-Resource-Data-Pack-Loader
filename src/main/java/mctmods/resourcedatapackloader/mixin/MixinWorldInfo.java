package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.Summary;

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
        String options = ContentTerrain.merge(generatorOptions, terrainType == null ? "" : terrainType.getName());
        if (options.isEmpty() || options.equals(generatorOptions)) { return; }

        generatorOptions = options;
        Summary.info("terrain.shaped", "Shaping the overworld of '" + name + "' with " + options);
    }
}
