package mctmods.resourcedatapackloader.mixin.block;

import mctmods.resourcedatapackloader.common.items.ContentRecordItem;

import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockJukebox.class) public abstract class MixinBlockJukebox {
    @Inject(method = "getComparatorInputOverride", at = @At("HEAD"), cancellable = true) private void rdpl$packDiscLevel(IBlockState blockState, World worldIn, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof BlockJukebox.TileEntityJukebox)) { return; }
        Item item = ((BlockJukebox.TileEntityJukebox) tile).getRecord().getItem();
        if (!(item instanceof ContentRecordItem)) { return; }
        int level = ((ContentRecordItem) item).comparator();
        if (level >= 0) { cir.setReturnValue(level); }
    }
}
