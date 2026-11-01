package mctmods.resourcedatapackloader.mixin.world.clock;

import mctmods.resourcedatapackloader.common.worldgen.surface.ContentTerrain;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerClockManager.class) public abstract class ServerClockManagerMixin {
    @ModifyExpressionValue(method = {"createFullSyncPacket()Lnet/minecraft/network/protocol/game/ClientboundSetTimePacket;", "modifyClock(Lnet/minecraft/core/Holder;Ljava/util/function/Consumer;)V"}, at = @At(value = "NEW", target = "(JLjava/util/Map;)Lnet/minecraft/network/protocol/game/ClientboundSetTimePacket;")) private ClientboundSetTimePacket rdpl$lockedSync(ClientboundSetTimePacket packet) {
        Map<Holder<WorldClock>, ClockNetworkState> updates = new HashMap<>(packet.clockUpdates());
        updates.replaceAll((clock, state) -> {
            long locked = ContentTerrain.lockedTime(clock);
            return locked >= 0 ? new ClockNetworkState(locked, 0.0F, 0.0F) : state;
        });
        return new ClientboundSetTimePacket(packet.gameTime(), updates);
    }
}
