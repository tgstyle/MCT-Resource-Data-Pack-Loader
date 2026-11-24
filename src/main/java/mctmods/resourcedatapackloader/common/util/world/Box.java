package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.world.gen.structure.StructureBoundingBox;

public class Box {
    protected int x1, y1, z1;
    protected int x2, y2, z2;

    public Box(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public static StructureBoundingBox clip(StructureBoundingBox box, int x0, int y0, int z0, int x1, int y1, int z1, int pad) {
        return new StructureBoundingBox(Math.max(box.minX, x0 - pad), Math.max(box.minY, y0 - pad), Math.max(box.minZ, z0 - pad), Math.min(box.maxX, x1 + pad), Math.min(box.maxY, y1 + pad), Math.min(box.maxZ, z1 + pad));
    }

    public boolean contains(int x, int y, int z) { return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2; }

    public boolean allMatch(IXYZPredicate predicate) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    if (!predicate.test(x, y, z)) { return false; }
                }
            }
        }
        return true;
    }

    @FunctionalInterface public interface IXYZPredicate { boolean test(int x, int y, int z); }
}
