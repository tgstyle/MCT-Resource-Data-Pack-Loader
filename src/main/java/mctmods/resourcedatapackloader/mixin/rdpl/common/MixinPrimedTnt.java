package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.IContentTnt;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;

@Mixin(PrimedTnt.class) public abstract class MixinPrimedTnt implements IContentTnt {
    @Unique @Nullable private EntityType<?> rdpl$lighter;

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    private void rdpl$litBy(Level level, double x, double y, double z, @Nullable LivingEntity owner, CallbackInfo ci) {
        if (owner != null) { rdpl$lighter = owner.getType(); }
    }

    @Override @Nullable public EntityType<?> rdpl$lighter() { return rdpl$lighter; }

    @Override public void rdpl$lighter(@Nullable EntityType<?> lighter) { rdpl$lighter = lighter; }
}
