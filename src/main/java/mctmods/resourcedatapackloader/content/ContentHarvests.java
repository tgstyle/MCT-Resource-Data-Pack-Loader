package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Registered;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.world.CropHarvest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import java.util.HashMap;
import java.util.Map;

public final class ContentHarvests {
    private static final String CROPS = "crops";
    private static final String AGE = "age";
    private static final String REPLANT = "replant";
    private static final Map<Block, Rule> BY_BLOCK = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentHarvests() {}

    private record Rule(int age, boolean replant) {}

    private static void reload() {
        BY_BLOCK.clear();
        GENERATION.stale();
        Json.eachObject(PackManager.HARVESTS, "crop harvests", ContentHarvests::read);
        if (!BY_BLOCK.isEmpty()) { Summary.info("content.harvests", "Loaded right-click harvest for " + BY_BLOCK.size() + " crop block(s)"); }
    }

    private static void read(Identifier key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        if (!json.has(CROPS)) {
            ContentLog.LOGGER.error("Crop harvest {} names no crops, ignoring it", key);
            return;
        }
        Rule rule = new Rule(Json.getInt(json, AGE, -1), Json.getBoolean(json, REPLANT, true));
        for (JsonElement element : Json.getJsonArray(json, CROPS)) {
            String name = Json.asString(element, CROPS, "");
            Block block = Registered.block(name);
            if (block == null) {
                ContentLog.LOGGER.error("Crop harvest {} names the block {}, which nothing registers, skipping it", key, name);
                continue;
            }
            BY_BLOCK.put(block, rule);
        }
    }

    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (event.getHand() != InteractionHand.MAIN_HAND || player.isShiftKeyDown() || player.isSpectator()) { return; }
        if (GENERATION.stale()) { reload(); }
        if (BY_BLOCK.isEmpty()) { return; }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Rule rule = BY_BLOCK.get(state.getBlock());
        if (rule == null || !CropHarvest.ripe(state, rule.age()) || !level.mayInteract(player, pos)) { return; }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!(level instanceof ServerLevel server)) { return; }
        CropHarvest.harvest(server, pos, state, player, rule.replant());
    }
}
