package mctmods.resourcedatapackloader.content.def;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import java.util.List;

public record AffixEntry(String name, Holder<Attribute> attribute, double min, double max, int operation, List<EquipmentSlot> slots, int weight) {}
