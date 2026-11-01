package mctmods.resourcedatapackloader.common.pack.definitions.effects;

import java.util.List;

public record EnchantmentEffectDef(String type, List<String> from, String mob, String on, String potion, double amountBase, double amountPerLevel, double durationBase, double durationPerLevel, int amplifier) {}
