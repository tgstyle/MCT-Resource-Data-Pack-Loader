package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ScriptRegistries {
    private static final List<Entry> ENTRIES = new ArrayList<>();
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
    }

    private static void read(ResourceLocation key, JsonObject json) {
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
        ResourceLocation id = event.getRegistryKey().location();
        String name = id.toString();
        for (Entry entry : ENTRIES) {
            if (!entry.registry.equals(name)) { continue; }
            entry.fired = true;
            if (!ScriptRegistrar.supports(id)) {
                ContentLog.LOGGER.error("{}: the registry '{}' is not a registry that scripts can add to, ignoring it", entry.key, entry.registry);
                continue;
            }
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("registry", ScriptRegistrar.of(entry.key, ScriptEngine.sourceName(entry.script), id, event.getRegistry()));
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
