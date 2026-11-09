package mctmods.resourcedatapackloader.common.blocks.plant;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParser;
import mctmods.resourcedatapackloader.common.util.plant.TallPlants;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WitherRoseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

public final class ContentBoneMealFlowers {
    private static final String ON = "on";
    private static final String FLOWERS = "flowers";
    private static final String TAG = "tag";
    private static final List<Rule> RULES = new ArrayList<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentBoneMealFlowers() {}

    private record Rule(BlockGroup on, List<BlockState> fixed, List<TagKey<Block>> tags, boolean everyMod, int radius, int count) {}

    private static void reload() {
        RULES.clear();
        GENERATION.stale();
        Json.eachObject(PackManager.FLOWER_SPREADS, "flower spread", ContentBoneMealFlowers::read);
        if (!RULES.isEmpty()) { Summary.info("content.flowerspreads", "Loaded " + RULES.size() + " flower spread rule(s)"); }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!ContentRegistry.available(Json.strings(json, "requires"), key)) { return; }
        String owner = "Flower spread " + key;
        BlockGroup on = new BlockGroup();
        if (json.has(ON)) {
            for (BlockMatchDef match : ContentParser.matches(key, json, ON)) { on.add(match, owner, ON); }
        }
        else { on.add(new BlockMatchDef(ResourceLocation.fromNamespaceAndPath("minecraft", "grass_block"), Map.of()), owner, ON); }
        if (on.isEmpty()) {
            ContentLog.LOGGER.error("{} names no registered blocks under '{}', ignoring it", owner, ON);
            return;
        }
        Set<BlockState> fixed = new LinkedHashSet<>();
        for (BlockMatchDef match : ContentParser.matches(key, json, FLOWERS)) {
            ContentStates.bind(match, owner, FLOWERS, all -> fixed.addAll(all.getStateDefinition().getPossibleStates()), fixed::add);
        }
        if (json.has("oreDict")) { ContentLog.LOGGER.warn("{} uses 'oreDict', which this line does not read: write the block tags under '{}'", owner, TAG); }
        List<TagKey<Block>> tags = new ArrayList<>();
        for (String name : Json.strings(json, TAG)) {
            ResourceLocation id = Registered.tagId(name);
            if (id == null) { ContentLog.LOGGER.error("{} names '{}' under '{}', which is not a valid tag id, leaving it out", owner, name, TAG); }
            else { tags.add(TagKey.create(Registries.BLOCK, id)); }
        }
        boolean everyMod = Json.getBoolean(json, "everyMod", false);
        if (fixed.isEmpty() && tags.isEmpty() && !everyMod) {
            ContentLog.LOGGER.error("{} names no flower source under '{}', '{}' or 'everyMod', ignoring it", owner, FLOWERS, TAG);
            return;
        }
        RULES.add(new Rule(on, new ArrayList<>(fixed), tags, everyMod, Mth.clamp(Json.getInt(json, "radius", 3), 1, 8), Mth.clamp(Json.getInt(json, "count", 4), 1, 64)));
    }

    private static List<BlockState> pool(Rule rule) {
        Set<BlockState> states = new LinkedHashSet<>(rule.fixed());
        for (TagKey<Block> tag : rule.tags()) {
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) { states.add(holder.value().defaultBlockState()); }
        }
        if (rule.everyMod()) {
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.SMALL_FLOWERS)) {
                if (!(holder.value() instanceof WitherRoseBlock)) { states.add(holder.value().defaultBlockState()); }
            }
        }
        return new ArrayList<>(states);
    }

    public static void onBonemeal(BonemealEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !event.isValidBonemealTarget()) { return; }
        if (GENERATION.stale()) { reload(); }
        if (RULES.isEmpty()) { return; }
        BlockState target = event.getState();
        for (Rule rule : RULES) {
            if (rule.on().contains(target)) {
                spread(level, event.getPos(), rule);
                return;
            }
        }
    }

    private static void spread(ServerLevel level, BlockPos origin, Rule rule) {
        List<BlockState> pool = pool(rule);
        if (pool.isEmpty()) { return; }
        RandomSource random = level.getRandom();
        int radius = rule.radius();
        int placed = 0;
        for (int tries = 0; tries < rule.count() * 8 && placed < rule.count(); tries++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            if (dx == 0 && dz == 0 || dx * dx + dz * dz > radius * radius) { continue; }
            BlockPos pos = origin.offset(dx, random.nextInt(3), dz);
            BlockState flower = pool.get(random.nextInt(pool.size()));
            if (level.isLoaded(pos) && level.getBlockState(pos).isAir() && flower.canSurvive(level, pos) && TallPlants.canPlaceTall(level, pos, flower.getBlock()) && level.setBlock(pos, flower, Block.UPDATE_ALL)) { placed++; }
        }
    }
}
