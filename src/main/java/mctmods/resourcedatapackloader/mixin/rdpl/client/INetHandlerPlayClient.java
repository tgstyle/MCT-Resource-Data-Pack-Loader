package mctmods.resourcedatapackloader.mixin.rdpl.client;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetHandlerPlayClient.class) public interface INetHandlerPlayClient { @Accessor WorldClient getWorld(); }
