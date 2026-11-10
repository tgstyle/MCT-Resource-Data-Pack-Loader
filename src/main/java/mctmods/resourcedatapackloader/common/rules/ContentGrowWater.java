package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaiverDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaterDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

public final class ContentGrowWater {
    public enum Status { NONE, FOUND, MISSING, WAIVED }

    private static final List<String> KEYS = List.of("within", "fluids", "kinds", "blocks", "except", "scale", "flowing", "height", "shape", "farmland", "rain", "bonemeal", "waiver", "breaks");
    private static final List<String> SHAPES = List.of("box", "diamond");

    private ContentGrowWater() {}

    private static boolean flag(String owner, JsonObject json, String key, boolean fallback) {
        JsonElement value = json.get(key);
        if (value == null) { return fallback; }
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) { return value.getAsBoolean(); }
        ContentLog.LOGGER.error("{} gives growth.water.{} as {}, which is not true or false, ignoring it", owner, key, value);
        return fallback;
    }

    private static void fluids(String owner, JsonElement element, List<Identifier> ids, List<TagKey<Fluid>> tags) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives growth.water.fluids that is not a list of fluid ids or #tags, ignoring it", owner);
            return;
        }
        for (JsonElement entry : element.getAsJsonArray()) {
            String text = entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString() ? entry.getAsString().trim() : "";
            Identifier id = text.startsWith("#") ? Registered.tagId(text) : Identifier.tryParse(text);
            if (id == null) { ContentLog.LOGGER.error("{} gives growth.water.fluids an entry {} that is not a fluid id or #tag, ignoring it", owner, entry); }
            else if (text.startsWith("#")) { tags.add(TagKey.create(Registries.FLUID, id)); }
            else { ids.add(id); }
        }
    }

    @Nullable public static WaterDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth.water that is not an object, ignoring it", owner);
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!KEYS.contains(entry.getKey())) { ContentLog.LOGGER.error("{} gives growth.water.{}, which is not a water key, ignoring it", owner, entry.getKey()); }
        }
        Float within = json.has("within") ? ContentGrowSwitch.within(owner, "growth.water.within", json.get("within"), true, 1.0D, WaterDef.MAX_WITHIN) : null;
        Float scale = json.has("scale") ? ContentGrowSwitch.within(owner, "growth.water.scale", json.get("scale"), false, 0.0D, 1.0D) : null;
        Float height = json.has("height") ? ContentGrowSwitch.within(owner, "growth.water.height", json.get("height"), true, 0.0D, WaterDef.MAX_HEIGHT) : null;
        List<Identifier> ids = new ArrayList<>();
        List<TagKey<Fluid>> tags = new ArrayList<>();
        if (json.has("fluids")) { fluids(owner, json.get("fluids"), ids, tags); }
        if (ids.isEmpty() && tags.isEmpty()) {
            if (json.has("fluids")) { ContentLog.LOGGER.error("{} gives growth.water.fluids with no usable fluid, using water", owner); }
            ids.add(WaterDef.WATER);
        }
        List<BlockMatch> blocks = new ArrayList<>();
        if (json.has("blocks")) { ContentGrowSwitch.blockList(owner, "growth.water.blocks", json.get("blocks"), blocks); }
        if (json.has("blocks") && blocks.isEmpty()) { ContentLog.LOGGER.error("{} gives growth.water.blocks with no usable blocks, using the default plants", owner); }
        List<BlockMatch> except = new ArrayList<>();
        if (json.has("except")) { ContentGrowSwitch.blockList(owner, "growth.water.except", json.get("except"), except); }
        boolean[] kinds = json.has("kinds") ? ContentGrowSwitch.kindList(owner, "growth.water.kinds", json.get("kinds")) : WaiverDef.bothKinds();
        String shape = json.has("shape") && json.get("shape").isJsonPrimitive() ? json.get("shape").getAsString() : SHAPES.getFirst();
        if (!SHAPES.contains(shape)) {
            ContentLog.LOGGER.error("{} gives growth.water.shape as {}, which is not one of {}, using box", owner, shape, SHAPES);
            shape = SHAPES.getFirst();
        }
        return new WaterDef(within == null ? WaterDef.DEFAULT_WITHIN : within.intValue(), ids, tags, kinds, blocks, except, scale == null ? 0.0F : scale, flag(owner, json, "flowing", true), height == null ? WaterDef.DEFAULT_HEIGHT : height.intValue(), SHAPES.get(1).equals(shape), flag(owner, json, "farmland", false), flag(owner, json, "rain", false), flag(owner, json, "bonemeal", false), flag(owner, json, "waiver", false), flag(owner, json, "breaks", false));
    }

    public static boolean sapling(Block block) { return block instanceof SaplingBlock || block instanceof ContentSaplingBlock; }

    public static int kindOf(Block block) { return sapling(block) ? GrowSwitchDef.SAPLINGS : GrowSwitchDef.CROPS; }

    private static boolean listed(List<BlockMatch> matches, Block block) {
        for (BlockMatch match : matches) {
            if (match.matches(block)) { return true; }
        }
        return false;
    }

    private static boolean covered(WaterDef water, Block block, int kind) {
        if (!water.covers(kind) || listed(water.except(), block)) { return false; }
        if (!water.blocks().isEmpty()) { return listed(water.blocks(), block); }
        return kind == GrowSwitchDef.CROPS ? block instanceof CropBlock || block instanceof StemBlock : sapling(block);
    }

    private static boolean hydrated(BlockState soil) { return soil.getBlock() instanceof FarmlandBlock && soil.getValue(FarmlandBlock.MOISTURE) > 0; }

    public static Status status(Level level, BlockPos pos, Block block, int kind, GrowSwitchDef def) {
        WaterDef water = def.water();
        if (water == null || !covered(water, block, kind)) { return Status.NONE; }
        if (water.rain() && level.isRainingAt(pos.above())) { return Status.FOUND; }
        if (water.farmland() && water.listsWater() && hydrated(level.getBlockState(pos.below()))) { return Status.FOUND; }
        WaiverDef waiver = def.waiver();
        if (water.waiver() && waiver != null && waiver.covers(kind) && waiver.found(level, pos)) { return Status.WAIVED; }
        return water.found(level, pos) ? Status.FOUND : Status.MISSING;
    }

    static boolean broke(GrowSwitchDef def, ServerLevel level, BlockPos pos, Block block, int kind) {
        WaterDef water = def.water();
        if (water == null || !water.breaks() || status(level, pos, block, kind, def) != Status.MISSING || !level.getBlockState(pos).is(block)) { return false; }
        level.destroyBlock(pos, true);
        return true;
    }

    public static void onBonemeal(BonemealEvent event) {
        Level level = event.getLevel();
        GrowSwitchDef def = ContentEnvironments.growth(level, event.getPos());
        WaterDef water = def.water();
        if (water == null || !water.bonemeal()) { return; }
        Block block = event.getState().getBlock();
        if (status(level, event.getPos(), block, kindOf(block), def) == Status.MISSING) { event.setCanceled(true); }
    }
}
