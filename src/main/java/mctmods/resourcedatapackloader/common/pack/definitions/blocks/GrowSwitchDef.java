package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;

public final class GrowSwitchDef {
    public static final int CROPS = 0;
    public static final int SAPLINGS = 1;
    public static final int ANIMALS = 2;
    public static final int BREEDING = 3;
    public static final int KINDS = 4;
    public static final int TOOLTIP_OFF = 0;
    public static final int TOOLTIP_YEAR = 2;
    public static final GrowSwitchDef NONE = new GrowSwitchDef(filled(1.0F), List.of(), null, TOOLTIP_OFF);
    private final float[] scales;
    private final List<Match> byBlock;
    @Nullable private final WaiverDef waiver;
    private final int tooltip;

    public record Match(BlockMatch what, float scale) {}

    public GrowSwitchDef(float[] scales, List<Match> byBlock, @Nullable WaiverDef waiver, int tooltip) {
        this.scales = scales;
        this.byBlock = byBlock;
        this.waiver = waiver;
        this.tooltip = tooltip;
    }

    public static float[] filled(float value) {
        float[] values = new float[KINDS];
        Arrays.fill(values, value);
        return values;
    }

    public float scale(int kind) { return scales[kind]; }

    public float scale(int kind, Block block) {
        if (kind > SAPLINGS) { return scales[kind]; }
        for (Match match : byBlock) {
            if (!match.what().group() && match.what().matches(block)) { return match.scale(); }
        }
        for (Match match : byBlock) {
            if (match.what().group() && match.what().matches(block)) { return match.scale(); }
        }
        return scales[kind];
    }

    @Nullable public WaiverDef waiver() { return waiver; }

    public int tooltip() { return tooltip; }
}
