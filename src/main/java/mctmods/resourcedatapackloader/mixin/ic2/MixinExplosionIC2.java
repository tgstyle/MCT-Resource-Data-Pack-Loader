package mctmods.resourcedatapackloader.mixin.ic2;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo @Mixin(targets = "ic2.core.ExplosionIC2", remap = false) public abstract class MixinExplosionIC2 {
    @SuppressWarnings("NameDoesntMatchTargetClass") @Redirect(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDDFFLic2/core/ExplosionIC2$Type;Lnet/minecraft/entity/EntityLivingBase;I)V", at = @At(value = "INVOKE", target = "Lic2/core/IC2;getWorldHeight(Lnet/minecraft/world/World;)I", remap = false), remap = false) private int rdpl$explosionMapHeight(World world, World constructorWorld, Entity entity, double x, double y, double z, float power1) {
        if (!GenHeights.rubic(world)) { return world.getHeight(); }
        return MathHelper.clamp(MathHelper.ceil(y + power1 / 0.4), 0, world.getHeight());
    }
}
