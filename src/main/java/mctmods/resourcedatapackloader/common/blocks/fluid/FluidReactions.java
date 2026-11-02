package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidReactionDef;
import mctmods.resourcedatapackloader.common.portways.PortwayFx;
import mctmods.resourcedatapackloader.common.util.entity.Functions;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.ForgeEventFactory;

public final class FluidReactions {
    public static final int COST_BLOCK = 1;
    public static final int COST_FUNCTION = 4;
    public static final int COST_EXPLOSION = 16;
    private static final Map<Fluid, List<FluidReactionDef>> PACK = new IdentityHashMap<>();
    private static boolean firing;

    private FluidReactions() {}

    public static List<FluidReactionDef> rulesOf(BlockState state) {
        if (!(state.getBlock() instanceof LiquidBlock liquid)) { return Collections.emptyList(); }
        Fluid fluid = liquid.getFluid();
        if (PACK.isEmpty()) {
            for (ContentFluids.Made made : ContentFluids.made()) {
                FluidDef def = made.def;
                PACK.put(made.still, def.behavior().reactions());
                PACK.put(made.flowing, def.behavior().reactions());
            }
        }
        List<FluidReactionDef> pack = PACK.get(fluid);
        return pack != null ? pack : VanillaFluidRules.rulesOf(liquid);
    }

    public static boolean resolve(FluidReactionDef rule, Object owner) {
        FluidTouch touch = FluidTouch.parse(owner, rule.touching);
        if (touch == null) { return false; }
        BlockState becomes = named(rule.becomes, owner);
        BlockState source = named(rule.source, owner);
        BlockState flowing = named(rule.flowing, owner);
        if (becomes == null && !rule.becomes.isEmpty() || source == null && !rule.source.isEmpty() || flowing == null && !rule.flowing.isEmpty()) { return false; }
        rule.resolve(touch, becomes, source, flowing);
        return true;
    }

    public static boolean check(Level world, BlockPos pos, BlockState state, boolean intervalOnly) {
        if (!(world instanceof ServerLevel level) || firing) { return false; }
        if (intervalOnly && FluidReactionQueue.watching(level, pos)) { return false; }
        for (FluidReactionDef rule : rulesOf(state)) {
            FluidTouch touch = rule.touch();
            if (touch == null || intervalOnly && rule.interval <= 0) { continue; }
            BlockPos touched = contact(level, pos, rule, touch);
            if (touched == null) { continue; }
            if (rule.interval > 0) { FluidReactionQueue.watch(level, pos, rule.interval, true); }
            if (rule.chance < 1.0F && level.random.nextFloat() >= rule.chance) { continue; }
            fire(level, pos, state, touched, rule);
            return level.getBlockState(pos) != state;
        }
        return false;
    }

    @Nullable private static BlockPos contact(ServerLevel level, BlockPos pos, FluidReactionDef rule, FluidTouch touch) {
        for (Direction face : rule.faces) {
            BlockPos beside = pos.relative(face);
            if (level.isLoaded(beside) && touch.matches(level.getBlockState(beside))) { return beside; }
        }
        return null;
    }

    private static void fire(ServerLevel level, BlockPos pos, BlockState state, BlockPos touched, FluidReactionDef rule) {
        BlockState self = state.getFluidState().isSource() ? rule.sourceState() : rule.flowingState();
        int cost = (rule.becomesState() == null ? 0 : COST_BLOCK) + (self == null ? 0 : COST_BLOCK) + (rule.function.isEmpty() ? 0 : COST_FUNCTION) + (rule.explosion == null ? 0 : COST_EXPLOSION);
        if (!FluidReactionQueue.spend(level, pos, cost)) {
            FluidReactionQueue.watch(level, pos, FluidReactionQueue.RECHECK, false);
            return;
        }
        firing = true;
        try {
            effects(level, pos, rule);
            FluidReactionDef.Explosion blast = rule.explosion;
            if (blast != null) {
                BlockPos at = blast.atSelf() ? pos : touched;
                level.explode(null, at.getX() + 0.5D, at.getY() + 0.5D, at.getZ() + 0.5D, blast.power(), blast.fire(), blast.grief() ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
            }
            if (!rule.function.isEmpty()) { Functions.runAt(level, pos, rule.function, "A fluid reaction at " + state.getBlock()); }
            BlockState becomes = rule.becomesState();
            if (becomes != null) { place(level, touched, pos, becomes, rule.drops); }
            if (self != null) { place(level, pos, pos, self, false); }
        }
        finally { firing = false; }
    }

    private static void effects(ServerLevel level, BlockPos pos, FluidReactionDef rule) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        PortwayFx.play(level, x, y, z, rule.sound);
        PortwayFx.particles(level, x, y, z, rule.particles);
    }

    private static void place(ServerLevel level, BlockPos at, BlockPos liquid, BlockState wanted, boolean drops) {
        BlockState current = level.getBlockState(at);
        BlockState placed = ForgeEventFactory.fireFluidPlaceBlockEvent(level, at, liquid, wanted);
        if (placed == current) { return; }
        if (drops) { Block.dropResources(current, level, at); }
        level.setBlock(at, placed, Block.UPDATE_ALL);
    }

    @Nullable private static BlockState named(String name, Object owner) { return name.isEmpty() ? null : ContentStates.parse(name, owner); }
}
