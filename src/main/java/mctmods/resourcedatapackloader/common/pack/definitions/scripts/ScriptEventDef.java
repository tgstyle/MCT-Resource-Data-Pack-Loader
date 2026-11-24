package mctmods.resourcedatapackloader.common.pack.definitions.scripts;

import com.google.gson.JsonObject;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ScriptEventDef {
    public static final String KIND = "event";
    public static final String BLOCK_PLACE = "block_place";
    public static final String BLOCK_BREAK = "block_break";
    public static final String ENTITY_HURT = "entity_hurt";
    public static final String ENTITY_SPAWN = "entity_spawn";
    public static final String ENTITY_DESPAWN = "entity_despawn";
    public static final String PLAYER_LOGIN = "player_login";
    public static final String PLAYER_TICK = "player_tick";
    public static final String SERVER_TICK = "server_tick";
    public static final String DIMENSION_CHANGE = "dimension_change";
    public static final String LOOT_DROP = "loot_drop";
    public static final String ENTITY_TRAVEL = "entity_travel";
    public static final String MOD_EVENT = "mod_event";
    private static final Set<String> KINDS = new HashSet<>(Arrays.asList(BLOCK_PLACE, BLOCK_BREAK, ENTITY_HURT, ENTITY_SPAWN, ENTITY_DESPAWN, PLAYER_LOGIN,
        PLAYER_TICK, SERVER_TICK, DIMENSION_CHANGE, LOOT_DROP, ENTITY_TRAVEL, MOD_EVENT));
    private static final List<String> EVENT_KEYS = Collections.unmodifiableList(Arrays.asList(KIND, "every", "filter", "scriptFile", "script", "class"));
    private static final Set<String> NO_KEYS = Collections.emptySet();
    private static final Set<String> NEW_KINDS = new HashSet<>(Arrays.asList(PLAYER_TICK, SERVER_TICK, DIMENSION_CHANGE, LOOT_DROP, ENTITY_TRAVEL, MOD_EVENT));

    public final String kind;
    public final int every;
    public final EventFilter filter;
    public final ScriptRef script;
    @Nullable public final String className;

    private ScriptEventDef(String kind, int every, EventFilter filter, ScriptRef script, @Nullable String className) {
        this.kind = kind;
        this.every = every;
        this.filter = filter;
        this.script = script;
        this.className = className;
    }

    public boolean due(long time) { return every == 1 || time % every == 0; }

    public static boolean disablesOnError(String kind) { return NEW_KINDS.contains(kind); }

    private static int defaultEvery(String kind) { return PLAYER_TICK.equals(kind) || SERVER_TICK.equals(kind) ? 20 : 1; }

    @Nullable private static Set<String> filterKeys(String kind) {
        switch (kind) {
            case PLAYER_TICK:
            case DIMENSION_CHANGE:
                return new HashSet<>(Arrays.asList(EventFilter.DIMENSION, EventFilter.ITEM));
            case LOOT_DROP:
            case ENTITY_TRAVEL:
                return new HashSet<>(Arrays.asList(EventFilter.DIMENSION, EventFilter.ENTITY, EventFilter.ITEM));
            case SERVER_TICK:
            case MOD_EVENT:
                return NO_KEYS;
            default:
                return null;
        }
    }

    @Nullable public static ScriptEventDef read(ResourceLocation owner, JsonObject json) {
        Json.unknownKeys(json, "the top level", EVENT_KEYS);
        String kind = Json.text(json, KIND, "");
        if (!KINDS.contains(kind)) {
            ContentLog.LOGGER.error("{}: unknown or missing event '{}', ignoring it", owner, kind);
            return null;
        }
        int every = Json.number(json, "every", defaultEvery(kind));
        if (every < 1) {
            ContentLog.LOGGER.error("{}: every must be at least 1, ignoring it", owner);
            return null;
        }
        EventFilter filter = EventFilter.read(owner, json.has("filter") ? Json.object(json, "filter") : null, kind, filterKeys(kind));
        if (filter == null) { return null; }
        ScriptRef script = ScriptRef.read(owner, json);
        if (script == null) {
            ContentLog.LOGGER.error("{}: needs script or scriptFile", owner);
            return null;
        }
        String className = MOD_EVENT.equals(kind) ? Json.text(json, "class", "").trim() : null;
        return new ScriptEventDef(kind, every, filter, script, className);
    }
}
