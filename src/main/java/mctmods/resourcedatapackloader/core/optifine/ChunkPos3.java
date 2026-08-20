package mctmods.resourcedatapackloader.core.optifine;

import net.minecraft.util.math.ChunkPos;

public class ChunkPos3 extends ChunkPos {
    private final int y;

    public ChunkPos3(int x, int y, int z) {
        super(x, z);
        this.y = y;
    }

    @Override public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ChunkPos3)) { return false; }
        if (!super.equals(o)) { return false; }
        ChunkPos3 chunkPos3 = (ChunkPos3) o;
        if (x != chunkPos3.x) { return false; }
        if (z != chunkPos3.z) { return false; }
        return y == chunkPos3.y;
    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + x;
        result = 31 * result + z;
        result = 31 * result + y;
        return result;
    }
}
