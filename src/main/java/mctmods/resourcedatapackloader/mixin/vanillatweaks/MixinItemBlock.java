package mctmods.resourcedatapackloader.mixin.vanillatweaks;

import mctmods.resourcedatapackloader.content.block.ContentChests;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlock.class) public abstract class MixinItemBlock {
    @Shadow @Final protected Block block;

    @Inject(method = "onItemUse", at = @At("HEAD")) private void rdpl$beginChestUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) { ContentChests.beginUse(player, worldIn, pos, facing, block); }

    @Inject(method = "onItemUse", at = @At("RETURN")) private void rdpl$endChestUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) { ContentChests.endUse(); }
}
