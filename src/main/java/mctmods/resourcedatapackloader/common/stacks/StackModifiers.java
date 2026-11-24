package mctmods.resourcedatapackloader.common.stacks;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class StackModifiers {
    public static final String ANY = "any";
    public static final List<EquipmentSlot> WORN = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    private StackModifiers() {}

    @Nullable public static List<EquipmentSlot> slots(Identifier key, String text) {
        String named = text.trim();
        if (named.isEmpty() || ANY.equalsIgnoreCase(named)) { return WORN; }
        for (EquipmentSlot slot : WORN) {
            if (slot.getName().equalsIgnoreCase(named)) { return List.of(slot); }
        }
        ContentLog.LOGGER.error("Unknown slot '{}' in {}, which takes mainhand, offhand, head, chest, legs, feet or {}", text, key, ANY);
        return null;
    }

    public static int operation(Identifier key, JsonObject json, String file, String skipped) {
        int operation = Json.getInt(json, "operation", 0);
        if (operation < 0 || operation > 2) {
            ContentLog.LOGGER.error("{} file {} sets operation to {}, which is not 0, 1 or 2, skipping {}", file, key, operation, skipped);
            return -1;
        }
        return operation;
    }

    public static void add(ItemStack stack, String kind, int index, Holder<Attribute> attribute, double amount, int operation, List<EquipmentSlot> slots) {
        ItemAttributeModifiers held = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (EquipmentSlot slot : slots) {
            Identifier id = Identifier.fromNamespaceAndPath("rdpl", kind + "/" + index + "/" + slot.getName());
            held = held.withModifierAdded(attribute, new AttributeModifier(id, amount, AttributeModifier.Operation.BY_ID.apply(operation)), EquipmentSlotGroup.bySlot(slot));
        }
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, held);
    }
}
