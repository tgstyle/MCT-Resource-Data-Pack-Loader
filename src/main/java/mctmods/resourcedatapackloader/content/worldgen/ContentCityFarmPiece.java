package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraftforge.common.world.PieceBeardifierModifier;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentCityFarmPiece extends StructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentCityFarmPiece::new;
    private static final int FOOTING = 4;
    private static final String LEVEL = "Level";
    private static final String EDGE = "Edge";
    private static final String SOIL = "Soil";
    private static final String GROUND = "Ground";
    private static final String CROPS = "Crops";
    private static final String WATER = "Water";
    private static final String ROW = "Row";
    private static final String PLOT = "Plot";
    private final int level;
    private final String edge;
    private final String soil;
    private final String ground;
    private final String crops;
    private final boolean water;
    private final int row;
    private final String plot;

    public ContentCityFarmPiece(int fromX, int fromZ, int toX, int toZ, int level, int height, String edge, String soil, String ground, String crops, boolean water, int row, String plot) {
        super(TYPE, 0, new BoundingBox(fromX, level, fromZ, toX, level + height, toZ));
        this.level = level;
        this.edge = edge;
        this.soil = soil;
        this.ground = ground;
        this.crops = crops;
        this.water = water;
        this.row = row;
        this.plot = plot;
    }

    public ContentCityFarmPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.level = tag.getInt(LEVEL);
        this.edge = tag.getString(EDGE);
        this.soil = tag.getString(SOIL);
        this.ground = tag.getString(GROUND);
        this.crops = tag.getString(CROPS);
        this.water = tag.getBoolean(WATER);
        this.row = tag.getInt(ROW);
        this.plot = tag.getString(PLOT);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putInt(LEVEL, level);
        tag.putString(EDGE, edge);
        tag.putString(SOIL, soil);
        tag.putString(GROUND, ground);
        tag.putString(CROPS, crops);
        tag.putBoolean(WATER, water);
        tag.putInt(ROW, row);
        tag.putString(PLOT, plot);
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        BlockState fence = stateOr(edge, Blocks.OAK_LOG.defaultBlockState());
        BlockState tilled = stateOr(soil, Blocks.FARMLAND.defaultBlockState());
        BlockState under = stateOr(ground, Blocks.DIRT.defaultBlockState());
        BlockState pond = Blocks.WATER.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BoundingBox held = getBoundingBox();
        int step = water ? row + 1 : row;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = Math.max(held.minX(), box.minX()); x <= Math.min(held.maxX(), box.maxX()); x++) {
            for (int z = Math.max(held.minZ(), box.minZ()); z <= Math.min(held.maxZ(), box.maxZ()); z++) {
                boolean rim = x == held.minX() || x == held.maxX() || z == held.minZ() || z == held.maxZ();
                boolean channel = !rim && water && Math.floorMod(x - held.minX() - 1, step) == row;
                BlockState laid = rim ? fence : channel ? pond : tilled;
                at.set(x, this.level, z);
                level.setBlock(at, laid, 2);
                for (int up = 1; up <= held.maxY() - this.level; up++) {
                    at.set(x, this.level + up, z);
                    if (!level.getBlockState(at).isAir()) { level.setBlock(at, air, 2); }
                }
                if (!rim && !channel) {
                    at.set(x, this.level + 1, z);
                    BlockState grown = crop(x, z);
                    if (grown != null && box.isInside(at)) { level.setBlock(at, grown, 2); }
                }
                for (int down = 1; down <= FOOTING; down++) {
                    at.set(x, this.level - down, z);
                    if (!box.isInside(at)) { break; }
                    BlockState below = level.getBlockState(at);
                    if (!below.isAir() && below.getFluidState().isEmpty()) { break; }
                    level.setBlock(at, under, 2);
                }
            }
        }
        VillageDef def = ContentVillages.byKey(plot);
        if (def == null) { return; }
        ContentCity.residents(level, def, box, index -> new BlockPos(boundingBox.minX() + def.villagerX() + index, boundingBox.minY() + def.villagerY(), boundingBox.minZ() + def.villagerZ()));
    }

    @Nullable private BlockState crop(int x, int z) {
        if (crops.isEmpty()) { return ripe(Blocks.WHEAT.defaultBlockState()); }
        String[] names = crops.split(",");
        String named = names[Math.floorMod((int) mix(x, z), names.length)].trim();
        BlockState grown = block(named);
        return grown == null ? null : ripe(grown);
    }

    private static BlockState ripe(BlockState grown) {
        if (!grown.hasProperty(BlockStateProperties.AGE_7)) { return grown; }
        return grown.setValue(BlockStateProperties.AGE_7, 7);
    }

    private static long mix(int x, int z) {
        long held = x * 0x2545F4914F6CDD1DL ^ z * 0xCBF29CE484222325L;
        held ^= held >>> 33;
        held *= 0xFF51AFD7ED558CCDL;
        return held ^ (held >>> 33);
    }

    @Nullable private static BlockState block(String named) {
        if (named.isEmpty()) { return null; }
        Block found = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(named));
        return found == null ? null : found.defaultBlockState();
    }

    private static BlockState stateOr(String named, BlockState fallback) {
        BlockState found = block(named);
        return found == null ? fallback : found;
    }

    @Override @Nonnull public BoundingBox getBeardifierBox() {
        BoundingBox held = getBoundingBox();
        return new BoundingBox(held.minX(), level, held.minZ(), held.maxX(), level, held.maxZ());
    }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.BEARD_THIN; }

    @Override public int getGroundLevelDelta() { return 0; }
}
