package mctmods.resourcedatapackloader.mixin.client.multiplayer;

import mctmods.resourcedatapackloader.client.util.IPreviousGameType;
import mctmods.resourcedatapackloader.common.hardness.ContentHardness;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class) public abstract class MixinPlayerControllerMP implements IPreviousGameType {
    @Shadow @Final private Minecraft mc;
    @Shadow private GameType currentGameType;
    @Unique @Nullable private GameType rdpl$previousGameType;
    @Unique private boolean rdpl$gameTypeReceived;

    @Redirect(method = "clickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canDestroy(Lnet/minecraft/block/Block;)Z")) private boolean rdpl$mayClick(ItemStack stack, Block blockIn, BlockPos loc, EnumFacing face) { return stack.canDestroy(blockIn) || ContentHardness.mayBreak(mc.player, mc.world.getBlockState(loc), stack); }

    @Redirect(method = "onPlayerDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canDestroy(Lnet/minecraft/block/Block;)Z")) private boolean rdpl$mayDestroy(ItemStack stack, Block blockIn, BlockPos pos) { return stack.canDestroy(blockIn) || ContentHardness.mayBreak(mc.player, mc.world.getBlockState(pos), stack); }

    @Inject(method = "setGameType", at = @At("HEAD")) private void rdpl$rememberPreviousGameType(GameType type, CallbackInfo ci) {
        if (rdpl$gameTypeReceived && type != currentGameType) { rdpl$previousGameType = currentGameType; }
        rdpl$gameTypeReceived = true;
    }

    @Override @Nullable public GameType rdpl$previousGameType() { return rdpl$previousGameType; }
}
