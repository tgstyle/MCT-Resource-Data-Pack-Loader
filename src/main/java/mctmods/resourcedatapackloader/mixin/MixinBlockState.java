package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("deprecation") @Mixin(BlockStateContainer.StateImplementation.class)
public abstract class MixinBlockState {
    @Unique private Material rdpl$material;

    @Redirect(method = "getMaterial", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getMaterial(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/material/Material;"))
    private Material rdpl$materialOnce(Block block, IBlockState state) {
        Material had = rdpl$material;
        return had == null ? rdpl$workOutMaterial(block, state) : had;
    }

    @Unique private Material rdpl$workOutMaterial(Block block, IBlockState state) {
        if (ContentChunkWatch.watching()) { ContentChunkWatch.materialLookup(); }

        rdpl$material = block.getMaterial(state);
        return rdpl$material;
    }
}
