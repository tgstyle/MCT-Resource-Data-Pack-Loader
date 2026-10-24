package mctmods.resourcedatapackloader.content.def;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public record GemDef(ItemStack item, String name, Attribute attribute, double amount, int operation, List<EquipmentSlot> slots, int levels) {}
