package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.VillageDef;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class ContentPlotPoolElement extends SinglePoolElement {
    public static final Codec<ContentPlotPoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(templateCodec(), processorsCodec(), projectionCodec(), Codec.STRING.fieldOf("plot").forGetter(held -> held.plot)).apply(instance, ContentPlotPoolElement::new));
    public static final StructurePoolElementType<ContentPlotPoolElement> TYPE = () -> CODEC;
    public static final String ENTRANCE = "minecraft:building_entrance";
    private final String plot;

    private ContentPlotPoolElement(Either<ResourceLocation, StructureTemplate> template, Holder<StructureProcessorList> processors, StructureTemplatePool.Projection projection, String plot) {
        super(template, processors, projection);
        this.plot = plot;
    }

    public static ContentPlotPoolElement of(VillageDef def) {
        StructureProcessorList processors = new StructureProcessorList(List.of());
        return new ContentPlotPoolElement(Either.left(ResourceLocation.tryParse(def.structure())), Holder.direct(processors), StructureTemplatePool.Projection.RIGID, def.key().toString());
    }

    @Override @Nonnull public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(@Nonnull StructureTemplateManager manager, @Nonnull BlockPos pos, @Nonnull Rotation rotation, @Nonnull RandomSource random) {
        List<StructureTemplate.StructureBlockInfo> found = new ArrayList<>(super.getShuffledJigsawBlocks(manager, pos, rotation, random));
        Vec3i size = manager.getOrCreate(template.left().orElseThrow()).getSize();
        BlockPos at = pos.offset(StructureTemplate.calculateRelativePosition(new StructurePlaceSettings().setRotation(rotation), new BlockPos(size.getX() / 2, 0, 0)));
        CompoundTag tag = new CompoundTag();
        tag.putString("name", ENTRANCE);
        tag.putString("target", ENTRANCE);
        tag.putString("pool", "minecraft:empty");
        tag.putString("joint", "aligned");
        tag.putString("final_state", "minecraft:air");
        found.add(0, new StructureTemplate.StructureBlockInfo(at, Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(rotation.rotate(Direction.NORTH), Direction.UP)), tag));
        return found;
    }

    @Override public boolean place(@Nonnull StructureTemplateManager manager, @Nonnull WorldGenLevel level, @Nonnull StructureManager structures, @Nonnull ChunkGenerator generator, @Nonnull BlockPos offset, @Nonnull BlockPos pos, @Nonnull Rotation rotation, @Nonnull BoundingBox box, @Nonnull RandomSource random, boolean keepJigsaws) {
        if (!super.place(manager, level, structures, generator, offset, pos, rotation, box, random, keepJigsaws)) { return false; }
        VillageDef def = ContentVillages.byKey(plot);
        if (def == null) { return true; }
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
        BoundingBox held = getBoundingBox(manager, offset, rotation);
        ResourceLocation table = def.lootTable().isEmpty() ? null : ResourceLocation.tryParse(def.lootTable());
        if (table != null) {
            for (BlockPos spot : level.getChunk(box.minX() >> 4, box.minZ() >> 4).getBlockEntitiesPos()) {
                if (!held.isInside(spot) || !box.isInside(spot)) { continue; }
                BlockEntity entity = level.getBlockEntity(spot);
                if (entity instanceof RandomizableContainerBlockEntity container) { container.setLootTable(table, level.getSeed() ^ spot.asLong()); }
            }
        }
        ContentCity.residents(level, def, box, index -> offset.offset(StructureTemplate.calculateRelativePosition(settings, new BlockPos(def.villagerX() + index, def.villagerY(), def.villagerZ()))));
        return true;
    }

    @Override @Nonnull protected StructurePlaceSettings getSettings(@Nonnull Rotation rotation, @Nonnull BoundingBox box, boolean offset) {
        StructurePlaceSettings settings = super.getSettings(rotation, box, offset);
        VillageDef def = ContentVillages.byKey(plot);
        if (def != null) { settings.addProcessor(new ContentCityBlocks(def.integrity(), false)); }
        return settings;
    }

    @Override @Nonnull public StructurePoolElementType<?> getType() { return TYPE; }
}
