package mctmods.resourcedatapackloader.mixin.quark;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.world.world.StoneInfoBasedGenerator;
import java.util.Random;

@Mixin(value = StoneInfoBasedGenerator.class, remap = false) public abstract class MixinStoneInfoBasedGenerator {
    @Unique private BlockPos rdpl$middle;

    @Inject(method = "generateChunkPart", at = @At("HEAD")) private void rdpl$rememberMiddle(BlockPos src, Random random, int chunkX, int chunkZ, World world, CallbackInfo ci) {
        rdpl$middle = src;
    }

    @Redirect(method = "lambda$generateChunkPart$0", at = @At(value = "INVOKE", target = "Lvazkii/quark/world/world/StoneInfoBasedGenerator;canPlaceBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean rdpl$measureBeforeReading(StoneInfoBasedGenerator generator, World world, BlockPos pos) {
        BlockPos middle = rdpl$middle;
        int reach = generator.infoSupplier.get().clusterSize;
        if (middle != null && pos.distanceSq(middle) >= (double) reach * (double) reach) {
            if (ContentChunkWatch.watching()) { ContentChunkWatch.stoneSpared(); }
            return false;
        }
        return generator.canPlaceBlock(world, pos);
    }
}
