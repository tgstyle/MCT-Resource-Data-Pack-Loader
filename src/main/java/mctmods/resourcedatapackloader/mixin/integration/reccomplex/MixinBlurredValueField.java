package mctmods.resourcedatapackloader.mixin.integration.reccomplex;

import mctmods.resourcedatapackloader.core.util.math.Longs;

import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import ivorius.ivtoolkit.random.BlurredValueField;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlurredValueField.class, remap = false) public abstract class MixinBlurredValueField {
    @Unique private Long2DoubleOpenHashMap rdpl$known;

    @Inject(method = "getValue([I)D", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$remembered(int[] position, CallbackInfoReturnable<Double> cir) {
        if (position.length != 3) { return; }
        if (rdpl$known == null) {
            rdpl$known = new Long2DoubleOpenHashMap();
            rdpl$known.defaultReturnValue(Double.NaN);
        }
        double held = rdpl$known.get(Longs.pack21(position[0], position[1], position[2]));
        if (!Double.isNaN(held)) { cir.setReturnValue(held); }
    }

    @Inject(method = "getValue([I)D", at = @At("RETURN"), remap = false) private void rdpl$learned(int[] position, CallbackInfoReturnable<Double> cir) {
        if (position.length != 3 || rdpl$known == null) { return; }
        rdpl$known.put(Longs.pack21(position[0], position[1], position[2]), cir.getReturnValueD());
    }

    @Inject(method = "addValue(Livorius/ivtoolkit/random/BlurredValueField$Value;)Z", at = @At("HEAD"), remap = false) private void rdpl$forgetOnAdd(BlurredValueField.Value value, CallbackInfoReturnable<Boolean> cir) { rdpl$known = null; }

    @Inject(method = "readFromNBT", at = @At("RETURN"), remap = false) private void rdpl$forgetOnRead(NBTTagCompound compound, CallbackInfo ci) { rdpl$known = null; }
}
