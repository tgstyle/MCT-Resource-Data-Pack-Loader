package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.util.world.StructureLoot;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentPierCargo {
    private static final String NAME = "villagePathPierCargo";
    private static final String LOOT = "villagePathPierLoot";
    public static final ResourceLocation LOOT_TABLE = new ResourceLocation("resourcedatapackloader", "chests/pier_cargo");
    private static final int MOST_HIGH = 8;
    private static final WeightedPicks CHOICES = new WeightedPicks(NAME);

    private ContentPierCargo() {}

    public static boolean wanted() {
        if (CHOICES.stale()) { load(); }
        return !CHOICES.isEmpty();
    }

    public static boolean place(World world, int x, int y, int z, EnumFacing facing) {
        if (CHOICES.stale()) { load(); }
        if (CHOICES.isEmpty()) { return false; }
        Random random = SeededRandom.at(world, x, y, z);
        WeightedPicks.Pick chosen = CHOICES.pick(random);
        if (chosen == null || WeightedPicks.EMPTY.equals(chosen.name)) { return false; }
        IBlockState state = ContentStates.parse(chosen.name, NAME);
        if (state == null) { return false; }
        int high = heightOf(chosen);
        for (int step = 0; step < high; step++) {
            if (BeardKeep.holds(x, y + step, z)) { return false; }
            if (!world.getBlockState(new BlockPos(x, y + step, z)).getMaterial().isReplaceable()) { return false; }
        }
        IBlockState stood = ContentBeard.faced(state, facing);
        for (int step = 0; step < high; step++) {
            BlockPos at = new BlockPos(x, y + step, z);
            world.setBlockState(at, stood, 2);
            BeardKeep.holdSpot(x, y + step, z);
            stock(world, at, random);
        }
        return true;
    }

    private static int heightOf(WeightedPicks.Pick chosen) {
        if (chosen.tail.isEmpty()) { return 1; }
        int asked;
        try { asked = Integer.parseInt(chosen.tail); }
        catch (NumberFormatException wrong) {
            ContentLog.LOGGER.error("{} gives {} a height of '{}', which is not a whole number, so one block is stood there", NAME, chosen.name, chosen.tail);
            return 1;
        }
        if (asked >= 1 && asked <= MOST_HIGH) { return asked; }
        ContentLog.LOGGER.error("{} gives {} a height of {}, which is not between 1 and {}, so one block is stood there", NAME, chosen.name, asked, MOST_HIGH);
        return 1;
    }

    private static void stock(World world, BlockPos at, Random random) { StructureLoot.stock(world.getTileEntity(at), ContentControl.text(ContentControl.VILLAGES, LOOT, Config.worldgen.villagePathPierLoot), random); }

    private static void load() {
        CHOICES.load(ContentControl.list(ContentControl.VILLAGES, NAME, Config.worldgen.villagePathPierCargo));
        for (String name : CHOICES.names()) {
            if (WeightedPicks.EMPTY.equals(name) || ContentStates.parse(name, NAME) != null) { continue; }
            ContentLog.LOGGER.error("{} names {}, which is no block here, so its share of the pier is left bare", NAME, name);
        }
        if (!CHOICES.isEmpty()) { ContentLog.LOGGER.info("Piers carry {} kind(s) of cargo", CHOICES.size()); }
    }
}
