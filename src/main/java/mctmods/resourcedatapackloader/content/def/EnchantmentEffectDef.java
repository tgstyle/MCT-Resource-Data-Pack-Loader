package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record EnchantmentEffectDef(String type, List<String> from, String mob, String on, String potion, double amountBase, double amountPerLevel, double durationBase, double durationPerLevel, int amplifier) {
    public double amount(int level) { return amountBase + amountPerLevel * (level - 1); }

    public int duration(int level) { return (int) Math.max(1.0D, durationBase + durationPerLevel * (level - 1)); }
}
