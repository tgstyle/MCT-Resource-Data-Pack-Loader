package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Random;

@Mixin(WorldGenBlockBlob.class) public class MixinWorldGenBlockBlob {
    @Unique private int rdpl$minY;

    @Inject(method = "generate", at = @At("HEAD")) private void onGenerate(World worldIn, Random rand, BlockPos position, CallbackInfoReturnable<Boolean> cbi) {
        this.rdpl$minY = Coords.getMinCubePopulationPos(position.getY()) - 1;
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 3, ordinal = 0)) private int getMinGenHeight0(int orig, World worldIn, Random rand, BlockPos position) {
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld()) { return rdpl$minY; }
        return orig;
    }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 3, ordinal = 1)) private int getMinGenHeight1(int orig, World worldIn, Random rand, BlockPos position) {
        if (((IRubicWorld) worldIn).rdpl$isRubicWorld()) { return rdpl$minY; }
        return orig;
    }
}
