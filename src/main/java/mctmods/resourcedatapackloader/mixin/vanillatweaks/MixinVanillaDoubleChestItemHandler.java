package mctmods.resourcedatapackloader.mixin.vanillatweaks;

import mctmods.resourcedatapackloader.content.block.ContentChests;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.VanillaDoubleChestItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = VanillaDoubleChestItemHandler.class, remap = false) public abstract class MixinVanillaDoubleChestItemHandler {
    @Redirect(method = "get", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", remap = true)) private static IBlockState rdpl$pairedBeside(World world, BlockPos pos, TileEntityChest chest) { return ContentChests.seen(world, chest.getPos(), pos, world.getBlockState(pos)); }
}
