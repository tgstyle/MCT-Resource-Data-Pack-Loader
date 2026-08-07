package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;

@Mixin(StructureVillagePieces.Path.class)
public abstract class MixinVillagePath extends StructureVillagePieces.Village {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "findPieceBox", at = @At("RETURN"), cancellable = true)
    private static void rdpl$widen(StructureVillagePieces.Start start, List<StructureComponent> p_175848_1_, Random rand, int p_175848_3_, int p_175848_4_, int p_175848_5_, EnumFacing facing, CallbackInfoReturnable<StructureBoundingBox> cir) {
        StructureBoundingBox found = cir.getReturnValue();
        int extra = ContentBeard.pathExtraWidth();
        if (found == null || extra == 0 || !ContentBeard.wanted()) { return; }

        StructureBoundingBox wide = new StructureBoundingBox(found);
        if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
            wide.minX -= extra;
            wide.maxX += extra;
        }
        else {
            wide.minZ -= extra;
            wide.maxZ += extra;
        }
        if (StructureComponent.findIntersecting(p_175848_1_, wide) == null) { cir.setReturnValue(wide); }
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
