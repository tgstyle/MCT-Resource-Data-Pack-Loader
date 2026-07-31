package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ContentOwners {
    private static final Map<String, ModContainer> CONTAINERS = new HashMap<>();
    private static final Set<String> RESERVED = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ResourceDataPackLoader.MOD_ID, MCTMixin.MIXIN_ID)));
    private static final Set<String> WARNED = new HashSet<>();

    private ContentOwners() {}

    public static boolean reserved(ResourceLocation key) {
        if (!RESERVED.contains(key.getNamespace())) { return false; }

        if (WARNED.add(key.getNamespace())) {
            ContentLog.LOGGER.error("A pack is trying to define content under '{}', which belongs to this mod. Content there is ignored, because it would claim ownership of things this mod registers and confuse the whitelists that read it. Use your own namespace, such as the pack name. Overriding this mod's own assets is still fine, only registering content is not", key.getNamespace());
        }
        return true;
    }

    public static ModContainer of(String namespace) {
        ModContainer existing = Loader.instance().getIndexedModList().get(namespace);
        if (existing != null) { return existing; }

        return CONTAINERS.computeIfAbsent(namespace, id -> {
            ModMetadata metadata = new ModMetadata();
            metadata.modId = id;
            metadata.name = id;
            metadata.version = "0.0";
            return new DummyModContainer(metadata);
        });
    }
}
