package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.world.BiomeNames;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.math.MathHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentBedrock {
    public static final int MAX_LAYERS = 5;
    private static final int FLAGS = 2 | 16;
    private static final TemplateMemo<Rules> RULES = new TemplateMemo<>();

    private static final class Rules {
        final Set<Integer> dimensions = new HashSet<>();
        final Set<String> biomeNames = Settings.lower(ContentControl.list(ContentControl.BEDROCK, "flatBedrockBiomes", Config.worldgen.flatBedrockBiomes));
        final List<BiomeDictionary.Type> biomeTypes = new ArrayList<>();
        final Map<Integer, IBlockState> byDimension = new HashMap<>();
        @Nullable final IBlockState configured;

        Rules() {
            for (int dimension : ContentControl.numbers(ContentControl.BEDROCK, "flatBedrockDimensions", Config.worldgen.flatBedrockDimensions)) { dimensions.add(dimension); }
            for (String name : ContentControl.list(ContentControl.BEDROCK, "flatBedrockBiomeTypes", Config.worldgen.flatBedrockBiomeTypes)) { biomeTypes.add(BiomeDictionary.Type.getType(name.trim())); }
            for (String entry : ContentControl.list(ContentControl.BEDROCK, "flatBedrockFillers", Config.worldgen.flatBedrockFillers)) {
                String[] parts = Settings.pair(entry, "flatBedrockFillers", "dimension=block");
                if (parts == null) { continue; }
                String number = parts[0];
                IBlockState state = state(parts[1]);
                if (state == null) { continue; }
                try { byDimension.put(Integer.parseInt(number), state); }
                catch (NumberFormatException ex) { ContentLog.LOGGER.error("flatBedrockFillers entry '{}' does not start with a dimension number, ignoring it", entry); }
            }
            configured = state(ContentControl.text(ContentControl.BEDROCK, "flatBedrockFiller", Config.worldgen.flatBedrockFiller).trim());
            Summary.info("bedrock", "Flattening bedrock to " + layers() + " layer(s) in "
                    + (dimensions.isEmpty() ? "every dimension" : "dimension(s) " + dimensions));
        }
    }

    private static Rules rules() { return RULES.get(Rules::new); }

    private ContentBedrock() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.BEDROCK)) { return false; }
        return ContentControl.flag(ContentControl.BEDROCK, "flatBedrock", Config.worldgen.flatBedrock);
    }

    public static int layers() { return MathHelper.clamp(ContentControl.number(ContentControl.BEDROCK, "bedrockLayers", Config.worldgen.bedrockLayers), 1, MAX_LAYERS); }

    public static boolean roofWanted() { return ContentControl.flag(ContentControl.BEDROCK, "flatBedrockRoof", Config.worldgen.flatBedrockRoof); }

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onPopulate(PopulateChunkEvent.Pre event) {
        if (!enabled()) { return; }
        World world = event.getWorld();
        if (world.isRemote) { return; }
        int dimension = world.provider.getDimension();
        if (!appliesTo(dimension)) { return; }
        flatten(world, event.getChunkX(), event.getChunkZ(), dimension);
    }

    public static boolean appliesTo(int dimension) {
        Set<Integer> dimensions = rules().dimensions;
        if (dimensions.isEmpty()) { return true; }
        return dimensions.contains(dimension) != ContentControl.flag(ContentControl.BEDROCK, "flatBedrockDimensionsAreBlacklist", Config.worldgen.flatBedrockDimensionsAreBlacklist);
    }

    public static void flatten(World world, int chunkX, int chunkZ, int dimension) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;
        int layers = layers();
        if (voidWorld(world, baseX, baseZ)) { return; }
        IBlockState filler = filler(dimension);
        IBlockState bedrock = Blocks.BEDROCK.getDefaultState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Rules rules = rules();
        boolean filtered = filtered(rules);
        boolean blacklist = ContentControl.flag(ContentControl.BEDROCK, "flatBedrockBiomesAreBlacklist", Config.worldgen.flatBedrockBiomesAreBlacklist);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (filtered && biomeSkipped(world, pos, baseX + x, baseZ + z, rules, blacklist)) { continue; }
                for (int y = MAX_LAYERS - 1; y >= layers; y--) {
                    pos.setPos(baseX + x, y, baseZ + z);
                    if (world.getBlockState(pos).getBlock() == Blocks.BEDROCK) { world.setBlockState(pos, filler, FLAGS); }
                }
                for (int y = layers - 1; y >= 0; y--) {
                    pos.setPos(baseX + x, y, baseZ + z);
                    if (world.getBlockState(pos).getBlock() != Blocks.BEDROCK) { world.setBlockState(pos, bedrock, FLAGS); }
                }
            }
        }
        if (roofWanted()) { roof(world, baseX, baseZ, layers, filler, bedrock, pos); }
    }

    private static void roof(World world, int baseX, int baseZ, int layers, IBlockState filler, IBlockState bedrock, BlockPos.MutableBlockPos pos) {
        int top = world.getActualHeight() - 1;
        pos.setPos(baseX, top, baseZ);
        if (world.getBlockState(pos).getBlock() != Blocks.BEDROCK) { return; }
        Rules rules = rules();
        boolean filtered = filtered(rules);
        boolean blacklist = ContentControl.flag(ContentControl.BEDROCK, "flatBedrockBiomesAreBlacklist", Config.worldgen.flatBedrockBiomesAreBlacklist);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (filtered && biomeSkipped(world, pos, baseX + x, baseZ + z, rules, blacklist)) { continue; }
                for (int y = top - MAX_LAYERS + 1; y <= top - layers; y++) {
                    pos.setPos(baseX + x, y, baseZ + z);
                    if (world.getBlockState(pos).getBlock() == Blocks.BEDROCK) { world.setBlockState(pos, filler, FLAGS); }
                }
                for (int y = top - layers + 1; y <= top; y++) {
                    pos.setPos(baseX + x, y, baseZ + z);
                    if (world.getBlockState(pos).getBlock() != Blocks.BEDROCK) { world.setBlockState(pos, bedrock, FLAGS); }
                }
            }
        }
    }

    private static boolean filtered(Rules rules) { return !rules.biomeNames.isEmpty() || !rules.biomeTypes.isEmpty(); }

    private static boolean biomeSkipped(World world, BlockPos.MutableBlockPos pos, int x, int z, Rules rules, boolean blacklist) { return matches(rules, world.getBiome(pos.setPos(x, 0, z))) == blacklist; }

    private static boolean matches(Rules rules, Biome biome) {
        if (BiomeNames.named(biome, rules.biomeNames)) { return true; }
        for (BiomeDictionary.Type type : rules.biomeTypes) {
            if (BiomeDictionary.hasType(biome, type)) { return true; }
        }
        return false;
    }

    private static boolean voidWorld(World world, int baseX, int baseZ) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int offset = 0; offset < 16; offset += 5) {
            pos.setPos(baseX + offset, 0, baseZ + offset);
            if (world.getBlockState(pos).getBlock() == Blocks.BEDROCK) { return false; }
        }
        return true;
    }

    private static IBlockState filler(int dimension) {
        Rules rules = rules();
        IBlockState perDimension = rules.byDimension.get(dimension);
        if (perDimension != null) { return perDimension; }
        if (rules.configured != null) { return rules.configured; }
        switch (dimension) {
            case -1: return Objects.requireNonNull(Blocks.NETHERRACK).getDefaultState();
            case 1: return Objects.requireNonNull(Blocks.END_STONE).getDefaultState();
            default: return Objects.requireNonNull(Blocks.STONE).getDefaultState();
        }
    }

    @Nullable private static IBlockState state(String name) {
        if (name.isEmpty()) { return null; }
        IBlockState parsed = ContentStates.parse(name, "flatBedrockFillers");
        if (parsed == null) { ContentLog.LOGGER.error("Unknown bedrock filler {}, falling back to the block that suits each dimension", name); }
        return parsed;
    }

}
