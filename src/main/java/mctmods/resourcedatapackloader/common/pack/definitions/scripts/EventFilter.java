package mctmods.resourcedatapackloader.common.pack.definitions.scripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.entities.behavior.MobStats;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.util.BlockMatches;
import mctmods.resourcedatapackloader.common.util.ItemMatch;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class EventFilter {
    private static final Set<String> KEYS = new HashSet<>(Arrays.asList("dimension", "block", "entity", "item"));
    public static final EventFilter ALL = new EventFilter(Collections.emptyList(), null, null, null);
    private final List<String> dimensions;
    @Nullable private final BlockMatch block;
    @Nullable private final List<String> entities;
    @Nullable private final ItemMatch item;

    private EventFilter(List<String> dimensions, @Nullable BlockMatch block, @Nullable List<String> entities, @Nullable ItemMatch item) {
        this.dimensions = dimensions;
        this.block = block;
        this.entities = entities;
        this.item = item;
    }

    @Nullable public static EventFilter read(ResourceLocation owner, @Nullable JsonObject json, String kind, @Nullable Set<String> allowed) {
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
        List<String> dimensions = json.has("dimension") ? names(json.get("dimension"), "dimension") : Collections.emptyList();
        BlockMatch block = json.has("block") ? BlockMatches.parse(ownerName, Json.text(json, "block"), "filter block") : null;
        List<String> entities = json.has("entity") ? names(json.get("entity"), "entity") : null;
        ItemMatch item = json.has("item") ? ItemMatch.parse(owner, Json.text(json, "item")) : null;
        if (json.has("block") && block == null || json.has("item") && item == null) {
            ContentLog.LOGGER.error("{}: filter value did not parse, skipping the entry", owner);
            return null;
        }
        if (entities != null) {
            for (String entity : entities) {
                if (entity.startsWith("#") && !creatureType(entity.substring(1))) {
                    ContentLog.LOGGER.error("{}: unknown entity group '{}', skipping the entry", owner, entity);
                    return null;
                }
            }
        }
        return new EventFilter(dimensions, block, entities, item);
    }

    private static boolean creatureType(String name) {
        for (EnumCreatureType type : EnumCreatureType.values()) {
            if (type.name().equalsIgnoreCase(name)) { return true; }
        }
        return false;
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

    public boolean matches(@Nullable World world, @Nullable BlockPos pos, int dimension, @Nullable IBlockState state, @Nullable Entity entity, @Nullable ItemStack stack) {
        if (!dimensions.isEmpty() && !CardPlace.inAny(dimensions, dimension)) { return false; }
        if (block != null && (state == null || !block.matches(state.getBlock(), world, pos))) { return false; }
        if (entities != null && (entity == null || !MobStats.targets(entities, entity))) { return false; }
        return item == null || stack != null && item.matches(stack);
    }
}
