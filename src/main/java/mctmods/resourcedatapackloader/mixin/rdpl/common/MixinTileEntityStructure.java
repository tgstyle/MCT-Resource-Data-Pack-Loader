package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.tileentity.TileEntityStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(TileEntityStructure.class) public class MixinTileEntityStructure {
    @ModifyConstant(method = "detectSize", require = 1, constant = @Constant(intValue = 0, ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityStructure;getPos()Lnet/minecraft/util/math/BlockPos;"), to = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityStructure;getNearbyCornerBlocks(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;"))) private int rubic$cornerSearchMinY(int orig) { TileEntityStructure self = (TileEntityStructure) (Object) this; return GenHeights.rubic(self.getWorld()) ? Math.max(self.getPos().getY() - 80, GenHeights.floor(self.getWorld(), orig)) : orig; }

    @ModifyConstant(method = "detectSize", require = 1, constant = @Constant(intValue = 255, ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityStructure;getPos()Lnet/minecraft/util/math/BlockPos;"), to = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityStructure;getNearbyCornerBlocks(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;"))) private int rubic$cornerSearchMaxY(int orig) { TileEntityStructure self = (TileEntityStructure) (Object) this; return GenHeights.rubic(self.getWorld()) ? Math.min(self.getPos().getY() + 80, GenHeights.ceiling(self.getWorld(), orig + 1) - 1) : orig; }
}
