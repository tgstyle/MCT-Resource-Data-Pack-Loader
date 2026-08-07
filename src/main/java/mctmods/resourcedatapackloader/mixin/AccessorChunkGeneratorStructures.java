package mctmods.resourcedatapackloader.mixin;

import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGeneratorOverworld.class)
public interface AccessorChunkGeneratorStructures {
    @Accessor("scatteredFeatureGenerator") MapGenScatteredFeature rdpl$temples();
    @Accessor("mineshaftGenerator") MapGenMineshaft rdpl$mineshafts();
    @Accessor("strongholdGenerator") MapGenStronghold rdpl$strongholds();
    @Accessor("oceanMonumentGenerator") StructureOceanMonument rdpl$monuments();
}
