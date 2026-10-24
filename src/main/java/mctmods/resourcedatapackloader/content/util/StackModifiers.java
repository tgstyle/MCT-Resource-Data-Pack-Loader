package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public final class StackModifiers {
    private static final String MODIFIERS = "AttributeModifiers";
    private static final int LIST = 9;
    public static final String ANY = "any";

    private StackModifiers() {}

    @Nullable public static List<EntityEquipmentSlot> slots(ResourceLocation key, String text) {
        String named = text.trim();
        if (named.isEmpty() || ANY.equalsIgnoreCase(named)) { return Collections.unmodifiableList(Arrays.asList(EntityEquipmentSlot.values())); }
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getName().equalsIgnoreCase(named)) { return Collections.singletonList(slot); }
        }
        ContentLog.LOGGER.error("Unknown slot '{}' in {}, which takes mainhand, offhand, head, chest, legs, feet or {}", text, key, ANY);
        return null;
    }

    public static void add(ItemStack stack, String name, IAttribute attribute, double amount, int operation, List<EntityEquipmentSlot> slots, String idSeed) {
        seed(stack);
        for (EntityEquipmentSlot slot : slots) {
            UUID id = UUID.nameUUIDFromBytes((idSeed + ":" + slot.getName()).getBytes(StandardCharsets.UTF_8));
            stack.addAttributeModifier(attribute.getName(), new AttributeModifier(id, name, amount, operation), slot);
        }
    }

    private static void seed(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(MODIFIERS, LIST)) { return; }
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            Multimap<String, AttributeModifier> held = stack.getItem().getAttributeModifiers(slot, stack);
            for (Map.Entry<String, AttributeModifier> entry : held.entries()) { stack.addAttributeModifier(entry.getKey(), entry.getValue(), slot); }
        }
    }
}
