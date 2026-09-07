package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import mctmods.resourcedatapackloader.util.world.GroundLevel;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;
import java.util.Random;

public final class ContentOreSigns {
    private ContentOreSigns() {}

    public static int place(World world, WorldgenDef def, BlockPos origin, int lowX, int lowZ, int highX, int highZ) {
        if (def.indicators.isEmpty()) { return 0; }
        Random random = SeededRandom.at(world, origin.getX(), origin.getZ());
        int count = def.indicatorCount.pick(random);
        int reach = Math.max(1, def.shape.radius.most) + def.indicatorSpread;
        int laid = 0;
        for (int i = 0; i < count; i++) {
            int x = MathHelper.clamp(origin.getX() + random.nextInt(2 * reach + 1) - reach, lowX, highX);
            int z = MathHelper.clamp(origin.getZ() + random.nextInt(2 * reach + 1) - reach, lowZ, highZ);
            WeightedPicks.Pick chosen = def.indicators.pick(random);
            if (chosen == null || WeightedPicks.EMPTY.equals(chosen.name)) { continue; }
            IBlockState state = ContentStates.parse(chosen.name, "indicators");
            if (state == null) {
                ContentLog.LOGGER.error("Worldgen entry {} names {} as an indicator, which is not a registered block, so none is left", def.registryName, chosen.name);
                continue;
            }
            BlockPos spot = GroundLevel.inWindow(world, new BlockPos(x, 0, z));
            if (spot.getY() <= 1 || !world.isAirBlock(spot)) { continue; }
            IBlockState under = world.getBlockState(spot.down());
            if (!under.getMaterial().isSolid() || under.getMaterial().isLiquid()) { continue; }
            if (taken(world, x, z)) { continue; }
            world.setBlockState(spot, state, 2);
            laid++;
        }
        if (laid > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("Left {} indicator block(s) on the surface over the {} at {}, {}, {}", laid, def.registryName, origin.getX(), origin.getY(), origin.getZ()); }
        return laid;
    }

    private static boolean taken(World world, int x, int z) {
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (village == null) { continue; }
            if (BeardPlots.underAnother(village, null, x, z) || BeardPlots.overRoad(village, x, z)) { return true; }
        }
        return false;
    }
}
