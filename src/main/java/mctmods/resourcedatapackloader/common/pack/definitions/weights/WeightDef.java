package mctmods.resourcedatapackloader.common.pack.definitions.weights;

import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

public final class WeightDef {
    public static final String HARD_PICKUP = "pickup";
    public static final String HARD_TAKE = "take";
    public static final String HARD_BOTH = "both";
    public static final String CONTENTS_NONE = "none";
    public static final String CONTENTS_RDPL = "rdpl";
    public static final String CONTENTS_ALL = "all";
    public static final String HUD_NEVER = "never";
    public static final String HUD_TIERS = "tiers";
    public static final String HUD_ALWAYS = "always";
    public static final String HUD_HOTBAR = "hotbar";
    public static final String HUD_TOP_LEFT = "top_left";
    public static final String HUD_TOP = "top";
    public static final String HUD_TOP_RIGHT = "top_right";
    public static final String HUD_LEFT = "left";
    public static final String HUD_CENTER = "center";
    public static final String HUD_RIGHT = "right";
    public static final String HUD_BOTTOM_LEFT = "bottom_left";
    public static final String HUD_BOTTOM = "bottom";
    public static final String HUD_BOTTOM_RIGHT = "bottom_right";

    public final String unit;
    public final int defaultWeight;
    public final int limit;
    public final List<Bonus> bonuses;
    public final List<Tier> tiers;
    public final double hardLimit;
    public final String hardLimitMode;
    public final int stackBudget;
    public final int stackBudgetMin;
    public final double containerFactor;
    public final String countContents;
    public final double wornFactor;
    public final boolean creative;
    public final String hudShow;
    public final boolean hudInventory;
    public final String hudAnchor;
    public final int hudX;
    public final int hudY;
    public final boolean tooltip;

    private WeightDef(Builder from) {
        this.unit = from.unit;
        this.defaultWeight = from.defaultWeight;
        this.limit = from.limit;
        this.bonuses = List.copyOf(from.bonuses);
        List<Tier> sorted = new ArrayList<>(from.tiers);
        sorted.sort(Comparator.comparingDouble(Tier::above));
        this.tiers = Collections.unmodifiableList(sorted);
        this.hardLimit = from.hardLimit;
        this.hardLimitMode = from.hardLimitMode;
        this.stackBudget = from.stackBudget;
        this.stackBudgetMin = from.stackBudgetMin;
        this.containerFactor = from.containerFactor;
        this.countContents = from.countContents;
        this.wornFactor = from.wornFactor;
        this.creative = from.creative;
        this.hudShow = from.hudShow;
        this.hudInventory = from.hudInventory;
        this.hudAnchor = from.hudAnchor;
        this.hudX = from.hudX;
        this.hudY = from.hudY;
        this.tooltip = from.tooltip;
    }

    public boolean limited() { return limit > 0; }

    public record Bonus(int add, @Nullable String advancement, List<String> worn) {}

    public record Tier(double above, double speed, boolean sprint, boolean jump, double exhaustion, List<PotionEffectDef> effects, int color, String message) {}

    public static final class Builder {
        public String unit = "";
        public int defaultWeight = 0;
        public int limit = 0;
        public final List<Bonus> bonuses = new ArrayList<>();
        public final List<Tier> tiers = new ArrayList<>();
        public double hardLimit = 0.0D;
        public String hardLimitMode = HARD_BOTH;
        public int stackBudget = 0;
        public int stackBudgetMin = 1;
        public double containerFactor = 1.0D;
        public String countContents = CONTENTS_RDPL;
        public double wornFactor = 1.0D;
        public boolean creative = false;
        public String hudShow = HUD_TIERS;
        public boolean hudInventory = true;
        public String hudAnchor = HUD_HOTBAR;
        public int hudX = 0;
        public int hudY = 0;
        public boolean tooltip = true;

        public WeightDef build() { return new WeightDef(this); }
    }
}
