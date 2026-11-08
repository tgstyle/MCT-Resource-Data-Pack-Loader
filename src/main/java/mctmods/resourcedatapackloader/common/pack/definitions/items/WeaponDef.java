package mctmods.resourcedatapackloader.common.pack.definitions.items;

import javax.annotation.Nullable;
import java.util.List;

public final class WeaponDef {
    public static final float VANILLA_REACH = 3.0F;
    public static final float MOST_REACH = 64.0F;
    public final float attackReach;
    @Nullable public final Sweep sweep;
    @Nullable public final Combo combo;
    public final int fireSeconds;
    @Nullable public final Finisher finisher;

    public WeaponDef(float attackReach, @Nullable Sweep sweep, @Nullable Combo combo, int fireSeconds, @Nullable Finisher finisher) {
        this.attackReach = attackReach;
        this.sweep = sweep;
        this.combo = combo;
        this.fireSeconds = fireSeconds;
        this.finisher = finisher;
    }

    public boolean onHit() { return fireSeconds > 0 || finisher != null; }

    public boolean hasReach() { return !Float.isNaN(attackReach); }

    public float reachOrVanilla() { return hasReach() ? attackReach : VANILLA_REACH; }

    public enum Shape { BOX, ARC, LINE, CIRCLE }

    public static final class Sweep {
        public final Shape shape;
        public final float reach;
        public final float angle;
        public final float width;
        public final float damage;

        public Sweep(Shape shape, float reach, float angle, float width, float damage) {
            this.shape = shape;
            this.reach = reach;
            this.angle = angle;
            this.width = width;
            this.damage = damage;
        }
    }

    public static final class Finisher {
        public final List<String> targets;
        public final float health;
        public final float damage;
        public final String damageType;
        public final float pushHorizontal;
        public final float pushVertical;

        public Finisher(List<String> targets, float health, float damage, String damageType, float pushHorizontal, float pushVertical) {
            this.targets = targets;
            this.health = health;
            this.damage = damage;
            this.damageType = damageType;
            this.pushHorizontal = pushHorizontal;
            this.pushVertical = pushVertical;
        }
    }

    public static final class Step {
        public final float damage;
        @Nullable public final Sweep sweep;

        public Step(float damage, @Nullable Sweep sweep) {
            this.damage = damage;
            this.sweep = sweep;
        }
    }

    public static final class Combo {
        public final int window;
        public final List<Step> steps;

        public Combo(int window, List<Step> steps) {
            this.window = window;
            this.steps = steps;
        }
    }
}
