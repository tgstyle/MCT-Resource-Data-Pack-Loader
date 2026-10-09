package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record CityClip(int minX, int minZ, int maxX, int maxZ) {
    public static CityClip of(BoundingBox box, int fromX, int fromZ, int toX, int toZ) { return new CityClip(Math.max(fromX, box.minX()), Math.max(fromZ, box.minZ()), Math.min(toX, box.maxX()), Math.min(toZ, box.maxZ())); }

    public static CityClip of(BoundingBox box, BoundingBox held, int ring) { return of(box, held.minX() - ring, held.minZ() - ring, held.maxX() + ring, held.maxZ() + ring); }

    public static int first(BoundingBox box, BoundingBox held, boolean alongX) { return alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ()); }

    public static int last(BoundingBox box, BoundingBox held, boolean alongX) { return alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ()); }

    public boolean empty() { return minX > maxX || minZ > maxZ; }
}
