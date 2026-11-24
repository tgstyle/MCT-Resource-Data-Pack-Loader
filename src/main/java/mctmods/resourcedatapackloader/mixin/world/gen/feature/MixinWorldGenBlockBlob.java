package mctmods.resourcedatapackloader.mixin.world.gen.feature;

import mctmods.resourcedatapackloader.common.locate.ContentStructureSearch;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenBlockBlob.class) public class MixinWorldGenBlockBlob {
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true) private void rdpl$spareRoads(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) { if (ContentStructureSearch.spareRoad(worldIn, position, position.getX() - 3, position.getY() - 28, position.getZ() - 3, position.getX() + 1, position.getY() + 14, position.getZ() + 1, "A boulder at {}, {}, {} would stand on the road at {}, {}, so it is not made")) { cir.setReturnValue(false); } }
}
