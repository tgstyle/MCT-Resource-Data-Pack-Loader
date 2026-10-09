package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentDressLight {
    private static final Set<Chunk> DARKENED = new LinkedHashSet<>();

    private ContentDressLight() {}

    public static boolean quenches(World world) { return !world.isRemote && !GenHeights.rubic(world) && ContentLightArea.outside(world); }

    public static void darken(Chunk chunk) {
        if (chunk.isLightPopulated()) { DARKENED.add(chunk); }
        chunk.setLightPopulated(false);
    }

    public static void relight() {
        if (DARKENED.isEmpty()) { return; }
        List<Chunk> chunks = new ArrayList<>(DARKENED);
        DARKENED.clear();
        for (Chunk chunk : chunks) {
            if (chunk.isLoaded()) { relit(chunk); }
        }
    }

    public static boolean relit(@Nullable Chunk chunk) {
        if (chunk == null || chunk.isLightPopulated() || !chunk.isTerrainPopulated()) { return false; }
        chunk.checkLight();
        if (!chunk.isLightPopulated()) { return false; }
        chunk.markDirty();
        return true;
    }
}
