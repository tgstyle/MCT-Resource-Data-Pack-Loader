package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.def.FollowDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import javax.annotation.Nullable;

public final class ContentOreSigns {
    private ContentOreSigns() {}

    public static void place(ContentPlacer placer, WorldgenDef def, RandomSource random, BlockPos origin) {
        if (def.indicators().isEmpty()) { return; }
        WorldGenLevel level = placer.level();
        int count = def.indicatorCount().pick(random);
        int reach = footprint(def) + def.indicatorSpread();
        int laid = 0;
        int lowX = square(placer.lowX(), placer.highX());
        int lowZ = square(placer.lowZ(), placer.highZ());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = 0; i < count; i++) {
            int x = Mth.clamp(origin.getX() + random.nextInt(2 * reach + 1) - reach, lowX, lowX + 15);
            int z = Mth.clamp(origin.getZ() + random.nextInt(2 * reach + 1) - reach, lowZ, lowZ + 15);
            String chosen = PickDef.pick(def.indicators(), random);
            if (chosen == null || FollowDef.EMPTY.equals(chosen)) { continue; }
            BlockState state = ContentStates.parse(chosen, "indicators");
            if (state == null) {
                ContentLog.LOGGER.error("Worldgen entry {} names {} as an indicator, which is not a registered block, so none is left", def.key(), chosen);
                continue;
            }
            if (!ContentPlacer.loaded(level, at.set(x, 0, z))) { continue; }
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (y <= level.getMinBuildHeight() + 1 || y >= level.getMaxBuildHeight() || !level.getBlockState(at.set(x, y, z)).isAir()) { continue; }
            BlockState under = level.getBlockState(at.set(x, y - 1, z));
            if (!under.isFaceSturdy(level, at, Direction.UP) || !under.getFluidState().isEmpty()) { continue; }
            if (taken(level, at.set(x, y - 1, z))) { continue; }
            if (placer.placeExactly(state, x, y, z)) { laid++; }
        }
        if (laid > 0) { ContentLog.LOGGER.debug("Left {} indicator block(s) on the surface over the {} at {}, {}, {}", laid, def.key(), origin.getX(), origin.getY(), origin.getZ()); }
    }

    private static boolean taken(WorldGenLevel level, BlockPos ground) {
        Registry<Structure> structures = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (StructurePiece piece : ContentCityClaim.pieces(level, ground, structure -> structure instanceof ContentCityStructure || structures.wrapAsHolder(structure).is(StructureTags.VILLAGE))) {
            BoundingBox footprint = underfoot(piece);
            if (footprint != null && footprint.intersects(ground.getX(), ground.getZ(), ground.getX(), ground.getZ())) { return true; }
        }
        return false;
    }

    @Nullable private static BoundingBox underfoot(StructurePiece piece) {
        if (piece instanceof ContentCityPiece street) { return street.paved(); }
        if (piece instanceof ContentCityBulbPiece bulb) { return bulb.stood(); }
        if (piece instanceof ContentCityPlazaPiece plaza) { return plaza.reached(); }
        if (piece instanceof ContentCityRailPiece rail) { return rail.stood(); }
        if (piece instanceof ContentCityStairsPiece stairs) { return stairs.stood(); }
        if (piece instanceof ContentCityStampPiece stamp) { return stamp.stood(); }
        if (piece instanceof ContentCityStationPiece || piece instanceof ContentCitySewerPiece || piece instanceof ContentCitySewerLoopPiece || piece instanceof ContentCitySewerHatchPiece) { return null; }
        return piece.getBoundingBox();
    }

    private static int footprint(WorldgenDef def) { return ShapeDef.VEIN.equals(def.shape().type()) ? ContentOreVein.REACH : Math.max(1, def.shape().radius().most()); }

    private static int square(int low, int high) { return ((low + high) >> 1) & ~15; }
}
