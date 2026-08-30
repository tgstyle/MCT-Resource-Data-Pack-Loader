package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.village.CityGrowth;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(StructureComponent.class) public abstract class MixinStructureComponent {
    @Inject(method = "findIntersecting", at = @At("HEAD"), cancellable = true) private static void rdpl$betweenBuildings(List<StructureComponent> listIn, StructureBoundingBox boundingboxIn, CallbackInfoReturnable<StructureComponent> cir) {
        if (!ContentBeard.spacedLayout()) {
            if (!CityGrowth.laying()) { return; }
            for (StructureComponent piece : listIn) {
                StructureBoundingBox held = ((IStructureComponentBox) piece).rdpl$box();
                if (held != null && rdpl$flatHit(held, boundingboxIn, 0)) {
                    cir.setReturnValue(piece);
                    return;
                }
            }
            cir.setReturnValue(null);
            return;
        }

        for (StructureComponent piece : listIn) {
            StructureBoundingBox held = ((IStructureComponentBox) piece).rdpl$box();
            if (held == null) { continue; }
            boolean flush = piece instanceof StructureVillagePieces.Path || piece instanceof StructureVillagePieces.Well;
            if (rdpl$flatHit(held, boundingboxIn, flush ? 0 : 1)) {
                cir.setReturnValue(piece);
                return;
            }
        }
        cir.setReturnValue(null);
    }

    private static boolean rdpl$flatHit(StructureBoundingBox held, StructureBoundingBox box, int gap) {
        return held.maxX >= box.minX - gap && held.minX <= box.maxX + gap && held.maxZ >= box.minZ - gap && held.minZ <= box.maxZ + gap;
    }

    @Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
    private boolean rdpl$ruledBlocks(World world, BlockPos pos, IBlockState newState, int flags) {
        StructureComponent self = StructureComponent.class.cast(this);
        if (!(self instanceof StructureVillagePieces.Village) || self instanceof StructureVillagePieces.Road) { return world.setBlockState(pos, newState, flags); }
        IBlockState wanted = ContentVillages.ruled(world, pos, newState);
        return world.setBlockState(pos, wanted == null ? newState : wanted, flags);
    }
}
