package mctmods.resourcedatapackloader.mixin.bop;

import mctmods.resourcedatapackloader.content.worldgen.ContentCascade;

import biomesoplenty.common.world.generator.GeneratorDoubleFlora;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GeneratorDoubleFlora.class, remap = false)
public abstract class MixinGeneratorDoubleFlora {
    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", remap = true))
    private IBlockState rdpl$readNearby(World world, BlockPos pos) { return ContentCascade.stateOrUnloaded(world, pos); }
}
