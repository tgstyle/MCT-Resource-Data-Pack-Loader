package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.world.NextTickListEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;

final class CubeTickIndex<C extends Collection<NextTickListEntry>> {
    private final Map<CubePos, C> byCube = new HashMap<>();
    private final Supplier<C> factory;

    CubeTickIndex(Supplier<C> factory) { this.factory = factory; }

    @Nullable C get(CubePos pos) { return byCube.get(pos); }

    void add(NextTickListEntry e) { byCube.computeIfAbsent(CubePos.fromBlockCoords(e.position), x -> factory.get()).add(e); }

    void remove(NextTickListEntry e) {
        CubePos pos = CubePos.fromBlockCoords(e.position);
        C entries = byCube.get(pos);
        entries.remove(e);
        if (entries.isEmpty()) { byCube.remove(pos); }
    }

    void clear() { byCube.clear(); }
}
