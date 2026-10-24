package mctmods.resourcedatapackloader.content.def;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import java.util.List;

public final class AffixEntry {
    public final String name;
    public final IAttribute attribute;
    public final double min;
    public final double max;
    public final int operation;
    public final List<EntityEquipmentSlot> slots;
    public final int weight;

    public AffixEntry(String name, IAttribute attribute, double min, double max, int operation, List<EntityEquipmentSlot> slots, int weight) {
        this.name = name;
        this.attribute = attribute;
        this.min = min;
        this.max = max;
        this.operation = operation;
        this.slots = slots;
        this.weight = weight;
    }
}
