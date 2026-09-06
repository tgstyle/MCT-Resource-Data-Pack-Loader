package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public final class ContentCoverFeature extends Feature<ContentCoverFeature.Setup> {
    public static final ContentCoverFeature INSTANCE = new ContentCoverFeature();

    private ContentCoverFeature() { super(Setup.CODEC); }

    @Override public boolean place(FeaturePlaceContext<Setup> context) {
        ContentCover cover = ContentCaveRegions.cover(context.config().region());
        if (cover == null) { return false; }
        ChunkPos center = new ChunkPos(context.origin());
        cover.generateChunk(new ContentPlacer(context.level(), ContentCaveRegions.palette(context.config().region()), center, ContentPlacer.CHUNK_ONLY), center, pos -> true);
        return true;
    }

    public record Setup(ResourceLocation region) implements FeatureConfiguration {
        public static final Codec<Setup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("region").forGetter(Setup::region)).apply(instance, Setup::new));
    }
}
