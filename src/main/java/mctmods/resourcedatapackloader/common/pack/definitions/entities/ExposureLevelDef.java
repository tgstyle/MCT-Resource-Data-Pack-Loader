package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;

import java.util.List;

public record ExposureLevelDef(String effect, float damage, int damageInterval, List<PotionEffectDef> extras, String onEnter) {}
