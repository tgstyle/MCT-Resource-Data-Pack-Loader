package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.block.RCBlocks;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaGeneration;
import ivorius.reccomplex.world.gen.feature.villages.GenericVillagePiece;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureComponent;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecurrentPlots {
    public static final int SKIPPED = 0;
    public static final int GROUND = 1;
    public static final int OPEN = 2;
    public static final int BUILT = 3;
    private static final Map<String, Integer> SINKS = new HashMap<>();
    private static final Map<String, Integer> COURSES = new HashMap<>();
    private static final Map<String, Integer> SEATS = new HashMap<>();
    private static final Map<String, IvWorldData> DATA = new HashMap<>();

    private RecurrentPlots() {}

    public static final class Plot {
        public final String structure;
        public final String generation;
        public final int weight;
        public final double leastBase;
        public final double mostBase;
        public final double leastScaled;
        public final double mostScaled;
        public final EnumFacing front;
        public final int courses;
        public final int seat;

        private Plot(String structure, String generation, int weight, double leastBase, double mostBase, double leastScaled, double mostScaled, EnumFacing front, int courses, int seat) {
            this.structure = structure;
            this.generation = generation;
            this.weight = weight;
            this.leastBase = leastBase;
            this.mostBase = mostBase;
            this.leastScaled = leastScaled;
            this.mostScaled = mostScaled;
            this.front = front;
            this.courses = courses;
            this.seat = seat;
        }
    }

    public static List<Plot> plots() {
        List<Plot> out = new ArrayList<>();
        for (Pair<Structure<?>, VanillaGeneration> pair : StructureRegistry.INSTANCE.getGenerationTypes(VanillaGeneration.class)) {
            Structure<?> structure = pair.getLeft();
            String id = StructureRegistry.INSTANCE.id(structure);
            if (id == null || !StructureRegistry.INSTANCE.hasActive(id) || !(structure instanceof GenericStructure)) { continue; }

            VanillaGeneration how = pair.getRight();
            int weight = how.getVanillaWeight(RCConfig.tweakedSpawnRate(id));
            if (weight <= 0) { continue; }

            out.add(new Plot(id, how.id(), weight, how.minBaseLimit, how.maxBaseLimit, how.minScaledLimit, how.maxScaledLimit, how.front, courseFor(id), seatFor(id, how.id())));
        }
        return out;
    }

    public static boolean fits(String structureId, String generationId, Biome biome) {
        Structure<?> structure = StructureRegistry.INSTANCE.get(structureId);
        GenerationType how = structure != null ? structure.generationType(generationId) : null;
        return how instanceof VanillaGeneration && ((VanillaGeneration) how).generatesIn(biome);
    }

    public static int[] sizeOf(String structureId) {
        IvBlockCollection held = collection(structureId);
        return held == null ? null : new int[] { held.width, held.height, held.length };
    }

    public static int classify(String structureId, int x, int y, int z) {
        IvBlockCollection held = collection(structureId);
        if (held == null) { return SKIPPED; }

        IBlockState state = held.getBlockState(new BlockPos(x, y, z));
        if (state.getBlock() == RCBlocks.genericSolid) { return GROUND; }
        if (state.getBlock().getRegistryName() != null && "reccomplex".equals(state.getBlock().getRegistryName().getNamespace())) { return SKIPPED; }
        if (state.getMaterial() == Material.AIR) { return OPEN; }

        return BUILT;
    }

    public static IBlockState stateAt(String structureId, int x, int y, int z) {
        IvBlockCollection held = collection(structureId);
        return held == null ? null : held.getBlockState(new BlockPos(x, y, z));
    }

    public static List<NBTTagCompound> tiles(String structureId) {
        IvWorldData data = worldData(structureId);
        return data == null ? Collections.emptyList() : data.tileEntities;
    }

    public static int courseFor(String structureId) {
        Integer known = COURSES.get(structureId);
        if (known != null) { return known; }

        int course = 0;
        IvBlockCollection held = collection(structureId);
        if (held != null) {
            for (int y = 0; y < held.height; y++) {
                boolean ground = false;
                for (int x = 0; x < held.width && !ground; x++) {
                    for (int z = 0; z < held.length; z++) {
                        if (held.getBlockState(new BlockPos(x, y, z)).getBlock() != RCBlocks.genericSolid) { continue; }

                        ground = true;
                        break;
                    }
                }
                if (ground) { course = y + 1; }
            }
        }
        COURSES.put(structureId, course);
        ContentLog.LOGGER.debug("The Recurrent Complex plot {} lays its own ground in its lowest {} course(s), so the ground under it is made up to there", structureId, course);
        return course;
    }

    public static int seatFor(String structureId, String generationId) {
        String named = structureId + '\u0000' + generationId;
        Integer known = SEATS.get(named);
        if (known != null) { return known; }

        int stand = standLayer(structureId);
        int seated = Math.max(-5, -stand);
        SEATS.put(named, seated);
        ContentLog.LOGGER.debug("The Recurrent Complex plot {} is walked on at its own layer {}, so it is seated {} below the plot's ground to put that layer level with the road", structureId, stand, -seated);
        return seated;
    }

    private static int standLayer(String structureId) {
        int courses = courseFor(structureId);
        if (courses <= 0) { return 1; }

        IvBlockCollection held = collection(structureId);
        if (held == null || courses >= held.height) { return courses - 1; }

        int built = 0;
        int space = 0;
        for (int x = 0; x < held.width; x++) {
            for (int z = 0; z < held.length; z++) {
                IBlockState state = held.getBlockState(new BlockPos(x, courses, z));
                if (state.getBlock() == RCBlocks.genericSolid) { built++; }
                else if (state.getBlock().getRegistryName() != null && "reccomplex".equals(state.getBlock().getRegistryName().getNamespace())) { space++; }
                else if (state.getMaterial().isSolid()) { built++; }
                else { space++; }
            }
        }
        return built > space ? courses : courses - 1;
    }

    public static int sink(StructureComponent piece) {
        if (!(piece instanceof GenericVillagePiece)) { return 0; }

        GenericVillagePiece plot = (GenericVillagePiece) piece;
        String named = plot.structureID + '\u0000' + plot.generationID;
        Integer known = SINKS.get(named);
        if (known != null) { return known; }

        Structure<?> structure = StructureRegistry.INSTANCE.get(plot.structureID);
        GenerationType how = structure != null ? structure.generationType(plot.generationID) : null;
        int sunk = how instanceof VanillaGeneration ? Math.max(0, Math.min(-((VanillaGeneration) how).spawnShift.getY(), ContentBeard.FOOTING_COURSE)) : 0;
        SINKS.put(named, sunk);
        return sunk;
    }

    public static int seat(StructureComponent piece) {
        if (!(piece instanceof GenericVillagePiece)) { return -1; }

        GenericVillagePiece plot = (GenericVillagePiece) piece;
        return seatFor(plot.structureID, plot.generationID);
    }

    public static int groundCourse(StructureComponent piece) {
        if (!(piece instanceof GenericVillagePiece)) { return 0; }

        return courseFor(((GenericVillagePiece) piece).structureID);
    }

    private static IvBlockCollection collection(String structureId) {
        IvWorldData data = worldData(structureId);
        return data == null ? null : data.blockCollection;
    }

    private static IvWorldData worldData(String structureId) {
        if (DATA.containsKey(structureId)) { return DATA.get(structureId); }

        Structure<?> structure = StructureRegistry.INSTANCE.get(structureId);
        IvWorldData data = structure instanceof GenericStructure ? ((GenericStructure) structure).constructWorldData() : null;
        DATA.put(structureId, data);
        return data;
    }
}
