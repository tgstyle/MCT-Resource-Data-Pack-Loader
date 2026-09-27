package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSpawners;

import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetherFortressPieces.MonsterThrone.class) public abstract class MixinMonsterThrone {
    @Redirect(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/util/RandomSource;)V"))
    private void rdpl$spawner(SpawnerBlockEntity spawner, EntityType<?> type, RandomSource random) { spawner.setEntityId(ContentStructureSpawners.pick(ContentStructureSpawners.FORTRESSES, type, random), random); }
}
