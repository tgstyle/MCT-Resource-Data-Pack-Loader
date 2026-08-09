package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;

@Mixin(StructureVillagePieces.Path.class)
public abstract class MixinVillagePath extends StructureVillagePieces.Village {
    @Redirect(method = "findPieceBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;getInt(Ljava/util/Random;II)I"))
    private static int rdpl$longerRuns(Random random, int minimum, int maximum) {
        int rolled = MathHelper.getInt(random, ContentBeard.wanted() ? 5 : minimum, ContentBeard.wanted() ? 7 : maximum);
        ContentLog.LOGGER.debug("A road rolls {} segments of 7, {} blocks", rolled, rolled * 7);
        return rolled;
    }

    @Redirect(method = "findPieceBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureComponent;findIntersecting(Ljava/util/List;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Lnet/minecraft/world/gen/structure/StructureComponent;"))
    @SuppressWarnings("ConstantConditions")
    private static StructureComponent rdpl$whoBlocks(List<StructureComponent> listIn, StructureBoundingBox boundingboxIn) {
        StructureComponent blocker = StructureComponent.findIntersecting(listIn, boundingboxIn);
        if (blocker != null) { ContentLog.LOGGER.debug("A road attempt {} is blocked by {} at {}", boundingboxIn, blocker.getClass().getSimpleName(), blocker.getBoundingBox()); }
        return blocker;
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "findPieceBox", at = @At("RETURN"), cancellable = true)
    private static void rdpl$widen(StructureVillagePieces.Start start, List<StructureComponent> p_175848_1_, Random rand, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing facing, CallbackInfoReturnable<StructureBoundingBox> cir) {
        StructureBoundingBox found = cir.getReturnValue();
        ContentLog.LOGGER.debug("A road box comes back {} facing {}: {}", found == null ? "null" : (Math.max(found.maxX - found.minX, found.maxZ - found.minZ) + 1) + " long", facing, found);
        if (found == null || !ContentBeard.wanted()) { return; }
        if (rdpl$acrossPlaza(p_175848_1_, found)) {
            cir.setReturnValue(null);
            return;
        }

        int half = (ContentBeard.pathFullWidth() - 3) / 2;
        if (half > 0) {
            StructureBoundingBox wide = new StructureBoundingBox(found);
            if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                wide.minX -= half;
                wide.maxX += half;
            }
            else {
                wide.minZ -= half;
                wide.maxZ += half;
            }
            if (StructureComponent.findIntersecting(p_175848_1_, wide) == null) {
                cir.setReturnValue(wide);
                return;
            }
        }
        if (3 < ContentBeard.pathMinimumWidth()) { cir.setReturnValue(null); }
    }

    @Unique private static boolean rdpl$acrossPlaza(List<StructureComponent> components, StructureBoundingBox road) {
        if (!ContentBeard.pathChosen() || components.isEmpty()) { return false; }

        StructureBoundingBox well = components.get(0).getBoundingBox();
        int reach = ContentBeard.pathFullWidth();
        if (road.maxX < well.minX - reach || road.minX > well.maxX + reach || road.maxZ < well.minZ - reach || road.minZ > well.maxZ + reach) { return false; }

        boolean alongX = road.maxX - road.minX >= road.maxZ - road.minZ;
        boolean radial = alongX ? road.maxZ >= well.minZ && road.minZ <= well.maxZ : road.maxX >= well.minX && road.minX <= well.maxX;
        return !radial;
    }

    @SuppressWarnings({"ConstantConditions", "DataFlowIssue"}) @Inject(method = "addComponentParts", at = @At("HEAD"), cancellable = true)
    private void rdpl$grade(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn, CallbackInfoReturnable<Boolean> cir) {
        if (!ContentBeard.wanted()) { return; }

        ContentBeard.pave(this, worldIn, structureBoundingBoxIn,
                ContentBeard.pathBlock("villagePathBlock", Config.worldgen.villagePathBlock, getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState())),
                ContentBeard.pathBlock("villagePathSupportBlock", Config.worldgen.villagePathSupportBlock, getBiomeSpecificBlockState(Blocks.GRAVEL.getDefaultState())),
                ContentBeard.pathBlock("villagePathBridgeBlock", Config.worldgen.villagePathBridgeBlock, getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState())),
                ContentBeard.pathChosen());
        cir.setReturnValue(true);
    }
}
