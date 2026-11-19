package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class ScriptRegistrar {
    private static final Map<ResourceLocation, Class<?>> TYPES = types();

    private final ResourceLocation owner;
    private final String script;
    private final ResourceLocation registryName;
    private final Registry registry;
    private final Class<?> type;

    private ScriptRegistrar(ResourceLocation owner, String script, ResourceLocation registryName, Registry registry, Class<?> type) {
        this.owner = owner;
        this.script = script;
        this.registryName = registryName;
        this.registry = registry;
        this.type = type;
    }

    private static Map<ResourceLocation, Class<?>> types() {
        Map<ResourceLocation, Class<?>> types = new HashMap<>();
        for (Field field : BuiltInRegistries.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Registry.class.isAssignableFrom(field.getType()) || "REGISTRY".equals(field.getName())) { continue; }
            try {
                Type element = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                Class<?> raw = element instanceof ParameterizedType parameterized ? (Class<?>) parameterized.getRawType() : element instanceof Class<?> plain ? plain : null;
                if (raw != null) { types.put(((Registry<?>) field.get(null)).key().location(), raw); }
            } catch (ReflectiveOperationException | ClassCastException e) {
                ContentLog.LOGGER.debug("Script registries skip {}: {}", field.getName(), e.getMessage());
            }
        }
        return types;
    }

    static boolean supports(ResourceLocation registryName) { return TYPES.containsKey(registryName); }

    static ScriptRegistrar of(ResourceLocation owner, String script, ResourceLocation registryName, Registry registry) {
        return new ScriptRegistrar(owner, script, registryName, registry, TYPES.get(registryName));
    }

    public void register(String name, Object object) {
        if (!type.isInstance(object)) {
            ContentLog.LOGGER.error("{}: {} is not a {}, not registering it", owner, object == null ? "null" : object.getClass().getName(), type.getName());
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
        try {
            Registry.register(registry, id, object);
        } catch (RuntimeException e) {
            ContentLog.LOGGER.error("{}: {} could not be registered: {}", owner, id, e.getMessage());
            return;
        }
        ContentLog.LOGGER.info("Script {} registered {} in {}", script, id, registryName);
    }
}
