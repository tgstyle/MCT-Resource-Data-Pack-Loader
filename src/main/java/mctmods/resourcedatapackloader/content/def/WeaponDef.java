package mctmods.resourcedatapackloader.content.def;

import java.util.List;
import javax.annotation.Nullable;

public record WeaponDef(float attackReach, @Nullable Sweep sweep, @Nullable Combo combo) {
    public static final float VANILLA_REACH = 3.0F;
    public static final float MOST_REACH = 64.0F;

    public boolean hasReach() { return !Float.isNaN(attackReach); }

    public float reachOrVanilla() { return hasReach() ? attackReach : VANILLA_REACH; }

    public enum Shape { BOX, ARC, LINE, CIRCLE }

    public record Sweep(Shape shape, float reach, float angle, float width, float damage) {}

    public record Step(float damage, @Nullable Sweep sweep) {}

    public record Combo(int window, List<Step> steps) {}
}
