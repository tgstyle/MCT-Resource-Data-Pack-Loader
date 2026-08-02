package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vazkii.quark.world.world.WorldTypeRealistic;

@Mixin(value = WorldTypeRealistic.class, remap = false)
public abstract class MixinWorldTypeRealistic {
    @ModifyArg(method = "getChunkGenerator",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/ChunkGeneratorOverworld;<init>(Lnet/minecraft/world/World;JZLjava/lang/String;)V"),
            index = 3, remap = false)
    private String rdpl$packShapesIt(String settings) { return ContentTerrain.merge(settings, "REALISTIC"); }
}
