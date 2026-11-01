package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.client.renderer.sky.SkyOccluder;
import mctmods.resourcedatapackloader.common.util.world.FaceCache;

import javax.annotation.Nonnull;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public final class FaceCacheReset implements ResourceManagerReloadListener {
    public static final FaceCacheReset INSTANCE = new FaceCacheReset();

    private FaceCacheReset() {}

    @Override public void onResourceManagerReload(@Nonnull ResourceManager manager) {
        FaceCache.rebaked();
        SkyOccluder.invalidate();
    }
}
