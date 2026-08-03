package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentLightArea;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Chunk.class)
public abstract class MixinChunkColumnLight {
    @Redirect(method = "checkLight(II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;checkLight(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean rdpl$skipSettledSky(World world, BlockPos pos) {
        if (!world.provider.hasSkyLight() || !ContentLightArea.skySettled(world, pos)) { return world.checkLight(pos); }

        ContentChunkWatch.skySettled();

        return world.checkLightFor(EnumSkyBlock.BLOCK, pos);
    }
}
