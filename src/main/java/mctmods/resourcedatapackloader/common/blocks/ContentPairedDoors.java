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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class ContentPairedDoors {
    private static final String BLOCKS = "blocks";
    private static final String TAG = "tag";
    private static final List<Rule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final Map<BlockPos, Player> ACTORS = new HashMap<>();
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

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        boolean all = Json.getBoolean(json, "all", false);
        if (!json.has(BLOCKS) && !json.has(TAG) && !all) {
            ContentLog.LOGGER.error("Paired door {} needs blocks, tag or all, ignoring it", key);
            return;
        }
        BlockGroup group = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, BLOCKS)) { group.add(match, "Paired door " + key, BLOCKS); }
        List<TagKey<Block>> tags = new ArrayList<>();
        for (String name : Json.names(json, TAG)) {
            ResourceLocation id = Registered.tagId(name);
            if (id == null) { ContentLog.LOGGER.error("Paired door {} names tag '{}', which is not a valid tag id, skipping it", key, name); }
            else { tags.add(TagKey.create(Registries.BLOCK, id)); }
        }
        if (group.isEmpty() && tags.isEmpty() && !all) {
            ContentLog.LOGGER.error("Paired door {} names no registered blocks, ignoring it", key);
            return;
        }
        RULES.add(new Rule(group, tags, all, Json.getBoolean(json, "unlessSneaking", true), Json.getBoolean(json, "mobs", false)));
    }

    public static void onUse(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide() || idle()) { return; }
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (DoorPairs.isDoor(state)) { ACTORS.put((DoorPairs.isLower(state) ? pos : pos.below()).immutable(), event.getEntity()); }
    }

    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !ACTORS.isEmpty()) { ACTORS.clear(); }
    }

    public static void onBlockChanged(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (following || oldState.getBlock() != newState.getBlock() || !newState.hasProperty(BlockStateProperties.OPEN) || oldState.getValue(BlockStateProperties.OPEN) == newState.getValue(BlockStateProperties.OPEN) || idle() || !DoorPairs.isDoor(newState) || !DoorPairs.isLower(newState)) { return; }
        changed(level, pos, DoorPairs.isOpen(newState), DoorPairs.isPowered(oldState) != DoorPairs.isPowered(newState));
    }

    private static void changed(ServerLevel level, BlockPos lower, boolean open, boolean redstone) {
        Player player = ACTORS.get(lower);
        if (player instanceof FakePlayer) { return; }
        if (player != null && player.level() != level) { player = null; }
        BlockState door = level.getBlockState(lower);
        BlockPos partnerPos = DoorPairs.partnerPos(door, lower);
        if (!level.isLoaded(partnerPos)) { return; }
        BlockState partner = level.getBlockState(partnerPos);
        for (Rule rule : RULES) {
            if (rule.misses(door) || rule.misses(partner) || !pairs(door, partner, open) || player != null && rule.unlessSneaking() && player.isShiftKeyDown() || player == null && !redstone && !rule.mobs()) { continue; }
            if (ClaimGuard.sameOwnerAcross(level, lower, partnerPos) && (player == null || ClaimGuard.passes(player, partnerPos))) { follow(level, partnerPos, partner, open); }
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
