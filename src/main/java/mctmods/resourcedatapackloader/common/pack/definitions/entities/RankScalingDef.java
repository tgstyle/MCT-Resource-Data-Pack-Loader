package mctmods.resourcedatapackloader.common.pack.definitions.entities;

public record RankScalingDef(float difficulty, float daysPerStep, float distancePerStep, float health, float damage, float armor, float weapon, float enchant) {
    public static final RankScalingDef NONE = new RankScalingDef(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
}
