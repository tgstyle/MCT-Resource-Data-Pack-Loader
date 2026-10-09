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
        return y == ((ChunkPos3) o).y;
    }

    @Override public int hashCode() { return 31 * super.hashCode() + y; }
}
