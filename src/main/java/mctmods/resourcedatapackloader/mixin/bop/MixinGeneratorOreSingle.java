package mctmods.resourcedatapackloader.mixin.bop;

import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;

import biomesoplenty.common.world.generator.GeneratorOreSingle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = GeneratorOreSingle.class, remap = false)
public abstract class MixinGeneratorOreSingle {
    @Shadow private IBlockState with;

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void rdpl$blockEmeralds(World world, Random random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (with == null || with.getBlock() != Blocks.EMERALD_ORE) { return; }
        if (world.isRemote || !ContentOreControl.blocks("EMERALD", "biomesoplenty", world.provider.getDimension())) { return; }

        ContentOreControl.denied("EMERALD", "biomesoplenty");
        cir.setReturnValue(false);
    }
}
