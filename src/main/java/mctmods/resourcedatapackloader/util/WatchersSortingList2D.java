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

    @Override protected long distanceSq(T element, int offset) {
        long dx = element.getX() - playerPositions[offset];
        long dz = element.getZ() - playerPositions[offset + 1];
        return dx * dx + dz * dz;
    }
}
