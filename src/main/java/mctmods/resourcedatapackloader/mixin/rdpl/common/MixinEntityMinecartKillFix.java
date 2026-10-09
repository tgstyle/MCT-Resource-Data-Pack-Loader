package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.world.GenHeights;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityMinecart.class) public abstract class MixinEntityMinecartKillFix extends Entity {
    public MixinEntityMinecartKillFix(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = -64.0D), require = 1) private double rdpl$getDeathY(double originalY) { return GenHeights.floor(world, 0) + originalY; }
}
