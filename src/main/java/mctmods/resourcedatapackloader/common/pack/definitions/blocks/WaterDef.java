package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

public final class WaterDef {
    public static final int DEFAULT_WITHIN = 4;
    public static final int MAX_WITHIN = 16;
    public static final int DEFAULT_HEIGHT = 1;
    public static final int MAX_HEIGHT = 4;
    public static final float DEFAULT_SCALE = 0.0F;
    private final int within;
    private final List<ResourceLocation> fluids;
    private final List<TagKey<Fluid>> tags;
    private final boolean crops;
    private final boolean saplings;
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

    public WaterDef(int within, List<ResourceLocation> fluids, List<TagKey<Fluid>> tags, boolean crops, boolean saplings, List<BlockMatch> blocks, List<BlockMatch> except, float scale, boolean flowing, int height, boolean diamond, boolean farmland, boolean rain, boolean bonemeal, boolean waiver, boolean breaks) {
        this.within = within;
        this.fluids = fluids;
        this.tags = tags;
        this.crops = crops;
        this.saplings = saplings;
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

    public static ResourceLocation water() { return ForgeRegistries.FLUIDS.getKey(Fluids.WATER); }

    public int within() { return this.within; }

    public float scale() { return this.scale; }

    public boolean farmland() { return this.farmland; }

    public boolean rain() { return this.rain; }

    public boolean bonemeal() { return this.bonemeal; }

    public boolean waiver() { return this.waiver; }

    public boolean breaks() { return this.breaks; }

    public boolean covers(int kind) { return kind == GrowSwitchDef.SAPLINGS ? this.saplings : this.crops; }

    public boolean named() { return !this.blocks.isEmpty(); }

    public boolean listed(Block block) { return any(this.blocks, block); }

    public boolean excepted(Block block) { return any(this.except, block); }

    private static boolean any(List<BlockMatch> matches, Block block) {
        for (BlockMatch match : matches) {
            if (match.matches(block)) { return true; }
        }
        return false;
    }

    public boolean listsWater() { return accepts(Fluids.WATER.defaultFluidState()); }

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
            Fluid fluid = Registered.find(ForgeRegistries.FLUIDS, id);
            if (fluid == null || fluid == Fluids.EMPTY) { ContentLog.LOGGER.error("growth.water.fluids names {}, which is not a registered fluid, ignoring it", id); }
            else { found.add(fluid instanceof FlowingFluid flowingType ? flowingType.getSource() : fluid); }
        }
        if (found.isEmpty() && this.tags.isEmpty()) {
            ContentLog.LOGGER.error("growth.water.fluids has no registered fluid, using water");
            return Set.of(Fluids.WATER);
        }
        return found;
    }

    private boolean accepts(FluidState state) {
        if (state.isEmpty()) { return false; }
        Fluid type = state.getType();
        if (resolved().contains(type instanceof FlowingFluid flowingType ? flowingType.getSource() : type)) { return true; }
        for (TagKey<Fluid> tag : this.tags) {
            if (state.is(tag)) { return true; }
        }
        return false;
    }

    public boolean supplied(Level level, BlockPos pos) {
        if (!loaded(level, pos)) { return true; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int reach = 0; reach <= this.within; reach++) {
            for (int dx = -reach; dx <= reach; dx++) {
                if (ring(level, pos, at, dx, reach)) { return true; }
            }
        }
        return false;
    }

    private boolean loaded(Level level, BlockPos pos) {
        for (int x = pos.getX() - this.within >> 4; x <= pos.getX() + this.within >> 4; x++) {
            for (int z = pos.getZ() - this.within >> 4; z <= pos.getZ() + this.within >> 4; z++) {
                if (!level.hasChunk(x, z)) { return false; }
            }
        }
        return true;
    }

    private boolean ring(Level level, BlockPos pos, BlockPos.MutableBlockPos at, int dx, int reach) {
        if (this.diamond) {
            int rest = reach - Math.abs(dx);
            return column(level, pos, at, dx, -rest) || rest != 0 && column(level, pos, at, dx, rest);
        }
        if (Math.abs(dx) != reach) { return column(level, pos, at, dx, -reach) || column(level, pos, at, dx, reach); }
        for (int dz = -reach; dz <= reach; dz++) {
            if (column(level, pos, at, dx, dz)) { return true; }
        }
        return false;
    }

    private boolean column(Level level, BlockPos pos, BlockPos.MutableBlockPos at, int dx, int dz) {
        for (int step = 0; step <= 2 * this.height; step++) {
            int dy = (step + 1) / 2 * (step % 2 == 1 ? -1 : 1);
            if (holds(level.getFluidState(at.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz)))) { return true; }
        }
        return false;
    }

    private boolean holds(FluidState state) { return (this.flowing || state.isSource()) && accepts(state); }
}
