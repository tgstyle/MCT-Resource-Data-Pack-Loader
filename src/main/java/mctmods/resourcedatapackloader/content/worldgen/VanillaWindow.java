package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import javax.annotation.Nullable;

public final class VanillaWindow extends WorldGenerationContext {
    @Nullable private static volatile ChunkGenerator deep;
    private boolean floorRead;

    private VanillaWindow(ChunkGenerator generator, int top) { super(generator, LevelHeightAccessor.create(ContentWorldShape.VANILLA_MIN, top - ContentWorldShape.VANILLA_MIN)); }

    static void keep(@Nullable ChunkGenerator generator) { deep = generator; }

    @Nullable public static VanillaWindow of(PlacementContext context) {
        ChunkGenerator generator = context.generator();
        if (generator != deep) { return null; }
        return new VanillaWindow(generator, context.getMinGenY() + context.getGenDepth());
    }

    @Override public int getMinGenY() {
        floorRead = true;
        return super.getMinGenY();
    }

    public boolean under(int y) { return floorRead && y < super.getMinGenY(); }
}
