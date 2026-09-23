package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class) public abstract class MixinMob {
    @Shadow protected abstract SoundEvent getAmbientSound();
    @Unique private boolean rdpl$slower;

    @Inject(method = "getLootTable", at = @At("HEAD"), cancellable = true)
    private void rdpl$variantLoot(CallbackInfoReturnable<ResourceLocation> cir) {
        Mob self = Mob.class.cast(this);
        EntityVariantDef def = ContentEntities.def(self);
        if (def == null) { return; }
        if (!def.lootTable().isEmpty()) {
            ResourceLocation table = ResourceLocation.tryParse(def.lootTable());
            if (table != null) { cir.setReturnValue(table); }
            return;
        }
        EntityType<?> base = ContentEntities.base(self.getType());
        if (base != null) { cir.setReturnValue(base.getDefaultLootTable()); }
    }

    @Redirect(method = "playAmbientSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getAmbientSound()Lnet/minecraft/sounds/SoundEvent;"))
    private SoundEvent rdpl$ambientSound(Mob self) {
        SoundEvent sound = ContentEntities.sound(self, ContentEntities.AMBIENT);
        return sound != null ? sound : getAmbientSound();
    }

    @Inject(method = "getMaxFallDistance", at = @At("HEAD"), cancellable = true)
    private void rdpl$maxFall(CallbackInfoReturnable<Integer> cir) {
        EntityVariantDef def = ContentEntities.def(Mob.class.cast(this));
        if (def != null && def.physics().maxFallHeight() >= 0) { cir.setReturnValue(def.physics().maxFallHeight()); }
    }

    @Redirect(method = "serverAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;tick()V"))
    private void rdpl$slowThinking(GoalSelector selector) {
        Mob self = Mob.class.cast(this);
        if (selector == self.targetSelector) { rdpl$slower = ContentEntityTicks.thinksSlower(self); }
        if (rdpl$slower) { selector.tickRunningGoals(false); }
        else { selector.tick(); }
    }

    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    private void rdpl$leashable(Player player, CallbackInfoReturnable<Boolean> cir) {
        EntityVariantDef def = ContentEntities.def(Mob.class.cast(this));
        if (def != null && def.flags().leashable()) { cir.setReturnValue(true); }
    }

    @Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true)
    private void rdpl$steered(CallbackInfoReturnable<LivingEntity> cir) {
        Mob self = Mob.class.cast(this);
        if (cir.getReturnValue() == null && ContentEntities.steerable(self) && self.getFirstPassenger() instanceof Player player) { cir.setReturnValue(player); }
    }
}
