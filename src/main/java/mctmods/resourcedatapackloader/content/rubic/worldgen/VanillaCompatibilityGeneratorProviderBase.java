package mctmods.resourcedatapackloader.content.rubic.worldgen;

import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.ICubeGenerator;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

public abstract class VanillaCompatibilityGeneratorProviderBase implements IForgeRegistryEntry<VanillaCompatibilityGeneratorProviderBase> {
    public static final ResourceLocation DEFAULT = new ResourceLocation("resourcedatapackloader", "default");
    public static IForgeRegistry<VanillaCompatibilityGeneratorProviderBase> REGISTRY;

    public static void init() {
        REGISTRY = new RegistryBuilder<VanillaCompatibilityGeneratorProviderBase>()
                .setType(VanillaCompatibilityGeneratorProviderBase.class)
                .setIDRange(0, 256)
                .setName(new ResourceLocation("resourcedatapackloader", "vanilla_compatibility_generators_registry"))
                .setDefaultKey(DEFAULT)
                .addCallback(VanillaCompatibilityGeneratorCallbacks.INSTANCE)
                .create();
    }

    public ResourceLocation registryName;

    @Override public VanillaCompatibilityGeneratorProviderBase setRegistryName(ResourceLocation registryNameIn) {
        registryName = registryNameIn;
        return this;
    }

    @Override public ResourceLocation getRegistryName() { return registryName; }

    @Override public Class<VanillaCompatibilityGeneratorProviderBase> getRegistryType() { return VanillaCompatibilityGeneratorProviderBase.class; }

    public abstract ICubeGenerator provideGenerator(IChunkGenerator vanillaChunkGenerator, World world);

    private static class VanillaCompatibilityGeneratorCallbacks implements IForgeRegistry.MissingFactory<VanillaCompatibilityGeneratorProviderBase> {
        private static final VanillaCompatibilityGeneratorCallbacks INSTANCE = new VanillaCompatibilityGeneratorCallbacks();

        @Override public VanillaCompatibilityGeneratorProviderBase createMissing(ResourceLocation key, boolean isNetwork) {
            return isNetwork ? new DummyVanillaCompatibilityGenerator().setRegistryName(key) : null;
        }

        private static class DummyVanillaCompatibilityGenerator extends VanillaCompatibilityGeneratorProviderBase {
            @Override public ICubeGenerator provideGenerator(IChunkGenerator vanillaChunkGenerator, World world) {
                throw new IllegalStateException("attempted to initialize generator for world " + world + " using dummy vanilla compatibility generator " + this.getRegistryName());
            }
        }
    }
}
