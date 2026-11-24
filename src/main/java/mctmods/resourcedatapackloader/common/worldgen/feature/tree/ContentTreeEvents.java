package mctmods.resourcedatapackloader.common.worldgen.feature.tree;

import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeGrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TreeRuleDef;
import mctmods.resourcedatapackloader.common.rules.ContentGrowSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentTreeRules;
import mctmods.resourcedatapackloader.common.util.world.PlacePositions;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

public final class ContentTreeEvents {
    private enum Outcome { NONE, USED, KEPT }

    private ContentTreeEvents() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ContentTreeStore::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentTreeStore::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(ContentTreeStore::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ContentTreeClock::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentTreeClock::onLevelTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ContentTreeEvents::onSaplingGrow);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ContentTreeEvents::onBonemeal);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentTreeEvents::onPlace);
        NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, _ -> ContentTreeStore.clear());
    }

    public static void onSaplingGrow(BlockGrowFeatureEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || event.isCanceled() || TreeRecorder.recording()) { return; }
        BlockState state = level.getBlockState(event.getPos());
        TreeGrowthDef growth = ContentTreeRules.growth(state);
        if (state.getBlock() instanceof ContentSaplingBlock || growth == null || !growth.growing()) { return; }
        event.setCanceled(true);
        ContentTreeGrowth.start(level, event.getPos(), state, ContentTreeRules.ruleId(state), growth, null);
    }

    public static void onBonemeal(BonemealEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        Outcome outcome = bonemeal(level, event.getPos(), event.getState(), event.getPlayer() == FakePlayerFactory.getMinecraft(level));
        if (outcome == Outcome.USED) { event.getStack().shrink(1); event.setSuccessful(true); }
        else if (outcome == Outcome.KEPT) { event.setCanceled(true); }
    }

    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        for (BlockPos pos : PlacePositions.of(event)) { placed(level, pos); }
    }

    public static boolean randomTick(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (!ContentTreeRules.any() || state.getBlock() instanceof ContentSaplingBlock) { return false; }
        TreeRuleDef rule = ContentTreeRules.rule(state);
        if (rule == null) { return false; }
        TreeGrowthDef growth = rule.growth();
        if (growth.chance() < 0.0 && growth.light() < 0 && !growth.timed() && growth.biomes().isEmpty() && !growth.needsSky() && !growth.needsWater()) { return false; }
        if (growth.timed()) {
            ContentTreeGrowth.plant(level, pos, state, rule.id());
            return true;
        }
        if (ContentGrowSwitch.skips(level, pos, state.getBlock(), random) || ContentTreeGrowth.conditions(level, pos, growth) != null) { return true; }
        if (!growth.decidesRandomTick()) { return false; }
        if (!growth.rolls(random)) { return true; }
        ContentTreeGrowth.drive(level, pos, state, random);
        return true;
    }

    public static boolean sneak(ServerLevel level, BlockPos pos, BlockState state, boolean bonemeal) {
        if (!bonemeal) { return randomTick(level, pos, state, level.getRandom()); }
        return bonemeal(level, pos, state, false) != Outcome.NONE;
    }

    private static Outcome bonemeal(ServerLevel level, BlockPos pos, BlockState state, boolean budget) {
        TreeEntry owner = ContentTreeStore.ownerOf(level, pos.asLong());
        Outcome outcome = owner == null ? Outcome.NONE : onTree(level, owner, pos, budget);
        return outcome != Outcome.NONE ? outcome : onSapling(level, pos, state, budget);
    }

    private static Outcome onTree(ServerLevel level, TreeEntry entry, BlockPos pos, boolean budget) {
        TreeGrowthDef growth = entry.phase == 1 ? ContentTreeRules.byId(entry.rule) : null;
        TreeShape shape = growth == null ? null : ContentTreeGrowth.shape(level, entry);
        if (shape == null || !shape.trunk().contains(pos.subtract(BlockPos.of(entry.pos)))) { return Outcome.NONE; }
        TreeGrowthDef.Bonemeal mode = growth.bonemeal() == TreeGrowthDef.Bonemeal.UNSET ? TreeGrowthDef.Bonemeal.STEP : growth.bonemeal();
        if (mode == TreeGrowthDef.Bonemeal.OFF) { return Outcome.KEPT; }
        if (mode == TreeGrowthDef.Bonemeal.TIME && growth.timed()) {
            ContentTreeClock.credit(level, entry, growth.bonemealTicks());
            return Outcome.USED;
        }
        if (kept(level, growth, () -> ContentTreeGrowth.conditions(level, entry, growth), budget)) { return Outcome.KEPT; }
        return ContentTreeGrowth.step(level, entry, growth, growth.timed()) == null ? Outcome.USED : Outcome.KEPT;
    }

    private static boolean kept(ServerLevel level, TreeGrowthDef growth, Supplier<String> conditions, boolean budget) {
        return growth.bonemealChecks() && conditions.get() != null || budget && !ContentTreeClock.spend(level);
    }

    private static Outcome onSapling(ServerLevel level, BlockPos pos, BlockState state, boolean budget) {
        boolean own = state.getBlock() instanceof ContentSaplingBlock;
        TreeGrowthDef growth = ContentTreeRules.growth(state);
        if (growth == null || !growth.growing() || !own && growth.bonemeal() == TreeGrowthDef.Bonemeal.UNSET) { return Outcome.NONE; }
        if (growth.bonemeal() == TreeGrowthDef.Bonemeal.OFF) { return Outcome.KEPT; }
        TreeEntry waiting = ContentTreeStore.get(level, pos.asLong());
        if (growth.bonemeal() == TreeGrowthDef.Bonemeal.TIME && waiting != null && waiting.phase == 0) {
            ContentTreeClock.credit(level, waiting, growth.bonemealTicks());
            return Outcome.USED;
        }
        if (kept(level, growth, () -> ContentTreeGrowth.conditions(level, pos, growth), budget)) { return Outcome.KEPT; }
        if (own) { return Outcome.NONE; }
        ContentTreeGrowth.drive(level, pos, state, level.getRandom());
        return Outcome.USED;
    }

    private static void placed(ServerLevel level, BlockPos pos) {
        TreeEntry owner = ContentTreeStore.ownerOf(level, pos.asLong());
        if (owner != null) {
            BlockPos offset = pos.subtract(BlockPos.of(owner.pos));
            int packed = TreeEntry.pack(offset.getX(), offset.getY(), offset.getZ());
            ContentTreeStore.removeOwned(level, owner, packed);
            owner.gone.add(packed);
            ContentTreeStore.dirty(level, owner);
        }
        BlockState state = level.getBlockState(pos);
        TreeGrowthDef growth = state.getBlock() instanceof ContentSaplingBlock ? null : ContentTreeRules.growth(state);
        if (growth != null && growth.growing() && growth.timed()) { ContentTreeGrowth.plant(level, pos, state, ContentTreeRules.ruleId(state)); }
    }
}
