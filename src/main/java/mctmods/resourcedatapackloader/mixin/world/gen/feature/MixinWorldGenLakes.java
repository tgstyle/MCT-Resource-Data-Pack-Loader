package mctmods.resourcedatapackloader.mixin.world.gen.feature;

import mctmods.resourcedatapackloader.common.locate.ContentStructureSearch;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLakes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenLakes.class) public class MixinWorldGenLakes {
    @Inject(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;down(I)Lnet/minecraft/util/math/BlockPos;", ordinal = 0), cancellable = true) private void rdpl$spareRoads(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cir) { if (ContentStructureSearch.spareRoad(worldIn, position, position.getX(), Integer.MIN_VALUE, position.getZ(), position.getX() + 15, position.getY() + 20, position.getZ() + 15, "A lake at {}, {}, {} would flood the road at {}, {}, so it is not made")) { cir.setReturnValue(false); } }
}
