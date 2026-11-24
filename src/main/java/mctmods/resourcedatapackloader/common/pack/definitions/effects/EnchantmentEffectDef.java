package mctmods.resourcedatapackloader.common.pack.definitions.effects;

import mctmods.resourcedatapackloader.common.util.TableMath;

import java.util.List;

public record EnchantmentEffectDef(String type, List<String> from, String mob, String on, String potion, double amountBase, double amountPerLevel, double durationBase, double durationPerLevel, int amplifier) {
    public double amount(int level) { return TableMath.perLevel(amountBase, amountPerLevel, level); }

    public int duration(int level) { return (int) Math.max(1.0D, TableMath.perLevel(durationBase, durationPerLevel, level)); }
}
