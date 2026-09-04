package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.IntRange;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRubicWorldData implements IMessage {
    private boolean isRubicWorld;
    private int minHeight;
    private int maxHeight;
    private int minGenerationHeight;
    private int maxGenerationHeight;

    public MessageRubicWorldData() {}

    public MessageRubicWorldData(WorldServer world) {
        this.minHeight = 0;
        this.maxHeight = 256;
        if (((IRubicWorld) world).rdpl$isRubicWorld()) {
            this.isRubicWorld = true;
            this.minHeight = ((IRubicWorld) world).rdpl$getMinHeight();
            this.maxHeight = ((IRubicWorld) world).rdpl$getMaxHeight();
            this.minGenerationHeight = 0;
            this.maxGenerationHeight = ((IRubicWorld) world).rdpl$getMaxGenerationHeight();
        }
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.isRubicWorld = buf.readBoolean();
        this.minHeight = buf.readInt();
        this.maxHeight = buf.readInt();
        this.minGenerationHeight = buf.readInt();
        this.maxGenerationHeight = buf.readInt();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isRubicWorld);
        buf.writeInt(this.minHeight);
        buf.writeInt(this.maxHeight);
        buf.writeInt(this.minGenerationHeight);
        buf.writeInt(this.maxGenerationHeight);
    }

    public boolean rdpl$isRubicWorld() { return this.isRubicWorld; }

    public int rdpl$getMinHeight() { return this.minHeight; }

    public int rdpl$getMaxHeight() { return this.maxHeight; }

    public int getMinGenerationHeight() { return minGenerationHeight; }

    public int rdpl$getMaxGenerationHeight() { return maxGenerationHeight; }

    public static class Handler extends AbstractClientMessageHandler<MessageRubicWorldData> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageRubicWorldData message, MessageContext ctx) {
            if (message.rdpl$isRubicWorld() && !((IRubicWorld) world).rdpl$isRubicWorld()) {
                ((IRubicWorldInternal.IClient) world).rdpl$initRubicWorldClient(
                        new IntRange(message.rdpl$getMinHeight(), message.rdpl$getMaxHeight()),
                        new IntRange(message.getMinGenerationHeight(), message.rdpl$getMaxGenerationHeight())
                );
                Minecraft.getMinecraft().renderGlobal.setWorldAndLoadRenderers((WorldClient) world);
            }
        }
    }
}
