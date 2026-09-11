package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.ContentHardness;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerControllerMP.class) public abstract class MixinPlayerControllerMP {
    @Shadow @Final private Minecraft mc;

    @Redirect(method = "clickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canDestroy(Lnet/minecraft/block/Block;)Z"))
    private boolean rdpl$mayClick(ItemStack stack, Block blockIn, BlockPos loc, EnumFacing face) {
        return stack.canDestroy(blockIn) || ContentHardness.mayBreak(mc.player, mc.world.getBlockState(loc), stack);
    }

    @Redirect(method = "onPlayerDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canDestroy(Lnet/minecraft/block/Block;)Z"))
    private boolean rdpl$mayDestroy(ItemStack stack, Block blockIn, BlockPos pos) {
        return stack.canDestroy(blockIn) || ContentHardness.mayBreak(mc.player, mc.world.getBlockState(pos), stack);
    }
}
