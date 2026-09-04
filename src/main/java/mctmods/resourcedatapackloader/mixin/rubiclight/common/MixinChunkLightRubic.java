package mctmods.resourcedatapackloader.mixin.rubiclight.common;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumnInternal;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import static mctmods.resourcedatapackloader.util.Coords.blockToCube;
import static mctmods.resourcedatapackloader.util.Coords.cubeToMinBlock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import javax.annotation.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chunk.class) public abstract class MixinChunkLightRubic {
    @Shadow @Final private World world;

    @Unique private boolean rdpl$isColumn() { return ((IColumnInternal) this).isRubicColumn(); }

    @Inject(method = "recheckGaps", at = @At("HEAD"), cancellable = true) private void rdpl$recheckGapsRubic(boolean onlyOne, CallbackInfo cbi) {
        if (rdpl$isColumn()) { cbi.cancel(); }
    }

    @Inject(method = "relightBlock", at = @At("HEAD"), cancellable = true) private void rdpl$relightBlockRubic(int x, int y, int z, CallbackInfo cbi) {
        if (rdpl$isColumn()) { cbi.cancel(); }
    }

    @Inject(method = "checkLight()V", at = @At("HEAD"), cancellable = true) private void rdpl$checkLightRubic(CallbackInfo cbi) {
        if (rdpl$isColumn()) { cbi.cancel(); }
    }

    @Inject(method = "setLightFor", at = @At("HEAD"), cancellable = true) private void rdpl$storageBeforeLight(EnumSkyBlock type, BlockPos pos, int value, CallbackInfo cbi) {
        IColumnInternal column = (IColumnInternal) this;
        if (!column.isRubicColumn() || column.getCompatGenerationPrimer() != null) { return; }
        int cubeY = blockToCube(pos.getY());
        if (column.getStorageForCube(cubeY) != null) { return; }
        column.setStorageForCube(cubeY, new ExtendedBlockStorage(cubeToMinBlock(cubeY), world.provider.hasSkyLight()));
        if (column.getStorageForCube(cubeY) == null) { cbi.cancel(); }
    }

    @Inject(method = "getLightFor", at = @At("HEAD"), cancellable = true) private void rdpl$getLightForRubic(EnumSkyBlock type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        IColumnInternal column = (IColumnInternal) this;
        if (!column.isRubicColumn() || column.getCompatGenerationPrimer() != null) { return; }
        ((IRubicWorldInternal) world).rdpl$getLightingManager().onGetLight();
        cir.setReturnValue(((Cube) column.getCube(blockToCube(pos.getY()))).getCachedLightFor(type, pos));
    }

    @Nullable @Redirect(method = "getLightFor", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;",
            opcode = Opcodes.GETFIELD, args = "array=get"
    ), expect = 0)
    private ExtendedBlockStorage rdpl$getLightForStorage(ExtendedBlockStorage[] array, int index) { return ((IColumnInternal) this).getStorageForCube(index); }
}
