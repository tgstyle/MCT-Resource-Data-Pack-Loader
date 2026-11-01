package mctmods.resourcedatapackloader.core.network.world;

import mctmods.resourcedatapackloader.client.util.RubicWorldDataMessageClient;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.math.IntRange;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RubicWorldDataMessage implements IMessage {
    private boolean isRubicWorld;
    private int minHeight;
    private int maxHeight;
    private int maxGenerationHeight;

    public RubicWorldDataMessage() {}

    public RubicWorldDataMessage(WorldServer world) {
        this.minHeight = 0;
        this.maxHeight = 256;
        if (GenHeights.rubic(world)) {
            this.isRubicWorld = true;
            this.minHeight = ((IRubicWorld) world).rdpl$getMinHeight();
            this.maxHeight = ((IRubicWorld) world).rdpl$getMaxHeight();
            this.maxGenerationHeight = ((IRubicWorld) world).rdpl$getMaxGenerationHeight();
        }
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.isRubicWorld = buf.readBoolean();
        this.minHeight = buf.readInt();
        this.maxHeight = buf.readInt();
        this.maxGenerationHeight = buf.readInt();
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isRubicWorld);
        buf.writeInt(this.minHeight);
        buf.writeInt(this.maxHeight);
        buf.writeInt(this.maxGenerationHeight);
    }

    public boolean rdpl$isRubicWorld() { return this.isRubicWorld; }

    public int rdpl$getMinHeight() { return this.minHeight; }

    public int rdpl$getMaxHeight() { return this.maxHeight; }

    public int rdpl$getMaxGenerationHeight() { return maxGenerationHeight; }

    public static class Handler extends AbstractClientMessageHandler<RubicWorldDataMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, RubicWorldDataMessage message, MessageContext ctx) {
            if (message.rdpl$isRubicWorld() && !GenHeights.rubic(world)) {
                ((IRubicWorldInternal.IClient) world).rdpl$initRubicWorldClient(
                        new IntRange(message.rdpl$getMinHeight(), message.rdpl$getMaxHeight()),
                        new IntRange(0,message.rdpl$getMaxGenerationHeight())
                );
                RubicWorldDataMessageClient.reloadRenderers(world);
            }
        }
    }
}
