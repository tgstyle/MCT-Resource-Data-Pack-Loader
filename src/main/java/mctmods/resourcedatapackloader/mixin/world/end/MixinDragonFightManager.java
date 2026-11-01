package mctmods.resourcedatapackloader.mixin.world.end;

import mctmods.resourcedatapackloader.common.environment.ContentEndDragon;

import net.minecraft.world.BossInfoServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFightManager.class) public abstract class MixinDragonFightManager {
    @Shadow @Final public BossInfoServer bossInfo;
    @Shadow @Final private WorldServer world;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true) private void rdpl$noFight(CallbackInfo ci) {
        if (ContentEndDragon.wanted(world)) { return; }
        bossInfo.setVisible(false);
        ci.cancel();
    }

    @Inject(method = "respawnDragon()V", at = @At("HEAD"), cancellable = true) private void rdpl$noRespawn(CallbackInfo ci) {
        if (!ContentEndDragon.wanted(world)) { ci.cancel(); }
    }
}
