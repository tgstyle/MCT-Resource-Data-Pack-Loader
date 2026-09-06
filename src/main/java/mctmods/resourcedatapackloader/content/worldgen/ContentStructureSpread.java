package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
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

public final class ContentStructureSpread extends RandomSpreadStructurePlacement {
    public static final Codec<ContentStructureSpread> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(ContentStructureSpread::locateOffset),
            StructurePlacement.FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", StructurePlacement.FrequencyReductionMethod.DEFAULT).forGetter(ContentStructureSpread::frequencyReductionMethod),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(ContentStructureSpread::frequency),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(ContentStructureSpread::salt),
            Codec.intRange(0, 4096).fieldOf("spacing").forGetter(ContentStructureSpread::spacing),
            Codec.intRange(0, 4096).fieldOf("separation").forGetter(ContentStructureSpread::separation),
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(ContentStructureSpread::spreadType),
            Codec.INT.listOf().listOf().optionalFieldOf("pins", List.of()).forGetter(ContentStructureSpread::pins),
            Codec.INT.optionalFieldOf("min_distance_from_spawn", 0).forGetter(ContentStructureSpread::minDistanceFromSpawn),
            Codec.INT.listOf().optionalFieldOf("spawn", List.of(0, 0)).forGetter(ContentStructureSpread::spawn)).apply(instance, ContentStructureSpread::new));
    public static final StructurePlacementType<ContentStructureSpread> TYPE = () -> CODEC;
    private final List<List<Integer>> pins;
    private final int minDistanceFromSpawn;
    private final List<Integer> spawn;

    public ContentStructureSpread(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod reduction, float frequency, int salt,
            int spacing, int separation, RandomSpreadType spreadType, List<List<Integer>> pins, int minDistanceFromSpawn, List<Integer> spawn) {
        super(locateOffset, reduction, frequency, salt, Optional.empty(), spacing, separation, spreadType);
        this.pins = pins;
        this.minDistanceFromSpawn = minDistanceFromSpawn;
        this.spawn = spawn;
    }

    public List<List<Integer>> pins() { return pins; }

    public int minDistanceFromSpawn() { return minDistanceFromSpawn; }

    public List<Integer> spawn() { return spawn; }

    @Override public ChunkPos getPotentialStructureChunk(long seed, int regionX, int regionZ) {
        if (pins.isEmpty()) { return super.getPotentialStructureChunk(seed, regionX, regionZ); }
        for (List<Integer> pin : pins) {
            if (pin.size() != 2) { continue; }
            ChunkPos chunk = chunkOf(pin);
            if (Math.floorDiv(chunk.x, spacing()) == Math.floorDiv(regionX, spacing()) && Math.floorDiv(chunk.z, spacing()) == Math.floorDiv(regionZ, spacing())) { return chunk; }
        }
        return super.getPotentialStructureChunk(seed, regionX, regionZ);
    }

    @Override protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int x, int z) {
        if (!pins.isEmpty()) {
            for (List<Integer> pin : pins) {
                if (pin.size() == 2 && pin.get(0) >> 4 == x && pin.get(1) >> 4 == z) { return true; }
            }
            return false;
        }
        if (!super.isPlacementChunk(state, x, z)) { return false; }
        if (minDistanceFromSpawn <= 0) { return true; }
        double offX = (x * 16 + 8) - (spawn.size() == 2 ? spawn.get(0) : 0);
        double offZ = (z * 16 + 8) - (spawn.size() == 2 ? spawn.get(1) : 0);
        return offX * offX + offZ * offZ >= (double) minDistanceFromSpawn * minDistanceFromSpawn;
    }

    @Override public StructurePlacementType<?> type() { return TYPE; }

    public static ChunkPos chunkOf(List<Integer> pin) { return new ChunkPos(pin.get(0) >> 4, pin.get(1) >> 4); }
}
