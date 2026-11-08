package mctmods.resourcedatapackloader.common.pack.definitions.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public record WeaponDef(float attackReach, @Nullable Sweep sweep, @Nullable Combo combo, int fireSeconds, @Nullable Finisher finisher) {
    public static final float VANILLA_REACH = 3.0F;
    public static final float MOST_REACH = 64.0F;
    public static final float MOST_PUSH = 100.0F;

    public boolean hasReach() { return !Float.isNaN(attackReach); }

    public float reachOrVanilla() { return hasReach() ? attackReach : VANILLA_REACH; }

    public enum Shape { BOX, ARC, LINE, CIRCLE }

    public record Sweep(Shape shape, float reach, float angle, float width, float damage) {}

    public record Step(float damage, @Nullable Sweep sweep) {}

    public record Combo(int window, List<Step> steps) {}

    public record Finisher(List<Identifier> ids, List<String> kinds, float health, float damage, String damageType, float horizontal, float vertical) {}
}
