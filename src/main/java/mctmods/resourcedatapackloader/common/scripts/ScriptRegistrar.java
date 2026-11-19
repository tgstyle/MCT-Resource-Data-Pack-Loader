package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class ScriptRegistrar {
    private final ResourceLocation owner;
    private final String script;
    private final ResourceLocation registryName;
    private final IForgeRegistry registry;
    private final Class<?> superType;

    ScriptRegistrar(ResourceLocation owner, String script, RegisterEvent event, Class<?> superType) {
        this.owner = owner;
        this.script = script;
        this.registryName = event.getRegistryKey().location();
        this.registry = event.getForgeRegistry();
        this.superType = superType;
    }

    public void register(String name, Object object) {
        if (!superType.isInstance(object)) {
            ContentLog.LOGGER.error("{}: {} is not a {}, not registering it", owner, object == null ? "null" : object.getClass().getName(), superType.getName());
            return;
        }
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id == null) {
            ContentLog.LOGGER.error("{}: {} could not be registered: not a valid name", owner, name);
            return;
        }
        if (!id.getNamespace().equals(owner.getNamespace())) {
            ContentLog.LOGGER.error("{}: {} is not in the pack namespace {}, not registering it", owner, id, owner.getNamespace());
            return;
        }
        if (registry.containsKey(id)) {
            ContentLog.LOGGER.error("{}: {} is already registered in {}, skipping it", owner, id, registryName);
            return;
        }
        try { registry.register(id, object); }
        catch (RuntimeException e) {
            ContentLog.LOGGER.error("{}: {} could not be registered: {}", owner, id, e.getMessage());
            return;
        }
        ContentLog.LOGGER.info("Script {} registered {} in {}", script, id, registryName);
    }
}
