package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnSwitch;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.SpawnState.class) public abstract class MixinSpawnState {
    @Redirect(method = "canSpawnForCategoryGlobal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I")) private int rdpl$globalCap(MobCategory category) { return ContentSpawnSwitch.ceiling((NaturalSpawner.SpawnState) (Object) this, category); }

    @Redirect(method = "canSpawnForCategoryLocal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LocalMobCapCalculator;canSpawn(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/ChunkPos;)Z")) private boolean rdpl$localCap(LocalMobCapCalculator calculator, MobCategory mobCategory, ChunkPos pos) { return ContentSpawnSwitch.local(calculator, (NaturalSpawner.SpawnState) (Object) this, mobCategory, pos); }
}
