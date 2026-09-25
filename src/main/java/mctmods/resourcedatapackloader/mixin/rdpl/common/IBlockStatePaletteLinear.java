package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.BlockStatePaletteLinear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockStatePaletteLinear.class) public interface IBlockStatePaletteLinear {
    @Accessor("states") IBlockState[] rdpl$states();

    @Accessor("arraySize") int rdpl$arraySize();
}
