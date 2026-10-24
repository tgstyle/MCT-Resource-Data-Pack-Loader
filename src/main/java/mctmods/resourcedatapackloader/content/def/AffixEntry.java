package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import java.util.List;

public record AffixEntry(String name, Attribute attribute, double min, double max, int operation, List<EquipmentSlot> slots, int weight) {}
