package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentStructureRings extends ConcentricRingsStructurePlacement {
    public static final MapCodec<ContentStructureRings> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        var placed = placementCodec(instance);
        var own = instance.group(
                Codec.intRange(0, 1023).fieldOf("distance").forGetter(ContentStructureRings::distance),
                Codec.intRange(0, 1023).fieldOf("spread").forGetter(ContentStructureRings::spread),
                Codec.intRange(1, 4095).fieldOf("count").forGetter(ContentStructureRings::count),
                RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("preferred_biomes").forGetter(ContentStructureRings::filteredBiomes),
                ContentStructureSpread.PINS.forGetter(ContentStructureRings::pins),
                RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("vanilla_preferred_biomes").forGetter(ContentStructureRings::vanillaBiomes),
                Codec.STRING.optionalFieldOf("filtered_for", "").forGetter(ContentStructureRings::filteredFor));
        return new Products.P12<>(placed.t1(), placed.t2(), placed.t3(), placed.t4(), placed.t5(), own.t1(), own.t2(), own.t3(), own.t4(), own.t5(), own.t6(), own.t7()).apply(instance,
                (offset, reduction, frequency, salt, _, distance, spread, count, preferred, pinned, vanilla, filter) -> new ContentStructureRings(offset, reduction, frequency, salt, distance, spread, count, preferred, pinned, vanilla.orElse(null), filter));
    });
    public static final StructurePlacementType<ContentStructureRings> TYPE = () -> CODEC;
    private final List<List<Integer>> pins;
    @Nullable private final HolderSet<Biome> vanillaBiomes;
    private final String filteredFor;
    private boolean warned;

    public ContentStructureRings(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod reduction, float frequency, int salt,
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
            ContentStructureControl.warnNoBiome(filteredFor);
        }
        return vanillaBiomes;
    }

    @Override protected boolean isPlacementChunk(@Nonnull ChunkGeneratorStructureState state, int x, int z) { return ContentStructurePins.pinnedAt(pins, x, z) || super.isPlacementChunk(state, x, z); }

    @Override @Nonnull public StructurePlacementType<?> type() { return TYPE; }
}
