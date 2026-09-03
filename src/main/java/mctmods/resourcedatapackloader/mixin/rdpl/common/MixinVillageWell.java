package mctmods.resourcedatapackloader.mixin.rdpl.common;

import org.spongepowered.asm.mixin.Unique;
import mctmods.resourcedatapackloader.util.world.SeededRandom;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import java.util.Objects;
import java.util.Random;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.WorldServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardBlocks;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureVillagePieces.Well.class) public abstract class MixinVillageWell extends StructureVillagePieces.Village {
    @Unique private static final WeightedPicks rdpl$CENTERPIECES = new WeightedPicks("villageWellStructure");

    @Inject(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Well;getBiomeSpecificBlockState(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", ordinal = 0), cancellable = true)
    private void rdpl$centerpiece(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn, CallbackInfoReturnable<Boolean> cir) {
        if (!(worldIn instanceof WorldServer)) { return; }
        if (rdpl$CENTERPIECES.stale()) { rdpl$CENTERPIECES.load(ContentControl.list(ContentControl.VILLAGES, "villageWellStructure", Config.worldgen.villageWellStructure)); }
        if (rdpl$CENTERPIECES.isEmpty()) { return; }
        StructureBoundingBox box = getBoundingBox();
        WeightedPicks.Pick chosen = rdpl$CENTERPIECES.pick(SeededRandom.at(worldIn, box.minX, box.minZ));
        if (chosen == null || WeightedPicks.EMPTY.equals(chosen.name)) { return; }
        WorldServer server = (WorldServer) worldIn;
        Template loaded = server.getStructureTemplateManager().get(server.getMinecraftServer(), new ResourceLocation(chosen.name));
        if (loaded == null) {
            ContentLog.LOGGER.error("villageWellStructure names '{}', which could not be loaded, so the well is built instead", chosen.name);
            return;
        }
        int ground = box.maxY - 3;
        BlockPos span = loaded.getSize();
        IBlockState floor = BeardRoads.pathBlock("villagePathBlock", Config.worldgen.villagePathBlock, getBiomeSpecificBlockState(Objects.requireNonNull(Blocks.GRASS_PATH).getDefaultState()));
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                at.setPos(x, ground, z);
                if (!structureBoundingBoxIn.isVecInside(at)) { continue; }
                BeardBlocks.clearAbove(worldIn, at, x, z, ground + 1, ground + Math.max(4, span.getY()));
                BeardBlocks.fillUnder(worldIn, at, x, z, ground - 1, ground - 8);
                worldIn.setBlockState(at.setPos(x, ground, z), floor, 2);
            }
        }
        BlockPos origin = new BlockPos(box.minX + (6 - span.getX()) / 2, ground + 1, box.minZ + (6 - span.getZ()) / 2);
        loaded.addBlocksToWorld(worldIn, origin, new PlacementSettings().setIgnoreEntities(true).setBoundingBox(structureBoundingBoxIn), 2);
        for (int dx = 0; dx < span.getX(); dx++) {
            for (int dy = 0; dy < span.getY(); dy++) {
                for (int dz = 0; dz < span.getZ(); dz++) {
                    at.setPos(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (structureBoundingBoxIn.isVecInside(at) && worldIn.getBlockState(at).getBlock() != Blocks.AIR) { BeardKeep.holdSpot(at.getX(), at.getY(), at.getZ()); }
                }
            }
        }
        cir.setReturnValue(true);
    }

    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Well;getAverageGroundLevel(Lnet/minecraft/world/World;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)I"))
    private int rdpl$lowestGround(StructureVillagePieces.Well well, World worldIn, StructureBoundingBox structurebb) {
        StructureBoundingBox box = getBoundingBox();
        if (ContentBeard.wanted() && ContentBeard.adapts(worldIn)) {
            ContentLog.LOGGER.debug("{} at {}, {} stays at the ground the village was founded on, y {}", getClass().getSimpleName(), box.minX, box.minZ, box.maxY - 3);
            return box.maxY - 3;
        }
        int found = getAverageGroundLevel(worldIn, structurebb);
        if (!ContentBeard.wanted() || found < 0) { return found; }
        int lowest = ContentBeard.lowestIn(worldIn, box.minX - 1, box.minZ - 1, box.maxX + 1, box.maxZ + 1, structurebb);
        if (lowest == Integer.MAX_VALUE) { return found; }
        ContentLog.LOGGER.debug("{} measured its ground at y {} and builds at y {}, so its rim sits flush with the lowest ground touching it", getClass().getSimpleName(), found, lowest - 1);
        return lowest - 1;
    }
}
