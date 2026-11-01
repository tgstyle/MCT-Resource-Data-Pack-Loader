package mctmods.resourcedatapackloader.common.pack.definitions.effects;

public record CostDef(CostPart xp, boolean xpPoints, String itemId, CostPart item, CostPart health, CostPart hunger, int cooldown) { public static final CostDef NONE = new CostDef(CostPart.NONE, false, "", CostPart.NONE, CostPart.NONE, CostPart.NONE, 0); }
