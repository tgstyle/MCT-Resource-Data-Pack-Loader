package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentBedrock {
    public static final int MAX_LAYERS = 5;
    private static final int FLAGS = 2 | 16;
    private static final Map<Integer, IBlockState> byDimension = new HashMap<>();
    private static Set<Integer> dimensions;
    private static Set<String> biomeNames;
    private static List<BiomeDictionary.Type> biomeTypes;
    @Nullable private static IBlockState configured;

    private ContentBedrock() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.BEDROCK)) { return false; }
        return ContentControl.flag(ContentControl.BEDROCK, "flatBedrock", Config.worldgen.flatBedrock);
    }

    public static int layers() { return Math.max(1, Math.min(MAX_LAYERS, ContentControl.number(ContentControl.BEDROCK, "bedrockLayers", Config.worldgen.bedrockLayers))); }

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
        if (dimensions == null) { load(); }
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
        boolean filtered = filtered();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (filtered && biomeSkipped(world, baseX + x, baseZ + z)) { continue; }
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
        boolean filtered = filtered();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (filtered && biomeSkipped(world, baseX + x, baseZ + z)) { continue; }
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

    private static boolean filtered() {
        if (biomeNames == null) { load(); }
        return !biomeNames.isEmpty() || !biomeTypes.isEmpty();
    }

    private static boolean biomeSkipped(World world, int x, int z) {
        Biome biome = world.getBiome(new BlockPos(x, 0, z));
        return matches(biome) == ContentControl.flag(ContentControl.BEDROCK, "flatBedrockBiomesAreBlacklist", Config.worldgen.flatBedrockBiomesAreBlacklist);
    }

    private static boolean matches(Biome biome) {
        ResourceLocation name = biome.getRegistryName();
        if (name != null && biomeNames.contains(name.toString().toLowerCase(Locale.ROOT))) { return true; }
        if (biomeNames.contains(ContentBiomeControl.shownName(biome).toLowerCase(Locale.ROOT))) { return true; }
        for (BiomeDictionary.Type type : biomeTypes) {
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
        IBlockState perDimension = byDimension.get(dimension);
        if (perDimension != null) { return perDimension; }
        if (configured != null) { return configured; }
        switch (dimension) {
            case -1: return Objects.requireNonNull(Blocks.NETHERRACK).getDefaultState();
            case 1: return Objects.requireNonNull(Blocks.END_STONE).getDefaultState();
            default: return Objects.requireNonNull(Blocks.STONE).getDefaultState();
        }
    }

    @Nullable private static IBlockState state(String name) {
        if (name.isEmpty()) { return null; }
        ResourceLocation location = new ResourceLocation(name);
        Block block = ForgeRegistries.BLOCKS.containsKey(location) ? ForgeRegistries.BLOCKS.getValue(location) : null;
        if (block == null) {
            ContentLog.LOGGER.error("Unknown bedrock filler {}, falling back to the block that suits each dimension", location);
            return null;
        }
        return block.getDefaultState();
    }

    private static void load() {
        dimensions = new HashSet<>();
        for (int dimension : ContentControl.numbers(ContentControl.BEDROCK, "flatBedrockDimensions", Config.worldgen.flatBedrockDimensions)) { dimensions.add(dimension); }
        biomeNames = new HashSet<>();
        for (String name : ContentControl.list(ContentControl.BEDROCK, "flatBedrockBiomes", Config.worldgen.flatBedrockBiomes)) { biomeNames.add(name.trim().toLowerCase(Locale.ROOT)); }
        biomeTypes = new ArrayList<>();
        for (String name : ContentControl.list(ContentControl.BEDROCK, "flatBedrockBiomeTypes", Config.worldgen.flatBedrockBiomeTypes)) { biomeTypes.add(BiomeDictionary.Type.getType(name.trim())); }
        for (String entry : ContentControl.list(ContentControl.BEDROCK, "flatBedrockFillers", Config.worldgen.flatBedrockFillers)) {
            int split = entry.indexOf('=');
            if (split < 1) {
                ContentLog.LOGGER.error("flatBedrockFillers entry '{}' is not written as dimension=block, ignoring it", entry);
                continue;
            }
            String number = entry.substring(0, split).trim();
            IBlockState state = state(entry.substring(split + 1).trim());
            if (state == null) { continue; }
            try { byDimension.put(Integer.parseInt(number), state); }
            catch (NumberFormatException ex) { ContentLog.LOGGER.error("flatBedrockFillers entry '{}' does not start with a dimension number, ignoring it", entry); }
        }
        configured = state(ContentControl.text(ContentControl.BEDROCK, "flatBedrockFiller", Config.worldgen.flatBedrockFiller).trim());
        Summary.info("bedrock", "Flattening bedrock to " + layers() + " layer(s) in "
                + (dimensions.isEmpty() ? "every dimension" : "dimension(s) " + dimensions));
    }
}
