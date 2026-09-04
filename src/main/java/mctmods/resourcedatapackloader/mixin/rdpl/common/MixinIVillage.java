package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardLayout;
import mctmods.resourcedatapackloader.content.worldgen.beard.interfaces.IVillageBlock;
import mctmods.resourcedatapackloader.util.world.GroundLevel;

import net.minecraft.util.math.BlockPos;
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
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.template.TemplateManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureVillagePieces.Village.class) public abstract class MixinIVillage extends StructureComponent implements IVillageBlock {
    @Unique private int rdpl$block;

    @Override public int rdpl$block() { return rdpl$block; }

    @Override public void rdpl$block(int size) { rdpl$block = size; }

    @Inject(method = "writeStructureToNBT", at = @At("TAIL")) private void rdpl$keepBlock(NBTTagCompound tagCompound, CallbackInfo ci) { if (rdpl$block > 0) { tagCompound.setInteger("rdpl_block", rdpl$block); } }

    @Inject(method = "readStructureFromNBT", at = @At("TAIL")) private void rdpl$loadBlock(NBTTagCompound tagCompound, TemplateManager p_143011_2_, CallbackInfo ci) { rdpl$block = tagCompound.getInteger("rdpl_block"); }

    @Redirect(method = "getAverageGroundLevel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;getTopSolidOrLiquidBlock(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos rdpl$seatInWindow(World world, BlockPos pos) { return GroundLevel.inWindow(world, pos); }

    @Inject(method = "getAverageGroundLevel", at = @At("RETURN"), cancellable = true) private void rdpl$leanLow(World worldIn, StructureBoundingBox structurebb, CallbackInfoReturnable<Integer> cir) {
        if (!ContentBeard.wanted()) { return; }
        int found = cir.getReturnValueI();
        if (found < 0) { return; }
        int leaned = BeardLayout.leanLow(this, worldIn, structurebb, found);
        if (leaned != found) { cir.setReturnValue(leaned); }
    }

    @Inject(method = "getBiomeSpecificBlockState", at = @At("RETURN"), cancellable = true) private void rdpl$packBlocks(IBlockState blockstateIn, CallbackInfoReturnable<IBlockState> cir) {
        IBlockState wanted = ContentVillages.swap(cir.getReturnValue());
        if (wanted == null) { wanted = ContentVillages.swap(blockstateIn); }
        if (wanted != null) { cir.setReturnValue(wanted); }
    }
}
