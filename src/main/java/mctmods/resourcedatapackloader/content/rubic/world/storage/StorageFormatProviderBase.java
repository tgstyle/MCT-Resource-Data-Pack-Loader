package mctmods.resourcedatapackloader.content.rubic.world.storage;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicStorage;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import java.io.IOException;
import java.nio.file.Path;

public abstract class StorageFormatProviderBase implements IForgeRegistryEntry<StorageFormatProviderBase> {
    public static final ResourceLocation DEFAULT = new ResourceLocation("resourcedatapackloader", "rubic3d");
    public static IForgeRegistry<StorageFormatProviderBase> REGISTRY;

    public static void init() {
        REGISTRY = new RegistryBuilder<StorageFormatProviderBase>()
                .setType(StorageFormatProviderBase.class)
                .setIDRange(0, 256)
                .setName(new ResourceLocation("resourcedatapackloader", "storage_format_provider_registry"))
                .addCallback(StorageFormatCallbacks.INSTANCE)
                .create();
    }

    public static ResourceLocation defaultStorageFormatProviderName(String fallback) {
        if (!fallback.isEmpty()) { return new ResourceLocation(fallback); }
        ResourceLocation[] providersThatCanBeDefault = REGISTRY.getValuesCollection().stream()
                .filter(StorageFormatProviderBase::canBeDefault)
                .map(StorageFormatProviderBase::getRegistryName)
                .toArray(ResourceLocation[]::new);
        return providersThatCanBeDefault.length == 1 ? providersThatCanBeDefault[0] : DEFAULT;
    }

    public ResourceLocation registryName;

    @Override public ResourceLocation getRegistryName() { return this.registryName; }

    @Override public StorageFormatProviderBase setRegistryName(ResourceLocation registryNameIn) {
        this.registryName = registryNameIn;
        return this;
    }

    @Override public Class<StorageFormatProviderBase> getRegistryType() { return StorageFormatProviderBase.class; }

    public abstract IRubicStorage provideStorage(World world, Path path) throws IOException;

    public boolean canBeDefault() { return false; }

    private static class StorageFormatCallbacks implements IForgeRegistry.MissingFactory<StorageFormatProviderBase> {
        private static final StorageFormatCallbacks INSTANCE = new StorageFormatCallbacks();

        @Override public StorageFormatProviderBase createMissing(ResourceLocation key, boolean isNetwork) {
            return isNetwork ? new DummyStorageFormat().setRegistryName(key) : null;
        }

        private static class DummyStorageFormat extends StorageFormatProviderBase {
            @Override public IRubicStorage provideStorage(World world, Path path) {
                throw new IllegalStateException("attempted to initialize storage for world " + world + " using dummy storage format " + this.getRegistryName());
            }
        }
    }
}
