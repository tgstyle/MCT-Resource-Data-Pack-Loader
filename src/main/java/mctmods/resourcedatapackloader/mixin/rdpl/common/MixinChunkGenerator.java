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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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
    @Inject(method = "tryGenerateStructure(Lnet/minecraft/world/level/levelgen/structure/StructureSet$StructureSelectionEntry;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/SectionPos;Lnet/minecraft/resources/ResourceKey;)Z", at = @At("HEAD"), cancellable = true)
    private void rdpl$structureMost(StructureSet.StructureSelectionEntry selected, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager structureTemplateManager, long seed, ChunkAccess centerChunk, ChunkPos sourceChunkPos, SectionPos sectionPos, ResourceKey<Level> level, CallbackInfoReturnable<Boolean> cir) {
        ContentPregen.worldgenMoved();
        if (ContentPopulateControl.refusesStructure(ChunkGenerator.class.cast(this), selected.structure().value()) || ContentStructureMost.refuses(selected.structure().value(), structureManager, registryAccess, sourceChunkPos) || ContentVoidWorld.voidRefuses(((IStructureManager) structureManager).rdpl$level(), selected.structure().value()) || ContentStructureMaps.refuses(((IStructureManager) structureManager).rdpl$level(), selected.structure().value())) { cir.setReturnValue(false); }
    }

    @ModifyVariable(method = "tryGenerateStructure(Lnet/minecraft/world/level/levelgen/structure/StructureSet$StructureSelectionEntry;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/SectionPos;Lnet/minecraft/resources/ResourceKey;)Z", at = @At("STORE"), ordinal = 0)
    private StructureStart rdpl$cityGround(StructureStart start, StructureSet.StructureSelectionEntry selected, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager structureTemplateManager, long seed) {
        return ContentCityClaim.overrides(start, seed, ChunkGenerator.class.cast(this), randomState, registryAccess) || ContentRoughGround.refuses(start, seed, ChunkGenerator.class.cast(this), randomState, registryAccess) ? StructureStart.INVALID_START : start;
    }

    @Inject(method = "tryGenerateStructure(Lnet/minecraft/world/level/levelgen/structure/StructureSet$StructureSelectionEntry;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/SectionPos;Lnet/minecraft/resources/ResourceKey;)Z", at = @At("RETURN"))
    private void rdpl$founded(StructureSet.StructureSelectionEntry selected, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager structureTemplateManager, long seed, ChunkAccess centerChunk, ChunkPos sourceChunkPos, SectionPos sectionPos, ResourceKey<Level> level, CallbackInfoReturnable<Boolean> cir) {
        ContentPregen.worldgenMoved();
        if (cir.getReturnValueZ()) { ContentStructureMost.founded(selected.structure().value(), structureManager, registryAccess, sourceChunkPos); }
    }

    @Inject(method = "applyBiomeDecoration(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/StructureManager;)V", at = @At("HEAD"))
    private void rdpl$born(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        ContentPregen.worldgenMoved();
        ContentBedrock.flattenChunk(level, chunk.getPos());
        if (ContentVoidWorld.voidApplies(level.getLevel())) { return; }
        CityBergs.cleared(level, chunk.getPos(), structureManager);
        CityCrown.born(level, chunk.getPos(), structureManager);
    }

    @Inject(method = "applyBiomeDecoration(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/StructureManager;)V", at = @At("TAIL"))
    private void rdpl$dressed(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        ContentPregen.worldgenMoved();
        if (ContentVoidWorld.voidApplies(level.getLevel())) { return; }
        ContentCityTrees.dressed(level, chunk.getPos(), structureManager);
        CityPlotSeams.pits(level, chunk.getPos(), structureManager);
        CityCrown.crowned(level, chunk.getPos(), structureManager);
    }
}
