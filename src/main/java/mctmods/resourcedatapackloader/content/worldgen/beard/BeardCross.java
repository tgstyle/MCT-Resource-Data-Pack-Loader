package mctmods.resourcedatapackloader.content.worldgen.beard;

import net.minecraft.world.gen.structure.StructureBoundingBox;
import java.util.List;

public final class BeardCross {
    public static final int NONE = 0;
    public static final int WALK = 1;
    public static final int LINE = 2;
    public static final int CORE = 3;

    public final StructureBoundingBox box;
    public final boolean alongX;
    public final int width;
    public final int center;
    public final int core;
    public final int lines;
    public final int walk;
    public final boolean alley;
    public final boolean oversize;

    private BeardCross(StructureBoundingBox box, boolean alongX, int full, int lines, int walk) {
        this.box = box;
        this.alongX = alongX;
        this.width = (alongX ? box.maxZ - box.minZ : box.maxX - box.minX) + 1;
        this.center = alongX ? (box.minZ + box.maxZ) / 2 : (box.minX + box.maxX) / 2;
        this.oversize = this.width > full;
        this.alley = this.width < full;
        if (this.alley) {
            this.core = (this.width - 1) / 2;
            this.lines = 0;
            this.walk = 0;
        }
        else {
            this.core = (full - 1) / 2 - lines - walk;
            this.lines = lines;
            this.walk = walk;
        }
    }

    public static BeardCross of(StructureBoundingBox box, boolean alongX) {
        return new BeardCross(box, alongX, BeardRoads.pathFullWidth(), BeardRoads.pathLineColumns(), BeardRoads.pathSidewalkWidth());
    }

    public boolean covers(int x, int z) {
        return x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ;
    }

    public int offset(int x, int z) { return Math.abs((alongX ? z : x) - center); }

    public int role(int x, int z) {
        if (oversize || !covers(x, z)) { return NONE; }
        int off = offset(x, z);
        if (off <= core) { return CORE; }
        if (off <= core + lines) { return LINE; }
        if (off <= core + lines + walk) { return WALK; }
        return NONE;
    }

    public int rank(int x, int z) {
        int role = role(x, z);
        if (role == NONE) { return 0; }
        if (alley) { return 500 + width; }
        return role * 1000 + width;
    }

    public boolean middle(int x, int z) { return role(x, z) == CORE && offset(x, z) == 0; }

    public int row(int x, int z) { return alongX ? x : z; }

    public static BeardCross winner(List<BeardCross> shapes, int x, int z) {
        BeardCross best = null;
        int high = 0;
        for (BeardCross shape : shapes) {
            int rank = shape.rank(x, z);
            if (rank <= high) { continue; }
            high = rank;
            best = shape;
        }
        return best;
    }

    public static boolean crossedOver(List<BeardCross> shapes, boolean alongX, int x, int z) {
        for (BeardCross shape : shapes) { if (!shape.alley && shape.alongX != alongX && shape.role(x, z) != NONE) { return true; } }
        return false;
    }
}
