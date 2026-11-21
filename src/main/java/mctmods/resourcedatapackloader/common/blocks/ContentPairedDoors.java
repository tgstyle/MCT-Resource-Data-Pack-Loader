package mctmods.resourcedatapackloader.common.blocks;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ContentPairedDoors {
    private static final String BLOCKS = "blocks";
    private static final List<String> DOOR_KEYS = Collections.unmodifiableList(Arrays.asList("requires", BLOCKS, "all", "unlessSneaking", "mobs"));
    private static final List<Rule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final Map<BlockPos, EntityPlayer> ACTORS = new HashMap<>();
    private static final Map<World, DoorChanges> LISTENERS = new WeakHashMap<>();
    private static boolean following;

    private ContentPairedDoors() {}

    private static final class Rule {
        final BlockGroup group;
        final boolean all;
        final boolean unlessSneaking;
        final boolean mobs;

        Rule(BlockGroup group, boolean all, boolean unlessSneaking, boolean mobs) {
            this.group = group;
            this.all = all;
            this.unlessSneaking = unlessSneaking;
            this.mobs = mobs;
        }

        boolean misses(IBlockState state) { return !DoorPairs.isDoor(state) || !all && !group.contains(state); }
    }

    public static boolean following() { return following; }

    public static boolean idle() {
        if (GENERATION.stale()) { reload(); }
        return RULES.isEmpty();
    }

    private static void reload() {
        RULES.clear();
        Json.eachObject(PackManager.PAIRED_DOORS, "paired door", ContentPairedDoors::read);
        if (!RULES.isEmpty()) { Summary.info("content.paired_doors", "Loaded " + RULES.size() + " paired door rule(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", DOOR_KEYS);
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        boolean all = Json.getBoolean(json, "all", false);
        if (!json.has(BLOCKS) && !all) {
            ContentLog.LOGGER.error("Paired door {} needs blocks or all, ignoring it", key);
            return;
        }
        BlockGroup group = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, BLOCKS)) { group.add(match, key, BLOCKS); }
        if (group.isEmpty() && !all) {
            ContentLog.LOGGER.error("Paired door {} names no registered blocks, ignoring it", key);
            return;
        }
        RULES.add(new Rule(group, all, Json.getBoolean(json, "unlessSneaking", true), Json.getBoolean(json, "mobs", false)));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (world.isRemote || idle()) { return; }
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        if (DoorPairs.isDoor(state)) { ACTORS.put((DoorPairs.isLower(state) ? pos : pos.down()).toImmutable(), event.getEntityPlayer()); }
    }

    @SubscribeEvent public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !ACTORS.isEmpty()) { ACTORS.clear(); }
    }

    @SubscribeEvent public static void onLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || LISTENERS.containsKey(world)) { return; }
        DoorChanges listener = new DoorChanges();
        LISTENERS.put(world, listener);
        world.addEventListener(listener);
    }

    @SubscribeEvent public static void onUnload(WorldEvent.Unload event) {
        DoorChanges listener = LISTENERS.remove(event.getWorld());
        if (listener != null) { event.getWorld().removeEventListener(listener); }
    }

    public static void changed(World world, BlockPos lower, boolean open, boolean redstone) {
        EntityPlayer player = ACTORS.get(lower);
        if (player instanceof FakePlayer) { return; }
        if (player != null && player.world != world) { player = null; }
        IBlockState door = DoorPairs.lowerState(world, lower);
        BlockPos partnerPos = DoorPairs.partnerPos(door, lower);
        if (partnerPos == null || !world.isBlockLoaded(partnerPos)) { return; }
        IBlockState partner = DoorPairs.lowerState(world, partnerPos);
        for (Rule rule : RULES) {
            if (rule.misses(door) || rule.misses(partner) || !pairs(door, partner, open) || player != null && rule.unlessSneaking && player.isSneaking() || player == null && !redstone && !rule.mobs) { continue; }
            if (ClaimGuard.sameOwnerAcross(world, lower, partnerPos) && (player == null || ClaimGuard.passes(player, world, partnerPos))) { follow(world, partnerPos, partner, open); }
            return;
        }
    }

    private static boolean pairs(IBlockState door, IBlockState partner, boolean open) {
        return DoorPairs.isLower(partner) && DoorPairs.mirrors(door, partner) && DoorPairs.isOpen(partner) != open && DoorPairs.sameClass(door, partner) && (open || !DoorPairs.isPowered(partner));
    }

    private static void follow(World world, BlockPos partnerPos, IBlockState partner, boolean open) {
        following = true;
        try { DoorPairs.move(world, partnerPos, partner, open); }
        finally { following = false; }
    }
}
