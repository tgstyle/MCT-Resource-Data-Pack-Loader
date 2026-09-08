package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.def.FollowDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContentOreSigns {
    private ContentOreSigns() {}

    public static int place(ContentPlacer placer, WorldgenDef def, RandomSource random, BlockPos origin) {
        if (def.indicators().isEmpty()) { return 0; }
        WorldGenLevel level = placer.level();
        int count = def.indicatorCount().pick(random);
        int reach = Math.max(1, def.shape().radius().most()) + def.indicatorSpread();
        int laid = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int i = 0; i < count; i++) {
            int x = origin.getX() + random.nextInt(2 * reach + 1) - reach;
            int z = origin.getZ() + random.nextInt(2 * reach + 1) - reach;
            if (!placer.writable(x, z)) { continue; }
            String chosen = PickDef.pick(def.indicators(), random);
            if (chosen == null || FollowDef.EMPTY.equals(chosen)) { continue; }
            Block block = Registered.find(ForgeRegistries.BLOCKS, ResourceLocation.tryParse(chosen));
            if (block == null) {
                ContentLog.LOGGER.error("Worldgen entry {} names {} as an indicator, which is not a registered block, so none is left", def.key(), chosen);
                continue;
            }
            if (!ContentPlacer.loaded(level, at.set(x, 0, z))) { continue; }
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            if (y <= level.getMinBuildHeight() + 1 || y >= level.getMaxBuildHeight() || !level.getBlockState(at.set(x, y, z)).isAir()) { continue; }
            BlockState under = level.getBlockState(at.set(x, y - 1, z));
            if (!under.isFaceSturdy(level, at, Direction.UP) || !under.getFluidState().isEmpty()) { continue; }
            if (placer.placeExactly(block.defaultBlockState(), x, y, z)) { laid++; }
        }
        if (laid > 0) { ContentLog.LOGGER.debug("Left {} indicator block(s) on the surface over the {} at {}, {}, {}", laid, def.key(), origin.getX(), origin.getY(), origin.getZ()); }
        return Mth.clamp(laid, 0, count);
    }
}
