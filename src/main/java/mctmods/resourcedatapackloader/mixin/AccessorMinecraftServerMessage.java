package mctmods.resourcedatapackloader.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftServer.class)
public interface AccessorMinecraftServerMessage {
    @Invoker("setUserMessage") void rdpl$setUserMessage(String message);
}
