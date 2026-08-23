package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.fluid.ThreadFindSeal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ThreadFindSeal.class, remap = false) public class MixinThreadFindSeal {
    @Shadow private World world;
    @Shadow private BlockVec3 head;

    @Unique private boolean rdpl$rubic() { return world instanceof IRubicWorld && ((IRubicWorld) world).rdpl$isRubicWorld(); }

    @Unique private int rdpl$yBase() { return rdpl$rubic() ? head.y - 128 : 0; }

    @Inject(method = "checkedAdd", at = @At("HEAD"), cancellable = true)
    private void rdpl$dropBeyondWindow(BlockVec3 vec, CallbackInfo cbi) {
        if (!rdpl$rubic()) { return; }
        int base = head.y - 128;
        if (vec.y < base || vec.y > base + 255) { cbi.cancel(); }
    }

    @Redirect(method = "checkedAdd", at = @At(value = "FIELD", target = "Lmicdoodle8/mods/galacticraft/api/vector/BlockVec3;y:I", opcode = Opcodes.GETFIELD))
    private int rdpl$rebaseAdd(BlockVec3 vec) { return vec.y - rdpl$yBase(); }

    @Redirect(method = "checkedContains(Lmicdoodle8/mods/galacticraft/api/vector/BlockVec3;)Z",
            at = @At(value = "FIELD", target = "Lmicdoodle8/mods/galacticraft/api/vector/BlockVec3;y:I", opcode = Opcodes.GETFIELD))
    private int rdpl$rebaseContains(BlockVec3 vec) { return vec.y - rdpl$yBase(); }

    @Redirect(method = "checkedContains(Lmicdoodle8/mods/galacticraft/api/vector/BlockVec3;I)Z",
            at = @At(value = "FIELD", target = "Lmicdoodle8/mods/galacticraft/api/vector/BlockVec3;y:I", opcode = Opcodes.GETFIELD))
    private int rdpl$rebaseContainsSided(BlockVec3 vec) { return vec.y - rdpl$yBase(); }

    @ModifyVariable(method = "checkedAll", at = @At("STORE"), name = "y")
    private int rdpl$rebaseAll(int y) { return y + rdpl$yBase(); }
}
