package mctmods.resourcedatapackloader.mixin.world.level.levelgen.feature;

import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructureSpawners;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentCityClaim;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MonsterRoomFeature.class) public abstract class MonsterRoomFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true) private void rdpl$notIntoABore(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) { if (ContentCityClaim.intoBore(level, origin)) { cir.setReturnValue(false); } }

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/util/RandomSource;)V")) private void rdpl$spawner(SpawnerBlockEntity spawner, EntityType<?> type, RandomSource random) { ContentStructureSpawners.place(spawner, ContentStructureSpawners.DUNGEONS, type, random); }
}
