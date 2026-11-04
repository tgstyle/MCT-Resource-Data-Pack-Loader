package mctmods.resourcedatapackloader.common.util.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;

public final class PlacePositions {
    private PlacePositions() {}

    public static List<BlockPos> of(BlockEvent.EntityPlaceEvent event) {
        if (!(event instanceof BlockEvent.EntityMultiPlaceEvent)) { return Collections.singletonList(event.getPos()); }
        List<BlockPos> found = new ArrayList<>();
        for (BlockSnapshot snapshot : ((BlockEvent.EntityMultiPlaceEvent) event).getReplacedBlockSnapshots()) { found.add(snapshot.getPos()); }
        return found;
    }
}
