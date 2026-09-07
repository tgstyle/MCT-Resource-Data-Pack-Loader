package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentThreat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NearestAttackableTargetGoal.class) public abstract class MixinNearestAttackableTargetGoal extends TargetGoal {
    @Shadow protected LivingEntity target;

    protected MixinNearestAttackableTargetGoal(Mob mob, boolean mustSee) { super(mob, mustSee); }

    @Inject(method = "findTarget", at = @At("TAIL"))
    private void rdpl$docile(CallbackInfo ci) {
        if (ContentThreat.docile(target, mob)) { target = null; }
    }
}
