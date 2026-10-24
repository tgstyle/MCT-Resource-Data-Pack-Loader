package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public final class StackModifiers {
    private static final String MODIFIERS = "AttributeModifiers";
    public static final String ANY = "any";

    private StackModifiers() {}

    @Nullable public static List<EquipmentSlot> slots(ResourceLocation key, String text) {
        String named = text.trim();
        if (named.isEmpty() || ANY.equalsIgnoreCase(named)) { return List.of(EquipmentSlot.values()); }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getName().equalsIgnoreCase(named)) { return List.of(slot); }
        }
        ContentLog.LOGGER.error("Unknown slot '{}' in {}, which takes mainhand, offhand, head, chest, legs, feet or {}", text, key, ANY);
        return null;
    }

    public static void add(ItemStack stack, String name, Attribute attribute, double amount, int operation, List<EquipmentSlot> slots, String idSeed) {
        seed(stack);
        for (EquipmentSlot slot : slots) {
            UUID id = UUID.nameUUIDFromBytes((idSeed + ":" + slot.getName()).getBytes(StandardCharsets.UTF_8));
            stack.addAttributeModifier(attribute, new AttributeModifier(id, name, amount, AttributeModifier.Operation.fromValue(operation)), slot);
        }
    }

    private static void seed(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(MODIFIERS, Tag.TAG_LIST)) { return; }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> held = stack.getItem().getAttributeModifiers(slot, stack);
            held.forEach((attribute, modifier) -> stack.addAttributeModifier(attribute, modifier, slot));
        }
    }
}
