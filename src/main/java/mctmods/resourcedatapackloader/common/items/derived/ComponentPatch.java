package mctmods.resourcedatapackloader.common.items.derived;

import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

public final class ComponentPatch {
    private static final List<Pending> PENDING = new ArrayList<>();

    private ComponentPatch() {}

    private record Pending(Item item, ResourceLocation key, JsonObject json, String name) {}

    static void remember(Item item, ResourceLocation key, JsonObject json, String name) {
        if (!json.isEmpty() || !name.isEmpty()) { PENDING.add(new Pending(item, key, json, name)); }
    }

    public static void onModify(ModifyDefaultComponentsEvent event) {
        if (PENDING.isEmpty()) { return; }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        for (Pending each : PENDING) {
            try {
                event.modify(each.item, patch -> {
                    if (!each.name.isEmpty()) { patch.set(DataComponents.ITEM_NAME, Component.literal(each.name)); }
                    for (Map.Entry<String, JsonElement> entry : each.json.entrySet()) { entry(patch, ops, each.key, entry.getKey(), entry.getValue()); }
                });
            }
            catch (RuntimeException e) { ContentLog.LOGGER.error("Derived item {} could not take its components, the base components stay: {}", each.key, e.toString()); }
        }
    }

    private static void entry(DataComponentPatch.Builder patch, RegistryOps<JsonElement> ops, ResourceLocation owner, String id, JsonElement value) {
        ResourceLocation named = ResourceLocation.tryParse(id);
        DataComponentType<?> type = named == null ? null : BuiltInRegistries.DATA_COMPONENT_TYPE.get(named);
        if (type == null || type.codec() == null) {
            ContentLog.LOGGER.error("Derived item {} sets the component {}, which is no component with a codec, leaving it out", owner, id);
            return;
        }
        apply(patch, ops, owner, id, type, value);
    }

    private static <T> void apply(DataComponentPatch.Builder patch, RegistryOps<JsonElement> ops, ResourceLocation owner, String id, DataComponentType<T> type, JsonElement value) {
        if (value.isJsonNull()) {
            patch.remove(type);
            return;
        }
        Codec<T> codec = Objects.requireNonNull(type.codec());
        DataResult<T> parsed = codec.parse(ops, value);
        if (parsed.result().isPresent()) { patch.set(type, parsed.result().get()); }
        else { ContentLog.LOGGER.error("Derived item {} sets the component {} to {}, which is {}, leaving it out", owner, id, value, parsed.error().map(DataResult.Error::message).orElse("not a valid value")); }
    }
}
