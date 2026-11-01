package mctmods.resourcedatapackloader.mixin.world.entity;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.EntityStorage;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityTicks;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.EntityVariantDef;

import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class) public abstract class MobMixin {
    @Shadow protected abstract SoundEvent getAmbientSound();
    @Unique private boolean rdpl$slower;

    @Inject(method = "getAttackBoundingBox", at = @At("RETURN"), cancellable = true) private void rdpl$ownReach(CallbackInfoReturnable<AABB> cir) {
        Mob self = (Mob) (Object) this;
        EntityVariantDef def = ContentEntities.def(self);
        if (def == null || def.combat().attackReach() <= 0.0F) { return; }
        double wanted = def.combat().attackReach() - self.getBbWidth() / 2.0D;
        if (wanted > 0.0D) { cir.setReturnValue(cir.getReturnValue().inflate(wanted, 0.0D, wanted)); }
    }

    @Inject(method = "getLootTable", at = @At("HEAD"), cancellable = true) private void rdpl$variantLoot(CallbackInfoReturnable<Optional<ResourceKey<LootTable>>> cir) {
        Mob self = (Mob) (Object) this;
        EntityVariantDef def = ContentEntities.def(self);
        if (def == null) { return; }
        if (!def.lootTable().isEmpty()) {
            Identifier table = Identifier.tryParse(def.lootTable());
            if (table != null) { cir.setReturnValue(Optional.of(ResourceKey.create(Registries.LOOT_TABLE, table))); }
            return;
        }
        EntityType<?> base = ContentEntities.base(self.getType());
        if (base != null) { cir.setReturnValue(base.getDefaultLootTable()); }
    }

    @Redirect(method = "playAmbientSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getAmbientSound()Lnet/minecraft/sounds/SoundEvent;")) private SoundEvent rdpl$ambientSound(Mob self) { return ContentEntities.sound(self, ContentEntities.AMBIENT, this::getAmbientSound); }

    @Inject(method = "getMaxFallDistance", at = @At("HEAD"), cancellable = true) private void rdpl$maxFall(CallbackInfoReturnable<Integer> cir) {
        EntityVariantDef def = ContentEntities.def((Mob) (Object) this);
        if (def != null && def.physics().maxFallHeight() >= 0) { cir.setReturnValue(def.physics().maxFallHeight()); }
    }

    @Inject(method = "canBeLeashed()Z", at = @At("HEAD"), cancellable = true) private void rdpl$leashable(CallbackInfoReturnable<Boolean> cir) {
        EntityVariantDef def = ContentEntities.def((Mob) (Object) this);
        if (def != null && def.flags().leashable()) { cir.setReturnValue(true); }
    }

    @Inject(method = "serverAiStep", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", args = "ldc=sensing"), cancellable = true) private void rdpl$stalled(CallbackInfo ci) { if (EntityStorage.stalled((Mob) (Object) this)) { ci.cancel(); } }

    @Redirect(method = "serverAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;tick()V")) private void rdpl$slowThinking(GoalSelector selector) {
        Mob self = (Mob) (Object) this;
        if (selector == self.targetSelector) { rdpl$slower = ContentEntityTicks.slowedNow(self); }
        if (rdpl$slower) { selector.tickRunningGoals(false); }
        else { selector.tick(); }
    }

    @Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true) private void rdpl$steered(CallbackInfoReturnable<LivingEntity> cir) {
        Mob self = (Mob) (Object) this;
        if (cir.getReturnValue() == null && ContentEntities.steerable(self) && self.getFirstPassenger() instanceof Player player) { cir.setReturnValue(player); }
    }
}
