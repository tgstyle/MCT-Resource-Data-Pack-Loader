package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ai.PathShortcut;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathNavigation.class) public abstract class MixinPathNavigation {
    @Unique private static final float RDPL_STILL_SPEED = 0.06F;

    @Shadow @Final protected Mob mob;
    @Shadow @Final protected Level level;
    @Shadow protected Path path;

    @Inject(method = "followThePath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;doStuckDetection(Lnet/minecraft/world/phys/Vec3;)V"))
    private void rdpl$straightAhead(CallbackInfo ci) {
        if (path != null && mob.getNavigation() instanceof GroundPathNavigation) { PathShortcut.ahead(mob, level, path); }
    }

    @Redirect(method = "doStuckDetection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getSpeed()F", ordinal = 0))
    private float rdpl$stuckTest(Mob asked) { return ContentEntities.def(asked) == null ? asked.getSpeed() : 1.0F; }

    @Redirect(method = "doStuckDetection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getSpeed()F", ordinal = 1))
    private float rdpl$stuckReach(Mob asked) { return ContentEntities.def(asked) == null ? asked.getSpeed() : RDPL_STILL_SPEED; }
}
