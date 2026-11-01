package mctmods.resourcedatapackloader.mixin.world.level.chunk;

import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPopulateControl;
import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructureMost;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentCityClaim;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.dress.ContentCityTrees;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.layout.CityBergs;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.plot.CityCrown;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.plot.CityPlotSeams;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentBedrock;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentRoughGround;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentSkyDecoration;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentVoidWorld;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.RegistryAccess;
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

@Mixin(ChunkGenerator.class) public abstract class ChunkGeneratorMixin {
    @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true) private void rdpl$structureMost(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true, name = "selected") StructureSet.StructureSelectionEntry selected, @Local(argsOnly = true, name = "structureManager") StructureManager structureManager, @Local(argsOnly = true, name = "registryAccess") RegistryAccess registryAccess, @Local(argsOnly = true, name = "sourceChunkPos") ChunkPos sourceChunkPos) {
        ContentPregen.worldgenMoved();
        if (ContentPopulateControl.refusesStructure((ChunkGenerator) (Object) this, selected.structure().value()) || ContentStructureMost.refuses(selected.structure().value(), structureManager, registryAccess, sourceChunkPos) || ContentVoidWorld.voidRefuses(structureManager.level, selected.structure().value()) || ContentStructureMaps.refuses(structureManager.level, selected.structure().value())) { cir.setReturnValue(false); }
    }

    @ModifyVariable(method = "tryGenerateStructure", at = @At("STORE"), name = "start") private StructureStart rdpl$cityGround(StructureStart start, StructureSet.StructureSelectionEntry selected, StructureManager structureManager, RegistryAccess registryAccess, RandomState randomState, StructureTemplateManager structureTemplateManager, long seed) {
        ChunkGenerator generator = (ChunkGenerator) (Object) this;
        return ContentCityClaim.overrides(start, seed, generator, randomState, registryAccess) || ContentRoughGround.refuses(start, seed, generator, randomState, registryAccess) ? StructureStart.INVALID_START : start;
    }

    @Inject(method = "tryGenerateStructure", at = @At("RETURN")) private void rdpl$founded(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true, name = "selected") StructureSet.StructureSelectionEntry selected, @Local(argsOnly = true, name = "structureManager") StructureManager structureManager, @Local(argsOnly = true, name = "registryAccess") RegistryAccess registryAccess, @Local(argsOnly = true, name = "sourceChunkPos") ChunkPos sourceChunkPos) {
        ContentPregen.worldgenMoved();
        if (cir.getReturnValueZ()) { ContentStructureMost.founded(selected.structure().value(), structureManager, registryAccess, sourceChunkPos); }
    }

    @Inject(method = "applyBiomeDecoration(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/StructureManager;)V", at = @At("HEAD")) private void rdpl$born(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        ContentPregen.worldgenMoved();
        ContentBedrock.flattenChunk(level, chunk.getPos());
        if (ContentVoidWorld.voidApplies(level.getLevel())) { return; }
        CityBergs.cleared(level, chunk.getPos(), structureManager);
        CityCrown.born(level, chunk.getPos(), structureManager);
    }

    @Inject(method = "applyBiomeDecoration(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/StructureManager;)V", at = @At("TAIL")) private void rdpl$dressed(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo ci) {
        ContentPregen.worldgenMoved();
        if (ContentVoidWorld.voidApplies(level.getLevel())) { return; }
        ContentCityTrees.dressed(level, chunk.getPos(), structureManager);
        CityPlotSeams.pits(level, chunk.getPos(), structureManager);
        CityCrown.crowned(level, chunk.getPos(), structureManager);
        ContentSkyDecoration.decorate(level, (ChunkGenerator) (Object) this, chunk);
    }
}
