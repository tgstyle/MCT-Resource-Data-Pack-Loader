package mctmods.resourcedatapackloader.common.pack.definitions.entities;

import java.util.Collections;
import java.util.List;

public final class RankRandomDef {
    public static final RankRandomDef NONE = new RankRandomDef(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Collections.emptyList());

    public final float armor;
    public final float armorPiece;
    public final float weapon;
    public final float offhand;
    public final float enchant;
    public final List<String> exclude;

    public RankRandomDef(float armor, float armorPiece, float weapon, float offhand, float enchant, List<String> exclude) {
        this.armor = armor;
        this.armorPiece = armorPiece;
        this.weapon = weapon;
        this.offhand = offhand;
        this.enchant = enchant;
        this.exclude = Collections.unmodifiableList(exclude);
    }
}
