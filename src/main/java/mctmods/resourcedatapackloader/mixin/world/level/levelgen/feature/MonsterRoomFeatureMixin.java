package mctmods.resourcedatapackloader.mixin.world.level.levelgen.feature;

import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructureSpawners;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentCityClaim;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MonsterRoomFeature.class) public abstract class MonsterRoomFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true) private void rdpl$notIntoABore(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir) { if (ContentCityClaim.intoBore(context.level(), context.origin())) { cir.setReturnValue(false); } }

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/util/RandomSource;)V")) private void rdpl$spawner(SpawnerBlockEntity spawner, EntityType<?> type, RandomSource random) { ContentStructureSpawners.place(spawner, ContentStructureSpawners.DUNGEONS, type, random); }
}
