package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldServer.class) public abstract class MixinWorldServerWeather {
    @Redirect(method = "updateBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;fillWithRain(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    private void rdpl$rainFillBelowCeiling(Block block, World worldIn, BlockPos pos) {
        if (ContentWeather.above(worldIn, pos.getY())) { return; }
        block.fillWithRain(worldIn, pos);
    }
}
