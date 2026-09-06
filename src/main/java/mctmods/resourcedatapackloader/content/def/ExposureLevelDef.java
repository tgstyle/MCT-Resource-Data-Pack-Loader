package mctmods.resourcedatapackloader.content.def;

import java.util.List;

public record ExposureLevelDef(String effect, float damage, int damageInterval, List<PotionEffectDef> extras) {}
