package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.rubic.Rubic;

import mctmods.resourcedatapackloader.util.interfaces.IBucketSorterEntry;
import mctmods.resourcedatapackloader.util.interfaces.IXYZAddressable;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Collection;
import java.util.function.Supplier;

public class WatchersSortingList3D<T extends IBucketSorterEntry & IXYZAddressable> extends WatchersSortingList<T> {
    private static final int BUCKET_COUNT = (int) (Rubic.MAX_RENDER_DISTANCE * Math.sqrt(3)) + 1;

    public WatchersSortingList3D(int intrusiveCollectionId, Supplier<Collection<EntityPlayer>> playersSupplier) {
        super(BUCKET_COUNT, intrusiveCollectionId, playersSupplier);
    }

    @Override protected int coordinatesPerPlayer() { return 3; }

    @Override protected void storePlayerPosition(int index, EntityPlayer player) {
        playerPositions[index] = Coords.blockToCube(player.posX);
        playerPositions[index + 1] = Coords.blockToCube(player.posY);
        playerPositions[index + 2] = Coords.blockToCube(player.posZ);
    }

    @Override protected int computeBucketIdx(T element) {
        if (playerPositions.length == 0) { return bucketCount - 1; }
        long x = element.getX();
        long y = element.getY();
        long z = element.getZ();
        long dx = x - playerPositions[0];
        long dy = y - playerPositions[1];
        long dz = z - playerPositions[2];
        long dx2 = dx * dx;
        long dy2 = dy * dy;
        long dz2 = dz * dz;
        long masked = dx2 | dy2 | dz2;
        long distSqMin = masked > (long) Integer.MAX_VALUE ? Integer.MAX_VALUE : dx2 + dy2 + dz2;
        for (int i = 3; i < playerPositions.length; i += 3) {
            dx = x - playerPositions[i];
            dy = y - playerPositions[i + 1];
            dz = z - playerPositions[i + 2];
            dx2 = dx * dx;
            dy2 = dy * dy;
            dz2 = dz * dz;
            masked = dx2 | dy2 | dz2;
            long distSq = masked > (long) Integer.MAX_VALUE ? Integer.MAX_VALUE : dx2 + dy2 + dz2;
            if (distSq < distSqMin) { distSqMin = distSq; }
        }
        return bucketFromDistanceSq(distSqMin);
    }
}
