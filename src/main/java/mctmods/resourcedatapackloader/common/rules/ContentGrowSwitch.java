package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SneakGrowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaiverDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaterDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class ContentGrowSwitch {
    private static final List<String> KEYS = List.of("crops", "saplings", "animals", "breeding");
    private static final String BY_BLOCK = "byBlock";
    private static final String TOOLTIP = "tooltip";
    private static final String WAIVER = "waiver";
    private static final String WATER = "water";
    private static final String SNEAK = "sneak";
    private static final List<String> TOOLTIPS = List.of("off", "here", "year");
    private static final List<String> WAIVER_KEYS = List.of("blocks", "within", "scale", "kinds", "clear");
    static final double MOST = 100.0D;
    private static final Map<Spot, Pending> PENDING = new HashMap<>();
    private static boolean draining;

    private record Pending(Block block, int extra) {}

    private record Spot(ServerLevel level, BlockPos pos) {
        boolean in(ServerLevel other) { return level == other; }
    }

    private ContentGrowSwitch() {}

    @Nullable static BlockMatch match(String owner, String text) {
        String name = text.trim();
        if (!name.startsWith("#")) {
            Block block = ContentStates.registered(name);
            if (block == null) { ContentLog.LOGGER.error("{} gives growth block {}, which is not registered, ignoring it", owner, name); }
            return block == null ? null : BlockMatch.of(block);
        }
        Identifier tag = name.length() > 1 ? Identifier.tryParse(name.substring(1)) : null;
        if (tag == null) { ContentLog.LOGGER.error("{} gives '{}' in growth, which is not a block tag id, ignoring it", owner, name); }
        return tag == null ? null : BlockMatch.group(tag);
    }

    @Nullable static Float within(String owner, String key, JsonElement value, boolean whole, double low, double high) {
        Double number = ContentSpawnSwitch.number(value);
        if (number == null || number < low || number > high || whole && number != Math.rint(number)) {
            ContentLog.LOGGER.error("{} gives {} that is not {}a number from {} to {}, ignoring it", owner, key, whole ? "whole, " : "", low, high);
            return null;
        }
        return number.floatValue();
    }

    private static List<GrowSwitchDef.Match> byBlock(String owner, JsonElement element) {
        List<GrowSwitchDef.Match> found = new ArrayList<>();
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth.byBlock that is not an object of block ids or #tags to scales, ignoring it", owner);
            return found;
        }
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            Float scale = within(owner, "growth.byBlock." + entry.getKey(), entry.getValue(), false, 0.0D, MOST);
            BlockMatch match = scale == null ? null : match(owner, entry.getKey());
            if (match != null) { found.add(new GrowSwitchDef.Match(match, scale)); }
        }
        return found;
    }

    private static int tooltip(String owner, JsonElement element) {
        int mode = element.isJsonPrimitive() && element.getAsJsonPrimitive().isString() ? TOOLTIPS.indexOf(element.getAsString()) : -1;
        if (mode < 0) { ContentLog.LOGGER.error("{} gives growth.tooltip as {}, which is not one of {}, ignoring it", owner, element, TOOLTIPS); }
        return Math.max(mode, GrowSwitchDef.TOOLTIP_OFF);
    }

    static void blockList(String owner, String key, JsonElement element, List<BlockMatch> into) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives {} that is not a list of block ids or #tags, ignoring it", owner, key);
            return;
        }
        for (JsonElement entry : element.getAsJsonArray()) {
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                ContentLog.LOGGER.error("{} gives {} an entry {} that is not a block id or #tag, ignoring it", owner, key, entry);
                continue;
            }
            BlockMatch match = match(owner, entry.getAsString());
            if (match != null) { into.add(match); }
        }
    }

    static boolean[] kindList(String owner, String key, JsonElement element) {
        boolean[] kinds = new boolean[GrowSwitchDef.KINDS];
        if (element.isJsonArray()) {
            for (JsonElement entry : element.getAsJsonArray()) {
                int kind = entry.isJsonPrimitive() ? KEYS.indexOf(entry.getAsString()) : -1;
                if (kind == GrowSwitchDef.CROPS || kind == GrowSwitchDef.SAPLINGS) { kinds[kind] = true; }
                else { ContentLog.LOGGER.error("{} gives {} the entry {}, which is not crops or saplings, ignoring it", owner, key, entry); }
            }
        }
        else { ContentLog.LOGGER.error("{} gives {} that is not a list of crops and saplings, ignoring it", owner, key); }
        if (!kinds[GrowSwitchDef.CROPS] && !kinds[GrowSwitchDef.SAPLINGS]) { return WaiverDef.bothKinds(); }
        return kinds;
    }

    @Nullable private static WaiverDef waiver(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth.waiver that is not an object of blocks, within, scale, kinds and clear keys, ignoring it", owner);
            return null;
        }
        JsonObject json = element.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!WAIVER_KEYS.contains(entry.getKey())) { ContentLog.LOGGER.error("{} gives growth.waiver.{}, which is not a waiver key, ignoring it", owner, entry.getKey()); }
        }
        List<BlockMatch> blocks = new ArrayList<>();
        if (json.has("blocks")) { blockList(owner, "growth.waiver.blocks", json.get("blocks"), blocks); }
        if (blocks.isEmpty()) {
            ContentLog.LOGGER.error("{} gives growth.waiver with no usable blocks, ignoring it", owner);
            return null;
        }
        Float within = json.has("within") ? within(owner, "growth.waiver.within", json.get("within"), true, 1.0D, WaiverDef.MAX_WITHIN) : null;
        Float scale = json.has("scale") ? within(owner, "growth.waiver.scale", json.get("scale"), false, 0.0D, MOST) : null;
        boolean[] kinds = json.has("kinds") ? kindList(owner, "growth.waiver.kinds", json.get("kinds")) : WaiverDef.bothKinds();
        JsonElement clearing = json.get("clear");
        boolean clear = false;
        if (clearing != null && clearing.isJsonPrimitive() && clearing.getAsJsonPrimitive().isBoolean()) { clear = clearing.getAsBoolean(); }
        else if (clearing != null) { ContentLog.LOGGER.error("{} gives growth.waiver.clear as {}, which is not true or false, ignoring it", owner, clearing); }
        return new WaiverDef(blocks, within == null ? WaiverDef.DEFAULT_WITHIN : within.intValue(), scale == null ? WaiverDef.DEFAULT_SCALE : scale, kinds, clear);
    }

    @Nullable public static GrowSwitchDef parse(String owner, JsonElement element) {
        if (!element.isJsonObject()) {
            ContentLog.LOGGER.error("{} gives growth that is not an object, ignoring it", owner);
            return null;
        }
        float[] scales = GrowSwitchDef.filled(1.0F);
        List<GrowSwitchDef.Match> matches = new ArrayList<>();
        WaiverDef waiver = null;
        WaterDef water = null;
        SneakGrowDef sneak = null;
        int tooltip = GrowSwitchDef.TOOLTIP_OFF;
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            if (BY_BLOCK.equals(entry.getKey())) {
                matches = byBlock(owner, entry.getValue());
                continue;
            }
            if (TOOLTIP.equals(entry.getKey())) {
                tooltip = tooltip(owner, entry.getValue());
                continue;
            }
            if (WAIVER.equals(entry.getKey())) {
                waiver = waiver(owner, entry.getValue());
                continue;
            }
            if (WATER.equals(entry.getKey())) {
                water = ContentGrowWater.parse(owner, entry.getValue());
                continue;
            }
            if (SNEAK.equals(entry.getKey())) {
                sneak = ContentSneakGrow.parse(owner, entry.getValue());
                continue;
            }
            int kind = KEYS.indexOf(entry.getKey());
            if (kind < 0) {
                ContentLog.LOGGER.error("{} gives growth.{}, which is not a growth key, ignoring it", owner, entry.getKey());
                continue;
            }
            Double value = ContentSpawnSwitch.number(entry.getValue());
            if (value == null || value < 0.0D || value > MOST) { ContentLog.LOGGER.error("{} gives growth.{} that is not a number from 0 to 100, ignoring it", owner, entry.getKey()); }
            else { scales[kind] = value.floatValue(); }
        }
        return new GrowSwitchDef(scales, matches, waiver, tooltip, water, sneak);
    }

    private static float dry(GrowSwitchDef def, ServerLevel level, BlockPos pos, Block block, int kind) {
        WaiverDef waiver = def.waiver();
        return waiver != null && waiver.covers(kind) && waiver.found(level, pos) ? waiver.scale() : def.scale(kind, block);
    }

    private static float scale(GrowSwitchDef def, ServerLevel level, BlockPos pos, Block block, int kind) {
        float speed = dry(def, level, pos, block, kind);
        WaterDef water = def.water();
        return speed > 0.0F && water != null && ContentGrowWater.status(level, pos, block, kind, def) == ContentGrowWater.Status.MISSING ? speed * water.scale() : speed;
    }

    public static float scale(ServerLevel level, BlockPos pos, Block sapling) { return dry(ContentEnvironments.growth(level, pos), level, pos, sapling, GrowSwitchDef.SAPLINGS); }

    public static int draws(float scale, RandomSource random) { return roll(scale, random); }

    static boolean stopped(ServerLevel level, BlockPos pos, Block block, int kind) { return scale(ContentEnvironments.growth(level, pos), level, pos, block, kind) <= 0.0F; }

    static int roll(float scale, RandomSource random) {
        int whole = (int) scale;
        float fraction = scale - whole;
        return fraction > 0.0F && random.nextFloat() < fraction ? whole + 1 : whole;
    }

    private static int steps(ServerLevel level, BlockPos pos, int kind, RandomSource random) { return roll(ContentEnvironments.growth(level, pos).scale(kind), random); }

    private static int steps(GrowSwitchDef def, ServerLevel level, BlockPos pos, Block block, int kind, RandomSource random) { return roll(scale(def, level, pos, block, kind), random); }

    public static void onCropGrow(CropGrowEvent.Pre event) {
        if (draining || !(event.getLevel() instanceof ServerLevel level)) { return; }
        Block block = event.getState().getBlock();
        GrowSwitchDef def = ContentEnvironments.growth(level, event.getPos());
        if (ContentGrowWater.broke(def, level, event.getPos(), block, GrowSwitchDef.CROPS)) {
            event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            return;
        }
        int steps = steps(def, level, event.getPos(), block, GrowSwitchDef.CROPS, level.getRandom());
        if (steps == 0) { event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW); }
        else if (steps > 1) { queue(level, event.getPos().immutable(), new Pending(block, steps - 1)); }
    }

    private static void queue(ServerLevel level, BlockPos pos, Pending added) { PENDING.merge(new Spot(level, pos), added, (now, more) -> now.block() == more.block() ? new Pending(now.block(), now.extra() + more.extra()) : more); }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || PENDING.isEmpty()) { return; }
        Map<Spot, Pending> held = new HashMap<>();
        for (Iterator<Map.Entry<Spot, Pending>> it = PENDING.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Spot, Pending> entry = it.next();
            if (entry.getKey().in(level)) {
                held.put(entry.getKey(), entry.getValue());
                it.remove();
            }
        }
        draining = true;
        try {
            for (Map.Entry<Spot, Pending> entry : held.entrySet()) { drain(level, entry.getKey().pos(), entry.getValue()); }
        }
        finally { draining = false; }
    }

    private static void drain(ServerLevel level, BlockPos pos, Pending pending) {
        if (!level.isLoaded(pos)) { return; }
        BlockPos at = pos;
        if (!level.getBlockState(at).is(pending.block())) { at = pos.below(); }
        if (!level.getBlockState(at).is(pending.block())) { at = pos.above(); }
        for (int count = 0; count < pending.extra(); count++) {
            BlockState now = level.getBlockState(at);
            if (!now.is(pending.block())) { return; }
            now.randomTick(level, at, level.getRandom());
        }
    }

    public static void age(LivingEntity living) {
        if (!(living instanceof Animal animal) || !(animal.level() instanceof ServerLevel level) || !animal.isAlive()) { return; }
        int age = animal.getAge();
        if (age == 0 || age < 0 && !animal.canAgeUp()) { return; }
        int delta = steps(level, animal.blockPosition(), age < 0 ? GrowSwitchDef.ANIMALS : GrowSwitchDef.BREEDING, animal.getRandom()) - 1;
        if (delta < 0) { animal.setAge(age < 0 ? age - 1 : age + 1); }
        else if (delta > 0) {
            int move = Math.min(delta, Math.abs(age) - 1);
            animal.setAge(age < 0 ? age + move : age - move);
        }
    }

    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getParentA() instanceof Animal parent) || !(parent.level() instanceof ServerLevel level)) { return; }
        if (ContentEnvironments.growth(level, parent.blockPosition()).scale(GrowSwitchDef.BREEDING) <= 0.0F) { event.setCanceled(true); }
    }

    public static boolean skips(ServerLevel level, BlockPos pos, Block block, RandomSource random) {
        if (draining) { return false; }
        GrowSwitchDef def = ContentEnvironments.growth(level, pos);
        if (ContentGrowWater.broke(def, level, pos, block, GrowSwitchDef.SAPLINGS)) { return true; }
        int steps = steps(def, level, pos, block, GrowSwitchDef.SAPLINGS, random);
        if (steps > 1) { queue(level, pos.immutable(), new Pending(block, steps - 1)); }
        return steps == 0;
    }

    public static void forget() {
        PENDING.clear();
        draining = false;
    }
}
