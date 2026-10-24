package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.StructureMapDef;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
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
    private static final String MAP = "Map";
    private static final String SEED = "Seed";

    private final String map;
    private final long seed;

    public ContentMapPiece(StructureTemplateManager manager, Identifier template, Rotation rotation, BlockPos corner, Identifier map, long seed) {
        super(TYPE, 0, manager, template, template.toString(), settings(rotation, map.toString(), seed), corner);
        this.map = map.toString();
        this.seed = seed;
    }

    public ContentMapPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(TYPE, tag, manager, _ -> settings(Rotation.valueOf(tag.getStringOr(ROTATION, "")), tag.getStringOr(MAP, ""), tag.getLongOr(SEED, 0L)));
        this.map = tag.getStringOr(MAP, "");
        this.seed = tag.getLongOr(SEED, 0L);
    }

    private static StructurePlaceSettings settings(Rotation rotation, String map, long seed) {
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE);
        Identifier key = map.isEmpty() ? null : Identifier.tryParse(map);
        StructureMapDef def = key == null ? null : ContentStructureMaps.def(key);
        if (def != null && def.fills()) { settings.addProcessor(new ContentMapFillProcessor(def, seed)); }
        return settings;
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(ROTATION, placeSettings.getRotation().name());
        tag.putString(MAP, map);
        tag.putLong(SEED, seed);
    }

    @Override protected void handleDataMarker(@Nonnull String name, @Nonnull BlockPos pos, @Nonnull ServerLevelAccessor level, @Nonnull RandomSource random, @Nonnull BoundingBox box) {}
}
