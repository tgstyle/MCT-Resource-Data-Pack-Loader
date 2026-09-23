package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.neoforged.neoforge.common.world.PieceBeardifierModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class ContentCityPlotPiece extends TemplateStructurePiece implements PieceBeardifierModifier, ContentCityTrees.Felling {
    public static final StructurePieceType TYPE = (StructurePieceType.StructureTemplateType) ContentCityPlotPiece::new;
    private static final String ROTATION = "Rot";
    private static final String INTEGRITY = "Int";
    private static final String PLOT = "Plot";
    private static final String RULED = "Ruled";
    private static final String SEAT = "Seat";
    private static final String KEEP = "Keep";
    private static final String ROADS = "Roads";
    private static final String STANDING = "Standing";
    private static final int BANK_REACH = 4;
    private final String plot;
    private final boolean ruled;
    private final int seat;
    private final int[] keep;
    private final int[] roads;
    private final boolean standing;

    public ContentCityPlotPiece(StructureTemplateManager manager, ResourceLocation template, Rotation rotation, int integrity, BlockPos corner, boolean standing) { this(manager, template, rotation, integrity, corner, "", false, corner.getY(), new int[0], new int[0], standing); }

    public ContentCityPlotPiece(StructureTemplateManager manager, ResourceLocation template, Rotation rotation, int integrity, BlockPos corner, String plot, boolean ruled, int seat, int[] keep, int[] roads) { this(manager, template, rotation, integrity, corner, plot, ruled, seat, keep, roads, false); }

    private ContentCityPlotPiece(StructureTemplateManager manager, ResourceLocation template, Rotation rotation, int integrity, BlockPos corner, String plot, boolean ruled, int seat, int[] keep, int[] roads, boolean standing) {
        super(TYPE, 0, manager, template, template.toString(), settings(rotation, integrity, ruled), corner);
        this.plot = plot;
        this.ruled = ruled;
        this.seat = seat;
        this.keep = keep;
        this.roads = roads;
        this.standing = standing;
    }

    public ContentCityPlotPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(TYPE, tag, manager, held -> settings(Rotation.valueOf(tag.getString(ROTATION)), tag.getInt(INTEGRITY), tag.getBoolean(RULED)));
        this.plot = tag.getString(PLOT);
        this.ruled = tag.getBoolean(RULED);
        this.seat = tag.contains(SEAT) ? tag.getInt(SEAT) : templatePosition.getY();
        this.keep = tag.getIntArray(KEEP);
        this.roads = tag.getIntArray(ROADS);
        this.standing = tag.contains(STANDING) ? tag.getBoolean(STANDING) : plot.isEmpty();
    }

    private static StructurePlaceSettings settings(Rotation rotation, int integrity, boolean ruled) {
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE);
        if (ruled) { settings.addProcessor(JigsawReplacementProcessor.INSTANCE); }
        settings.addProcessor(new ContentCityBlocks(integrity, ruled));
        return settings;
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(ROTATION, placeSettings.getRotation().name());
        tag.putInt(INTEGRITY, ContentCityBlocks.integrityOf(placeSettings));
        tag.putString(PLOT, plot);
        tag.putBoolean(RULED, ruled);
        tag.putInt(SEAT, seat);
        tag.putIntArray(KEEP, keep);
        tag.putIntArray(ROADS, roads);
        tag.putBoolean(STANDING, standing);
    }

    @Override @Nonnull public BoundingBox stood() { return getBoundingBox(); }

    @Override public int fellFloor() { return seat + 1; }

    @Override public BoundingBox crowned() {
        if (!settled()) { return null; }
        BoundingBox held = getBoundingBox();
        return new BoundingBox(held.minX() - CityPlotGround.RING, seat + 1, held.minZ() - CityPlotGround.RING, held.maxX() + CityPlotGround.RING, Math.max(seat + CityPlotClearing.OPEN_LEAST, held.maxY() + 1), held.maxZ() + CityPlotGround.RING);
    }

    private String named() { return plot.isEmpty() ? templateName : plot; }

    private void laid(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        VillageDef def = plot.isEmpty() ? null : ContentVillages.byKey(plot);
        BoundingBox held = getBoundingBox();
        int felled = ContentCityTrees.fellAround(level, manager, chunk, this, box);
        if (felled > 0) { ContentLog.LOGGER.debug("Felled {} tree block(s) crowding {} at {}, {}", felled, named(), held.minX(), held.minZ()); }
        if (def != null) { CityPlotGround.eaves(level, held, box, seat); }
        List<CityRails.Laid> bores = bores(level);
        ContentCityBlocks.bored(bores);
        try { super.postProcess(level, manager, generator, random, box, chunk, pos); }
        finally { ContentCityBlocks.unbored(); }
        if (!plot.isEmpty()) { CityPlotGround.raisedFooting(level, held, box, seat, keep, roads, bores); }
        if (def == null) { return; }
        stock(level, box, def);
        BlockState ground = ContentStates.known(def.ground(), "village plot " + def.key());
        int grounded = CityPlotGround.footing(level, held, box, seat, ground == null ? Blocks.DIRT.defaultBlockState() : ground, bores);
        int overhead = CityPlotGround.liftOffRoof(level, held, box);
        int banked = CityPlotGround.bankRing(level, held, box, seat, keep, roads, bores, false, named());
        int opened = CityPlotClearing.openOver(level, held, box, seat, keep, roads);
        int doorways = CityPlotClearing.doorways(level, held, box, keep, roads);
        int bridged = bridges(level, manager, chunk, held, box);
        if (opened + grounded + banked + overhead + bridged + doorways > 0) { ContentLog.LOGGER.debug("Opened {} block(s) around {} at {}, {}, stood {} block(s) of ground under it, banked {} up to its grade, lifted {} off its roof, bridged {} between it and a neighbor, and freed {} in front of its doors", opened, named(), held.minX(), held.minZ(), grounded, banked, overhead, bridged, doorways); }
        ContentCity.residents(level, def, box, index -> templatePosition.offset(StructureTemplate.calculateRelativePosition(placeSettings, new BlockPos(def.villagerX() + index, def.villagerY(), def.villagerZ()))));
    }

    private int bridges(WorldGenLevel level, StructureManager manager, ChunkPos chunk, BoundingBox held, BoundingBox box) {
        int bridged = 0;
        BoundingBox reach = held.inflatedBy(CityPlotGround.BRIDGE_GAP);
        List<BoundingBox> others = ContentCityTrees.kin(manager, chunk, this, reach, piece -> piece != this);
        List<BoundingBox> streets = ContentCityTrees.kin(manager, chunk, this, reach, ContentCityPiece.class::isInstance);
        for (StructurePiece piece : ContentCityTrees.kinPieces(manager, chunk, this, reach, ContentCityPlotPiece.class::isInstance)) {
            if (piece == this || !(piece instanceof ContentCityPlotPiece near)) { continue; }
            bridged += CityPlotGround.bridge(level, held, seat, near.getBoundingBox(), near.seat, box, others, streets);
        }
        return bridged;
    }

    private List<CityRails.Laid> bores(WorldGenLevel level) {
        BoundingBox held = getBoundingBox();
        return CityRails.subways(CityGround.of(level), held.minX() - BANK_REACH, held.minZ() - BANK_REACH, held.maxX() + BANK_REACH, held.maxZ() + BANK_REACH);
    }

    boolean settled() { return !plot.isEmpty() && ContentVillages.byKey(plot) != null; }

    int[] roads() { return roads; }

    public void ringBeyond(@Nonnull WorldGenLevel level, @Nonnull BoundingBox box) {
        BoundingBox held = getBoundingBox();
        if (!settled() || CityPlotGround.ringMisses(held, box)) { return; }
        CityBiome.within(level, box, () -> CityPlotGround.bankRing(level, held, box, seat, keep, roads, bores(level), false, named()));
    }

    private void stock(WorldGenLevel level, BoundingBox box, VillageDef def) {
        ResourceLocation table = def.lootTable().isEmpty() ? null : ResourceLocation.tryParse(def.lootTable());
        if (table == null) { return; }
        BoundingBox held = getBoundingBox();
        ChunkAccess access = level.getChunk(box.minX() >> 4, box.minZ() >> 4);
        List<BlockPos> spots = new ArrayList<>(access.getBlockEntitiesPos());
        long seed = level.getSeed() ^ ((long) held.minX() << 32 ^ held.minZ());
        for (BlockPos spot : spots) {
            if (!held.isInside(spot) || !box.isInside(spot)) { continue; }
            BlockEntity entity = level.getBlockEntity(spot);
            if (entity instanceof RandomizableContainerBlockEntity container) { container.setLootTable(ResourceKey.create(Registries.LOOT_TABLE, table), seed ^ spot.asLong()); }
        }
    }

    @Override protected void handleDataMarker(@Nonnull String name, @Nonnull BlockPos pos, @Nonnull ServerLevelAccessor level, @Nonnull RandomSource random, @Nonnull BoundingBox box) {}

    @Override @Nonnull public BoundingBox getBeardifierBox() { return getBoundingBox(); }

    @Override @Nonnull public TerrainAdjustment getTerrainAdjustment() { return ContentCity.adaptation(); }

    @Override public int getGroundLevelDelta() { return standing ? 0 : 1; }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        CityBiome.within(level, box, () -> laid(level, manager, generator, random, box, chunk, pos));
    }
}
