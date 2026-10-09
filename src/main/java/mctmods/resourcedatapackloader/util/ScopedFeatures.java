package mctmods.resourcedatapackloader.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

public final class ScopedFeatures<T> {
    private volatile Map<ChunkGenerator, Map<PlacedFeature, T>> scoped = Map.of();

    public int load(ServerLevel level, boolean active, BiFunction<Identifier, PlacedFeature, T> judge) {
        Map<ChunkGenerator, Map<PlacedFeature, T>> next = level.dimension() == Level.OVERWORLD ? new IdentityHashMap<>() : new IdentityHashMap<>(scoped);
        int count = 0;
        if (active) {
            Map<PlacedFeature, T> features = new IdentityHashMap<>();
            for (Map.Entry<ResourceKey<PlacedFeature>, PlacedFeature> entry : level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).entrySet()) {
                T verdict = judge.apply(entry.getKey().identifier(), entry.getValue());
                if (verdict != null) { features.put(entry.getValue(), verdict); }
            }
            next.put(level.getChunkSource().getGenerator(), features);
            count = features.size();
        }
        scoped = next;
        return count;
    }

    @Nullable public T get(ChunkGenerator generator, PlacedFeature feature) {
        Map<PlacedFeature, T> held = scoped.get(generator);
        return held == null ? null : held.get(feature);
    }
}
