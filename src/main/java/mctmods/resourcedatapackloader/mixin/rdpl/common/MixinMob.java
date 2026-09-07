package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class) public abstract class MixinMob {
    @Inject(method = "getLootTable", at = @At("HEAD"), cancellable = true)
    private void rdpl$variantLoot(CallbackInfoReturnable<ResourceKey<LootTable>> cir) {
        Mob self = (Mob) (Object) this;
        EntityVariantDef def = ContentEntities.def(self);
        if (def == null) { return; }
        if (!def.lootTable().isEmpty()) {
            ResourceLocation table = ResourceLocation.tryParse(def.lootTable());
            if (table != null) { cir.setReturnValue(ResourceKey.create(Registries.LOOT_TABLE, table)); }
            return;
        }
        EntityType<?> base = ContentEntities.base(self.getType());
        if (base != null) { cir.setReturnValue(base.getDefaultLootTable()); }
    }

    @Inject(method = "getAmbientSound", at = @At("HEAD"), cancellable = true)
    private void rdpl$ambientSound(CallbackInfoReturnable<SoundEvent> cir) {
        SoundEvent sound = ContentEntities.sound((Mob) (Object) this, ContentEntities.AMBIENT);
        if (sound != null) { cir.setReturnValue(sound); }
    }

    @Inject(method = "getMaxFallDistance", at = @At("HEAD"), cancellable = true)
    private void rdpl$maxFall(CallbackInfoReturnable<Integer> cir) {
        EntityVariantDef def = ContentEntities.def((Mob) (Object) this);
        if (def != null && def.physics().maxFallHeight() >= 0) { cir.setReturnValue(def.physics().maxFallHeight()); }
    }

    @Inject(method = "canBeLeashed()Z", at = @At("HEAD"), cancellable = true)
    private void rdpl$leashable(CallbackInfoReturnable<Boolean> cir) {
        EntityVariantDef def = ContentEntities.def((Mob) (Object) this);
        if (def != null && def.flags().leashable()) { cir.setReturnValue(true); }
    }

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void rdpl$slowThinking(CallbackInfo ci) {
        if (ContentEntityTicks.thinksSlower((Mob) (Object) this)) { ci.cancel(); }
    }

    @Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true)
    private void rdpl$steered(CallbackInfoReturnable<LivingEntity> cir) {
        Mob self = (Mob) (Object) this;
        if (cir.getReturnValue() == null && ContentEntities.steerable(self) && self.getFirstPassenger() instanceof Player player) { cir.setReturnValue(player); }
    }
}
