package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

public final class StackModifiers {
    public static final String ANY = "any";
    private static final List<EquipmentSlot> CONCRETE = Arrays.stream(EquipmentSlot.values()).filter(slot -> slot != EquipmentSlot.BODY).toList();

    private StackModifiers() {}

    @Nullable public static List<EquipmentSlot> slots(ResourceLocation key, String text) {
        String named = text.trim();
        if (named.isEmpty() || ANY.equalsIgnoreCase(named)) { return CONCRETE; }
        for (EquipmentSlot slot : CONCRETE) {
            if (slot.getName().equalsIgnoreCase(named)) { return List.of(slot); }
        }
        ContentLog.LOGGER.error("Unknown slot '{}' in {}, which takes mainhand, offhand, head, chest, legs, feet or {}", text, key, ANY);
        return null;
    }

    public static void add(ItemStack stack, Holder<Attribute> attribute, double amount, int operation, List<EquipmentSlot> slots, String idSeed) {
        ItemAttributeModifiers held = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (EquipmentSlot slot : slots) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("rdpl", idSeed + "/" + slot.getName());
            held = held.withModifierAdded(attribute, new AttributeModifier(id, amount, AttributeModifier.Operation.BY_ID.apply(operation)), EquipmentSlotGroup.bySlot(slot));
        }
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, held);
    }
}
