package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.plant.TallPlants;
import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public final class ContentBoneMealFlowers {
    private static final String ON = "on";
    private static final String FLOWERS = "flowers";
    private static final String ORE_DICT = "oreDict";
    private static final int TRIES_PER_FLOWER = 8;
    private static final List<String> SPREAD_KEYS = Collections.unmodifiableList(Arrays.asList("requires", ON, FLOWERS, ORE_DICT, "everyMod", "radius", "count"));
    private static final BlockMatchDef GRASS = new BlockMatchDef(new ResourceLocation("minecraft:grass"), -1, Collections.emptyMap());
    private static final List<Rule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentBoneMealFlowers() {}

    private static final class Rule {
        final BlockGroup on;
        final List<IBlockState> fixed;
        final List<String> oreDict;
        final boolean everyMod;
        final int radius;
        final int count;

        Rule(BlockGroup on, List<IBlockState> fixed, List<String> oreDict, boolean everyMod, int radius, int count) {
            this.on = on;
            this.fixed = fixed;
            this.oreDict = oreDict;
            this.everyMod = everyMod;
            this.radius = radius;
            this.count = count;
        }
    }

    private static void reload() {
        RULES.clear();
        Json.eachObject(PackManager.FLOWER_SPREADS, "flower spread", ContentBoneMealFlowers::read);
        if (!RULES.isEmpty()) { Summary.info("content.flowerspreads", "Loaded " + RULES.size() + " flower spread rule(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", SPREAD_KEYS);
        if (!ContentRegistry.available(json, key)) { return; }
        BlockGroup on = new BlockGroup();
        List<BlockMatchDef> targets = json.has(ON) ? ContentParser.matches(key, json, ON) : Collections.singletonList(GRASS);
        for (BlockMatchDef match : targets) { on.add(match, key, ON); }
        List<IBlockState> fixed = new ArrayList<>();
        for (BlockMatchDef match : ContentParser.matches(key, json, FLOWERS)) {
            Block block = ContentStates.block(match.block.toString(), key + " " + FLOWERS);
            if (block != null) { ContentStates.sort(match, block, all -> fixed.addAll(all.getBlockState().getValidStates()), fixed::add, key); }
        }
        List<String> oreDict = Json.strings(json, ORE_DICT);
        boolean everyMod = Json.getBoolean(json, "everyMod", false);
        if (on.isEmpty() || fixed.isEmpty() && oreDict.isEmpty() && !everyMod) {
            ContentLog.LOGGER.error("Flower spread {} needs a registered block under {} and at least one of {}, {} or everyMod, ignoring it", key, ON, FLOWERS, ORE_DICT);
            return;
        }
        RULES.add(new Rule(on, fixed, oreDict, everyMod, MathHelper.clamp(Json.getInt(json, "radius", 3), 1, 8), MathHelper.clamp(Json.getInt(json, "count", 4), 1, 64)));
    }

    private static List<IBlockState> pool(Rule rule) {
        List<IBlockState> states = new ArrayList<>(rule.fixed);
        for (String name : rule.oreDict) {
            for (ItemStack stack : OreDictionary.getOres(name)) { addStack(states, stack); }
        }
        if (rule.everyMod) {
            for (Block block : ForgeRegistries.BLOCKS) {
                if (block instanceof BlockFlower || block instanceof ContentFlowerBlock) { states.addAll(block.getBlockState().getValidStates()); }
            }
        }
        return states;
    }

    private static void addStack(List<IBlockState> states, ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == Blocks.AIR) { return; }
        if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) { states.addAll(block.getBlockState().getValidStates()); }
        else { states.add(ContentStates.of(block, stack.getMetadata())); }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onBonemeal(BonemealEvent event) {
        World world = event.getWorld();
        if (world.isRemote || event.getResult() != Event.Result.DEFAULT) { return; }
        if (GENERATION.stale()) { reload(); }
        if (RULES.isEmpty()) { return; }
        IBlockState target = event.getBlock();
        BlockPos origin = event.getPos();
        if (!(target.getBlock() instanceof IGrowable) || !((IGrowable) target.getBlock()).canGrow(world, origin, target, false)) { return; }
        for (Rule rule : RULES) {
            if (rule.on.contains(target)) {
                spread(world, origin, rule);
                return;
            }
        }
    }

    private static void spread(World world, BlockPos origin, Rule rule) {
        List<IBlockState> states = pool(rule);
        if (states.isEmpty()) { return; }
        int placed = 0;
        for (int attempt = 0; attempt < rule.count * TRIES_PER_FLOWER && placed < rule.count; attempt++) {
            int dx = world.rand.nextInt(rule.radius * 2 + 1) - rule.radius;
            int dz = world.rand.nextInt(rule.radius * 2 + 1) - rule.radius;
            if (dx == 0 && dz == 0 || dx * dx + dz * dz > rule.radius * rule.radius) { continue; }
            BlockPos pos = origin.add(dx, world.rand.nextInt(3), dz);
            if (!world.isBlockLoaded(pos) || !world.isAirBlock(pos)) { continue; }
            IBlockState flower = states.get(world.rand.nextInt(states.size()));
            if (flower.getBlock() instanceof BlockBush && !((BlockBush) flower.getBlock()).canBlockStay(world, pos, flower)) { continue; }
            if (!TallPlants.canPlaceTall(world, pos, flower.getBlock())) { continue; }
            world.setBlockState(pos, flower, BlockFlags.UPDATE_ALL);
            placed++;
        }
    }
}
