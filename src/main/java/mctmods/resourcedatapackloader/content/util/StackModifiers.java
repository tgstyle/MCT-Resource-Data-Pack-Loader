package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import java.util.List;
import javax.annotation.Nullable;

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

    public static void add(ItemStack stack, String kind, int index, Holder<Attribute> attribute, double amount, int operation, List<EquipmentSlot> slots) {
        ItemAttributeModifiers held = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (EquipmentSlot slot : slots) {
            Identifier id = Identifier.fromNamespaceAndPath("rdpl", kind + "/" + index + "/" + slot.getName());
            held = held.withModifierAdded(attribute, new AttributeModifier(id, amount, AttributeModifier.Operation.BY_ID.apply(operation)), EquipmentSlotGroup.bySlot(slot));
        }
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, held);
    }
}
