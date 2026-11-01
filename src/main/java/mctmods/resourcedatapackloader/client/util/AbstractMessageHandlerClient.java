package mctmods.resourcedatapackloader.client.util;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class AbstractMessageHandlerClient {
    private AbstractMessageHandlerClient() {}

    public static IThreadListener taskQueue() { return Minecraft.getMinecraft(); }

    public static EntityPlayer player(MessageContext c) { return c.side.isClient() ? Minecraft.getMinecraft().player : c.getServerHandler().player; }

    @Nullable public static World world() {
        return Minecraft.getMinecraft().getConnection() == null ? null :
                Minecraft.getMinecraft().getConnection().world;
    }
}
