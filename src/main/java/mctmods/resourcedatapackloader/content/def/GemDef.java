package mctmods.resourcedatapackloader.content.def;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public record GemDef(ItemStack item, String name, Holder<Attribute> attribute, double amount, int operation, List<EquipmentSlot> slots, int levels) {}
