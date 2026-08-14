package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.RecurrentPlots;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public final class RecurrentVillages {
    private static boolean registered;

    private RecurrentVillages() {}

    public static void register() {
        if (registered || !Loader.isModLoaded("reccomplex")) { return; }
        registered = true;

        MapGenStructureIO.registerStructureComponent(RecurrentVillagePiece.class, "RdplRcVillage");
        VillagerRegistry.instance().registerVillageCreationHandler(new Handler());
        ContentLog.LOGGER.info("Recurrent Complex village buildings are placed by the loader whenever terrain adaptation is on: its own placement stands down then, and its structures are laid through the same village pipeline as everything else");
    }

    private static Rotation facingFront(EnumFacing front) {
        if (front == EnumFacing.EAST) { return Rotation.COUNTERCLOCKWISE_90; }
        if (front == EnumFacing.SOUTH) { return Rotation.CLOCKWISE_180; }
        if (front == EnumFacing.WEST) { return Rotation.CLOCKWISE_90; }

        return Rotation.NONE;
    }

    public static final class Handler implements VillagerRegistry.IVillageCreationHandler {
        private static final Map<Random, Map<String, Integer>> LIMITS = new WeakHashMap<>();

        @Override public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
            if (!ContentBeard.wanted()) { return new StructureVillagePieces.PieceWeight(RecurrentVillagePiece.class, 0, 0); }

            int weight = 0;
            int limit = 0;
            Map<String, Integer> capped = new HashMap<>();
            for (RecurrentPlots.Plot plot : RecurrentPlots.plots()) {
                weight += plot.weight;
                double least = plot.leastBase + size * plot.leastScaled;
                double most = Math.max(least, plot.mostBase + size * plot.mostScaled);
                int allowed = MathHelper.floor(MathHelper.nextDouble(random, least, most) + 0.5D);
                capped.put(plot.structure, allowed);
                limit += allowed;
            }
            LIMITS.put(random, capped);
            return new StructureVillagePieces.PieceWeight(RecurrentVillagePiece.class, weight, limit);
        }

        private static int standing(List<StructureComponent> placed, String structure) {
            int count = 0;
            for (StructureComponent piece : placed) {
                if (piece instanceof RecurrentVillagePiece && structure.equals(((RecurrentVillagePiece) piece).structureId())) { count++; }
            }
            return count;
        }

        @Override public Class<?> getComponentClass() { return RecurrentVillagePiece.class; }

        @Override @SuppressWarnings({"ConstantConditions", "ConstantValue"}) public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight weight, StructureVillagePieces.Start start, List<StructureComponent> placed, Random random, int x, int y, int z, EnumFacing facing, int type) {
            if (!ContentBeard.wanted()) { return null; }

            Map<String, Integer> capped = LIMITS.get(random);
            List<RecurrentPlots.Plot> plots = new ArrayList<>();
            int total = 0;
            for (RecurrentPlots.Plot plot : RecurrentPlots.plots()) {
                if (!RecurrentPlots.fits(plot.structure, plot.generation, start.biome)) { continue; }
                if (capped != null && standing(placed, plot.structure) >= capped.getOrDefault(plot.structure, 0)) { continue; }

                plots.add(plot);
                total += plot.weight;
            }
            if (total <= 0) { return null; }

            RecurrentPlots.Plot chosen = null;
            int roll = random.nextInt(total);
            for (RecurrentPlots.Plot plot : plots) {
                roll -= plot.weight;
                if (roll < 0) {
                    chosen = plot;
                    break;
                }
            }
            if (chosen == null) { return null; }

            int[] size = RecurrentPlots.sizeOf(chosen.structure);
            if (size == null) { return null; }

            Rotation turned = facingFront(chosen.front);
            boolean quarter = turned == Rotation.CLOCKWISE_90 || turned == Rotation.COUNTERCLOCKWISE_90;
            int wide = quarter ? size[2] : size[0];
            int deep = quarter ? size[0] : size[2];
            EnumFacing road = facing.getOpposite();
            StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x + road.getXOffset(), y, z + road.getZOffset(), 0, 0, 0, wide, size[1], deep, facing);
            if (!RecurrentVillagePiece.deepEnough(box)) { return null; }
            if (StructureComponent.findIntersecting(placed, box) != null) { return null; }

            if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The Recurrent Complex plot {} is laid by the loader at {}, {} facing {}", chosen.structure, box.minX, box.minZ, facing); }
            return new RecurrentVillagePiece(start, type, box, facing, chosen, turned);
        }
    }
}
