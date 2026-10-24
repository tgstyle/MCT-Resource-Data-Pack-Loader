package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Advancements;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentVeins {
    private static final String BLOCKS = "blocks";
    private static final String TOOLS = "tools";
    private static final float EXHAUSTION_PER_POINT = 4.0F;
    private static final List<Rule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static boolean veining;

    private ContentVeins() {}

    private static final class Rule {
        final BlockGroup group;
        final List<ItemStack> tools;
        final int limit;
        final float hunger;
        final String advancement;

        Rule(BlockGroup group, List<ItemStack> tools, int limit, float hunger, String advancement) {
            this.group = group;
            this.tools = tools;
            this.limit = limit;
            this.hunger = hunger;
            this.advancement = advancement;
        }

        boolean holds(ItemStack held) {
            for (ItemStack tool : tools) {
                if (ContentStacks.matches(held, tool.getItem(), tool.getMetadata())) { return true; }
            }
            return false;
        }
    }

    private static void reload() {
        RULES.clear();
        GENERATION.stale();
        Json.eachObject(PackManager.VEINS, "vein mining", ContentVeins::read);
        if (!RULES.isEmpty()) { Summary.info("content.veins", "Loaded " + RULES.size() + " vein mining group(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        BlockGroup group = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, BLOCKS)) { group.add(match, key, BLOCKS); }
        List<ItemStack> tools = new ArrayList<>();
        for (String name : Json.strings(json, TOOLS)) {
            ItemStack stack = ContentStacks.parse(key, name, 1);
            if (!stack.isEmpty()) { tools.add(stack); }
        }
        if (group.isEmpty() || tools.isEmpty()) {
            ContentLog.LOGGER.error("Vein mining {} names no registered blocks or no registered tools, ignoring it", key);
            return;
        }
        RULES.add(new Rule(group, tools, MathHelper.clamp(Json.getInt(json, "limit", 16), 1, 256), Math.max(0.0F, Json.getFloat(json, "hunger", 0.1F)), Json.getString(json, "advancement", "").trim()));
    }

    @Nullable private static Rule ruleFor(EntityPlayerMP player, IBlockState state, ItemStack held) {
        for (Rule rule : RULES) {
            if (!rule.advancement.isEmpty() && !Advancements.has(player, rule.advancement)) { continue; }
            if (rule.group.contains(state) && rule.holds(held)) { return rule; }
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onBreak(BlockEvent.BreakEvent event) {
        if (veining || event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP)) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        if (player.isCreative() || player.isSpectator()) { return; }
        if (GENERATION.stale()) { reload(); }
        if (RULES.isEmpty()) { return; }
        IBlockState state = event.getState();
        Rule rule = ruleFor(player, state, player.getHeldItemMainhand());
        if (rule == null) { return; }
        veining = true;
        try {
            for (BlockPos pos : connected(event.getWorld(), event.getPos(), state.getBlock(), rule)) {
                if (!rule.holds(player.getHeldItemMainhand()) || player.getFoodStats().getFoodLevel() <= 0) { break; }
                player.interactionManager.tryHarvestBlock(pos);
                player.addExhaustion(rule.hunger * EXHAUSTION_PER_POINT);
            }
        }
        finally { veining = false; }
    }

    private static List<BlockPos> connected(World world, BlockPos origin, Block block, Rule rule) {
        List<BlockPos> found = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        seen.add(origin);
        queue.add(origin);
        while (!queue.isEmpty() && found.size() < rule.limit) {
            BlockPos at = queue.poll();
            for (int step = 0; step < 27 && found.size() < rule.limit; step++) {
                BlockPos next = at.add(step % 3 - 1, step / 3 % 3 - 1, step / 9 - 1);
                if (!seen.add(next)) { continue; }
                IBlockState neighbor = world.getBlockState(next);
                if (neighbor.getBlock() != block || !rule.group.contains(neighbor)) { continue; }
                found.add(next);
                queue.add(next);
            }
        }
        return found;
    }
}
