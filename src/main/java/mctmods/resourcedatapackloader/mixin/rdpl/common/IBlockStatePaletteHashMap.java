package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.world.chunk.BlockStatePaletteHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockStatePaletteHashMap.class) public interface IBlockStatePaletteHashMap { @Accessor("statePaletteMap") IntIdentityHashBiMap<IBlockState> rdpl$statePaletteMap(); }
