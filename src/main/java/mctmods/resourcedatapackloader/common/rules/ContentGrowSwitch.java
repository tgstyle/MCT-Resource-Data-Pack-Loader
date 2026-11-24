package mctmods.resourcedatapackloader.common.rules;

import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowSwitchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SneakGrowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaiverDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WaterDef;
import mctmods.resourcedatapackloader.common.util.BlockMatches;
import mctmods.resourcedatapackloader.common.util.ticks.RandomTicks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mctmods.resourcedatapackloader.core.util.SideUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ContentGrowSwitch {
    private static final List<String> KEYS = Arrays.asList("crops", "saplings", "animals", "breeding");
    private static final String GROWTH = "growth";
    private static final String BY_BLOCK = "byBlock";
    private static final String TOOLTIP = "tooltip";
    private static final String WAIVER = "waiver";
    private static final String WATER = "water";
    private static final String SNEAK = "sneak";
    private static final List<String> TOOLTIPS = Arrays.asList("off", "here", "year");
    private static final List<String> WAIVER_KEYS = Arrays.asList("blocks", "within", "scale", "kinds", "clear");
    static final float MOST = 100.0F;
    private static final Map<Integer, Map<BlockPos, Pending>> PENDING = new ConcurrentHashMap<>();
    private static boolean draining;

    private ContentGrowSwitch() {}

    private static final class Pending {
        final Block block;
        int extra;

        Pending(Block block, int extra) {
            this.block = block;
            this.extra = extra;
        }
    }

    private static List<GrowSwitchDef.Match> byBlock(String owner, JsonElement element) {
        List<GrowSwitchDef.Match> found = new ArrayList<>();
        JsonObject json = Json.object(owner, "growth.byBlock", element, "an object of block ids or #names to scales");
        if (json == null) { return found; }
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            Float scale = Json.within(owner, "growth.byBlock." + entry.getKey(), entry.getValue(), false, 0.0F, MOST);
            BlockMatch match = scale == null ? null : BlockMatches.parse(owner, entry.getKey(), GROWTH);
            if (match != null) { found.add(new GrowSwitchDef.Match(match, scale)); }
        }
        return found;
    }

    private static int tooltip(String owner, JsonElement element) {
        int mode = TOOLTIPS.indexOf(Json.text(element));
        if (mode < 0) { Json.bad(owner, "growth.tooltip", element, "one of " + TOOLTIPS); }
        return Math.max(mode, GrowSwitchDef.TOOLTIP_OFF);
    }

    static void blockList(String owner, String key, JsonElement element, List<BlockMatch> into) {
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("{} gives {} that is not a list of block ids or #names, ignoring it", owner, key);
            return;
        }
        for (JsonElement entry : element.getAsJsonArray()) {
            String name = Json.text(entry);
            if (name == null) {
                ContentLog.LOGGER.error("{} gives {} an entry {} that is not a block id or #name, ignoring it", owner, key, entry);
                continue;
            }
            BlockMatch match = BlockMatches.parse(owner, name, GROWTH);
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
        JsonObject json = Json.object(owner, "growth.waiver", element, "an object of blocks, within, scale, kinds and clear keys");
        if (json == null) { return null; }
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!WAIVER_KEYS.contains(entry.getKey())) { ContentLog.LOGGER.error("{} gives growth.waiver.{}, which is not a waiver key, ignoring it", owner, entry.getKey()); }
        }
        List<BlockMatch> blocks = new ArrayList<>();
        if (json.has("blocks")) { blockList(owner, "growth.waiver.blocks", json.get("blocks"), blocks); }
        if (blocks.isEmpty()) {
            ContentLog.LOGGER.error("{} gives growth.waiver with no usable blocks, ignoring it", owner);
            return null;
        }
        Float within = json.has("within") ? Json.within(owner, "growth.waiver.within", json.get("within"), true, 1.0F, WaiverDef.MAX_WITHIN) : null;
        Float scale = json.has("scale") ? Json.within(owner, "growth.waiver.scale", json.get("scale"), false, 0.0F, MOST) : null;
        boolean[] kinds = json.has("kinds") ? kindList(owner, "growth.waiver.kinds", json.get("kinds")) : WaiverDef.bothKinds();
        boolean clear = json.has("clear") && Json.flag(owner, "growth.waiver.clear", json.get("clear"), false);
        return new WaiverDef(blocks, within == null ? WaiverDef.DEFAULT_WITHIN : within.intValue(), scale == null ? WaiverDef.DEFAULT_SCALE : scale, kinds, clear);
    }

    @Nullable public static GrowSwitchDef parse(String owner, @Nullable JsonElement element) {
        if (element == null) { return null; }
        JsonObject json = Json.object(owner, "growth", element, "an object of crops, saplings, animals, breeding, byBlock, tooltip, waiver, water and sneak keys");
        if (json == null) { return null; }
        float[] scales = GrowSwitchDef.filled(1.0F);
        List<GrowSwitchDef.Match> matches = new ArrayList<>();
        WaiverDef waiver = null;
        WaterDef water = null;
        SneakGrowDef sneak = null;
        int tooltip = GrowSwitchDef.TOOLTIP_OFF;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
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
            Float value = Json.number(owner, "growth." + entry.getKey(), entry.getValue(), false);
            if (value == null) { continue; }
            if (value > MOST) {
                ContentLog.LOGGER.error("{} gives growth.{} as {}, which is above {}, ignoring it", owner, entry.getKey(), value, (int) MOST);
                continue;
            }
            scales[kind] = value;
        }
        return new GrowSwitchDef(scales, matches, waiver, tooltip, water, sneak);
    }

    private static float dry(GrowSwitchDef def, World world, BlockPos pos, Block block, int kind) {
        WaiverDef waiver = def.waiver();
        return waiver != null && waiver.covers(kind) && waiver.found(world, pos) ? waiver.scale : def.scale(kind, block, world, pos);
    }

    private static float scale(GrowSwitchDef def, World world, BlockPos pos, Block block, int kind) {
        float speed = dry(def, world, pos, block, kind);
        return speed > 0.0F ? speed * ContentGrowWater.factor(world, pos, block, kind, def) : speed;
    }

    public static float scale(World world, BlockPos pos, Block sapling) { return dry(ContentEnvironments.growth(world, pos), world, pos, sapling, GrowSwitchDef.SAPLINGS); }

    public static int draws(float scale, Random random) { return roll(scale, random); }

    static boolean stopped(World world, BlockPos pos, Block block, int kind) { return scale(ContentEnvironments.growth(world, pos), world, pos, block, kind) <= 0.0F; }

    static int steps(World world, BlockPos pos, int kind, Random rand) { return roll(ContentEnvironments.growth(world, pos).scale(kind), rand); }

    static int steps(GrowSwitchDef def, World world, BlockPos pos, Block block, int kind, Random rand) { return roll(scale(def, world, pos, block, kind), rand); }

    static int roll(float scale, Random rand) {
        int whole = (int) scale;
        float fraction = scale - whole;
        return fraction <= 0.0F ? whole : whole + (rand.nextFloat() < fraction ? 1 : 0);
    }

    public static boolean skips(World world, BlockPos pos, Block block, Random rand) {
        if (draining || world.isRemote) { return false; }
        return rollQueued(world, pos, block, GrowSwitchDef.SAPLINGS, rand) <= 0;
    }

    private static int rollQueued(World world, BlockPos pos, Block block, int kind, Random rand) {
        GrowSwitchDef def = ContentEnvironments.growth(world, pos);
        if (ContentGrowWater.broke(def, world, pos, block, kind)) { return -1; }
        int steps = steps(def, world, pos, block, kind, rand);
        if (steps > 1) { queue(world, pos, block, steps - 1); }
        return steps;
    }

    private static void queue(World world, BlockPos pos, Block block, int extra) {
        PENDING.computeIfAbsent(world.provider.getDimension(), key -> new LinkedHashMap<>()).merge(pos.toImmutable(), new Pending(block, extra), (held, added) -> {
            if (held.block == added.block) {
                held.extra += added.extra;
                return held;
            }
            return added;
        });
    }

    @SubscribeEvent public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        World world = event.getWorld();
        if (draining || world.isRemote) { return; }
        BlockPos pos = event.getPos();
        Block block = event.getState().getBlock();
        if (rollQueued(world, pos, block, GrowSwitchDef.CROPS, world.rand) <= 0) { event.setResult(Event.Result.DENY); }
    }

    @SubscribeEvent public static void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (!SideUtils.serverEnd(event) || PENDING.isEmpty()) { return; }
        Map<BlockPos, Pending> queued = PENDING.remove(world.provider.getDimension());
        if (queued == null) { return; }
        draining = true;
        try {
            for (Map.Entry<BlockPos, Pending> entry : queued.entrySet()) { drain(world, entry.getKey(), entry.getValue()); }
        }
        finally { draining = false; }
    }

    private static void drain(World world, BlockPos pos, Pending pending) {
        if (!world.isBlockLoaded(pos)) { return; }
        BlockPos at = pos;
        if (world.getBlockState(at).getBlock() != pending.block) { at = pos.down(); }
        if (world.getBlockState(at).getBlock() != pending.block) { at = pos.up(); }
        RandomTicks.run(world, at, pending.block, pending.extra, null);
    }

    @SubscribeEvent public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!(living instanceof EntityAnimal) || living.world.isRemote || !living.isEntityAlive()) { return; }
        EntityAnimal animal = (EntityAnimal) living;
        int age = animal.getGrowingAge();
        if (age == 0) { return; }
        int toward = age < 0 ? 1 : -1;
        int delta = steps(living.world, animal.getPosition(), age < 0 ? GrowSwitchDef.ANIMALS : GrowSwitchDef.BREEDING, animal.getRNG()) - 1;
        if (delta < 0) { animal.setGrowingAge(age - toward); }
        else if (delta > 0) { animal.setGrowingAge(age + toward * Math.min(delta, Math.abs(age) - 1)); }
    }

    @SubscribeEvent public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getParentA() instanceof EntityAnimal)) { return; }
        EntityAnimal parent = (EntityAnimal) event.getParentA();
        if (ContentEnvironments.growth(parent.world, parent.getPosition()).scale(GrowSwitchDef.BREEDING) == 0.0F) { event.setCanceled(true); }
    }

    public static void serverStopped() {
        PENDING.clear();
        draining = false;
    }
}
