package mctmods.resourcedatapackloader.common.pack.definitions.entities;

public final class RankScalingDef {
    public static final RankScalingDef NONE = new RankScalingDef(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

    public final float difficulty;
    public final float daysPerStep;
    public final float distancePerStep;
    public final float health;
    public final float damage;
    public final float armor;
    public final float weapon;
    public final float enchant;

    public RankScalingDef(float difficulty, float daysPerStep, float distancePerStep, float health, float damage, float armor, float weapon, float enchant) {
        this.difficulty = difficulty;
        this.daysPerStep = daysPerStep;
        this.distancePerStep = distancePerStep;
        this.health = health;
        this.damage = damage;
        this.armor = armor;
        this.weapon = weapon;
        this.enchant = enchant;
    }
}
