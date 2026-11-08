package mctmods.resourcedatapackloader.mixin.integration.tconstruct;

import mctmods.resourcedatapackloader.common.blocks.ContentVeins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.utils.ToolHelper;

@Mixin(value = ToolHelper.class, remap = false) public abstract class MixinToolHelper {
    @Inject(method = {"breakExtraBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)V", "shearExtraBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)V"}, at = @At("HEAD"), cancellable = true) private static void rdpl$skipNested(ItemStack stack, World world, EntityPlayer player, BlockPos pos, BlockPos refPos, CallbackInfo ci) {
        if (ContentVeins.nested(player, pos)) { ci.cancel(); }
    }
}
