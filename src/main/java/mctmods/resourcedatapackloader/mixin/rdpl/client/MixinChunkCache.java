package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ChunkCache.class) public abstract class MixinChunkCache {
    @Shadow protected World world;

    @ModifyConstant(method = "getLightFor",
            constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO))
    private int getLightFor_getMinHeight(int orig) { return ((IRubicWorld) world).rdpl$getMinHeight(); }

    @ModifyConstant(method = "getLightFor", constant = @Constant(intValue = 256)) private int getLightFor_getMaxHeight(int orig) {
        return ((IRubicWorld) world).rdpl$getMaxHeight();
    }

    @ModifyConstant(method = "getLightForExt",
            constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO),
            slice = @Slice(
                    from = @At(value = "INVOKE:FIRST", target = "Lnet/minecraft/util/math/BlockPos;getY()I"),
                    to = @At(value = "INVOKE:FIRST",
                            target = "Lnet/minecraft/world/ChunkCache;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;")
            )
    )
    private int getLightForExt_getMinHeight(int orig) { return ((IRubicWorld) world).rdpl$getMinHeight(); }

    @ModifyConstant(method = "getLightForExt", constant = @Constant(intValue = 256)) private int getLightForExt_getMaxHeight(int orig) {
        return ((IRubicWorld) world).rdpl$getMaxHeight();
    }
}
