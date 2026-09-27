package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.AbstractSpreadingStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentStructureRings extends ConcentricRingsStructurePlacement {
    private static final MapCodec<ContentStructureRings> FIELDS = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(ContentStructureRings::locateOffset),
            AbstractSpreadingStructurePlacement.FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", AbstractSpreadingStructurePlacement.FrequencyReductionMethod.DEFAULT).forGetter(ContentStructureRings::frequencyReductionMethod),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(ContentStructureRings::frequency),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(ContentStructureRings::salt),
            Codec.intRange(0, 1023).fieldOf("distance").forGetter(ContentStructureRings::distance),
            Codec.intRange(0, 1023).fieldOf("spread").forGetter(ContentStructureRings::spread),
            Codec.intRange(1, 4095).fieldOf("count").forGetter(ContentStructureRings::count),
            RegistryCodecs.holderSet(Registries.BIOME).fieldOf("preferred_biomes").forGetter(ContentStructureRings::filteredBiomes),
            Codec.INT.listOf().listOf().optionalFieldOf("pins", List.of()).forGetter(ContentStructureRings::pins),
            RegistryCodecs.holderSet(Registries.BIOME).optionalFieldOf("vanilla_preferred_biomes").forGetter(ContentStructureRings::vanillaBiomes),
            Codec.STRING.optionalFieldOf("filtered_for", "").forGetter(ContentStructureRings::filteredFor)).apply(instance,
            (offset, reduction, frequency, salt, distance, spread, count, preferred, pinned, vanilla, filter) -> new ContentStructureRings(offset, reduction, frequency, salt, distance, spread, count, preferred, pinned, vanilla.orElse(null), filter)));
    public static final MapCodec<ConcentricRingsStructurePlacement> CODEC = FIELDS.xmap(rings -> rings, placed -> (ContentStructureRings) placed);
    private final List<List<Integer>> pins;
    @Nullable private final HolderSet<Biome> vanillaBiomes;
    private final String filteredFor;
    private boolean warned;

    public ContentStructureRings(Vec3i locateOffset, AbstractSpreadingStructurePlacement.FrequencyReductionMethod reduction, float frequency, int salt,
            int distance, int spread, int count, HolderSet<Biome> preferredBiomes, List<List<Integer>> pins, @Nullable HolderSet<Biome> vanillaBiomes, String filteredFor) {
        super(locateOffset, reduction, frequency, salt, Optional.empty(), distance, spread, count, preferredBiomes);
        this.pins = pins;
        this.vanillaBiomes = vanillaBiomes;
        this.filteredFor = filteredFor;
        ContentStructurePins.hold(pins);
    }

    public List<List<Integer>> pins() { return pins; }

    private HolderSet<Biome> filteredBiomes() { return super.preferredBiomes(); }

    private Optional<HolderSet<Biome>> vanillaBiomes() { return Optional.ofNullable(vanillaBiomes); }

    private String filteredFor() { return filteredFor; }

    @Override @Nonnull public HolderSet<Biome> preferredBiomes() {
        HolderSet<Biome> filtered = super.preferredBiomes();
        if (filtered.size() > 0 || vanillaBiomes == null) { return filtered; }
        if (!warned) {
            warned = true;
            ContentLog.LOGGER.warn("Biome settings for {} leave no biome at all, so the vanilla list is left alone", filteredFor);
        }
        return vanillaBiomes;
    }

    @Override protected boolean isPlacementChunk(@Nonnull ChunkGeneratorStructureState state, int x, int z) { return ContentStructurePins.pinnedAt(pins, x, z) || super.isPlacementChunk(state, x, z); }

    @Override @Nonnull public MapCodec<ConcentricRingsStructurePlacement> codec() { return CODEC; }
}
