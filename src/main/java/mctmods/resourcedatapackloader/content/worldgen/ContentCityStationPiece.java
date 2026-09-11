package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityStationPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityStationPiece::new;
    private static final int CLEAR = 4;
    private static final int RAISE = 2;
    private static final String LEVEL = "Level";
    private static final String MIDDLE = "Mid";
    private static final String ALONG_X = "AlongX";
    private static final String BED_HALF = "Bed";
    private static final String CAPPED = "Capped";
    private final int level;
    private final int middle;
    private final boolean alongX;
    private final int bedHalf;
    private final boolean capped;

    public ContentCityStationPiece(int fromX, int fromZ, int toX, int toZ, int level, int middle, boolean alongX, int bedHalf) {
        super(TYPE, 0, box(alongX ? fromX - 1 : fromX, alongX ? fromZ : fromZ - 1, alongX ? toX + 1 : toX, alongX ? toZ : toZ + 1, level, middle, alongX, bedHalf));
        this.level = level;
        this.middle = middle;
        this.alongX = alongX;
        this.bedHalf = bedHalf;
        this.capped = true;
    }

    public ContentCityStationPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.middle = tag.getInt(MIDDLE);
        this.alongX = tag.getBoolean(ALONG_X);
        this.bedHalf = tag.getInt(BED_HALF);
        this.capped = tag.getBoolean(CAPPED);
    }

    private static BoundingBox box(int fromX, int fromZ, int toX, int toZ, int level, int middle, boolean alongX, int bedHalf) {
        int reach = bedHalf + ContentCity.platformWidth() + 1;
        int leastX = alongX ? fromX : middle - reach;
        int mostX = alongX ? toX : middle + reach;
        int leastZ = alongX ? middle - reach : fromZ;
        int mostZ = alongX ? middle + reach : toZ;
        return new BoundingBox(leastX, level, leastZ, mostX, level + CLEAR + 1 + RAISE, mostZ);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putInt(MIDDLE, middle);
        tag.putBoolean(ALONG_X, alongX);
        tag.putInt(BED_HALF, bedHalf);
        tag.putBoolean(CAPPED, capped);
    }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BlockState lining = block(ContentCity.railTunnelBlock(true));
        if (lining == null) { return; }
        BlockState platform = stateOr(ContentCity.platformBlock(), lining);
        BlockState light = block(ContentCity.railTunnelLightBlock(true));
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
        int bore = bedHalf + ContentCity.railShoulderWidth(true) + 1;
        int laid = 0;
        int ends = 0;
        for (int row = first; row <= last; row++) {
            for (int across = middle - reach - 1; across <= middle + reach + 1; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                if (!box.isInside(at.set(x, this.level, z))) { continue; }
                if (capped && (row == endLow || row == endHigh)) {
                    int from = Math.abs(across - middle) <= bore ? this.level + ContentCityRailPiece.CLEAR + 2 : this.level;
                    for (int y = from; y <= roof; y++) { level.setBlock(at.set(x, y, z), lining, 2); }
                    ends++;
                    continue;
                }
                if (across == middle - reach - 1 || across == middle + reach + 1) {
                    for (int y = this.level; y <= roof; y++) { level.setBlock(at.set(x, y, z), lining, 2); }
                    laid++;
                    continue;
                }
                boolean added = Math.abs(across - middle) > bedHalf;
                if (added) { level.setBlock(at.set(x, this.level + 1, z), platform, 2); }
                for (int y = this.level + (added ? 2 : CLEAR + 1); y <= roof - 1; y++) { level.setBlock(at.set(x, y, z), air, 2); }
                boolean lamp = light != null && Math.floorMod(row, run) == 0 && (across == middle || Math.abs(across - middle) == reach);
                level.setBlock(at.set(x, roof, z), lamp ? light : lining, 2);
                laid++;
            }
        }
        if (laid + ends > 0) { ContentLog.LOGGER.debug("A subway station chamber opened at {}, {} over {} column(s), {} block(s) of platform each side, its ends walled over {} column(s) around the tunnel", held.minX(), held.minZ(), laid, wide, ends); }
    }

    @Nullable private static BlockState block(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(named));
        return found == null ? null : found.defaultBlockState();
    }

    private static BlockState stateOr(String named, BlockState fallback) {
        BlockState found = block(named);
        return found == null ? fallback : found;
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.enter(level, (box.minX() + box.maxX()) / 2, (box.minZ() + box.maxZ()) / 2);
        try { laid(level, box); }
        finally { CityBiome.leave(); }
    }
}
