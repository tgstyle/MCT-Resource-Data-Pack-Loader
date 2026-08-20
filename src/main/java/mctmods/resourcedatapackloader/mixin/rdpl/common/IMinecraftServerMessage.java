package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftServer.class) public interface IMinecraftServerMessage {
    @Invoker("setUserMessage") void rdpl$setUserMessage(String message);

    @Accessor("percentDone") void rdpl$setPercentDone(int percent);

    @Accessor("currentTask") void rdpl$setCurrentTask(String task);
}
