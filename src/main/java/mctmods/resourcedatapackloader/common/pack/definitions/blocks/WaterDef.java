package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class WaterDef {
    public static final int DEFAULT_WITHIN = 4;
    public static final int MAX_WITHIN = 16;
    public static final int DEFAULT_HEIGHT = 1;
    public static final int MAX_HEIGHT = 4;
    public static final Identifier WATER = Identifier.withDefaultNamespace("water");
    private final int within;
    private final List<Identifier> fluids;
    private final List<TagKey<Fluid>> tags;
    private final boolean[] kinds;
    private final List<BlockMatch> blocks;
    private final List<BlockMatch> except;
    private final float scale;
    private final boolean flowing;
    private final int height;
    private final boolean diamond;
    private final boolean farmland;
    private final boolean rain;
    private final boolean bonemeal;
    private final boolean waiver;
    private final boolean breaks;
    @Nullable private volatile Set<Fluid> resolved;

    public WaterDef(int within, List<Identifier> fluids, List<TagKey<Fluid>> tags, boolean[] kinds, List<BlockMatch> blocks, List<BlockMatch> except, float scale, boolean flowing, int height, boolean diamond, boolean farmland, boolean rain, boolean bonemeal, boolean waiver, boolean breaks) {
        this.within = within;
        this.fluids = fluids;
        this.tags = tags;
        this.kinds = kinds;
        this.blocks = blocks;
        this.except = except;
        this.scale = scale;
        this.flowing = flowing;
        this.height = height;
        this.diamond = diamond;
        this.farmland = farmland;
        this.rain = rain;
        this.bonemeal = bonemeal;
        this.waiver = waiver;
        this.breaks = breaks;
    }

    public int within() { return within; }

    public float scale() { return scale; }

    public boolean farmland() { return farmland; }

    public boolean rain() { return rain; }

    public boolean bonemeal() { return bonemeal; }

    public boolean waiver() { return waiver; }

    public boolean breaks() { return breaks; }

    public List<BlockMatch> blocks() { return blocks; }

    public List<BlockMatch> except() { return except; }

    public boolean covers(int kind) { return kinds[kind]; }

    private Set<Fluid> resolved() {
        Set<Fluid> found = resolved;
        if (found == null) {
            found = resolve();
            resolved = found;
        }
        return found;
    }

    private Set<Fluid> resolve() {
        Set<Fluid> found = new HashSet<>();
        for (Identifier id : fluids) {
            Fluid fluid = Registered.find(BuiltInRegistries.FLUID, id);
            if (fluid == null || fluid == Fluids.EMPTY) { ContentLog.LOGGER.error("growth.water.fluids names {}, which is not a registered fluid, ignoring it", id); }
            else { found.add(fluid instanceof FlowingFluid flow ? flow.getSource() : fluid); }
        }
        if (found.isEmpty() && tags.isEmpty()) {
            ContentLog.LOGGER.error("growth.water.fluids has no registered fluid, using water");
            return Set.of(Fluids.WATER);
        }
        return found;
    }

    private boolean accepts(FluidState state) {
        if (state.isEmpty()) { return false; }
        Fluid type = state.getType() instanceof FlowingFluid flow ? flow.getSource() : state.getType();
        if (resolved().contains(type)) { return true; }
        for (TagKey<Fluid> tag : tags) {
            if (state.is(tag)) { return true; }
        }
        return false;
    }

    public boolean listsWater() { return accepts(Fluids.WATER.defaultFluidState()); }

    private boolean listed(FluidState state) { return (flowing || state.isSource()) && accepts(state); }

    private boolean onRing(int dx, int dz, int ring) { return diamond ? Math.abs(dx) + Math.abs(dz) == ring : Math.max(Math.abs(dx), Math.abs(dz)) == ring; }

    private boolean column(Level level, BlockPos.MutableBlockPos at, int x, int y, int z) {
        for (int layer = 0; layer <= height; layer++) {
            if (listed(level.getFluidState(at.set(x, y - layer, z)))) { return true; }
            if (layer > 0 && listed(level.getFluidState(at.set(x, y + layer, z)))) { return true; }
        }
        return false;
    }

    public boolean found(Level level, BlockPos pos) {
        if (!level.isAreaLoaded(pos, within)) { return true; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int ring = 0; ring <= within; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (onRing(dx, dz, ring) && column(level, at, pos.getX() + dx, pos.getY(), pos.getZ() + dz)) { return true; }
                }
            }
        }
        return false;
    }
}
