package mctmods.resourcedatapackloader.common.pack.definitions.effects;

import mctmods.resourcedatapackloader.common.util.TableMath;

import java.util.List;

public final class EnchantmentEffectDef {
    public final String type;
    public final List<String> from;
    public final String mob;
    public final String on;
    public final String potion;
    public final double amountBase;
    public final double amountPerLevel;
    public final double durationBase;
    public final double durationPerLevel;
    public final int amplifier;

    public EnchantmentEffectDef(String type, List<String> from, String mob, String on, String potion, double amountBase, double amountPerLevel, double durationBase, double durationPerLevel, int amplifier) {
        this.type = type;
        this.from = from;
        this.mob = mob;
        this.on = on;
        this.potion = potion;
        this.amountBase = amountBase;
        this.amountPerLevel = amountPerLevel;
        this.durationBase = durationBase;
        this.durationPerLevel = durationPerLevel;
        this.amplifier = amplifier;
    }

    public double amount(int level) { return TableMath.perLevel(amountBase, amountPerLevel, level); }

    public int duration(int level) { return (int) Math.max(1.0D, TableMath.perLevel(durationBase, durationPerLevel, level)); }
}
