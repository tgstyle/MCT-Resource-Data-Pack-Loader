package mctmods.resourcedatapackloader.common.pack.definitions.scripts;

import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.util.BlockMatches;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class EventFilter {
    public static final String DIMENSION = "dimension";
    public static final String BLOCK = "block";
    public static final String ENTITY = "entity";
    public static final String ITEM = "item";
    private static final Set<String> KEYS = Set.of(DIMENSION, BLOCK, ENTITY, ITEM);
    public static final EventFilter ALL = new EventFilter(Collections.emptyList(), null, null, null, null);
    private final List<String> dimensions;
    @Nullable private final BlockMatch block;
    @Nullable private final List<Identifier> entityIds;
    @Nullable private final List<String> entityKinds;
    @Nullable private final ItemMatch item;

    private EventFilter(List<String> dimensions, @Nullable BlockMatch block, @Nullable List<Identifier> entityIds, @Nullable List<String> entityKinds, @Nullable ItemMatch item) {
        this.dimensions = dimensions;
        this.block = block;
        this.entityIds = entityIds;
        this.entityKinds = entityKinds;
        this.item = item;
    }

    @Nullable public static EventFilter read(Identifier owner, @Nullable JsonObject json, String kind, @Nullable Set<String> allowed) {
        if (json == null) { return ALL; }
        for (Map.Entry<String, JsonElement> member : json.entrySet()) {
            if (!KEYS.contains(member.getKey())) {
                ContentLog.LOGGER.error("{}: unknown filter key '{}', skipping the entry", owner, member.getKey());
                return null;
            }
            if (allowed != null && !allowed.contains(member.getKey())) {
                ContentLog.LOGGER.error("{}: the {} event does not read the filter key '{}', skipping the entry", owner, kind, member.getKey());
                return null;
            }
        }
        String ownerName = owner.toString();
        List<String> dimensions = new ArrayList<>();
        if (json.has(DIMENSION)) {
            for (String name : names(json.get(DIMENSION), DIMENSION)) { dimensions.add(CardPlace.dimensionId(name)); }
        }
        BlockMatch block = json.has(BLOCK) ? BlockMatches.parse(ownerName, Json.text(json, BLOCK), "filter block") : null;
        if (json.has(BLOCK) && block == null) { return skipped(owner); }
        List<Identifier> entityIds = null;
        List<String> entityKinds = null;
        if (json.has(ENTITY)) {
            entityIds = new ArrayList<>();
            entityKinds = new ArrayList<>();
            JsonArray listed = new JsonArray();
            for (String name : names(json.get(ENTITY), ENTITY)) { listed.add(name); }
            if (!MobStats.entities(ownerName, "filter entity", listed, entityIds, entityKinds) || (entityIds.isEmpty() && entityKinds.isEmpty())) { return skipped(owner); }
        }
        ItemMatch item = json.has(ITEM) ? ItemMatch.parse(owner, Json.text(json, ITEM)) : null;
        if (json.has(ITEM) && item == null) { return skipped(owner); }
        return new EventFilter(Collections.unmodifiableList(dimensions), block, entityIds, entityKinds, item);
    }

    @Nullable private static EventFilter skipped(Identifier owner) {
        ContentLog.LOGGER.error("{}: filter value did not parse, skipping the entry", owner);
        return null;
    }

    private static List<String> names(JsonElement element, String key) {
        JsonArray array;
        if (element.isJsonArray()) { array = element.getAsJsonArray(); }
        else {
            array = new JsonArray();
            array.add(element);
        }
        List<String> values = new ArrayList<>();
        for (String name : Json.texts(array, key)) {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) { values.add(trimmed); }
        }
        return Collections.unmodifiableList(values);
    }

    public boolean matches(Level level, @Nullable BlockState state, @Nullable Entity entity, @Nullable ItemStack stack) {
        if (!dimensions.isEmpty() && !CardPlace.inAny(dimensions, level)) { return false; }
        if (block != null && (state == null || !block.matches(state.getBlock()))) { return false; }
        if (entityIds != null && (entity == null || !MobStats.targets(entityIds, entityKinds, entity))) { return false; }
        return item == null || stack != null && item.matches(stack);
    }
}
