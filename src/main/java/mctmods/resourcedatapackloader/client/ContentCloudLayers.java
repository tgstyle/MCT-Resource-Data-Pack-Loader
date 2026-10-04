package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.mixin.rdpl.client.ICloudRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.util.ARGB;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentCloudLayers {
    private static final List<CloudRenderer> EXTRA = new ArrayList<>();
    @Nullable private static SkyLookDef.CloudLayer drawing;
    private static int active;

    private ContentCloudLayers() {}

    public interface Pass { void draw(CloudRenderer renderer, int color, float height); }

    public static float drift(float drift) {
        SkyLookDef.CloudLayer layer = drawing;
        if (layer != null) { return drift * layer.speed(); }
        SkyLookDef look = ContentFogSampler.look(Minecraft.getInstance().level);
        return look == null ? drift : drift * look.cloudSpeed();
    }

    public static boolean draw(CloudRenderer renderer, int color, float height, Pass pass) {
        if (drawing != null || EXTRA.contains(renderer)) { return false; }
        SkyLookDef look = ContentFogSampler.look(Minecraft.getInstance().level);
        List<SkyLookDef.CloudLayer> layers = look == null ? List.of() : look.cloudLayers();
        active = Math.max(0, layers.size() - 1);
        if (layers.isEmpty()) { return false; }
        try {
            for (int i = 0; i < layers.size(); i++) {
                SkyLookDef.CloudLayer layer = layers.get(i);
                drawing = layer;
                pass.draw(i == 0 ? renderer : extra(renderer, i - 1), layer.color() == SkyLookDef.UNSET ? color : ARGB.color(ARGB.alpha(color), layer.color()), layer.height() < 0.0F ? height : layer.height());
            }
        }
        finally { drawing = null; }
        return true;
    }

    public static List<CloudRenderer> extras(CloudRenderer renderer) { return drawing != null || EXTRA.contains(renderer) ? List.of() : EXTRA.subList(0, active); }

    private static CloudRenderer extra(CloudRenderer main, int index) {
        while (EXTRA.size() <= index) { EXTRA.add(new CloudRenderer()); }
        CloudRenderer renderer = EXTRA.get(index);
        CloudRenderer.TextureData texture = ((ICloudRenderer) main).rdpl$getTexture();
        if (((ICloudRenderer) renderer).rdpl$getTexture() != texture) {
            ((ICloudRenderer) renderer).rdpl$setTexture(texture);
            renderer.markForRebuild();
        }
        return renderer;
    }
}
