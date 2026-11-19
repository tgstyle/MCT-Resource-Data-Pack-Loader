package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class ScriptRegistrar {
    private final Identifier owner;
    private final String script;
    private final Identifier registryName;
    private final Registry registry;
    private final Class<?> type;

    ScriptRegistrar(Identifier owner, String script, Identifier registryName, Registry registry, Class<?> type) {
        this.owner = owner;
        this.script = script;
        this.registryName = registryName;
        this.registry = registry;
        this.type = type;
    }

    public void register(String name, Object object) {
        if (!type.isInstance(object)) {
            ContentLog.LOGGER.error("{}: {} is not a {}, not registering it", owner, object == null ? "null" : object.getClass().getName(), type.getName());
            return;
        }
        Identifier id = Identifier.tryParse(name);
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
        try {
            Registry.register(registry, id, object);
        } catch (RuntimeException e) {
            ContentLog.LOGGER.error("{}: {} could not be registered: {}", owner, id, e.getMessage());
            return;
        }
        ContentLog.LOGGER.info("Script {} registered {} in {}", script, id, registryName);
    }

    public Object key(String name) {
        return ResourceKey.create(registry.key(), Identifier.parse(name));
    }
}
