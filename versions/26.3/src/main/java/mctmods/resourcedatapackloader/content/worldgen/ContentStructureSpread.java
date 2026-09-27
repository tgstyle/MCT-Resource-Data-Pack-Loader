package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.AbstractSpreadingStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public final class ContentStructureSpread extends RandomSpreadStructurePlacement {
    private static final MapCodec<ContentStructureSpread> FIELDS = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(ContentStructureSpread::locateOffset),
            AbstractSpreadingStructurePlacement.FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", AbstractSpreadingStructurePlacement.FrequencyReductionMethod.DEFAULT).forGetter(ContentStructureSpread::frequencyReductionMethod),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(ContentStructureSpread::frequency),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(ContentStructureSpread::salt),
            Codec.intRange(0, 4096).fieldOf("spacing").forGetter(ContentStructureSpread::spacing),
            Codec.intRange(0, 4096).fieldOf("separation").forGetter(ContentStructureSpread::separation),
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(ContentStructureSpread::spreadType),
            Codec.INT.listOf().listOf().optionalFieldOf("pins", List.of()).forGetter(ContentStructureSpread::pins),
            Codec.INT.optionalFieldOf("min_distance_from_spawn", 0).forGetter(ContentStructureSpread::minDistanceFromSpawn),
            Codec.INT.listOf().optionalFieldOf("spawn", List.of(0, 0)).forGetter(ContentStructureSpread::spawn)).apply(instance, ContentStructureSpread::new));
    public static final MapCodec<RandomSpreadStructurePlacement> CODEC = FIELDS.xmap(spread -> spread, placed -> (ContentStructureSpread) placed);
    private final List<List<Integer>> pins;
    private final int minDistanceFromSpawn;
    private final List<Integer> spawn;

    public ContentStructureSpread(Vec3i locateOffset, AbstractSpreadingStructurePlacement.FrequencyReductionMethod reduction, float frequency, int salt,
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

    @Override @Nonnull public MapCodec<RandomSpreadStructurePlacement> codec() { return CODEC; }
}
