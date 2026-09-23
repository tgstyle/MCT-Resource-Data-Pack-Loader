package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRails;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenFossils;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.List;
import java.util.Random;

@Mixin(WorldGenFossils.class) public class MixinWorldGenFossils {
    @ModifyConstant(method = "generate", constant = @Constant(intValue = 0, ordinal = 0), require = 1) private int rdpl$boxMinY(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.floor(worldIn, orig); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256), require = 2) private int rdpl$boxMaxY(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.ceiling(worldIn, orig); }

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 10, ordinal = 1), require = 1) private int rdpl$lowestY(int orig, World worldIn, Random rand, BlockPos position) { return GenHeights.floor(worldIn, orig); }

    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/template/Template;addBlocksToWorld(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/PlacementSettings;I)V"), require = 2) private void rdpl$spareBores(Template template, World worldIn, BlockPos pos, PlacementSettings placementIn, int flags) {
        if (rdpl$meetsBore(template, worldIn, pos, placementIn)) { return; }
        template.addBlocksToWorld(worldIn, pos, placementIn, flags);
    }

    @Unique private static boolean rdpl$meetsBore(Template template, World world, BlockPos pos, PlacementSettings settings) {
        BlockPos far = pos.add(Template.transformedBlockPos(settings, template.getSize().add(-1, -1, -1)));
        StructureBoundingBox clip = settings.getBoundingBox();
        int minX = Math.min(pos.getX(), far.getX());
        int maxX = Math.max(pos.getX(), far.getX());
        int minZ = Math.min(pos.getZ(), far.getZ());
        int maxZ = Math.max(pos.getZ(), far.getZ());
        if (clip != null) {
            minX = Math.max(minX, clip.minX);
            maxX = Math.min(maxX, clip.maxX);
            minZ = Math.max(minZ, clip.minZ);
            maxZ = Math.min(maxZ, clip.maxZ);
        }
        if (maxX < minX || maxZ < minZ) { return false; }
        List<RailPiece> subways = BeardRails.subways(world, new StructureBoundingBox(minX, 0, minZ, maxX, 0, maxZ));
        if (subways.isEmpty()) { return false; }
        int minY = Math.min(pos.getY(), far.getY());
        int maxY = Math.max(pos.getY(), far.getY());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    if (!BeardRails.insideBore(world, subways, x, y, z)) { continue; }
                    if (ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("A fossil at {}, {}, {} would have written into a subway bore, so it is left out", pos.getX(), pos.getY(), pos.getZ()); }
                    return true;
                }
            }
        }
        return false;
    }
}
