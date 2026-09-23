package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ContentDressLight {
    private static final Set<Chunk> DARKENED = new LinkedHashSet<>();

    private ContentDressLight() {}

    public static boolean quenches(World world) { return !world.isRemote && !((IRubicWorld) world).rdpl$isRubicWorld() && ContentLightArea.outside(world); }

    public static void darken(Chunk chunk) {
        if (chunk.isLightPopulated()) { DARKENED.add(chunk); }
        chunk.setLightPopulated(false);
    }

    public static void relight() {
        if (DARKENED.isEmpty()) { return; }
        List<Chunk> chunks = new ArrayList<>(DARKENED);
        DARKENED.clear();
        for (Chunk chunk : chunks) {
            if (!chunk.isLoaded() || chunk.isLightPopulated() || !chunk.isTerrainPopulated()) { continue; }
            chunk.checkLight();
        }
    }
}
