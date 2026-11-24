package mctmods.resourcedatapackloader.common.blocks;

import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.world.WorldEvents;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ContentColumns {
    private static final String ALTERNATE = "alternate";
    private static final List<String> COLUMN_KEYS = Collections.unmodifiableList(Arrays.asList("requires", ContentParser.BLOCKS, ALTERNATE, "limit", "includeSelf", "unlessSneaking"));
    private static final List<String> ALTERNATE_KEYS = Collections.unmodifiableList(Arrays.asList("item", "chance"));
    private static final List<Rule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();
    private static boolean clearing;

    private ContentColumns() {}

    private static final class Rule {
        final BlockGroup group;
        final int limit;
        final boolean includeSelf;
        final boolean unlessSneaking;
        final ItemStack alternate;
        final float chance;

        Rule(BlockGroup group, int limit, boolean includeSelf, boolean unlessSneaking, ItemStack alternate, float chance) {
            this.group = group;
            this.limit = limit;
            this.includeSelf = includeSelf;
            this.unlessSneaking = unlessSneaking;
            this.alternate = alternate;
            this.chance = chance;
        }
    }

    public static boolean clearing() { return clearing; }

    private static boolean idle() {
        if (GENERATION.stale()) { reload(); }
        return RULES.isEmpty();
    }

    private static void reload() { PackGeneration.reload(RULES, PackManager.COLUMNS, "falling column", "content.columns", "falling column group(s)", ContentColumns::read); }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", COLUMN_KEYS);
        if (!ContentRegistry.available(json, key)) { return; }
        BlockGroup group = new BlockGroup();
        for (BlockMatchDef match : ContentParser.matches(key, json, ContentParser.BLOCKS)) { group.add(match, key, ContentParser.BLOCKS); }
        if (group.isEmpty()) {
            ContentLog.LOGGER.error("Falling column {} names no registered blocks, ignoring it", key);
            return;
        }
        ItemStack alternate = ItemStack.EMPTY;
        float chance = 0.0F;
        if (json.has(ALTERNATE)) {
            JsonObject extra = Json.object(json, ALTERNATE);
            Json.unknownKeys(extra, ALTERNATE, ALTERNATE_KEYS);
            alternate = ContentStacks.parse(key, Json.text(extra, "item"), 1);
            chance = Json.bounded(extra, "chance", 0.0F, 1.0F, key + " " + ALTERNATE);
            if (alternate.isEmpty() || Float.isNaN(chance)) {
                ContentLog.LOGGER.error("Falling column {} has a bad {}, which needs a registered item and a chance from 0 to 1, ignoring that key", key, ALTERNATE);
                alternate = ItemStack.EMPTY;
                chance = 0.0F;
            }
        }
        RULES.add(new Rule(group, MathHelper.clamp(Json.getInt(json, "limit", 256), 1, 256), Json.getBoolean(json, "includeSelf", false), Json.getBoolean(json, "unlessSneaking", false), alternate, chance));
    }

    @Nullable private static Rule ruleFor(IBlockState above) {
        for (Rule rule : RULES) {
            if (rule.group.contains(above)) { return rule; }
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onBreak(BlockEvent.BreakEvent event) {
        World world = event.getWorld();
        if (clearing || ContentCarry.breaking() || ContentCarry.probing() || world.isRemote || !(event.getPlayer() instanceof EntityPlayerMP) || event.getPlayer() instanceof FakePlayer) { return; }
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        if (player.isSpectator() || idle()) { return; }
        BlockPos origin = event.getPos();
        Rule rule = ruleFor(world.getBlockState(origin.up()));
        if (rule == null || rule.unlessSneaking && player.isSneaking() || !rule.includeSelf && rule.group.contains(event.getState())) { return; }
        clearing = true;
        try { clear(world, origin, rule, player); }
        finally { clearing = false; }
    }

    private static void clear(World world, BlockPos origin, Rule rule, EntityPlayerMP player) {
        for (int step = 1; step <= rule.limit; step++) {
            BlockPos pos = origin.up(step);
            if (pos.getY() >= world.getHeight()) { return; }
            IBlockState state = world.getBlockState(pos);
            if (!rule.group.contains(state)) { return; }
            BlockEvent.BreakEvent broken = new BlockEvent.BreakEvent(world, pos, state, player);
            MinecraftForge.EVENT_BUS.post(broken);
            if (broken.isCanceled()) { return; }
            if (!player.isCreative()) { drop(world, pos, state, rule); }
            world.setBlockToAir(pos);
            WorldEvents.breakEffect(world, null, pos, state);
        }
    }

    private static void drop(World world, BlockPos pos, IBlockState state, Rule rule) {
        Block block = state.getBlock();
        if (rule.alternate.isEmpty()) { block.dropBlockAsItem(world, pos, state, 0); }
        else if (world.rand.nextFloat() < rule.chance) { Block.spawnAsEntity(world, pos, rule.alternate.copy()); }
        else { Block.spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(block), 1, block.damageDropped(state))); }
    }
}
