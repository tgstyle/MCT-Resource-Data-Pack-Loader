package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptEventDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.scripts.engine.ScriptEngine;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ScriptEvents {
    private static final Map<String, List<ScriptEventDef>> EVENTS = new HashMap<>();

    private ScriptEvents() {}

    public static void load() {
        EVENTS.clear();
        ScriptModEvents.clear();
        Json.eachObject(PackManager.SCRIPTS + "/events", "script event file", ScriptEvents::read);
        ScriptEngine.reload();
        int count = 0;
        for (List<ScriptEventDef> entries : EVENTS.values()) { count += entries.size(); }
        if (count > 0) { Summary.info("scripts", "Loaded " + count + " script event(s)"); }
    }

    public static boolean has(String kind) { return EVENTS.containsKey(kind); }

    public static boolean fire(String kind, Level level, @Nullable BlockState state, @Nullable Entity entity, ItemStack item, Map<String, Object> bindings) {
        return fire(kind, level, state, entity, item, null, bindings);
    }

    public static boolean idle(String kind, long time) {
        List<ScriptEventDef> entries = EVENTS.get(kind);
        if (entries == null) { return true; }
        for (ScriptEventDef entry : entries) {
            if (entry.every == 1 || time % entry.every == 0) { return false; }
        }
        return true;
    }

    public static boolean fire(String kind, Level level, @Nullable BlockState state, @Nullable Entity entity, ItemStack item, @Nullable ItemStack alternate, Map<String, Object> bindings) {
        List<ScriptEventDef> entries = EVENTS.get(kind);
        if (entries == null) { return false; }
        boolean cancelled = false;
        boolean disable = ScriptEventDef.disablesOnError(kind);
        for (ScriptEventDef entry : entries) {
            if (entry.every != 1 && level.getGameTime() % entry.every != 0) { continue; }
            if (!entry.filter.matches(level, state, entity, item, alternate)) { continue; }
            if ("cancel".equals(ScriptEngine.run(entry.script, bindings, disable))) { cancelled = true; }
        }
        return cancelled;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        ScriptEventDef def = ScriptEventDef.read(key, json);
        if (def != null && ScriptEventDef.MOD_EVENT.equals(def.kind)) { ScriptModEvents.accept(key, def); }
        else if (def != null) { EVENTS.computeIfAbsent(def.kind, kind -> new ArrayList<>()).add(def); }
    }
}
