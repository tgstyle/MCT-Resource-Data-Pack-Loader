package mctmods.resourcedatapackloader.mixin.client.renderer.item.properties.numeric;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CompassAngleState.class) public abstract class CompassAngleStateMixin {
    @Shadow @Final private CompassAngleState.CompassTarget compassTarget;

    @ModifyExpressionValue(method = "calculate(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/multiplayer/ClientLevel;ILnet/minecraft/world/entity/ItemOwner;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/properties/numeric/CompassAngleState$CompassTarget;get(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/ItemOwner;)Lnet/minecraft/core/GlobalPos;")) @Nullable private GlobalPos rdpl$naturalSpawn(@Nullable GlobalPos target, ItemStack itemStack, ClientLevel level, int seed, ItemOwner owner) {
        if (target == null || compassTarget != CompassAngleState.CompassTarget.SPAWN) { return target; }
        DimensionDef def = ContentDimensions.def(level);
        return def != null && ContentDimensions.sets(def, "sky.surfaceWorld") && def.surfaceWorld() ? GlobalPos.of(level.dimension(), target.pos()) : target;
    }
}
