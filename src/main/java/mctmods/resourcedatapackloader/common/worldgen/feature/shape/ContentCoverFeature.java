package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveRegionDef;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

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
        CaveRegionDef region = ContentCaveRegions.def(context.config().region());
        if (cover == null || region == null) { return false; }
        ChunkPos center = new ChunkPos(context.origin());
        cover.generateChunk(new ContentPlacer(context.level(), ContentCaveRegions.palette(context.config().region()), center, ContentPlacer.CHUNK_ONLY), center, pos -> ContentCaveRegions.holds(context.level(), region, pos));
        return true;
    }

    public record Setup(ResourceLocation region) implements FeatureConfiguration {
        public static final Codec<Setup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("region").forGetter(Setup::region)).apply(instance, Setup::new));
    }
}
