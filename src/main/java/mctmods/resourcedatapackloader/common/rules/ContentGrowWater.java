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
import net.minecraftforge.event.entity.player.BonemealEvent;

public final class ContentGrowWater {
    public enum Status { NONE, FOUND, MISSING, WAIVED }

    private static final List<String> KEYS = List.of("within", "fluids", "kinds", "blocks", "except", "scale", "flowing", "height", "shape", "farmland", "rain", "bonemeal", "waiver", "breaks");
    private static final List<String> SHAPES = List.of("box", "diamond");

    private ContentGrowWater() {}

    public static boolean sapling(Block block) { return block instanceof SaplingBlock || block instanceof ContentSaplingBlock; }

    public static int kindOf(Block block) { return sapling(block) ? GrowSwitchDef.SAPLINGS : GrowSwitchDef.CROPS; }

    private static boolean flag(String owner, JsonObject json, String key, boolean fallback) {
        JsonElement value = json.get(key);
        if (value == null) { return fallback; }
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean(); }
        ContentLog.LOGGER.error("{} gives growth.water.{} as {}, which is not true or false, ignoring it", owner, key, value);
        return fallback;
    }

    private static int whole(String owner, JsonObject json, String key, int fallback, int low, int high) {
        Float number = json.has(key) ? ContentGrowSwitch.within(owner, "growth.water." + key, json.get(key), true, low, high) : null;
        return number == null ? fallback : number.intValue();
    }

    private static void fluids(String owner, JsonElement element, List<ResourceLocation> ids, List<TagKey<Fluid>> tags) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives growth.water.fluids that is not a list of fluid ids or #tags, ignoring it", owner);
            return;
        }
        for (JsonElement entry : element.getAsJsonArray()) {
            String name = entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString() ? entry.getAsString().trim() : "";
            boolean tag = name.startsWith("#");
            ResourceLocation id = name.length() > 1 || !tag && !name.isEmpty() ? ResourceLocation.tryParse(tag ? name.substring(1) : name) : null;
            if (id == null) { ContentLog.LOGGER.error("{} gives growth.water.fluids an entry {} that is not a fluid id or #tag, ignoring it", owner, entry); }
            else if (tag) { tags.add(TagKey.create(Registries.FLUID, id)); }
            else { ids.add(id); }
        }
    }

    private static String shape(String owner, JsonObject json) {
        JsonElement value = json.get("shape");
        if (value == null) { return SHAPES.get(0); }
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() && SHAPES.contains(value.getAsString())) { return value.getAsString(); }
        ContentLog.LOGGER.error("{} gives growth.water.shape as {}, which is not one of {}, ignoring it", owner, value, SHAPES);
        return SHAPES.get(0);
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
        List<ResourceLocation> ids = new ArrayList<>();
        List<TagKey<Fluid>> tags = new ArrayList<>();
        if (json.has("fluids")) { fluids(owner, json.get("fluids"), ids, tags); }
        if (ids.isEmpty() && tags.isEmpty()) {
            if (json.has("fluids")) { ContentLog.LOGGER.error("{} gives growth.water.fluids with no usable fluid, using water", owner); }
            ids.add(WaterDef.water());
        }
        boolean[] kinds = json.has("kinds") ? ContentGrowSwitch.kindList(owner, "growth.water.kinds", json.get("kinds")) : WaiverDef.bothKinds();
        List<BlockMatch> blocks = new ArrayList<>();
        if (json.has("blocks")) { ContentGrowSwitch.blockList(owner, "growth.water.blocks", json.get("blocks"), blocks); }
        if (json.has("blocks") && blocks.isEmpty()) { ContentLog.LOGGER.error("{} gives growth.water.blocks with no usable blocks, using the default plants", owner); }
        List<BlockMatch> except = new ArrayList<>();
        if (json.has("except")) { ContentGrowSwitch.blockList(owner, "growth.water.except", json.get("except"), except); }
        Float scale = json.has("scale") ? ContentGrowSwitch.within(owner, "growth.water.scale", json.get("scale"), false, 0.0F, 1.0F) : null;
        return new WaterDef(whole(owner, json, "within", WaterDef.DEFAULT_WITHIN, 1, WaterDef.MAX_WITHIN), ids, tags, kinds[GrowSwitchDef.CROPS], kinds[GrowSwitchDef.SAPLINGS], blocks, except, scale == null ? WaterDef.DEFAULT_SCALE : scale, flag(owner, json, "flowing", true), whole(owner, json, "height", WaterDef.DEFAULT_HEIGHT, 0, WaterDef.MAX_HEIGHT), "diamond".equals(shape(owner, json)), flag(owner, json, "farmland", false), flag(owner, json, "rain", false), flag(owner, json, "bonemeal", false), flag(owner, json, "waiver", false), flag(owner, json, "breaks", false));
    }

    private static boolean covered(WaterDef water, int kind, Block block) {
        if (!water.covers(kind) || water.excepted(block)) { return false; }
        if (water.named()) { return water.listed(block); }
        return kind == GrowSwitchDef.SAPLINGS ? sapling(block) : block instanceof CropBlock || block instanceof StemBlock;
    }

    private static boolean hydrated(Level level, BlockPos pos) {
        BlockState soil = level.getBlockState(pos.below());
        return soil.getBlock() instanceof FarmBlock && soil.getValue(FarmBlock.MOISTURE) > 0;
    }

    public static Status status(Level level, BlockPos pos, Block block, int kind, GrowSwitchDef def) {
        WaterDef water = def.water();
        if (water == null || !covered(water, kind, block)) { return Status.NONE; }
        if (water.rain() && level.isRainingAt(pos.above())) { return Status.FOUND; }
        if (water.farmland() && water.listsWater() && hydrated(level, pos)) { return Status.FOUND; }
        WaiverDef waiver = def.waiver();
        if (water.waiver() && waiver != null && waiver.covers(kind) && waiver.found(level, pos)) { return Status.WAIVED; }
        return water.supplied(level, pos) ? Status.FOUND : Status.MISSING;
    }

    static float factor(Level level, BlockPos pos, Block block, int kind, GrowSwitchDef def) {
        WaterDef water = def.water();
        return water != null && status(level, pos, block, kind, def) == Status.MISSING ? water.scale() : 1.0F;
    }

    static boolean broke(GrowSwitchDef def, ServerLevel level, BlockPos pos, Block block, int kind) {
        WaterDef water = def.water();
        if (water == null || !water.breaks() || status(level, pos, block, kind, def) != Status.MISSING || !level.getBlockState(pos).is(block)) { return false; }
        level.destroyBlock(pos, true);
        return true;
    }

    public static void onBonemeal(BonemealEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        GrowSwitchDef def = ContentEnvironments.growth(level, pos);
        WaterDef water = def.water();
        if (water == null || !water.bonemeal()) { return; }
        Block block = event.getBlock().getBlock();
        if (status(level, pos, block, kindOf(block), def) == Status.MISSING) { event.setCanceled(true); }
    }
}
