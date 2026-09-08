package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.interfaces.IContentChunkShape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import java.util.ArrayList;

public final class ContentShapeFeature extends Feature<ContentShapeFeature.Setup> {
    public static final ContentShapeFeature INSTANCE = new ContentShapeFeature();

    private ContentShapeFeature() { super(Setup.CODEC); }

    @Override public boolean place(FeaturePlaceContext<Setup> context) {
        ContentWorldgen.Entry entry = ContentWorldgen.entry(context.config().entry());
        if (entry == null) { return false; }
        ChunkPos center = new ChunkPos(context.origin());
        return run(entry, new ContentPlacer(context.level(), entry.palette(), center), context.random(), center, context.origin());
    }

    public static boolean run(ContentWorldgen.Entry entry, ContentPlacer placer, RandomSource random, ChunkPos center, BlockPos origin) {
        if (entry.shape() instanceof IContentChunkShape chunked) {
            chunked.generateChunk(placer, center, pos -> ContentWorldgen.allows(entry, placer.level(), pos));
            return true;
        }
        boolean placed = entry.shape().generate(placer, random, origin);
        if (placed && entry.def().follows()) { ContentWorldgen.after(entry, placer, random, origin, new ArrayList<>()); }
        return placed;
    }

    public record Setup(ResourceLocation entry) implements FeatureConfiguration {
        public static final Codec<Setup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("entry").forGetter(Setup::entry)).apply(instance, Setup::new));
    }
}
