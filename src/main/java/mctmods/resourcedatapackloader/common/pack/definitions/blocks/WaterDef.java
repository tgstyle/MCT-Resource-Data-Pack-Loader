package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.blocks.fluid.FluidTouch;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

public final class WaterDef {
    public static final int DEFAULT_WITHIN = 4;
    public static final int MAX_WITHIN = 16;
    public static final int DEFAULT_HEIGHT = 1;
    public static final int MAX_HEIGHT = 4;
    private static final String FLOWING = "flowing_";
    private static final int WORLD_TOP = 255;
    private final int within;
    private final int height;
    public final float scale;
    private final List<String> fluids;
    private final boolean[] kinds;
    private final List<BlockMatch> blocks;
    private final List<BlockMatch> except;
    private final boolean flowing;
    private final boolean diamond;
    private final boolean farmland;
    private final boolean rain;
    public final boolean bonemeal;
    public final boolean waiver;
    public final boolean breaks;
    @Nullable private volatile Set<Fluid> resolved;

    public WaterDef(int within, int height, float scale, List<String> fluids, boolean[] kinds, List<BlockMatch> blocks, List<BlockMatch> except, boolean flowing, boolean diamond, boolean farmland, boolean rain, boolean bonemeal, boolean waiver, boolean breaks) {
        this.within = within;
        this.height = height;
        this.scale = scale;
        this.fluids = fluids;
        this.kinds = kinds;
        this.blocks = blocks;
        this.except = except;
        this.flowing = flowing;
        this.diamond = diamond;
        this.farmland = farmland;
        this.rain = rain;
        this.bonemeal = bonemeal;
        this.waiver = waiver;
        this.breaks = breaks;
    }

    public int within() { return this.within; }

    public boolean covers(int kind) { return this.kinds[kind]; }

    public boolean hasBlocks() { return !this.blocks.isEmpty(); }

    public boolean listed(Block block, World world, BlockPos pos) { return anyMatch(this.blocks, block, world, pos); }

    public boolean excepted(Block block, World world, BlockPos pos) { return anyMatch(this.except, block, world, pos); }

    private static boolean anyMatch(List<BlockMatch> matches, Block block, World world, BlockPos pos) {
        for (BlockMatch match : matches) {
            if (match.matches(block, world, pos)) { return true; }
        }
        return false;
    }

    public boolean rained(World world, BlockPos pos) { return this.rain && world.isRainingAt(pos.up()); }

    public boolean hydrated(World world, BlockPos pos) {
        if (!this.farmland || !fluids().contains(FluidRegistry.WATER)) { return false; }
        IBlockState soil = world.getBlockState(pos.down());
        return soil.getBlock() instanceof BlockFarmland && soil.getValue(BlockFarmland.MOISTURE) > 0;
    }

    private Set<Fluid> fluids() {
        Set<Fluid> found = this.resolved;
        if (found == null) {
            found = resolve();
            this.resolved = found;
        }
        return found;
    }

    private Set<Fluid> resolve() {
        Set<Fluid> found = new HashSet<>();
        for (String id : this.fluids) {
            String name = id.substring(id.indexOf(':') + 1);
            Fluid fluid = FluidRegistry.getFluid(name.startsWith(FLOWING) ? name.substring(FLOWING.length()) : name);
            if (fluid == null) { ContentLog.LOGGER.error("growth.water.fluids names {}, which is not a registered fluid, ignoring it", id); }
            else { found.add(fluid); }
        }
        if (found.isEmpty()) {
            ContentLog.LOGGER.error("growth.water.fluids has no registered fluid, using water");
            return Collections.singleton(FluidRegistry.WATER);
        }
        return found;
    }

    public boolean found(World world, BlockPos pos) {
        int low = Math.max(pos.getY() - this.height, 0);
        int high = Math.min(pos.getY() + this.height, WORLD_TOP);
        if (!world.isAreaLoaded(new BlockPos(pos.getX() - this.within, low, pos.getZ() - this.within), new BlockPos(pos.getX() + this.within, high, pos.getZ() + this.within))) { return true; }
        Set<Fluid> wanted = fluids();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int ring = 0; ring <= this.within; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                int reach = this.diamond ? ring - Math.abs(dx) : ring;
                int step = !this.diamond && Math.abs(dx) == ring ? 1 : Math.max(reach * 2, 1);
                for (int dz = -reach; dz <= reach; dz += step) {
                    if (column(world, at, pos, dx, dz, wanted)) { return true; }
                    if (reach == 0) { break; }
                }
            }
        }
        return false;
    }

    private boolean column(World world, BlockPos.MutableBlockPos at, BlockPos pos, int dx, int dz, Set<Fluid> wanted) {
        for (int layer = 0; layer <= this.height * 2; layer++) {
            int dy = (layer + 1) / 2 * (layer % 2 == 1 ? -1 : 1);
            int y = pos.getY() + dy;
            if (y < 0 || y > WORLD_TOP) { continue; }
            at.setPos(pos.getX() + dx, y, pos.getZ() + dz);
            if (fluidAt(world, at, wanted)) { return true; }
        }
        return false;
    }

    private boolean fluidAt(World world, BlockPos at, Set<Fluid> wanted) {
        IBlockState state = world.getBlockState(at);
        Block block = state.getBlock();
        Fluid fluid = FluidTouch.fluidOf(block);
        if (fluid == null || !wanted.contains(fluid)) { return false; }
        return this.flowing || source(world, at, state, block);
    }

    private static boolean source(World world, BlockPos at, IBlockState state, Block block) {
        if (block instanceof BlockLiquid) { return state.getValue(BlockLiquid.LEVEL) == 0; }
        if (block instanceof BlockFluidClassic) { return ((BlockFluidClassic) block).isSourceBlock(world, at); }
        return ((IFluidBlock) block).getFilledPercentage(world, at) >= 1.0F;
    }
}
