package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityMinecart.class) public abstract class MixinEntityMinecartKillFix extends Entity {
    public MixinEntityMinecartKillFix(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = -64.0D), require = 1) private double getDeathY(double originalY) {
        return ((IRubicWorld) world).rdpl$getMinHeight() + originalY;
    }
}
