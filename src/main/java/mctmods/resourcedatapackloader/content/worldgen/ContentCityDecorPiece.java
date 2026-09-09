package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.Registered;

import mctmods.resourcedatapackloader.util.ContentLog;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public final class ContentCityDecorPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityDecorPiece::new;
    private static final int FOOTING = 3;
    private static final String ALONG_X = "AlongX";
    private static final String SPOTS = "Spots";
    private static final String LEVELS = "Levels";
    private static final String ACROSS = "Across";
    private final boolean alongX;
    private final int[] spots;
    private final int[] levels;
    private final int[] across;

    public ContentCityDecorPiece(boolean alongX, int[] spots, int[] levels, int[] across, BoundingBox box) {
        super(TYPE, 0, box);
        this.alongX = alongX;
        this.spots = spots;
        this.levels = levels;
        this.across = across;
    }

    public ContentCityDecorPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.alongX = tag.getBoolean(ALONG_X);
        this.spots = tag.getIntArray(SPOTS);
        this.levels = tag.getIntArray(LEVELS);
        this.across = tag.getIntArray(ACROSS);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putBoolean(ALONG_X, alongX);
        tag.putIntArray(SPOTS, spots);
        tag.putIntArray(LEVELS, levels);
        tag.putIntArray(ACROSS, across);
    }

    private static boolean paved(BlockState laid) {
        for (String named : new String[] { ContentCity.paving(), ContentCity.sidewalkBlock(), ContentCity.lineBlock(), ContentCity.centerBlock(), ContentCity.bridgeBlock() }) {
            if (named.isEmpty()) { continue; }
            ResourceLocation key = ResourceLocation.tryParse(named);
            Block found = key == null ? null : Registered.find(ForgeRegistries.BLOCKS, key);
            if (found != null && laid.is(found)) { return true; }
        }
        return laid.is(Blocks.DIRT_PATH);
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int outside = 0;
        int bare = 0;
        int paved = 0;
        int blocked = 0;
        int laid = 0;
        for (int spot = 0; spot < spots.length; spot++) {
            int x = alongX ? spots[spot] : across[spot];
            int z = alongX ? across[spot] : spots[spot];
            at.set(x, levels[spot], z);
            if (!box.isInside(at)) {
                outside++;
                continue;
            }
            int ground = levels[spot];
            while (ground > levels[spot] - FOOTING && level.getBlockState(at.set(x, ground, z)).isAir()) { ground--; }
            if (level.getBlockState(at.set(x, ground, z)).isAir()) {
                bare++;
                continue;
            }
            if (paved(level.getBlockState(at))) {
                paved++;
                continue;
            }
            at.set(x, ground + 1, z);
            if (!level.getBlockState(at).isAir()) {
                blocked++;
                continue;
            }
            laid++;
            RandomSource roll = RandomSource.create(ContentCityBlocks.spot(x, z));
            String named = ContentCity.decor(roll);
            if (named == null) { continue; }
            ContentWorldgen.Entry entry = ContentWorldgen.byName(named);
            if (entry == null) {
                ContentCity.missingDecor(named);
                continue;
            }
            entry.shape().generate(new ContentPlacer(level, entry.palette(), chunk), roll, at.immutable());
        }
        ContentLog.LOGGER.debug("A verge of {} spot(s) scattered {}: {} outside the chunk, {} over air, {} over paving, {} blocked above", spots.length, laid, outside, bare, paved, blocked);
    }
}
