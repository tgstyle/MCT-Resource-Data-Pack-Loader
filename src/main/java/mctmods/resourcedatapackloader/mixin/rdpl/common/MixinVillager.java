package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.interfaces.ITradeSwitchHolder;
import mctmods.resourcedatapackloader.content.worldgen.ContentTradeSwitch;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@Mixin(Villager.class) public abstract class MixinVillager implements ITradeSwitchHolder {
    @Unique private final Map<String, ContentTradeSwitch.Held> rdpl$held = new HashMap<>();
    @Unique private int rdpl$guard;

    @Override public Map<String, ContentTradeSwitch.Held> rdpl$held() { return rdpl$held; }

    @Override public boolean rdpl$own() { return rdpl$guard > 0; }

    @Inject(method = "updateTrades", at = @At("HEAD")) private void rdpl$ownTradesIn(CallbackInfo ci) { rdpl$guard++; }

    @Inject(method = "updateTrades", at = @At("RETURN")) private void rdpl$ownTradesOut(CallbackInfo ci) { rdpl$guard--; }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD")) private void rdpl$ownSaveIn(CompoundTag compound, CallbackInfo ci) { rdpl$guard++; }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL")) private void rdpl$ownSaveOut(CompoundTag compound, CallbackInfo ci) {
        rdpl$guard--;
        ContentTradeSwitch.write((Villager) (Object) this, compound);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL")) private void rdpl$read(CompoundTag compound, CallbackInfo ci) { ContentTradeSwitch.read((Villager) (Object) this, compound); }

    @Inject(method = "finalizeSpawn", at = @At("TAIL")) private void rdpl$joblessProfession(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir) { ContentVillagers.spawned((Villager) (Object) this, reason); }
}
