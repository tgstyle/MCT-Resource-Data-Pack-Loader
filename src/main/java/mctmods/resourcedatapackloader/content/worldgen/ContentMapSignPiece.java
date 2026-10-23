package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.VillageDef;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nonnull;

public final class ContentMapSignPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) ContentMapSignPiece::new;
    private static final String PLOT = "Plot";
    private static final String ROTATION = "Rot";
    private final String plot;
    private final Rotation turn;

    public ContentMapSignPiece(BlockPos at, String plot, Rotation turn) {
        super(TYPE, 0, new BoundingBox(at));
        this.plot = plot;
        this.turn = turn;
    }

    public ContentMapSignPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.plot = tag.getStringOr(PLOT, "");
        this.turn = Rotation.valueOf(tag.getStringOr(ROTATION, ""));
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putString(PLOT, plot);
        tag.putString(ROTATION, turn.name());
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        VillageDef def = ContentVillages.byKey(plot);
        if (def == null) { return; }
        BoundingBox held = getBoundingBox();
        ContentPlotSign.place(level, def, box, new BlockPos(held.minX(), held.minY(), held.minZ()), turn);
    }
}
