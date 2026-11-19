package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentOwners;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    private static boolean inactive() { return !Config.registersToClients(); }

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

    @SuppressWarnings("rawtypes")
    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onRegister(RegistryEvent.Register event) {
        if (inactive()) { return; }
        load();
        String name = event.getName().toString();
        for (Entry entry : ENTRIES) {
            if (!entry.registry.equals(name)) { continue; }
            entry.fired = true;
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("registry", new ScriptRegistrar(entry.key, ScriptEngine.sourceName(entry.script), event.getName(), event.getRegistry()));
            ContentOwners.as(entry.key.getNamespace(), () -> ScriptEngine.run(entry.script, bindings, true, true));
        }
    }

    public static void report() {
        if (inactive()) { return; }
        for (Entry entry : ENTRIES) {
            if (!entry.fired) { ContentLog.LOGGER.error("{}: no mod provides the registry '{}', ignoring it", entry.key, entry.registry); }
        }
    }
}
