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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class VanillaFluidRules {
    private static final ResourceLocation WATER_ID = new ResourceLocation("minecraft", "water");
    private static final ResourceLocation LAVA_ID = new ResourceLocation("minecraft", "lava");
    private static final List<FluidReactionDef> WATER = new ArrayList<>();
    private static final List<FluidReactionDef> LAVA = new ArrayList<>();
    private static final Map<IBlockState, Boolean> TOUCHED = new ConcurrentHashMap<>();

    private VanillaFluidRules() {}

    public static boolean wanted() { return !WATER.isEmpty() || !LAVA.isEmpty(); }

    public static void load() {
        WATER.clear();
        LAVA.clear();
        if (!Config.content.load || !Config.content.fluidRules) { return; }
        Json.eachObject(PackManager.FLUID_RULES, "fluid rule", VanillaFluidRules::read);
    }

    public static void resolve() {
        TOUCHED.clear();
        WATER.removeIf(rule -> !FluidReactions.resolve(rule, "Rules on " + WATER_ID));
        LAVA.removeIf(rule -> !FluidReactions.resolve(rule, "Rules on " + LAVA_ID));
    }

    public static List<FluidReactionDef> rulesOf(Block block) {
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) { return WATER; }
        return block == Blocks.LAVA || block == Blocks.FLOWING_LAVA ? LAVA : Collections.emptyList();
    }

    @SubscribeEvent public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        World world = event.getWorld();
        if (world.isRemote) { return; }
        IBlockState state = event.getState();
        if (!rulesOf(state.getBlock()).isEmpty()) { FluidReactionQueue.watch(world, event.getPos(), 0, false); }
        if (!touched(state)) { return; }
        for (EnumFacing side : event.getNotifiedSides()) {
            BlockPos beside = event.getPos().offset(side);
            if (world.isBlockLoaded(beside) && !rulesOf(world.getBlockState(beside).getBlock()).isEmpty()) { FluidReactionQueue.watch(world, beside, 0, false); }
        }
    }

    private static boolean touched(IBlockState state) { return TOUCHED.computeIfAbsent(state, key -> touches(WATER, key) || touches(LAVA, key)); }

    private static boolean touches(List<FluidReactionDef> rules, IBlockState state) {
        for (FluidReactionDef rule : rules) {
            if (rule.touch() != null && rule.touch().matches(state)) { return true; }
        }
        return false;
    }

    private static void read(ResourceLocation key, JsonObject json) {
        String named = Json.text(json, "fluid", "");
        ResourceLocation fluid = named.isEmpty() ? null : new ResourceLocation(named);
        List<FluidReactionDef> into = WATER_ID.equals(fluid) ? WATER : LAVA_ID.equals(fluid) ? LAVA : null;
        if (into == null) {
            ContentLog.LOGGER.error("Fluid rule {} names the fluid '{}', but only minecraft:water and minecraft:lava take rules this way, so the file is skipped", key, named);
            return;
        }
        List<FluidReactionDef> rules = ContentParserFluids.reactions(key, json, true);
        for (FluidReactionDef rule : rules) {
            if (!rule.sound.isEmpty() && !"minecraft".equals(new ResourceLocation(rule.sound).getNamespace())) { VanillaClients.clientDrawn(key, "sound " + rule.sound); }
        }
        into.addAll(rules);
    }
}
