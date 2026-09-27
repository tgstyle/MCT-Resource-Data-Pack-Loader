package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public final class ContentFeatureTypes {
    private static final Shape SHAPE = new Shape();
    private static final Cover COVER = new Cover();
    private static final CaveStructure CAVE_STRUCTURE = new CaveStructure();

    private ContentFeatureTypes() {}

    public static void register(RegisterEvent event) {
        event.register(Registries.FEATURE, helper -> {
            helper.register(id(ContentWorldgen.SHAPE_FEATURE), SHAPE);
            helper.register(id(ContentCaveRegions.COVER_FEATURE), COVER);
            helper.register(id(ContentCaveRegions.STRUCTURE_FEATURE), CAVE_STRUCTURE);
        });
        event.register(Registries.STRUCTURE_PLACEMENT, helper -> {
            helper.register(id(ContentWorldgen.SPREAD_PLACEMENT), ContentStructureSpread.TYPE);
            helper.register(id(ContentWorldgen.RINGS_PLACEMENT), ContentStructureRings.TYPE);
        });
        event.register(Registries.PLACEMENT_MODIFIER_TYPE, helper -> helper.register(id(ContentWorldgen.SPREAD_PLACEMENT), Spread.TYPE));
    }

    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, path); }

    public static boolean foreign(PlacedFeature placed) {
        Feature<?> kind = placed.feature().value().feature();
        return !(kind instanceof Shape || kind instanceof Cover || kind instanceof CaveStructure);
    }

    public static boolean nonOre(PlacedFeature placed) {
        Feature<?> kind = placed.feature().value().feature();
        return !(kind instanceof OreFeature || kind instanceof ScatteredOreFeature);
    }

    private static Codec<Named> named(String field) { return RecordCodecBuilder.create(instance -> instance.group(Identifier.CODEC.fieldOf(field).forGetter(Named::id)).apply(instance, Named::new)); }

    private record Named(Identifier id) implements FeatureConfiguration {}

    private static final class Shape extends Feature<Named> {
        private Shape() { super(named("entry")); }

        @Override public boolean place(@Nonnull FeaturePlaceContext<Named> context) { return ContentShapeFeature.place(context.level(), context.random(), context.origin(), context.config().id()); }
    }

    private static final class Cover extends Feature<Named> {
        private Cover() { super(named("region")); }

        @Override public boolean place(@Nonnull FeaturePlaceContext<Named> context) { return ContentCoverFeature.place(context.level(), context.origin(), context.config().id()); }
    }

    private static final class CaveStructure extends Feature<Named> {
        private CaveStructure() { super(named("region")); }

        @Override public boolean place(@Nonnull FeaturePlaceContext<Named> context) { return ContentCaveStructureFeature.place(context.level(), context.origin(), context.config().id()); }
    }

    private static final class Spread extends PlacementModifier {
        private static final MapCodec<Spread> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Identifier.CODEC.fieldOf("entry").forGetter(Spread::entry)).apply(instance, Spread::new));
        private static final PlacementModifierType<Spread> TYPE = () -> CODEC;
        private final Identifier entry;

        private Spread(Identifier entry) { this.entry = entry; }

        private Identifier entry() { return entry; }

        @Override @Nonnull public PlacementModifierType<?> type() { return TYPE; }

        @Override @Nonnull public Stream<BlockPos> getPositions(@Nonnull PlacementContext context, @Nonnull RandomSource random, @Nonnull BlockPos origin) { return ContentSpreadPlacement.placed(entry, context, random, origin); }
    }
}
