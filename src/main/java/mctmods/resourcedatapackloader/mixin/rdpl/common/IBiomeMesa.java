package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.BiomeMesa;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BiomeMesa.class) public interface IBiomeMesa {
    @Accessor("clayBands") IBlockState[] rdpl$clayBands();
    @Accessor("worldSeed") long rdpl$worldSeed();
    @Accessor("hasForest") boolean rdpl$hasForest();
    @Invoker("generateBands") void rdpl$generateBands(long seed);
    @Invoker("getBand") IBlockState rdpl$getBand(int x, int y, int z);
}
