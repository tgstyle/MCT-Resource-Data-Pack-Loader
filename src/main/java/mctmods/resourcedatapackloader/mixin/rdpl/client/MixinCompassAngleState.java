package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import javax.annotation.Nullable;

@Mixin(CompassAngleState.class) public abstract class MixinCompassAngleState {
    @Shadow @Final private CompassAngleState.CompassTarget compassTarget;

    @ModifyExpressionValue(method = "calculate(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/multiplayer/ClientLevel;ILnet/minecraft/world/entity/ItemOwner;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/properties/numeric/CompassAngleState$CompassTarget;get(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/ItemOwner;)Lnet/minecraft/core/GlobalPos;"))
    @Nullable private GlobalPos rdpl$naturalSpawn(@Nullable GlobalPos target, ItemStack itemStack, ClientLevel level, int seed, ItemOwner owner) {
        if (target == null || compassTarget != CompassAngleState.CompassTarget.SPAWN) { return target; }
        DimensionDef def = ContentDimensions.def(level);
        return def != null && def.surfaceWorld() ? GlobalPos.of(level.dimension(), target.pos()) : target;
    }
}
