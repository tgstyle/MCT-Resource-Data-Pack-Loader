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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ScriptRegistries {
    private static final Map<Identifier, Class<?>> TYPES = types();
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static boolean loaded;

    private ScriptRegistries() {}

    private static Map<Identifier, Class<?>> types() {
        Map<Identifier, Class<?>> types = new HashMap<>();
        for (Field field : BuiltInRegistries.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Registry.class.isAssignableFrom(field.getType()) || "REGISTRY".equals(field.getName())) { continue; }
            try {
                Type element = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                Class<?> raw = element instanceof ParameterizedType parameterized ? (Class<?>) parameterized.getRawType() : element instanceof Class<?> plain ? plain : null;
                if (raw != null) { types.put(((Registry<?>) field.get(null)).key().identifier(), raw); }
            } catch (ReflectiveOperationException | ClassCastException e) {
                ContentLog.LOGGER.debug("Script registries skip {}: {}", field.getName(), e.getMessage());
            }
        }
        return types;
    }

    private static final class Entry {
        private final Identifier key;
        private final String registry;
        private final ScriptRef script;
        private boolean fired;

        private Entry(Identifier key, String registry, ScriptRef script) {
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
    }

    private static void read(Identifier key, JsonObject json) {
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

    public static void onRegister(RegisterEvent event) {
        if (inactive()) { return; }
        load();
        Identifier name = event.getRegistryKey().identifier();
        for (Entry entry : ENTRIES) {
            if (!entry.registry.equals(name.toString())) { continue; }
            entry.fired = true;
            Class<?> type = TYPES.get(name);
            if (type == null) {
                ContentLog.LOGGER.error("{}: the registry '{}' is not a registry that scripts can add to, ignoring it", entry.key, entry.registry);
                continue;
            }
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("registry", new ScriptRegistrar(entry.key, ScriptEngine.sourceName(entry.script), name, event.getRegistry(), type));
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
