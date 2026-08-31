package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.world.GroundLevel;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentVillageDecor {
    private static final String EMPTY = "empty";
    private static final String NAME = "villageDecor";
    private static final int STEP = 3;
    private static final int SPREAD = 8;
    private static final int RISE = 16;
    private static final List<Choice> CHOICES = new ArrayList<>();
    @Nullable private static WorldTemplateDef listedFrom;
    private static boolean listed;
    private static int total;

    private ContentVillageDecor() {}

    @SubscribeEvent public static void onDecorated(PopulateChunkEvent.Post event) {
        World world = event.getWorld();
        if (world.isRemote) { return; }
        refresh();
        if (CHOICES.isEmpty()) { return; }
        Collection<StructureStart> starts = ContentStructureSearch.villageStarts(world);
        if (starts.isEmpty()) { return; }
        int blockX = (event.getChunkX() << 4) + 8;
        int blockZ = (event.getChunkZ() << 4) + 8;
        StructureBoundingBox clip = new StructureBoundingBox(blockX, 0, blockZ, blockX + 15, 255, blockZ + 15);
        for (StructureStart start : starts) {
            if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(clip)) { continue; }
            for (StructureComponent piece : start.getComponents()) {
                if (piece instanceof StructureVillagePieces.Path) { alongRoad(world, start, piece, clip); }
            }
        }
    }

    private static void alongRoad(World world, StructureStart start, StructureComponent piece, StructureBoundingBox clip) {
        StructureBoundingBox box = piece.getBoundingBox();
        if (box.minX - 2 > clip.maxX || box.maxX + 2 < clip.minX || box.minZ - 2 > clip.maxZ || box.maxZ + 2 < clip.minZ) { return; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        boolean alongX = BeardPlots.roadAlongX(box);
        int from = alongX ? box.minX : box.minZ;
        int to = alongX ? box.maxX : box.maxZ;
        int near = (alongX ? box.minZ : box.minX) - 1;
        int far = (alongX ? box.maxZ : box.maxX) + 1;
        for (int row = from; row <= to; row++) {
            if (Math.floorMod(row, STEP) != 0) { continue; }
            place(world, start, clip, at, alongX ? row : near, alongX ? near : row);
            place(world, start, clip, at, alongX ? row : far, alongX ? far : row);
        }
    }

    private static void place(World world, StructureStart start, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int z) {
        if (x < clip.minX || x > clip.maxX || z < clip.minZ || z > clip.maxZ) { return; }
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (BeardPlots.underAnother(village, null, x, z) || BeardPlots.overRoad(village, x, z)) { return; }
            if (ContentBeard.wanted() && BeardPlots.insidePlaza(village.getComponents(), x, z)) { return; }
        }
        BlockPos origin = GroundLevel.inWindow(world, new BlockPos(x, 0, z));
        int bed = origin.getY();
        if (bed <= 1 || BeardKeep.holds(x, bed, z)) { return; }
        if (!world.isAirBlock(origin) || !world.getBlockState(origin.down()).getMaterial().isSolid()) { return; }
        if (!world.isAreaLoaded(origin, SPREAD + 1)) { return; }
        if (ContentBeard.beforeADoor(world, clip, at, x, bed, z)) { return; }
        Random random = SeededRandom.at(world, x, z);
        Choice chosen = pick(random);
        if (chosen == null || EMPTY.equals(chosen.name)) { return; }
        IContentShape figure = ContentWorldgen.shapeFor(chosen.name);
        if (figure == null) { return; }
        if (!ContentBeard.wanted()) {
            figure.generate(world, random, origin);
            return;
        }
        StructureBoundingBox area = new StructureBoundingBox(x - SPREAD, bed - 1, z - SPREAD, x + SPREAD, bed + RISE, z + SPREAD);
        BeardKeep.watchArea(world, area, NAME);
        figure.generate(world, random, origin);
        BeardKeep.learn(world);
    }

    @Nullable private static Choice pick(Random random) {
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (Choice choice : CHOICES) {
            roll -= choice.weight;
            if (roll < 0) { return choice; }
        }
        return null;
    }

    private static void refresh() {
        WorldTemplateDef active = ContentWorldTemplates.active();
        if (listed && active == listedFrom) { return; }
        load();
        listedFrom = active;
        listed = true;
    }

    private static void load() {
        CHOICES.clear();
        total = 0;
        for (String entry : ContentControl.list(ContentControl.VILLAGES, NAME, Config.worldgen.villageDecor)) {
            String[] parts = Settings.pair(entry, NAME, "name=weight");
            if (parts == null) { continue; }
            int weight = weightOf(parts[1], entry);
            if (weight < 1) { continue; }
            CHOICES.add(new Choice(parts[0], weight));
            total += weight;
        }
        for (Choice choice : CHOICES) {
            if (EMPTY.equals(choice.name) || ContentWorldgen.shapeFor(choice.name) != null) { continue; }
            ContentLog.LOGGER.error("{} names {}, which no pack registers as worldgen, so its share of the verge is left bare", NAME, choice.name);
        }
        if (!CHOICES.isEmpty()) { ContentLog.LOGGER.info("Villages scatter {} kind(s) of decoration along their roads", CHOICES.size()); }
    }

    private static int weightOf(String said, String entry) {
        int asked;
        try { asked = Integer.parseInt(said); }
        catch (NumberFormatException wrong) {
            ContentLog.LOGGER.error("{} entry '{}' gives a weight of '{}', which is not a whole number, ignoring the entry", NAME, entry, said);
            return -1;
        }
        if (asked >= 1) { return asked; }
        ContentLog.LOGGER.error("{} entry '{}' asks for a weight of {}, which is below 1, ignoring the entry", NAME, entry, asked);
        return -1;
    }

    private static final class Choice {
        private final String name;
        private final int weight;

        private Choice(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }
    }
}
