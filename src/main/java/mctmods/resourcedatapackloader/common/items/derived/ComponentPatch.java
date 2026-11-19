package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

final class ComponentPatch {
    private static final String REMOVE = "!";

    private ComponentPatch() {}

    static void apply(DataComponentMap.Builder builder, HolderLookup.Provider context, JsonObject patch, Identifier owner) {
        for (Map.Entry<String, JsonElement> entry : patch.entrySet()) {
            String name = entry.getKey();
            boolean remove = name.startsWith(REMOVE);
            String text = remove ? name.substring(REMOVE.length()) : name;
            Identifier id = Identifier.tryParse(text);
            DataComponentType<?> type = id == null ? null : BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(id).orElse(null);
            if (type == null || type.isTransient()) {
                ContentLog.LOGGER.error("Derived item {} sets component {}, which is not a persistent component, leaving it out", owner, text);
                continue;
            }
            if (remove) { builder.set(type, null); }
            else { put(builder, type, entry.getValue(), context, owner); }
        }
    }

    private static <T> void put(DataComponentMap.Builder builder, DataComponentType<T> type, JsonElement json, HolderLookup.Provider context, Identifier owner) {
        type.codecOrThrow().parse(context.createSerializationContext(JsonOps.INSTANCE), json)
            .ifSuccess(value -> builder.set(type, value))
            .ifError(error -> ContentLog.LOGGER.error("Derived item {} sets component {} to {}, which is not valid: {}, leaving it out", owner, BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type), json, error.message()));
    }
}
