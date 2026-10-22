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
    @Redirect(method = "canSpawnForCategory(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/ChunkPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I")) private int rdpl$ceiling(MobCategory category) { return ContentSpawnSwitch.ceiling((NaturalSpawner.SpawnState) (Object) this, category); }

    @Redirect(method = "canSpawnForCategory(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/ChunkPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LocalMobCapCalculator;canSpawn(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/ChunkPos;)Z")) private boolean rdpl$scoped(LocalMobCapCalculator calculator, MobCategory category, ChunkPos pos) {
        ContentSpawnSwitch.scope(ContentSpawnSwitch.ceiling((NaturalSpawner.SpawnState) (Object) this, category));
        try { return calculator.canSpawn(category, pos); }
        finally { ContentSpawnSwitch.unscope(); }
    }
}
