package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PathIntersectDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ContentCityStructurePlaza {
    private ContentCityStructurePlaza() {}

    @Nullable static ContentCityStructure.Well wellAt(List<ContentCityStructure.Well> wells, CityPlan.Junction junction) {
        for (ContentCityStructure.Well well : wells) {
            if (well.middle().equals(junction)) { return well; }
        }
        return null;
    }

    static List<ContentCityStructure.Well> wells(GenerationContext context, CityGround ground, CityPlan plan, Map<CityPlan.Junction, Integer> levels) {
        List<ContentCityStructure.Well> found = new ArrayList<>();
        if (plan.plazas().isEmpty()) { ContentLog.LOGGER.debug("The district at {}, {} has no plaza: its streets run one way only", plan.originX(), plan.originZ()); }
        for (CityPlan.Junction middle : plan.plazas()) {
            if (!middle.alongX().covers(middle.alongZ().middle()) || !middle.alongZ().covers(middle.alongX().middle())) {
                ContentLog.LOGGER.debug("The district at {}, {} has no plaza at {}, {}: its streets do not cross there", plan.originX(), plan.originZ(), middle.alongZ().middle(), middle.alongX().middle());
                continue;
            }
            int x = (middle.fromX() + middle.toX()) / 2 - ContentCityWellPiece.SIZE / 2;
            int z = (middle.fromZ() + middle.toZ()) / 2 - ContentCityWellPiece.SIZE / 2;
            if (!plan.emits(x, z)) { continue; }
            int level = CityPlan.wellGround(ground, x, z);
            levels.put(middle, level);
            long placed = found.isEmpty() && plan.plazas().size() == 1 ? plan.originX() * 341873128712L + plan.originZ() * 132897987541L : x * 341873128712L + z * 132897987541L;
            String named = ContentCity.wellStructure(RandomSource.create(context.seed() ^ ContentCityStructure.SALT ^ placed));
            ResourceLocation template = named == null ? null : ResourceLocation.tryParse(named);
            if (named != null && (template == null || context.structureTemplateManager().get(template).isEmpty())) {
                ContentCity.missingWell(named);
                template = null;
            }
            int tall = template == null ? -1 : context.structureTemplateManager().get(template).orElseThrow().getSize(Rotation.NONE).getY();
            int top = level + Math.max(4, tall);
            found.add(new ContentCityStructure.Well(template, tall, new BoundingBox(x, level, z, x + ContentCityWellPiece.SIZE - 1, top, z + ContentCityWellPiece.SIZE - 1), level, middle));
        }
        return found;
    }

    static void plazaLevels(List<ContentCityStructure.Well> wells, Map<CityPlan.Junction, Integer> levels) {
        for (Map.Entry<CityPlan.Junction, Integer> crossing : levels.entrySet()) {
            ContentCityStructure.Well well = plazaOf(wells, crossing.getKey());
            if (well != null) { crossing.setValue(well.level()); }
        }
    }

    static List<BoundingBox> boxes(List<ContentCityStructure.Well> wells) { return wells.stream().map(ContentCityStructure.Well::box).toList(); }

    @Nullable static ContentCityStructure.Well plazaOf(List<ContentCityStructure.Well> wells, CityPlan.Junction junction) {
        for (ContentCityStructure.Well well : wells) {
            if (CityPlan.inPlaza(well.box(), junction)) { return well; }
        }
        return null;
    }

    @Nullable static ContentCityStructure.Well mouthPlaza(CityPlan.Line line, List<ContentCityStructure.Well> wells, int from, int to) {
        for (ContentCityStructure.Well well : wells) {
            if (CityPlan.nearMouth(line, well.box(), from, to)) { return well; }
        }
        return null;
    }

    static int[] footprints(List<BoundingBox> claims, List<ContentCityStructure.Seated> seated) {
        IntList boxes = new IntArrayList();
        for (BoundingBox claim : claims) { boxes.addAll(IntList.of(claim.minX(), claim.minZ(), claim.maxX(), claim.maxZ())); }
        for (ContentCityStructure.Seated held : seated) { boxes.addAll(IntList.of(held.box().minX(), held.box().minZ(), held.box().maxX(), held.box().maxZ())); }
        return boxes.toIntArray();
    }

    static int[] streetsOver(ContentCityStructure.Well well, Map<CityPlan.Line, ContentCityStructure.Laid> laid) {
        int reach = ContentCitySewerLoopPiece.LOOP + ContentCity.sewerWidth() / 2;
        BoundingBox box = well.box();
        IntList found = new IntArrayList();
        for (CityPlan.Line line : List.of(well.middle().alongX(), well.middle().alongZ())) {
            ContentCityStructure.Laid street = laid.get(line);
            if (street == null) { continue; }
            int low = (line.alongX() ? box.minX() : box.minZ()) - reach;
            int high = (line.alongX() ? box.maxX() : box.maxZ()) + reach;
            for (int row = low; row <= high; row++) {
                int index = row - street.start();
                if (index < 0 || index >= street.profile().length || !street.paved()[index] || street.bridged()[index] || street.profile()[index] >= well.level()) { continue; }
                for (int across = line.at() - 1; across <= line.last() + 1; across++) {
                    found.add(line.alongX() ? row : across);
                    found.add(line.alongX() ? across : row);
                    found.add(street.profile()[index]);
                }
            }
        }
        return found.toIntArray();
    }

    static List<ContentCityStructure.Well> near(GenerationContext context, CityGround ground, CityPlan plan) {
        int size = CityPlan.district();
        int reach = CityPlan.plazaReach() + ContentCityPlazaPiece.MOUTH_MOST + ContentCityWellPiece.SIZE;
        List<ContentCityStructure.Well> found = new ArrayList<>();
        for (CityPlan other : ContentCityStructureSite.plansOver(ground, plan, plan.windowX() - reach, plan.windowX() + size - 1 + reach, plan.windowZ() - reach, plan.windowZ() + size - 1 + reach)) {
            if (other == plan) { continue; }
            CityPlan settled = ContentCityStructureSite.settled(context, ground, other);
            for (ContentCityStructure.Well well : ContentLog.LOGGER.quietly(() -> wells(context, ground, settled, new HashMap<>()))) {
                BoundingBox box = well.box();
                if (box.maxX() + reach >= plan.windowX() && box.minX() - reach < plan.windowX() + size && box.maxZ() + reach >= plan.windowZ() && box.minZ() - reach < plan.windowZ() + size) { found.add(well); }
            }
        }
        return found;
    }

    static void plaza(GenerationContext context, CityGround ground, CityPlan plan, ContentCityStructure.Well well, boolean first, int[] footprints, int[] stations, int[] streets, StructurePiecesBuilder builder) {
        BoundingBox box = well.box();
        int middleX = well.middle().alongZ().middle();
        int middleZ = well.middle().alongX().middle();
        int arms = CityCross.arms(ground, plan, well.middle().alongX(), well.middle().alongZ());
        builder.addPiece(new ContentCityWellPiece(box.minX(), box.minZ(), well.level(), well.tall(), plan.villageType(), first));
        if (well.template() != null) {
            Vec3i span = context.structureTemplateManager().get(well.template()).orElseThrow().getSize(Rotation.NONE);
            builder.addPiece(new ContentCityPlotPiece(context.structureTemplateManager(), well.template(), Rotation.NONE, 100, new BlockPos(box.minX() + (ContentCityWellPiece.SIZE - span.getX()) / 2, well.level() + 1, box.minZ() + (ContentCityWellPiece.SIZE - span.getZ()) / 2), true));
        }
        PathIntersectDef design = ContentPathIntersects.forJunction(context.seed(), box.minX(), box.minZ());
        builder.addPiece(new ContentCityPlazaPiece(box.minX(), box.minZ(), box.maxX(), box.maxZ(), well.level(), CityPlan.plazaPaved(), CityPlan.walkWidth(), CityCross.of(CityPlan.streetFullWidth(), false).core(), design == null ? "" : design.key().toString(), middleX, middleZ, arms, footprints));
        ContentLog.LOGGER.debug("The well {} stands at {}, {} to {}, {} on the plaza of the district at {}, {}, at level {}", well.template() == null ? "of the game's own build" : well.template(), box.minX(), box.minZ(), box.maxX(), box.maxZ(), plan.originX(), plan.originZ(), well.level());
        if (!ContentCity.sewers() || !ContentCity.sewerWellEntrance()) { return; }
        if (ContentCitySewerPiece.cramped(context.heightAccessor().getMinBuildHeight(), well.level())) {
            ContentLog.LOGGER.debug("The sewer loop around the well at {}, {} is not dug: it would not fit between y {}, the world floor plus its lining, and the plaza at y {}", box.minX(), box.minZ(), context.heightAccessor().getMinBuildHeight() + ContentCitySewerPiece.FLOOR_LEAST, well.level());
            return;
        }
        builder.addPiece(new ContentCitySewerLoopPiece(box.minX(), box.minZ(), box.maxX(), box.maxZ(), well.level(), middleX, middleZ, stations, streets));
    }
}
