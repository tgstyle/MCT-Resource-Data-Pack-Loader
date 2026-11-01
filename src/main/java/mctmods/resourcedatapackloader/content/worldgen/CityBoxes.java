package mctmods.resourcedatapackloader.content.worldgen;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class CityBoxes {
    private CityBoxes() {}

    public static void add(IntList boxes, int[] values) { boxes.addElements(boxes.size(), values); }

    public static void add(IntList boxes, int fromX, int fromZ, int toX, int toZ) { add(boxes, new int[] {fromX, fromZ, toX, toZ}); }

    public static void add(IntList boxes, BoundingBox box) { add(boxes, box.minX(), box.minZ(), box.maxX(), box.maxZ()); }

    public static BoundingBox flattened(BoundingBox box, int reach) { return new BoundingBox(box.minX() - reach, box.minY(), box.minZ() - reach, box.maxX() + reach, box.maxY(), box.maxZ() + reach); }

    public static BoundingBox well(int minX, int minY, int minZ, int maxY) { return new BoundingBox(minX, minY, minZ, minX + ContentCityWellPiece.SIZE - 1, maxY, minZ + ContentCityWellPiece.SIZE - 1); }
}
