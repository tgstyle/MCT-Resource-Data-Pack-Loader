package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.VillageDef;

import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nonnull;

public final class ContentCityPlotPiece extends TemplateStructurePiece implements PieceBeardifierModifier {
    public static final StructurePieceType TYPE = (StructurePieceType.StructureTemplateType) ContentCityPlotPiece::new;
    private static final String ROTATION = "Rot";
    private static final String INTEGRITY = "Int";
    private static final String PLOT = "Plot";
    private final String plot;

    public ContentCityPlotPiece(StructureTemplateManager manager, ResourceLocation template, Rotation rotation, int integrity, BlockPos corner, String plot) {
        super(TYPE, 0, manager, template, template.toString(), settings(rotation, integrity), corner);
        this.plot = plot;
    }

    public ContentCityPlotPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(TYPE, tag, manager, held -> settings(Rotation.valueOf(tag.getString(ROTATION)), tag.getInt(INTEGRITY)));
        this.plot = tag.getString(PLOT);
    }

    private static StructurePlaceSettings settings(Rotation rotation, int integrity) {
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE);
        settings.addProcessor(new ContentCityBlocks(integrity));
        return settings;
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(ROTATION, placeSettings.getRotation().name());
        tag.putInt(INTEGRITY, ContentCityBlocks.integrityOf(placeSettings));
        tag.putString(PLOT, plot);
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        super.postProcess(level, manager, generator, random, box, chunk, pos);
        VillageDef def = ContentVillages.byKey(plot);
        if (def == null) { return; }
        ContentCity.residents(level, def, box, index -> templatePosition.offset(StructureTemplate.calculateRelativePosition(placeSettings, new BlockPos(def.villagerX() + index, def.villagerY(), def.villagerZ()))));
    }

    @Override protected void handleDataMarker(@Nonnull String name, @Nonnull BlockPos pos, @Nonnull ServerLevelAccessor level, @Nonnull RandomSource random, @Nonnull BoundingBox box) {}

    @Override @Nonnull public BoundingBox getBeardifierBox() { return getBoundingBox(); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return TerrainAdjustment.BEARD_THIN; }

    @Override public int getGroundLevelDelta() { return 0; }
}
