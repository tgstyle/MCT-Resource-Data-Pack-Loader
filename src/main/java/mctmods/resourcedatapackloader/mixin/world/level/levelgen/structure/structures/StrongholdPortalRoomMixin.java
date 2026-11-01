package mctmods.resourcedatapackloader.mixin.world.level.levelgen.structure.structures;

import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructureSpawners;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StrongholdPieces.PortalRoom.class) public abstract class StrongholdPortalRoomMixin {
    @Redirect(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/util/RandomSource;)V")) private void rdpl$spawner(SpawnerBlockEntity spawner, EntityType<?> type, RandomSource random) { ContentStructureSpawners.place(spawner, ContentStructureSpawners.STRONGHOLDS, type, random); }
}
