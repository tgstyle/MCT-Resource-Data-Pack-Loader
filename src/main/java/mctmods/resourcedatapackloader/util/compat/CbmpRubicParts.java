package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.rubic.world.CubeWatchEvent;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.util.ContentLog;

import codechicken.lib.packet.PacketCustom;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.handler.MultipartSPH;
import codechicken.multipart.handler.MultipartSaveLoad;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.List;

public final class CbmpRubicParts {
    private CbmpRubicParts() {}

    public static void register() {
        if (!Loader.isModLoaded("forgemultipartcbe")) { return; }
        MinecraftForge.EVENT_BUS.register(new Handler());
        ContentLog.LOGGER.info("Forge Multipart only describes its parts to a client when a whole chunk is watched, so on rubic worlds every cube sent to a player carries its part descriptions with it");
    }

    public static final class Handler {
        @SubscribeEvent public void onCubeWatch(CubeWatchEvent event) {
            ICube cube = event.getCube();
            if (cube == null || cube.getTileEntityMap().isEmpty()) { return; }
            List<TileEntity> held = new ArrayList<>(cube.getTileEntityMap().values());
            for (TileEntity tile : held) {
                if (tile instanceof MultipartSaveLoad.TileNBTContainer) { convert((MultipartSaveLoad.TileNBTContainer) tile); }
            }
            PacketCustom packet = MultipartSPH.getDescPacket(cube.getColumn(), new ArrayList<>(cube.getTileEntityMap().values()).iterator());
            if (packet != null) { packet.sendToPlayer(event.getPlayer()); }
        }

        private static void convert(MultipartSaveLoad.TileNBTContainer container) {
            if (container.tag() == null) { return; }
            World world = container.getWorld();
            BlockPos pos = container.getPos();
            TileMultipart tile = TileMultipart.createFromNBT(container.tag());
            if (tile == null) { world.setBlockToAir(pos); return; }
            tile.validate();
            world.setTileEntity(pos, tile);
            tile.notifyTileChange();
        }
    }
}
