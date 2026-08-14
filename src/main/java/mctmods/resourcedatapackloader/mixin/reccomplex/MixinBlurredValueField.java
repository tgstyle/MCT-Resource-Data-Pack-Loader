package mctmods.resourcedatapackloader.mixin.reccomplex;

import ivorius.ivtoolkit.random.BlurredValueField;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashMap;

@Mixin(value = BlurredValueField.class, remap = false)
public abstract class MixinBlurredValueField {
    @Unique private HashMap<Long, Double> rdpl$known;

    @Inject(method = "getValue([I)D", at = @At("HEAD"), cancellable = true, remap = false)
    private void rdpl$remembered(int[] position, CallbackInfoReturnable<Double> cir) {
        if (position.length != 3) { return; }

        if (rdpl$known == null) { rdpl$known = new HashMap<>(); }
        Double held = rdpl$known.get(rdpl$key(position));
        if (held != null) { cir.setReturnValue(held); }
    }

    @Inject(method = "getValue([I)D", at = @At("RETURN"), remap = false)
    private void rdpl$learned(int[] position, CallbackInfoReturnable<Double> cir) {
        if (position.length != 3 || rdpl$known == null) { return; }

        rdpl$known.put(rdpl$key(position), cir.getReturnValue());
    }

    @Inject(method = "addValue(Livorius/ivtoolkit/random/BlurredValueField$Value;)Z", at = @At("HEAD"), remap = false)
    private void rdpl$forgetOnAdd(BlurredValueField.Value value, CallbackInfoReturnable<Boolean> cir) { rdpl$known = null; }

    @Inject(method = "readFromNBT", at = @At("RETURN"), remap = false)
    private void rdpl$forgetOnRead(NBTTagCompound compound, CallbackInfo ci) { rdpl$known = null; }

    @Unique private static long rdpl$key(int[] position) { return ((long) (position[0] & 0x1FFFFF) << 42) | ((long) (position[1] & 0x1FFFFF) << 21) | (position[2] & 0x1FFFFF); }
}
