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

    @Override protected long distanceSq(T element, int offset) {
        long dx = element.getX() - playerPositions[offset];
        long dy = element.getY() - playerPositions[offset + 1];
        long dz = element.getZ() - playerPositions[offset + 2];
        long dx2 = dx * dx;
        long dy2 = dy * dy;
        long dz2 = dz * dz;
        return (dx2 | dy2 | dz2) > (long) Integer.MAX_VALUE ? Integer.MAX_VALUE : dx2 + dy2 + dz2;
    }
}
