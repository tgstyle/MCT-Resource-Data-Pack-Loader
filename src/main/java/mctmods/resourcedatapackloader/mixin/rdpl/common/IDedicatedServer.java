package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DedicatedServer.class) public interface IDedicatedServer {
    @Accessor("settings") DedicatedServerSettings rdpl$settings();
}
