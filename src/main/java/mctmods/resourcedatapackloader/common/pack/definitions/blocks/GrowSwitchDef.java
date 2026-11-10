package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class GrowSwitchDef {
    public static final int CROPS = 0;
    public static final int SAPLINGS = 1;
    public static final int ANIMALS = 2;
    public static final int BREEDING = 3;
    public static final int KINDS = 4;
    public static final int TOOLTIP_OFF = 0;
    public static final int TOOLTIP_YEAR = 2;
    public static final GrowSwitchDef NONE = new GrowSwitchDef(filled(1.0F), Collections.emptyList(), null, TOOLTIP_OFF);
    private final float[] scales;
    private final List<Match> byBlock;
    @Nullable private final WaiverDef waiver;
    private final int tooltip;

    public static final class Match {
        private final BlockMatch what;
        private final float scale;

        public Match(BlockMatch what, float scale) {
            this.what = what;
            this.scale = scale;
        }
    }

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

    public float scale(int kind) { return this.scales[kind]; }

    public float scale(int kind, Block block, World world, BlockPos pos) {
        if (kind > SAPLINGS) { return this.scales[kind]; }
        for (Match match : this.byBlock) {
            if (!match.what.group() && match.what.matches(block, world, pos)) { return match.scale; }
        }
        for (Match match : this.byBlock) {
            if (match.what.group() && match.what.matches(block, world, pos)) { return match.scale; }
        }
        return this.scales[kind];
    }

    @Nullable public WaiverDef waiver() { return this.waiver; }

    public int tooltip() { return this.tooltip; }
}
