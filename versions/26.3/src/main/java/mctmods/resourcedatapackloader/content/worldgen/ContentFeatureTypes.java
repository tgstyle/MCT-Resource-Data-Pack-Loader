package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.AbstractOreFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public final class ContentFeatureTypes {
    private ContentFeatureTypes() {}

    public static void register(RegisterEvent event) {
        event.register(Registries.FEATURE_TYPE, helper -> {
            helper.register(id(ContentWorldgen.SHAPE_FEATURE), Shape.CODEC);
            helper.register(id(ContentCaveRegions.COVER_FEATURE), Region.COVER);
            helper.register(id(ContentCaveRegions.STRUCTURE_FEATURE), Region.CAVE_STRUCTURE);
        });
        event.register(Registries.STRUCTURE_PLACEMENT, helper -> {
            helper.register(id(ContentWorldgen.SPREAD_PLACEMENT), ContentStructureSpread.CODEC);
            helper.register(id(ContentWorldgen.RINGS_PLACEMENT), ContentStructureRings.CODEC);
        });
        event.register(Registries.PLACEMENT_MODIFIER_TYPE, helper -> helper.register(id(ContentWorldgen.SPREAD_PLACEMENT), Spread.CODEC));
    }

    private static Identifier id(String path) { return ResourceDataPackLoader.id(path); }

    public static boolean foreign(PlacedFeature placed) {
        Feature kind = placed.feature().value();
        return !(kind instanceof Shape || kind instanceof Region);
    }

    public static boolean nonOre(PlacedFeature placed) { return !(placed.feature().value() instanceof AbstractOreFeature); }

    private record Shape(Identifier entry) implements Feature {
        private static final MapCodec<Shape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Identifier.CODEC.fieldOf("entry").forGetter(Shape::entry)).apply(instance, Shape::new));

        @Override @Nonnull public MapCodec<Shape> codec() { return CODEC; }

        @Override public boolean place(@Nonnull WorldGenLevel level, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BlockPos origin) { return ContentShapeFeature.place(level, random, origin, entry); }
    }

    private enum Kind { COVER, CAVE_STRUCTURE }

    private record Region(Identifier region, Kind kind) implements Feature {
        private static final MapCodec<Region> COVER = codecOf(Kind.COVER);
        private static final MapCodec<Region> CAVE_STRUCTURE = codecOf(Kind.CAVE_STRUCTURE);

        private static MapCodec<Region> codecOf(Kind kind) { return RecordCodecBuilder.mapCodec(instance -> instance.group(Identifier.CODEC.fieldOf("region").forGetter(Region::region)).apply(instance, region -> new Region(region, kind))); }

        @Override @Nonnull public MapCodec<Region> codec() { return kind == Kind.COVER ? COVER : CAVE_STRUCTURE; }

        @Override public boolean place(@Nonnull WorldGenLevel level, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BlockPos origin) { return kind == Kind.COVER ? ContentCoverFeature.place(level, origin, region) : ContentCaveStructureFeature.place(level, origin, region); }
    }

    private record Spread(Identifier entry) implements PlacementModifier {
        private static final MapCodec<Spread> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Identifier.CODEC.fieldOf("entry").forGetter(Spread::entry)).apply(instance, Spread::new));

        @Override @Nonnull public MapCodec<Spread> codec() { return CODEC; }

        @Override public void modify(@Nonnull PlacementContext context, @Nonnull RandomSource random, @Nonnull BlockPos origin, @Nonnull Consumer<BlockPos> output) { ContentSpreadPlacement.placed(entry, context, random, origin).forEach(output); }
    }
}
