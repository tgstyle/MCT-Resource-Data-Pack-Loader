package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMost;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class) public abstract class MixinChunkGenerator {
    @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true)
    private void rdpl$structureMost(StructureSet.StructureSelectionEntry entry, StructureManager manager, RegistryAccess registries, RandomState random, StructureTemplateManager templates, long seed, ChunkAccess chunk, ChunkPos pos, SectionPos section, CallbackInfoReturnable<Boolean> cir) {
        if (ContentStructureMost.refuses(entry.structure().value(), manager, registries, pos)) { cir.setReturnValue(false); }
    }

    @Inject(method = "tryGenerateStructure", at = @At("RETURN"))
    private void rdpl$founded(StructureSet.StructureSelectionEntry entry, StructureManager manager, RegistryAccess registries, RandomState random, StructureTemplateManager templates, long seed, ChunkAccess chunk, ChunkPos pos, SectionPos section, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) { ContentStructureMost.founded(entry.structure().value(), manager, registries, pos); }
    }
}
