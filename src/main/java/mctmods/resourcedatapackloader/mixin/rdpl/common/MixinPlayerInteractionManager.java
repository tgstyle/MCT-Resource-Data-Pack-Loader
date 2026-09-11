package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentHardness;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInteractionManager.class) public abstract class MixinPlayerInteractionManager {
    @Shadow public EntityPlayerMP player;

    @Redirect(method = "onBlockClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canDestroy(Lnet/minecraft/block/Block;)Z"))
    private boolean rdpl$mayDestroy(ItemStack stack, Block blockIn, BlockPos pos, EnumFacing side) {
        return stack.canDestroy(blockIn) || ContentHardness.mayBreak(player, player.world.getBlockState(pos), stack);
    }
}
