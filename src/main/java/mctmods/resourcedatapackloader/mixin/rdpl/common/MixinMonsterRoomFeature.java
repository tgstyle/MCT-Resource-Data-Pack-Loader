package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSpawners;

import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MonsterRoomFeature.class) public abstract class MixinMonsterRoomFeature {
    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/util/RandomSource;)V"))
    private void rdpl$spawner(SpawnerBlockEntity spawner, EntityType<?> vanilla, RandomSource random) { spawner.setEntityId(ContentStructureSpawners.pick(ContentStructureSpawners.DUNGEONS, vanilla, random), random); }
}
