package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.rubic.world.CubeWatchEvent;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.network.MessageMultipartDesc;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import codechicken.lib.packet.PacketCustom;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.handler.MultipartSaveLoad;
import io.netty.buffer.Unpooled;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.List;

public final class CbmpRubicParts {
    private CbmpRubicParts() {}

    public static void register() { if (CompatHooks.present("forgemultipartcbe", "Forge Multipart only describes its parts to a client when a whole chunk is watched, so on rubic worlds every cube sent to a player carries its part descriptions with it")) { MinecraftForge.EVENT_BUS.register(new Handler()); } }

    public static boolean rubic(World world) { return world instanceof IRubicWorld && ((IRubicWorld) world).rdpl$isRubicWorld(); }

    public static final class Client {
        private Client() {}

        public static void apply(World world, BlockPos pos, byte[] data) { TileMultipart.handleDescPacket(world, pos, new PacketCustom(Unpooled.wrappedBuffer(data))); }
    }

    public static final class Handler {
        @SubscribeEvent public void onCubeWatch(CubeWatchEvent event) {
            ICube cube = event.getCube();
            if (cube == null || cube.getTileEntityMap().isEmpty()) { return; }
            List<TileEntity> held = new ArrayList<>(cube.getTileEntityMap().values());
            for (TileEntity tile : held) {
                if (tile instanceof MultipartSaveLoad.TileNBTContainer) { convert((MultipartSaveLoad.TileNBTContainer) tile); }
            }
            for (TileEntity tile : new ArrayList<>(cube.getTileEntityMap().values())) {
                if (tile instanceof TileMultipart) { RDPLNetwork.sendTo(describe((TileMultipart) tile), event.getPlayer()); }
            }
        }

        private static MessageMultipartDesc describe(TileMultipart tile) {
            PacketCustom out = new PacketCustom(RDPLNetwork.CHANNEL, 1);
            tile.writeDesc(out);
            byte[] data = new byte[out.readableBytes()];
            out.getBytes(out.readerIndex(), data);
            return new MessageMultipartDesc(tile.getPos(), data);
        }

        private static void convert(MultipartSaveLoad.TileNBTContainer container) {
            if (container.tag() == null) { return; }
            World world = container.getWorld();
            BlockPos pos = container.getPos();
            TileMultipart tile = TileMultipart.createFromNBT(container.tag());
            if (tile == null) {
                world.setBlockToAir(pos);
                return;
            }
            tile.validate();
            world.setTileEntity(pos, tile);
            tile.notifyTileChange();
        }
    }
}
