package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

public final class ScriptRegistries {
    private static final List<String> REGISTRY_KEYS = List.of("registry", "script", "scriptFile");
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<ResourceLocation, Class<?>> TYPES = new HashMap<>();
    private static boolean loaded;

    private ScriptRegistries() {}

    private static final class Entry {
        private final ResourceLocation key;
        private final String registry;
        private final ScriptRef script;
        private boolean fired;

        private Entry(ResourceLocation key, String registry, ScriptRef script) {
            this.key = key;
            this.registry = registry;
            this.script = script;
        }
    }

    private static boolean inactive() { return Config.contentOff(); }

    private static void load() {
        if (loaded) { return; }
        loaded = true;
        Json.eachObject(PackManager.SCRIPTS + "/registries", "script registry file", ScriptRegistries::read);
        ScriptEngine.prepare();
        indexTypes();
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", REGISTRY_KEYS);
        String registry = Json.text(json, "registry", "").trim();
        ScriptRef script = ScriptRef.read(key, json);
        if (registry.isEmpty()) {
            ContentLog.LOGGER.error("{}: needs a registry, ignoring it", key);
            return;
        }
        if (script == null) {
            ContentLog.LOGGER.error("{}: needs script or scriptFile", key);
            return;
        }
        ENTRIES.add(new Entry(key, registry, script));
    }

    private static void indexTypes() {
        for (Field field : ForgeRegistries.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !IForgeRegistry.class.isAssignableFrom(field.getType())) { continue; }
            try {
                Type element = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                Class<?> raw = element instanceof ParameterizedType parameterized ? (Class<?>) parameterized.getRawType() : element instanceof Class<?> plain ? plain : null;
                if (raw != null) { TYPES.put(((IForgeRegistry<?>) field.get(null)).getRegistryName(), raw); }
            }
            catch (ReflectiveOperationException | ClassCastException e) { ContentLog.LOGGER.debug("Script registries skip {}: {}", field.getName(), e.getMessage()); }
        }
    }

    @Nullable private static Class<?> typeOf(ResourceLocation name) { return TYPES.get(name); }

    public static void onRegister(RegisterEvent event) {
        if (inactive()) { return; }
        load();
        ResourceLocation name = event.getRegistryKey().location();
        for (Entry entry : ENTRIES) {
            if (!entry.registry.equals(name.toString())) { continue; }
            entry.fired = true;
            Class<?> type = typeOf(name);
            if (event.getForgeRegistry() == null || type == null) {
                ContentLog.LOGGER.error("{}: the registry '{}' is not a registry that scripts can add to, ignoring it", entry.key, entry.registry);
                continue;
            }
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("registry", new ScriptRegistrar(entry.key, ScriptEngine.sourceName(entry.script), event, type));
            ScriptEngine.run(entry.script, bindings, true, true);
        }
    }

    public static void report() {
        if (inactive()) { return; }
        for (Entry entry : ENTRIES) {
            if (!entry.fired) { ContentLog.LOGGER.error("{}: no mod provides the registry '{}', ignoring it", entry.key, entry.registry); }
        }
    }
}
