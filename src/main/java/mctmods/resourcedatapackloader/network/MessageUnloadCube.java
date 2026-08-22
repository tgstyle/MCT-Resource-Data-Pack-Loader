package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.CubeProviderClient;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.CubePos;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageUnloadCube implements IMessage {
    private CubePos cubePos;

    public MessageUnloadCube() {}

    public MessageUnloadCube(CubePos cubePos) { this.cubePos = cubePos; }

    @Override public void fromBytes(ByteBuf in) { this.cubePos = new CubePos(in.readInt(), in.readInt(), in.readInt()); }

    @Override public void toBytes(ByteBuf out) {
        out.writeInt(cubePos.getX());
        out.writeInt(cubePos.getY());
        out.writeInt(cubePos.getZ());
    }

    CubePos getCubePos() { return Preconditions.checkNotNull(cubePos); }

    public static class Handler extends AbstractClientMessageHandler<MessageUnloadCube> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageUnloadCube message, MessageContext ctx) {
            IRubicWorld worldClient = (IRubicWorld) world;
            if (!worldClient.rdpl$isRubicWorld()) { return; }
            if (!(((World) worldClient).getChunkProvider() instanceof CubeProviderClient)) { return; }
            CubeProviderClient cubeCache = (CubeProviderClient) worldClient.rdpl$getCubeCache();

            cubeCache.getCube(message.getCubePos()).markForRenderUpdate();
            cubeCache.unloadCube(message.getCubePos());
        }
    }
}
