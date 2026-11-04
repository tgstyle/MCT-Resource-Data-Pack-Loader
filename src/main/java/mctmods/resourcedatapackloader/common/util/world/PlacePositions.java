package mctmods.resourcedatapackloader.common.util.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;

public final class PlacePositions {
    private PlacePositions() {}

    public static List<BlockPos> of(BlockEvent.EntityPlaceEvent event) {
        if (!(event instanceof BlockEvent.EntityMultiPlaceEvent multi)) { return List.of(event.getPos()); }
        List<BlockPos> found = new ArrayList<>();
        for (BlockSnapshot snapshot : multi.getReplacedBlockSnapshots()) { found.add(snapshot.getPos()); }
        return found;
    }
}
