package mctmods.resourcedatapackloader.common.blocks;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimGuard;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ContentPairedDoors {
    private static final String BLOCKS = "blocks";
    private static final String TAG = "tag";
    private static final List<String> DOOR_KEYS = List.of("requires", BLOCKS, TAG, "all", "unlessSneaking", "mobs");
    private static final List<Rule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final Map<BlockPos, ServerPlayer> ACTORS = new HashMap<>();
    private static boolean following;

    private ContentPairedDoors() {}

    private record Rule(BlockGroup group, List<TagKey<Block>> tags, boolean all, boolean unlessSneaking, boolean mobs) {
        boolean misses(BlockState state) { return !DoorPairs.isDoor(state) || !all && !group.contains(state) && !tagged(state); }

        private boolean tagged(BlockState state) {
            for (TagKey<Block> tag : tags) {
                if (state.is(tag)) { return true; }
            }
            return false;
        }
    }

    private static boolean idle() {
        if (GENERATION.stale()) { reload(); }
        return RULES.isEmpty();
    }

    private static void reload() {
        RULES.clear();
        Json.eachObject(PackManager.PAIRED_DOORS, "paired door", ContentPairedDoors::read);
        if (!RULES.isEmpty()) { Summary.info("content.paired_doors", "Loaded " + RULES.size() + " paired door rule(s)"); }
    }

    private static void read(Identifier key, JsonObject json) {
        Json.unknownKeys(json, "the top level", DOOR_KEYS);
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        boolean all = Json.getBoolean(json, "all", false);
        if (!json.has(BLOCKS) && !json.has(TAG) && !all) {
            ContentLog.LOGGER.error("Paired door {} needs blocks, tag or all, ignoring it", key);
            return;
        }
        BlockGroup group = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, BLOCKS)) { group.add(match, key, BLOCKS); }
        List<TagKey<Block>> tags = new ArrayList<>();
        for (String named : Json.strings(json, TAG)) {
            Identifier id = Registered.tagId(named);
            if (id == null) { ContentLog.LOGGER.error("Paired door {} names tag '{}', which is not a valid tag id, skipping it", key, named); }
            else { tags.add(TagKey.create(Registries.BLOCK, id)); }
        }
        if (group.isEmpty() && tags.isEmpty() && !all) {
            ContentLog.LOGGER.error("Paired door {} names no registered blocks, ignoring it", key);
            return;
        }
        RULES.add(new Rule(group, tags, all, Json.getBoolean(json, "unlessSneaking", true), Json.getBoolean(json, "mobs", false)));
    }

    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || idle()) { return; }
        BlockPos pos = event.getPos();
        BlockState state = player.level().getBlockState(pos);
        if (DoorPairs.isDoor(state)) { ACTORS.put((DoorPairs.isLower(state) ? pos : pos.below()).immutable(), player); }
    }

    public static void clearActors() {
        if (!ACTORS.isEmpty()) { ACTORS.clear(); }
    }

    public static void onBlockChanged(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (following || idle() || oldState.getBlock() != newState.getBlock() || !DoorPairs.isDoor(newState)) { return; }
        boolean open = DoorPairs.isOpen(newState);
        if (DoorPairs.isOpen(oldState) == open || !DoorPairs.isLower(newState)) { return; }
        changed(level, pos, newState, open, DoorPairs.isPowered(oldState) != DoorPairs.isPowered(newState));
    }

    private static void changed(ServerLevel level, BlockPos lower, BlockState door, boolean open, boolean redstone) {
        ServerPlayer actor = ACTORS.get(lower);
        ServerPlayer player = actor != null && actor.level() == level ? actor : null;
        if (player instanceof FakePlayer) { return; }
        BlockPos partnerPos = DoorPairs.partnerPos(door, lower);
        if (!level.isLoaded(partnerPos)) { return; }
        BlockState partner = level.getBlockState(partnerPos);
        for (Rule rule : RULES) {
            if (rule.misses(door) || rule.misses(partner) || !pairs(door, partner, open) || player != null && rule.unlessSneaking() && player.isShiftKeyDown() || player == null && !redstone && !rule.mobs()) { continue; }
            if (ClaimGuard.sameOwnerAcross(level, lower, partnerPos) && (player == null || ClaimGuard.passes(player, level, partnerPos))) { follow(level, partnerPos, partner, open); }
            return;
        }
    }

    private static boolean pairs(BlockState door, BlockState partner, boolean open) {
        return DoorPairs.isLower(partner) && DoorPairs.mirrors(door, partner) && DoorPairs.isOpen(partner) != open && DoorPairs.sameClass(door, partner) && (open || !DoorPairs.isPowered(partner));
    }

    private static void follow(ServerLevel level, BlockPos partnerPos, BlockState partner, boolean open) {
        following = true;
        try { DoorPairs.move(level, partnerPos, partner, open); }
        finally { following = false; }
    }
}
