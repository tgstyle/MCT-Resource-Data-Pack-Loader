package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;

public final class ContentCityStationPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityStationPiece::new;
    public static final int OPEN = Integer.MIN_VALUE;
    private static final int CLEAR = CityRails.CLEAR;
    private static final int RAISE = 2;
    private static final String LEVEL = "Level";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String BED_HALF = "Bed";
    private static final String CAPPED = "Capped";
    private static final String LOW_BORE = "LowBore";
    private static final String HIGH_BORE = "HighBore";
    private static final String BENCH = "Bench";
    private static final String BENCH_WAY = "BenchWay";
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final int bedHalf;
    private final int lowBore;
    private final int highBore;
    private final int bench;
    private final int benchWay;

    public ContentCityStationPiece(int from, int to, int level, int middle, boolean alongX, int bedHalf, int lowBore, int highBore, int bench, int benchWay) {
        super(TYPE, 0, box(from - 1, to + 1, level, middle, alongX, bedHalf));
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.bedHalf = bedHalf;
        this.lowBore = lowBore;
        this.highBore = highBore;
        this.bench = bench;
        this.benchWay = benchWay;
    }

    public ContentCityStationPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.bedHalf = tag.getInt(BED_HALF);
        boolean capped = tag.getBoolean(CAPPED);
        this.lowBore = tag.contains(LOW_BORE) ? tag.getInt(LOW_BORE) : capped ? level : OPEN;
        this.highBore = tag.contains(HIGH_BORE) ? tag.getInt(HIGH_BORE) : capped ? level : OPEN;
        this.bench = tag.getInt(BENCH);
        this.benchWay = tag.getInt(BENCH_WAY);
    }

    private static BoundingBox box(int from, int to, int level, int middle, boolean alongX, int bedHalf) {
        int reach = bedHalf + ContentCity.platformWidth() + 1;
        return alongX ? new BoundingBox(from, level, middle - reach, to, level + CLEAR + 1 + RAISE, middle + reach) : new BoundingBox(middle - reach, level, from, middle + reach, level + CLEAR + 1 + RAISE, to);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putInt(BED_HALF, bedHalf);
        tag.putInt(LOW_BORE, lowBore);
        tag.putInt(HIGH_BORE, highBore);
        tag.putInt(BENCH, bench);
        tag.putInt(BENCH_WAY, benchWay);
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        long seed = level.getSeed();
        CityPalette linings = CityPalette.of(ContentCity.railTunnelBlock(true), Blocks.AIR.defaultBlockState());
        CityPalette platforms = ContentCity.platformBlock().isEmpty() ? linings : CityPalette.of(ContentCity.platformBlock(), linings.first());
        BlockState light = CityPalette.state(ContentCity.railTunnelLightBlock(true));
        BlockState air = Blocks.AIR.defaultBlockState();
        int run = ContentCity.railTunnelLightRun(true);
        int wide = ContentCity.platformWidth();
        int reach = bedHalf + wide;
        int roof = this.level + CLEAR + 1 + RAISE;
        BoundingBox held = getBoundingBox();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int first = alongX ? Math.max(held.minX(), box.minX()) : Math.max(held.minZ(), box.minZ());
        int last = alongX ? Math.min(held.maxX(), box.maxX()) : Math.min(held.maxZ(), box.maxZ());
        int endLow = alongX ? held.minX() : held.minZ();
        int endHigh = alongX ? held.maxX() : held.maxZ();
        int laid = 0;
        int ends = 0;
        for (int row = first; row <= last; row++) {
            boolean end = row == endLow || row == endHigh;
            int bore = row == endLow ? lowBore : highBore;
            if (end && bore == OPEN) { continue; }
            for (int across = middle - reach - 1; across <= middle + reach + 1; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                if (!box.isInside(at.set(x, this.level, z))) { continue; }
                if (end) {
                    int from = Math.abs(across - middle) <= bedHalf ? Math.min(this.level, bore) + CLEAR + 2 : this.level;
                    for (int y = from; y <= roof; y++) { level.setBlock(at.set(x, y, z), linings.pick(seed, x, y, z), 2); }
                    ends++;
                    continue;
                }
                if (across == middle - reach - 1 || across == middle + reach + 1) {
                    for (int y = this.level; y <= roof; y++) { level.setBlock(at.set(x, y, z), linings.pick(seed, x, y, z), 2); }
                    laid++;
                    continue;
                }
                boolean added = Math.abs(across - middle) > bedHalf;
                if (added) { level.setBlock(at.set(x, this.level + 1, z), platforms.pick(seed, x, this.level + 1, z), 2); }
                for (int y = this.level + (added ? 2 : CLEAR + 1); y <= roof - 1; y++) { level.setBlock(at.set(x, y, z), air, 2); }
                boolean lamp = light != null && Math.floorMod(row, run) == 0 && (across == middle || Math.abs(across - middle) == reach);
                level.setBlock(at.set(x, roof, z), lamp ? light : linings.pick(seed, x, roof, z), 2);
                laid++;
            }
        }
        int seated = benchWay == 0 ? 0 : ContentCityStairsPiece.platformBench(level, box, alongX, bench, middle, this.level, bedHalf, benchWay, at);
        if (laid + ends + seated > 0) { ContentLog.LOGGER.debug("A subway station chamber opened at {}, {} over {} column(s), {} block(s) of platform each side, its ends walled over {} column(s) around the tunnel, {} bench block(s) on the platform", held.minX(), held.minZ(), laid, wide, ends, seated); }
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, box));
    }
}
