package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.lighting.LightingManager;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IHeightMap;

import com.google.common.base.Throwables;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientHeightMap implements IHeightMap {
    private final Chunk column;
    private final HeightMap hmap;

    public ClientHeightMap(Chunk column, int[] heightmap) {
        this.column = column;
        this.hmap = new HeightMap(heightmap);
    }

    @Override public void onOpacityChange(int localX, int blockY, int localZ, int opacity) {
        writeNewTopBlockY(localX, blockY, localZ, opacity, getTopBlockY(localX, localZ));
    }

    private void writeNewTopBlockY(int localX, int changeY, int localZ, int newOpacity, int oldTopY) {
        if (addedTopBlock(changeY, newOpacity, oldTopY)) {
            this.setHeight(localX, localZ, changeY);
            return;
        }
        if (!changedTopToTransparent(changeY, newOpacity, oldTopY)) { return; }
        assert !(newOpacity == 0 && oldTopY < changeY) : "Changed transparent block into transparent!";
        int newTop = oldTopY - 1;
        while (column.getBlockLightOpacity(new BlockPos(localX, newTop, localZ)) == 0 && newTop > oldTopY - LightingManager.MAX_CLIENT_LIGHT_SCAN_DEPTH){
            newTop--;
        }
        this.setHeight(localX, localZ, newTop);
    }

    private boolean changedTopToTransparent(int changeY, int newOpacity, int oldTopY) { return newOpacity == 0 && changeY == oldTopY; }

    private boolean addedTopBlock(int changeY, int newOpacity, int oldTopY) { return (changeY > oldTopY) && newOpacity != 0; }

    @Override public int getTopBlockY(int localX, int localZ) { return hmap.get(getIndex(localX, localZ)); }

    @Override public int getTopBlockYBelow(int localX, int localZ, int blockY) { throw new UnsupportedOperationException("Not implemented"); }

    public void setHeight(int localX, int localZ, int height) { hmap.set(getIndex(localX, localZ), height); }

    public byte[] getData() {
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(buf);
            for (int i = 0; i < Cube.SIZE * Cube.SIZE; i++) { out.writeInt(hmap.get(i)); }
            out.close();
            return buf.toByteArray();
        } catch (IOException e) {
            Throwables.throwIfUnchecked(e);
            throw new AssertionError();
        }
    }

    public void loadData(PacketBuffer in) {
        for (int i = 0; i < Cube.SIZE * Cube.SIZE; i++) { hmap.set(i, in.readInt()); }
    }

    private static int getIndex(int localX, int localZ) { return (localZ << 4) | localX; }
}
