package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.CityBergs;
import mctmods.resourcedatapackloader.content.worldgen.CityCrown;
import mctmods.resourcedatapackloader.content.worldgen.CityPlotSeams;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityTrees;
import mctmods.resourcedatapackloader.content.worldgen.ContentPopulateControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentRoughGround;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMost;
import mctmods.resourcedatapackloader.content.worldgen.ContentBedrock;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class) public abstract class MixinChunkGenerator {
    @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true)
    private void rdpl$structureMost(StructureSet.StructureSelectionEntry structureSelectionEntry, StructureManager structureManager, RegistryAccess registryAccess, RandomState random, StructureTemplateManager structureTemplateManager, long seed, ChunkAccess chunk, ChunkPos chunkPos, SectionPos sectionPos, CallbackInfoReturnable<Boolean> cir) {
        ContentPregen.worldgenMoved();
        if (ContentPopulateControl.refusesStructure(ChunkGenerator.class.cast(this), structureSelectionEntry.structure().value()) || ContentStructureMost.refuses(structureSelectionEntry.structure().value(), structureManager, registryAccess, chunkPos) || ContentVoidWorld.voidRefuses(((IStructureManager) structureManager).rdpl$level(), structureSelectionEntry.structure().value()) || ContentStructureMaps.refuses(((IStructureManager) structureManager).rdpl$level(), structureSelectionEntry.structure().value())) { cir.setReturnValue(false); }
    }

    @ModifyVariable(method = "tryGenerateStructure", at = @At("STORE"))
    private StructureStart rdpl$cityGround(StructureStart structurestart, StructureSet.StructureSelectionEntry structureSelectionEntry, StructureManager structureManager, RegistryAccess registryAccess, RandomState random, StructureTemplateManager structureTemplateManager, long seed) {
        return ContentCityClaim.overrides(structurestart, seed, ChunkGenerator.class.cast(this), random, registryAccess) || ContentRoughGround.refuses(structurestart, seed, ChunkGenerator.class.cast(this), random, registryAccess) ? StructureStart.INVALID_START : structurestart;
    }

    @Inject(method = "tryGenerateStructure", at = @At("RETURN"))
    private void rdpl$founded(StructureSet.StructureSelectionEntry structureSelectionEntry, StructureManager structureManager, RegistryAccess registryAccess, RandomState random, StructureTemplateManager structureTemplateManager, long seed, ChunkAccess chunk, ChunkPos chunkPos, SectionPos sectionPos, CallbackInfoReturnable<Boolean> cir) {
        ContentPregen.worldgenMoved();
        if (cir.getReturnValueZ()) { ContentStructureMost.founded(structureSelectionEntry.structure().value(), structureManager, registryAccess, chunkPos); }
    }

    @Inject(method = "applyBiomeDecoration", at = @At("HEAD"))
    private void rdpl$born(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        ContentPregen.worldgenMoved();
        ContentBedrock.flattenChunk(level, chunk.getPos());
        if (ContentVoidWorld.voidApplies(level.getLevel())) { return; }
        CityBergs.cleared(level, chunk.getPos(), structureManager);
        CityCrown.born(level, chunk.getPos(), structureManager);
    }

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
    private void rdpl$dressed(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        ContentPregen.worldgenMoved();
        if (ContentVoidWorld.voidApplies(level.getLevel())) { return; }
        ContentCityTrees.dressed(level, chunk.getPos(), structureManager);
        CityPlotSeams.pits(level, chunk.getPos(), structureManager);
        CityCrown.crowned(level, chunk.getPos(), structureManager);
    }
}
