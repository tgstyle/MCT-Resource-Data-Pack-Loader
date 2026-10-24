package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record EnchantmentEffectDef(String type, List<String> from, String mob, String on, String potion, double amountBase, double amountPerLevel, double durationBase, double durationPerLevel, int amplifier) {}
