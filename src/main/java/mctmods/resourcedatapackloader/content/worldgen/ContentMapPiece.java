package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nonnull;

public final class ContentMapPiece extends TemplateStructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.StructureTemplateType) ContentMapPiece::new;
    private static final String ROTATION = "Rot";

    public ContentMapPiece(StructureTemplateManager manager, ResourceLocation template, Rotation rotation, BlockPos corner) {
        super(TYPE, 0, manager, template, template.toString(), settings(rotation), corner);
    }

    public ContentMapPiece(StructureTemplateManager manager, CompoundTag tag) { super(TYPE, tag, manager, held -> settings(Rotation.valueOf(tag.getString(ROTATION)))); }

    private static StructurePlaceSettings settings(Rotation rotation) { return new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE); }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(ROTATION, placeSettings.getRotation().name());
    }

    @Override protected void handleDataMarker(@Nonnull String name, @Nonnull BlockPos pos, @Nonnull ServerLevelAccessor level, @Nonnull RandomSource random, @Nonnull BoundingBox box) {}
}
