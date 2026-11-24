package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeRuleDef;
import mctmods.resourcedatapackloader.common.rules.ContentGrowSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentTreeRules;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentTreeEvents {
    private enum Outcome { NONE, USED, KEPT }

    private ContentTreeEvents() {}

    @SubscribeEvent(priority = EventPriority.LOW) public static void onSaplingGrow(SaplingGrowTreeEvent event) {
        if (!(event.getWorld() instanceof WorldServer) || event.getResult() == Event.Result.DENY || TreeRecorder.recording()) { return; }
        WorldServer world = (WorldServer) event.getWorld();
        IBlockState state = world.getBlockState(event.getPos());
        TreeGrowthDef growth = ContentTreeRules.growing(state);
        if (state.getBlock() instanceof ContentSaplingBlock || growth == null) { return; }
        event.setResult(Event.Result.DENY);
        ContentTreeGrowth.start(world, event.getPos(), state, ContentTreeRules.ruleId(state), growth, null);
    }

    @SubscribeEvent(priority = EventPriority.LOW) public static void onBonemeal(BonemealEvent event) {
        if (!(event.getWorld() instanceof WorldServer)) { return; }
        Outcome outcome = bonemeal((WorldServer) event.getWorld(), event.getPos(), event.getBlock(), event.getEntityPlayer() == null);
        if (outcome == Outcome.USED) { event.setResult(Event.Result.ALLOW); }
        else if (outcome == Outcome.KEPT) { event.setCanceled(true); }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getWorld() instanceof WorldServer)) { return; }
        WorldServer world = (WorldServer) event.getWorld();
        for (BlockPos pos : PlacePositions.of(event)) { placed(world, pos); }
    }

    public static boolean randomTick(WorldServer world, BlockPos pos, IBlockState state, Random rand) {
        if (!ContentTreeRules.any() || state.getBlock() instanceof ContentSaplingBlock) { return false; }
        TreeRuleDef rule = ContentTreeRules.rule(state);
        if (rule == null) { return false; }
        TreeGrowthDef growth = rule.growth;
        if (growth.chance < 0.0 && growth.light < 0 && !growth.timed() && growth.biomes.isEmpty() && !growth.needsSky && !growth.needsWater) { return false; }
        if (growth.timed()) {
            ContentTreeGrowth.plant(world, pos, state, rule.id);
            return true;
        }
        if (ContentGrowSwitch.skips(world, pos, state.getBlock(), rand) || ContentTreeGrowth.conditions(world, pos, growth) != null) { return true; }
        if (!growth.decidesRandomTick()) { return false; }
        if (!growth.rolls(rand)) { return true; }
        ContentTreeGrowth.drive(world, pos, state, rand);
        return true;
    }

    public static boolean sneak(WorldServer world, BlockPos pos, IBlockState state, boolean bonemeal) {
        if (!bonemeal) { return randomTick(world, pos, state, world.rand); }
        return bonemeal(world, pos, state, false) != Outcome.NONE;
    }

    private static Outcome bonemeal(WorldServer world, BlockPos pos, IBlockState state, boolean budget) {
        TreeEntry owner = ContentTreeStore.ownerOf(world, pos.toLong());
        Outcome outcome = owner == null ? Outcome.NONE : onTree(world, owner, pos, budget);
        return outcome != Outcome.NONE ? outcome : onSapling(world, pos, state, budget);
    }

    private static Outcome onTree(WorldServer world, TreeEntry entry, BlockPos pos, boolean budget) {
        TreeGrowthDef growth = entry.phase == TreeEntry.PHASE_GROWING ? ContentTreeRules.byId(entry.rule) : null;
        TreeShape shape = growth == null ? null : ContentTreeGrowth.shape(world, entry);
        if (shape == null || !shape.trunk().contains(pos.subtract(BlockPos.fromLong(entry.pos)))) { return Outcome.NONE; }
        TreeGrowthDef.Bonemeal mode = growth.bonemeal == TreeGrowthDef.Bonemeal.UNSET ? TreeGrowthDef.Bonemeal.STEP : growth.bonemeal;
        if (mode == TreeGrowthDef.Bonemeal.OFF) { return Outcome.KEPT; }
        if (mode == TreeGrowthDef.Bonemeal.TIME && growth.timed()) {
            ContentTreeClock.credit(world, entry, growth.bonemealTicks);
            return Outcome.USED;
        }
        if (kept(world, growth, () -> ContentTreeGrowth.conditions(world, entry, growth), budget)) { return Outcome.KEPT; }
        return ContentTreeGrowth.step(world, entry, growth, growth.timed()) == null ? Outcome.USED : Outcome.KEPT;
    }

    private static boolean kept(WorldServer world, TreeGrowthDef growth, Supplier<String> conditions, boolean budget) {
        return growth.bonemealChecks && conditions.get() != null || budget && !ContentTreeClock.spend(world);
    }

    private static Outcome onSapling(WorldServer world, BlockPos pos, IBlockState state, boolean budget) {
        boolean own = state.getBlock() instanceof ContentSaplingBlock;
        TreeGrowthDef growth = ContentTreeRules.growing(state);
        if (growth == null || !own && growth.bonemeal == TreeGrowthDef.Bonemeal.UNSET) { return Outcome.NONE; }
        if (growth.bonemeal == TreeGrowthDef.Bonemeal.OFF) { return Outcome.KEPT; }
        TreeEntry waiting = ContentTreeStore.get(world, pos.toLong());
        if (growth.bonemeal == TreeGrowthDef.Bonemeal.TIME && waiting != null && waiting.phase == TreeEntry.PHASE_SAPLING) {
            ContentTreeClock.credit(world, waiting, growth.bonemealTicks);
            return Outcome.USED;
        }
        if (kept(world, growth, () -> ContentTreeGrowth.conditions(world, pos, growth), budget)) { return Outcome.KEPT; }
        if (own) { return Outcome.NONE; }
        ContentTreeGrowth.drive(world, pos, state, world.rand);
        return Outcome.USED;
    }

    private static void placed(WorldServer world, BlockPos pos) {
        TreeEntry owner = ContentTreeStore.ownerOf(world, pos.toLong());
        if (owner != null) {
            BlockPos offset = pos.subtract(BlockPos.fromLong(owner.pos));
            int packed = TreeEntry.pack(offset);
            ContentTreeStore.removeOwned(world, owner, packed);
            owner.gone.add(packed);
            ContentTreeStore.dirty(world, owner);
        }
        IBlockState state = world.getBlockState(pos);
        TreeGrowthDef growth = state.getBlock() instanceof ContentSaplingBlock ? null : ContentTreeRules.growing(state);
        if (growth != null && growth.timed()) { ContentTreeGrowth.plant(world, pos, state, ContentTreeRules.ruleId(state)); }
    }
}
