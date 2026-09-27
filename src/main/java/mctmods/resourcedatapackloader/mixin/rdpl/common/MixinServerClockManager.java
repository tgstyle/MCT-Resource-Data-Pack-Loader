package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;
import java.util.Map;

@Mixin(ServerClockManager.class) public abstract class MixinServerClockManager {
    @Inject(method = "getTotalTicks(Lnet/minecraft/core/Holder;)J", at = @At("HEAD"), cancellable = true)
    private void rdpl$lockedTime(Holder<WorldClock> definition, CallbackInfoReturnable<Long> cir) {
        long locked = ContentTerrain.lockedTime(definition);
        if (locked >= 0) { cir.setReturnValue(locked); }
    }

    @Inject(method = "getPartialTick(Lnet/minecraft/core/Holder;)F", at = @At("HEAD"), cancellable = true)
    private void rdpl$lockedPartial(Holder<WorldClock> definition, CallbackInfoReturnable<Float> cir) {
        if (ContentTerrain.lockedTime(definition) >= 0) { cir.setReturnValue(0.0F); }
    }

    @ModifyExpressionValue(method = {"createFullSyncPacket()Lnet/minecraft/network/protocol/game/ClientboundSetTimePacket;", "modifyClock(Lnet/minecraft/core/Holder;Ljava/util/function/Consumer;)V"}, at = @At(value = "NEW", target = "(JLjava/util/Map;)Lnet/minecraft/network/protocol/game/ClientboundSetTimePacket;"))
    private ClientboundSetTimePacket rdpl$lockedSync(ClientboundSetTimePacket packet) {
        Map<Holder<WorldClock>, ClockNetworkState> updates = new HashMap<>(packet.clockUpdates());
        updates.replaceAll((clock, state) -> {
            long locked = ContentTerrain.lockedTime(clock);
            return locked >= 0 ? new ClockNetworkState(locked, 0.0F, 0.0F) : state;
        });
        return new ClientboundSetTimePacket(packet.gameTime(), updates);
    }
}
