package mctmods.resourcedatapackloader.common.stacks;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public final class StackModifiers {
    private static final String MODIFIERS = "AttributeModifiers";
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

    public static int operation(ResourceLocation key, JsonObject json, String file, String skipped) {
        int operation = Json.getInt(json, "operation", 0);
        if (operation < 0 || operation > 2) {
            ContentLog.LOGGER.error("{} file {} sets operation to {}, which is not 0, 1 or 2, skipping {}", file, key, operation, skipped);
            return -1;
        }
        return operation;
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
        if (tag != null && tag.hasKey(MODIFIERS, Constants.NBT.TAG_LIST)) { return; }
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            Multimap<String, AttributeModifier> held = stack.getItem().getAttributeModifiers(slot, stack);
            for (Map.Entry<String, AttributeModifier> entry : held.entries()) { stack.addAttributeModifier(entry.getKey(), entry.getValue(), slot); }
        }
    }
}
