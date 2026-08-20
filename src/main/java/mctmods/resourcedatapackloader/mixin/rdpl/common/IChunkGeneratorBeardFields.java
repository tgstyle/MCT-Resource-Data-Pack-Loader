package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.WoodlandMansion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGeneratorOverworld.class) public interface IChunkGeneratorBeardFields {
    @Accessor("woodlandMansionGenerator") WoodlandMansion rdpl$mansions();
    @Accessor("villageGenerator") MapGenVillage rdpl$villages();
}
