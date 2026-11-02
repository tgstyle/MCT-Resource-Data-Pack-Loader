package mctmods.resourcedatapackloader.common.blocks.fluid;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidReactionDef;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserFluids;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.VanillaClients;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

public final class VanillaFluidRules {
    private static final ResourceLocation WATER_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "water");
    private static final ResourceLocation LAVA_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "lava");
    private static final List<FluidReactionDef> WATER = new ArrayList<>();
    private static final List<FluidReactionDef> LAVA = new ArrayList<>();
    private static final Map<BlockState, Boolean> TOUCHED = new ConcurrentHashMap<>();

    private VanillaFluidRules() {}

    public static boolean wanted() { return !WATER.isEmpty() || !LAVA.isEmpty(); }

    public static void load() {
        WATER.clear();
        LAVA.clear();
        if (Config.definitionsOff() || !Config.content.fluidRules()) { return; }
        Json.eachObject(PackManager.FLUID_RULES, "fluid rule", VanillaFluidRules::read);
    }

    public static void resolve() {
        TOUCHED.clear();
        WATER.removeIf(rule -> !FluidReactions.resolve(rule, "Rules on " + WATER_ID));
        LAVA.removeIf(rule -> !FluidReactions.resolve(rule, "Rules on " + LAVA_ID));
    }

    public static List<FluidReactionDef> rulesOf(Block block) {
        if (block == Blocks.WATER) { return WATER; }
        return block == Blocks.LAVA ? LAVA : Collections.emptyList();
    }

    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) { return; }
        BlockState state = event.getState();
        if (!rulesOf(state.getBlock()).isEmpty()) { FluidReactionQueue.watch(level, event.getPos(), 0, false); }
        if (!touched(state)) { return; }
        for (Direction side : event.getNotifiedSides()) {
            BlockPos beside = event.getPos().relative(side);
            if (level.isLoaded(beside) && !rulesOf(level.getBlockState(beside).getBlock()).isEmpty()) { FluidReactionQueue.watch(level, beside, 0, false); }
        }
    }

    public static void tagsUpdated() { TOUCHED.clear(); }

    private static boolean touched(BlockState state) { return TOUCHED.computeIfAbsent(state, key -> touches(WATER, key) || touches(LAVA, key)); }

    private static boolean touches(List<FluidReactionDef> rules, BlockState state) {
        for (FluidReactionDef rule : rules) {
            FluidTouch touch = rule.touch();
            if (touch != null && touch.matches(state)) { return true; }
        }
        return false;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        String named = Json.text(json, "fluid", "");
        ResourceLocation fluid = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        List<FluidReactionDef> into = WATER_ID.equals(fluid) ? WATER : LAVA_ID.equals(fluid) ? LAVA : null;
        if (into == null) {
            ContentLog.LOGGER.error("Fluid rule {} names the fluid '{}', but only minecraft:water and minecraft:lava take rules this way, so the file is skipped", key, named);
            return;
        }
        List<FluidReactionDef> rules = ContentParserFluids.reactions(key, json, true);
        for (FluidReactionDef rule : rules) {
            ResourceLocation sound = rule.sound.isEmpty() ? null : ResourceLocation.tryParse(rule.sound);
            if (sound != null && !"minecraft".equals(sound.getNamespace())) { VanillaClients.clientDrawn(key, "sound " + rule.sound); }
        }
        into.addAll(rules);
    }
}
