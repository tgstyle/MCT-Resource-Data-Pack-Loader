package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class WaterDef {
    public static final int DEFAULT_WITHIN = 4;
    public static final int MAX_WITHIN = 16;
    public static final int DEFAULT_HEIGHT = 1;
    public static final int MAX_HEIGHT = 4;
    public final float scale;
    public final boolean farmland;
    public final boolean rain;
    public final boolean bonemeal;
    public final boolean waiver;
    public final boolean breaks;
    public static final ResourceLocation WATER = ResourceLocation.withDefaultNamespace("water");
    public final int within;
    private final List<ResourceLocation> fluids;
    private final List<TagKey<Fluid>> tags;
    private final boolean[] kinds;
    private final List<BlockMatch> blocks;
    private final List<BlockMatch> except;
    private final boolean flowing;
    private final int height;
    private final boolean diamond;
    @Nullable private volatile Set<Fluid> resolved;

    public WaterDef(int within, List<ResourceLocation> fluids, List<TagKey<Fluid>> tags, boolean[] kinds, List<BlockMatch> blocks, List<BlockMatch> except, float scale, boolean flowing, int height, boolean diamond, boolean[] flags) {
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
        this.farmland = flags[0];
        this.rain = flags[1];
        this.bonemeal = flags[2];
        this.waiver = flags[3];
        this.breaks = flags[4];
    }

    private static boolean listed(List<BlockMatch> matches, Block block) {
        for (BlockMatch match : matches) {
            if (match.matches(block)) { return true; }
        }
        return false;
    }

    public boolean covers(int kind, Block block, boolean standard) { return this.kinds[kind] && !listed(this.except, block) && (this.blocks.isEmpty() ? standard : listed(this.blocks, block)); }

    private Set<Fluid> resolved() {
        Set<Fluid> found = this.resolved;
        if (found == null) {
            found = resolve();
            this.resolved = found;
        }
        return found;
    }

    private Set<Fluid> resolve() {
        Set<Fluid> found = new HashSet<>();
        for (ResourceLocation id : this.fluids) {
            Fluid fluid = Registered.find(BuiltInRegistries.FLUID, id);
            if (fluid == null || fluid == Fluids.EMPTY) { ContentLog.LOGGER.error("growth.water.fluids names {}, which is not a registered fluid, ignoring it", id); }
            else { found.add(fluid instanceof FlowingFluid flow ? flow.getSource() : fluid); }
        }
        if (found.isEmpty() && this.tags.isEmpty()) {
            ContentLog.LOGGER.error("growth.water.fluids has no registered fluid, using water");
            return Set.of(Fluids.WATER);
        }
        return found;
    }

    private boolean accepts(FluidState state) {
        if (state.isEmpty()) { return false; }
        Fluid type = state.getType() instanceof FlowingFluid flow ? flow.getSource() : state.getType();
        if (resolved().contains(type)) { return true; }
        for (TagKey<Fluid> tag : this.tags) {
            if (state.is(tag)) { return true; }
        }
        return false;
    }

    public boolean listsWater() { return accepts(Fluids.WATER.defaultFluidState()); }

    private boolean wet(Level level, BlockPos.MutableBlockPos at) {
        FluidState state = level.getFluidState(at);
        return (this.flowing || state.isSource()) && accepts(state);
    }

    private boolean layers(Level level, BlockPos center, int dx, int dz, BlockPos.MutableBlockPos at) {
        for (int step = 0; step <= 2 * this.height; step++) {
            int dy = (step + 1 >> 1) * ((step & 1) == 1 ? -1 : 1);
            if (wet(level, at.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz))) { return true; }
        }
        return false;
    }

    private boolean ring(Level level, BlockPos center, int distance, BlockPos.MutableBlockPos at) {
        for (int dx = -distance; dx <= distance; dx++) {
            int limit = this.diamond ? distance - Math.abs(dx) : distance;
            boolean edge = this.diamond ? limit == 0 : Math.abs(dx) == distance;
            int stride = edge ? 1 : 2 * limit;
            for (int dz = -limit; dz <= limit; dz += stride) {
                if (layers(level, center, dx, dz, at)) { return true; }
            }
        }
        return false;
    }

    private boolean loaded(Level level, BlockPos pos) {
        for (int cx = pos.getX() - this.within >> 4; cx <= pos.getX() + this.within >> 4; cx++) {
            for (int cz = pos.getZ() - this.within >> 4; cz <= pos.getZ() + this.within >> 4; cz++) {
                if (!level.hasChunk(cx, cz)) { return false; }
            }
        }
        return true;
    }

    public boolean found(Level level, BlockPos pos) {
        if (!loaded(level, pos)) { return true; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int distance = 0; distance <= this.within; distance++) {
            if (ring(level, pos, distance, at)) { return true; }
        }
        return false;
    }
}
