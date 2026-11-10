package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaiverDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaterDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

public final class ContentGrowWater {
    public enum Status { NONE, FOUND, MISSING, WAIVED }

    private static final List<String> KEYS = List.of("within", "fluids", "kinds", "blocks", "except", "scale", "flowing", "height", "shape", "farmland", "rain", "bonemeal", "waiver", "breaks");

    private ContentGrowWater() {}

    private static List<String> strings(String owner, JsonElement element) {
        List<String> found = new ArrayList<>();
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives growth.water.fluids that is not a list of fluid ids or #tags, ignoring it", owner);
            return found;
        }
        for (JsonElement entry : element.getAsJsonArray()) {
            if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) { found.add(entry.getAsString()); }
            else { ContentLog.LOGGER.error("{} gives growth.water.fluids an entry {} that is not text, ignoring it", owner, entry); }
        }
        return found;
    }

    private static List<BlockMatch> blocks(String owner, String key, JsonElement element) {
        List<BlockMatch> found = new ArrayList<>();
        ContentGrowSwitch.blockList(owner, "growth.water." + key, element, found);
        return found;
    }

    private static void fluids(String owner, JsonElement element, List<ResourceLocation> fluids, List<TagKey<Fluid>> tags) {
        for (String text : strings(owner, element)) {
            String name = text.trim();
            boolean tag = name.startsWith("#");
            ResourceLocation id = name.length() > (tag ? 1 : 0) ? ResourceLocation.tryParse(Settings.lower(name).substring(tag ? 1 : 0)) : null;
            if (id == null) { ContentLog.LOGGER.error("{} gives growth.water.fluids the entry '{}', which is not a fluid id or #tag, ignoring it", owner, name); }
            else if (tag) { tags.add(TagKey.create(Registries.FLUID, id)); }
            else { fluids.add(id); }
        }
        if (fluids.isEmpty() && tags.isEmpty()) {
            ContentLog.LOGGER.error("{} gives growth.water.fluids with no usable fluid, using minecraft:water", owner);
            fluids.add(WaterDef.WATER);
        }
    }

    private static int height(String owner, JsonElement element) {
        float number = ContentSpawnSwitch.amount(owner, "growth.water.height", element);
        if (number < 0.0F) { return WaterDef.DEFAULT_HEIGHT; }
        if (number != Math.floor(number) || number > WaterDef.MAX_HEIGHT) {
            ContentLog.LOGGER.error("{} gives growth.water.height as {}, which is not a whole number from 0 to {}, ignoring it", owner, number, WaterDef.MAX_HEIGHT);
            return WaterDef.DEFAULT_HEIGHT;
        }
        return (int) number;
    }

    private static boolean flag(String owner, String key, JsonElement element, boolean fallback) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) { return element.getAsBoolean(); }
        ContentLog.LOGGER.error("{} gives growth.water.{} as {}, which is not true or false, ignoring it", owner, key, element);
        return fallback;
    }

    @Nullable public static WaterDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth.water that is not an object of within, fluids, kinds, blocks, except, scale, flowing, height, shape, farmland, rain, bonemeal, waiver and breaks keys, ignoring it", owner);
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!KEYS.contains(entry.getKey())) { ContentLog.LOGGER.error("{} gives growth.water.{}, which is not a water key, ignoring it", owner, entry.getKey()); }
        }
        List<ResourceLocation> fluids = new ArrayList<>();
        List<TagKey<Fluid>> tags = new ArrayList<>();
        if (json.has("fluids")) { fluids(owner, json.get("fluids"), fluids, tags); }
        else { fluids.add(WaterDef.WATER); }
        List<BlockMatch> blocks = json.has("blocks") ? blocks(owner, "blocks", json.get("blocks")) : List.of();
        if (json.has("blocks") && blocks.isEmpty()) { ContentLog.LOGGER.error("{} gives growth.water.blocks with no usable blocks, using the default plants", owner); }
        float scale = json.has("scale") ? ContentGrowSwitch.within(owner, "growth.water.scale", json.get("scale"), false, 1.0F) : -1.0F;
        float within = json.has("within") ? ContentGrowSwitch.within(owner, "growth.water.within", json.get("within"), true, WaterDef.MAX_WITHIN) : -1.0F;
        boolean diamond = false;
        if (json.has("shape")) {
            String shape = json.get("shape").isJsonPrimitive() ? json.get("shape").getAsString() : "";
            diamond = "diamond".equals(shape);
            if (!diamond && !"box".equals(shape)) { ContentLog.LOGGER.error("{} gives growth.water.shape as {}, which is not box or diamond, ignoring it", owner, json.get("shape")); }
        }
        boolean[] flags = {flag(owner, "farmland", json), flag(owner, "rain", json), flag(owner, "bonemeal", json), flag(owner, "waiver", json), flag(owner, "breaks", json)};
        return new WaterDef(within < 0.0F ? WaterDef.DEFAULT_WITHIN : (int) within, fluids, tags, json.has("kinds") ? ContentGrowSwitch.kindList(owner, "growth.water.kinds", json.get("kinds")) : WaiverDef.bothKinds(), blocks, json.has("except") ? blocks(owner, "except", json.get("except")) : List.of(), Math.max(scale, 0.0F), !json.has("flowing") || flag(owner, "flowing", json.get("flowing"), true), json.has("height") ? height(owner, json.get("height")) : WaterDef.DEFAULT_HEIGHT, diamond, flags);
    }

    private static boolean flag(String owner, String key, JsonObject json) { return json.has(key) && flag(owner, key, json.get(key), false); }

    public static boolean sapling(Block block) { return block instanceof SaplingBlock || block instanceof ContentSaplingBlock; }

    private static boolean standard(Block block, int kind) { return kind == GrowSwitchDef.SAPLINGS ? sapling(block) : block instanceof CropBlock || block instanceof StemBlock; }

    public static int kindOf(Block block) { return sapling(block) ? GrowSwitchDef.SAPLINGS : GrowSwitchDef.CROPS; }

    public static Status status(Level level, BlockPos pos, Block block, int kind, GrowSwitchDef def) {
        WaterDef water = def.water();
        if (water == null || !water.covers(kind, block, standard(block, kind))) { return Status.NONE; }
        if (water.rain && level.isRainingAt(pos.above())) { return Status.FOUND; }
        if (water.farmland && water.listsWater()) {
            BlockState soil = level.getBlockState(pos.below());
            if (soil.getBlock() instanceof FarmBlock && soil.getValue(FarmBlock.MOISTURE) > 0) { return Status.FOUND; }
        }
        WaiverDef waiver = def.waiver();
        if (water.waiver && waiver != null && waiver.covers(kind) && waiver.found(level, pos)) { return Status.WAIVED; }
        return water.found(level, pos) ? Status.FOUND : Status.MISSING;
    }

    static boolean broke(GrowSwitchDef def, ServerLevel level, BlockPos pos, Block block, int kind) {
        WaterDef water = def.water();
        if (water == null || !water.breaks || status(level, pos, block, kind, def) != Status.MISSING || !level.getBlockState(pos).is(block)) { return false; }
        level.destroyBlock(pos, true);
        return true;
    }

    public static void onBonemeal(BonemealEvent event) {
        Level level = event.getLevel();
        GrowSwitchDef def = ContentEnvironments.growth(level, event.getPos());
        WaterDef water = def.water();
        if (water == null || !water.bonemeal) { return; }
        Block block = event.getState().getBlock();
        if (status(level, event.getPos(), block, kindOf(block), def) == Status.MISSING) { event.setCanceled(true); }
    }
}
