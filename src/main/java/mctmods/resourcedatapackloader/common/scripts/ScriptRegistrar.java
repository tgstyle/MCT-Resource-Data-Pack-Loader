package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class ScriptRegistrar {
    private final ResourceLocation owner;
    private final String script;
    private final ResourceLocation registryName;
    private final IForgeRegistry registry;

    ScriptRegistrar(ResourceLocation owner, String script, ResourceLocation registryName, IForgeRegistry registry) {
        this.owner = owner;
        this.script = script;
        this.registryName = registryName;
        this.registry = registry;
    }

    public void register(String name, Object object) {
        if (!registry.getRegistrySuperType().isInstance(object)) {
            ContentLog.LOGGER.error("{}: {} is not a {}, not registering it", owner, object == null ? "null" : object.getClass().getName(), registry.getRegistrySuperType().getName());
            return;
        }
        ResourceLocation id = new ResourceLocation(name);
        if (!id.getNamespace().equals(owner.getNamespace())) {
            ContentLog.LOGGER.error("{}: {} is not in the pack namespace {}, not registering it", owner, id, owner.getNamespace());
            return;
        }
        if (registry.containsKey(id)) {
            ContentLog.LOGGER.error("{}: {} is already registered in {}, skipping it", owner, id, registryName);
            return;
        }
        try {
            IForgeRegistryEntry entry = (IForgeRegistryEntry) object;
            entry.setRegistryName(id);
            registry.register(entry);
        } catch (RuntimeException e) {
            ContentLog.LOGGER.error("{}: {} could not be registered: {}", owner, id, e.getMessage());
            return;
        }
        ContentLog.LOGGER.info("Script {} registered {} in {}", script, id, registryName);
    }
}
