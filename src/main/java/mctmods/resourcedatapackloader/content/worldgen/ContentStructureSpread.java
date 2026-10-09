package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public final class ContentStructureSpread extends RandomSpreadStructurePlacement {
    static final MapCodec<List<List<Integer>>> PINS = Codec.INT.listOf().listOf().optionalFieldOf("pins", List.of());
    public static final Codec<ContentStructureSpread> CODEC = ExtraCodecs.validate(RecordCodecBuilder.mapCodec(instance -> {
        var placed = placementCodec(instance);
        var own = instance.group(
                Codec.intRange(0, 4096).fieldOf("spacing").forGetter(ContentStructureSpread::spacing),
                Codec.intRange(0, 4096).fieldOf("separation").forGetter(ContentStructureSpread::separation),
                RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(ContentStructureSpread::spreadType),
                PINS.forGetter(ContentStructureSpread::pins),
                Codec.INT.optionalFieldOf("min_distance_from_spawn", 0).forGetter(ContentStructureSpread::minDistanceFromSpawn),
                Codec.INT.listOf().optionalFieldOf("spawn", List.of(0, 0)).forGetter(ContentStructureSpread::spawn));
        return new Products.P11<>(placed.t1(), placed.t2(), placed.t3(), placed.t4(), placed.t5(), own.t1(), own.t2(), own.t3(), own.t4(), own.t5(), own.t6()).apply(instance,
                (offset, reduction, frequency, salt, exclusion, spacing, separation, type, pins, nearest, spawn) -> new ContentStructureSpread(offset, reduction, frequency, salt, spacing, separation, type, pins, nearest, spawn));
    }), ContentStructureSpread::validate).codec();
    public static final StructurePlacementType<ContentStructureSpread> TYPE = () -> CODEC;
    private final List<List<Integer>> pins;
    private final int minDistanceFromSpawn;
    private final List<Integer> spawn;

    public ContentStructureSpread(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod reduction, float frequency, int salt,
            int spacing, int separation, RandomSpreadType spreadType, List<List<Integer>> pins, int minDistanceFromSpawn, List<Integer> spawn) {
        super(locateOffset, reduction, frequency, salt, Optional.empty(), spacing, separation, spreadType);
        this.pins = pins;
        ContentStructurePins.hold(pins);
        this.minDistanceFromSpawn = minDistanceFromSpawn;
        this.spawn = spawn;
    }

    public List<List<Integer>> pins() { return pins; }

    public int minDistanceFromSpawn() { return minDistanceFromSpawn; }

    public List<Integer> spawn() { return spawn; }

    @Override @Nonnull public ChunkPos getPotentialStructureChunk(long seed, int regionX, int regionZ) {
        ChunkPos pinned = pins.isEmpty() ? null : ContentStructurePins.pinnedRegion(pins, spacing(), regionX, regionZ);
        return pinned != null ? pinned : super.getPotentialStructureChunk(seed, regionX, regionZ);
    }

    @Override protected boolean isPlacementChunk(@Nonnull ChunkGeneratorStructureState state, int x, int z) {
        if (!pins.isEmpty()) { return ContentStructurePins.pinnedAt(pins, x, z); }
        return super.isPlacementChunk(state, x, z) && ContentStructurePins.farFromSpawn(minDistanceFromSpawn, spawn, x, z);
    }

    @Override public boolean isStructureChunk(@Nonnull ChunkGeneratorStructureState state, int x, int z) { return pins.isEmpty() ? super.isStructureChunk(state, x, z) : ContentStructurePins.pinnedAt(pins, x, z); }

    @Override @Nonnull public StructurePlacementType<?> type() { return TYPE; }

    private static DataResult<ContentStructureSpread> validate(ContentStructureSpread placement) { return placement.spacing() <= placement.separation() ? DataResult.error(() -> "Spacing has to be larger than separation") : DataResult.success(placement); }
}
