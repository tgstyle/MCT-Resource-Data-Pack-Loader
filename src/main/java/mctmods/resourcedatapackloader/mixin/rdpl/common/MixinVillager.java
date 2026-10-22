package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.interfaces.ITradeSwitchHolder;
import mctmods.resourcedatapackloader.content.worldgen.ContentTradeSwitch;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD")) private void rdpl$ownSaveIn(ValueOutput output, CallbackInfo ci) { rdpl$guard++; }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL")) private void rdpl$ownSaveOut(ValueOutput output, CallbackInfo ci) {
        rdpl$guard--;
        ContentTradeSwitch.write(this, output);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL")) private void rdpl$readTrades(ValueInput input, CallbackInfo ci) { ContentTradeSwitch.read(this, input); }

    @Inject(method = "finalizeSpawn", at = @At("TAIL")) private void rdpl$joblessProfession(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) { ContentVillagers.spawned((Villager) (Object) this, spawnReason); }
}
