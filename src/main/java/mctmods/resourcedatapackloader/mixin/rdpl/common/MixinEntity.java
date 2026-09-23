package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class) public abstract class MixinEntity {
    @Inject(method = "getGravity", at = @At("RETURN"), cancellable = true)
    private void rdpl$worldGravity(CallbackInfoReturnable<Double> cir) {
        double base = cir.getReturnValueD();
        Entity self = (Entity) (Object) this;
        if (base != 0.0D && rdpl$scaled(self)) {
            double factor = ContentPhysics.gravity(self.level());
            if (factor != 1.0D) { cir.setReturnValue(base * factor); }
        }
    }

    @Unique private static boolean rdpl$scaled(Entity self) {
        if (self instanceof Squid || self instanceof ThrownPotion || self instanceof ThrownExperienceBottle) { return false; }
        if (self instanceof LivingEntity living) { return ContentPhysics.openAir(living); }
        return self instanceof ItemEntity || self instanceof ExperienceOrb || self instanceof PrimedTnt || self instanceof FallingBlockEntity || self instanceof AbstractArrow || self instanceof ThrowableProjectile;
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void rdpl$legacyForgeData(CompoundTag tag, CallbackInfo ci) {
        CompoundTag forge = tag.getCompound("ForgeData");
        if (forge.isEmpty()) { return; }
        CompoundTag data = ((Entity) (Object) this).getPersistentData();
        for (String key : forge.getAllKeys()) {
            Tag held = forge.get(key);
            if (held != null && !data.contains(key)) { data.put(key, held.copy()); }
        }
    }
}
