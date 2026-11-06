package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import java.util.List;

public record RankRandomDef(float armor, float armorPiece, float weapon, float offhand, float enchant, List<String> exclude) {
    public static final RankRandomDef NONE = new RankRandomDef(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, List.of());
}
