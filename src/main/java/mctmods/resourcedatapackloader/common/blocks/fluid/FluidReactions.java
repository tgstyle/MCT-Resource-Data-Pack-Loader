package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidReactionDef;
import mctmods.resourcedatapackloader.common.pack.io.port.Ids;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.portways.PortwayFx;
import mctmods.resourcedatapackloader.common.util.entity.Functions;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public final class FluidReactions {
    public static final int COST_BLOCK = 1;
    public static final int COST_FUNCTION = 4;
    public static final int COST_EXPLOSION = 16;
    private static final int PLACE_FLAGS = 3;
    private static boolean firing;

    private FluidReactions() {}

    public static List<FluidReactionDef> rulesOf(IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockLiquid) { return VanillaFluidRules.rulesOf(block); }
        if (!(block instanceof ContentFluidBlock) || block.getRegistryName() == null) { return Collections.emptyList(); }
        FluidDef def = ContentRegistry.fluidDef(block.getRegistryName());
        return def == null ? Collections.emptyList() : def.behavior.reactions;
    }

    public static boolean resolve(FluidReactionDef rule, Object owner) {
        FluidTouch touch = FluidTouch.parse(owner, rule.touching);
        if (touch == null) { return false; }
        IBlockState becomes = named(rule.becomes, owner);
        IBlockState source = named(rule.source, owner);
        IBlockState flowing = named(rule.flowing, owner);
        if (becomes == null && !rule.becomes.isEmpty() || source == null && !rule.source.isEmpty() || flowing == null && !rule.flowing.isEmpty()) { return false; }
        rule.resolve(touch, becomes, source, flowing);
        return true;
    }

    public static boolean check(World world, BlockPos pos, IBlockState state, boolean intervalOnly) {
        if (world.isRemote || firing) { return false; }
        if (intervalOnly && FluidReactionQueue.watching(world, pos)) { return false; }
        for (FluidReactionDef rule : rulesOf(state)) {
            FluidTouch touch = rule.touch();
            if (touch == null || intervalOnly && rule.interval <= 0) { continue; }
            BlockPos touched = contact(world, pos, rule, touch);
            if (touched == null) { continue; }
            if (rule.interval > 0) { FluidReactionQueue.watch(world, pos, rule.interval, true); }
            if (rule.chance < 1.0F && world.rand.nextFloat() >= rule.chance) { continue; }
            fire(world, pos, state, touched, rule);
            return world.getBlockState(pos) != state;
        }
        return false;
    }

    @Nullable private static BlockPos contact(World world, BlockPos pos, FluidReactionDef rule, FluidTouch touch) {
        for (EnumFacing face : rule.faces) {
            BlockPos beside = pos.offset(face);
            if (world.isBlockLoaded(beside) && touch.matches(world.getBlockState(beside))) { return beside; }
        }
        return null;
    }

    private static void fire(World world, BlockPos pos, IBlockState state, BlockPos touched, FluidReactionDef rule) {
        IBlockState self = state.getBlock().getMetaFromState(state) == 0 ? rule.sourceState() : rule.flowingState();
        int cost = (rule.becomesState() == null ? 0 : COST_BLOCK) + (self == null ? 0 : COST_BLOCK) + (rule.function.isEmpty() ? 0 : COST_FUNCTION) + (rule.explosion == null ? 0 : COST_EXPLOSION);
        if (!FluidReactionQueue.spend(world, pos, cost)) {
            FluidReactionQueue.watch(world, pos, FluidReactionQueue.RECHECK, false);
            return;
        }
        firing = true;
        try {
            effects(world, pos, rule);
            if (rule.explosion != null) {
                BlockPos at = rule.explosion.atSelf ? pos : touched;
                world.newExplosion(null, at.getX() + 0.5D, at.getY() + 0.5D, at.getZ() + 0.5D, rule.explosion.power, rule.explosion.fire, rule.explosion.grief);
            }
            if (!rule.function.isEmpty()) { Functions.runAt(world, pos, rule.function, "A fluid reaction at " + state.getBlock().getRegistryName()); }
            if (rule.becomesState() != null) { place(world, touched, pos, rule.becomesState(), rule.drops); }
            if (self != null) { place(world, pos, pos, self, false); }
        }
        finally { firing = false; }
    }

    private static void effects(World world, BlockPos pos, FluidReactionDef rule) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        PortwayFx.play(world, x, y, z, rule.sound);
        String mapped = rule.particles.isEmpty() ? null : Ids.particle(rule.particles);
        PortwayFx.particles(world, x, y, z, mapped == null ? rule.particles : mapped);
    }

    private static void place(World world, BlockPos at, BlockPos liquid, IBlockState wanted, boolean drops) {
        IBlockState current = world.getBlockState(at);
        IBlockState placed = ForgeEventFactory.fireFluidPlaceBlockEvent(world, at, liquid, wanted);
        if (placed == current) { return; }
        if (drops) { current.getBlock().dropBlockAsItem(world, at, current, 0); }
        world.setBlockState(at, placed, PLACE_FLAGS);
    }

    @Nullable private static IBlockState named(String name, Object owner) { return name.isEmpty() ? null : ContentStates.parse(name, owner); }
}
