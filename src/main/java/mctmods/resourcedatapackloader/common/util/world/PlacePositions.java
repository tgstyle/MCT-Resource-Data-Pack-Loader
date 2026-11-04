package mctmods.resourcedatapackloader.common.util.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class PlacePositions {
    private PlacePositions() {}

    public static List<BlockPos> of(BlockEvent.EntityPlaceEvent event) {
        List<BlockPos> found = new ArrayList<>();
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multi) {
            for (BlockSnapshot snapshot : multi.getReplacedBlockSnapshots()) { found.add(snapshot.getPos()); }
        } else { found.add(event.getPos()); }
        return found;
    }
}
