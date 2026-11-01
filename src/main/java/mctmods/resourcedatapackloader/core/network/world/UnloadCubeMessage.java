package mctmods.resourcedatapackloader.core.network.world;

import mctmods.resourcedatapackloader.client.util.CubeProviderClient;
import mctmods.resourcedatapackloader.common.util.world.CubePos;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnloadCubeMessage implements IMessage {
    private CubePos cubePos;

    public UnloadCubeMessage() {}

    public UnloadCubeMessage(CubePos cubePos) { this.cubePos = cubePos; }

    @Override public void fromBytes(ByteBuf in) { this.cubePos = new CubePos(in.readInt(), in.readInt(), in.readInt()); }

    @Override public void toBytes(ByteBuf out) {
        out.writeInt(cubePos.getX());
        out.writeInt(cubePos.getY());
        out.writeInt(cubePos.getZ());
    }

    CubePos getCubePos() { return Preconditions.checkNotNull(cubePos); }

    public static class Handler extends AbstractClientMessageHandler<UnloadCubeMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, UnloadCubeMessage message, MessageContext ctx) {
            CubeProviderClient cubeCache = cubeCache(world);
            if (cubeCache == null) { return; }
            cubeCache.getCube(message.getCubePos()).markForRenderUpdate();
            cubeCache.unloadCube(message.getCubePos());
        }
    }
}
