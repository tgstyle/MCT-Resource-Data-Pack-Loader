package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentEndDragon;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonFight.class) public abstract class MixinEndDragonFight {
    @Shadow private ServerBossEvent dragonEvent;
    @Shadow private ServerLevel level;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true) private void rdpl$noFight(CallbackInfo ci) {
        if (!ContentEndDragon.unwanted(level)) { return; }
        dragonEvent.setVisible(false);
        ci.cancel();
    }

    @Inject(method = "tryRespawn", at = @At("HEAD"), cancellable = true) private void rdpl$noRespawn(CallbackInfo ci) {
        if (ContentEndDragon.unwanted(level)) { ci.cancel(); }
    }
}
