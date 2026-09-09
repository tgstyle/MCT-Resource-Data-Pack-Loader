package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.util.FaceCache;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import javax.annotation.Nonnull;

public final class FaceCacheReset implements ResourceManagerReloadListener {
    public static final FaceCacheReset INSTANCE = new FaceCacheReset();

    private FaceCacheReset() {}

    @Override public void onResourceManagerReload(@Nonnull ResourceManager manager) { FaceCache.rebaked(); }
}
