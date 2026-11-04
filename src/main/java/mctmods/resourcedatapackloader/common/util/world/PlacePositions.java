package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import java.util.ArrayList;
import java.util.List;

public final class PlacePositions {
    private PlacePositions() {}

    public static List<BlockPos> of(BlockEvent.EntityPlaceEvent event) {
        if (!(event instanceof BlockEvent.EntityMultiPlaceEvent multi)) { return List.of(event.getPos()); }
        List<BlockPos> found = new ArrayList<>();
        for (BlockSnapshot snapshot : multi.getReplacedBlockSnapshots()) { found.add(snapshot.getPos()); }
        return found;
    }
}
