package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(NetHandlerPlayServer.class) public class MixinNetHandlerPlayServer {
    @ModifyConstant(method = "isMovePlayerPacketInvalid",
            slice = @Slice(
                    from = @At(value = "INVOKE:LAST", target = "Lnet/minecraft/network/play/client/CPacketPlayer;getY(D)D"),
                    to = @At(value = "INVOKE:LAST", target = "Lnet/minecraft/network/play/client/CPacketPlayer;getZ(D)D")
            ),
            constant = @Constant(doubleValue = 3.0E7D),
            require = 0
    )
    private static double getMaxY(double old) { return Integer.MAX_VALUE - 4096; }
}
