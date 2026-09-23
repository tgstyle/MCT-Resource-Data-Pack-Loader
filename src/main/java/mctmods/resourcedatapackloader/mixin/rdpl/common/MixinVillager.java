package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.extra.ContentVillagers;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;

@Mixin(Villager.class) public abstract class MixinVillager {
    @Inject(method = "finalizeSpawn", at = @At("TAIL")) private void rdpl$joblessProfession(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) { ContentVillagers.spawned(Villager.class.cast(this), spawnType); }
}
