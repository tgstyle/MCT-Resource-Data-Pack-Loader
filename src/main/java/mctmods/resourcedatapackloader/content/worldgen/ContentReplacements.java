package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentReplacements {
    private static final int FLAGS = 2 | 16;
    private static final Blocked REPLACED = new Blocked();
    private static final Set<String> REPORTED = new HashSet<>();
    private static final Map<IBlockState, IBlockState> EXACT = new HashMap<>();
    private static final Map<Block, IBlockState> WHOLE = new LinkedHashMap<>();
    private static Set<Integer> dimensions;
    private static String token;
    private static boolean wanted;
    private static int minHeight;
    private static int maxHeight;
    private static int chunks;

    private ContentReplacements() {}

    public static boolean wanted() {
        if (dimensions == null) { load(); }

        return wanted;
    }

    public static String token() {
        if (dimensions == null) { load(); }

        return token;
    }

    public static boolean appliesTo(int dimension) {
        if (dimensions == null) { load(); }
        if (dimensions.isEmpty()) { return true; }

        return dimensions.contains(dimension) != ContentControl.flag(ContentControl.REPLACEMENTS, "blockReplacementDimensionsAreBlacklist", Config.worldgen.blockReplacementDimensionsAreBlacklist);
    }

    public static int replace(World world, int chunkX, int chunkZ) {
        if (dimensions == null) { load(); }
        if (!wanted) { return 0; }

        Chunk chunk = world.getChunk(chunkX, chunkZ);
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        int replaced = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (ExtendedBlockStorage section : chunk.getBlockStorageArray()) {
            if (section == Chunk.NULL_BLOCK_STORAGE) { continue; }

            int bottom = section.getYLocation();
            if (bottom > maxHeight || bottom + 15 < minHeight) { continue; }

            for (int y = 0; y < 16; y++) {
                int worldY = bottom + y;
                if (worldY < minHeight || worldY > maxHeight) { continue; }

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        IBlockState found = section.get(x, y, z);
                        IBlockState swap = replacementFor(found);
                        if (swap == null) { continue; }

                        pos.setPos(baseX + x, worldY, baseZ + z);
                        world.setBlockState(pos, swap, FLAGS);
                        count(found, swap);
                        replaced++;
                    }
                }
            }
        }

        if (replaced > 0) { chunks++; }
        return replaced;
    }

    public static void report() {
        if (REPLACED.total() == 0) { return; }

        Summary.info("replacements", "Replaced " + REPLACED.total() + " block(s) in " + chunks + " chunk(s) that already existed");
        if (ContentControl.flag(ContentControl.REPLACEMENTS, "logBlockReplacements", Config.worldgen.logBlockReplacements)) {
            for (Map.Entry<String, Integer> entry : REPLACED.map().entrySet()) { ContentLog.LOGGER.info("  replaced {} {}", entry.getValue(), entry.getKey()); }
        }
    }

    public static void reload() {
        dimensions = null;
        EXACT.clear();
        WHOLE.clear();
        REPORTED.clear();
        REPLACED.clear();
        chunks = 0;
    }

    @Nullable private static IBlockState replacementFor(@Nullable IBlockState found) {
        if (found == null) { return null; }

        IBlockState swap = WHOLE.get(found.getBlock());
        if (swap == null && !EXACT.isEmpty()) { swap = EXACT.get(found); }
        if (swap == null || swap == found) { return null; }

        return swap;
    }

    private static void count(IBlockState found, IBlockState wanted) {
        String key = name(found) + " to " + name(wanted);
        REPLACED.count(key);
        if (!ContentControl.flag(ContentControl.REPLACEMENTS, "logBlockReplacements", Config.worldgen.logBlockReplacements)) { return; }
        if (REPORTED.add(key)) { ContentLog.LOGGER.info("Replacing {} in chunks that already exist", key); }
    }

    private static String name(IBlockState state) {
        ResourceLocation registry = state.getBlock().getRegistryName();
        return (registry == null ? "unknown" : registry.toString()) + ":" + state.getBlock().getMetaFromState(state);
    }

    private static void load() {
        if (ContentControl.off(ContentControl.REPLACEMENTS)) {
            dimensions = new HashSet<>();
            token = "replace:off";
            wanted = false;
            return;
        }

        dimensions = new HashSet<>();
        for (int dimension : ContentControl.numbers(ContentControl.REPLACEMENTS, "blockReplacementDimensions", Config.worldgen.blockReplacementDimensions)) { dimensions.add(dimension); }

        minHeight = Math.max(0, ContentControl.number(ContentControl.REPLACEMENTS, "blockReplacementMinHeight", Config.worldgen.blockReplacementMinHeight));
        maxHeight = Math.min(255, ContentControl.number(ContentControl.REPLACEMENTS, "blockReplacementMaxHeight", Config.worldgen.blockReplacementMaxHeight));
        maxHeight = Math.max(minHeight, maxHeight);

        EXACT.clear();
        WHOLE.clear();
        for (String entry : ContentControl.list(ContentControl.REPLACEMENTS, "blockReplacements", Config.worldgen.blockReplacements)) {
            int split = entry.indexOf('=');
            if (split < 1) {
                ContentLog.LOGGER.error("blockReplacements entry '{}' is not written as block=block, ignoring it", entry);
                continue;
            }
            String from = entry.substring(0, split).trim();
            IBlockState wanted = state(entry.substring(split + 1).trim(), entry);
            if (wanted == null) { continue; }

            Block block = block(from, entry);
            if (block == null) { continue; }

            int meta = meta(from);
            if (meta < 0) { WHOLE.put(block, wanted); }
            else { EXACT.put(ContentStates.of(block, meta), wanted); }
        }

        wanted = !EXACT.isEmpty() || !WHOLE.isEmpty();
        token = "replace:" + EXACT.keySet().hashCode() + ":" + WHOLE.keySet().hashCode() + "#" + ContentControl.text(ContentControl.REPLACEMENTS, "blockReplacementKey", Config.worldgen.blockReplacementKey).trim();
        if (wanted) { Summary.info("replacements.setup", "Replacing " + (EXACT.size() + WHOLE.size()) + " kind(s) of block in chunks that already exist, between y" + minHeight + " and y" + maxHeight); }
    }

    @Nullable private static IBlockState state(String name, String entry) {
        Block block = block(name, entry);
        if (block == null) { return null; }

        int meta = meta(name);
        return meta < 0 ? block.getDefaultState() : ContentStates.of(block, meta);
    }

    @Nullable private static Block block(String name, String entry) {
        String plain = name.toLowerCase(Locale.ROOT);
        int split = plain.lastIndexOf(':');
        if (split > 0 && isNumber(plain.substring(split + 1))) { plain = plain.substring(0, split); }
        if (plain.isEmpty()) {
            ContentLog.LOGGER.error("blockReplacements entry '{}' is missing a block name, ignoring it", entry);
            return null;
        }

        ResourceLocation location = new ResourceLocation(plain);
        Block block = ForgeRegistries.BLOCKS.containsKey(location) ? ForgeRegistries.BLOCKS.getValue(location) : null;
        if (block == null) { ContentLog.LOGGER.error("blockReplacements entry '{}' names {}, which no mod registers, ignoring it", entry, location); }

        return block;
    }

    private static int meta(String name) {
        int split = name.lastIndexOf(':');
        if (split < 1 || !isNumber(name.substring(split + 1))) { return -1; }

        return Integer.parseInt(name.substring(split + 1));
    }

    private static boolean isNumber(String value) {
        if (value.isEmpty()) { return false; }

        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) { return false; }
        }
        return true;
    }
}
