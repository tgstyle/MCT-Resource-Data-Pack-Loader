package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.rubic.Rubic;

import mctmods.resourcedatapackloader.util.interfaces.IBucketSorterEntry;
import mctmods.resourcedatapackloader.util.interfaces.IXZAddressable;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Collection;
import java.util.function.Supplier;

public class WatchersSortingList2D<T extends IBucketSorterEntry & IXZAddressable> extends WatchersSortingList<T> {
    private static final int BUCKET_COUNT = (int) (Rubic.MAX_RENDER_DISTANCE * Math.sqrt(2)) + 1;

    public WatchersSortingList2D(int intrusiveCollectionId, Supplier<Collection<EntityPlayer>> playersSupplier) {
        super(BUCKET_COUNT, intrusiveCollectionId, playersSupplier);
    }

    @Override protected int coordinatesPerPlayer() { return 2; }

    @Override protected void storePlayerPosition(int index, EntityPlayer player) {
        playerPositions[index] = Coords.blockToCube(player.posX);
        playerPositions[index + 1] = Coords.blockToCube(player.posZ);
    }

    @Override protected int computeBucketIdx(T element) {
        if (playerPositions.length == 0) { return bucketCount - 1; }
        long x = element.getX();
        long z = element.getZ();
        long dx = x - playerPositions[0];
        long dz = z - playerPositions[1];
        long distSqMin = dx * dx + dz * dz;
        for (int i = 2; i < playerPositions.length; i += 2) {
            dx = x - playerPositions[i];
            dz = z - playerPositions[i + 1];
            long distSq = dx * dx + dz * dz;
            if (distSq < distSqMin) { distSqMin = distSq; }
        }
        return bucketFromDistanceSq(distSqMin);
    }
}
