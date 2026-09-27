package mctmods.resourcedatapackloader.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3fc;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentBannerItemRenderer implements SpecialModelRenderer<Void> {
    private final ContentBannerRenderer renderer;
    private final Block block;

    private ContentBannerItemRenderer(ContentBannerRenderer renderer, Block block) {
        this.renderer = renderer;
        this.block = block;
    }

    @Override public void submit(@Nullable Void argument, @Nonnull PoseStack pose, @Nonnull SubmitNodeCollector collector, int light, int overlay, boolean foil, int outline) { renderer.submitItem(block, pose, collector, light, overlay, outline); }

    @Override public void getExtents(@Nonnull Consumer<Vector3fc> output) { renderer.getExtents(output); }

    @Override @Nullable public Void extractArgument(@Nonnull ItemStack stack) { return null; }

    public record Unbaked(Block block) implements SpecialModelRenderer.Unbaked<Void> {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(Unbaked::block)).apply(instance, Unbaked::new));

        @Override @Nonnull public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override @Nonnull public ContentBannerItemRenderer bake(@Nonnull SpecialModelRenderer.BakingContext context) { return new ContentBannerItemRenderer(new ContentBannerRenderer(context.entityModelSet()), block); }
    }
}
