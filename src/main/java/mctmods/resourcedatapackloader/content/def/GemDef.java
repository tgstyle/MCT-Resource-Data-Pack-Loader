package mctmods.resourcedatapackloader.content.def;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import java.util.List;

public final class GemDef {
    public final ItemStack item;
    public final String name;
    public final IAttribute attribute;
    public final double amount;
    public final int operation;
    public final List<EntityEquipmentSlot> slots;
    public final int levels;

    public GemDef(ItemStack item, String name, IAttribute attribute, double amount, int operation, List<EntityEquipmentSlot> slots, int levels) {
        this.item = item;
        this.name = name;
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
        this.slots = slots;
        this.levels = levels;
    }
}
