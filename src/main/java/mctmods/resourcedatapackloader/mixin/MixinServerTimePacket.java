package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public abstract class MixinServerTimePacket {
    @Redirect(method = "updateTimeLightAndEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Ljava/lang/String;)Z"))
    private boolean rdpl$holdTheSkyStill(GameRules rules, String name) {
        if ("doDaylightCycle".equals(name)) {
            for (WorldServer world : ((MinecraftServer) (Object) this).worlds) {
                if (ContentPregen.busyIn(world)) { return false; }
            }
        }
        return rules.getBoolean(name);
    }
}
