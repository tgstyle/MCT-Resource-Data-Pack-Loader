package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentHardness;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class) public abstract class MixinPlayerBreakRestriction {
    @Inject(method = "blockActionRestricted", at = @At("RETURN"), cancellable = true)
    private void rdpl$adventureMining(Level level, BlockPos pos, GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || gameMode != GameType.ADVENTURE) { return; }
        Player self = (Player) (Object) this;
        if (ContentHardness.mayBreak(self, level.getBlockState(pos), self.getMainHandItem())) { cir.setReturnValue(false); }
    }
}
