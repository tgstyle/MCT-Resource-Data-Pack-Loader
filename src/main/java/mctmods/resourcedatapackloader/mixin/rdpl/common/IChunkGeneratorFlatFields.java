package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.structure.MapGenStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(ChunkGeneratorFlat.class) public interface IChunkGeneratorFlatFields {
    @Accessor("structureGenerators") Map<String, MapGenStructure> rdpl$structures();
}
