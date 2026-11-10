package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaiverDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaterDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentGrowWater {
    public enum Status { NONE, FOUND, MISSING, WAIVED }

    private static final String PATH = "growth.water";
    private static final List<String> KEYS = Arrays.asList("within", "fluids", "kinds", "blocks", "except", "scale", "flowing", "height", "shape", "farmland", "rain", "bonemeal", "waiver", "breaks");
    private static final List<String> SHAPES = Arrays.asList("box", "diamond");
    private static final String WATER = "minecraft:water";

    private ContentGrowWater() {}

    public static boolean sapling(Block block) { return block instanceof BlockSapling || block instanceof ContentSaplingBlock; }

    public static int kindOf(Block block) { return sapling(block) ? GrowSwitchDef.SAPLINGS : GrowSwitchDef.CROPS; }

    private static boolean flag(String owner, JsonObject json, String key, boolean fallback) {
        JsonElement value = json.get(key);
        if (value == null) { return fallback; }
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean(); }
        ContentLog.LOGGER.error("{} gives {}.{} as {}, which is not true or false, ignoring it", owner, PATH, key, value);
        return fallback;
    }

    private static int whole(String owner, JsonObject json, String key, int fallback, int low, int high) {
        Float value = json.has(key) ? ContentGrowSwitch.within(owner, PATH + "." + key, json.get(key), true, low, high) : null;
        return value == null ? fallback : value.intValue();
    }

    private static List<String> fluids(String owner, JsonElement element) {
        List<String> found = new ArrayList<>();
        if (element.isJsonArray()) {
            for (JsonElement entry : element.getAsJsonArray()) {
                if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString() && !entry.getAsString().startsWith("#")) { found.add(entry.getAsString().trim().toLowerCase(Locale.ROOT)); }
                else { ContentLog.LOGGER.error("{} gives {}.fluids an entry {} that is not a fluid id, ignoring it", owner, PATH, entry); }
            }
        }
        else { ContentLog.LOGGER.error("{} gives {}.fluids that is not a list of fluid ids, ignoring it", owner, PATH); }
        if (found.isEmpty()) {
            ContentLog.LOGGER.error("{} gives {}.fluids with no usable fluid, using water", owner, PATH);
            found.add(WATER);
        }
        return found;
    }

    private static boolean diamond(String owner, JsonElement element) {
        int shape = element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() ? SHAPES.indexOf(element.getAsString()) : -1;
        if (shape < 0) { ContentLog.LOGGER.error("{} gives {}.shape as {}, which is not one of {}, ignoring it", owner, PATH, element, SHAPES); }
        return shape == 1;
    }

    @Nullable public static WaterDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives {} that is not an object of within, fluids, kinds, blocks, except, scale, flowing, height, shape, farmland, rain, bonemeal, waiver and breaks keys, ignoring it", owner, PATH);
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!KEYS.contains(entry.getKey())) { ContentLog.LOGGER.error("{} gives {}.{}, which is not a water key, ignoring it", owner, PATH, entry.getKey()); }
        }
        List<BlockMatch> blocks = new ArrayList<>();
        if (json.has("blocks")) { ContentGrowSwitch.blockList(owner, PATH + ".blocks", json.get("blocks"), blocks); }
        if (json.has("blocks") && blocks.isEmpty()) { ContentLog.LOGGER.error("{} gives {}.blocks with no usable blocks, using the default plants", owner, PATH); }
        List<BlockMatch> except = new ArrayList<>();
        if (json.has("except")) { ContentGrowSwitch.blockList(owner, PATH + ".except", json.get("except"), except); }
        Float scale = json.has("scale") ? ContentGrowSwitch.within(owner, PATH + ".scale", json.get("scale"), false, 0.0F, 1.0F) : null;
        return new WaterDef(whole(owner, json, "within", WaterDef.DEFAULT_WITHIN, 1, WaterDef.MAX_WITHIN), whole(owner, json, "height", WaterDef.DEFAULT_HEIGHT, 0, WaterDef.MAX_HEIGHT), scale == null ? 0.0F : scale,
            json.has("fluids") ? fluids(owner, json.get("fluids")) : Collections.singletonList(WATER), json.has("kinds") ? ContentGrowSwitch.kindList(owner, PATH + ".kinds", json.get("kinds")) : WaiverDef.bothKinds(), blocks, except,
            flag(owner, json, "flowing", true), json.has("shape") && diamond(owner, json.get("shape")), flag(owner, json, "farmland", false), flag(owner, json, "rain", false), flag(owner, json, "bonemeal", false), flag(owner, json, "waiver", false), flag(owner, json, "breaks", false));
    }

    private static boolean covers(WaterDef water, Block block, int kind, World world, BlockPos pos) {
        if (!water.covers(kind) || water.excepted(block, world, pos)) { return false; }
        if (water.hasBlocks()) { return water.listed(block, world, pos); }
        return kind == GrowSwitchDef.SAPLINGS ? sapling(block) : block instanceof BlockCrops || block instanceof BlockStem;
    }

    public static Status status(World world, BlockPos pos, Block block, int kind, GrowSwitchDef def) {
        WaterDef water = def.water();
        if (water == null || !covers(water, block, kind, world, pos)) { return Status.NONE; }
        if (water.rained(world, pos) || water.hydrated(world, pos)) { return Status.FOUND; }
        WaiverDef waiver = def.waiver();
        if (water.waiver && waiver != null && waiver.covers(kind) && waiver.found(world, pos)) { return Status.WAIVED; }
        return water.found(world, pos) ? Status.FOUND : Status.MISSING;
    }

    static float factor(World world, BlockPos pos, Block block, int kind, GrowSwitchDef def) {
        WaterDef water = def.water();
        return water != null && status(world, pos, block, kind, def) == Status.MISSING ? water.scale : 1.0F;
    }

    static boolean broke(GrowSwitchDef def, World world, BlockPos pos, Block block, int kind) {
        WaterDef water = def.water();
        if (water == null || !water.breaks || status(world, pos, block, kind, def) != Status.MISSING || world.getBlockState(pos).getBlock() != block) { return false; }
        world.destroyBlock(pos, true);
        return true;
    }

    @SubscribeEvent public static void onBonemeal(BonemealEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        GrowSwitchDef def = ContentEnvironments.growth(world, pos);
        WaterDef water = def.water();
        if (water == null || !water.bonemeal) { return; }
        Block block = event.getBlock().getBlock();
        if (status(world, pos, block, kindOf(block), def) == Status.MISSING) { event.setCanceled(true); }
    }
}
