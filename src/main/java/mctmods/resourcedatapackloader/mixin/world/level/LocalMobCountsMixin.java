package mctmods.resourcedatapackloader.mixin.world.level;

import mctmods.resourcedatapackloader.common.rules.ContentSpawnSwitch;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.level.LocalMobCapCalculator$MobCounts") public abstract class LocalMobCountsMixin {
    @Redirect(method = "canSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I")) private int rdpl$localMax(MobCategory category) { return ContentSpawnSwitch.scoped(category); }
}
