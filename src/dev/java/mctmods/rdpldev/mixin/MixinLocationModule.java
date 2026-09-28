package mctmods.rdpldev.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "me.cominixo.betterf3.modules.LocationModule", remap = false) public abstract class MixinLocationModule {
    @Redirect(method = "update(Lnet/minecraft/client/Minecraft;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getDimensionValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;)Ljava/lang/Object;"))
    private <V> V rdpldev$positionalValue(EnvironmentAttributeSystem system, EnvironmentAttribute<V> attribute, Minecraft client) {
        Entity camera = client.getCameraEntity();
        return camera == null ? attribute.defaultValue() : system.getValue(attribute, camera.blockPosition());
    }
}
